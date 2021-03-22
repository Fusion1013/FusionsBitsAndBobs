package me.fusion1013.bitsandbobs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity() instanceof Player){
            ((Player) event.getEntity()).setGameMode(GameMode.SPECTATOR);

            int alivePlayers = 0;
            Player playerAlive = null;
            for (Player p : Bukkit.getOnlinePlayers()){
                if (p.getGameMode() != GameMode.SPECTATOR){
                    alivePlayers++;
                    playerAlive = p;
                }
            }

            if (alivePlayers <= 1){
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendTitle(ChatColor.GOLD + playerAlive.getDisplayName() + " is the winner!", "");
                }
            } else {
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                }
            }
        }
    }
}
