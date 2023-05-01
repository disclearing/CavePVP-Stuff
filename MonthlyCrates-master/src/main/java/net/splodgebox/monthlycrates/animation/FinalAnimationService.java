package net.splodgebox.monthlycrates.animation;

import net.splodgebox.monthlycrates.Core;
import net.splodgebox.monthlycrates.events.CrateListener;
import net.splodgebox.monthlycrates.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalAnimationService extends BukkitRunnable {
    public static HashMap<String, String> finalAnimation;
    public static HashMap<Player, ArrayList<ItemStack>> takenItems;

    public void run() {
        for (int i = 0; i < 2; ++i) {
            for (final String key : CrateListener.animCount.keySet()) {
                CrateListener.animationCounter.put(key, CrateListener.animCount.get(key));
            }
            for (final String key : CrateListener.animationCounter.keySet()) {
                final Integer slot = CrateListener.animationCounter.get(key);
                final String[] split = key.split(":");
                final String playerName = split[0];
                final int specialNumber = Integer.parseInt(split[1]);
                final Player player = Bukkit.getPlayer(playerName);
                final Inventory inv = player.getOpenInventory().getTopInventory();
                final String path = "crates." + CrateListener.opening.get(player.getName()) + ".";
                final String guiPath = path + "gui.animation";

                String rewardPath = path + "normal-rewards";
                rewardPath = rewardPath + "." + generateReward(rewardPath) + ".";
                ItemStack itemstack = item(player, rewardPath, true);
                changePanes(guiPath, player, true);
                inv.setItem(slot, itemstack);
                CrateListener.animCount.remove(key);
                CrateListener.animCount.put(playerName + ":" + (specialNumber - 1), slot);
                if (specialNumber - 1 <= 0) {
                    if (Core.getInstance().getConfig().getBoolean("once-per-item")) {
                        if (!FinalAnimationService.takenItems.containsKey(player)) {
                            FinalAnimationService.takenItems.put(player, new ArrayList<>());
                        }
                        for (int o = 0; o < 1000; ++o) {
                            rewardPath = path + "normal-rewards";
                            rewardPath = rewardPath + "." + generateReward(rewardPath) + ".";
                            itemstack = item(player, rewardPath, false);
                            if (!FinalAnimationService.takenItems.get(player).contains(itemstack)) {
                                break;
                            }
                        }
                        FinalAnimationService.takenItems.get(player).add(itemstack);
                        inv.setItem(slot, itemstack);
                    }

                    if (!CrateListener.rewards.containsKey(player.getUniqueId())) {
                        CrateListener.rewards.put(player.getUniqueId(), new ArrayList<>());
                    }
                    final List<String> lists = CrateListener.rewards.get(player.getUniqueId());
                    lists.add(Core.getInstance().getConfig().getString(path + "Name"));
                    CrateListener.rewards.put(player.getUniqueId(), lists);

                    lists.add(Core.getInstance().getConfig().getString(rewardPath + "Name"));

                    if (Core.getInstance().getConfig().getBoolean(rewardPath + "GiveItem")) {
                        player.getInventory().addItem(itemstack);
                    }
                    for (final String command : Core.getInstance().getConfig().getStringList(rewardPath + "Commands")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName));
                    }
                    CrateListener.animCount.remove(playerName + ":" + (specialNumber - 1));
                }
                if (this.animationFinished(inv, player) && !this.animationStillRunning(player)) {
                    FinalAnimationService.finalAnimation.put(player.getName(), "0:8");
                    changePanes(guiPath, player, false);
                    if (!FinalAnimationService.takenItems.containsKey(player)) {
                        continue;
                    }
                    FinalAnimationService.takenItems.remove(player);
                } else {
                    if (this.animationFinished(inv, player) || this.animationStillRunning(player)) {
                        continue;
                    }
                    changePanes(guiPath, player, false);
                }
            }
            CrateListener.animationCounter.clear();
        }
    }

    public static ItemStack item(Player player, final String path, boolean loop) {
        ItemStack itemstack;

        if (Material.valueOf(Core.getInstance().getConfig().getString(path + "Material")).equals(Material.SKULL_ITEM)) {
            itemstack = Util.createItemStackSkull(Core.getInstance().getConfig().getString(path + "SkullOwner"), Core.getInstance().getConfig().getInt(path + "Amount"), Core.getInstance().getConfig().getString(path + "Name"), Core.getInstance().getConfig().getStringList(path + "Lores"));
        } else {
            itemstack = Util.createItemStack(Material.valueOf(Core.getInstance().getConfig().getString(path + "Material")), Core.getInstance().getConfig().getInt(path + "Amount"), Core.getInstance().getConfig().getString(path + "Name"), Core.getInstance().getConfig().getBoolean(path + "Glow"), Core.getInstance().getConfig().getInt(path + "ItemData"), Core.getInstance().getConfig().getStringList(path + "Lores"));
            for (final String ench : Core.getInstance().getConfig().getStringList(path + "Enchantments")) {
                final Enchantment enchant = Enchantment.getByName(ench.split(":")[0]);
                final int level = Integer.parseInt(ench.split(":")[1]);
                itemstack.addUnsafeEnchantment(enchant, level);
            }
            if (itemstack.getType().getMaxDurability() == 0) {
                itemstack.setDurability((short) 0);
            }
        }
        return itemstack;
    }

    public static Integer generateReward(final String path) {
        double sumNumber = 0;

        final Map<Integer, Double> chanceSplit = new HashMap<>();

        for (final String item : Core.getInstance().getConfig().getConfigurationSection(path).getKeys(false)) {
            double chance = Core.getInstance().getConfig().getDouble(path + "." + item + ".Chance");

//            System.out.println(item + " <-- item");

            sumNumber += chance;
            chanceSplit.put(Integer.parseInt(item.replace("'", "")), chance);
        }

        double random = Math.random() * sumNumber;

        int choice = 0;

        for (Map.Entry<Integer, Double> entry : chanceSplit.entrySet()) {
            double chance = entry.getValue();

            choice = entry.getKey();
            random -= chance;
            if (random < 0) {
                break;
            }
        }

        return choice;
    }

    public static void changePanes(final String apath, final Player player, final boolean changeAll) {
        final Inventory inv = player.getOpenInventory().getTopInventory();
        int n = 0;
        for (final ItemStack animationItem : inv.getContents()) {
            if (animationItem.hasItemMeta() && animationItem.getItemMeta().hasDisplayName() && animationItem.getItemMeta().getDisplayName().equals(" ")) {
                if (changeAll) {
                    inv.setItem(n, Util.makeGUIPane(Material.STAINED_GLASS_PANE, dyeColor(apath), 1, " ", Core.getInstance().getConfig().getBoolean(apath + ".Glow"), null));
                } else {
                    inv.setItem(n, Util.makeGUIPane(Material.STAINED_GLASS_PANE, DyeColor.valueOf(Core.getInstance().getConfig().getString(apath + ".animation-off-color")), 1, " ", Core.getInstance().getConfig().getBoolean(apath + ".Glow"), null));
                }
            }
            ++n;
        }
    }

    public boolean animationStillRunning(final Player player) {
        for (final String string : CrateListener.animCount.keySet()) {
            if (string.split(":")[0].equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean animationFinished(final Inventory inv, final Player player) {
        for (final ItemStack item : inv.getContents()) {
            final String path = "crates." + CrateListener.opening.get(player.getName()) + ".gui.not-redeemed.Name";
            if (Util.c(Core.getInstance().getConfig().getString(path)).equals(item.getItemMeta().getDisplayName())) {
                return false;
            }
        }
        return true;
    }

    public static DyeColor dyeColor(final String apath) {
        final List<String> colors = Core.getInstance().getConfig().getStringList(apath + ".animation-on-colors");
        final int size = colors.size();
        final int element = Util.randInt(0, size - 1);
        return DyeColor.valueOf(colors.get(element));
    }

    static {
        FinalAnimationService.finalAnimation = new HashMap<>();
        FinalAnimationService.takenItems = new HashMap<>();
    }
}
