package net.frozenorb.foxtrot.team.menu.roster;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class TeamManageRosterMenu extends Menu {
    private Team team;
    private UUID uuid;

    @Override
    public String getTitle(Player player) {
        return "Manage";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        if (!team.getRoster().containsKey(uuid)) {
            player.closeInventory();
            return new HashMap<>();
        }

        final Role role = team.getRoster().get(uuid);
        final Role nextRole = Arrays.stream(Role.values()).filter(it -> it.getNumber() == role.getNumber()+1).findFirst().orElse(Role.MEMBER);

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, ""));
        }

        toReturn.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Manage Role";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Manage this player's role");
                toReturn.add("");
                toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Roles");
                for (Role value : Role.values()) {
                    if (value == role) {
                        toReturn.add(ChatColor.translate("&4&l┃ &c" + value.getRoleName()));
                    } else {
                        toReturn.add(ChatColor.translate("&4&l┃ &f" + value.getRoleName()));
                    }
                }
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch their role");


                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GOLD_INGOT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                team.getRoster().put(uuid, nextRole);
            }
        });

        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Go Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new TeamRosterMenu(team).openMenu(player);
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Remove From Roster";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Remove player from Roster");
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to remove this player from the roster");


                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TNT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                team.getRoster().remove(uuid);
                player.closeInventory();
                new TeamRosterMenu(team).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }
}
