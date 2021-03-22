package me.fusion1013.bitsandbobs.gui;

import me.fusion1013.bitsandbobs.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeamsGUI extends AbstractGUI {

    public TeamsGUI(int invSize, String invName) {
        super(invSize, invName);

        registerTeams();

        backButton(invSize - 1);
    }

    private void backButton(int slot){

        ItemStack stack = new ItemStackUtil(Material.BARRIER)
                .setName("Back")
                .getItemStack();

        setItem(slot, stack, player -> {

            GUIHolder.uhcgui.reload();
            GUIHolder.uhcgui.open(player);

        });
    }

    private void registerTeams(){
        int slot = 0;
        for (ChatColor c : ChatColor.values()){
            if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam(c.name()) == null){
                Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(c.name());
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(c.name()).setColor(c);
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(c.name()).setAllowFriendlyFire(false);

            ItemStack stack = new ItemStackUtil(getTeamMaterial(c.name()))
                    .setName(c.name())
                    .getItemStack();
            setItem(slot, stack, player -> {

                for (ChatColor c1 : ChatColor.values()){
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam(c1.name()).removeEntry(player.getName());
                }

                Bukkit.getScoreboardManager().getMainScoreboard().getTeam(c.name()).addEntry(player.getName());

                player.sendMessage(ChatColor.YELLOW + "Joined team " + c + c.name());
            });
            slot++;
        }
    }

    // This is the worst method
    private Material getTeamMaterial(String color){
        switch (color){
            case "DARK_BLUE":
                return Material.BLUE_WOOL;
            case "BLUE":
                return Material.LIGHT_BLUE_WOOL;
            case "DARK_GREEN":
                return Material.GREEN_WOOL;
            case "DARK_AQUA":
                return Material.CYAN_WOOL;
            case "DARK_RED":
                return Material.RED_WOOL;
            case "DARK_PURPLE":
                return Material.PURPLE_WOOL;
            case "GOLD":
                return Material.GOLD_BLOCK;
            case "GRAY":
                return Material.LIGHT_GRAY_WOOL;
            case "DARK_GRAY":
                return Material.GRAY_WOOL;
            case "GREEN":
                return Material.LIME_WOOL;
            case "AQUA":
                return Material.LIGHT_BLUE_CONCRETE;
            case "RED":
                return Material.RED_CONCRETE;
            case "LIGHT_PURPLE":
                return Material.MAGENTA_WOOL;
            case "YELLOW":
                return Material.YELLOW_WOOL;
            case "WHITE":
                return Material.WHITE_WOOL;
            case "BLACK":
                return Material.BLACK_WOOL;
            default:
                return Material.WHITE_WOOL;
        }
    }
}
