package cc.fyre.neutron.profile.menu.staffhistory;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.menu.staffhistory.staffhistory.StaffHistoryTypeMenu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

//This code is actual fucking cancer please swat me HOLYSHIT LOL

@AllArgsConstructor
public class StaffHistoryMenu extends Menu {

    private static Button RED_GLASS = new Button() {
        @Override
        public String getName(Player player) {
            return " ";
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.STAINED_GLASS_PANE;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 14;
        }
    };
    private static Integer[] RED_GLASS_POSITIONS = new Integer[]{
            0,1,2,3,4,5,6,7,8,
            9,17,
            18,26,
            27,35,
            36,37,38,39,40,41,42,43,44
    };

    @Getter private Profile profile;

    @Getter private Map<UUID,Punishment> executedPunishments;
    @Getter private Map<UUID,RemoveAblePunishment> executedRemoveAblePunishments;
    @Getter private Map<UUID,RemoveAblePunishment> pardonedRemoveAblePunishments;

    @Override
    public String getTitle(Player player) {
        return "Staff History";
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    public int size(Player player) {
        return 5 * 9;
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(11,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.DARK_RED.toString() + ChatColor.BOLD + RemoveAblePunishment.Type.BLACKLIST.getReadable() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BLACKLIST).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.executedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.BLACKLIST).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BLACKLIST).count())
        );

        toReturn.put(13,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.RED.toString() + ChatColor.BOLD + RemoveAblePunishment.Type.BAN.getReadable() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BAN).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.executedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.BAN).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BAN).count())
        );

        toReturn.put(15,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.GOLD.toString() + ChatColor.BOLD + RemoveAblePunishment.Type.MUTE.getReadable() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.MUTE).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.executedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.MUTE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.executedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.MUTE).count())
        );

        toReturn.put(19,new StaffHistoryButton(
                IPunishment.Type.NORMAL,
                ChatColor.YELLOW.toString() + ChatColor.BOLD + Punishment.Type.WARN.getReadable() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.executedPunishments.values().stream().filter(punishment -> punishment.getType() == Punishment.Type.WARN).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.executedPunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == Punishment.Type.WARN).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.executedPunishments.values().stream().filter(punishment -> punishment.getType() == Punishment.Type.WARN).count())
        );

        toReturn.put(25,new StaffHistoryButton(
                IPunishment.Type.NORMAL,
                ChatColor.GREEN.toString() + ChatColor.BOLD + Punishment.Type.KICK.getReadable() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.executedPunishments.values().stream().filter(punishment -> punishment.getType() == Punishment.Type.KICK).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.executedPunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == Punishment.Type.KICK).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.executedPunishments.values().stream().filter(punishment -> punishment.getType() == Punishment.Type.KICK).count())
        );

        toReturn.put(29,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Un" + RemoveAblePunishment.Type.BLACKLIST.getReadable().toLowerCase() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BLACKLIST).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.pardonedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.BLACKLIST).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BLACKLIST).count())
        );

        toReturn.put(31,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.RED.toString() + ChatColor.BOLD + "Un" + RemoveAblePunishment.Type.BAN.getReadable().toLowerCase() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BAN).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.pardonedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.BAN).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.BAN).count())
        );

        toReturn.put(33,new StaffHistoryButton(
                IPunishment.Type.REMOVE_ABLE,
                ChatColor.GOLD.toString() + ChatColor.BOLD + "Un" + RemoveAblePunishment.Type.MUTE.getReadable().toLowerCase() + "s " +
                        ChatColor.GRAY + "(" +
                        ChatColor.WHITE + (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.MUTE).count() +
                        ChatColor.GRAY + ")",
                new HashMap(this.pardonedRemoveAblePunishments.entrySet().stream().filter(entry -> entry.getValue().getType() == RemoveAblePunishment.Type.MUTE).collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()))),
                (int) this.pardonedRemoveAblePunishments.values().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == RemoveAblePunishment.Type.MUTE).count())
        );


        for (Integer redGlassPosition : RED_GLASS_POSITIONS) {
            toReturn.put(redGlassPosition,RED_GLASS);
        }

        return toReturn;
    }

    @AllArgsConstructor
    class StaffHistoryButton extends Button {

        @Getter private IPunishment.Type type;
        @Getter private String displayName;
        @Getter private HashMap<UUID, IPunishment> map;
        @Getter private int amount;

        @Override
        public String getName(Player player) {
            return this.displayName;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public int getAmount(Player player) {
            return this.amount;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.PAPER;
        }

        @Override
        public void clicked(Player player,int slot,ClickType clickType) {

            new StaffHistoryTypeMenu(this.displayName,this.map,profile,executedPunishments,executedRemoveAblePunishments,pardonedRemoveAblePunishments).openMenu(player);
        }
    }
}
