package net.frozenorb.foxtrot.server.voucher;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import net.buycraft.plugin.BuyCraftAPI;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.data.Coupon;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.menu.VoucherMainMenu;
import net.frozenorb.foxtrot.server.voucher.menu.VoucherRedeemMenu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoucherCommand {

    @Command(names = {"perkkeybroadcast"}, permission = "op")
    public static void broadcast(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "reward", wildcard = true)String reward) {
        final Server server = Foxtrot.getInstance().getServer();

        for (Player onlinePlayer : server.getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&a&lPerk Crate"));
            onlinePlayer.sendMessage(ChatColor.translate(target.getName() + " &chas won " + reward + "&c!"));
            onlinePlayer.sendMessage(ChatColor.translate("&7Purchase Perk Keys on our store at &fstore.cavepvp.org&7."));
            onlinePlayer.sendMessage("");
        }
    }

    @Command(names = {"voucher view", "vouchers view"}, permission = "command.voucher.view", hidden = true)
    public static void execute(CommandSender commandSender, @Parameter(name = "target")UUID target) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to do this command.");
            return;
        }

        final Player player = (Player) commandSender;

        new VoucherMainMenu(target).openMenu(player);

        player.sendMessage(ChatColor.GREEN + "Successfully opened GUI.");
    }


    @Command(names = {"voucher save"}, permission = "op")
    public static void execute(Player player) {
        Foxtrot.getInstance().getVoucherHandler().saveData();
        player.sendMessage(ChatColor.GREEN + "Saved all voucher data!");
    }

    @Command(names = {"voucher data"}, permission = "op")
    public static void data(Player player) {
        int amountGiven = 0;
        int amountRedeemed = 0;
        int ranks = 0;
        int ranksRedeemed = 0;

        for (Voucher voucher : Foxtrot.getInstance().getVoucherHandler().getCache()) {

            if (voucher.getAmount() != 0) {
                amountGiven += voucher.getAmount();
            }

            if (voucher.getAmount() != 0 && voucher.isUsed()) {
                amountRedeemed += voucher.getAmount();
            }

            if (voucher.isRank()) {
                ranks++;
            }

            if (voucher.isRank() && voucher.isUsed()) {
                ranksRedeemed++;
            }
        }

        player.sendMessage(ChatColor.translate("&6Amount Given: &2$&a" + amountGiven));
        player.sendMessage(ChatColor.translate("&6Amount Redeemed: &2$&a" + amountRedeemed));
        player.sendMessage(ChatColor.translate("&6Ranks Given: &f" + ranks));
        player.sendMessage(ChatColor.translate("&6Ranks Redeemed: &f" + ranksRedeemed));
    }

    @Command(names = {"massview"}, permission = "op", async = true)
    public static void massView(Player player, @Parameter(name = "duration") DurationWrapper duration) {

        if (!player.getName().equalsIgnoreCase("SimplyTrash")) {
            player.sendMessage(ChatColor.RED + "Command disabled atm");
            return;
        }

        player.sendMessage("Getting all vouchers from " + duration.getSource() + " to " + duration.getDuration());

        int amountGiven = 0;
        int amountRedeemed = 0;
        int ranks = 0;
        int ranksRedeemed = 0;
        int ranksRemoved = 0;

        final BuyCraftAPI api = Foxtrot.getInstance().getBuycraftPlugin().getApiClient();
        final List<Coupon> couponList = new ArrayList<>();
        try {
            player.sendMessage(ChatColor.GREEN + "Inserted list");
            couponList.addAll(api.getAllCoupons().execute().body().getData());
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Failed to insert list!");
            e.printStackTrace();
            return;
        }

        for (Coupon coupon : couponList) {
            final long startTime = coupon.getStartDate().getTime();

            if (startTime < System.currentTimeMillis()-duration.getDuration()) {
                continue;
            }

            try {
                for (Coupon datum : couponList) {
                    if (!datum.getCode().startsWith("ALF-IMAKE-KIT-CODE-14919") && !datum.getCode().startsWith("COPY-THIS-COUPON-AND-CHANGE-AMOUNT-AND-GENERATE-NEW-CODE")) {
                        api.deleteCoupon(datum.getId()).execute();
                        player.sendMessage(ChatColor.GREEN + "Identified & deleted " + datum.getCode() + "!");
                    } else {
                        player.sendMessage(datum.getCode() + " Doesnt begin ");
                    }

                }
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Failed! Check console");
                e.printStackTrace();
            }
        }

        for (Voucher voucher : Foxtrot.getInstance().getVoucherHandler().getCache()) {

            if (voucher.getAddedTime() < System.currentTimeMillis()-duration.getDuration()) {
                continue;
            }

            if (voucher.getAmount() != 0 && voucher.isUsed()) {
                final String code = voucher.getCode();

                try {
                    for (Coupon datum : couponList) {
                        if (!datum.getCode().startsWith("ALF-IMAKE-KIT-CODE-14919") && !datum.getCode().startsWith("COPY-THIS-COUPON-AND-CHANGE-AMOUNT-AND-GENERATE-NEW-CODE")) {
                            api.deleteCoupon(datum.getId()).execute();
                            player.sendMessage(ChatColor.GREEN + "Identified & deleted " + datum.getCode() + "!");
                        } else {
                            player.sendMessage(datum.getCode() + " Doesnt begin ");
                        }

                    }
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + "Failed! Check console");
                    e.printStackTrace();
                }
            }

            if (voucher.isRank() && voucher.isUsed()) {
                if (!voucher.getCode().contains("Given to ")) {
                    player.sendMessage(ChatColor.RED + "Doesnt exist!");
                    return;
                }

                final String playerName = voucher.getCode().replace("Given to ", "");
                final String usedBy = UUIDUtils.name(voucher.getTarget());

                if (UUIDUtils.uuid(playerName) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid UUID");
                    continue;
                }

                final Profile profile = Neutron.getInstance().getProfileHandler().fromName(playerName, true, true, true);

                player.sendMessage("Trying " + playerName);

                if (profile == null) {
                    player.sendMessage(ChatColor.RED + "PROFILE NOT FOUND!");
                    continue;
                }

                for (Grant grant : profile.getGrants()) {

                    if (grant.getExecutedAt() < System.currentTimeMillis()-duration.getDuration()) {
                        continue;
                    }

                    if (grant.isPardoned()) {
                        continue;
                    }

                    if (grant.getExecutedReason().contains("Auto-generated voucher") && grant.getExecutedReason().contains(usedBy) && grant.getRank().getName().contains(voucher.getRankName())) {
                        ranksRemoved++;
                        player.sendMessage(ChatColor.GREEN + "Deleted a rank, found " + grant.getRank().getName() + " on " + ChatColor.WHITE + playerName + " removed " + ranksRemoved);

                        grant.setPardoner(player.getUniqueId());
                        grant.setPardonedAt(System.currentTimeMillis());
                        grant.setPardonedReason("DUPED DO NOT GIVE IT BACK");
                    }
                }

                profile.recalculateGrants();
                profile.save();
            }
        }

        player.sendMessage(ChatColor.translate("&6Amount Given: &2$&a" + amountGiven));
        player.sendMessage(ChatColor.translate("&6Amount Redeemed: &2$&a" + amountRedeemed));
        player.sendMessage(ChatColor.translate("&6Ranks Given: &f" + ranks));
        player.sendMessage(ChatColor.translate("&6Ranks Redeemed: &f" + ranksRedeemed));
    }

    @Command(names = {"massdelete"}, permission = "op", async = true)
    public static void execute(Player player, @Parameter(name = "lol")String begins) {

        if (!player.getName().equalsIgnoreCase("SimplyTrash")) {
            player.sendMessage(ChatColor.RED + "No permission");
            return;
        }

        final BuyCraftAPI api = Foxtrot.getInstance().getBuycraftPlugin().getApiClient();
        final List<Coupon> couponList = new ArrayList<>();
        try {
            player.sendMessage(ChatColor.GREEN + "Inserted list");
            couponList.addAll(api.getAllCoupons().execute().body().getData());
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Failed to insert list!");
            e.printStackTrace();
            return;
        }

        try {
            for (Coupon datum : couponList) {
                if (!datum.getCode().startsWith("ALF-IMAKE-KIT-CODE-14919") && !datum.getCode().startsWith("COPY-THIS-COUPON-AND-CHANGE-AMOUNT-AND-GENERATE-NEW-CODE")) {
                    api.deleteCoupon(datum.getId()).execute();
                    player.sendMessage(ChatColor.GREEN + "Identified & deleted " + datum.getCode() + "!");
                } else {
                    player.sendMessage(datum.getCode() + " Doesnt begin ");
                }

            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Failed! Check console");
            e.printStackTrace();
        }

    }

    @Command(names = {"voucher clear", "vouchers clear"}, permission = "command.voucher.clear", hidden = true)
    public static void clear(CommandSender commandSender, @Parameter(name = "target")UUID target) {
        if (commandSender instanceof Player && !commandSender.getName().equalsIgnoreCase("SimplyTrash")) {
            commandSender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to do this command.");
            return;
        }
        final Player player = (Player) commandSender;

        Foxtrot.getInstance().getVoucherHandler().getCache().stream().filter(it -> it.getTarget().toString().equalsIgnoreCase(target.toString())).forEach(it -> Foxtrot.getInstance().getVoucherHandler().getCache().remove(it));

        player.sendMessage(ChatColor.GREEN + "Cleared all of their Vouchers");
    }

    @Command(names = {"voucher addrank", "vouchers addrank"}, permission = "command.voucher.rank", hidden = true)
    public static void addRank(CommandSender commandSender, @Parameter(name = "target")UUID target, @Parameter(name = "rankName")String rankName, @Parameter(name = "duration")String duration, @Parameter(name = "displayName", wildcard = true)String displayName) {
        if (commandSender instanceof Player && !commandSender.getName().equalsIgnoreCase("SimplyTrash")) {
            commandSender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        final Voucher voucher = new Voucher(target, ChatColor.translate(displayName), commandSender.getName());

        voucher.setRank(true);
        voucher.setRankName(rankName);
        voucher.setRankDuration(duration);

        commandSender.sendMessage(ChatColor.GREEN + "Added voucher: " + CC.translate(displayName) + ChatColor.GREEN + " to " + Proton.getInstance().getUuidCache().name(target) + "'s account.");

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer == null || commandSender instanceof Player) {
            return;
        }

        targetPlayer.sendMessage("");
        targetPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Voucher");
        targetPlayer.sendMessage(ChatColor.GRAY + "Congratulations! You have won a " + ChatColor.translate(displayName) + ChatColor.GRAY + ".");
        targetPlayer.sendMessage(ChatColor.translate("&cType /voucher redeem to redeem your voucher!"));
        targetPlayer.sendMessage("");

        for (int i = 0; i < 5; i++) {
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        }

        spawnFireworks(targetPlayer.getLocation(), 25, 3, Color.RED, FireworkEffect.Type.BALL_LARGE);
    }

    @Command(names = {"voucher addbundle", "vouchers addbundles"}, permission = "command.voucher.rank", hidden = true)
    public static void addRank(CommandSender commandSender, @Parameter(name = "target")UUID target, @Parameter(name = "bundleID")String bundleID, @Parameter(name = "displayName", wildcard = true)String displayName) {
        final Voucher voucher = new Voucher(target, ChatColor.translate(displayName), commandSender.getName());

        voucher.setBundle(true);
        voucher.setBundleID(bundleID);

        commandSender.sendMessage(ChatColor.GREEN + "Added voucher: " + CC.translate(displayName) + ChatColor.GREEN + " to " + Proton.getInstance().getUuidCache().name(target) + "'s account.");

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer == null || commandSender instanceof Player) {
            return;
        }

        targetPlayer.sendMessage("");
        targetPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Voucher");
        targetPlayer.sendMessage(ChatColor.GRAY + "Congratulations! You have won a " + ChatColor.translate(displayName) + ChatColor.GRAY + ".");
        targetPlayer.sendMessage(ChatColor.translate("&cType /voucher redeem to redeem your voucher!"));
        targetPlayer.sendMessage("");

        for (int i = 0; i < 5; i++) {
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        }

        spawnFireworks(targetPlayer.getLocation(), 25, 3, Color.RED, FireworkEffect.Type.BALL_LARGE);
    }

    @Command(names = {"voucher addamount", "vouchers addamount"}, permission = "command.voucher.rank", hidden = true)
    public static void addamount(CommandSender commandSender, @Parameter(name = "target")UUID target, @Parameter(name = "amount")int amount) {
        if (commandSender instanceof Player && !commandSender.getName().equalsIgnoreCase("SimplyTrash")) {
            commandSender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        final Voucher voucher = new Voucher(target, ChatColor.translate("&2$&a" + amount + " Buycraft Voucher"), commandSender.getName());

        voucher.setAmount(amount);

        commandSender.sendMessage(ChatColor.GREEN + "Added voucher: " + ChatColor.translate("&2$&a" + amount + " Buycraft Voucher") + ChatColor.GREEN + " to " + Proton.getInstance().getUuidCache().name(target) + "'s account.");

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer == null || commandSender instanceof Player) {
            return;
        }

        targetPlayer.sendMessage("");
        targetPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Voucher");
        targetPlayer.sendMessage(ChatColor.GRAY + "Congratulations! You have won a " + ChatColor.translate("&2$&a" + amount + " Buycraft Voucher") + ChatColor.GRAY + ".");
        targetPlayer.sendMessage(ChatColor.translate("&cType /voucher redeem to redeem your voucher!"));
        targetPlayer.sendMessage("");

        for (int i = 0; i < 5; i++) {
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        }

        spawnFireworks(targetPlayer.getLocation(), 25, 3, Color.RED, FireworkEffect.Type.BALL_LARGE);
    }

    @Command(names = {"voucher redeem", "vouchers redeem"}, permission = "")
    public static void redeem(Player player) {
        new VoucherRedeemMenu().openMenu(player);
    }

    @Command(names = {"voucher add", "voucher create", "vouchers add", "vouchers create"}, permission = "command.voucher.create", hidden = true)
    public static void add(CommandSender commandSender, @Parameter(name = "target")UUID target, @Parameter(name = "voucher", wildcard = true)String text) {
        if (commandSender instanceof Player && !commandSender.getName().equalsIgnoreCase("SimplyTrash")) {
            commandSender.sendMessage(ChatColor.RED + "No permission!");
            return;
        }

        final Voucher voucher = new Voucher(target, CC.translate(text), commandSender.getName());

        commandSender.sendMessage(ChatColor.GREEN + "Added voucher: " + CC.translate(text) + ChatColor.GREEN + " to " + Proton.getInstance().getUuidCache().name(target) + "'s account.");

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer == null || commandSender instanceof Player) {
            return;
        }

        targetPlayer.sendMessage("");
        targetPlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Voucher");
        targetPlayer.sendMessage(ChatColor.GRAY + "Congratulations! You have won a " + CC.translate(text) + ChatColor.GRAY + ".");
        targetPlayer.sendMessage(ChatColor.translate("&cType /voucher redeem to redeem your voucher!"));
        targetPlayer.sendMessage("");

        for (int i = 0; i < 5; i++) {
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        }

        spawnFireworks(targetPlayer.getLocation(), 25, 3, Color.RED, FireworkEffect.Type.BALL_LARGE);
    }

    @Command(names = {"nitroboost"}, permission = "command.nitroboost.giverewards")
    public static void execute(Player player, @Parameter(name = "target")Player target) {
        player.sendMessage(ChatColor.GREEN + "Giving rewards...");
        player.chat("/crates give " + target.getName() + " Seasonal 5");
        player.chat("/crates give " + target.getName() + " Reinforce 5");
        player.chat("/cr givekey " + target.getName() + " Items 10");
    }

    public static void spawnFireworks(Location location, int amount, int power, Color color, FireworkEffect.Type fireworkEffect){
//        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
//        FireworkMeta fireworkMeta = firework.getFireworkMeta();
//
//        fireworkMeta.setPower(power);;
//        fireworkMeta.addEffect(FireworkEffect.builder().with(fireworkEffect).with(FireworkEffect.Type.BURST).withColor(color).flicker(true).build());
//        firework.setFireworkMeta(fireworkMeta);
//        firework.detonate();
//
//        for(int i = 0; i < amount; i++){
//            Firework entityFirework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
//            entityFirework.setFireworkMeta(fireworkMeta);
//        }
    }

}