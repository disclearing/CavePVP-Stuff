package org.cavepvp.suge.enchant.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;
import org.cavepvp.suge.util.RomanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CEMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Custom Enchants";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7, ""));
        }

        for (Tier value : Tier.values()) {
            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return value.getDisplayName() + " Book";
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(CC.translate("&7Get a " + WordUtils.capitalizeFully(value.name()) + " &7level custom"));
                    toReturn.add(CC.translate("&7enchant book to apply to your armor!"));
                    toReturn.add("");

                    if (value == Tier.CAVE) {
                        toReturn.add(CC.translate("&cThis tier may only be obtained from a &6&lMystery Box&c!"));
                        return toReturn;
                    }

                    toReturn.add(CC.translate("&4&l┃ &fCost: &c" + value.getLevelCost() + " EXP Levels"));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to purchase a random book");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.BOOK;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isRightClick()) {
                        new CEListMenu(value).openMenu(player);
                        return;
                    }

                    if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < value.getLevelCost()) {
                        player.sendMessage(ChatColor.RED + "You can't afford a book of this type!");
                        return;
                    }

                    if (value == Tier.CAVE) {
                        player.sendMessage(CC.translate("&cYou may only obtain a &f" + value.getDisplayName() + " &cenchant from a &6&lMystery Box&c!"));
                        return;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED + "Your inventory is full!");
                        return;
                    }

                    final List<CustomEnchant> customEnchants = Suge.getInstance().getEnchantHandler().getCustomEnchants().stream().filter(it -> it.getLevel() == value).collect(Collectors.toList());
                    final CustomEnchant randomEnchant = customEnchants.get(ThreadLocalRandom.current().nextInt(0,customEnchants.size()));

                    final String romanNumber = RomanUtil.map.entrySet().stream().filter(it -> it.getValue() ==  randomEnchant.getAmplifier()).map(Map.Entry::getKey).findFirst().orElse(null);

                    player.getInventory().addItem(Suge.getInstance().getEnchantHandler().getEnchantBookHandler().getBook(randomEnchant, romanNumber).clone());
                    player.setLevel(player.getLevel()-value.getLevelCost());
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.GREEN + "You have received a custom enchant book!");
                }
            });
        }

        return toReturn;
    }
}
