package net.frozenorb.foxtrot.scoreboard;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.Proton;
import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.scoreboard.construct.ScoreGetter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.Universe;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.type.*;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEventsHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.OutpostHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.Duel;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners.TreasureCoveListener;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.VotePartyHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.listener.event.TimerEndEvent;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamStuckCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Logout;

import net.valorhcf.ThreadingManager;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FoxtrotScoreGetter implements ScoreGetter {

    public String[] getScores(LinkedList<String> scores, Player player) {
        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
            if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
                Foxtrot.getInstance().getDeathbanArenaHandler().revive(player.getUniqueId());
                return new String[0];
            }
            long seconds = (Foxtrot.getInstance().getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis());

            scores.add("&c&lDeathban&7: &f" + ScoreFunction.TIME_FANCY.apply((float) seconds / 1000F));
            scores.add("&a&lLives&7: &f" + Foxtrot.getInstance().getFriendLivesMap().getLives(player.getUniqueId()));
            scores.add("&4&lArena Kills&7: &f" + Foxtrot.getInstance().getDeathbanArenaHandler().getCache().getOrDefault(player.getUniqueId(), 0) + "/5");
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
            return new String[0];
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
                ongoingGame.getScoreboardLines(player, scores);
                scores.addFirst("&a&7&m--------------------");
                scores.add("&b&7&m--------------------");
                return new String[0];
            }
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(player)) {
            Duel duel = Foxtrot.getInstance().getMapHandler().getDuelHandler().getDuel(player);

            scores.add("&4Opponent: &f" + Proton.getInstance().getUuidCache().name(duel.getOpponent(player.getUniqueId())));
            scores.add("&4Wager: &a" + duel.getWager() + " Gems");

            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");

            return new String[0];
        }

        String spawnTagScore = getSpawnTagScore(player);
        String enderpearlScore = getEnderpearlScore(player);
        String abilityScore = getAbilityScore(player);
        String pvpTimerScore = getPvPTimerScore(player);
        String archerMarkScore = getArcherMarkScore(player);
        String rodScore = getRodScore(player);
        final String effectScore = getEffectScore(player);
        final String energyScore = getEnergyScore(player);
        String fstuckScore = getFStuckScore(player);
        String logoutScore = getLogoutScore(player);
        final String goppleScore = getGoppleScore(player);
        String homeScore = getHomeScore(player);
        String appleScore = getAppleScore(player);
        final String classUltimateScore = getClassUltimateEnergyScore(player);
        String gemBoosterScore = getGemBooster(player);
        String warmupScore = getClassWarmupScore(player);
        String getBooster = getBooster(player);

        if (Foxtrot.getInstance().getMapHandler().isKitMap() && DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

            scores.add("&4&lKills&7: &f" + stats.getKills());
            scores.add("&4&lDeaths&7: &f" + stats.getDeaths());
            scores.add("&2&lGems&7: &f" + Foxtrot.getInstance().getGemMap().getGems(player));
        }

        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {

            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            String displayName;

            switch (event.getName()) {
                case "End":
                    displayName = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "End";
                    break;
                case "Hell":
                    displayName = ChatColor.RED + ChatColor.BOLD.toString() + "Hell";
                    break;
                case "EOTW":
                    displayName = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                    break;
                case "Citadel":
                    displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                    break;
                case "NetherCitadel":
                    displayName = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Citadel";
                    break;
                default:
                    displayName = ChatColor.BLUE.toString() + ChatColor.BOLD + event.getName();
                    break;
            }

            if (event.getType() == EventType.DTC) {
                scores.add(displayName + "&7: &c" + ((DTC) event).getCurrentPoints());
            } else {
                scores.add(displayName + "&7: &c" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));
            }

        }

        final PvPClass pvpClass = PvPClassHandler.getPvPClass(player);

        if (classUltimateScore != null && pvpClass != null) {
            scores.add("&b&lUltimate&7: &c" + classUltimateScore);
        }

        if (spawnTagScore != null) {
            scores.add("&c&lSpawn Tag&7: &c" + spawnTagScore);
        }

        if (homeScore != null) {
            scores.add("&9&lHome§7: &c" + homeScore);
        }

        if (goppleScore != null) {
            scores.add("&6&lGopple&7: &c" + goppleScore);
        }

        if (rodScore != null) {
            scores.add("&5&lFishing Rod&7: &c" + rodScore);
        }

        if (appleScore != null) {
            scores.add("&6&lApple&7: &c" + appleScore);
        }

        if (enderpearlScore != null) {
            scores.add("&e&lEnderpearl&7: &c" + enderpearlScore);
        }

        if (warmupScore != null) {
            scores.add("&b&lClass Warmup&7: &c" + warmupScore);
        }

        if (pvpTimerScore != null) {
            scores.add("&a&lPvP Timer&7: &c" + pvpTimerScore);
        }

        if (gemBoosterScore != null) {
            scores.add("&2&l2x Gem Booster&7: &c" + gemBoosterScore);
        }

        if (abilityScore != null) {
            scores.add("&d&lAbility Item&7: &c" + abilityScore);
        }

        if (Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()) {
            scores.add("&b&lFrenzy Booster&7: &c" + getBooster);
        }

        final Iterator<Map.Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> timer = iterator.next();

            if (timer.getValue() < System.currentTimeMillis()) {
                iterator.remove();

                final TimerEndEvent timerEndEvent = new TimerEndEvent(timer.getValue(), timer.getKey());
                Foxtrot.getInstance().getServer().getPluginManager().callEvent(timerEndEvent);
                continue;
            }

            final String display = this.getCustomTimerDisplay(player, timer.getKey(), timer.getValue());

            if (display != null) {
                scores.add(display);
            }
        }

        final KeyAllHandler keyAllHandler = Foxtrot.getInstance().getKeyAllHandler();

        for (KeyAll keyAll : keyAllHandler.findScoreboardKeyalls()) {
            long difference = keyAll.getGiveAllTime()-System.currentTimeMillis();

            scores.add(keyAll.getScoreboardDisplay() + "&7: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
        }

        if (EOTWCommand.isFfaEnabled()) {
            long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
                long difference = ffaEnabledAt - System.currentTimeMillis();
                scores.add("&4&lFFA&7: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
            }
        }

        if (archerMarkScore != null) {
            scores.add("&6&lArcher Mark&7: &c" + archerMarkScore);
        }

        if (effectScore != null && pvpClass != null) {
            scores.add("&a&l" + pvpClass.getName() + " Effect&7: &c" + effectScore);
        }

        if (energyScore != null && pvpClass != null) {
            scores.add("&b&l" + pvpClass.getName() + " Energy&7: &c" + energyScore);
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        Long antiCleanTime = AntiCleanListener.TIME_CACHE.get(team);

        if (antiCleanTime != null) {
            scores.add("&5&lAnti-Clean&7: &c" + getTimerScore(antiCleanTime));

            if (System.currentTimeMillis() > antiCleanTime) {
                AntiCleanListener.CACHE.remove(team);
                AntiCleanListener.TIME_CACHE.remove(team);
                team.sendMessage(ChatColor.translate("&cYour &5&lAnti-Clean &ctag is now over!"));
            }
        }

        if (fstuckScore != null) {
            scores.add("&4&lStuck&7: &c" + fstuckScore);
        }

        if (logoutScore != null) {
            scores.add("&4&lLogout&7: &c" + logoutScore);
        }

        if (player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
            int amount = CrappleLimitListener.consumed.getOrDefault(player.getUniqueId(), new AtomicInteger(0)).get();
            scores.add("&6&lApples: &c" + amount + "/12");
        }

        if (Proton.getInstance().getAutoRebootHandler().isRebooting()) {
            scores.add("&4&lReboot&7: &c" + TimeUtils.formatIntoMMSS(Proton.getInstance().getAutoRebootHandler().getRebootSecondsRemaining()));
        }

        final VotePartyHandler votePartyHandler = Foxtrot.getInstance().getVotePartyHandler();

        if (votePartyHandler != null && votePartyHandler.getCurrentVotes() >= votePartyHandler.getVotePartyRequirement() - 25) {
            scores.add(ChatColor.translate("&4&lVote Party&7: &c" + (votePartyHandler.getVotePartyRequirement() - votePartyHandler.getCurrentVotes()) + " away"));
        }

        if (this.getAbilityCooldownsScore(player) != null) {
            scores.addAll(this.getAbilityCooldownsScore(player));
        }

        if (AntiBlockup.cache.containsKey(player.getUniqueId())) {
            if (AntiBlockup.cache.get(player.getUniqueId()) < System.currentTimeMillis()) {
                AntiBlockup.cache.remove(player.getUniqueId());
            } else {
                scores.add("&d&lAnti-Blockup&7: &c" + ScoreFunction.TIME_FANCY.apply((float) (AntiBlockup.cache.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000));
            }
        }

        if (Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())) {
            if (!scores.isEmpty()) {
                scores.add("&3&7&m--------------------");
            }

            final OutpostHandler outpostHandler = Foxtrot.getInstance().getOutpostHandler();
            final EndOutpost endOutpost = (EndOutpost) outpostHandler.findOutpost("End");
            final NetherOutpost netherOutpost = (NetherOutpost) outpostHandler.findOutpost("Nether");

            if (FoxtrotTitleGetter.domain) {
                scores.add("&5End: &c" + (endOutpost.getPercentage().get() == 0.0D ? ChatColor.RED : ChatColor.GREEN) + String.format("%.2f", endOutpost.getPercentage().get()) + "%");
                scores.add("&4&7• &fStatus: &e" + endOutpost.getStatus().getDisplayName());
            } else {
                scores.add("&4Nether: &c" + (netherOutpost.getPercentage().get() == 0.0D ? ChatColor.RED : ChatColor.GREEN) + String.format("%.2f", netherOutpost.getPercentage().get()) + "%");
                scores.add("&7• &fStatus: &e" + netherOutpost.getStatus().getDisplayName());
            }

            if (team != null && team.getFocusedTeam() != null) {
                final Team focusedTeam = team.getFocusedTeam();

                scores.add("&7▶ &d" + focusedTeam.getName() + ": &a" + focusedTeam.formatDTR());
            }
        }

        if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId()) && this.showTimer(player, "treasure") && Foxtrot.getInstance().getTreasureCoveHandler() != null && TreasureCoveListener.SPAWN_IN != 0 && TreasureCoveListener.SPAWN_IN > System.currentTimeMillis()) {
            Location location = Foxtrot.getInstance().getTreasureCoveHandler().getCentralChest();

            if (location != null) {
                if (!scores.isEmpty()) {
                    scores.add("&5&7&m--------------------");
                }

                scores.add("&4&lTreasure Cove");
                scores.add("&7A gift card");
                scores.add("&7drops in &f" + getTimerScore(TreasureCoveListener.SPAWN_IN));

                scores.add(ChatColor.RED + "Location: &f" + location.getBlockX() + ", " + location.getBlockZ() + " &7[Nether]");
            }
        }

        final EnderDragon enderDragon = EndListener.ENDER_DRAGON;

        if (enderDragon != null && !enderDragon.isDead()) {
            final Location location = enderDragon.getLocation();

            if (!scores.isEmpty()) {
                scores.add("&1&7&m--------------------");
            }

            scores.add("&5&lEnder Dragon");
            scores.add("&5┃ &fHealth: &d" + Math.round(enderDragon.getHealth() / enderDragon.getMaxHealth() * 100) + "%");
            scores.add("&5┃ &fLocation: &d" + location.getBlockX() + ", " + location.getBlockZ());
        }

        final long difference = Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() - System.currentTimeMillis();

        if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId()) && Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() != 0 && difference > 0) {
            final Location location = Foxtrot.getInstance().getCouponDropsHandler().getLocation().clone();

            if (!scores.isEmpty()) {
                scores.add("&d&7&m--------------------");
            }

            scores.add("&4&lDiscount Drop");
            scores.add("&7drops in &f" + getTimerScore(Foxtrot.getInstance().getCouponDropsHandler().getDropsIn()));
            scores.add(ChatColor.RED + "Location: &f" + location.getBlockX() + ", " + location.getBlockZ());
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap() && KillTheKingCommand.king != null) {

            final Player king = Foxtrot.getInstance().getServer().getPlayer(KillTheKingCommand.king);

            if (king != null) {
                if (!scores.isEmpty()) {
                    scores.add("&6&7&m--------------------");
                }

                scores.add("&4&lKill The King");
                scores.add("&4┃ &fKing: &c" + king.getName());
                scores.add("&4┃ &fLocation: &c" + king.getLocation().getBlockX() + ", " + king.getLocation().getBlockZ());

                if (king.getWorld().getName().equalsIgnoreCase(player.getLocation().getWorld().getName())) {
                    scores.add("&4┃ &fDistance: &c" + (int) Math.round(king.getLocation().distance(player.getLocation())) + "m");
                }
            }
        }

        final MiniEventsHandler miniEventsHandler = Foxtrot.getInstance().getMiniEventsHandler();

        if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId()) && player.getWorld().getEnvironment() == World.Environment.NORMAL && miniEventsHandler != null && miniEventsHandler.getActiveEvent() != null) {
            final MiniEvent miniEvent = miniEventsHandler.getActiveEvent();

            if (!scores.isEmpty()) {
                scores.add("&0&7&m--------------------");
            }

            scores.add("&4&l" + miniEvent.getObjective() + " Event");
            scores.add("&7ends in &f" + ScoreFunction.TIME_FANCY.apply((float) ((miniEvent.getEndsAt() - System.currentTimeMillis()) / 1000)));
            scores.add("&a/minievent");
        }

        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        if (caveNiteHandler != null && caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            if (!scores.isEmpty()) {
                scores.add("&1&7&m--------------------");
            }

            scores.addAll(caveNiteHandler.getScoreboardLines());
        }

        if (team != null && team.getFocusedTeam() != null && !Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())) {
            final Team focusedTeam = team.getFocusedTeam();
            if (!scores.isEmpty()) {
                scores.add("&9&7&m--------------------");
            }

            final Location hq = focusedTeam.getHQ();

            scores.add("&4&lTeam: &f" + focusedTeam.getName());

            if (hq != null) {
                scores.add("&4&lHome: &f" + hq.getBlockX() + ", " + hq.getBlockZ());
            }

            if (focusedTeam.getOwner() != null) {
                String dtrFormat = focusedTeam.formatDTR();

                if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
                    int currentHearts = (int) Math.ceil(focusedTeam.getDTR());

                    dtrFormat = focusedTeam.getDTRColor().toString() + currentHearts + "❤" + focusedTeam.getDTRSuffix();
                }

                scores.add("&4&lDTR: &f" + dtrFormat);
                scores.add("&4&lOnline: &f" + focusedTeam.getOnlineMembers().size());
            }
        }

        ConquestGame conquest = Foxtrot.getInstance().getConquestHandler().getGame();

        if (conquest != null) {

            if (scores.size() != 0) {
                scores.add("&c&7&m--------------------");
            }

            int i = 0;

            final List<String> conquestScores = new ArrayList<>();

            for (int x = 0; x < 2; x++) {

                boolean first = true;
                StringBuilder text = new StringBuilder();

                for (int y = 0; y < 2; y++) {

                    final ConquestCapzone zone = ConquestCapzone.values()[i];
                    final Event event = Foxtrot.getInstance().getEventHandler().getEvent("Conquest-" + zone.getName());

                    if (event == null) {
                        i++;
                        continue;
                    }

                    if (!(event instanceof KOTH)) {
                        i++;
                        continue;
                    }

                    final KOTH koth = (KOTH) event;

                    if (!first) {
                        text.append(" ")/*.append(ChatColor.GRAY.toString()).append("┃").append(" ")*/;
                    }

                    final long time = koth.getRemainingCapTime() <= 0 ? ConquestHandler.TIME_TO_CAP : koth.getRemainingCapTime();

                    text.append(zone.getChatColor().toString()).append("⬛ ").append(ChatColor.RED).append(TimeUtils.formatIntoMMSS((int) time));
                    i++;
                    first = !first;
                }

                if (text.toString().equalsIgnoreCase("")) {
                    continue;
                }

                conquestScores.add(text.toString());
            }

            int displayed = 0;

            for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {

                if (entry.getValue() <= 0) {
                    continue;
                }

                final Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());

                if (resolved != null) {

                    if (displayed == 0) {
                        conquestScores.add(" ");
                    }

                    conquestScores.add(ChatColor.YELLOW + resolved.getName() + "&7: &c" + entry.getValue());
                    displayed++;
                }

                if (displayed == 3) {
                    break;
                }

            }

            scores.addAll(conquestScores);
        }

        if (player.hasMetadata("STAFF_BOARD")) {

            if (!scores.isEmpty()) {
                scores.add("&f&7&m--------------------");
            }

            if (player.getName().equalsIgnoreCase("SimplyTrash")) {
                final Integer[] array = ThreadingManager.getTickCounter().getTicksPerSecond();
                final Integer last = array[array.length - 1];

                scores.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "TPS" + ChatColor.GRAY + ": " + ChatColor.RED + this.formatTPS(last));
            }

            scores.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Online" + ChatColor.GRAY + ": " + ChatColor.RED + Foxtrot.getInstance().getServer().getOnlinePlayers().size() + "/" + Foxtrot.getInstance().getServer().getMaxPlayers() + " &7(" + Universe.getInstance().getUniverseHandler().getPlayersOnNetwork() + ")");
            scores.add(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Vanished" + ChatColor.GRAY + ": " + (ModHandler.INSTANCE.isInVanish(player.getUniqueId()) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));

            if (!player.getName().equalsIgnoreCase("SimplyTrash")) {
                scores.add(CC.BLUE + CC.BOLD + "Staff Chat" + CC.GRAY + ": " + (Profiles.getInstance().hasStaffChatEnabled(player) ? CC.GREEN + "Yes" : CC.RED + "No"));
            }
        }
        while (scores.size() > 13) {
            scores.remove(scores.getLast());
        }

        if (!scores.isEmpty()) {
            // 'Top' and bottom.
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
        }
        return new String[0];
    }

    public boolean showTimer(Player player, String timer) {
        if (player.hasMetadata("NO_TIMER") || player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return false;
        }

        if (timer.contains("ALL") || timer.contains("2x Points") || timer.contains("Double Gem")) {
            return true;
        }

        if (CustomTimerCreateCommand.isSOTWTimer() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return true;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return true;
        }

        if (SpawnTagHandler.isTagged(player)) {
            return false;
        }

        if (DTRBitmask.CITADEL.appliesAt(player.getLocation()) || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            return false;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (profile != null && profile.getActiveRank() != null && profile.getActiveRank().getName().equalsIgnoreCase("Partner")) {
            return false;
        }

        if (Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())) {
            return false;
        }

        return Foxtrot.getInstance().getSaleTimersScoreboardMap().isSaleTimers(player.getUniqueId());
    }

    public String getRodScore(Player player) {
        if (RodCooldownListener.cache.containsKey(player.getUniqueId()) && RodCooldownListener.cache.get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = RodCooldownListener.cache.get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public List<String> getAbilityCooldownsScore(Player player) {

        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            return null;
        }

        if (!Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(player.getUniqueId())) {
            return null;
        }

        final List<String> toReturn = new ArrayList<>();

        for (Ability ability : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
            if (!ability.hasCooldown(player, false)) {
                continue;
            }

            if (ability.getName().equalsIgnoreCase("Combo") && Combo.cache.containsKey(player.getUniqueId())) {
                int hits = Combo.cache.get(player.getUniqueId());

                toReturn.add(ability.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RED + hits + " hits");
                continue;
            }

            toReturn.add(ability.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RED + (ScoreFunction.TIME_FANCY.apply((float) ability.getRemaining(player) / 1000)));
        }

        if (NinjaStar.teleportCooldowns.containsKey(player.getUniqueId())) {
            if (NinjaStar.teleportCooldowns.get(player.getUniqueId()) < System.currentTimeMillis()) {
                NinjaStar.teleportCooldowns.remove(player.getUniqueId());
            } else {
                toReturn.add("&b&lTeleport Item" + ChatColor.GRAY + ": " + ChatColor.RED + (ScoreFunction.TIME_FANCY.apply((float) (NinjaStar.teleportCooldowns.get(player.getUniqueId())-System.currentTimeMillis())/ 1000)));
            }
        }

        if (toReturn.isEmpty()) {
            return null;
        }

        return toReturn;
    }

    public String getCustomTimerDisplay(Player player, String display, long time) {
        if (display.equals("&a&lSOTW") && CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            return ChatColor.translate("&a&l&mSOTW&a ends in " + getTimerScore(time));
        }

        if (display.equals("&a&lSOTW")) {
            return ChatColor.translate("&a&lSOTW &aends in " + getTimerScore(time));
        }

        if (display.equalsIgnoreCase("dragon")) {
            return ChatColor.translate("&5&lDragon &5spawns in " + getTimerScore(time));
        }

        if (this.showTimer(player, display)) {
            return ChatColor.translate(display) + "&7: &c" + getTimerScore(time);
        }

        return null;
    }

    public String getAppleScore(Player player) {
        if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getHomeScore(Player player) {
        if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getFStuckScore(Player player) {
        if (TeamStuckCommand.getWarping().containsKey(player.getName())) {
            float diff = TeamStuckCommand.getWarping().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getLogoutScore(Player player) {
        Logout logout = ServerHandler.getTasks().get(player.getName());

        if (logout != null) {
            float diff = logout.getLogoutTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getEnderpearlScore(Player player) {
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getAbilityScore(Player player) {
        if (Foxtrot.getInstance().getMapHandler().getAbilityHandler().getGlobalCooldowns().containsKey(player.getUniqueId()) && Foxtrot.getInstance().getMapHandler().getAbilityHandler().getGlobalCooldowns().get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getGlobalCooldowns().get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getPvPTimerScore(Player player) {
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

    public static String getTimerScore(long timer) {
        long diff = Math.max(0, timer - System.currentTimeMillis());
        return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public static String getEffectScore(Player player) {

        final PvPClass pvpClass = PvPClassHandler.getPvPClass(player);

        if (pvpClass != null && pvpClass.isEnergyBased() && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().containsKey(player.getUniqueId())) {

            if (Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(player.getUniqueId()) >= System.currentTimeMillis()) {
                float diff = Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(player.getUniqueId()) - System.currentTimeMillis();

                if (diff > 0) {
                    return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000));
                }
            }

        }

        return (null);
    }

    public String getEnergyScore(Player player) {

        final PvPClass pvpClass = PvPClassHandler.getPvPClass(player);

        if (pvpClass != null && pvpClass.isEnergyBased() && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().containsKey(player.getUniqueId())) {
            float energy = Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().get(player.getUniqueId());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(energy));
            }

        }

        return (null);
    }

    public String getClassUltimateEnergyScore(Player player) {

        if (PvPClassHandler.getUltimate().containsKey(player.getUniqueId())) {
            final float ultimate = PvPClassHandler.getUltimate().get(player.getUniqueId());

            if (ultimate > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(ultimate));
            }
        }

        return null;
    }

    public String getGoppleScore(Player player) {
        if (Foxtrot.getInstance().getOppleMap().isOnCooldown(player.getUniqueId()) && Foxtrot.getInstance().getOppleMap().getCooldown(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = Foxtrot.getInstance().getOppleMap().getCooldown(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000));
            }
        }

        return (null);
    }

    public String getClassWarmupScore(Player player) {
        if (PvPClassHandler.getWarmupTasks().containsKey(player.getName())) {
            float diff = PvPClassHandler.getWarmupTasks().get(player.getName()).getTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000));
            }
        }

        return (null);
    }

    public String getBooster(Player player) {
        if (Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()) {
            float diff = Foxtrot.getInstance().getNetworkBoosterHandler().findBooster("Frenzy").getActivatedAt()+TimeUnit.HOURS.toMillis(1) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000));
            }
        }

        return (null);
    }

    public String getGemBooster(Player player) {
        long left = Foxtrot.getInstance().getGemBoosterMap().getGemBoosterMillisLeft(player);
        if (left == 0) return null;

        return TimeUtils.formatIntoHHMMSS((int) (left / 1000));
    }

    private String formatTPS(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : ((tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED)) + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

}