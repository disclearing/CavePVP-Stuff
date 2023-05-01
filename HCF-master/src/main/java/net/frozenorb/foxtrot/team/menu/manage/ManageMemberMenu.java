package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@AllArgsConstructor
public class ManageMemberMenu extends Menu {
    private Team team;
    private UUID target;
    private Profile targetProfile;

    @Override
    public String getTitle(Player player) {
        return "Manage Members";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kick Member";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click here to kick this member");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE_BLOCK;
            }
        });


        toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Promote Member";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click here to promote this member");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD_BLOCK;
            }
        });


        toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Demote Member";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click here to demote to");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE_BLOCK;
            }
        });

        return toReturn;
    }

    public String getRoleBelow(UUID member) {
        if (team.getCaptains().contains(member)) {
            return "Member";
        }

        if (team.getColeaders().contains(member)) {
            return "Co-Leader";
        }

        return "N/A";
    }
}
