package me.fusion1013.bitsandbobs.scenarios;

import me.fusion1013.bitsandbobs.listeners.BlockMinedListener;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class AutoSmelt implements IScenario {

    private Plugin plugin;
    private BlockMinedListener blockMinedListener;

    public AutoSmelt(Plugin plugin){
        this.plugin = plugin;
        blockMinedListener = new BlockMinedListener();
        getServer().getPluginManager().registerEvents(blockMinedListener, plugin);
    }

    @Override
    public void toggle() {
        blockMinedListener.autoSmelt = !blockMinedListener.autoSmelt;
    }

    @Override
    public boolean getEnabled() {
        return blockMinedListener.autoSmelt;
    }

    @Override
    public Material getSlotMaterial() {
        return Material.FURNACE;
    }

    @Override
    public String getName() {
        return "AutoSmelt";
    }

    @Override
    public String getDescription() {
        return "All ores autosmelt";
    }
}
