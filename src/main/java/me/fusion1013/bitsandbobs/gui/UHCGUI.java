package me.fusion1013.bitsandbobs.gui;

import me.fusion1013.bitsandbobs.listeners.DamageListener;
import me.fusion1013.bitsandbobs.listeners.SwitcherooListener;
import me.fusion1013.bitsandbobs.util.ItemStackUtil;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UHCGUI extends AbstractGUI {

    private Plugin plugin;

    // Default Values
    private int borderStartSize = 4000;
    private int borderEndSize = 100;
    private int timeBeforeShrink = 3600;
    private int shrinkTime = 3600;
    private int eternalDayTime = -1;

    private int switchDelay = 60 * 8; // Time in seconds
    private int switchRandom = 120;

    // Guis
    ScenarioGUI scenarioGui;
    TeamsGUI teamsGUI;

    private World world;

    public UHCGUI(Plugin plugin, World world) {
        super(9, "Fusion-UHC");

        // Set Variables
        this.world = world;
        this.plugin = plugin;

        // Create GUI's
        scenarioGui = new ScenarioGUI(27, "Scenarios", plugin);
        teamsGUI = new TeamsGUI(27, "Teams");
        reload();
    }

    public void reload(){
        teams(6);
        addScenarioButton(2);
        startGame(4, 11, 10);
    }

    public void setDefaults(){
        borderStartSize = 4000;
        borderEndSize = 100;
        timeBeforeShrink = 3600;
        shrinkTime = 3600;
    }

    public Map<String, Integer> getSettings(){
        Map<String, Integer> settings = new HashMap<>();

        settings.put("border_start_size", borderStartSize);
        settings.put("border_end_size", borderEndSize);
        settings.put("time_before_shrink", timeBeforeShrink);
        settings.put("shrink_time", shrinkTime);

        return settings;
    }

    /**
     * Starts a new game of Fusion-UHC
     * @param slot the slot in the GUI for the startGame item
     * @param countdown countdown in seconds before the game starts
     * @param delayBeforeCountdown delay in seconds before the countdown starts
     */
    private void startGame(int slot, int countdown, int delayBeforeCountdown){
        // Creates the UHC Diamond
        ItemStack stack = new ItemStackUtil(Material.DIAMOND)
                .setName("Start UHC")
                .setEnchantmentGlint(true)
                .setLore(Arrays.asList("Starts a game of Fusion-UHC!"))
                .getItemStack();

        // Sets the ItemStack in the correct GUI slot
        setItem(slot, stack, player ->{
            Bukkit.broadcastMessage(ChatColor.GREEN + "Starting Game...");

            // Give players effects, set gamemode to survival, clear inventories
            int effectDuration = 20 * countdown + 20 * delayBeforeCountdown;
            for (Player p : Bukkit.getOnlinePlayers()){
                if (p.getGameMode() != GameMode.SPECTATOR){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effectDuration, 9));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, effectDuration, 255));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 255));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, effectDuration, 255));

                    p.setGameMode(GameMode.SURVIVAL);

                    p.getInventory().clear();

                    p.setExp(0);
                }
            }

            // Set world time
            world.setTime(0);

            // Gamerules
            for (World w : Bukkit.getWorlds()){
                w.setGameRule(GameRule.NATURAL_REGENERATION, false);
                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }

            // Scoreboard
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            Objective objective = board.registerNewObjective("health", "health", "Health");
            objective.setRenderType(RenderType.HEARTS);
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            for (Player p : Bukkit.getOnlinePlayers()){
                p.setScoreboard(board);
                p.setHealth(p.getHealth());
            }

            // Set border initial size
            WorldBorder border = world.getWorldBorder();
            border.setCenter(new Location(world, 0, 0, 0));
            border.setSize(borderStartSize, 0);

            // Teleport all players that are not in gm 3
            spreadPlayers();

            // Start the countdown
            startCountdown(countdown, delayBeforeCountdown);

            this.openInventories.remove(player.getUniqueId());
            player.closeInventory();
        });
    }

    private void timerEvents(){
        // Time Alert
        int period = 1200;

        BukkitTask task = new BukkitRunnable() {
            int counter = 0;

            public void run() {
                counter += period / 60;
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Time Elapsed: " + counter + " minutes");

                WorldBorder border = world.getWorldBorder();
                double borderSize = Math.round(border.getSize());

                int bt = (borderStartSize - borderEndSize) / 2;
                float borderSpeed = Math.round(bt / (float)shrinkTime * 100) / 100f;
                if (counter * 60 < timeBeforeShrink) borderSpeed = 0;
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Border is currently at: " + borderSize / 2 + "x/z, moving at a speed of " + borderSpeed + " blocks/second");

                for (Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
            }
        }.runTaskTimer(plugin, 20 * period, 20 * period);

        // Eternal day
        if (eternalDayTime >= 0){
            BukkitTask task2 = new BukkitRunnable() {

                public void run() {
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Eternal Day Enabled");

                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                    }

                    // Rotate the sun to midday position over 20 seconds
                    BukkitTask task = new BukkitRunnable() {
                        long totalTravelDistance = 0;
                        public void run() {
                            long time = world.getTime();
                            long targetTime = 6000;
                            long travelDistance;

                            if (time > targetTime){
                                travelDistance = 24000 - time + targetTime;
                            } else {
                                travelDistance = targetTime - time;
                            }

                            if (totalTravelDistance == 0) totalTravelDistance = travelDistance;

                            if (travelDistance <= 0){
                                this.cancel();
                            } else {
                                long add = Math.min(totalTravelDistance / 400, travelDistance);
                                world.setTime(world.getTime()+add);
                            }
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }

            }.runTaskLater(plugin, eternalDayTime * 20);
        }
    }

    /**
     * Spreads all players that are not in spectator around the world
     */
    private void spreadPlayers(){
        // Teleport players
        Bukkit.broadcastMessage(ChatColor.GREEN + "Spreading Players...");
        int x = 0;
        int z = 0;
        int minDistance = borderStartSize / 6;
        int maxRange = borderStartSize / 2;
        String players = "@a[gamemode=!spectator]";

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.getServer().dispatchCommand(console, String.format("spreadplayers %d %d %d %d %b %s", x, z, minDistance, maxRange, true, players));
    }

    private void borderEvents(){
        // Border handling
        WorldBorder border = world.getWorldBorder();

        // Set border initial size
        border.setCenter(new Location(world, 0, 0, 0));
        border.setSize(borderStartSize, 0);

        // Schedule border event
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                border.setCenter(new Location(world, 0, 0, 0));
                border.setSize(borderEndSize, shrinkTime);
                Bukkit.broadcastMessage(ChatColor.GREEN + "Shrinking border from "
                        + borderStartSize + " blocks to "
                        + borderEndSize + " blocks over "
                        + shrinkTime + " seconds");
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
                }
            }
        }, 20 * timeBeforeShrink);
    }

    private void startCountdown(int countdown, int delayBeforeCountdown){
        BukkitTask task = new BukkitRunnable() {
            int counter = countdown;

            public void run() {

                if (counter == countdown){ // Countdown Starting
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Starting in:");
                } else if (counter == 0) { // Countdown Finished
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Go!");
                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    }
                    timerEvents();
                    borderEvents();
                    scenarioGui.queueTimedScenarios(20 * switchDelay, 20 * switchRandom);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                    this.cancel();
                } else {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "" + counter);
                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }
                }
                counter--;
            }
        }.runTaskTimer(plugin, 20 * delayBeforeCountdown, 20);
    }

    // Teams feature
    private void teams(int slot){
        // Creates an iron helmet ItemStack
        ItemStack stack = new ItemStackUtil(Material.IRON_HELMET)
                .setName("Teams")
                .getItemStack();

        // Creates the item in the GUI with the given method
        setItem(slot, stack, player -> {
            teamsGUI.open(player);
        });
    }

    /* Scenarios */

    private void addScenarioButton(int slot){
        ItemStack stack = new ItemStackUtil(Material.EMERALD)
                .setName("Scenarios")
                .setLore(scenarioGui.getActiveScenarios())
                .getItemStack();
        setItem(slot, stack, player -> {
            scenarioGui.open(player);
        });
    }

    // Getters and Setters

    public void setBorderStartSize(int borderStartSize) {
        this.borderStartSize = borderStartSize;
    }

    public void setBorderEndSize(int borderEndSize) {
        this.borderEndSize = borderEndSize;
    }

    public void setTimeBeforeShrink(int timeBeforeShrink) {
        this.timeBeforeShrink = timeBeforeShrink;
    }

    public void setShrinkTime(int shrinkTime) {
        this.shrinkTime = shrinkTime;
    }

    public void setEternalDayTime(int eternalDayTime) {this.eternalDayTime = eternalDayTime;}
}