package net.frozenorb.foxtrot.gameplay.pvpclasses.mastery;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MasteryUpgradeHandler {
    private Foxtrot instance;

    @Getter private Map<UUID, Map<String, Integer>> cache = new HashMap<>();

    @Getter private File file;
    @Getter private FileConfiguration data;

    public MasteryUpgradeHandler(Foxtrot instance) {
        this.instance = instance;

        this.loadData();
    }

    public void loadData() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/mastery-upgrades.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("upgrades") == null) {
            return;
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.data.getConfigurationSection("upgrades").getKeys(false).forEach(it -> {
            final UUID uuid = UUID.fromString(it);
            final Map<String, Integer> toReturn = new HashMap<>();

            for (String key : this.data.getConfigurationSection("upgrades." + it).getKeys(false)) {
                int score = this.data.getInt("upgrades." + it + "." + key);

                toReturn.put(key, score);
            }

            this.cache.put(uuid, toReturn);
        }), 5);
    }

    public void saveData() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Map.Entry<UUID, Map<String, Integer>> entry : this.cache.entrySet()) {
            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                this.data.set("upgrades." + entry.getKey().toString() + "." + entry2.getKey(), entry2.getValue());
            }

        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUpgrade(Player player, String pvpClass, int score) {
        this.cache.putIfAbsent(player.getUniqueId(), new HashMap<>());
        final Map<String, Integer> entry = this.cache.get(player.getUniqueId());
        int currentScore = entry.getOrDefault(pvpClass, 0);

        final MasteryUpgrades currentUpgrade = MasteryUpgrades.getUpgradeByScore(currentScore);

        currentScore += score;

        entry.put(pvpClass, currentScore);

        MasteryUpgrades newUpgrade = MasteryUpgrades.getUpgradeByScore(currentScore);

        if (currentUpgrade.equals(newUpgrade)) {
            return;
        }

        this.instance.getGemMap().addGems(player.getUniqueId(), newUpgrade.gemPrize, true);
        if (newUpgrade.equals(MasteryUpgrades.LEVEL_FIVE)) {
            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), "chest give Treasure " + player.getName() + newUpgrade.treasureChests);
        }

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage("");
        player.sendMessage(CC.translate("&4&lMastery Upgrades"));
        player.sendMessage(CC.translate("&fYou have leveled up to level " + newUpgrade.displayName + "!"));
        player.sendMessage(CC.translate("&aYou have received " + newUpgrade.gemPrize + " Gems!"));
        player.sendMessage("");
    }

}
