package net.frozenorb.foxtrot.team.commands.team.subclaim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TeamSubclaimCommand implements Listener {

    @Getter
    private static Map<String, Selection> selections = new HashMap<>();
    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_SPADE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oSubclaim Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect subclaim's corners"

        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names = {"team subclaim", "t subclaim", "f subclaim", "faction subclaim", "fac subclaim", "team sub", "t sub", "f sub", "faction sub", "fac sub"}, permission = "")
    public static void teamSubclaim(Player sender) {

        String[] msg = {
                "§8§m-----------------------------------------------------",
                "§c§lSubclaim Help",

                "",
                "§7/f subclaim start §f- start the subclaiming process.",
                "§7/f subclaim map §f- toggle a visual subclaim map.",
                "§7/f subclaim create <subclaim> §f- create a subclaim.",
                "§7/f subclaim addplayer <subclaim> <player> §f- add a player to a subclaim.",
                "§7/f subclaim removeplayer <subclaim> <player> §f- remove a player from a subclaim.",
                "§7/f subclaim list §f- view all subclaims.",
                "§7/f subclaim info <subclaim> §f- view info about a subclaim.",
                "/f subclaim unclaim <subclaim> <player> §f- unclaim a subclaim.",

                "§8§m-----------------------------------------------------"
        };

        sender.sendMessage(msg);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == SELECTION_WAND.getType()) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Subclaim")) {
                event.setCancelled(true);

                if (team != null) {
                    Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                    if (subclaim != null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "(" + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ") is a part of " + subclaim.getName() + "!");
                        return;
                    }

                    if (LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation()) != team) {
                        event.getPlayer().sendMessage(ChatColor.RED + "This block is not a part of your factions' territory!");
                        return;
                    }
                }

                Selection selection = new Selection(null, null);

                if (selections.containsKey(event.getPlayer().getName())) {
                    selection = selections.get(event.getPlayer().getName());
                }

                int set;

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    set = 2;
                    selection.setLoc1(event.getClickedBlock().getLocation());
                } else {
                    set = 1;
                    selection.setLoc2(event.getClickedBlock().getLocation());
                }

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Set subclaim's location " + ChatColor.LIGHT_PURPLE + set + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
                selections.put(event.getPlayer().getName(), selection);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(TeamSubclaimCommand.SELECTION_WAND)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
    }

    @Data
    @AllArgsConstructor
    public static class Selection {

        private Location loc1;
        private Location loc2;

        public boolean isComplete() {
            return (loc1 != null && loc2 != null);
        }

    }

}
