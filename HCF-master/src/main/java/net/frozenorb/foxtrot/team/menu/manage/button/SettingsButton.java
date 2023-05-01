package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import net.frozenorb.foxtrot.team.upgrade.effects.PurchaseableEffects;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SettingsButton extends Button {
    private Team team;

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Settings";
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.GRAY + "View all current faction");
        toReturn.add(ChatColor.GRAY + "settings and modify them.");
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Click to view & modify all settings");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WORKBENCH;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        // TODO: Open menu
    }
}
