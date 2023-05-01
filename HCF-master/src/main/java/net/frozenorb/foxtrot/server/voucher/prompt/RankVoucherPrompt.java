package net.frozenorb.foxtrot.server.voucher.prompt;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.universe.UniverseAPI;
import lombok.AllArgsConstructor;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import net.frozenorb.foxtrot.server.voucher.menu.VoucherRedeemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@AllArgsConstructor
public class RankVoucherPrompt extends StringPrompt {
    private Player player;
    private Voucher voucher;

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String string) {

        String playerName = string;

        if (string.equalsIgnoreCase("self")) {
            playerName = player.getName();
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromName(playerName, true, true, true);

        if (profile == null) {
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + playerName + " has never logged onto the server.");
            return END_OF_CONVERSATION;
        }

        if (playerName.equalsIgnoreCase("cancel")) {
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Cancelled rank granting process.");
            new VoucherRedeemMenu().openMenu(player);
            return END_OF_CONVERSATION;
        }

        voucher.setCode("Given to " + playerName);
        voucher.setUsedBy(player.getName());
        voucher.setUsedTime(System.currentTimeMillis());
        voucher.setUsed(true);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        final Server server = Foxtrot.getInstance().getServer();

        final Rank activeRank = profile.getActiveRank();
        final Rank wonRank = Neutron.getInstance().getRankHandler().fromName(voucher.getRankName());

        if (activeRank != null && wonRank != null && activeRank.getWeight().get() <= wonRank.getWeight().get()) {
            server.dispatchCommand(server.getConsoleSender(), "reclaimreset " + profile.getName());
        }

        server.dispatchCommand(server.getConsoleSender(), "grant " + profile.getName() + " " + voucher.getRankName() + " " + voucher.getRankDuration() + " Auto-generated voucher on " + UniverseAPI.getServerName() + ", won by " + player.getName());

        conversationContext.getForWhom().sendRawMessage(ChatColor.translate("&6Successfully gave " + voucher.getVoucher() + " &6to &f" + profile.getFancyName() + "&6."));
        return Prompt.END_OF_CONVERSATION;
    }
}
