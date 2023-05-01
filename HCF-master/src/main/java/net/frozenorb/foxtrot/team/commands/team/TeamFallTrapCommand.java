package net.frozenorb.foxtrot.team.commands.team;

import java.util.Arrays;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamFallTrapCommand {

    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_HOE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oFallTrap Wand");
        meta.setLore(Arrays.asList(
                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect falltrap's corners",
                "",
                "§eRight Click §6Air",
                "§b- §fCancel current selection",
                "",
                "§9Crouch §eLeft Click §6Block/Air",
                "§b- §fPurchase current selection"
        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names = {"falltrap wand", "team falltrap wand", "t falltrap wand", "f falltrap wand", "faction falltrap wand"}, permission = "")
    public static void teamFallTrapWand(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            new BukkitRunnable() {
                @Override
                public void run() {
                    sender.getInventory().addItem(SELECTION_WAND.clone());
                }
            }.runTaskLater(Foxtrot.getInstance(), 1L);

            new VisualClaim(sender, VisualClaimType.FALL_TRAP, false).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}
