package me.fusion1013.bitsandbobs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

import static org.bukkit.Material.GOLD_INGOT;
import static org.bukkit.Material.IRON_INGOT;

public class BlockMinedListener implements Listener {
    public boolean autoSmelt = false;

    @EventHandler
    public void onBlockMined(BlockDropItemEvent event){
        if (autoSmelt){
            for (Item item : event.getItems()) {
                Material drop = getDrop(item.getItemStack().getType());
                if (drop != item.getItemStack().getType()){
                    item.getItemStack().setType(getDrop(drop));

                    Location l = event.getBlock().getLocation();
                    event.getPlayer().getWorld().spawnParticle(Particle.FLAME, l, 10, 1, 1, 1, 0.0001,null, true);
                }
            }
        }
    }

    public Material getDrop(Material material){
        switch(material){
            case IRON_ORE:
                return IRON_INGOT;
            case GOLD_ORE:
                return GOLD_INGOT;
        }

        return material;
    }

    private ItemStack smeltableResult(ItemStack item){
        ItemStack result = null;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()){
            Recipe recipe = iter.next();
            if (!(recipe instanceof FurnaceRecipe)) continue;
            if (((FurnaceRecipe) recipe).getInput().getType() != item.getType()) continue;
            result = recipe.getResult();
            break;
        }
        result.setAmount(item.getAmount());

        return result;
    }
}
