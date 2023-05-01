package net.frozenorb.foxtrot.team.upgrade;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.effects.UpgradeEffectsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class UpgradeMenu extends Menu {

    private Team team;

    @Override
    public String getTitle(Player player) {
        return "Purchase Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        for (UpgradeType value : UpgradeType.values()) {
            if (value.equals(UpgradeType.DOUBLE_GEMS) && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
                continue;
            }

            if (value.equals(UpgradeType.DOUBLE_DROPS) && Foxtrot.getInstance().getMapHandler().isKitMap()) {
                continue;
            }

            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> lore = new ArrayList<>(value.getDescription());
                    lore.add("");

                    if (value.getCost() != -1) {
                        lore.add(ChatColor.translate("&4&l┃ &fCost: &c" + value.getCost() + " gems"));
                        lore.add("");
                    }

                    if (value == UpgradeType.POTION_EFFECTS) {
                        lore.add(ChatColor.GREEN + "Click to view all purchasable potion effects");
                        return lore;
                    }


                    if (team.getPurchasedUpgrades().contains(value)) {
                        lore.add(ChatColor.RED + "Purchased");
                        return lore;
                    }

                    lore.add(ChatColor.GREEN + "Click to purchase this upgrade");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (value == UpgradeType.POTION_EFFECTS) {
                        new UpgradeEffectsMenu(team).openMenu(player);
                        return;
                    }

                    if (value.name().contains("REDUCED_DTR_REGEN") && Foxtrot.getInstance().getMapHandler().isKitMap()) {
                        player.sendMessage(ChatColor.RED + "You may not purchase " + value.displayName + " on Kitmap.");
                        return;
                    }

                    if (team.getPurchasedUpgrades().contains(value)) {
                        player.sendMessage(ChatColor.RED + "You have already purchased this upgrade!");
                        return;
                    }

                    if (team.recalculateGems() < value.getCost()) {
                        player.sendMessage(ChatColor.RED + "Insufficient Gems balance");
                        return;
                    }

                    team.setRemovedGems(team.getRemovedGems()+value.getCost());
                    team.flagForSave();

                    if (value.equals(UpgradeType.DOUBLE_GEMS)) {
                        for (UUID member : team.getMembers()) {
                            Foxtrot.getInstance().getGemBoosterMap().giveGemBooster(member, TimeUnit.HOURS.toMinutes(6));
                        }
                    } else {
                        team.getPurchasedUpgrades().add(value);
                    }

                    team.sendMessage(ChatColor.translate("&6Your team has purchased &f" + value.getDisplayName() + " &6for &f" + value.getCost() + " gems&6!"));
                    player.closeInventory();
                }
            });
        }

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Faction Upgrades";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.translate("&2❙ &fGems: &a" + team.recalculateGems()));
                toReturn.add("");
                toReturn.add(ChatColor.translate("&aWhat are Faction Upgrades?"));
                toReturn.add(ChatColor.translate("&2❙ &fCosts faction gems to get."));
                toReturn.add(ChatColor.translate("&2❙ &fUnique upgrades for your entire faction."));
                toReturn.add("");
                toReturn.add(ChatColor.translate("&aHow to get Faction Gems?"));
                toReturn.add(ChatColor.translate("&2❙ &f+1 Gem per Kill"));
                toReturn.add(ChatColor.translate("&2❙ &f-1 Gem per Death"));
                toReturn.add(ChatColor.translate("&2❙ &f+10 Gems per Mini-KOTH Capture"));
                toReturn.add(ChatColor.translate("&2❙ &f+25 Gems per KOTH Capture"));
                toReturn.add(ChatColor.translate("&2❙ &f+100 Gems per Citadel Capture"));
                toReturn.add("");
                toReturn.add(ChatColor.translate("&7Hover over each armor classes to view its use"));

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }
        });

        return toReturn;
    }
}
