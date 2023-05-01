package net.frozenorb.foxtrot.gameplay.loot.treasurechest;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.util.PersistableLocation;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class TreasureChest {
    @Getter private String id;
    @Getter @Setter private int slot;
    @Getter @Setter private int maxOpened;
    @Getter @Setter private String displayName;
    @Getter @Setter private Material material;
    @Getter @Setter private Location centralLocation;
    @Getter private List<Location> chests = new ArrayList<>();
    @Getter private List<TreasureChestReward> rewards = new ArrayList<>();
    @Getter private Map<UUID, Integer> cache = new HashMap<>();

    public TreasureChest(String id) {
        this.id = id;
    }

    public void loadLocations() {
        final File file = new File(Foxtrot.getInstance().getTreasureChestHandler().getFolder(), id + ".yml");
        final FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.slot = data.getInt("slot");
        this.displayName = data.getString("displayName");

        this.material = Material.valueOf(data.getString("material"));
        this.maxOpened = data.getInt("maxOpened");

        if (data.contains("centralLocation")) {
            this.centralLocation = ((PersistableLocation) data.get("centralLocation")).getLocation();
        }

        if (data.get("location") != null) {
            data.getConfigurationSection("location").getKeys(false).forEach(it -> this.chests.add(((PersistableLocation) data.get("location." + it)).getLocation()));
        }

        if (data.get("players") != null) {
            for (String path : data.getConfigurationSection("players").getKeys(false)) {
                final UUID uuid = UUID.fromString(path);

                cache.put(uuid, data.getInt("players." + path));
            }
        }
    }

    public void loadRewards() {
        final File file = new File(Foxtrot.getInstance().getTreasureChestHandler().getFolder(), id + ".yml");
        final FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (data.get("rewards") == null) {
            return;
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> data.getConfigurationSection("rewards").getKeys(false).forEach(it ->
                this.rewards.add(new TreasureChestReward(data.getItemStack("rewards." + it + ".itemStack"),
                        data.getDouble("rewards." + it + ".chance"),
                        data.getString("rewards." + it + ".command"),
                        data.getBoolean("rewards." + it + ".giveItem"), data.getBoolean("rewards." + it + ".broadcast", false)))), 5);
    }

    public void saveCrate(File file, FileConfiguration data) {
        data.getValues(false).forEach((key, value) -> data.set(key, null));

        int i = 0;

        if (this.centralLocation != null) {
            data.set("centralLocation", new PersistableLocation(this.centralLocation));
        }

        data.set("displayName", this.displayName);
        data.set("slot", this.slot);
        data.set("maxOpened", this.maxOpened);
        data.set("material", this.material.name());

        for (Location location : this.chests) {
            i++;
            data.set("location.location_" + i, new PersistableLocation(location));
        }

        for (Map.Entry<UUID, Integer> entry : cache.entrySet()) {
            data.set("players." + entry.getKey().toString(), entry.getValue());
        }

        for (TreasureChestReward treasureChestReward : this.rewards) {
            i++;
            data.set("rewards.reward_" + i + ".itemStack", treasureChestReward.getItemStack());
            data.set("rewards.reward_" + i + ".chance", treasureChestReward.getChance());
            data.set("rewards.reward_" + i + ".command", treasureChestReward.getCommand());
            data.set("rewards.reward_" + i + ".giveItem", treasureChestReward.isGrantItem());
            data.set("rewards.reward_" + i + ".broadcast", treasureChestReward.isBroadcast());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
