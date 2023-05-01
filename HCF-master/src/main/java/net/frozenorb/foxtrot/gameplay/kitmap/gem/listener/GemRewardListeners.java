package net.frozenorb.foxtrot.gameplay.kitmap.gem.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Chance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GemRewardListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (Foxtrot.getInstance().getGemHandler().getOreChances().containsKey(event.getBlock().getType())) {
            event.getBlock().setMetadata("GEM_ANTI_DUPE", new FixedMetadataValue(Foxtrot.getInstance(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("GEM_ANTI_DUPE")) {
            return;
        }

        if (Foxtrot.getInstance().getGemHandler().getOreChances().containsKey(event.getBlock().getType())) {
            if (Chance.percent(Foxtrot.getInstance().getGemHandler().getOreChances().get(event.getBlock().getType()))) {
                long added = Foxtrot.getInstance().getGemMap().addGems(event.getPlayer().getUniqueId(), 1);
                String gem = added > 1 ? "Gems" : "Gem";
                event.getPlayer().sendMessage(CC.DARK_RED + CC.BOLD + "Cave" + CC.DARK_GRAY + " ┃ "
                        + CC.GREEN + "You found " + CC.DARK_GREEN + "+" + added + " " + gem + CC.GREEN + " while mining!");
            }
        }
    }
}
