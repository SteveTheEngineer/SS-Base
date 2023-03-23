package me.ste.stevesseries.base.i18n

import me.ste.stevesseries.base.api.i18n.PlayerLocaleProvider
import org.bukkit.entity.Player
import java.util.*

class ClientLocaleProvider : PlayerLocaleProvider {
    override fun getLocale(player: Player): Locale? {
        val parts = player.locale.split("_")

        try {
            if (parts.size == 1) {
                 Locale(parts[0])
            } else if (parts.size == 2) {
                return Locale(parts[0], parts[1])
            } else if (parts.size >= 3) {
                return Locale(parts[0], parts[2], parts[3])
            }
        } catch (_: Throwable) {}

        return null
    }
}