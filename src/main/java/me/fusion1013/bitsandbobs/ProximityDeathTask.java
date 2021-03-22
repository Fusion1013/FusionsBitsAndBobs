package me.fusion1013.bitsandbobs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProximityDeathTask extends BukkitRunnable {

    private List<Player> registeredPlayers;
    private double distance;
    private double currentDistance = 0;
    private int samples;
    private int period;

    public ProximityDeathTask(List<Player> players, double distance, int samples, int period){
        registeredPlayers = players;
        this.distance = distance;
        this.samples = samples;
        this.period = period;
        currentDistance = distance;
    }

    public ProximityDeathTask(List<Player> players, double oldDistance, double distance, int samples, int period){
        registeredPlayers = players;
        this.distance = distance;
        this.samples = samples;
        this.period = period;
        currentDistance = oldDistance;
    }

    @Override
    public void run() {
        double maxDistance = maxPlayerDistance();

        if (currentDistance < distance) currentDistance += .1 * period * (distance - currentDistance);
        else if (currentDistance > distance) currentDistance -= .1 * period * (currentDistance - distance);
        else currentDistance = distance;

        if (maxDistance > distance){
            for (Player p : registeredPlayers) {
                p.setHealth(0);
            }
        }

        displayParticles(getCenter());
    }

    private void displayParticles(Location center){
        List<Location> particleLocations = calculateParticleLocations(registeredPlayers.get(0).getWorld(), center, .5 * currentDistance);

        Random rd = new Random();
        for (Location l : particleLocations){
            if (rd.nextInt(10) == 1) registeredPlayers.get(0).getWorld().spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0.0001,null, true);
        }
    }

    // Calculates the particle locations using the fibonacci sphere algorithm
    private List<Location> calculateParticleLocations(World world, Location center, double r){
        List<Location> locations = new ArrayList<>();

        double phi = Math.PI * (3 - Math.sqrt(5));

        for (int i = 0; i < samples; i++){
            double y = 1 - (i / (float)(samples - 1)) * 2;
            double radius = Math.sqrt(1 - y * y);

            double theta = phi * i;

            double x = Math.cos(theta) * radius;
            double z = Math.sin(theta) * radius;

            x *= r;
            y *= r;
            z *= r;

            locations.add(new Location(world, x + center.getX(), y + center.getY(), z + center.getZ()));
        }

        return locations;
    }

    private Location getCenter(){
        double x = 0, y = 0, z = 0;

        for (Player p1 : registeredPlayers){
            x += p1.getLocation().getX();
            y += p1.getLocation().getY();
            z += p1.getLocation().getZ();
        }

        x /= registeredPlayers.size();
        y /= registeredPlayers.size();
        z /= registeredPlayers.size();

        return new Location(registeredPlayers.get(0).getWorld(), x, y, z);
    }

    private double maxPlayerDistance(){
        double maxDistance = 0;

        for (int x = 0; x < registeredPlayers.size(); x++){
            for (int y = 0; y < registeredPlayers.size(); y++){

                // Get the location of the players
                Location loc1 = registeredPlayers.get(x).getLocation();
                Location loc2 = registeredPlayers.get(y).getLocation();

                // Calculate the distance between the players
                double dist = loc1.distance(loc2);

                if (dist > maxDistance && loc1.getWorld() == loc2.getWorld()) maxDistance = dist;
            }
        }

        return maxDistance;
    }
}
