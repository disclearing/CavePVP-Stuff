package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PointsButton extends Button {
    private Team team;

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Points Information";
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.GRAY + "View all information");
        toReturn.add(ChatColor.GRAY + "regarding faction points");
        toReturn.add("");
        toReturn.add(ChatColor.translate("&4&l┃ &fTotal Points: &c" + team.recalculatePoints()));
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Click to view a breakdown of your points");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EXP_BOTTLE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        player.chat("/t pbr self");
    }
}
