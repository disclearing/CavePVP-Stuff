package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class VoucherElement extends Button {

    private Voucher voucher;
    private Menu menu;

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
    public void clicked(Player player, int i, ClickType clickType) {
        if (voucher.isUsed()) {

            if (voucher.getRankName() != null) {
                Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {

                    if (!voucher.getCode().contains("Given to ")) {
                        player.sendMessage(ChatColor.RED + "Doesnt exist!");
                        return;
                    }

                    final String playerName = voucher.getCode().replace("Given to ", "");
                    final String usedBy = UUIDUtils.name(voucher.getTarget());
                    final Profile profile = Neutron.getInstance().getProfileHandler().fromName(playerName, true, true, true);

                    if (profile == null) {
                        player.sendMessage(ChatColor.RED + "PROFILE NOT FOUND!");
                        return;
                    }

                    for (Grant grant : profile.getGrants()) {
                        if (grant.getExecutedReason().contains("Auto-generated voucher") && grant.getExecutedReason().contains(usedBy) && grant.getRank().getName().contains(voucher.getRankName())) {
                            player.sendMessage(ChatColor.GREEN + "Deleted a rank, found " + grant.getRank().getName());

                            grant.setPardoner(player.getUniqueId());
                            grant.setPardonedAt(System.currentTimeMillis());
                            grant.setPardonedReason("DUPED DO NOT GIVE IT BACK");
                        }
                    }

                    profile.recalculateGrants();
                    profile.save();

                    voucher.setCode(null);
                    voucher.setUsedBy(null);
                    voucher.setUsedTime(0);
                    voucher.setUsed(false);
                    Foxtrot.getInstance().getVoucherHandler().getCache().remove(voucher);
                });
            }

            player.sendMessage(ChatColor.YELLOW + "This voucher has been set as pending.");
            return;
        }

        if (!voucher.isUsed()) {
            player.closeInventory();

            ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                public String getPromptText(ConversationContext context) {
                    return "§eType the code of the Voucher that has been applied.";
                }

                @Override
                public Prompt acceptInput(ConversationContext cc, String s) {
                    voucher.setCode(s);
                    voucher.setUsedBy(player.getName());
                    voucher.setUsedTime(System.currentTimeMillis());
                    voucher.setUsed(true);

                    player.sendMessage(ChatColor.GREEN + "This voucher has been set as used.");

                    menu.openMenu(player);
                    return Prompt.END_OF_CONVERSATION;
                }

            }).withLocalEcho(false).withEscapeSequence("/null").withTimeout(100).thatExcludesNonPlayersWithMessage("Go away evil console!");
            Conversation con = factory.buildConversation(player);
            player.beginConversation(con);
        }
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        if (voucher.isUsed()) {
            return ItemBuilder.of(Material.BEDROCK).name(ChatColor.WHITE + voucher.getVoucher()).setLore(Arrays.asList("", CC.translate("&eTime Added &6\u00bb &f" + new Date(voucher.getAddedTime()).toLocaleString()),CC.translate("&eAdded By &6\u00bb &f" + voucher.getAddedBy()), CC.translate("&eStaff who Redeemed &6\u00bb &f" + voucher.getUsedBy()), CC.translate("&eTime Used &6\u00bb &f" + new Date(voucher.getUsedTime()).toLocaleString()), CC.translate("&eVoucher Code &6\u00bb &f" + voucher.getCode()), "", ChatColor.GRAY + "Click to set as pending.")).build();
        }

        return ItemBuilder.of(Material.BEDROCK).name(ChatColor.WHITE + voucher.getVoucher()).setLore(Arrays.asList("",CC.translate("&eTime Added &6\u00bb &f" + new Date(voucher.getAddedTime()).toLocaleString()),CC.translate("&eAdded By &6\u00bb &f" + voucher.getAddedBy()), "", ChatColor.GRAY + "Click to set as used.")).build();
    }
}