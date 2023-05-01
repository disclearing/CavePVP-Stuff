package net.frozenorb.foxtrot.gameplay.ability.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.portablebard.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortableBardMenu extends Menu {
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(Material.STAINED_GLASS_PANE).data((byte)7).name("").build();
                }

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
            });
        }


        toReturn.put(11, new Button() {
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
                return ItemBuilder.of(Material.BLAZE_POWDER).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Portable Strength")
                        .setLore(Arrays.asList(ChatColor.GRAY + "Give yourself and your teammates", ChatColor.GRAY + "around you Strength 2 for 5 seconds", "", ChatColor.GREEN + "Click here to get the Portable Strength item.")).build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                final ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR || !Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName("PortableBard").isSimilar(itemInHand)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You don't have anymore portable bards to transfer!");
                    return;
                }

                if (itemInHand.getAmount() == 1) {
                    player.closeInventory();
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount()-1);
                }

                final ItemStack itemStack = PortableStrength.itemStack.clone();

                itemStack.setAmount(5);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

                player.sendMessage(ChatColor.GREEN + "You have been given Portable Strength!");
            }
        });
        toReturn.put(12, new Button() {
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
                return ItemBuilder.of(Material.GHAST_TEAR).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Portable Regeneration")
                        .setLore(Arrays.asList(ChatColor.GRAY + "Give yourself and your teammates", ChatColor.GRAY + "around you Regeneration 3 for 5 seconds", "", ChatColor.GREEN + "Click here to get the Portable Regeneration item.")).build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                final ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR || !Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName("PortableBard").isSimilar(itemInHand)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You don't have anymore portable bards to transfer!");
                    return;
                }

                if (itemInHand.getAmount() == 1) {
                    player.closeInventory();
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount()-1);
                }

                final ItemStack itemStack = PortableRegeneration.itemStack.clone();

                itemStack.setAmount(5);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

                player.sendMessage(ChatColor.GREEN + "You have been given Portable Regeneration!");
            }
        });

        toReturn.put(13, new Button() {
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
                return ItemBuilder.of(Material.FEATHER).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Portable Jump Boost")
                        .setLore(Arrays.asList(ChatColor.GRAY + "Give yourself and your teammates", ChatColor.GRAY + "around you Jump Boost 7 for 5 seconds", "", ChatColor.GREEN + "Click here to get the Portable Jump Boost item.")).build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                final ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR || !Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName("PortableBard").isSimilar(itemInHand)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You don't have anymore portable bards to transfer!");
                    return;
                }

                if (itemInHand.getAmount() == 1) {
                    player.closeInventory();
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount()-1);
                }

                final ItemStack itemStack = PortableJumpBoost.itemStack.clone();

                itemStack.setAmount(5);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

                player.sendMessage(ChatColor.GREEN + "You have been given Portable Jump Boost!");
            }
        });

        toReturn.put(14, new Button() {
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
                return ItemBuilder.of(Material.SUGAR).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Portable Speed")
                        .setLore(Arrays.asList(ChatColor.GRAY + "Give yourself and your teammates", ChatColor.GRAY + "around you Speed 3 for 5 seconds", "", ChatColor.GREEN + "Click here to get the Portable Speed item.")).build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                final ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR || !Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName("PortableBard").isSimilar(itemInHand)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You don't have anymore portable bards to transfer!");
                    return;
                }

                if (itemInHand.getAmount() == 1) {
                    player.closeInventory();
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount()-1);
                }

                final ItemStack itemStack = PortableSpeed.itemStack.clone();

                itemStack.setAmount(5);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

                player.sendMessage(ChatColor.GREEN + "You have been given a Portable Speed!");
            }
        });

        toReturn.put(15, new Button() {
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
                return ItemBuilder.of(Material.IRON_INGOT).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Portable Resistance")
                        .setLore(Arrays.asList(ChatColor.GRAY + "Give yourself and your teammates", ChatColor.GRAY + "around you Strength 2 for 5 seconds", "", ChatColor.GREEN + "Click here to get the Portable Resistance item..")).build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                final ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR ||
                        !Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName("PortableBard").isSimilar(itemInHand)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You don't have anymore portable bards to transfer!");
                    return;
                }

                if (itemInHand.getAmount() == 1) {
                    player.closeInventory();
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount()-1);
                }

                final ItemStack itemStack = PortableResistance.itemStack.clone();

                itemStack.setAmount(5);

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    player.getInventory().addItem(itemStack);
                }

                player.sendMessage(ChatColor.GREEN + "You have been given Portable Resistance!");
            }
        });

        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return "Choose an effect";
    }
}
