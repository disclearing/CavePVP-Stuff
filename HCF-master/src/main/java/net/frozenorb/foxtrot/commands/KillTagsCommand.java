package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.killtags.KillTagMenu;
import net.frozenorb.foxtrot.server.HelpfulColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KillTagsCommand {

    @Command(names = {"killtags", "killtag", "kills tags"},permission = "")
    public static void execute(Player player) {
        new KillTagMenu().openMenu(player);
    }
}
