package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavepvp.suge.Suge;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomCommands {
    public static List<String> noKeyList = Arrays.asList("Starter", "Brewer", "Custom", "Trapper", "Pyromaniac", "Raider");

    @Command(names = {"randomclickablekit"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")int amount) {
        final List<String> clickableKits = Suge.getInstance().getKitHandler().getKits().keySet().stream().filter(it -> noKeyList.stream().noneMatch(it::equalsIgnoreCase)).collect(Collectors.toList());

        for (int i = 0; i < amount; i++) {
            String clickableKit = clickableKits.get(ThreadLocalRandom.current().nextInt(0, clickableKits.size()));

            Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "clickablekit give " + clickableKit + " " + target.getName());
        }
    }


    @Command(names = {"randomownerkeys"}, permission = "op")
    public static void ownerKeys(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")int amount) {
        final List<String> cache = Arrays.asList("Resucting", "NotRamix", "Dylan", "Headed", "iMakeMcVids");

        String clickableKit = cache.get(ThreadLocalRandom.current().nextInt(0, cache.size()));

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "cr givekey " + target.getName() + " " + clickableKit + " " + amount);
    }

    @Command(names = {"randompartnerkeys"}, permission = "op")
    public static void partnerKeys(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")int amount) {
        final List<String> cache = Arrays.stream(PartnerType.values()).map(PartnerType::getCrateName).collect(Collectors.toList());

        String clickableKit = cache.get(ThreadLocalRandom.current().nextInt(0, cache.size()));

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "cr givekey " + target.getName() + " Partner " + amount);
    }

}
