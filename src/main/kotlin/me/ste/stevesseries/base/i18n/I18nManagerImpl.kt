package me.ste.stevesseries.base.i18n

import me.ste.stevesseries.base.Base
import me.ste.stevesseries.base.api.config.storage.ConfigStorage
import me.ste.stevesseries.base.api.i18n.I18nManager
import me.ste.stevesseries.base.api.i18n.PlayerLocaleProvider
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

class I18nManagerImpl(
    plugin: Base
) : I18nManager {
    private val templates = mutableMapOf<I18nTemplateKey, String>()

    val clientProviderId = NamespacedKey(plugin, "client")
    private val clientProvider = ClientLocaleProvider()

    private val providers = mutableMapOf<NamespacedKey, PlayerLocaleProvider>(
        this.clientProviderId to this.clientProvider
    )
    private var orderedProviders = arrayOf<PlayerLocaleProvider>(this.clientProvider)

    override fun translate(player: Player, translationKey: NamespacedKey, arguments: Map<String, String>): String {
        val locale = this.getLocale(player)
        var template = this.getTemplate(locale, translationKey)

        for ((key, value) in arguments) {
            template = template.replace("{{$key}}", value)
        }

        return template
    }

    override fun setTranslation(locale: Locale, translationKey: NamespacedKey, template: String) {
        this.templates[I18nTemplateKey(locale, translationKey)] = template
    }

    override fun getTemplate(locale: Locale, translationKey: NamespacedKey): String {
        // First try with the provided locale
        val preferred = this.templates[I18nTemplateKey(locale, translationKey)]
        if (preferred != null) {
            return preferred
        }

        // Then the default
        val default = this.templates[I18nTemplateKey(Locale.getDefault(), translationKey)]
        if (default != null) {
            return default
        }

        // Then first available
        val first = this.templates.filterKeys { it.key == translationKey }.values.firstOrNull()
        if (first != null) {
            return first
        }

        return translationKey.toString()
    }

    override fun getLocales() = this.templates.keys.map { it.locale }.toSet()

    override fun getTranslations(locale: Locale) = this.templates.filterKeys { it.locale == locale }.mapKeys { (key, _) -> key.key }

    override fun getLocale(player: Player): Locale {
        for ((id, provider) in this.providers) {
            return provider.getLocale(player) ?: continue
        }

        return Locale.getDefault()
    }

    override fun registerLocaleProvider(id: NamespacedKey, provider: PlayerLocaleProvider) {
        this.providers[id] = provider
    }

    override fun getLocaleProviders() = this.providers

    override fun setLocaleProviders(providers: Array<NamespacedKey>) {
        this.orderedProviders = providers.map {
            this.providers[it] ?: throw IllegalArgumentException("Provider is not registered: ${it}.")
        }.toTypedArray()
    }

    override fun syncLocale(storage: ConfigStorage, locale: Locale, selector: Predicate<NamespacedKey>) {
        val translations = this.getTranslations(locale).filterKeys(selector::test)

        for ((key, value) in translations) {
            val configPath = "${key.namespace}.${key.key}"
            val overrideValue = storage.getValue(configPath, String::class.java)

            if (overrideValue == null) {
                storage.setValue(configPath, String::class.java, value, emptyList())
                continue
            }

            this.setTranslation(locale, key, overrideValue)
        }

        storage.save()
    }

    override fun syncLocales(pathSelector: Function<Locale, ConfigStorage>, selector: Predicate<NamespacedKey>) {
        for (locale in this.getLocales()) {
            val translations = this.getTranslations(locale)
            if (!translations.any { (key, _) -> selector.test(key) }) {
                continue
            }

            val storage = pathSelector.apply(locale)
            this.syncLocale(storage, locale, selector)
        }
    }
}