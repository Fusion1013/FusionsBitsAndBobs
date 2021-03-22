package me.fusion1013.bitsandbobs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    public boolean noFall = false;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (noFall && event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
        }
    }
}
