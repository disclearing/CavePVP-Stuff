package net.frozenorb.foxtrot.gameplay.kitmap.partner;

import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.util.FileConfig;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class PartnerCrateHandler implements Listener {

    private final FileConfig fileConfig = new FileConfig(Foxtrot.getInstance(), "data/partner_packages.yml");

    public PartnerCrateHandler() {
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = fileConfig.getConfig();
        config.addDefault("package-crate-name", "&4&lPartner &f&lPackage &c&lCrate");
        config.addDefault("package-crate-lore", Collections.singletonList("&fThe best partner packages"));
        config.addDefault("package-commands", Arrays.asList("/raw %player% is raw", "/raw %player% is raw af"));

        config.options().copyDefaults(true);
        fileConfig.save();

        reloadConfig();
    }

    @Getter
    private ItemStack crateItem;
    private final List<String> commands = new ArrayList<>();

    @SneakyThrows
    public void reloadConfig() {
        FileConfiguration config = fileConfig.getConfig();
        config.load(fileConfig.getFile());

        crateItem = ItemBuilder.of(Material.ENDER_CHEST)
                .name(config.getString("package-crate-name"))
                .addToLore(config.getStringList("package-crate-lore").toArray(new String[0]))
                .build();

        commands.clear();
        commands.addAll(config.getStringList("package-commands"));
    }

    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (item.getType() != Material.ENDER_CHEST) return;
        if (!item.hasItemMeta()) return;
        if (!crateItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) return;
        if (!crateItem.getItemMeta().getLore().equals(item.getItemMeta().getLore())) return;

        InventoryUtils.removeAmountFromInventory(player.getInventory(), item, 1);

        List<Ability> hassan = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()
                .stream()
                .filter(Ability::inPartnerPackage)
                .collect(Collectors.toList());
        Ability ability = hassan.get(ThreadLocalRandom.current().nextInt(hassan.size()));
        ItemStack itemStack = ability.hassanStack.clone();

        Firework fireWork = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwMeta = fireWork.getFireworkMeta();

        fwMeta.addEffect(FireworkEffect.builder()
                .flicker(false)
                .trail(true)
                .with(FireworkEffect.Type.BALL)
                .with(FireworkEffect.Type.BALL_LARGE)
                .with(FireworkEffect.Type.STAR)
                .withColor(Color.ORANGE)
                .withColor(Color.YELLOW)
                .withFade(Color.PURPLE)
                .withFade(Color.RED)
                .build());

        fireWork.setFireworkMeta(fwMeta);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                commands.get(ThreadLocalRandom.current().nextInt(commands.size())).replace("%player%", player.getName()).substring(1));

        InventoryUtils.addAmountToInventory(player.getInventory(), itemStack, 4);
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), player::updateInventory, 2L);

        event.setCancelled(true);
    }
}
