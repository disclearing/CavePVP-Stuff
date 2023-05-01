package net.frozenorb.foxtrot.gameplay.loot.treasurechest.command;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreasureChestCommand {

    @Command(names = {"chest thirtydayrate"}, permission = "op")
    public static void changeRate(Player player, @Parameter(name = "number")double rate) {

        for (TreasureChest treasureChest : Foxtrot.getInstance().getTreasureChestHandler().getTreasureChests()) {
            for (TreasureChestReward reward : treasureChest.getRewards()) {

                if (reward.getItemStack() == null) {
                    continue;
                }

                final ItemStack itemStack = reward.getItemStack();

                if (itemStack.getType().equals(Material.AIR)) {
                    continue;
                }

                if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
                    continue;
                }

                String name = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());

                if (name.equalsIgnoreCase("The Custom Rank")) {
                    player.sendMessage(ChatColor.translate("&aChanged the rate from &f" + reward.getChance() + " &ato &f" + rate));
                    reward.setChance(rate);
                    break;
                }

            }
        }

    }

    @Command(names = {"chest sevendayrate"}, permission = "op")
    public static void changeDayRate(Player player, @Parameter(name = "number")double rate) {

        for (TreasureChest treasureChest : Foxtrot.getInstance().getTreasureChestHandler().getTreasureChests()) {
            for (TreasureChestReward reward : treasureChest.getRewards()) {

                if (reward.getItemStack() == null) {
                    continue;
                }

                final ItemStack itemStack = reward.getItemStack();

                if (itemStack.getType().equals(Material.AIR)) {
                    continue;
                }

                if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
                    continue;
                }

                String name = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());

                if (name.equalsIgnoreCase("Custom Rank")) {
                    player.sendMessage(ChatColor.translate("&aChanged the rate from &f" + reward.getChance() + " &ato &f" + rate));
                    reward.setChance(rate);
                    break;
                }

            }
        }

    }

    @Command(names = {"chest setcentral"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "treasure")TreasureChest treasureChest) {
        treasureChest.setCentralLocation(player.getLocation().clone());

        player.sendMessage(ChatColor.GREEN + "Set the central chest location for " + treasureChest.getId());
    }

    @Command(names = {"chest endcurrent"}, permission = "op")
    public static void endCurrent(Player player) {
        final TreasureChestHandler treasureChestHandler = Foxtrot.getInstance().getTreasureChestHandler();
        treasureChestHandler.getTreasureChestListener().end(player);

        player.sendMessage(ChatColor.GREEN + "Ended current stage.");
    }

    @Command(names = {"chest reload"}, permission = "op", hidden = true)
    public static void reload(Player player) {
        final TreasureChestHandler treasureChestHandler = Foxtrot.getInstance().getTreasureChestHandler();
        treasureChestHandler.getTreasureChests().clear();
        treasureChestHandler.loadData(false);

        player.sendMessage(ChatColor.GREEN + "Reloaded Treasure Chests.");
    }

    @Command(names = {"chest addchest"}, permission = "op")
    public static void addChest(Player player, @Parameter(name = "chest")TreasureChest treasureChest) {
        final Block standing = player.getLocation().getBlock();

        if (standing.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "You must be standing on a Chest!");
            return;
        }

        final TreasureChestHandler treasureChestHandler = Foxtrot.getInstance().getTreasureChestHandler();

        if (treasureChest.getChests().contains(standing.getLocation())) {
            player.sendMessage(ChatColor.RED + "There is already a chest in that spot!");
            return;
        }

        if (treasureChest.getChests().size() > 8) {
            player.sendMessage(ChatColor.RED + "There's already 8 chests!");
            return;
        }

        treasureChest.getChests().add(standing.getLocation().clone());

        player.sendMessage(ChatColor.GREEN + "Added another chest to the list");
    }

    @Command(names = {"chest clearchests"}, permission = "op")
    public static void clearChests(Player player) {
        final TreasureChestHandler treasureChestHandler = Foxtrot.getInstance().getTreasureChestHandler();

        player.sendMessage(ChatColor.GREEN + "Cleared all chests");
    }

    @Command(names = {"chest give"}, permission = "op", hidden = true)
    public static void execute(CommandSender sender, @Parameter(name = "chest")TreasureChest treasureChest, @Parameter(name = "target") Player target, @Parameter(name = "amount")int amount) {
        target.sendMessage("");
        target.sendMessage(ChatColor.translate(treasureChest.getDisplayName()));
        target.sendMessage(ChatColor.translate("&7You have been given &f" + amount + "x &7" + treasureChest.getDisplayName() + "&7!"));
        target.sendMessage(ChatColor.translate("&aOpen it by simply right clicking the Treasure Chest at Spawn."));
        target.sendMessage("");

        PlayerUtil.sendTitle(target, ChatColor.translate(treasureChest.getDisplayName()), ChatColor.translate("&7You have been given &f" + amount + "x &7" + treasureChest.getDisplayName() + "&7!"));

        sender.sendMessage(ChatColor.translate("&6Gave &f" + target.getName() + " " + amount + "x " + treasureChest.getDisplayName() + "&6."));

        treasureChest.getCache().put(target.getUniqueId(), treasureChest.getCache().getOrDefault(target.getUniqueId(), 0)+amount);
    }

    @Command(names = {"chest take"}, permission = "op", hidden = true)
    public static void take(CommandSender sender, @Parameter(name = "chest")TreasureChest treasureChest, @Parameter(name = "target") Player target, @Parameter(name = "amount")int amount) {
        target.sendMessage(ChatColor.translate("&6You got " + amount + " x " + treasureChest.getDisplayName() + "&6 taken from you by " + sender.getName()));
        sender.sendMessage(ChatColor.translate("&cTook &f" + target.getName() + " " + amount + "x " + treasureChest.getDisplayName() + "&6."));

        treasureChest.getCache().put(target.getUniqueId(), treasureChest.getCache().getOrDefault(target.getUniqueId(), 0)-amount);

        if (treasureChest.getCache().getOrDefault(target.getUniqueId(), 0) < 0) {
            treasureChest.getCache().put(target.getUniqueId(), 0);
        }
    }

    @Command(names = {"chest show"}, permission = "op", hidden = true)
    public static void take(CommandSender sender, @Parameter(name = "chest")TreasureChest treasureChest, @Parameter(name = "target") Player target) {
        sender.sendMessage(ChatColor.translate("&c" + target.getName() + " has " + treasureChest.getCache().getOrDefault(target.getUniqueId(), 0)));
    }

    @Command(names = {"chest add"}, permission = "command.treasure.add", hidden = true)
    public static void addItem(Player player, @Parameter(name = "chest")TreasureChest treasureChest, @Parameter(name = "chance") double chance) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand!");
            return;
        }

        final ItemStack itemStack = player.getItemInHand().clone();

        player.sendMessage(ChatColor.translate("&6Added &f" + ItemUtils.getName(itemStack) + " &6to the Treasure Rewards."));
        treasureChest.getRewards().add(new TreasureChestReward(itemStack.clone(), chance, "none",true, false));
    }

}
