package net.frozenorb.foxtrot.gameplay.events.mini.command;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEventsHandler;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreGetter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MiniEventCommand {

    @Command(names = {"minievent"}, permission = "")
    public static void execute(Player player) {
        final MiniEvent miniEvent = Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent();

        if (miniEvent == null) {
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Mini Events");
            player.sendMessage(ChatColor.GRAY + "Mini Events consist of a challenge that the whole server is tasked to complete in a set amount of time.");
            player.sendMessage(ChatColor.GRAY + "The top 3 players at the end of the time will receive OP rewards.");
            player.sendMessage("");
            player.sendMessage(ChatColor.translate("&4&lEvent Schedule"));

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                player.sendMessage(ChatColor.translate("&4&l┃ &fRage Event: &cSunday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fKing Event: &4Monday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fMayhem Event: &dTuesday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fTraveller Event: &bWednesday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fStabber Event: &cThursday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fShooter Event: &cFriday at 4 PM EST"));
                player.sendMessage("");
                player.sendMessage(ChatColor.translate("&4&lEvent Rewards"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f1st Place: &4&l2x Treasure Chests &b&l5x Airdrops &fand &a&l1000 Gems"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f2nd Place: &b&l3x Airdrops &fand &a&l1000 Gems"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f3rd Place: &a&l500 Gems"));
            } else {
                player.sendMessage(ChatColor.translate("&4&l┃ &fRage Event: &cSunday at 3 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fRaider Event: &4Monday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fMayhem Event: &dTuesday at 12 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fTraveller Event: &bWednesday at 4 PM EST"));
                player.sendMessage(ChatColor.translate("&4&l┃ &fRage Event: &cThursday at 2 PM EST"));
                player.sendMessage("");
                player.sendMessage(ChatColor.translate("&4&lEvent Rewards"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f1st Place: &4&l2x Omega Chests&f and &b&l10x Airdrops"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f2nd Place: &b&l10x Airdrops"));
                player.sendMessage(ChatColor.translate("&4&l┃ &f3rd Place: &6&l10x Halloween Keys"));
            }
            player.sendMessage("");
            return;
        }

        final List<UUID> placing = new ArrayList<>(miniEvent.getSortedList());

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + miniEvent.getObjective() + " Event");
        for (String s : miniEvent.getDescription()) {
            player.sendMessage(ChatColor.GRAY + s);
        }
        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&l┃ &fPlacing: &c" + miniEvent.findPlacing(player.getUniqueId())));
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Leaderboards (Hover for rewards)");

        final UUID firstPlace = placing.isEmpty() ? null : placing.remove(0);
        final UUID secondPlace = placing.isEmpty() ? null : placing.remove(0);
        final UUID thirdPlace = placing.isEmpty() ? null : placing.remove(0);

        final String first = firstPlace == null ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(firstPlace);
        final String second = secondPlace == null ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(secondPlace);
        final String third = thirdPlace == null  ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(thirdPlace);

        List<String> tooltip = Arrays.asList(ChatColor.translate("&4&lRewards"),
                ChatColor.translate("&4&l┃ &f1st Place: &e&l2x Omega Chests &fand &b&l10x Airdrops"),
                ChatColor.translate("&4&l┃ &f2nd Place: &b&l10x Airdrops"),
                ChatColor.translate("&4&l┃ &f3rd Place: &6&l10x Halloween Keys"));

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            tooltip = Arrays.asList(ChatColor.translate("&4&lRewards"),
                    ChatColor.translate("&4&l┃ &f1st Place: &4&l2x Treasure Chests &b&l5x Airdrops &fand &a&l1000 Gems"),
                    ChatColor.translate("&4&l┃ &f2nd Place: &b&l3x Airdrops &fand &a&l1000 Gems"),
                    ChatColor.translate("&4&l┃ &f3rd Place: &a&l500 Gems"));
        }

        new FancyMessage(ChatColor.translate("&4&l┃ &f1st Place: &c" + first)).tooltip(tooltip).send(player);
        new FancyMessage(ChatColor.translate("&4&l┃ &f2nd Place: &c" + second)).tooltip(tooltip).send(player);
        new FancyMessage(ChatColor.translate("&4&l┃ &f3rd Place: &c" + third)).tooltip(tooltip).send(player);
        player.sendMessage("");
    }

    @Command(names = {"minievent start", "minievent activate"}, permission = "op")
    public static void start(Player player, @Parameter(name = "taskName", defaultValue = "R_A_N_D_O_M")String taskName) {
        final MiniEventsHandler miniEventsHandler = Foxtrot.getInstance().getMiniEventsHandler();

        if (taskName.equalsIgnoreCase("R_A_N_D_O_M")) {
            miniEventsHandler.activateRandom();
            return;
        }

        final MiniEvent miniEvent = miniEventsHandler.findEvent(taskName);

        miniEventsHandler.setActiveEvent(miniEvent);
        miniEvent.activate();
    }

    @Command(names = {"minievent stop", "minievent deactivate"}, permission = "op")
    public static void stop(Player player) {
        final MiniEventsHandler miniEventsHandler = Foxtrot.getInstance().getMiniEventsHandler();

        miniEventsHandler.getActiveEvent().deactivate(false);
    }
}
