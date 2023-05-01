package net.frozenorb.foxtrot.server.keyalls;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.command.parameter.KeyAllParameter;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.EditorMainMenu;
import net.frozenorb.foxtrot.server.keyalls.task.KeyAllService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KeyAllHandler {
    @Getter private File file;
    @Getter private FileConfiguration data;
    @Getter private Map<String, KeyAll> cache = new HashMap<>();

    public KeyAllHandler(Foxtrot instance) {
        new KeyAllService(instance, this).runTaskTimer(instance, 20, 20);

        Proton.getInstance().getCommandHandler().registerParameterType(KeyAll.class, new KeyAllParameter());

        this.loadData();
    }

    public void loadData() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/keyalls.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.getConfigurationSection("keyalls") == null) {
            return;
        }

        for (String path : this.data.getConfigurationSection("keyalls").getKeys(false)) {
            final KeyAll keyAll = new KeyAll(path);

            final List<ItemStack> items = new ArrayList<>();

            if (this.data.getConfigurationSection("keyalls." + path + ".items") != null) {
                for (String itemPath : this.data.getConfigurationSection("keyalls." + path + ".items").getKeys(false)) {
                    items.add(this.data.getItemStack("keyalls." + path + ".items." + itemPath));
                }
            }

            keyAll.setItems(items);
            keyAll.setEnd(this.data.getLong("keyalls." + path + ".end"));
            keyAll.setGiving(this.data.getBoolean("keyalls." + path + ".giving"));
            keyAll.setGiveAllTime(this.data.getLong("keyalls." + path + ".giveAllTime"));
            keyAll.setDisplayName(this.data.getString("keyalls." + path + ".displayName"));
            keyAll.setScoreboardDisplay(this.data.getString("keyalls." + path + ".scoreboardDisplay"));

            final List<UUID> uuids = new ArrayList<>();

            if (this.data.getConfigurationSection("keyalls." + path + ".redeemed") != null) {
                for (String uuid : this.data.getConfigurationSection("keyalls." + path + ".redeemed").getKeys(false)) {

                    uuids.add(UUID.fromString(uuid));
                }
            }

            keyAll.setRedeemed(uuids);

            this.cache.put(path, keyAll);
        }
    }

    public void saveData() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Map.Entry<String, KeyAll> entry : this.cache.entrySet()) {
            final KeyAll keyAll = entry.getValue();

            int i = 0;

            for (ItemStack item : keyAll.getItems()) {
                i++;
                this.data.set("keyalls." + entry.getKey() + ".items.REWARD_" + i, item.clone());
            }

            this.data.set("keyalls." + entry.getKey() + ".end", keyAll.getEnd());
            this.data.set("keyalls." + entry.getKey() + ".giving", keyAll.isGiving());
            this.data.set("keyalls." + entry.getKey() + ".giveAllTime", keyAll.getGiveAllTime());
            this.data.set("keyalls." + entry.getKey() + ".displayName", keyAll.getDisplayName());
            this.data.set("keyalls." + entry.getKey() + ".scoreboardDisplay", keyAll.getScoreboardDisplay());

            for (UUID uuid : keyAll.getRedeemed()) {
                this.data.set("keyalls." + entry.getKey() + ".redeemed." + uuid.toString(), true);
            }
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KeyAll findKeyAll(String input) {
        return cache.entrySet().stream().filter(it -> it.getKey().equalsIgnoreCase(input)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public List<KeyAll> findScoreboardKeyalls() {
        final List<KeyAll> toReturn = new ArrayList<>();

        for (KeyAll keyAll : this.cache.values()) {

            if (keyAll.getGiveAllTime() == 0) {
                continue;
            }

            if (keyAll.getGiveAllTime() < System.currentTimeMillis()) {
                continue;
            }

            long difference = keyAll.getGiveAllTime()-System.currentTimeMillis();

            if (difference <= TimeUnit.MINUTES.toMillis(10)) {
                toReturn.add(keyAll);
            }
        }

        return toReturn;
    }

    public List<KeyAll> findAvailableKeyAlls(Player player) {
        final List<KeyAll> toReturn = new ArrayList<>();

        for (KeyAll value : this.cache.values()) {

            if (!value.isGiving()) {
                continue;
            }

            if (value.getItems().isEmpty()) {
                continue;
            }

            if (value.getRedeemed().contains(player.getUniqueId())) {
                continue;
            }

            // Key-All is over
            if (value.getEnd() < System.currentTimeMillis()) {
                continue;
            }

            toReturn.add(value);
        }

        return toReturn;
    }
}