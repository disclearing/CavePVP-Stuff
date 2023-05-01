package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand {

    @Command(names = {"stats"}, permission = "")
    public static void stats(Player sender, @Parameter(name = "player", defaultValue = "self") UUID uuid) {
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.WHITE + Proton.getInstance().getUuidCache().name(uuid) + ChatColor.GOLD + "'s Statistics");
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        final StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(uuid);

        if (stats == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        sender.sendMessage(CC.translate("&cKills: &f" + stats.getKills()));
        sender.sendMessage(CC.translate("&cDeaths: &f" + stats.getDeaths()));
        sender.sendMessage(CC.translate("&cKill Streak: &f" + stats.getKillstreak()));
        sender.sendMessage(CC.translate("&cHighest Kill Streak: &f" + stats.getHighestKillstreak()));
        if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            sender.sendMessage(CC.translate("&cCave Says Completed: &f" + stats.getCaveSaysCompleted()));
        }
        sender.sendMessage(CC.translate("&cKDR: &f" + (stats.getDeaths() == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) stats.getKills() / (double) stats.getDeaths()))));
        sender.sendMessage(CC.translate("&cKOTH Captures: &f" + stats.getKothCaptures()));
        sender.sendMessage(CC.translate("&cFaction: &f" + (Foxtrot.getInstance().getTeamHandler().getTeam(uuid) == null ? "N/A" : Foxtrot.getInstance().getTeamHandler().getTeam(uuid).getName())));

        final PlaytimeMap playtimeMap = Foxtrot.getInstance().getPlaytimeMap();

        int playtimeTime = (int) playtimeMap.getPlaytime(uuid);
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(uuid);

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtimeMap.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(CC.translate("&cPlaytime: &f" + TimeUtils.formatIntoDetailedString(playtimeTime)));
        sender.sendMessage(CC.translate("&cDiamonds Mined: &f" + Foxtrot.getInstance().getDiamondMinedMap().getMined(uuid)));
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Command(names = {"clearallstats"}, permission = "op")
    public static void clearallstats(Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear all stats? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Foxtrot.getInstance().getMapHandler().getStatsHandler().clearAll();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
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

    @Command(names={ "Ores" }, permission="")
    public static void ores(Player sender, @Parameter(name="player") UUID player) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + Foxtrot.getInstance().getDiamondMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + Foxtrot.getInstance().getEmeraldMinedMap().getMined(player));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + Foxtrot.getInstance().getRedstoneMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + Foxtrot.getInstance().getGoldMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + Foxtrot.getInstance().getIronMinedMap().getMined(player));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + Foxtrot.getInstance().getLapisMinedMap().getMined(player));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + Foxtrot.getInstance().getCoalMinedMap().getMined(player));
    }

}