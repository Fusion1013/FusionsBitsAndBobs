package me.fusion1013.bitsandbobs.commands;

import me.fusion1013.bitsandbobs.gui.GUIHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class UHCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Opens the UHC GUI
        if (args.length < 1){
            GUIHolder.uhcgui.reload();
            GUIHolder.uhcgui.open((Player)sender);
            return true;
        }

        switch (args[0]){

            case "border":
                switch (args[1]){
                    case "startsize":
                        int borderStartSize = Integer.parseInt(args[2]);
                        GUIHolder.uhcgui.setBorderStartSize(borderStartSize);
                        sender.sendMessage(ChatColor.GREEN + "Set border start size to: " + borderStartSize + " blocks (diameter)");
                        return true;
                    case "endsize":
                        int borderEndSize = Integer.parseInt(args[2]);
                        GUIHolder.uhcgui.setBorderEndSize(borderEndSize);
                        sender.sendMessage(ChatColor.GREEN + "Set border end size to: " + borderEndSize + " blocks (diameter)");
                        return true;
                    case "timebeforeshrink":
                        sender.sendMessage(ChatColor.GREEN + "Set time before border shrink to: " + args[2]);
                        int timeBeforeShrink = formatTime(args[2]);
                        GUIHolder.uhcgui.setTimeBeforeShrink(timeBeforeShrink);
                        return true;
                    case "shrinktime":
                        sender.sendMessage(ChatColor.GREEN + "Set border shrink time to: " + args[2]);
                        int shrinkTime = formatTime(args[2]);
                        GUIHolder.uhcgui.setShrinkTime(shrinkTime);
                        return true;
                }
            case "defaults":
                GUIHolder.uhcgui.setDefaults();
                printSettings(sender);
                break;
            case "settings":
                printSettings(sender);
                break;
            case "eternalday":
                sender.sendMessage(ChatColor.GREEN + "Set eternal day timer to: " + args[1]);
                int eternalDayTime = formatTime(args[1]);
                GUIHolder.uhcgui.setEternalDayTime(eternalDayTime);
                break;
        }

        return true;
    }

    private void printSettings(CommandSender sender){
        Map settings = GUIHolder.uhcgui.getSettings();
        sender.sendMessage(ChatColor.GREEN + "Current Settings:");
        sender.sendMessage(ChatColor.YELLOW + "Border Start Size: " + Integer.parseInt(settings.get("border_start_size").toString()) + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Border End Size: " + Integer.parseInt(settings.get("border_end_size").toString()) + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Time Before Shrink: " + Integer.parseInt(settings.get("time_before_shrink").toString()) / 60 + "m");
        sender.sendMessage(ChatColor.YELLOW + "Shrink Time: " + Integer.parseInt(settings.get("shrink_time").toString()) / 60 + "m");

        int bss = Integer.parseInt(settings.get("border_start_size").toString());
        int bes = Integer.parseInt(settings.get("border_end_size").toString());
        int st = Integer.parseInt(settings.get("shrink_time").toString());
        int bt = (bss - bes) / 2;
        float borderSpeed = bt / (float)st;

        sender.sendMessage(ChatColor.YELLOW + "Border Speed: " + Math.round(borderSpeed * 1000) / 1000.0 + " blocks/second");
    }

    private int formatTime(String input){
        int formatted = Integer.parseInt(input.substring(0, input.length() - 1));

        switch (input.charAt(input.length() - 1)) {
            case 'm':
                formatted *= 60;
                break;
            case 'h':
                formatted *= 3600;
                break;
        }

        return formatted;
    }
}
