package net.frozenorb.foxtrot.gameplay.kitmap.daily;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DailyKitHandler {
    private Foxtrot instance;

    @Getter private File file;
    @Getter private FileConfiguration data;
    public static Map<UUID, Long> cooldowns = new HashMap<>();
    public static Map<UUID, Integer> dailyStreak = new HashMap<>();
    public static Map<UUID, Long> lastUse = new HashMap<>();

    public DailyKitHandler(Foxtrot instance) {
        this.instance = instance;
    }

    public void loadInfo() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/dailykits.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("dailyStreak") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.data.getConfigurationSection("dailyStreak").getKeys(false).forEach(it ->
                    dailyStreak.put(UUID.fromString(it), this.data.getInt("dailyStreak." + it))), 5);
        }

        if (this.data.get("lastUse") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.data.getConfigurationSection("lastUse").getKeys(false).forEach(it ->
                    lastUse.put(UUID.fromString(it), this.data.getLong("lastUse." + it))), 5);
        }
    }

    public void saveData() {
        if (true) {
            return;
        }

        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        for (Map.Entry<UUID, Integer> entry : dailyStreak.entrySet()) {
            this.data.set("dailyStreak." + entry.getKey().toString(), entry.getValue());
        }

        for (Map.Entry<UUID, Long> entry : lastUse.entrySet()) {
            this.data.set("lastUse." + entry.getKey().toString(), entry.getValue());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(names = {"dailykit"}, permission = "")
    public static void exceute(Player player) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.GREEN + "Must be in spawn to use this command!");
            return;
        }

        if (true) {
            player.sendMessage("Disabled");
            return;
        }

        if (Arrays.stream(player.getInventory().getContents()).anyMatch(it -> it != null && it.getType() != Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Your inventory must be empty to use this!");
            return;
        }

        useKit(player);
    }

    public static void useKit(Player player) {

        if (lastUse.containsKey(player.getUniqueId()) && (lastUse.get(player.getUniqueId())+TimeUnit.DAYS.toMillis(1)) > (System.currentTimeMillis())) {
            player.sendMessage(ChatColor.RED + "You are currently on Daily Kit cooldown for another " + (TimeUtils.formatIntoDetailedString((int) ((lastUse.get(player.getUniqueId())-System.currentTimeMillis())/1000))));
            return;
        }

        int day = dailyStreak.getOrDefault(player.getUniqueId(), 0)+1;
        long lastUseLong = lastUse.getOrDefault(player.getUniqueId(), 0L);

        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());

        final Calendar lastCalendar = new GregorianCalendar();
        lastCalendar.setTimeInMillis(lastUseLong);

        int lastUseDay = calendar.get(Calendar.DAY_OF_YEAR);
        int currentDay = lastCalendar.get(Calendar.DAY_OF_YEAR);

        if ((lastUseDay+2) < currentDay && day != 1) {
            day = 1;

            player.sendMessage(ChatColor.RED + "Your daily streak has reset as it's been over 2 days since you last used it!");
        }

        boolean protectionTwo = false;
        boolean speedBoots = false;
        boolean fireResistance = false;
        boolean crapples = false;
        boolean moreCrapples = false;
        boolean godApple = false;
        boolean customEnchant = false;
        boolean fewPartnerItems = false;
        boolean morePartnerItems = false;

        if (day >= 2) {
            protectionTwo = true;
        }

        if (day >= 3) {
            speedBoots = true;
        }

        if (day >= 4) {
            fireResistance = true;
            crapples = true;
        }

        if (day >= 5) {
            godApple = true;
            moreCrapples = true;
        }

        if (day >= 6) {
            customEnchant = true;
            fewPartnerItems = true;
        }

        if (day >= 8) {
            morePartnerItems = true;
        }

        final ItemBuilder helmet = ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3);
        final ItemBuilder chestplate = ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3);
        final ItemBuilder leggings = ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3);
        final ItemBuilder boots = ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4);

        if (protectionTwo) {
            helmet.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            chestplate.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            leggings.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            boots.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }

        if (speedBoots) {
            boots.addToLore("&cSpeed II");
        }

        if (fireResistance) {
            leggings.addToLore("&cFireResistance I");
        }

        List<String> lore = Arrays.asList("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I", "§cFireResistance I");

        if (customEnchant) {
            helmet.setLore(lore);
            chestplate.setLore(lore);
            leggings.setLore(lore);
            boots.addToLore("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I", "§cFireResistance I");
        }

        player.getInventory().setHelmet(helmet.build());
        player.getInventory().setChestplate(chestplate.build());
        player.getInventory().setLeggings(leggings.build());
        player.getInventory().setBoots(boots.build());

        if (protectionTwo) {
            player.getInventory().setItem(0, ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.DURABILITY, 3).build());
        } else {
            player.getInventory().setItem(0, ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).build());
        }

        player.getInventory().setItem(1, new ItemStack(Material.ENDER_PEARL, 16));

        if (!customEnchant) {
            player.getInventory().setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        }

        final AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();

        if (crapples) {
            player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 8));
        }

        if (moreCrapples) {
            player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 16));
        }

        if (godApple) {
            player.getInventory().setItem(9+9+9, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
        }

        if (fewPartnerItems) {
            player.getInventory().setItem(3, ItemBuilder.copyOf(abilityHandler.fromName("Eggport").hassanStack.clone()).amount(8).build());
            player.getInventory().setItem(4, ItemBuilder.copyOf(abilityHandler.fromName("PortableStrength").hassanStack.clone()).amount(2).build());
            player.getInventory().setItem(9, ItemBuilder.copyOf(abilityHandler.fromName("SwitchStick").hassanStack.clone()).amount(2).build());
            player.getInventory().setItem(9+9, ItemBuilder.copyOf(abilityHandler.fromName("ItemCounter").hassanStack.clone()).amount(2).build());
        }

        if (morePartnerItems) {
            player.getInventory().setItem(3, ItemBuilder.copyOf(abilityHandler.fromName("Eggport").hassanStack.clone()).amount(16).build());
            player.getInventory().setItem(4, ItemBuilder.copyOf(abilityHandler.fromName("PortableStrength").hassanStack.clone()).amount(4).build());
            player.getInventory().setItem(9, ItemBuilder.copyOf(abilityHandler.fromName("SwitchStick").hassanStack.clone()).amount(4).build());
            player.getInventory().setItem(10, ItemBuilder.copyOf(abilityHandler.fromName("Shockwave").hassanStack.clone()).amount(4).build());
            player.getInventory().setItem(9+9, ItemBuilder.copyOf(abilityHandler.fromName("ItemCounter").hassanStack.clone()).amount(4).build());
            player.getInventory().setItem(9+10, ItemBuilder.copyOf(abilityHandler.fromName("MedKit").hassanStack.clone()).amount(4).build());
        }

        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new Potion(PotionType.INSTANT_HEAL, 2, true).toItemStack(1));
        }

        day++;

        dailyStreak.put(player.getUniqueId(), day);
        lastUse.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
