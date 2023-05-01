package net.frozenorb.foxtrot.listener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.serialization.PlayerInventorySerializer;
import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.team.Team;
import net.valorhcf.ThreadingManager;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class WebsiteListener implements Listener {
    private Foxtrot instance;

    public WebsiteListener(Foxtrot instance) {
        this.instance = instance;
        Bukkit.getLogger().info("Creating indexes...");
        DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");
        
        mongoCollection.createIndex(new BasicDBObject("uuid", 1));
        mongoCollection.createIndex(new BasicDBObject("killerUUID", 1));
        mongoCollection.createIndex(new BasicDBObject("ip", 1));
        Bukkit.getLogger().info("Creating indexes done.");
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
            return;
        }

        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        final BasicDBObject playerDeath = new BasicDBObject();

        playerDeath.put("_id", UUID.randomUUID().toString().substring(0, 7));

        if (event.getEntity().getKiller() != null) {
            playerDeath.append("healthLeft", (int) killer.getHealth());
            playerDeath.append("killerUUID", killer.getUniqueId().toString());
            playerDeath.append("killerLastUsername", killer.getName());
            playerDeath.append("killerInventory", PlayerInventorySerializer.getInsertableObject(killer));
            playerDeath.append("killerPing", PlayerUtils.getPing(killer));
            playerDeath.append("killerHunger", killer.getFoodLevel());
            playerDeath.append("killerLocation", LocationSerializer.serialize(killer.getLocation()));

            final Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);

            playerDeath.append("killerTeam", (killerTeam == null ? "None Found" : killerTeam.getName()));
            playerDeath.append("killerDTR", (killerTeam == null ? "No Faction" : killerTeam.formatDTR()));
        }

        if (event.getEntity().getLastDamageCause() == null || event.getEntity().getLastDamageCause().getCause() == null) {
            playerDeath.append("reason", "None found");
        } else {
            playerDeath.append("reason", event.getEntity().getLastDamageCause().getCause().toString());
        }

        playerDeath.append("playerInventory", PlayerInventorySerializer.getInsertableObject(player));
        playerDeath.append("uuid", player.getUniqueId().toString().replace("-", ""));
        playerDeath.append("lastUsername", player.getName());
        playerDeath.append("hunger", player.getFoodLevel());
        playerDeath.append("ping", PlayerUtils.getPing(player));
        playerDeath.append("when", new Date());
        playerDeath.append("systemTime", System.currentTimeMillis());

        final Integer[] array = ThreadingManager.getTickCounter().getTicksPerSecond();
        final Integer last = array[array.length - 1];

        playerDeath.append("tps", last);

        final Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        playerDeath.append("playerTeam", (playerTeam == null ? "None Found" : playerTeam.getName()));
        playerDeath.append("beforeDTR", (playerTeam == null ? "No Faction" : playerTeam.formatDTR()));
        playerDeath.append("afterDTR", (playerTeam == null ? 0 : playerTeam.getDTR()-this.instance.getServerHandler().getDTRLoss(player.getLocation())));

        playerDeath.append("playerLocation", LocationSerializer.serialize(player.getLocation()));

        new BukkitRunnable() {

            public void run() {
                Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case LAPIS_ORE:
            case EMERALD_ORE:
                event.getBlock().setMetadata("PlacedByPlayer", new FixedMetadataValue(Foxtrot.getInstance(), true));
                break;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) || event.getBlock().hasMetadata("PlacedByPlayer")) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                Foxtrot.getInstance().getDiamondMinedMap().setMined(event.getPlayer(), Foxtrot.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case GOLD_ORE:
                Foxtrot.getInstance().getGoldMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getGoldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case IRON_ORE:
                Foxtrot.getInstance().getIronMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getIronMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case COAL_ORE:
                Foxtrot.getInstance().getCoalMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getCoalMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                Foxtrot.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case LAPIS_ORE:
                Foxtrot.getInstance().getLapisMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getLapisMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case EMERALD_ORE:
                Foxtrot.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
        }
    }

}