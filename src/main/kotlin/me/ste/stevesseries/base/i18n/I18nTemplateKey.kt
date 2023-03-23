package me.ste.stevesseries.base.i18n

import org.bukkit.NamespacedKey
import java.util.Locale

data class I18nTemplateKey(
    val locale: Locale,
    val key: NamespacedKey
)
