package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names={ "team", "t", "f", "faction", "fac" }, permission="")
    public static void  team(Player sender) {

        String[] msg = {

                "§8§m-----------------------------------------------------",
                "§4§lFaction Help",
                "§8§m-----------------------------------------------------",


                "§cGeneral Commands:",
                "§7/f create <teamName> §f- Create a new team.",
                "§7/f accept <teamName> §f- Accept a pending invitation.",
                "§7/f lives add <amount> §f- Irreversibly add lives to your faction.",
                "§7/f leave §f- Leave your current faction.",
                "§7/f home §f- Teleport to your faction home.",
                "§7/f stuck §f- Teleport out of enemy territory.",
                "§7/f deposit <amount/all> §f- Deposit money into your team balance.",


                "",
                "§cInformation Commands:",
                "§7/f who <player§7/faction> §f- Display a faction's information.",
                "§7/f map §f- Show nearby claims (identified by pillars).",
                "§7/f list §f- Show list of online factions.",


                "",
                "§cCaptain Commands:",
                "§7/f invite <player> §f- Invite a player to your faction.",
                "§7/f uninvite <player> §f- Revoke an invitation.",
                "§7/f invites §f- List all open invitations.",
                "§7/f kick <player> §f- Kick a player from your faction.",
                "§7/f claim §f- Create a claim for your faction.",
                "§7/f subclaim §f- Show the subclaim help page.",
                "§7/f sethome §f- Set your faction home at your current location.",
                "§7/f withdraw <amount> §f- Withdraw money from your faction's balance.",
                "§7/f announcement <message> §f- Set a faction announcement.",

                "",
                "§cCo-Leader Commands:",
                "§7/f lockclaim §f- Lock your claim during SOTW.",
                "§7/f unlockclaim §f- Unlock your claim during SOTW.",

                "",
                "§cLeader Commands:",

                "§7/f coleader <add/remove> <player> §f- Add or remove a co-leader.",
                "§7/f captain <add/remove> <player> §f- Add or remove a captain.",
                "§7/f revive <player> §f- Revive a teammate using faction lives.",
                "§7/f unclaim [all] §f- Unclaim your faction's territory.",
                "§7/f rename <newName> §f- Rename your faction.",
                "§7/f disband §f- Disband your faction.",


                "§8§m-----------------------------------------------------",



        };
        sender.sendMessage(msg);
    }

}