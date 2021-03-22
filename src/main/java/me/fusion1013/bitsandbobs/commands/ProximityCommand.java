package me.fusion1013.bitsandbobs.commands;

import me.fusion1013.bitsandbobs.ProximityDeathTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ProximityCommand implements CommandExecutor {

    BukkitTask task;
    List<Player> registeredPlayers;
    double distance = 10;
    int samples = 1000;
    int period = 10;

    Plugin plugin;
    public ProximityCommand(Plugin plugin){
        this.plugin = plugin;
        registeredPlayers = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (args[0]){
            case "enable":
                task = new ProximityDeathTask(registeredPlayers, distance, samples, period).runTaskTimer(plugin, 1, period);
                sender.sendMessage("Enabled Proximity Death with settings: ");
                sender.sendMessage("Distance: " + distance);
                sender.sendMessage("Samples: " + samples);
                sender.sendMessage("Period: " + period);
                break;
            case "disable":
                task.cancel();
                sender.sendMessage("Disabled Proximity Death");
                break;
            case "register":
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) registeredPlayers.add(target);
                sender.sendMessage("Registered user: " + target.getName());
                break;
            case "unregister":
                Player target2 = Bukkit.getPlayer(args[1]);
                removeAll(target2);
                sender.sendMessage("Unregistered user: " + target2.getName());
                break;
            case "distance":
                double temp = distance;
                distance = Double.parseDouble(args[1]);
                task.cancel();
                task = new ProximityDeathTask(registeredPlayers, temp, distance, samples, period).runTaskTimer(plugin, 1, period);
                sender.sendMessage("Changed distance to: " + distance);
                break;
            case "samples":
                samples = Integer.parseInt(args[1]);
                task.cancel();
                task = new ProximityDeathTask(registeredPlayers, distance, samples, period).runTaskTimer(plugin, 1, period);
                sender.sendMessage("Changed samples to: " + samples);
                break;
            case "period":
                period = Integer.parseInt(args[1]);
                task.cancel();
                task = new ProximityDeathTask(registeredPlayers, distance, samples, period).runTaskTimer(plugin, 1, period);
                sender.sendMessage("Changed period to: " + period);
                break;
            case "settings":
                sender.sendMessage("Proximity Death has the following settings:");
                sender.sendMessage("Distance: " + distance);
                sender.sendMessage("Samples: " + samples);
                sender.sendMessage("Period: " + period);
                break;
            case "users":
                sender.sendMessage("Registered Users:");
                for (Player p : registeredPlayers) {
                    sender.sendMessage("-" + p.getName());
                }
                break;
        }

        return true;
    }

    private void removeAll(Player player){
        for (int i = 0; i < registeredPlayers.size(); i++){
            if (registeredPlayers.get(i).getName() == player.getName()){
                registeredPlayers.remove(i);
                i--;
            }
        }
    }
}
