package net.frozenorb.foxtrot.team.commands.pvp;

import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class PvPCommand {

    @Command(names={ "pvptimer", "timer", "pvp" }, permission="")
    public static void pvp(Player sender) {
        String[] msges = {
                "§8§m-----------------------------------------------------",
                "§c§lPvP Help",
                "",
                "§7/pvp lives [target] §f- Display the amount of lives a player has.",
                "§7/pvp revive <player> §f- Revive a player.",
                "§7/pvp time §f- Display time left on PVP Timer.",
                "§7/pvp enable §f- Remove PVP Timer.",
                "§8§m-----------------------------------------------------",
        };

        sender.sendMessage(msges);
    }

}