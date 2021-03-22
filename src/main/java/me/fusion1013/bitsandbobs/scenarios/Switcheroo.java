package me.fusion1013.bitsandbobs.scenarios;

import me.fusion1013.bitsandbobs.listeners.SwitcherooListener;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class Switcheroo implements IScenario {

    private SwitcherooListener switcherooListener;

    public Switcheroo(Plugin plugin){
        switcherooListener = new SwitcherooListener();
        getServer().getPluginManager().registerEvents(switcherooListener, plugin);
    }

    @Override
    public void toggle() {
        if (switcherooListener.enabled && !switcherooListener.chaosMode){
            switcherooListener.chaosMode = true;
        } else if (switcherooListener.enabled){
            switcherooListener.enabled = false;
            switcherooListener.chaosMode = false;
        } else {
            switcherooListener.enabled = true;
        }
    }

    @Override
    public boolean getEnabled() {
        return switcherooListener.enabled;
    }

    @Override
    public Material getSlotMaterial() {
        if (switcherooListener.chaosMode) return Material.TIPPED_ARROW;
        else return Material.ARROW;
    }

    @Override
    public String getName() {
        if (switcherooListener.chaosMode) return "Chaos Switcheroo";
        else return "Switcheroo";
    }

    @Override
    public String getDescription() {
        return "Players switch place when hit by a projectile";
    }
}
