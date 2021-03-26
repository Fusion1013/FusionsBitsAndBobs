package me.fusion1013.bitsandbobs.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {
    public boolean speedMode = false;

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if (speedMode){
            ItemStack result = event.getRecipe().getResult();
            Material material = result.getType();

            System.out.println("Item crafted: " + result);

            Enchantment ench = getEnchantment(material);
            if (ench != null) {
                System.out.println(ench.toString());
                result.addEnchantment(ench, 2);
                event.setCurrentItem(result);
            }
        }
    }

    private Enchantment getEnchantment(Material material){
        switch(material){
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
                return Enchantment.DIG_SPEED;
        }
        return null;
    }
}
