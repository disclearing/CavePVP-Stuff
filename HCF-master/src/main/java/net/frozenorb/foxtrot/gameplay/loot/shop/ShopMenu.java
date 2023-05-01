package net.frozenorb.foxtrot.gameplay.loot.shop;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.SellAllListener;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.NumberFormat;
import java.util.*;

@AllArgsConstructor
public class ShopMenu extends Menu {
    private boolean buyShop;

    @Override
    public String getTitle(Player player) {
        return buyShop ? "Buy Shop" : "Sell Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        if (buyShop) {

            for (Map.Entry<ItemStack, Double> entry : Foxtrot.getInstance().getShopHandler().getSpecificBuyShop().entrySet()) {
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
                        return ItemBuilder.copyOf(entry.getKey().clone())
                                .addToLore("",
                                        "&4&l┃ &fCost: &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(entry.getValue()),
                                        "",
                                        ChatColor.GREEN + "Click to purchase this item."
                                ).build();
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        double balance = Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId());

                        double cost = entry.getValue();

                        if (cost > balance) {
                            player.sendMessage(ChatColor.RED + "Insufficient funds!");
                            return;
                        }

                        Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), balance - cost);
                        player.getInventory().addItem(entry.getKey().clone());
                    }
                });
            }

            return toReturn;
        }

        toReturn.put(8, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&4&lSell All");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to sell all valuables in your inventory!");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                final List<ItemStack> soldItems = new ArrayList<>();

                int itemsSold = 0;
                double amountPaid = 0;

                for (ItemStack content : player.getInventory().getContents()) {
                    if (content == null || content.getType() == Material.AIR || content.getAmount() <= 0) {
                        continue;
                    }

                    if (content.getItemMeta() != null && content.getItemMeta().hasLore()) {
                        continue;
                    }

                    if (!SellAllListener.sellableItems.containsKey(content.getType())) {
                        continue;
                    }

                    final double cost = SellAllListener.sellableItems.get(content.getType());

                    soldItems.add(content);

                    itemsSold += content.getAmount();
                    amountPaid += content.getAmount()*cost;
                }

                if (soldItems.size() == 0) {
                    player.sendMessage(ChatColor.RED + "You do not have any valuables to sell on you!");
                    return;
                }

                soldItems.forEach(it -> player.getInventory().remove(it));

                Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), Math.floor(amountPaid));

                player.sendMessage(ChatColor.translate("&6You sold &f" + itemsSold + " blocks &6for &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid))));
            }
        });

        for (Map.Entry<Material, Double> entry : Foxtrot.getInstance().getShopHandler().getSellShop().entrySet()) {
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
                    return ItemBuilder.of(entry.getKey()).addToLore(
                            "",
                            "&4&l┃ &fCost: &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(entry.getValue()),
                            "",
                            ChatColor.GREEN + "Click to sell " + ItemUtils.getName(new ItemStack(entry.getKey(), 1)) + " for $" + NumberFormat.getNumberInstance(Locale.US).format(Math.round(entry.getValue())),
                            ChatColor.GREEN + "Shift Click to sell " + ItemUtils.getName(new ItemStack(entry.getKey(), 16)) + " for $" + NumberFormat.getNumberInstance(Locale.US).format(Math.round(entry.getValue()*16)),
                            ChatColor.GREEN + "Middle Click to sell all"
                    ).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    final List<ItemStack> soldItems = new ArrayList<>();

                    int itemCount = 0;

                    for (ItemStack content : player.getInventory().getContents()) {
                        if (content == null || content.getType() == Material.AIR || content.getAmount() <= 0) {
                            continue;
                        }

                        if (content.getItemMeta() != null && content.getItemMeta().hasLore()) {
                            continue;
                        }

                        if (!content.getType().equals(entry.getKey())) {
                            continue;
                        }

                        itemCount += content.getAmount();
                    }

                    if (itemCount == 0) {
                        player.sendMessage(ChatColor.RED + "You don't have enough items!");
                        return;
                    }

                    if (clickType.isShiftClick()) {
                        double total = entry.getValue()*16;

                        if (itemCount < 16) {
                            player.sendMessage(ChatColor.RED + "You don't have enough items!");
                            return;
                        }

                        Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), total);
                        removeItem(player, new ItemStack(entry.getKey(), 16), 16);

                        if (Foxtrot.getInstance().getBattlePassHandler() != null) {
                            Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                                progress.setValuablesSold(progress.getValuablesSold() + ((int) Math.round(total)));
                                progress.requiresSave();

                                Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                            });
                        }
                        return;
                    }

                    if (clickType.equals(ClickType.MIDDLE)) {
                        int itemsSold = 0;
                        double amountPaid = 0;

                        for (ItemStack content : player.getInventory().getContents()) {
                            if (content == null || content.getType() == Material.AIR || content.getAmount() <= 0) {
                                continue;
                            }

                            if (content.getItemMeta() != null && content.getItemMeta().hasLore()) {
                                continue;
                            }

                            if (!entry.getKey().equals(content.getType())) {
                                continue;
                            }

                            final double cost = entry.getValue();

                            soldItems.add(content);

                            itemsSold += content.getAmount();
                            amountPaid += content.getAmount()*cost;
                        }

                        if (soldItems.size() == 0) {
                            player.sendMessage(ChatColor.RED + "You do not have any valuables to sell on you!");
                            return;
                        }

                        soldItems.forEach(it -> player.getInventory().remove(it));

                        Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), Math.floor(amountPaid));

                        if (Foxtrot.getInstance().getBattlePassHandler() != null) {
                            double finalAmountPaid = amountPaid;
                            Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                                progress.setValuablesSold(progress.getValuablesSold() + ((int) Math.round(finalAmountPaid)));
                                progress.requiresSave();

                                Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                            });
                        }

                        player.sendMessage(ChatColor.translate("&6You sold &f" + itemsSold + " blocks &6for &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid))));
                        return;
                    }

                    double total = entry.getValue();

                    Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), total);
                    removeItem(player, new ItemStack(entry.getKey(), 16), 1);
                }
            });

        }

        return toReturn;
    }

    public int countItems(Player player, Material material, int damageValue) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int amount = 0;

        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = material.getMaxDurability() == (short) 0;

                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
                    amount += item.getAmount();
                }
            }
        }

        return (amount);
    }

    public void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

        for (int a = 0; a < amount; a++) {
            for (ItemStack i : p.getInventory()) {
                if (i != null) {
                    if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                        if (i.getAmount() == 1) {
                            p.getInventory().clear(p.getInventory().first(i));
                        } else {
                            i.setAmount(i.getAmount() - 1);
                        }
                        break;
                    }
                }
            }
        }

    }

    @Override
    public int getSlot(int x, int y) {
        if (buyShop) {
            return 36;
        }

        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }
}
