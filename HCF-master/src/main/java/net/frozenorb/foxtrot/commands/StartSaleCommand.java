package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

public class StartSaleCommand {
    @Command(names = {"startsale"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "type")int type) {
        // 1 = Key-Sale, 5x Keys
        // 2 = 5x Keys, 60% Sale
        // 3 = Key-Sale, 3x Keys, 50% Sale, 2x Airdrops, 2x Perk
        // 4 = Key-Sale, 5x Keys, 50% Sale, 2x Airdrops, 2x Perk
        // 5 = 3x Keys, 50% Sale

        player.chat("/customtimer create 90000 &4&l5x-KEYS");

        if (type != 5) {
            player.chat("/customtimer create 90000 &d&lKEY-SALE");
        }

        if (type != 1) {
            player.chat("/customtimer create 0 &d&lKEY-SALE");
            player.chat("/customtimer create 90000 &6&l60%-SALE");
        }

        if (type == 3 || type == 4) {
            player.chat("/customtimer create 20000 &a&l3x-PERK");
            player.chat("/customtimer create 20000 &b&l3x-AIRDROPS");
            player.chat("/customtimer create 90000 &e&l3x-MYSTERY");
        }

        if (type == 4) {
            player.chat("/customtimer create 90000 &4&l5x-KEYS");
        }
    }
}