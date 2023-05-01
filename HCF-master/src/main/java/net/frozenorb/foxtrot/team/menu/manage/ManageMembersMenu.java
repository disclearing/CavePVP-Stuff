package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class ManageMembersMenu extends Menu {
    private Team team;

    @Override
    public String getTitle(Player player) {
        return "Manage Members";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        int highest = (int) Math.ceil(team.getMembers().size()/9);

        for (int i = 0; i < highest*9; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        int i = 9;

        for (UUID member : team.getMembers()) {
            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(member, true);

            if (i % 9 == 0) {
                i += 2;
            }

            toReturn.put(i++, new Button() {
                @Override
                public String getName(Player player) {
                    return Neutron.getInstance().getProfileHandler().findDisplayName(member);
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fRole: &c" + getRole(member)));
                    toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &c" + (profile.getServerProfile().isOnline() ? "&aOnline" : "&cOffline")));
                    if (!profile.getServerProfile().isOnline()) {
                        toReturn.add(ChatColor.translate("&4&l┃ &fLast Online: &c" + profile.getServerProfile().getLastSeenString()));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.RED + "Click to manage this player");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) 3;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.copyOf(super.getButtonItem(player)).skull(UUIDUtils.name(member)).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new ManageMemberMenu(team, member, profile).openMenu(player);
                }
            });
        }

        return toReturn;
    }

    public String getRole(UUID member) {
        if (team.getCaptains().contains(member)) {
            return "&eCaptain";
        }

        if (team.getColeaders().contains(member)) {
            return "&6Co-Leader";
        }

        if (team.getOwner().equals(member)) {
            return "&c&lLeader";
        }

        return "&aMember";
    }
}
