package net.frozenorb.foxtrot.gameplay.totem.listener;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.totem.Totem;
import net.frozenorb.foxtrot.gameplay.totem.menu.TotemMenu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TotemListener implements Listener {

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || !event.getAction().name().contains("RIGHT") || itemStack.getItemMeta() == null) {
            return;
        }

        if (!itemStack.getType().equals(Material.NAME_TAG)) {
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translate("&6&lSelect Totem Effect"))) {
            return;
        }

        final List<String> lore = itemMeta.getLore();
        String tierLine = lore.get(1);
        
        if (tierLine == null || !tierLine.contains("Tier")) {
            return;
        }
        
        int tier = Integer.parseInt(ChatColor.stripColor(tierLine).replace("Tier: ", ""));

        if (tier != 1 && tier != 2) {
            player.sendMessage(ChatColor.RED + "INVALID TOTEM! Contact an admin!");
            return;
        }

        if (itemStack.getAmount() != 1) {
            itemStack.setAmount(itemStack.getAmount()-1);
        } else {
            player.getInventory().remove(itemStack);
        }

        new TotemMenu(tier).openMenu(player);
    }

    @EventHandler
    private void onDeploy(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || itemStack.getItemMeta() == null) {
            return;
        }

        if (!itemStack.getType().equals(Material.BEACON)) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translate("&6&lTotem"))) {
            return;
        }

        final List<String> lore = itemMeta.getLore();
        String tierLine = lore.get(1);

        if (tierLine == null || !tierLine.contains("Tier")) {
            return;
        }

        event.setCancelled(true);

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this!");
            return;
        }

        final Map<ObjectId, Long> cache = Foxtrot.getInstance().getTotemHandler().getOnCooldown();

        if (cache.containsKey(team.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Your faction is still on cooldown for another " + (TimeUtils.formatIntoMMSS((int) (cache.get(team.getUniqueId()) - System.currentTimeMillis()) / 1000)));
            return;
        }

        if (Foxtrot.getInstance().getTotemHandler().getCache().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have an active totem!");
            return;
        }

        int tier = Integer.parseInt(ChatColor.stripColor(tierLine).replace("Tier: ", ""));

        if (tier != 1 && tier != 2) {
            player.sendMessage(ChatColor.RED + "INVALID TOTEM TIER! Contact an admin!");
            return;
        }

        String effectLine = lore.get(2);

        if (effectLine == null || !effectLine.contains("Effect")) {
            return;
        }

        PotionEffectType potionEffectType = null;

        String name = null;

        if (effectLine.toLowerCase().contains("strength")) {
            potionEffectType = PotionEffectType.INCREASE_DAMAGE;
            name = "&cStrength";
        } else if (effectLine.toLowerCase().contains("resistance")) {
            potionEffectType = PotionEffectType.DAMAGE_RESISTANCE;
            name = "&7Resistance";
        } else if (effectLine.toLowerCase().contains("regeneration")) {
            potionEffectType = PotionEffectType.REGENERATION;
            name = "&dRegeneration";
        }

        if (potionEffectType == null) {
            player.sendMessage(ChatColor.RED + "INVALID TOTEMEFFECT! Contact an admin!");
            return;
        }

        final Block aboveBlock = event.getClickedBlock().getRelative(BlockFace.UP);

        if (!aboveBlock.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Can't place that there as there is a block above the block you just clicked!");
            return;
        }

        final Block aboveBlock2 = aboveBlock.getRelative(BlockFace.UP);

        if (!aboveBlock2.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Can't place that there as there must be two empty blocks above the block you clicked!");
            return;
        }

        if (itemStack.getAmount() != 1) {
            itemStack.setAmount(itemStack.getAmount()-1);
        } else {
            player.getInventory().remove(itemStack);
        }

        int seconds = 10;
        int hits = 20;

        if (tier == 2) {
            seconds = 15;
            hits = 30;
        }

        final Totem totem = new Totem(player.getUniqueId(), aboveBlock.getLocation(), potionEffectType, System.currentTimeMillis(), System.currentTimeMillis()+ TimeUnit.SECONDS.toMillis(seconds), hits);

        aboveBlock.setType(Material.FENCE);
        aboveBlock2.setType(Material.BEACON);

        for (Player onlineMember : team.getOnlineMembers()) {
            onlineMember.sendMessage("");
            onlineMember.sendMessage(ChatColor.translate("&4&lTotems"));
            onlineMember.sendMessage(player.getName() + " &7has placed a totem at &f" + aboveBlock.getLocation().getBlockX() + ", " + aboveBlock.getLocation().getBlockZ());
            onlineMember.sendMessage(ChatColor.translate("&aYour team will now receive " + name + " I &afor " + seconds + " seconds!"));
            onlineMember.sendMessage("");
        }

        Foxtrot.getInstance().getTotemHandler().getCache().put(player.getUniqueId(), totem);
        Foxtrot.getInstance().getTotemHandler().getOnCooldown().put(team.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(3));
        
        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            for (Player onlineMember : team.getOnlineMembers()) {
                onlineMember.sendMessage("");
                onlineMember.sendMessage(ChatColor.RED + "Your team is no longer on a Totem cooldown.");
                onlineMember.sendMessage("");
            }
            Foxtrot.getInstance().getTotemHandler().getOnCooldown().remove(team.getUniqueId());
        }, 20*60*3);
    }

    @EventHandler
    private void onTap(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || !event.getAction().equals(Action.LEFT_CLICK_BLOCK) || itemStack.getItemMeta() == null) {
            return;
        }

        final Block tapped = event.getClickedBlock();

        if (tapped == null) {
            return;
        }

        if (!tapped.getType().equals(Material.BEACON)) {
            return;
        }

        final Totem totem = Foxtrot.getInstance().getTotemHandler().getCache().values().stream().filter(it -> it.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().equals(tapped.getLocation())).findFirst().orElse(null);

        if (totem == null) {
            return;
        }

        if (totem.getHitsRemaining() == 1) {
            totem.getLocation().getWorld().playSound(totem.getLocation(), Sound.ANVIL_BREAK, 1,1);
            Foxtrot.getInstance().getTotemHandler().getCache().remove(totem.getPlacedBy());
            final Location location = totem.getLocation();

            location.getBlock().setType(Material.AIR);
            location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
            return;
        }

        totem.setHitsRemaining(totem.getHitsRemaining()-1);
        Foxtrot.getInstance().getTotemHandler().getCache().replace(totem.getPlacedBy(), totem);
        player.sendMessage(ChatColor.RED.toString() + totem.getHitsRemaining() + " hits remaining!");
    }

}
