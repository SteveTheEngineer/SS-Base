package me.ste.stevesseries.base;

import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Item stack builder class
 */
public class ItemStackBuilder {
    private ItemStack stack;
    private String displayName = null;
    private final List<Consumer<ItemMeta>> modifiers = new ArrayList<>();

    public ItemStackBuilder(Material type, int amount) {
        this.stack = new ItemStack(type, amount);
    }
    public ItemStackBuilder(Material type) {
        this(type, 1);
    }
    public ItemStackBuilder(ItemStack itemStack) {
        this.stack = itemStack.clone();
    }
    public ItemStackBuilder() {
        this.stack = new ItemStack(Material.AIR, 0);
    }

    public <T extends ItemMeta> ItemStackBuilder meta(Consumer<T> modifier) {
        this.modifiers.add((Consumer<ItemMeta>) modifier);
        return this;
    }
    public ItemStackBuilder displayName(String name) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    @Deprecated
    public ItemStackBuilder damage(int value) {
        return this.meta(meta -> ((Damageable) meta).setDamage(value));
    }

    public ItemStackBuilder lore(List<String> lore) {
        List<String> coloredLore = new ArrayList<>();
        for(String str : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return this.meta(meta -> meta.setLore(coloredLore));
    }

    public ItemStackBuilder lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }
    @Deprecated
    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        return this.meta(meta -> {
            meta.removeEnchant(enchantment);
            meta.addEnchant(enchantment, level, true);
        });
    }
    @Deprecated
    public ItemStackBuilder itemFlags(ItemFlag... itemFlags) {
        return this.meta(meta -> meta.addItemFlags(itemFlags));
    }
    @Deprecated
    public ItemStackBuilder color(Color color) {
        ItemMeta itemMeta = this.stack.getItemMeta();
        if(itemMeta instanceof LeatherArmorMeta) {
            return this.<LeatherArmorMeta>meta(meta -> meta.setColor(color));
        } else if(itemMeta instanceof PotionMeta) {
            return this.<PotionMeta>meta(meta -> meta.setColor(color));
        }
        return this;
    }

    public ItemStack build() {
        try {
            Object nmsStack = Objects.requireNonNull(NMSUtil.getOBCClass("inventory.CraftItemStack")).getMethod("asNMSCopy", this.stack.getClass()).invoke(null, this.stack);
            if(this.displayName != null) {
                Object compound = ((boolean) nmsStack.getClass().getMethod("hasTag").invoke(nmsStack)) ? nmsStack.getClass().getMethod("getTag").invoke(nmsStack) : Objects.requireNonNull(NMSUtil.getNMSClass("NBTTagCompound")).getConstructor().newInstance();
                Object display = Objects.requireNonNull(NMSUtil.getNMSClass("NBTTagCompound")).getConstructor().newInstance();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("italic", false);
                jsonObject.addProperty("text", this.displayName);
                display.getClass().getMethod("setString", String.class, String.class).invoke(display, "Name", jsonObject.toString());
                compound.getClass().getMethod("set", String.class, NMSUtil.getNMSClass("NBTBase")).invoke(compound, "display", display);
                nmsStack.getClass().getMethod("setTag", compound.getClass()).invoke(nmsStack, compound);
            }
            ItemStack stack = (ItemStack) Objects.requireNonNull(NMSUtil.getOBCClass("inventory.CraftItemStack")).getMethod("asBukkitCopy", nmsStack.getClass()).invoke(null, nmsStack);
            ItemMeta meta = stack.getItemMeta();
            for(Consumer<ItemMeta> modifier : this.modifiers) {
                modifier.accept(meta);
            }
            stack.setItemMeta(meta);
            return stack;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}