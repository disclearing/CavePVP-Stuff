package net.frozenorb.foxtrot.team.commands.team.chatspy;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyCommand {

    @Command(names = {"team chatspy", "t chatspy", "f chatspy", "faction chatspy", "fac chatspy"}, permission = "foxtrot.chatspy")
    public static void teamChatSpy(Player sender) {
        String[] msg = {
                "§9§m-----------------------------------------------------",
                "§c/f chatspy list - views factions who you are spying on.",
                "§c/f chatspy add - starts spying on a faction.",
                "§c/f chatspy del - stop spying on a faction.",
                "§c/f chatspy clear - stops spying on all factions.",
                "§c/f chatspy list - views factions who you are spying on.",
                "§9§m-----------------------------------------------------"
        };

        sender.sendMessage(msg);

    }

}