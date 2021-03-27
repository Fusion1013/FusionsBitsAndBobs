package me.fusion1013.bitsandbobs.gui;

import me.fusion1013.bitsandbobs.util.ItemStackUtil;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class UHCGUI extends AbstractGUI {

    private final Plugin plugin;

    // Default Values
    private int borderStartSize = 4000;
    private int borderEndSize = 100;
    private int timeBeforeShrink = 1200;
    private int shrinkTime = 2400;
    private int eternalDayTime = 1600;

    // GUI's
    ScenarioGUI scenarioGui;
    TeamsGUI teamsGUI;

    private final World world;

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

    /**
     * Reloads the GUI
     */
    public void reload(){
        teams();
        addScenarioButton();
        startGame();
    }

    /**
     * Resets everything to default values
     */
    public void setDefaults(){
        borderStartSize = 4000;
        borderEndSize = 100;
        timeBeforeShrink = 1200;
        shrinkTime = 2400;
        eternalDayTime = 1600;
    }

    /**
     * Returns a map of all current settings
     * @return map containing the current settings
     */
    public Map<String, Integer> getSettings(){
        Map<String, Integer> settings = new HashMap<>();

        settings.put("border_start_size", borderStartSize);
        settings.put("border_end_size", borderEndSize);
        settings.put("time_before_shrink", timeBeforeShrink);
        settings.put("shrink_time", shrinkTime);
        settings.put("eternal_day_time", eternalDayTime);

        return settings;
    }

    /**
     * Starts a new game of Fusion-UHC
     */
    private void startGame(){
        // Creates the UHC Diamond
        ItemStack stack = new ItemStackUtil(Material.DIAMOND)
                .setName("Start UHC")
                .setEnchantmentGlint(true)
                .setLore(Collections.singletonList("Starts a game of Fusion-UHC!"))
                .getItemStack();

        // Sets the ItemStack in the correct GUI slot
        setItem(4, stack, player ->{
            Bukkit.broadcastMessage(ChatColor.GREEN + "Starting Game...");

            // Give players effects, set gamemode to survival, clear inventories
            int freezeDuration = 20 * 11 + 20 * 10;
            for (Player p : Bukkit.getOnlinePlayers()){
                if (p.getGameMode() != GameMode.SPECTATOR){
                    FreezePlayer(p, freezeDuration);
                    ResetPlayer(p);
                }
            }

            PrepareWorlds();

            // Teleport all players that are not in gm 3
            spreadPlayers();

            // Start the countdown
            startCountdown();

            openInventories.remove(player.getUniqueId());
            player.closeInventory();
        });
    }

    /**
     * Resets all worlds
     */
    private void PrepareWorlds(){
        for (World w : Bukkit.getWorlds()){

            // Set gamerules
            w.setGameRule(GameRule.NATURAL_REGENERATION, false);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

            // Set world times to dawn
            w.setTime(0);

            // Set border initial size
            WorldBorder border = world.getWorldBorder();
            border.setCenter(new Location(world, 0, 0, 0));
            border.setSize(borderStartSize, 0);
        }
    }

    /**
     * Resets the player p
     * @param p the player to reset
     */
    private void ResetPlayer(Player p){
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setExp(0);

        // Clears all advancement progress
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext())
        {
            AdvancementProgress progress = p.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria())
                progress.revokeCriteria(criteria);
        }
    }

    /**
     * Freezes the player p for the duration
     * @param p player to freeze
     * @param duration duration to freeze the player in ticks
     */
    private void FreezePlayer(Player p, int duration){
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 200));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 200));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 200));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, duration, 200));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 200));
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

    /**
     * Starts the border events
     */
    private void borderEvents(){
        // Border handling
        WorldBorder border = world.getWorldBorder();

        // Schedule border event
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            border.setSize(borderEndSize, shrinkTime);
            Bukkit.broadcastMessage(ChatColor.GREEN + "Shrinking border from "
                    + borderStartSize + " blocks to "
                    + borderEndSize + " blocks over "
                    + shrinkTime + " seconds");
            for (Player p : Bukkit.getOnlinePlayers()){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            }
        }, 20 * timeBeforeShrink);
    }

    /**
     * Starts the countdown before the game starts
     */
    private void startCountdown(){
        new BukkitRunnable() {
            int counter = 11;

            // Runs the countdown after delay with a period of 1 second
            public void run() {

                if (counter == 11){ // Countdown Starting
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Starting in:");
                } else if (counter == 0) { // Countdown Finished
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Go!");
                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        p.setGameMode(GameMode.SURVIVAL);
                    }

                    // Starts all the timed events
                    initTimedEvents();
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                    this.cancel();
                } else { // Countdown progress
                    Bukkit.broadcastMessage(ChatColor.GREEN + "" + counter);
                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }
                }
                counter--;
            }
        }.runTaskTimer(plugin, 20 * 10, 20);
    }

    /**
     * Initializes all timed events
     */
    private void initTimedEvents(){
        alertEvent();
        eternalDayEvent();
        borderEvents();
        scenarioGui.queueTimedScenarios();
    }

    /**
     * Sends an alert in chat every 20 minutes, with the current border size and speed
     */
    private void alertEvent(){
        // Time Alert
        int period = 1200;

        new BukkitRunnable() {
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
    }

    /**
     * Initializes the eternal day event
     */
    private void eternalDayEvent(){
        // Eternal day
        if (eternalDayTime >= 0){
            new BukkitRunnable() {

                public void run() {
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Eternal Day Enabled");

                    for (Player p : Bukkit.getOnlinePlayers()){
                        p.playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                    }

                    // Rotate the sun to midday position over 20 seconds
                    new BukkitRunnable() {
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
     * Teams GUI slot
     */
    private void teams(){
        // Creates an iron helmet ItemStack
        ItemStack stack = new ItemStackUtil(Material.IRON_HELMET)
                .setName("Teams")
                .getItemStack();

        // Creates the item in the GUI with the given method
        setItem(6, stack, player -> teamsGUI.open(player));
    }

    /* Scenarios */

    private void addScenarioButton(){
        ItemStack stack = new ItemStackUtil(Material.EMERALD)
                .setName("Scenarios")
                .setLore(scenarioGui.getActiveScenarios())
                .getItemStack();
        setItem(2, stack, player -> scenarioGui.open(player));
    }

    /* Getters & Setters */

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
