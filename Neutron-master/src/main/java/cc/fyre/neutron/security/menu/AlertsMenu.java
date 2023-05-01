package cc.fyre.neutron.security.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.security.SecurityAlert;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class AlertsMenu extends PaginatedMenu {
    private UUID target;
    private UUID victim;
    private String server;
    private AlertType alertType;
    private boolean urgent = false;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Security";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(3, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Filter Alert Types";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&6&lCurrent Filters"));
                toReturn.add(ChatColor.translate("&eAlert Type: &f" + (alertType == null ? "None" : alertType.getDisplayName())));
                toReturn.add(ChatColor.translate("&eTarget: &f" + (target == null ? "N/A" : UUIDUtils.name(target))));
                toReturn.add(ChatColor.translate("&eVictim: &f" + (victim == null ? "N/A" : UUIDUtils.name(victim))));
                toReturn.add(ChatColor.translate("&eServer: &f" + (server == null ? "Global" : server)));
                toReturn.add(ChatColor.translate("&eUrgent: &f" + (urgent ? "&aYes" : "&cNo")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch between filters");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return alertType != null ? alertType.getMaterial() : Material.TNT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new AlertFiltersMenu(target, victim, server, urgent).openMenu(player);
            }
        });

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Filter Servers";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&6&lCurrent Filters"));
                toReturn.add(ChatColor.translate("&eAlert Type: &f" + (alertType == null ? "None" : alertType.getDisplayName())));
                toReturn.add(ChatColor.translate("&eTarget: &f" + (target == null ? "N/A" : UUIDUtils.name(target))));
                toReturn.add(ChatColor.translate("&eVictim: &f" + (victim == null ? "N/A" : UUIDUtils.name(victim))));
                toReturn.add(ChatColor.translate("&eServer: &f" + (server == null ? "Global" : server)));
                toReturn.add(ChatColor.translate("&eUrgent: &f" + (urgent ? "&aYes" : "&cNo")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to switch between servers");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ServersFilterMenu(target, victim, urgent, alertType).openMenu(player);
            }
        });

        toReturn.put(5, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Toggle Urgent";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&6&lCurrent Filters"));
                toReturn.add(ChatColor.translate("&eAlert Type: &f" + (alertType == null ? "None" : alertType.getDisplayName())));
                toReturn.add(ChatColor.translate("&eTarget: &f" + (target == null ? "N/A" : UUIDUtils.name(target))));
                toReturn.add(ChatColor.translate("&eVictim: &f" + (victim == null ? "N/A" : UUIDUtils.name(victim))));
                toReturn.add(ChatColor.translate("&eServer: &f" + (server == null ? "Global" : server)));
                toReturn.add(ChatColor.translate("&eUrgent: &f" + (urgent ? "&aYes" : "&cNo")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to toggle urgent checks only");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return urgent ? Material.EMERALD : Material.REDSTONE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new AlertsMenu(target, victim, server, alertType, !urgent).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (SecurityAlert alert : Neutron.getInstance().getSecurityHandler().findAlerts(target, victim, alertType, urgent)) {
            if (server != null && !server.equalsIgnoreCase("") && !alert.getServer().equalsIgnoreCase(server)) {
                continue;
            }

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(alert.getTimeAt()));
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    if (alert.getVictim() != null) {
                        toReturn.add(ChatColor.translate("&eVictim: &f" + UUIDUtils.name(alert.getVictim())));
                    }

                    if (alert.getTarget() != null) {
                        toReturn.add(ChatColor.translate("&eTarget: &f" + UUIDUtils.name(alert.getTarget())));
                    }

                    toReturn.add(ChatColor.translate("&eAlert Type: &f" + alert.getAlertType().getDisplayName()));
                    toReturn.add(ChatColor.translate("&eUrgent: &f" + (alert.isUrgent() ? "&aYes" : "&cNo")));
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    for (String s : alert.getDescription()) {
                        toReturn.add(ChatColor.translate("&7- &f" + s));
                    }
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return alert.getAlertType().getMaterial();
                }
            });
        }

        return toReturn;
    }
}
