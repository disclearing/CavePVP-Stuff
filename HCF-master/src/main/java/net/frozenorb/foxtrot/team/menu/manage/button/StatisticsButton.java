package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StatisticsButton extends Button {
    private Team team;

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.GRAY + "View all current faction");
        toReturn.add(ChatColor.GRAY + "statistics and rankings.");
        toReturn.add("");
        toReturn.add(ChatColor.translate("&4&l┃ &fKills: &c" + team.getKills()));
        toReturn.add(ChatColor.translate("&4&l┃ &fDeaths: &c" + team.getDeaths()));
        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH Captures: &c" + team.getKothCaptures()));
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Click to view all statistics and rankings");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        // TODO: Open GUI
    }
}
