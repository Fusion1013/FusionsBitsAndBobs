package me.fusion1013.bitsandbobs.scenarios;

import me.fusion1013.bitsandbobs.listeners.CraftListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Bukkit.getServer;

public class SpeedMode implements IScenario {

    private CraftListener craftListener;

    public SpeedMode(Plugin plugin){
        craftListener = new CraftListener();
        getServer().getPluginManager().registerEvents(craftListener, plugin);
    }

    @Override
    public void toggle() {
        craftListener.speedMode = !craftListener.speedMode;

        float walkSpeedMultiplier = 1.2f;

        for (Player p : Bukkit.getOnlinePlayers()){
            if (craftListener.speedMode){
                p.setWalkSpeed(0.2f * walkSpeedMultiplier);
            } else {
                p.setWalkSpeed(0.2f);
            }
        }
    }

    @Override
    public boolean getEnabled() {
        return craftListener.speedMode;
    }

    @Override
    public Material getSlotMaterial() {
        return Material.SUGAR;
    }

    @Override
    public String getName() {
        return "SpeedMode";
    }

    @Override
    public String getDescription() {
        return "All players get faster, all tools get efficiency";
    }
}
