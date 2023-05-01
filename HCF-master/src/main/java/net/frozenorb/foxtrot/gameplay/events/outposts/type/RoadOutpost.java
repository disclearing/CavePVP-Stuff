package net.frozenorb.foxtrot.gameplay.events.outposts.type;

import cc.fyre.proton.serialization.LocationSerializer;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.InventorySerialization;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RoadOutpost extends Outpost {
    @Override
    public String getId() {
        return "Road";
    }

    @Override
    public ChatColor getDisplayColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getMaterial() {
        return Material.REDSTONE_LAMP_OFF;
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public List<String> getBenefits() {
        return Arrays.asList("25% Reduced Partner Item cooldowns for your faction.", "Access to partner item chests around the build.");
    }

    @Getter private File roadOutpostData;
    @Getter private Set<Location> roadOutpostChests = new HashSet<>();
    @Getter private List<ItemStack> roadOutpostLoot = new ArrayList<>();

    public RoadOutpost() {
        roadOutpostData = new File(Foxtrot.getInstance().getDataFolder(), "data/roadOutpost.json");

        this.loadOutpostLoot();
    }

    public void loadOutpostLoot() {

        try {
            if (!roadOutpostData.exists() && roadOutpostData.createNewFile()) {
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("chests", new BasicDBList());
                dbo.put("loot", new BasicDBList());

                FileUtils.write(roadOutpostData, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(roadOutpostData));

            if (dbo != null) {
                BasicDBList chests = (BasicDBList) dbo.get("chests");
                BasicDBList loot = (BasicDBList) dbo.get("loot");

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    roadOutpostChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }

                for (Object lootObj : loot) {
                    roadOutpostLoot.add(InventorySerialization.deserialize((BasicDBObject) lootObj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOutpostLoot() {
        try {
            BasicDBObject dbo = new BasicDBObject();

            BasicDBList chests = new BasicDBList();
            BasicDBList loot = new BasicDBList();

            for (Location citadelChest : roadOutpostChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", LocationSerializer.serialize(citadelChest));
                chests.add(chest);
            }

            for (ItemStack lootItem : roadOutpostLoot) {
                loot.add(InventorySerialization.serialize(lootItem));
            }

            dbo.put("chests", chests);
            dbo.put("loot", loot);

            roadOutpostData.delete();
            FileUtils.write(roadOutpostData, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int scanLoot() {
        roadOutpostChests.clear();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmask.OUTPOST)) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("RoadOutpost", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            roadOutpostChests.add(location);
                        }
                    }
                }
            }
        }
        return roadOutpostChests.size();
    }

    public int respawnOutpostChests() {
        int respawned = 0;

        for (Location chest : roadOutpostChests) {
            if (respsawnOutpostChest(chest)) {
                respawned++;
            }
        }

        return (respawned);
    }

    public boolean respsawnOutpostChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            chest.getBlockInventory().clear();
            chest.getBlockInventory().addItem(roadOutpostLoot.get(ThreadLocalRandom.current().nextInt(roadOutpostLoot.size())));
            return (true);
        } else {
            Foxtrot.getInstance().getLogger().warning("Outpost chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
            return (false);
        }
    }
}
