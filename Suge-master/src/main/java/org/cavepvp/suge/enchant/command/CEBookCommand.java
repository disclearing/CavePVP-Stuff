package org.cavepvp.suge.enchant.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.util.RomanUtil;

import java.util.Map;

public class CEBookCommand {

    @Command(names = {"ce givebook"}, permission = "command.ce.givebook", hidden = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "enchant")String enchant, @Parameter(name = "level")int level) {

        final CustomEnchant customEnchant = Suge.getInstance().getEnchantHandler().findCustomEnchant(enchant);

        if (customEnchant == null) {
            sender.sendMessage(ChatColor.RED + "That enchant could not be found!");
            return;
        }

        if (level > 5) {
            sender.sendMessage(ChatColor.RED + "Number must be below 6.");
            return;
        }

        final String romanNumber = RomanUtil.map.entrySet().stream().filter(it -> it.getValue() == level).map(Map.Entry::getKey).findFirst().orElse(null);

        if (romanNumber == null) {
            sender.sendMessage(ChatColor.RED + "That roman number could not be found! Try between 1-5.");
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        for (Material material : customEnchant.getApplicableItems()) {
            stringBuilder.append(stringBuilder.length() == 0 ? "" : ", ").append(WordUtils.capitalizeFully(material.name()));
        }

        target.getInventory().addItem(Suge.getInstance().getEnchantHandler().getEnchantBookHandler().getBook(customEnchant, romanNumber).clone());
    }
}
