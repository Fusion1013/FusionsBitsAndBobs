package me.fusion1013.bitsandbobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UHCTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            String arg = args.length > 0 ? args[args.length - 1] : "";

            return Arrays.asList("border", "defaults", "settings", "eternalday").stream()
                    .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            switch (args[0]){
                case "border":
                    String arg = args.length > 0 ? args[args.length - 1] : "";

                    return Arrays.asList("startsize", "endsize", "timebeforeshrink", "shrinktime").stream()
                            .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                            .collect(Collectors.toList());
                case "eternalday":
                    return addTime(args[1]);
                default:
                    return new ArrayList<String>();
            }
        } else if (args.length == 3){
            switch (args[1]){
                case "timebeforeshrink":
                case "shrinktime":
                    if (args[2].length() != 0){
                        switch (args[2].charAt(args[2].length() - 1)){
                            case 's':
                            case 'm':
                            case 'h':
                                break;
                            default:
                                return addTime(args[2]);
                        }
                    }
                    break;
            }
        }

        return new ArrayList<String>();
    }

    private List<String> addTime(String arg){
        if (arg.length() != 0){
            switch (arg.charAt(arg.length() - 1)){
                case 's':
                case 'm':
                case 'h':
                    return new ArrayList<String>();
                default:
                    return Arrays.asList(arg + "s", arg + "m", arg + "h");
            }
        } else {
            return new ArrayList<String>();
        }
    }
}
