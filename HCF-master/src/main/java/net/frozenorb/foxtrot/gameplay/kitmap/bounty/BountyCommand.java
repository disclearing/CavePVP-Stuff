package net.frozenorb.foxtrot.gameplay.kitmap.bounty;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BountyCommand {

    @Command(names = {"bounty", "setbounty", "addbounty"}, permission = "")
    public static void bounty(Player sender, @Parameter(name = "target") Player target, @Parameter(name = "amount") int amount) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        if (Foxtrot.getInstance().getBountyCooldownMap().isOnCooldown(sender.getUniqueId())) {
            long millisLeft = Foxtrot.getInstance().getBountyCooldownMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "Bounty cooldown: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
            return;
        }

        if (amount < 5) {
            sender.sendMessage(CC.RED + "Your bounty must be at least 5 gems!");
            return;
        }

        if (sender == target) {
            sender.sendMessage(CC.RED + "You cannot put a bounty on yourself!");
            return;
        }

        Bounty bounty = Foxtrot.getInstance().getBountyManager().getBounty(target);

        if (bounty != null && bounty.getGems() >= amount) {
            sender.sendMessage(CC.RED + "Your bounty must be higher than the current bounty of " + bounty.getGems() + " gems!");
            return;
        }

        if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), amount)) {
            sender.sendMessage(CC.RED + "You do not have enough gems for this!");
            return;
        }

        if (bounty != null) {
            Foxtrot.getInstance().getGemMap().addGems(bounty.getPlacedBy(), bounty.getGems(), true);
        }

        Foxtrot.getInstance().getBountyManager().placeBounty(sender, target, amount);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Bounty");
            onlinePlayer.sendMessage(sender.getDisplayName() + ChatColor.translate(" &7placed a bounty on &f" + target.getDisplayName() + " &7worth &a" + amount + " Gems&7."));
            onlinePlayer.sendMessage(ChatColor.translate("&cLocation: &f" + target.getLocation().getBlockX() + ", " + target.getLocation().getBlockZ()));
            onlinePlayer.sendMessage("");
        }

        Foxtrot.getInstance().getBountyCooldownMap().applyCooldown(sender.getUniqueId(), 30);
    }

    @Command(names = {"bounty list", "bountylist"}, permission = "")
    public static void bountyList(Player sender) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        new PaginatedMenu() {
            @Override
            public String getPrePaginatedTitle(Player player) {
                return null;
            }

            @Override
            public Map<Integer, Button> getAllPagesButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();
                Foxtrot.getInstance().getBountyManager().getBountyMap()
                        .entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .forEach((entry) -> {
                           toReturn.put(toReturn.size(), new Button() {
                               @Override
                               public String getName(Player player) {
                                   return ChatColor.DARK_RED + ChatColor.BOLD.toString() + UUIDUtils.name(entry.getKey());
                               }

                               @Override
                               public byte getDamageValue(Player player) {
                                   return (byte) 3;
                               }

                               @Override
                               public List<String> getDescription(Player player) {
                                   return Arrays.asList("", "&4&l┃ &fWorth: &a" + entry.getValue().getGems() + " Gems");
                               }

                               @Override
                               public Material getMaterial(Player player) {
                                   return Material.SKULL_ITEM;
                               }

                               @Override
                               public ItemStack getButtonItem(Player player) {
                                   return ItemBuilder.of(this.getMaterial(player)).name(this.getName(player)).setLore(this.getDescription(player)).data(this.getDamageValue(player)).skull(UUIDUtils.name(entry.getKey())).build();
                               }
                           });
                        });

                return toReturn;
            }
        }.openMenu(sender);

//        Foxtrot.getInstance().getBountyManager().getBountyMap()
//                .entrySet().stream()
//                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//                .forEach((entry) ->
//                        sender.sendMessage(ChatColor.DARK_GREEN
//                                + Proton.getInstance().getUuidCache().name(entry.getKey()) + ": "
//                                + ChatColor.GREEN + entry.getValue().getGems() + " gems"));
    }
}
