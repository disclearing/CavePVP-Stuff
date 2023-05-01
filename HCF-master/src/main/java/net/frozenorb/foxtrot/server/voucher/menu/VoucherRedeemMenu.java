package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import net.buycraft.plugin.BuyCraftAPI;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.data.Coupon;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Bundles;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import net.frozenorb.foxtrot.server.voucher.prompt.RankVoucherPrompt;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoucherRedeemMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Redeem Vouchers";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Voucher voucher : Foxtrot.getInstance().getVoucherHandler().getCache().stream().filter(it -> it.getTarget().toString().equalsIgnoreCase(player.getUniqueId().toString())).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(voucher.getVoucher());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> lore = new ArrayList<>();
                    lore.add("");

                    if (voucher.isRank()) {
                        lore.add(ChatColor.translate("&4&l┃ &fRank: &c" + voucher.getRankName()));
                        lore.add(ChatColor.translate("&4&l┃ &fDuration: &c" + (voucher.getRankDuration().equalsIgnoreCase("perm") ? "Lifetime" : voucher.getRankDuration())));
                    }

                    if (voucher.getAmount() != 0) {
                        lore.add(ChatColor.translate("&4&l┃ &fAmount: &2$&a" + voucher.getAmount()));
                        lore.add(ChatColor.translate("&4&l┃ &fCode: &c" + (voucher.isUsed() ? ChatColor.MAGIC + "dhauhduDh38" : "Click to generate")));
                    }

                    lore.add(ChatColor.translate("&4&l┃ &fDate Added: &c" + new Date(voucher.getAddedTime()).toLocaleString()));
                    lore.add("");

                    if (voucher.isUsed()) {
                        lore.add(ChatColor.RED + "This voucher has already been redeemed!");
                    } else if (voucher.isRank()) {
                        lore.add(ChatColor.GREEN + "Click to choose who to give the rank to");
                    } else if (voucher.getAmount() != 0) {
                        lore.add(ChatColor.GREEN + "Click to reveal the code");
                    } else {
                        lore.add(ChatColor.RED + "Go ts.cavepvp.org to redeem this voucher.");
                    }
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    if (!voucher.isRank() && voucher.getAmount() == 0 && !voucher.isBundle()) {
                        return Material.BEDROCK;
                    }

                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {
                    if (!voucher.isRank() && voucher.getAmount() == 0 && !voucher.isBundle()) {
                        return 0;
                    }

                    return (byte) (voucher.isUsed() ? 14 : 5);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();

                    if (voucher.isUsed() && voucher.getAmount() != 0) {
                        player.sendMessage("");
                        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Vouchers");
                        player.sendMessage(ChatColor.GRAY + "Your code is shown below. Do not share this with anyone.");
                        player.sendMessage(ChatColor.RED + voucher.getCode());
                        player.sendMessage("");
                        return;
                    }

                    if (voucher.isGenerating()) {
                        player.sendMessage(ChatColor.RED + "This voucher is currently being generated! Please wait...");
                        return;
                    }

                    if (voucher.isUsed()) {
                        player.sendMessage(ChatColor.RED + "This voucher has already been redeemed!");
                        return;
                    }

                    if (!voucher.isRank() && voucher.getAmount() != 0) {
                        final Coupon.Discount discount = new Coupon.Discount("value", new BigDecimal("0.0"), new BigDecimal(voucher.getAmount()));

                        final Calendar expiration = Calendar.getInstance();
                        expiration.set(Calendar.YEAR, 2030);

                        final Coupon coupon = Coupon.builder()
                                .note("Generated by " + player.getName() + " on " + new Date(System.currentTimeMillis()).toLocaleString())
                                .basketType("single")
                                .code(RandomStringUtils.random(10, true, true))
                                .userLimit(1)
                                .redeemUnlimited(false)
                                .startDate(new Date(System.currentTimeMillis()))
                                .minimum(new BigDecimal("0.0"))
                                .expire(new Coupon.Expire("limit", 1, expiration.getTime()))
                                .discountMethod(2)
                                .effective(new Coupon.Effective("packages", Foxtrot.getInstance().getVoucherHandler().getEffectiveHandler().getEffectivePackages(), new ArrayList<>()))
                                .startDate(new Date(System.currentTimeMillis()))
                                .discount(discount).build();

                        player.sendMessage(ChatColor.GREEN + "Generating vouchers...");
                        voucher.setGenerating(true);

                        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {

                            try {
                                Foxtrot.getInstance().getBuycraftPlugin().getApiClient().createCoupon(coupon).execute();
                            } catch (IOException e) {
                                player.sendMessage(ChatColor.RED + "Failed to generate this voucher. Contact an administrator immediately.");
                                e.printStackTrace();
                                return;
                            }

                            voucher.setUsed(true);
                            voucher.setUsedBy("Voucher System");
                            voucher.setUsedTime(System.currentTimeMillis());
                            voucher.setCode(coupon.getCode());
                            voucher.setGenerating(false);

                            player.sendMessage("");
                            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Vouchers");
                            player.sendMessage(ChatColor.GRAY + "Successfully generated the voucher. Your code is shown below. Do not share this with anyone.");
                            player.sendMessage(ChatColor.RED + "Code: " + ChatColor.WHITE + coupon.getCode());
                            player.sendMessage("");
                            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "WARNING" + ChatColor.YELLOW + " This voucher will disappear the moment you use it, no matter how much you spend! Only use it once!");
                            player.sendMessage(ChatColor.GRAY + "Jan 8th Update: " + ChatColor.WHITE + "You can no longer use Vouchers on Partner Keys. You can however on Unbans/Unmutes again.");
                            player.sendMessage("");
                        });

                        return;
                    }

                    if (voucher.isBundle()) {
                        final Bundles bundle = Arrays.stream(Bundles.values()).filter(it -> it.getId().equalsIgnoreCase(voucher.getBundleID())).findFirst().orElse(null);

                        if (bundle == null) {
                            player.sendMessage(ChatColor.RED + "That bundle doesn't exist! Go ts.cavepvp.org");
                            return;
                        }

                        player.sendMessage(ChatColor.GREEN + "Generating bundle...");

                        for (String command : bundle.getCommands()) {
                            Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), command.replace("{player}", player.getName()));
                        }

                        voucher.setUsed(true);
                        voucher.setUsedBy(player.getName());
                        voucher.setUsedTime(System.currentTimeMillis());
                        return;
                    }

                    if (!voucher.isRank()) {
                        player.sendMessage(ChatColor.RED + "Go ts.cavepvp.org to redeem this voucher.");
                        return;
                    }

                    player.sendMessage("");
                    player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Vouchers");
                    player.sendMessage(ChatColor.GRAY + "Type the name of the player that you want to give " + ChatColor.translate(voucher.getVoucher() + " &7to."));
                    player.sendMessage("");

                    ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                            .withModality(true)
                            .withPrefix(new NullConversationPrefix())
                            .withFirstPrompt(new RankVoucherPrompt(player, voucher))
                            .withLocalEcho(false)
                            .withEscapeSequence("/null")
                            .withTimeout(15000)
                            .thatExcludesNonPlayersWithMessage("Go away evil console!");
                    player.beginConversation(factory.buildConversation(player));
                }
            });
        }

        return toReturn;
    }
}