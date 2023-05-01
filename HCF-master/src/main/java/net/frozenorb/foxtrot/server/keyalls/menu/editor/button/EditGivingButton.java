package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class EditGivingButton extends Button {
    private Menu parentMenu;
    private KeyAll keyAll;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "Status";
    }

    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList(ChatColor.GRAY + "Modify if the key-all", ChatColor.GRAY + "is currently available to players.", "", ChatColor.translate("&6&l┃ &fCurrent: &e" + keyAll.isGiving()),
                "", ChatColor.GREEN + "Click to modify this key-all's status.");
    }

    @Override
    public Material getMaterial(Player player) {
        if (keyAll.isGiving()) {
            return Material.EMERALD_BLOCK;
        }

        return Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

        if (keyAll.isGiving()) {
            player.sendMessage(ChatColor.RED + "Key-All is no longer available to players.");

            keyAll.setGiving(false);
            keyAll.setGiveAllTime(0);
            keyAll.setEnd(0);
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Key-All is now available to players.");

        keyAll.setEnd(System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
        keyAll.setGiveAllTime(0);
        keyAll.setGiving(true);
    }
}