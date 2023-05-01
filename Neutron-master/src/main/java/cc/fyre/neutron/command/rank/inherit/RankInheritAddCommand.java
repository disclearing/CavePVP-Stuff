package cc.fyre.neutron.command.rank.inherit;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class RankInheritAddCommand {

    @Command(
            names = {"rank inherit add"},
            permission = "neutron.command.rank.inherit.add"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "inherit")Rank inherit) {

        if (rank.getInherits().contains(inherit)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " already has " + inherit.getFancyName() + ChatColor.RED + " inherited.");
            return;
        }

        if (!rank.getInherits().contains(inherit) && rank.getEffectiveInherits().contains(inherit)) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(rank.getFancyName() + ChatColor.RED + " already has " + inherit.getFancyName() + ChatColor.RED + " inherited from another rank.");
                return;
            }

            final ConversationFactory factory = new ConversationFactory(Neutron.getInstance()).withFirstPrompt(new InheritAddPrompt(rank,inherit)).withLocalEcho(false);

            final Player player = (Player)sender;

            player.beginConversation(factory.buildConversation(player));
            return;
        }

        rank.getInherits().add(inherit);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Added " + inherit.getFancyName() + ChatColor.GOLD + " to " + rank.getFancyName() + ChatColor.GOLD + "'s inherits.");
    }

    @AllArgsConstructor
    static class InheritAddPrompt extends StringPrompt {

        @Getter private Rank rank;
        @Getter private Rank inherit;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.GOLD + "Are you sure you want to inherit this rank to " + this.rank.getFancyName() + ChatColor.RED +
                    " as it already has this rank inherited from another rank. " + ChatColor.GRAY +
                    "(" + ChatColor.DARK_GREEN + "Yes " + ChatColor.GRAY + "\u2503" + ChatColor.DARK_RED + " No" + ChatColor.GRAY + ")";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext,String input) {

            final Player player = (Player)conversationContext.getForWhom();

            if (input.equalsIgnoreCase("Yes")) {
                this.rank.getInherits().add(this.inherit);
                this.rank.save();

                player.sendMessage(ChatColor.GOLD + "Added " + this.inherit.getFancyName() + ChatColor.GOLD + " to " + this.rank.getFancyName() + ChatColor.GOLD + "'s inherits.");
            } else {
                player.sendMessage(ChatColor.RED + "Cancelled adding " + this.inherit.getFancyName() + ChatColor.RED + " to " + this.rank.getFancyName() + ChatColor.RED + "'s inherits.");
            }

            return END_OF_CONVERSATION;
        }

    }

}
