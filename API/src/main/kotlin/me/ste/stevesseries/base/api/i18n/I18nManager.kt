package me.ste.stevesseries.base.api.i18n

import me.ste.stevesseries.base.api.config.storage.ConfigStorage
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.Locale
import java.util.function.Function
import java.util.function.Predicate

interface I18nManager {
    fun translate(player: Player, translationKey: NamespacedKey, arguments: Map<String, String> = emptyMap()): String

    fun setTranslation(locale: Locale, translationKey: NamespacedKey, template: String)
    fun getTemplate(locale: Locale, translationKey: NamespacedKey): String?

    fun getLocales(): Set<Locale>
    fun getTranslations(locale: Locale): Map<NamespacedKey, String>

    fun getLocale(player: Player): Locale
    fun registerLocaleProvider(id: NamespacedKey, provider: PlayerLocaleProvider)
    fun getLocaleProviders(): Map<NamespacedKey, PlayerLocaleProvider>
    fun setLocaleProviders(providers: Array<NamespacedKey>)

    fun syncLocale(storage: ConfigStorage, locale: Locale, selector: Predicate<NamespacedKey>)
    fun syncLocales(pathSelector: Function<Locale, ConfigStorage>, selector: Predicate<NamespacedKey>)
}