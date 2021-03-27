package me.fusion1013.bitsandbobs;

import me.fusion1013.bitsandbobs.commands.*;
import me.fusion1013.bitsandbobs.gui.AbstractGUIListener;
import me.fusion1013.bitsandbobs.gui.GUIHolder;
import me.fusion1013.bitsandbobs.gui.UHCGUI;
import me.fusion1013.bitsandbobs.listeners.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Listeners
        DeathListener dl = new DeathListener();

        // Command Registration
        this.getCommand("proximity").setExecutor(new ProximityCommand(this));
        this.getCommand("proximity").setTabCompleter(new ProximityTabCompleter());

        this.getCommand("uhc").setExecutor(new UHCCommand());
        this.getCommand("uhc").setTabCompleter(new UHCTabCompleter());

        this.getCommand("delapfloor").setExecutor(new DelapadatedFloorCommand());

        // Listener Registration
        getServer().getPluginManager().registerEvents(new AbstractGUIListener(), this);
        getServer().getPluginManager().registerEvents(dl, this);

        // Instantiate all GUI's
        GUIHolder.uhcgui = new UHCGUI(this, Bukkit.getWorlds().get(0));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
