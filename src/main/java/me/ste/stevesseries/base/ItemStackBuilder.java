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

public class ItemStackBuilder {
    private ItemStack stack;
    private ItemMeta meta;
    private String displayName = null;

    public ItemStackBuilder(Material type, int amount) {
        this.stack = new ItemStack(type, amount);
        this.meta = this.stack.getItemMeta();
    }
    public ItemStackBuilder(Material type) {
        this(type, 1);
    }
    public ItemStackBuilder(ItemStack itemStack) {
        this.stack = itemStack.clone();
        this.meta = this.stack.getItemMeta();
    }

    public ItemStackBuilder damage(int value) {
        if(this.meta instanceof Damageable) {
            ((Damageable) this.meta).setDamage(value);
        }
        return this;
    }
    public ItemStackBuilder displayName(String name) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }
    public ItemStackBuilder lore(List<String> lore) {
        List<String> actualLore = new ArrayList<>();
        for(String string : lore) {
            actualLore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', string));
        }
        this.meta.setLore(actualLore);
        return this;
    }
    public ItemStackBuilder lore(String... lore) {
        this.lore(Arrays.asList(lore));
        return this;
    }
    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        this.meta.removeEnchant(enchantment);
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }
    public ItemStackBuilder itemFlags(ItemFlag... itemFlags) {
        this.meta.addItemFlags(itemFlags);
        return this;
    }
    public ItemStackBuilder color(Color color) {
        if(this.meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) this.meta).setColor(color);
        } else if(this.meta instanceof PotionMeta) {
            ((PotionMeta) this.meta).setColor(color);
        }
        return this;
    }

    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
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
            meta.setLore(this.meta.getLore());
            meta.addItemFlags(this.meta.getItemFlags().toArray(new ItemFlag[0]));
            for(Map.Entry<Enchantment, Integer> e : this.meta.getEnchants().entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }
            if(meta instanceof Damageable && this.meta instanceof Damageable) {
                ((Damageable) meta).setDamage(((Damageable) this.meta).getDamage());
            }
            if(meta instanceof LeatherArmorMeta && this.meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(((LeatherArmorMeta) this.meta).getColor());
            } else if(meta instanceof PotionMeta && this.meta instanceof PotionMeta) {
                ((PotionMeta) meta).setColor(((PotionMeta) this.meta).getColor());
            }
            stack.setItemMeta(meta);
            return stack;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}