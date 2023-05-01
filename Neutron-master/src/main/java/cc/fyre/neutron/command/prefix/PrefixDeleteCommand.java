package cc.fyre.neutron.command.prefix;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prefix.Prefix;
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

public class PrefixDeleteCommand {

    @Command(
            names = {"prefix delete"},
            permission = "neutron.command.prefix.delete"
    )
    public static void execute(CommandSender sender,@Parameter(name = "prefix") Prefix prefix) {

        if (!(sender instanceof Player)) {
            prefix.delete();
            return;
        }

        final ConversationFactory factory = new ConversationFactory(Neutron.getInstance()).withFirstPrompt(new PrefixDeletePrompt(prefix)).withLocalEcho(false);

        final Player player = (Player)sender;

        player.beginConversation(factory.buildConversation(player));
    }

    @AllArgsConstructor
    static class PrefixDeletePrompt extends StringPrompt {

        @Getter private Prefix prefix;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.GOLD + "Are you sure you want to delete this prefix?" + ChatColor.GRAY + "(" + ChatColor.DARK_GREEN + "Yes " + ChatColor.GRAY + "\u2503" + ChatColor.DARK_RED + " No" + ChatColor.GRAY + ")";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext,String input) {

            final Player player = (Player)conversationContext.getForWhom();

            if (input.equalsIgnoreCase("Yes")) {
                this.prefix.delete();
                player.sendMessage(ChatColor.GOLD + "Deleted prefix " + ChatColor.WHITE + this.prefix.getName() + ChatColor.GOLD + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Cancelled deleting prefix " + ChatColor.WHITE + this.prefix.getName() + ChatColor.GOLD + ".");
            }

            return END_OF_CONVERSATION;
        }

    }

}
