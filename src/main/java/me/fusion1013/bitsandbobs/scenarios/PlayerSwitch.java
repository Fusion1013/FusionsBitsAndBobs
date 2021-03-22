package me.fusion1013.bitsandbobs.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class PlayerSwitch implements ITimedScenario {

    private Plugin plugin;
    private boolean enabled = false;

    public PlayerSwitch(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void QueueScenario(int delay, int randomDelay) {
        Random r = new Random();
        int timer = delay + r.nextInt(randomDelay);

        BukkitTask task = new BukkitRunnable() {

            public void run() {
                System.out.println("Attempting Switch...");

                Collection<Team> teams = Bukkit.getScoreboardManager().getMainScoreboard().getTeams();

                List<Team> normalizedTeams = new ArrayList<>();

                for (Team t : teams){
                    if (t.getEntries().size() > 0){
                        normalizedTeams.add(t);
                    }
                }

                while (normalizedTeams.size() > 1){
                    Team t = normalizedTeams.remove(0);
                    Random r = new Random();
                    int teamNum = r.nextInt(normalizedTeams.size());

                    switchTeams(t, get(normalizedTeams, teamNum));
                }

                QueueScenario(delay, randomDelay);
            }
        }.runTaskLater(plugin, timer);
    }

    private static <T> T get(Collection<T> coll, int num) {
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }

    private boolean switchTeams(Team t1, Team t2){
        Collection<String> playersOnTeam1 = t1.getEntries();
        Collection<String> playersOnTeam2 = t2.getEntries();

        if (playersOnTeam1.size() == playersOnTeam2.size()) {

            for (int i = 0; i < playersOnTeam1.size(); i++){
                Player p1 = Bukkit.getPlayer(get(playersOnTeam1, i));
                Player p2 = Bukkit.getPlayer(get(playersOnTeam2, i));

                if (p1.getGameMode() != GameMode.SPECTATOR && p2.getGameMode() != GameMode.SPECTATOR){
                    Location loc1 = p1.getLocation();
                    Location loc2 = p2.getLocation();

                    p1.teleport(loc2);
                    p2.teleport(loc1);

                    p1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
                    p2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
                }
            }

            System.out.println("Switched team " + t1.getName() + " with team " + t2.getName());

            return true;
        } else return false;
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
        return Material.ENDER_PEARL;
    }

    @Override
    public String getName() {
        return "Player Switch";
    }

    @Override
    public String getDescription() {
        return "Teams switch locations after a set timer";
    }
}
