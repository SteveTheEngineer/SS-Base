package me.ste.stevesseries.base.util

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import java.util.function.Consumer

@Deprecated("For backwards compatibility only. Look into the GUI Library for an alternative.")
class ItemStackBuilder {
    private val stack = ItemStack(Material.STONE, 1)

    fun material(material: Material): ItemStackBuilder {
        this.stack.type = material
        return this
    }

    fun amount(amount: Int): ItemStackBuilder {
        this.stack.amount = amount
        return this
    }

    fun item(itemTransformer: Consumer<ItemStack>): ItemStackBuilder {
        itemTransformer.accept(this.stack)
        return this
    }

    fun <T: ItemMeta> meta(metaTransformer: Consumer<T>): ItemStackBuilder {
        val itemMeta = this.stack.itemMeta
        if (itemMeta != null) {
            metaTransformer.accept(itemMeta as T)
            this.stack.itemMeta = itemMeta
        }
        return this
    }

    fun lore(lore: Iterable<String>): ItemStackBuilder {
        val coloredLore: MutableList<String> = ArrayList()
        for (line in lore) {
            coloredLore += ChatColor.translateAlternateColorCodes('&', "&f$line")
        }
        return this.meta<ItemMeta> {
            it.lore = coloredLore
        }
    }

    fun lore(vararg lore: String) = this.lore(listOf(*lore))

    fun name(name: String): ItemStackBuilder = this.meta<ItemMeta> {
        it.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f$name"))
    }

    fun enchant(enchantment: Enchantment, level: Int): ItemStackBuilder = this.meta<ItemMeta> {
        it.addEnchant(enchantment, level, true)
    }

    fun enchant(enchantment: Enchantment) = this.enchant(enchantment, 0)

    fun enchant() = this.enchant(Enchantment.PROTECTION_ENVIRONMENTAL)

    fun flag(flag: ItemFlag) = this.meta<ItemMeta> {
        it.addItemFlags(flag)
    }

    fun color(color: Color): ItemStackBuilder = this.meta<ItemMeta> {
        if (it is LeatherArmorMeta) {
            it.setColor(color)
        } else if(it is PotionMeta) {
            it.color = color
        }
    }

    fun condition(condition: Boolean, consumerTrue: Consumer<ItemStackBuilder>, consumerFalse: Consumer<ItemStackBuilder>? = null): ItemStackBuilder {
        if (condition) {
            consumerTrue.accept(this)
        } else {
            consumerFalse?.accept(this)
        }
        return this
    }

    fun build() = this.stack
}
