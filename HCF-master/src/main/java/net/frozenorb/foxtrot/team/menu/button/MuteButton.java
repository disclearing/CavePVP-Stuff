package net.frozenorb.foxtrot.team.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import cc.fyre.proton.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MuteButton extends Button {

    private int minutes;
    private Team team;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        TeamShadowMuteCommand.teamShadowMute(player, team, minutes);
    }

    @Override
    public String getName(Player player) {
        return "§e" + minutes +"m mute";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 0;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack it = new ItemStack(getMaterial(player));
        ItemMeta im = it.getItemMeta();

        im.setLore(getDescription(player));
        im.setDisplayName(getName(player));
        it.setItemMeta(im);
        it.setAmount(minutes);

        return it;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CHEST;
    }
}
