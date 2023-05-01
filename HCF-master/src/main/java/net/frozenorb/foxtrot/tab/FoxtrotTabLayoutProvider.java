package net.frozenorb.foxtrot.tab;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.tab.construct.TabLayout;
import cc.fyre.proton.tab.provider.LayoutProvider;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamListCommand;
import net.frozenorb.foxtrot.util.PlayerDirection;

public class FoxtrotTabLayoutProvider implements LayoutProvider {

    private LinkedHashMap<Team, Integer> cachedTeamList = Maps.newLinkedHashMap();
    long cacheLastUpdated;

    @Override
    public TabLayout provide(Player player) {
        TabListMode mode = Foxtrot.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId());

        TabLayout layout = TabLayout.create(player);

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        String serverName = Foxtrot.getInstance().getServerHandler().getTabServerName();
        String titleColor = Foxtrot.getInstance().getServerHandler().getTabSectionColor();
        String infoColor = Foxtrot.getInstance().getServerHandler().getTabInfoColor();

        layout.set(1, 0, serverName);

        int y = -1;

        layout.set(0, ++y, titleColor + "Home:");

        if (team != null && team.getHQ() != null) {
            String homeLocation = infoColor + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
            layout.set(0, ++y, homeLocation);
        } else {
            if (team == null) {
                layout.set(0, ++y, infoColor + "Create a team");
                layout.set(0, ++y, infoColor + "by typing");
                layout.set(0, ++y, "&f/t create [name]");
            } else {
                layout.set(0, ++y, infoColor + "Not Set");
            }
        }

        ++y; // blank

        if (team != null) {
            int balance = (int) team.getBalance();
            layout.set(0, ++y, titleColor + "Team Info:");

            String dtrFormat = team.formatDTR();

            if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
                int currentHearts = (int) Math.ceil(team.getDTR());

                dtrFormat = team.getDTRColor().toString() + currentHearts + "❤" + team.getDTRSuffix();
            }

            layout.set(0, ++y, infoColor + "DTR: &f" + dtrFormat);
            layout.set(0, ++y, infoColor + "Online: &f" + team.getOnlineMemberAmount() + "/" + team.getMembers().size());
            layout.set(0, ++y, infoColor + "Balance: &2$&a" + balance);
            ++y; // blank
        }

        final StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

        layout.set(0, ++y, titleColor + "Statistics:");

        if (stats != null) {
            layout.set(0, ++y, infoColor + "Kills: &f" + stats.getKills());
            layout.set(0, ++y, infoColor + "Deaths: &f" + stats.getDeaths());
        } else {
            layout.set(0, ++y, infoColor + "Kills: &f" + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()));
            layout.set(0, ++y, infoColor + "Deaths: &f" + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()));
        }

        ++y; // blank

        layout.set(0, ++y, titleColor + "Your Location:");

        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().contains("citadel")) {
            location = titleColor + "Citadel";
        } else {
            location = ChatColor.DARK_RED + "WarZone";
        }

        layout.set(0, ++y, location);

        String direction = PlayerDirection.getCardinalDirection(player);
        if (direction != null) {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") &f[" + direction + "]");
        } else {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
        }
        ++y; // blank

        if (team != null) {
            layout.set(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 5 : 2, titleColor + team.getName());

            String watcherName = ChatColor.DARK_GREEN + player.getName();
            if (team.isOwner(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCoLeader(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCaptain(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "*";
            }

            layout.set(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 6 : 3, watcherName, ((CraftPlayer) player).getHandle().ping); // the viewer is always first on the list

            Player owner = null;
            List<Player> coleaders = Lists.newArrayList();
            List<Player> captains = Lists.newArrayList();
            List<Player> members = Lists.newArrayList();
            for (Player member : team.getOnlineMembers()) {
                if (team.isOwner(member.getUniqueId())) {
                    owner = member;
                } else if (team.isCoLeader(member.getUniqueId())) {
                    coleaders.add(member);
                } else if (team.isCaptain(member.getUniqueId())) {
                    captains.add(member);
                } else {
                    members.add(member);
                }
            }

            int x = 1;
            y = mode == TabListMode.DETAILED ? 4 : 7;

            // then the owner
            if (owner != null && owner != player) {
                layout.set(x, y, ChatColor.DARK_GREEN + owner.getName() + ChatColor.GRAY + "**", ((CraftPlayer) owner).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // then the coleaders
            for (Player coleader : coleaders) {
                if (coleader == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + coleader.getName() + ChatColor.GRAY + "**", ((CraftPlayer) coleader).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }


            // then the captains
            for (Player captain : captains) {
                if (captain == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + captain.getName() + ChatColor.GRAY + "*", ((CraftPlayer) captain).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // and only then, normal members.
            for (Player member : members) {
                if (member == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + member.getName(), ((CraftPlayer) member).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // basically, if we're not on the third column yet, set the y to 0, and go to the third column.
            // if we're already there, just place whatever we got under the last player's name
            if (x < 2) {
                y = 0;
            } else {
                y++; // comment this out if you don't want a space in between the last player and the info below:
            }
        }

        if (team == null) {
            y = 0;
        }

        String endPortalLocation = Foxtrot.getInstance().getMapHandler().getEndPortalLocation();
        if (endPortalLocation != null && (!endPortalLocation.equals("N/A") && !endPortalLocation.isEmpty())) {
            layout.set(2, y, titleColor + "End Portals:");
            layout.set(2, ++y,ChatColor.WHITE + endPortalLocation);
            layout.set(2, ++y, infoColor + "in each quadrant");

            ++y;
            layout.set(2, ++y, titleColor + "Map Kit:");
        } else {
            layout.set(2, y, titleColor + "Map Kit:");
        }

        layout.set(2, ++y, infoColor + Foxtrot.getInstance().getServerHandler().getEnchants());

        ++y;
        layout.set(2, ++y, titleColor + "Border:");
        layout.set(2, ++y, infoColor + BorderListener.BORDER_SIZE);

        ++y;
        layout.set(2, ++y, titleColor + "Players:");
        layout.set(2, ++y, infoColor + (Bukkit.getOnlinePlayers().size()+Universe.fakePlayers));

        KOTH activeKOTH = null;
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!(event instanceof KOTH)) continue;
            KOTH koth = (KOTH) event;
            if (koth.isActive() && !koth.isHidden()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            Date now = new Date();

            String nextKothName = null;
            Date nextKothDate = null;

            for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                if (entry.getKey().toDate().after(now)) {
                    if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                        nextKothName = entry.getValue();
                        nextKothDate = entry.getKey().toDate();
                    }
                }
            }

            ++y;

            if (nextKothName != null) {
                layout.set(2, ++y, titleColor + "Next KOTH:");
                layout.set(2, ++y, nextKothName);

                Event event = Foxtrot.getInstance().getEventHandler().getEvent(nextKothName);

                if (event instanceof KOTH) {
                    KOTH koth = (KOTH) event;
                    layout.set(2, ++y, infoColor + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ()); // location

                    int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000);
                    layout.set(2, ++y, titleColor + "Goes active in:");

                    String time = formatIntoDetailedString(seconds)
                            .replace("minutes", "min").replace("minute", "min")
                            .replace("seconds", "sec").replace("second", "sec");

                    layout.set(2, ++y, infoColor + time);
                }
            }
        } else {
            ++y;
            layout.set(2, ++y, titleColor + activeKOTH.getName());
            layout.set(2, ++y, ChatColor.WHITE + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));
            layout.set(2, ++y, infoColor + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ()); // location
        }

        // faction list (10 entries)
        boolean shouldReloadCache = cachedTeamList == null || (System.currentTimeMillis() - cacheLastUpdated > 2000);

        y = 1;

        Map<Team, Integer> teamPlayerCount = new HashMap<>();

        if (shouldReloadCache) {
            // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
            for (Player other : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (ModHandler.INSTANCE.isInVanish(other.getUniqueId())) {
                    continue;
                }

                Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(other);

                if (playerTeam != null) {
                    if (teamPlayerCount.containsKey(playerTeam)) {
                        teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                    } else {
                        teamPlayerCount.put(playerTeam, 1);
                    }
                }
            }
        }

        LinkedHashMap<Team, Integer> sortedTeamPlayerCount;

        if (shouldReloadCache) {
            sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount);
            cachedTeamList = sortedTeamPlayerCount;
            cacheLastUpdated = System.currentTimeMillis();
        } else {
            sortedTeamPlayerCount = cachedTeamList;
        }

        int index = 0;

        boolean title = false;

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
            index++;

            if (index > 19) {
                break;
            }

            if (!title) {
                title = true;
                layout.set(3, 0, titleColor + "Team List:");
            }

            String teamName = teamEntry.getKey().getName();
            String teamColor = teamEntry.getKey().isMember(player.getUniqueId()) ? ChatColor.GREEN.toString() : infoColor;

            if (teamName.length() > 10) teamName = teamName.substring(0, 10);

            layout.set(3, y++, teamColor + teamName + ChatColor.GRAY + " (" + teamEntry.getValue() + ")");
        }

        return layout;
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs <= 60) {
            return "1 minute";
        } else {
            int remainder = secs % 86400;
            int days = secs / 86400;
            int hours = remainder / 3600;
            int minutes = remainder / 60 - hours * 60;
            String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
            String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
            String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
            return (fDays + fHours + fMinutes).trim();
        }

    }

}