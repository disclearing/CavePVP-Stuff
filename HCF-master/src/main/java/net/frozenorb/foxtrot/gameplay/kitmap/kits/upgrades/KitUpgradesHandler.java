package net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitUpgradesHandler {

    private static final File UPGRADES_FILE = new File(Foxtrot.getInstance().getDataFolder(), "upgrades.json");

    private final Map<UUID, Map<Material, Upgrades>> upgrades = new HashMap<>();

    public KitUpgradesHandler() {
        if (UPGRADES_FILE.exists()) {
            loadUpgrades();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), this::saveUpgrades, 20L * 60L * 2L, 20L * 60L * 2L);
    }

    private void loadUpgrades() {
        JsonParser parser = new JsonParser();

        try (BufferedReader br = new BufferedReader(new FileReader(UPGRADES_FILE))) {
            JsonObject root = (JsonObject) parser.parse(br);
            JsonArray upgradesArray = root.get("upgrades").getAsJsonArray();

            upgradesArray.forEach(upgradeElement -> {
                JsonObject obj = upgradeElement.getAsJsonObject();
                String uuid = obj.get("uuid").getAsString();
                JsonArray materials = obj.get("materials").getAsJsonArray();

                Map<Material, Upgrades> map = new HashMap<>();

                materials.forEach(materialElement -> {
                    JsonObject materialObject = materialElement.getAsJsonObject();
                    Material material = Material.getMaterial(materialObject.get("material").getAsString());
                    Upgrades upgrades = new Upgrades();

                    JsonArray enchantments = materialObject.get("enchantments").getAsJsonArray();
                    enchantments.forEach(element3 -> upgrades.getEnchantmentList().add(Enchantment.getByName(element3.getAsString())));

                    JsonArray customEnchantments = materialObject.get("custom_enchantments").getAsJsonArray();
                    customEnchantments.forEach(element3 -> upgrades.getCustomEnchantmentList().add(element3.getAsString()));

                    map.put(material, upgrades);
                });

                this.upgrades.put(UUID.fromString(uuid), map);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUpgrades() {
        JsonArray array = new JsonArray();

        // WARNING: MORE UGLY CODE

        upgrades.forEach((uuid, map) -> {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid.toString());

            JsonArray materials = new JsonArray();

            map.forEach((material, upgrades) -> {
                JsonObject upgradeObject = new JsonObject();
                upgradeObject.addProperty("material", material.name());

                JsonArray enchantments = new JsonArray();
                upgrades.getEnchantmentList().stream().map(Enchantment::getName).forEach(enchantments::add);
                upgradeObject.add("enchantments", enchantments);

                JsonArray customEnchantments = new JsonArray();
                upgrades.getCustomEnchantmentList().forEach(customEnchantments::add);
                upgradeObject.add("custom_enchantments", customEnchantments);

                materials.add(upgradeObject);
            });

            object.add("materials", materials);
            array.add(object);
        });

        JsonObject root = new JsonObject();
        root.add("upgrades", array);

        try {
            Files.write(root.toString(), UPGRADES_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Material, Upgrades> getUpgrades(Player player) {
        return upgrades.get(player.getUniqueId());
    }

    public Map<Material, Upgrades> getOrComputeUpgrades(Player player) {
        return upgrades.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>());
    }
}
