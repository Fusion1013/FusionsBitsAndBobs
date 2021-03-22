package me.fusion1013.bitsandbobs.scenarios;

import me.fusion1013.bitsandbobs.listeners.DamageListener;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class NoFall implements IScenario {

    private DamageListener damageListener;

    public NoFall(Plugin plugin){
        damageListener = new DamageListener();
        getServer().getPluginManager().registerEvents(damageListener, plugin);
    }

    @Override
    public void toggle() {
        damageListener.noFall = !damageListener.noFall;
    }

    @Override
    public boolean getEnabled() {
        return damageListener.noFall;
    }

    @Override
    public Material getSlotMaterial() {
        return Material.FEATHER;
    }

    @Override
    public String getName() {
        return "NoFall";
    }

    @Override
    public String getDescription() {
        return "Players do not take fall damage";
    }
}
