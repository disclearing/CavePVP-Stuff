package cc.fyre.neutron.profile.menu.history.type;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.Punishments;
import cc.fyre.neutron.profile.attributes.punishment.comparator.PunishmentDateComparator;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.menu.history.HistoryMenu;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HistoryTypeMenu extends PaginatedMenu {
    @Getter
    String displayName;
    @Getter
    Profile profile;
    @Getter
    List<IPunishment> punishments;
    @Getter
    Punishments type;

    public HistoryTypeMenu(String displayName, Profile profile, List<IPunishment> punishments, Punishments type) {
        this.displayName = displayName;
        this.profile = profile;
        this.punishments = punishments;
        this.type = type;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return displayName;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> toReturn = Maps.newHashMap();
        this.profile.getPunishments().stream().sorted(new PunishmentDateComparator().reversed()).filter(punishment ->
                punishment.getPunishType() == type).forEach(punishment -> {


            if (type.getType().equals(IPunishment.Type.REMOVE_ABLE)) {
                toReturn.put(toReturn.size(), handleRemoveAblePunishments((RemoveAblePunishment) punishment));
            } else {
                toReturn.put(toReturn.size(), handleNonRemoveAblePunishments((Punishment) punishment));
            }
        });
        return toReturn;
    }


    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {

        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(4, new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED + "<-- Back";
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
                new HistoryMenu(profile).openMenu(player);
            }
        });

        return toReturn;
    }

    private Button handleRemoveAblePunishments(RemoveAblePunishment punishment) {
        Button button = new Button() {
            @Override
            public String getName(Player var1) {
                return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(punishment.getExecutedAt()));
            }

            @Override
            public List<String> getDescription(Player var1) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "By: " + ChatColor.RED + UUIDUtils.name(punishment.getExecutor()));
                toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (punishment.getExecutedSilent() ? "Yes" : "No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + punishment.getExecutedReason());
                toReturn.add(ChatColor.YELLOW + "Time: " + ChatColor.RED + (punishment.isPermanent() ? "Permanent" :
                        FormatUtil.millisToRoundedTime(punishment.getDuration(), true)));
                toReturn.add(ChatColor.YELLOW + "Server: " + ChatColor.RED + punishment.getServer());

                if (punishment.isActive() && !punishment.isPermanent()) {
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + punishment.getRemainingString());
                }

                if (punishment.isPardoned()) {
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Pardoned By: " + ChatColor.RED + UUIDUtils.name(punishment.getPardoner()));
                    toReturn.add(ChatColor.YELLOW + "Pardoned At: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(punishment.getPardonedAt())));
                    toReturn.add(ChatColor.YELLOW + "Pardoned Silent: " + ChatColor.RED + (punishment.getPardonedSilent() ? "Yes" : "No"));
                    toReturn.add(ChatColor.YELLOW + "Pardoned Reason: " + ChatColor.RED + punishment.getPardonedReason());
                }

                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player var1) {
                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {
                if (punishment.isActive()) {
                    return DyeColor.LIME.getWoolData();
                }
                if (punishment.isPardoned()) {
                    return DyeColor.RED.getWoolData();
                }
                return DyeColor.ORANGE.getWoolData();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
            }
        };

        return button;
    }

    private Button handleNonRemoveAblePunishments(Punishment punishment) {
        Button button = new Button() {
            @Override
            public String getName(Player var1) {
                return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(punishment.getExecutedAt()));
            }

            @Override
            public List<String> getDescription(Player var1) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "By: " + ChatColor.RED + Proton.getInstance().getUuidCache().name(punishment.getExecutor()));
                toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (punishment.getExecutedSilent() ? "Yes" : "No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + punishment.getExecutedReason());
                toReturn.add(ChatColor.YELLOW + "Server: " + ChatColor.RED + punishment.getServer());
                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player var1) {
                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {
                return DyeColor.LIME.getWoolData();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
            }
        };

        return button;
    }
}
