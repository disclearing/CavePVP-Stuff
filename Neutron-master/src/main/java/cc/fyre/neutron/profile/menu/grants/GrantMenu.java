package cc.fyre.neutron.profile.menu.grants;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantRemovePacket;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.button.RankInfoButton;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.UnicodeUtils;
import it.unimi.dsi.fastutil.Hash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@AllArgsConstructor
public class GrantMenu extends Menu {
    private Profile target;

    @Override
    public String getTitle(Player player) {
        return "Select a Rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final Profile executorProfile = Neutron.getInstance().getProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (executorProfile == null) {
            player.sendMessage(ChatColor.RED + "Could not find your profile! Contact an owner!");
            return new HashMap<>();
        }

        Neutron.getInstance().getRankHandler().getSortedValueCache().stream().filter(it -> executorProfile.getActiveRank().getWeight().get() > it.getWeight().get()).forEach(rank -> toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return rank.getFancyName();
            }

            @Override
            public List<String> getDescription(Player player) {

                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                toReturn.add(ChatColor.GOLD + "Example Prefix:");
                toReturn.add(ChatColor.WHITE + rank.getPrefix() + target.getName());
                toReturn.add("");
                toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT-CLICK to grant this rank");
                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(rank.getColor()).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                final ConversationFactory factory = new ConversationFactory(Proton.getInstance()).withFirstPrompt(new ReasonPrompt(target, rank)).withLocalEcho(false);

                player.beginConversation(factory.buildConversation(player));
            }
        }));

        return toReturn;
    }

    @AllArgsConstructor
    public class DurationPrompt extends StringPrompt {

        @Getter private Profile target;
        @Getter private Rank rank;
        @Getter private String reason;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type a duration for this grant, (\"perm\" for permanent) or type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            final Player sender = (Player) conversationContext.getForWhom();

            if (input.equalsIgnoreCase("cancel")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Granting process cancelled.");
                return END_OF_CONVERSATION;
            }

            DurationWrapper durationWrapper = NeutronConstants.findDurationWrapper(input);

            if (durationWrapper.isPermanent() && !input.toLowerCase().startsWith("perm")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Granting process cancelled as you typed in an invalid duration.");
                return END_OF_CONVERSATION;
            }

            if (durationWrapper.isPermanent() && rank.getName().equalsIgnoreCase("VIP")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "You can't grant VIP permanently!");
                return END_OF_CONVERSATION;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    new ConfirmGrantMenu(target, rank, reason, durationWrapper).openMenu(sender);
                }
            }.runTask(Neutron.getInstance());
            return Prompt.END_OF_CONVERSATION;
        }
    }

    public void promptTime(Player player, Profile target, Rank rank, String reason) {
        final ConversationFactory factory = new ConversationFactory(Proton.getInstance()).withFirstPrompt(new DurationPrompt(target, rank, reason)).withLocalEcho(false);

        player.beginConversation(factory.buildConversation(player));
    }

    @AllArgsConstructor
    public class ReasonPrompt extends StringPrompt {

        @Getter private Profile target;
        @Getter private Rank rank;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type a reason for this grant to be added, or type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            final Player sender = (Player) conversationContext.getForWhom();

            if (input.equalsIgnoreCase("cancel")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Granting process cancelled.");
                return END_OF_CONVERSATION;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    promptTime(sender, target, rank, input);
                }
            }.runTask(Neutron.getInstance());

            return Prompt.END_OF_CONVERSATION;
        }
    }
}
