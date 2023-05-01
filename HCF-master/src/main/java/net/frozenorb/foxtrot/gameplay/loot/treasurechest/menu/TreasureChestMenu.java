package net.frozenorb.foxtrot.gameplay.loot.treasurechest.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener.TreasureChestListener;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureChestMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Treasure Chests";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (TreasureChest treasureChest : Foxtrot.getInstance().getTreasureChestHandler().getTreasureChests()) {
            int chests = treasureChest.getCache().getOrDefault(player.getUniqueId(), 0);

            toReturn.put(treasureChest.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(treasureChest.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fAmount: &c" + chests));
                    toReturn.add("");

                    toReturn.add(ChatColor.GRAY + "Right Click to view all possible loot");
                    if (chests == 0) {
                        toReturn.add(ChatColor.RED + "Purchase a " + ChatColor.translate(treasureChest.getDisplayName()) + ChatColor.RED + " at store.cavepvp.org!");
                    } else {
                        toReturn.add(ChatColor.GREEN + "Left Click to open a " + ChatColor.translate(treasureChest.getDisplayName()) + ChatColor.GREEN + "!");
                    }

                    return toReturn;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType.isShiftClick()) {
                        player.closeInventory();
                        new Menu() {
                            @Override
                            public String getTitle(Player player) {
                                return "Omega Loot";
                            }

                            @Override
                            public Map<Integer, Button> getButtons(Player player) {
                                final Map<Integer, Button> toReturn = new HashMap<>();

                                new ArrayList<>(treasureChest.getRewards()).stream().filter(it -> it.getItemStack() != null).forEach(it -> {

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
                                            return it.getItemStack().clone();
                                        }
                                    });
                                });

                                return toReturn;
                            }
                        }.openMenu(player);
                        return;
                    }

                    if (!treasureChest.getCache().containsKey(player.getUniqueId()) || treasureChest.getCache().get(player.getUniqueId()) <= 0) {
                        player.sendMessage(ChatColor.RED + "You don't have any Treasure Chests! You can purchase one at https://store.cavepvp.org");
                        return;
                    }

                    final TreasureChestListener treasureChestListener = Foxtrot.getInstance().getTreasureChestHandler().getTreasureChestListener();

                    if (treasureChestListener.getCurrentPlayer() != null) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "A player is already opening one!");
                        return;
                    }

                    final Location centralLocation = treasureChest.getCentralLocation();

                    if (centralLocation == null) {
                        return;
                    }

                    final List<Hologram> holograms = new ArrayList<>(Proton.getInstance().getHologramHandler().getCache().values());
                    holograms.stream().filter(it -> it.getLines().contains(ChatColor.translate("&f&ki&4&l TREASURE CHESTS &f&ki"))).forEach(Hologram::delete);

                    treasureChest.getCache().put(player.getUniqueId(), treasureChest.getCache().get(player.getUniqueId())-1);

                    centralLocation.getBlock().setType(Material.AIR);

                    treasureChestListener.setEnding(false);
                    treasureChestListener.setChestsOpened(0);
                    treasureChestListener.getRewardsWon().clear();
                    treasureChestListener.getOpenedChests().clear();
                    treasureChestListener.setCurrentlyOpening(treasureChest);
                    treasureChestListener.setCurrentPlayer(player.getUniqueId());

                    String color = "&c";

                    if (treasureChest.getId().equalsIgnoreCase("Illuminated")) {
                        color = "&b";
                    }

                    for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        onlinePlayer.sendMessage(ChatColor.translate(""));
                        onlinePlayer.sendMessage(ChatColor.translate(treasureChest.getDisplayName()));
                        onlinePlayer.sendMessage(ChatColor.translate("&f" + player.getName() + " &7is opening a &4&l" + treasureChest.getDisplayName() + "&7!"));
                        onlinePlayer.sendMessage(ChatColor.translate(color + "Purchase " + ChatColor.stripColor(ChatColor.translate(treasureChest.getDisplayName())) + " on our store at &fstore.cavepvp.org" + color + "!"));
                        onlinePlayer.sendMessage(ChatColor.translate(""));
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            final TreasureChestListener listener = Foxtrot.getInstance().getTreasureChestHandler().getTreasureChestListener();

                            if (listener.getCurrentPlayer() == null || listener.isEnding()) {
                                this.cancel();
                                return;
                            }

                            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

                                if (onlinePlayer.getUniqueId().toString().equalsIgnoreCase(listener.getCurrentPlayer().toString())) {
                                    continue;
                                }

                                if (!DTRBitmask.SAFE_ZONE.appliesAt(onlinePlayer.getLocation())) {
                                    continue;
                                }

                                if (!onlinePlayer.getWorld().getName().equalsIgnoreCase("Spawn")) {
                                    continue;
                                }

                                if (onlinePlayer.getLocation().distance(centralLocation.clone()) <= 4) {
                                    onlinePlayer.setVelocity(onlinePlayer.getLocation().getDirection().multiply(-1.75));
                                }
                            }
                        }
                    }.runTaskTimer(Foxtrot.getInstance(), 20, 20);

                    player.teleport(centralLocation.clone());

                    Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        for (Location chest : treasureChest.getChests()) {
                            chest.getBlock().setType(Material.CHEST);

                            chest.getWorld().playEffect(chest, Effect.EXPLOSION_HUGE, 1, 1);
                            chest.getWorld().playSound(chest, Sound.ANVIL_LAND, 1, 1);
                        }
                    }, 20*4);
                }

                @Override
                public Material getMaterial(Player player) {
                    return treasureChest.getMaterial();
                }
            });
        }

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }
}
