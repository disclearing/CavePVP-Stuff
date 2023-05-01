package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.command.parameterType.CrateParameterProvider;
import net.frozenorb.foxtrot.gameplay.loot.crate.data.ArmorType;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.gameplay.loot.crate.listener.CrateListener;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrateHandler {
    private Foxtrot instance;

    @Getter
    private List<Crate> crates = new ArrayList<>();
    @Getter
    private File folder;

    public CrateHandler(Foxtrot instance) {
        this.instance = instance;

        Proton.getInstance().getCommandHandler().registerParameterType(Crate.class, new CrateParameterProvider());

        this.instance.getServer().getPluginManager().registerEvents(new CrateListener(this), this.instance);
        this.instance.getServer().getScheduler().runTaskLater(this.instance, this::loadData, 40);
    }

    public void loadData() {
        this.folder = new File(this.instance.getDataFolder(), "data/crates");

        if (!this.folder.exists()) {
            this.folder.mkdir();
        }

        for (File file : Objects.requireNonNull(this.folder.listFiles())) {
            this.loadFile(YamlConfiguration.loadConfiguration(file));
        }
    }

    public void loadFile(FileConfiguration data) {
        final String id = data.getString("id");
        final String displayName = data.getString("displayName");
        final Material material = Material.getMaterial(data.getString("material"));
        final Color fireworkColor = Color.fromRGB(data.getInt("fireworkColor"));
        final List<String> lore = data.getStringList("lore");

        final List<CrateItem> crateItems = new ArrayList<>();

        if (data.getConfigurationSection("items") != null) {
            for (String path : data.getConfigurationSection("items").getKeys(false)) {
                final String finalPath = "items." + path + ".";
                crateItems.add(new CrateItem(data.getItemStack(finalPath + "itemStack"), data.getDouble(finalPath + "chance"),
                        data.getString(finalPath + "command"), data.getBoolean(finalPath + "giveItem"), data.getBoolean(finalPath + "broadcast")));
            }
        }

        this.crates.add(new Crate(id, displayName, material, fireworkColor, lore, crateItems));
    }

    public void saveData() {
        this.crates.forEach(it -> {
            final File file = new File(this.instance.getDataFolder(), "data/crates/" + it.getId() + ".yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            it.saveCrate(file, YamlConfiguration.loadConfiguration(file), it);
        });
    }

    public Optional<Crate> findByItemStack(ItemStack itemStack) {
        return crates.stream().filter(it -> it.getItemStack().isSimilar(itemStack)).findFirst();
    }

    public Optional<Crate> findById(String id) {
        return crates.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst();
    }

    public void openCrate(Player player, Crate crate) {
        player.sendMessage(ChatColor.translate("&aYou have opened a " + crate.getDisplayName() + "&a."));
        player.playSound(player.getLocation(), Sound.BAT_LOOP, 1, 1);
        VoucherCommand.spawnFireworks(player.getLocation(), 1, 2, crate.getFireworkColor(), FireworkEffect.Type.BURST);

        if (crate.getId().equalsIgnoreCase("Reinforce")) {
            final List<ArmorType> types = Arrays.stream(ArmorType.values()).collect(Collectors.toList());
            final ArmorType armorType = types.get(ThreadLocalRandom.current().nextInt(0, types.size()));

            giveArmor(player, armorType);
            player.getInventory().addItem(this.getRandomItem(player, crate, crate.getItems()));
        } else {
            IntStream.range(0, 6).forEach(it -> player.getInventory().addItem(this.getRandomItem(player, crate, crate.getItems())));
        }
    }

    public void giveArmor(Player player, ArmorType armorType) {

        List<String> lore = Arrays.asList("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I", "§cFireResistance I");

        player.getInventory().addItem(ItemBuilder.of(armorType.getHelmet()).name("§6§lReinforce §7┃ §fHelmet").setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());

        if (armorType == ArmorType.GOLD) {
            player.getInventory().addItem(ItemBuilder.of(armorType.getChestplate()).name("§6§lReinforce §7┃ §fChestplate").setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        } else {
            player.getInventory().addItem(ItemBuilder.of(armorType.getChestplate()).name("§6§lReinforce §7┃ §fChestplate").setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        }

        player.getInventory().addItem(ItemBuilder.of(armorType.getLeggings()).name("§6§lReinforce §7┃ §fLeggings").setLore(lore).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).build());
        player.getInventory().addItem(ItemBuilder.of(armorType.getBoots()).name("§6§lReinforce §7┃ §fBoots").addToLore("§cHellForged IV", "§cImplants V", "§cMermaid III", "§cRecover I", "§cFireResistance I", "§cSpeed II").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_EXPLOSIONS, 5).enchant(Enchantment.PROTECTION_FALL, 4).build());
    }

    public ItemStack getRandomItem(Player player, Crate crate, List<CrateItem> crateItems) {
        double sum = crateItems.stream().mapToDouble(CrateItem::getChance).sum();
        double randomNumber = Math.random() * sum;

        CrateItem choice = null;

        for (CrateItem crateItem : crateItems) {
            choice = crateItem;
            randomNumber -= crateItem.getChance();
            if (randomNumber < 0) {
                break;
            }
        }

        if (choice == null) {
            return new ItemStack(Material.AIR);
        }

        if (choice.getCommand() != null && !choice.getCommand().equalsIgnoreCase("none")) {
            final String command = choice.getCommand();
            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", choice.getItemStack().getItemMeta().hasDisplayName() ? choice.getItemStack().getItemMeta().getDisplayName() : choice.getItemStack().getType().name()));
        }

        if (choice.isBroadcast()) {
            for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(ChatColor.translate(crate.getDisplayName()));
                onlinePlayer.sendMessage(ChatColor.translate("&f" + player.getName() + " &chas won a " + (choice.getItemStack().getItemMeta().hasDisplayName() ? choice.getItemStack().getItemMeta().getDisplayName() : choice.getItemStack().getType().name()) + "&c!"));
                onlinePlayer.sendMessage(ChatColor.translate("&7Purchase Lootboxes in the coin shop by typing &f/coinshop"));
                new FancyMessage(ChatColor.translate("&a[Click to open the Coin Shop]")).command("/coinshopcategory cratekeys").send(onlinePlayer);
                onlinePlayer.sendMessage("");
            }
        }

        if (!choice.isGiveItem()) {
            return new ItemStack(Material.AIR);
        }

        final ItemStack itemStack = choice.getItemStack().clone();

        if (itemStack.getAmount() == 0 || itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
        }
        return itemStack;
    }
}
