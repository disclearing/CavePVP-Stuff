package cc.fyre.neutron.command.rank.permission;

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

public class RankPermissionAddCommand {

    @Command(
            names = {"rank permission add"},
            permission = "neutron.command.rank.permission.add"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "permission")String permission) {

        if (rank.getPermissions().contains(permission)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " already has the permission node " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        if (!rank.getPermissions().contains(permission) && rank.getEffectivePermissions().contains(permission)) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(rank.getFancyName() + ChatColor.RED + " already has the permission node " + ChatColor.WHITE + permission + ChatColor.RED + " inherited.");
                return;
            }

            final ConversationFactory factory = new ConversationFactory(Neutron.getInstance()).withFirstPrompt(new PermissionAddPrompt(rank,permission)).withLocalEcho(false);

            final Player player = (Player) sender;

            player.beginConversation(factory.buildConversation(player));
            return;
        }

        if ((permission.equalsIgnoreCase("*") || permission.contains("*")) && sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "You may not add that permission unless you're in CONSOLE!");
            return;
        }

        rank.getPermissions().add(permission);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Added permission node " + ChatColor.WHITE + permission + ChatColor.GOLD + " to " + rank.getFancyName() + ChatColor.GOLD + ".");
    }

    @AllArgsConstructor
    static class PermissionAddPrompt extends StringPrompt {

        @Getter private Rank rank;
        @Getter private String permission;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.GOLD + "Are you sure you want to add this permission node to " + this.rank.getFancyName() + ChatColor.GOLD +
                    " as it already has this permission node inherited from another rank. " + ChatColor.GRAY +
                    "(" + ChatColor.DARK_GREEN + "Yes " + ChatColor.GRAY + "\u2503" + ChatColor.DARK_RED + " No" + ChatColor.GRAY + ")";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext,String input) {

            final Player player = (Player)conversationContext.getForWhom();

            if (input.equalsIgnoreCase("Yes")) {
                this.rank.getPermissions().add(this.permission);
                this.rank.save();

                player.sendMessage(ChatColor.GOLD + "Added permission node " + ChatColor.WHITE + this.permission + ChatColor.GOLD + " to " + this.rank.getFancyName() + ChatColor.GOLD + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Cancelled permission node " + ChatColor.WHITE + this.permission + ChatColor.RED + " to " + this.rank.getFancyName() + ChatColor.RED + ".");
            }

            return END_OF_CONVERSATION;
        }

    }
}
