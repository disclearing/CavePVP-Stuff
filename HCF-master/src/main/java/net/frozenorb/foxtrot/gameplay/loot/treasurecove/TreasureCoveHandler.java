package net.frozenorb.foxtrot.gameplay.loot.treasurecove;

import cc.fyre.proton.serialization.LocationSerializer;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.buycraft.plugin.data.GiftCard;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners.TreasureCoveListener;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.tasks.TreasureCoveSaveTask;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.InventorySerialization;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TreasureCoveHandler {
    public static final String PREFIX = ChatColor.GOLD + "[Treasure Cove]";

    private File treasureInfo;

    @Getter private String nextVoucher;
    @Getter private int nextVoucherAmount = 5;
    @Getter @Setter private Location centralChest;
    @Getter private Set<Location> treasureChests = new HashSet<>();
    @Getter private List<ItemStack> treasureLoot = new ArrayList<>();

    public TreasureCoveHandler() {
        treasureInfo = new File(Foxtrot.getInstance().getDataFolder(), "data/treasureInfo.json");

        loadTreasureInfo();
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new TreasureCoveListener(Foxtrot.getInstance()), Foxtrot.getInstance());

        (new TreasureCoveSaveTask()).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 20 * 60 * 5);

        this.nextVoucherAmount = ThreadLocalRandom.current().nextInt(5, 30);
    }

    public boolean generateCentralChest() {
        if (this.centralChest == null) {
            System.out.println();
            System.out.println("NO CENTRAL CHEST FOUND!!");
            System.out.println();
            return false;
        }

        final Block block = this.centralChest.getBlock();
        block.setType(Material.CHEST);
        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            String code = "";
            try {
                final GiftCard giftCard = Foxtrot.getInstance().getBuycraftPlugin().getApiClient().createGiftCard(BigDecimal.valueOf(this.nextVoucherAmount), "For Treasure Cove on " + new Date(System.currentTimeMillis()).toLocaleString()).execute().body().getData();

                code = giftCard.getCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (code == null) {
                return;
            }

            final Chest chest = (Chest) block.getState();

            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            final BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.addPage(ChatColor.BLACK + "Enjoy your " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + this.nextVoucherAmount + ChatColor.BLACK + " Gift Card. The code is on the next page.");
            bookMeta.addPage(code);
            bookMeta.setDisplayName(ChatColor.translate("&2&l$&a&l" + this.nextVoucherAmount + " &6Gift Card"));
            bookMeta.setAuthor("Treasure Cove");
            bookMeta.setTitle("Gift Card");
            book.setItemMeta(bookMeta);

            chest.getInventory().setItem(13, book.clone());
        });

        return true;
    }

    public void loadTreasureInfo() {

        try {
            if (!treasureInfo.exists() && treasureInfo.createNewFile()) {
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("chests", new BasicDBList());
                dbo.put("loot", new BasicDBList());

                FileUtils.write(treasureInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(treasureInfo));

            if (dbo != null) {
                this.centralChest = LocationSerializer.deserialize((BasicDBObject) dbo.get("centralChest"));

                BasicDBList chests = (BasicDBList) dbo.get("chests");
                BasicDBList loot = (BasicDBList) dbo.get("loot");

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    treasureChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }

                for (Object lootObj : loot) {
                    treasureLoot.add(InventorySerialization.deserialize((BasicDBObject) lootObj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTreasureInfo() {
        try {
            BasicDBObject dbo = new BasicDBObject();

            BasicDBList chests = new BasicDBList();
            BasicDBList loot = new BasicDBList();

            for (Location treasureChest : treasureChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", LocationSerializer.serialize(treasureChest));
                chests.add(chest);
            }

            for (ItemStack lootItem : treasureLoot) {
                loot.add(InventorySerialization.serialize(lootItem));
            }

            dbo.put("chests", chests);
            dbo.put("loot", loot);
            dbo.put("centralChest", LocationSerializer.serialize(this.centralChest));

            treasureInfo.delete();
            FileUtils.write(treasureInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanLoot() {
        treasureChests.clear();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmask.MOUNTAIN) && team.getName().equalsIgnoreCase("TreasureCove")) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("TreasureCove", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            treasureChests.add(location);
                        }
                    }
                }
            }
        }
    }

    public int respawnTreasureChests() {
        int respawned = 0;

        for (Location chest : treasureChests) {
            if (respawnTreasureChest(chest)) {
                respawned++;
            }
        }

        return (respawned);
    }

    public boolean respawnTreasureChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            chest.getBlockInventory().clear();
            chest.getBlockInventory().addItem(treasureLoot.get(ThreadLocalRandom.current().nextInt(treasureLoot.size())));
            return (true);
        } else {
            Foxtrot.getInstance().getLogger().warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
            return (false);
        }
    }

    public long getResetsIn() {
        return 0;
    }

}