package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class LeaderboardAddCommand {

    @Command(names = {"leaderboard add"}, permission = "op")
    public static void leaderboardAdd(Player sender, @Parameter(name = "objective") String objectiveName, @Parameter(name = "place") int place) {
        Block block = sender.getTargetBlock(null, 10);
        StatsTopCommand.StatsObjective objective;

        try {
            objective = StatsTopCommand.StatsObjective.valueOf(objectiveName);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Invalid objective!");
            return;
        }

        if (block != null && block.getType() != Material.AIR) {
            if (block.getState() instanceof Skull) {
                Skull skull = (Skull) block.getState();

                if (skull.getSkullType() != SkullType.PLAYER) {
                    sender.sendMessage(ChatColor.RED + "That's not a player skull.");
                    return;
                }

                Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboardHeads().put(skull.getLocation(), place);
                Foxtrot.getInstance().getMapHandler().getStatsHandler().getObjectives().put(skull.getLocation(), objective);
                Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
                sender.sendMessage(ChatColor.GREEN + "This skull will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's head.");
            } else {
                Sign sign = (Sign) block.getState();

                Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboardSigns().put(sign.getLocation(), place);
                Foxtrot.getInstance().getMapHandler().getStatsHandler().getObjectives().put(sign.getLocation(), objective);
                Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
                sender.sendMessage(ChatColor.GREEN + "This sign will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's stats.");
            }
        }

        Foxtrot.getInstance().getMapHandler().getStatsHandler().getObjectives().put(block == null ? sender.getLocation():block.getLocation(),objective);
    }

    @Command(names = {"clearleaderboards"}, permission = "op")
    public static void clearallstats(Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear leaderboards? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Foxtrot.getInstance().getMapHandler().getStatsHandler().clearLeaderboards();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Leaderboards cleared");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}