package org.cavepvp.suge.kit;

import cc.fyre.proton.Proton;
import cc.fyre.universe.UniverseAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.command.parameter.KitTypeParameter;
import org.cavepvp.suge.kit.data.Category;
import org.cavepvp.suge.kit.data.Kit;
import org.cavepvp.suge.kit.listener.KitListener;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KitHandler {
    private Suge instance;

    @Getter @Setter private File kitsFile;
    @Getter @Setter private FileConfiguration kitsData;

    private File cooldownFile = new File(Suge.getInstance().getDataFolder(), "cooldowns.yml");
    private FileConfiguration cooldownData = YamlConfiguration.loadConfiguration(cooldownFile);

    @Getter private Map<String, Kit> kits = new HashMap<>();
    @Getter private Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    @Getter private Map<Kit, Integer> kitUses = new HashMap<>();

    public KitHandler(Suge instance) {
        this.instance = instance;

        Proton.getInstance().getCommandHandler().registerParameterType(Kit.class, new KitTypeParameter());

        this.instance.getServer().getPluginManager().registerEvents(new KitListener(this.instance), this.instance);

        this.loadKits();
        this.loadCooldowns();
    }

    public Optional<Kit> findKit(String name) {
        return Suge.getInstance().getKitHandler().getKits().entrySet().stream().filter(it -> it.getKey().equalsIgnoreCase(name)).map(Map.Entry::getValue).findFirst();
    }

    public void loadKits() {
        System.out.println("[Suge] Loading kits...");

        this.kitsFile = new File(Suge.getInstance().getDataFolder(), "kits.yml");
        this.kitsData = YamlConfiguration.loadConfiguration(kitsFile);

        if (!kitsFile.exists()) {
            try {
                kitsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (kitsData.get("kits") == null) {
            return;
        }

        for (String path : kitsData.getConfigurationSection("kits").getKeys(false)) {
            System.out.println("[Suge] Loaded kit named " + path + ".");

            final Kit kit = new Kit();

            kit.setName(path);
            kit.setSlot(kitsData.getInt("kits." + path + ".slot"));

            kit.setDisplayName(kitsData.getString("kits." + path + ".displayName"));
            kit.setMaterial(Material.valueOf(kitsData.getString("kits." + path + ".material")));

            if (UniverseAPI.getServerName().equalsIgnoreCase("Squads")) {
                kit.setCooldown(TimeUnit.MINUTES.toMinutes(15));
            } else {
                kit.setCooldown(TimeUnit.HOURS.toMillis(kitsData.getInt("kits." + path + ".cooldown")));
            }
            kit.setLore(kitsData.getStringList("kits." + path + ".lore"));
            kit.setDamage(kitsData.getInt("kits." + path + ".damage"));
            kit.setCategory(Category.valueOf(kitsData.getString("kits." + path + ".category", "NONE")));

            final List<ItemStack> armor = new ArrayList<>();

            for (String armorPath : kitsData.getConfigurationSection("kits." + path + ".armor").getKeys(false)) {
                armor.add(kitsData.getItemStack("kits." + path + ".armor." + armorPath));
            }

            kit.setArmor(armor);

            final List<ItemStack> content = new ArrayList<>();

            for (String itemsPath : kitsData.getConfigurationSection("kits." + path + ".content").getKeys(false)) {
                content.add(kitsData.getItemStack("kits." + path + ".content." + itemsPath));
            }

            kit.setItems(content);

            this.kitUses.put(kit, kitsData.getInt("kits." + path + ".uses", 0));
            this.kits.put(path, kit);
        }
    }

    public void loadCooldowns() {

        if (!cooldownFile.exists()) {
            try {
                cooldownFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (cooldownData.get("cooldowns") == null) {
            return;
        }

        for (String uuidString : cooldownData.getConfigurationSection("cooldowns").getKeys(false)) {

            final UUID uuid = UUID.fromString(uuidString);

            final Map<String, Long> kitCooldowns = new HashMap<>();

            for (String kitName : cooldownData.getConfigurationSection("cooldowns." + uuidString).getKeys(false)) {
                kitCooldowns.put(kitName, cooldownData.getLong("cooldowns." + uuidString + "." + kitName));
            }

            cooldowns.put(uuid, kitCooldowns);
        }
    }

    public void saveKits() {
        for (Kit value : this.kits.values()) {
            kitsData.set("kits." + value.getName() + ".slot", value.getSlot());
            kitsData.set("kits." + value.getName() + ".displayName", value.getDisplayName());
            kitsData.set("kits." + value.getName() + ".material", value.getMaterial().name());
            kitsData.set("kits." + value.getName() + ".cooldown", TimeUnit.MILLISECONDS.toHours(value.getCooldown()));
            kitsData.set("kits." + value.getName() + ".lore", value.getLore());
            kitsData.set("kits." + value.getName() + ".damage", value.getDamage());
            kitsData.set("kits." + value.getName() + ".uses", kitUses.getOrDefault(value, 0));
            kitsData.set("kits." + value.getName() + ".category", value.getCategory().name());

            int i = 0;

            for (ItemStack item : value.getItems()) {
                kitsData.set("kits." + value.getName() + ".content." + "item_" + i++, item.clone());
            }

            int j = 0;

            for (ItemStack item : value.getArmor()) {
                kitsData.set("kits." + value.getName() + ".armor." + "item_" + j++, item.clone());
            }
        }

        try {
            kitsData.save(kitsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCooldowns() {
        this.cooldownData.getValues(false).forEach((key, value) -> this.cooldownData.set(key, null));

        for (Map.Entry<UUID, Map<String, Long>> uuidEntry : this.cooldowns.entrySet()) {
            for (Map.Entry<String, Long> durationEntry : uuidEntry.getValue().entrySet()) {
                cooldownData.set("cooldowns." + uuidEntry.getKey().toString() + "." + durationEntry.getKey(), durationEntry.getValue());
            }
        }

        try {
            cooldownData.save(cooldownFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCooldown(Player player, Kit kit) {
        final long remaining = this.getRemaining(player, kit);

        if (remaining == 0L) {
            return false;
        }

        if (!this.cooldowns.containsKey(player.getUniqueId())) {
            return false;
        }

        final Map<String, Long> allKitCooldowns = this.cooldowns.get(player.getUniqueId());

        if (!allKitCooldowns.containsKey(kit.getName().toLowerCase()) || System.currentTimeMillis() > allKitCooldowns.get(kit.getName().toLowerCase())) {
            return false;
        }

        return true;
    }

    public long getRemaining(Player player, Kit kit) {
        if (!this.cooldowns.containsKey(player.getUniqueId())) {
            return 0L;
        }

        final Map<String, Long> allKitCooldowns = this.cooldowns.get(player.getUniqueId());

        if (!allKitCooldowns.containsKey(kit.getName().toLowerCase())) {
            return 0L;
        }

        return allKitCooldowns.get(kit.getName().toLowerCase())-System.currentTimeMillis();
    }

    public void setCooldown(Player player, Kit kit) {

        final Map<String, Long> allKitCooldowns = this.cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());

        allKitCooldowns.remove(kit.getName().toLowerCase());
        allKitCooldowns.put(kit.getName().toLowerCase(), System.currentTimeMillis()+kit.getCooldown());

        this.cooldowns.put(player.getUniqueId(), allKitCooldowns);
    }

    public List<Kit> findKitsByCategory(Category category) {
        return this.kits.values().stream().filter(it -> it.getCategory().equals(category)).collect(Collectors.toList());
    }

    public List<Kit> findKitsOwned(Player player) {
        return this.kits.values().stream().filter(it -> player.hasPermission("crazyenchantments.gkitz." + it.getName().toLowerCase())).collect(Collectors.toList());
    }

    public List<Kit> findKitsOwned(Player player, Category category) {
        return this.findKitsByCategory(category).stream().filter(it -> player.hasPermission("crazyenchantments.gkitz." + it.getName().toLowerCase())).collect(Collectors.toList());
    }

}
