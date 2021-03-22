package me.fusion1013.bitsandbobs.listeners;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class SwitcherooListener implements Listener {

    public boolean enabled = false;
    public boolean chaosMode = false;

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event){
        // Only runs if switcheroo is enabled
        if(enabled){
            // Gets the entity that was hit and the projectile entity
            Entity hitEntity = event.getHitEntity();
            Entity projectileEntity = event.getEntity();

            Entity shootingEntity = null;

            // Determines the type of the projectile and gets the shooter entity
            if (projectileEntity instanceof Arrow){
                shootingEntity = (Entity)((Arrow) projectileEntity).getShooter();
            } else if (projectileEntity instanceof SpectralArrow) {
                shootingEntity = (Entity)((SpectralArrow) projectileEntity).getShooter();
            } else if (projectileEntity instanceof Trident) {
                shootingEntity = (Entity)((Trident) projectileEntity).getShooter();
            }

            // If none of the entities are null (projectile hit block)
            if (hitEntity != null && shootingEntity != null){

                // If chaosMode is enabled or both entities are players, switch their locations
                if (chaosMode || (hitEntity instanceof Player && shootingEntity instanceof Player)){
                    Location loc1 = hitEntity.getLocation();
                    Location loc2 = shootingEntity.getLocation();

                    hitEntity.teleport(loc2);
                    shootingEntity.teleport(loc1);
                }
            }
        }
    }
}
