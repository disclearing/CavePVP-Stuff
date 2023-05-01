package net.frozenorb.foxtrot.gameplay.events.dtc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

public class DTCCreateCommand {

    @Command(names={ "DTC Create" }, permission="foxtrot.dtc.admin")
    public static void kothCreate(Player sender, @Parameter(name="dtc") String koth) {
        new DTC(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
    }

}
