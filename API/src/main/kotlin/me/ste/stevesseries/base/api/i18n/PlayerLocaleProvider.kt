package me.ste.stevesseries.base.api.i18n

import org.bukkit.entity.Player
import java.util.Locale

interface PlayerLocaleProvider {
    fun getLocale(player: Player): Locale?
}