package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.menu.PartnerItemMainMenu;
import net.frozenorb.foxtrot.gameplay.ability.menu.PartnerItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PartnerItemsCommand {
    @Command(names = {"partneritems", "abilityitems"}, permission = "")
    public static void execute(Player player) {

        new Menu() {

            @Override
            public String getTitle(Player player) {
                return "Partner Items";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();
                for (Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {

                    if (value.getCategory() == Category.PORTABLE_BARD || !Foxtrot.getInstance().getMapHandler().isKitMap() && value.getCategory() == Category.KIT_MAP) {
                        continue;
                    }
                    toReturn.put(toReturn.size(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return null;
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            return null;
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return null;
                        }

                        @Override
                        public ItemStack getButtonItem(Player player) {
                            return value.hassanStack.clone();
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {

                            if (player.isOp()) {
                                player.getInventory().addItem(value.hassanStack.clone());
                            }

                        }
                    });
                }
                return toReturn;
            }
        }.openMenu(player);
    }

    @Command(names = {"ability statistics", "ability stats"}, permission = "op")
    public static void statistics(Player player) {
        new Menu() {
            @Override
            public String getTitle(Player player) {
                return "Ability Statistics";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                final List<Ability> abilities = new ArrayList<>();

                for (Map.Entry<String, Ability> stringAbilityEntry : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().entrySet()) {
                    if (stringAbilityEntry.getKey().contains("Launcher") || stringAbilityEntry.getKey().contains("BanStick")) {
                        continue;
                    }

                    if (!Foxtrot.getInstance().getMapHandler().isKitMap() && stringAbilityEntry.getValue().getCategory() == Category.KIT_MAP) {
                        continue;
                    }

                    abilities.add(stringAbilityEntry.getValue());
                }

                abilities.sort(Comparator.comparingInt(it -> Foxtrot.getInstance().getMapHandler().getAbilityHandler().getUsedItems().getOrDefault(it, 0)));

                for (Ability ability : abilities) {
                    toReturn.put(toReturn.size(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return null;
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            return null;
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return null;
                        }

                        @Override
                        public ItemStack getButtonItem(Player player) {
                            final int amountUsed = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getUsedItems().getOrDefault(ability, 0);

                            final ItemStack itemStack = ability.hassanStack.clone();
                            final ItemMeta itemMeta = itemStack.getItemMeta();
                            final List<String> lore = itemMeta.getLore();
                            lore.add("");
                            lore.add(ChatColor.GOLD + "Amount Used: " + ChatColor.WHITE + amountUsed);
                            itemMeta.setLore(lore);
                            itemStack.setAmount(Math.min(amountUsed == 0 ? 1 : amountUsed, 64));
                            itemStack.setItemMeta(itemMeta);

                            return itemStack;
                        }
                    });
                }
                return toReturn;
            }

            @Override
            public boolean isAutoUpdate() {
                return true;
            }
        }.openMenu(player);
    }
}
