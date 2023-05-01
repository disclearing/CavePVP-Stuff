package net.frozenorb.foxtrot.gameplay.loot.treasurechest;

import cc.fyre.proton.Proton;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.command.parameterType.CrateParameterProvider;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.command.parameterType.TreasureChestParameterProvider;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener.TreasureChestListener;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import net.frozenorb.foxtrot.util.PersistableLocation;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TreasureChestHandler {
    private Foxtrot instance;

    @Getter private List<TreasureChest> treasureChests = new ArrayList<>();
    @Getter private TreasureChestListener treasureChestListener;

    @Getter private File folder;

    public TreasureChestHandler(Foxtrot instance) {
        this.instance = instance;

        treasureChestListener = new TreasureChestListener(this.instance);

        Proton.getInstance().getCommandHandler().registerParameterType(TreasureChest.class, new TreasureChestParameterProvider());

        instance.getServer().getPluginManager().registerEvents(treasureChestListener, this.instance);
        instance.getServer().getScheduler().runTaskLater(instance, () -> this.loadData(false), 5);
    }

    public void loadData(boolean rewards) {
        this.folder = new File(this.instance.getDataFolder(), "data/treasurechests");

        if (!this.folder.exists()) {
            this.folder.mkdir();
        }

        for (File file : Objects.requireNonNull(this.folder.listFiles())) {
            final TreasureChest treasureChest = new TreasureChest(file.getName().replace(".yml", ""));

            treasureChest.loadRewards();
            if (!rewards) {
                treasureChest.loadLocations();
            }

            this.treasureChests.add(treasureChest);
        }
    }

    public void saveData() {
        this.treasureChests.forEach(it -> {
            final File file = new File(this.instance.getDataFolder(), "data/treasurechests/" + it.getId() + ".yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            it.saveCrate(file, YamlConfiguration.loadConfiguration(file));
        });
    }

    public TreasureChestReward openChest(TreasureChest treasureChest, Chest chest, Player player) {
        final TreasureChestReward treasureChestReward = this.findReward(treasureChest, chest, player);
        final ItemStack itemStack = treasureChestReward.getItemStack().clone();

        if (itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
        }

        final ItemStack displayItem = itemStack.clone();

        if (displayItem.getItemMeta() != null) {
            final ItemMeta itemMeta = displayItem.getItemMeta();

            itemMeta.setDisplayName("#" + ThreadLocalRandom.current().nextInt(50000,1000000));
            displayItem.setItemMeta(itemMeta);
        }

        VoucherCommand.spawnFireworks(chest.getLocation().add(0.5, -1, 0.5), 1, 0, Color.RED, FireworkEffect.Type.BURST);

        final Item item = chest.getWorld().dropItem(chest.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5), displayItem);
        item.setVelocity(new Vector(0, 0 , 0));
        item.setMetadata("TREASURE_REWARD", new FixedMetadataValue(Foxtrot.getInstance(), true));

        if (treasureChestReward.isGrantItem()) {
            player.getInventory().addItem(itemStack.clone());
        }

        return treasureChestReward;
    }

    private TreasureChestReward findReward(TreasureChest treasureChest, Chest chest, Player player) {
        final List<TreasureChestReward> rewards = new ArrayList<>(treasureChest.getRewards());
        double sumNumber = rewards.stream().mapToDouble(TreasureChestReward::getChance).sum();
        double random = Math.random() * sumNumber;

        TreasureChestReward choice = new TreasureChestReward(null, 0, "", false, false);

        for (TreasureChestReward treasureChestReward : rewards) {
            double chance = treasureChestReward.getChance();

            choice = treasureChestReward;
            random -= chance;
            if (random < 0) {
                break;
            }
        }

        final String command = choice.getCommand();

        if (!command.equalsIgnoreCase("none") && !command.equalsIgnoreCase("")) {
            final String item = choice.getItemStack().getItemMeta().hasDisplayName() ? choice.getItemStack().getItemMeta().getDisplayName() : choice.getItemStack().getType().name();

            for (int i = 0; i < 8; i++) {
                this.instance.getServer().broadcastMessage(ChatColor.translate(player.getName() + " &ahas just won a " + item + "&a!"));
            }

            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", item));

            VoucherCommand.spawnFireworks(chest.getLocation(), 3, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);
        }

        return choice;
    }
}
