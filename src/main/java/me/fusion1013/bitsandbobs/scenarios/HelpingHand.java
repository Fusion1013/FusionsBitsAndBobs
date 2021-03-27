package me.fusion1013.bitsandbobs.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HelpingHand implements ITimedScenario {

    private Plugin plugin;
    private boolean enabled;

    public HelpingHand(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void QueueScenario(int delay, int randomDelay) {

        int timer = 20 * 60 * 10;

        BukkitTask task = new BukkitRunnable() {

            public void run() {
                double minHealth = Integer.MAX_VALUE;
                Player minPlayer = null;

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.getHealth() < minHealth) {
                        minHealth = player.getHealth();
                        minPlayer = player;
                    }
                }

                if (minPlayer != null && minHealth != 20){
                    minPlayer.setHealth(Math.min(minPlayer.getHealth() + 2, 20));
                    minPlayer.sendMessage(ChatColor.GREEN + "You have been healed!");
                    minPlayer.playSound(minPlayer.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                }

                QueueScenario(delay, randomDelay);
            }

        }.runTaskLater(plugin, timer);
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
        return Material.GOLDEN_APPLE;
    }

    @Override
    public String getName() {
        return "HelpingHand";
    }

    @Override
    public String getDescription() {
        return "The weakest player gets a heart every ten minutes";
    }
}
