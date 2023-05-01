package cc.fyre.neutron.profile.menu.history;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.Punishments;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.menu.history.type.HistoryTypeMenu;
import cc.fyre.neutron.profile.menu.staffhistory.staffhistory.StaffHistoryTypeMenu;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class HistoryMenu extends Menu {
    private Profile profile;
    private List<IPunishment> punishments;
    public HistoryMenu(Profile profile) {
        this.profile = profile;
        this.punishments = profile.getPunishments();

    }
    @Override
    public String getTitle(Player player) {
        return ChatColor.GOLD + " Punishments";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = Maps.newHashMap();
        if(player.hasPermission("neutron.command.blacklist")) {
            toReturn.put(0, new HistoryButton(IPunishment.Type.REMOVE_ABLE,  ChatColor.GOLD + " Blacklists",
                    Material.WOOL, (byte) 15, Punishments.BLACKLIST));
        }
        if(player.hasPermission("neutron.command.ban")) {
            toReturn.put(2, new HistoryButton(IPunishment.Type.REMOVE_ABLE, ChatColor.GOLD + " Bans",
                    Material.WOOL, (byte) 14, Punishments.BAN));
        }
        if(player.hasPermission("neutron.command.warn")) {
            toReturn.put(4, new HistoryButton(IPunishment.Type.REMOVE_ABLE, ChatColor.GOLD + " Warns",
                    Material.WOOL, (byte) 1, Punishments.WARN));
        }
        if(player.hasPermission("neutron.command.mute")) {
            toReturn.put(6, new HistoryButton(IPunishment.Type.REMOVE_ABLE, ChatColor.GOLD + " Mutes",
                    Material.WOOL, (byte) 4, Punishments.MUTE));
        }
        if(player.hasPermission("neutron.command.kick")) {
            toReturn.put(8, new HistoryButton(IPunishment.Type.NORMAL, ChatColor.GOLD + " Kicks",
                    Material.WOOL, (byte) 5, Punishments.KICK));
        }


        return toReturn;
    }






    @AllArgsConstructor
    class HistoryButton extends Button {

        @Getter private IPunishment.Type type;
        @Getter private String displayName;
        @Getter private Material item;
        @Getter private byte damage;
        @Getter private Punishments types;

        @Override
        public String getName(Player var1) {
            return displayName;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public int getAmount(Player player) {
            return 1;
        }

        @Override
        public Material getMaterial(Player player) {
            return item;
        }
        @Override
        public byte getDamageValue(Player player) {
            return damage;
        }


        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            new HistoryTypeMenu(this.displayName + ChatColor.GRAY,profile,punishments, types).openMenu(player);
        }
    }
}
