package me.fusion1013.bitsandbobs.scenarios;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Snitch implements ITimedScenario {

    private boolean enabled;
    private Plugin plugin;

    public Snitch(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void QueueScenario() {
        BukkitTask task = new BukkitRunnable() {

            public void run() {
                List<Player> playerList = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (p.getGameMode() == GameMode.SURVIVAL) playerList.add(p);
                }

                Random r = new Random();
                Player p = playerList.get(r.nextInt(playerList.size()));

                String environment = p.getWorld().getEnvironment().name();
                switch (environment){
                    case "NORMAL":
                        environment = "overworld";
                        break;
                    case "NETHER":
                        environment = "nether";
                        break;
                    case "THE_END":
                        environment = "end";
                        break;
                }
                String location = "[" + Math.round(p.getLocation().getX()) + ", " + Math.round(p.getLocation().getZ()) + "] in the " + environment;
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " is at coordinates: " + location);
                p.playSound(p.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1, 1);
            }

        }.runTaskTimer(plugin, ScenarioSettingsHolder.snitchDelay, ScenarioSettingsHolder.snitchDelay);
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public Material getSlotMaterial() {
        return Material.COMPASS;
    }

    @Override
    public String getName() {
        return "Snitch";
    }

    @Override
    public String getDescription() {
        return "Prints a random players coordinates in chat every " + ScenarioSettingsHolder.snitchDelay / (20 * 60) + "m";
    }
}
