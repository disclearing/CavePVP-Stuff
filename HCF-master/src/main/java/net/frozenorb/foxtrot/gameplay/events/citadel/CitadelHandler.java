package net.frozenorb.foxtrot.gameplay.events.citadel;

import cc.fyre.proton.serialization.LocationSerializer;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.citadel.listeners.CitadelListener;
import net.frozenorb.foxtrot.gameplay.events.citadel.tasks.CitadelSaveTask;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.InventorySerialization;
import net.frozenorb.foxtrot.util.LocationUtil;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CitadelHandler {
    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    private File citadelInfo;
    @Getter
    private Set<ObjectId> cappers = new HashSet<>();
    @Getter private Date lootable;

    @Getter private Set<Location> citadelChests = new HashSet<>();
    @Getter private List<ItemStack> citadelLoot = new ArrayList<>();

    public CitadelHandler() {
        citadelInfo = new File(Foxtrot.getInstance().getDataFolder(), "citadelInfo.json");

        loadCitadelInfo();
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), Foxtrot.getInstance());

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        (new CitadelSaveTask()).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 20 * 60 * 5);
    }

    public KOTH getEvent() {
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event instanceof KOTH && event.getName().equalsIgnoreCase("Citadel")) {
                return (KOTH) event;
            }
        }
        return null;
    }

    public boolean isActive() {
        return getEvent() != null;
    }

    public void loadCitadelInfo() {

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        try {
            if (!citadelInfo.exists() && citadelInfo.createNewFile()) {
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("cappers", new HashSet<>());
                dbo.put("lootable", new Date());
                dbo.put("chests", new BasicDBList());
                dbo.put("loot", new BasicDBList());

                FileUtils.write(citadelInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(citadelInfo));

            if (dbo != null) {
                // Conversion
                if (dbo.containsField("capper")) {
                    cappers.add(new ObjectId(dbo.getString("capper")));
                }

                for (String capper : (List<String>) dbo.get("cappers")) {
                    cappers.add(new ObjectId(capper));
                }

                this.lootable = dbo.getDate("lootable");

                BasicDBList chests = (BasicDBList) dbo.get("chests");
                BasicDBList loot = (BasicDBList) dbo.get("loot");

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    citadelChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }

                for (Object lootObj : loot) {
                    citadelLoot.add(InventorySerialization.deserialize((BasicDBObject) lootObj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCitadelInfo() {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        try {
            BasicDBObject dbo = new BasicDBObject();

            dbo.put("cappers", cappers.stream().map(ObjectId::toString).collect(Collectors.toList()));
            dbo.put("lootable", lootable);

            BasicDBList chests = new BasicDBList();
            BasicDBList loot = new BasicDBList();

            for (Location citadelChest : citadelChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", LocationSerializer.serialize(citadelChest));
                chests.add(chest);
            }

            for (ItemStack lootItem : citadelLoot) {
                loot.add(InventorySerialization.serialize(lootItem));
            }

            dbo.put("chests", chests);
            dbo.put("loot", loot);

            citadelInfo.delete();
            FileUtils.write(citadelInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCappers() {
        this.cappers.clear();
    }

    public void addCapper(ObjectId capper) {
        this.cappers.add(capper);
        this.lootable = generateLootableDate();

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(new CitadelCapturedEvent(capper));
        saveCitadelInfo();
    }

    public boolean canLootCitadel(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return false;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return true;
        }

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) <= 14) {
            return true;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        return ((team != null && cappers.contains(team.getUniqueId())));
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.TUESDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);

        // 11:59 PM
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);

        return (date.getTime());
    }

    public void scanLoot() {
        citadelChests.clear();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmask.CITADEL)) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            citadelChests.add(location);
                        }
                    }
                }
            }
        }
    }

    public int respawnCitadelChests() {
        int respawned = 0;

        for (Location chest : citadelChests) {
            if (respawnCitadelChest(chest)) {
                respawned++;
            }
        }

        return (respawned);
    }

    public boolean respawnCitadelChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            chest.getBlockInventory().clear();
            chest.getBlockInventory().addItem(citadelLoot.get(ThreadLocalRandom.current().nextInt(citadelLoot.size())));
            return (true);
        } else {
            Foxtrot.getInstance().getLogger().warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
            return (false);
        }
    }

}