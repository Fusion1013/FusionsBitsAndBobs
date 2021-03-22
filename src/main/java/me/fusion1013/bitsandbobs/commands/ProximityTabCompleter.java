package me.fusion1013.bitsandbobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProximityTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            String arg = args.length > 0 ? args[args.length - 1] : "";

            return Arrays.asList("enable", "disable", "register", "unregister", "distance", "samples", "period", "settings", "users").stream()
                    .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            switch (args[0]){
                case "disable":
                case "enable":
                case "distance":
                case "samples":
                case "period":
                    return new ArrayList<String>();
                case "register":
                case "unregister":
                    return null;
            }
        }

        return new ArrayList<String>();
    }
}
