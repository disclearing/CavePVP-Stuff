package net.frozenorb.foxtrot.gameplay.events;

import cc.fyre.proton.serialization.LocationSerializer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventDeactivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EventListener implements Listener {

    public EventListener() {
        Bukkit.getLogger().info("Creating indexes...");
        DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("KOTHCaptures");
        mongoCollection.createIndex(new BasicDBObject("Capper", 1));
        mongoCollection.createIndex(new BasicDBObject("CapperTeam", 1));
        mongoCollection.createIndex(new BasicDBObject("EventName", 1));
        Bukkit.getLogger().info("Creating indexes done.");
    }

    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().isHidden()) {
            return;
        }

        String[] messages;

        switch (event.getEvent().getName()) {
            case "EOTW":
                messages = new String[]{
                        ChatColor.RED + "███████",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + ChatColor.BOLD + "The cap point at spawn",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + ChatColor.BOLD + "is now active.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
                        ChatColor.RED + "███████"
                };

                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
                }

                break;
            case "Citadel":
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getEvent().getName(),
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;

            case "NetherCitadel":
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_RED + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_RED + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Nether Citadel]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_RED + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_RED + "Nether Citadel",
                        ChatColor.GRAY + "█" + ChatColor.DARK_RED + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_RED + "█" + ChatColor.GRAY + "█████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_RED + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;

            default:
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[KingOfTheHill]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getEvent().getName() + " KOTH",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;
        }

        if (event.getEvent().getType() == EventType.DTC) {
            messages = new String[]{
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW + "DTC",
                    ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.GOLD + "can be contested now.",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████"
            };
        }

        final String[] messagesFinal = messages;

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            player.sendMessage(messagesFinal);
        }

        // Can't forget console now can we
        for (String message : messages) {
            Foxtrot.getInstance().getLogger().info(message);
        }
    }

    @EventHandler
    public void onKOTHCaptured(final EventCapturedEvent event) {
        if (event.getEvent().isHidden()) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
        }

        String[] messages;

        if (event.getEvent().getName().contains("Citadel")) {
            messages = new String[]{
                    ChatColor.GRAY + "███████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "███████"
            };


            if (!Foxtrot.getInstance().getServerHandler().isAu()) {
                PlayerProfileAPI.addStatistic(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.CITADELS_CAPTURED, 1);
            }

            Profiles.getInstance().getReputationHandler().addReputation(event.getPlayer().getUniqueId(), event.getPlayer().getName(), 25);

            ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(event.getEvent().getName() + " KOTH");
            rewardKey.setAmount(6);

            event.getPlayer().getInventory().addItem(rewardKey);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }
        } else if (event.getEvent().getName().equalsIgnoreCase("EOTW")) {
            messages = new String[]{
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + ChatColor.BOLD + "EOTW has been",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + ChatColor.BOLD + "controlled by",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████",
            };
            if (team != null)
                team.setEotwCapped(true);
        } else if (event.getEvent().getType() == EventType.DTC) {
            messages = new String[]{
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW + ChatColor.BOLD + "DTC has been",
                    ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.YELLOW + ChatColor.BOLD + "controlled by",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████",
            };

            ItemStack kothSign = Foxtrot.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), team == null ? event.getPlayer().getName() : team.getName(), EventType.DTC);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }
        } else {
            final KOTH koth = (KOTH) event.getEvent();

            messages = new String[]{
                    ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.BLUE + event.getEvent().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!",
                    ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Awarded" + ChatColor.BLUE + " KOTH Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "."
            };

            ItemStack kothSign = Foxtrot.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), team == null ? event.getPlayer().getName() : team.getName(), EventType.KOTH);

            if (!Foxtrot.getInstance().getServerHandler().isAu()) {
                PlayerProfileAPI.addStatistic(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.KOTH_CAPTURES, 1);
            }

            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }

//            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(event.getEvent().getName() + " KOTH");

            if (Foxtrot.getInstance().getMapHandler().isKitMap() || event.getEvent().getName().equalsIgnoreCase("End") || event.getEvent().getName().equalsIgnoreCase("Hell")) {
                rewardKey.setAmount(2);
            }

            event.getPlayer().getInventory().addItem(rewardKey);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }
//            } else {
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " koth 3");
//            }

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                Foxtrot.getInstance().getGemMap().addGems(event.getPlayer().getUniqueId(), 25, true);
            }

            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
            if (playerTeam != null) {

                if (koth.isMini()) {
                    playerTeam.setMiniKothCaptures(playerTeam.getMiniKothCaptures()+1);
                } else {
                    playerTeam.setKothCaptures(playerTeam.getKothCaptures() + 1);
                }

                if (!Foxtrot.getInstance().getMapHandler().isKitMap() && (event.getEvent().getName().equalsIgnoreCase("Hell") || event.getEvent().getName().equalsIgnoreCase("End"))) {
                    playerTeam.setDoublePoints(playerTeam.getDoublePoints()+15);
                }

                if (CustomTimerCreateCommand.isDoublePoints() || playerTeam.hasNetherOutpost()) {
                    playerTeam.setDoublePoints(playerTeam.getDoublePoints() + (koth.isMini() ? 10 : 20));
                }
            }
        }

        StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(event.getPlayer());
        stats.addKothCapture();
        Profiles.getInstance().getReputationHandler().addReputation(event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10);

        final String[] messagesFinal = messages;

        new BukkitRunnable() {

            public void run() {
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    player.sendMessage("");
                    player.sendMessage(messagesFinal);
                    player.sendMessage("");
                }
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());

        // Can't forget console now can we
        // but we don't want to give console the filler.
        for (String message : messages) {
            Foxtrot.getInstance().getLogger().info(message);
        }

        final BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("EventName", event.getEvent().getName());
        dbObject.put("EventType", event.getEvent().getType().name());
        dbObject.put("CapturedAt", new Date());
        dbObject.put("Capper", event.getPlayer().getUniqueId().toString().replace("-", ""));
        dbObject.put("CapperTeam", team == null ? null : team.getUniqueId().toString());
        if (event.getEvent().getType() == EventType.KOTH) {
            dbObject.put("EventLocation", LocationSerializer.serialize(((KOTH) event.getEvent()).getCapLocation().toLocation(event.getPlayer().getWorld())));
        }

        new BukkitRunnable() {

            public void run() {
                DBCollection kothCapturesCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("KOTHCaptures");
                kothCapturesCollection.insert(dbObject);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    @EventHandler
    public void onKOTHControlLost(final KOTHControlLostEvent event) {
        if (event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] Control of " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHDeactivated(EventDeactivatedEvent event) {
        if (!(event.getEvent() instanceof KOTH)) {
            return;
        }

        // activate koths every 10m on the kitmap
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            net.frozenorb.foxtrot.gameplay.events.EventHandler eventHandler = Foxtrot.getInstance().getEventHandler();
            List<Event> localEvents = new ArrayList<>(eventHandler.getEvents());

            if (localEvents.isEmpty()) {
                return;
            }

            List<KOTH> koths = new ArrayList<>();

            for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
                if (otherKoth.isActive()) {
                    return;
                }

                if (otherKoth.getName().contains("Citadel") || otherKoth.getName().contains("Outpost")) {
                    continue;
                }

                if (otherKoth.getType() == EventType.KOTH) {
                    koths.add((KOTH) otherKoth);
                }
            }

            KOTH selected = koths.get(ThreadLocalRandom.current().nextInt(koths.size()));
            selected.activate();
        }, 10 * 60 * 20);
    }

}
