package cc.fyre.neutron.profile.menu.staffhistory.staffhistory;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class StaffHistoryTypeMenu extends PaginatedMenu {

    @Getter
    private String displayName;
    @Getter
    private Map<UUID, IPunishment> punishments;

    @Getter
    private Profile profile;

    @Getter
    private Map<UUID, Punishment> executedPunishments;
    @Getter
    private Map<UUID, RemoveAblePunishment> executedRemoveAblePunishments;
    @Getter
    private Map<UUID, RemoveAblePunishment> pardonedRemoveAblePunishments;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Staff History";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {

        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Map.Entry<UUID, IPunishment> entry : this.punishments.entrySet()) {

            if (entry.getValue().getIType() == IPunishment.Type.NORMAL) {

                toReturn.put(toReturn.size(), new Button() {

                    @Override
                    public String getName(Player player) {
                        return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(entry.getValue().getExecutedAt()));
                    }

                    @Override
                    public List<String> getDescription(Player player) {

                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        toReturn.add(ChatColor.YELLOW + "For: " + ChatColor.RED + Proton.getInstance().getUuidCache().name(entry.getKey()));
                        toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (entry.getValue().getExecutedSilent() ? "Yes" : "No"));
                        toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + entry.getValue().getExecutedReason());
                        toReturn.add(ChatColor.YELLOW + "Server: " + ChatColor.RED + entry.getValue().getServer());
                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                        return toReturn;
                    }

                    @Override
                    public byte getDamageValue(Player player) {
                        return 14;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.WOOL;
                    }

                });
            } else if (entry.getValue().getIType() == IPunishment.Type.REMOVE_ABLE) {

                toReturn.put(toReturn.size(), new Button() {

                    @Override
                    public String getName(Player player) {
                        return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(entry.getValue().getExecutedAt()));
                    }

                    @Override
                    public List<String> getDescription(Player player) {

                        final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment) entry.getValue();

                        final List<String> toReturn = new ArrayList<>();

                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        toReturn.add(ChatColor.YELLOW + "For: " + ChatColor.RED + Proton.getInstance().getUuidCache().name(entry.getKey()));
                        toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (removeAblePunishment.getExecutedSilent() ? "Yes" : "No"));
                        toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + removeAblePunishment.getExecutedReason());
                        toReturn.add(ChatColor.YELLOW + "Time: " + ChatColor.RED + (removeAblePunishment.isPermanent() ? "Permanent" :
                                FormatUtil.millisToRoundedTime(removeAblePunishment.getDuration(), true)));
                        toReturn.add(ChatColor.YELLOW + "Server: " + ChatColor.RED + removeAblePunishment.getServer());
                        if (removeAblePunishment.isActive() && !removeAblePunishment.isPermanent()) {
                            toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                            toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + removeAblePunishment.getRemainingString());
                        }

                        if (removeAblePunishment.isPardoned()) {
                            toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                            toReturn.add(ChatColor.YELLOW + "Pardoned By: " + ChatColor.RED + Proton.getInstance().getUuidCache().name(removeAblePunishment.getPardoner()));
                            toReturn.add(ChatColor.YELLOW + "Pardoned At: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(removeAblePunishment.getPardonedAt())));
                            toReturn.add(ChatColor.YELLOW + "Pardoned Silent: " + ChatColor.RED + (removeAblePunishment.getPardonedSilent() ? "Yes" : "No"));
                            toReturn.add(ChatColor.YELLOW + "Pardoned Reason: " + ChatColor.RED + removeAblePunishment.getPardonedReason());
                        }

                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.WOOL;
                    }

                    @Override
                    public byte getDamageValue(Player player) {

                        final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment) entry.getValue();

                        if (removeAblePunishment.isActive()) {
                            return removeAblePunishment.isPermanent() ? DyeColor.GREEN.getWoolData() : DyeColor.YELLOW.getWoolData();
                        }

                        return DyeColor.RED.getWoolData();
                    }

                });

            }

        }

        return toReturn;
    }

}