package me.fusion1013.bitsandbobs.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackUtil {

    private ItemStack stack;

    public ItemStackUtil(Material mat){
        this.stack = new ItemStack(mat);
    }

    /**
     * Sets the name of the ItemStack
     * @param name name of the ItemStack
     */
    public ItemStackUtil setName(String name){
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the enchantment glint of the ItemStack
     * @param enchant whether to enchant the ItemStack or not
     * @return
     */
    public ItemStackUtil setEnchantmentGlint(boolean enchant){
        if (enchant){
            stack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta);
        } else {
            stack.removeEnchantment(Enchantment.ARROW_INFINITE);
        }
        return this;
    }

    /**
     * Sets the lore of the ItemStack
     * @param lore Lore
     * @return
     */
    public ItemStackUtil setLore(List<String> lore){
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Hides all attributes
     * @return
     */
    public ItemStackUtil hideAttributes(){
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Returns the ItemStack
     * @return ItemStack
     */
    public ItemStack getItemStack(){
        return stack;
    }
}
