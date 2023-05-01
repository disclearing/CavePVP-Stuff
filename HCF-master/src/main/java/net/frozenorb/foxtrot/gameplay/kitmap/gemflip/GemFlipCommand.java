package net.frozenorb.foxtrot.gameplay.kitmap.gemflip;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipSide;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu.WagerListMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu.WagerMenu;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.HashSet;
import java.util.Set;

public class GemFlipCommand {
    public static Set<Pair<Integer, GemFlipSide>> entryInfo;

    static {
        entryInfo = new HashSet<>();
        entryInfo.add(new Pair<>(3, GemFlipSide.HEADS));
        entryInfo.add(new Pair<>(5, GemFlipSide.TAILS));
    }

    @Command(names = {"gemflip add", "gemflip bet", "gemflip wager", "coinflip add", "coinflip bet", "coinflip wager", "gf add", "gf bet", "gf wager", "cf add", "cf wager", "cf bet"}, permission = "")
    public static void gemFlip(Player player, @Parameter(name = "gems")int gems) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        final GemFlipHandler gemFlipHandler = Foxtrot.getInstance().getGemFlipHandler();
        long balance = Foxtrot.getInstance().getGemMap().getGems(player.getUniqueId());

       if (gems < 10) {
           player.sendMessage(ChatColor.RED + "You must have a minimum of 10 Gems to wager!");
           return;
       }

        if (gemFlipHandler.getEntry(player) != null || gemFlipHandler.getActiveWager(player) != null) {
            player.sendMessage(ChatColor.RED + "You already have an active gem flip wager.");
            return;
        }

        if (gems > balance) {
            player.sendMessage(ChatColor.RED + "Insufficient Gems!");
            return;
        }

        if(Foxtrot.getInstance().getGemFlipHandler().getQueue().size() >= 8) {
            player.sendMessage(ChatColor.RED + "The wager queue is currently full. Please try again later.");
            return;
        }

        new WagerMenu(entryInfo, gems).openMenu(player);
    }

    @Command(names = {"gemflip", "gemflips", "coinflip", "coinflips", "cf", "gf"}, permission = "")
    public static void execute(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        new WagerListMenu().openMenu(player);
    }

    @Command(names = {"gemflip cancel", "coinflip cancel", "gf cancel", "cf cancel", "gf remove"}, permission = "")
    public static void cancel(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        GemFlipEntry entry = Foxtrot.getInstance().getGemFlipHandler().removeEntry(player);
        if (entry == null) {
            player.sendMessage(ChatColor.RED + "You do not have an active gem flip wager.");
        } else {
            player.sendMessage(ChatColor.RED + "You have cancelled your gem flip wager.");

            Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), entry.getAmount(), true);
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
        }
    }

    @Command(names = {"gemflip total"}, permission = "op")
    public static void total(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GREEN + "Total Wagered: " + ChatColor.GREEN + Foxtrot.getInstance().getGemFlipHandler().getTotalWagered());
    }
}
