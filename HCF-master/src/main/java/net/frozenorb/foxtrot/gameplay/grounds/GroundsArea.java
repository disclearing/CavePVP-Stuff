package net.frozenorb.foxtrot.gameplay.grounds;

import cc.fyre.proton.util.EntityUtils;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnerType;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GroundsArea implements Listener {
    @Getter @Setter private boolean active;
    @Getter @Setter private List<Location> guardLocations = new ArrayList<>();
    @Getter @Setter private Map<Entity, Location> takenLocations = new HashMap<>();

    public abstract String getGroundsID();
    public abstract String getGroundsDisplayName();
    public abstract EntityType getSpawnerType();
    public abstract List<EntityType> getGuardTypes();

    public void reset() {
        final Team team = this.findTeam();

        if (team == null) {
            System.out.println("**********************");
            System.out.println();
            System.out.println("SPAWNER GROUNDS " + this.getGroundsID() + " has no spawn!");
            System.out.println();
            System.out.println("**********************");
            return;
        }

        final Location location = team.getHQ();
        final Block below = location.getBlock().getRelative(BlockFace.DOWN);

        below.setType(Material.MOB_SPAWNER);
        below.setMetadata("GROUNDS", new FixedMetadataValue(Foxtrot.getInstance(), true));

        final CreatureSpawner spawner = (CreatureSpawner) below.getState();

        spawner.setSpawnedType(this.getSpawnerType());
        spawner.update();
    }

    public void startGuardService() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                final Map<Entity, Location> newMap = new HashMap<>(takenLocations);

                for (Map.Entry<Entity, Location> entry : newMap.entrySet()) {
                    if (entry.getKey().isDead()) {
                        takenLocations.remove(entry.getKey());
                    }
                }

                for (Location guardLocation : guardLocations) {

                    if (takenLocations.containsValue(guardLocation)) {
                        continue;
                    }

                    final Monster monster = (Monster) guardLocation.getWorld().spawnEntity(guardLocation.clone(), getSpawnerType());
                    monster.setCustomNameVisible(true);
                    monster.setCustomName(getGroundsDisplayName() + " Guard");
                    monster.setMetadata("GUARD", new FixedMetadataValue(Foxtrot.getInstance(), true));

                    if (monster instanceof Zombie || monster instanceof Skeleton) {
                        monster.getEquipment().setHelmet(ItemBuilder.of(Material.DIAMOND_HELMET).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                        monster.getEquipment().setChestplate(ItemBuilder.of(Material.DIAMOND_CHESTPLATE).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                        monster.getEquipment().setLeggings(ItemBuilder.of(Material.DIAMOND_LEGGINGS).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                        monster.getEquipment().setBoots(ItemBuilder.of(Material.DIAMOND_BOOTS).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                        monster.getEquipment().setItemInHand(ItemBuilder.of(Material.DIAMOND_SWORD).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.DAMAGE_ALL, 2).build());
                    }

                    takenLocations.put(monster, guardLocation);
                }

            }
        }.runTaskTimer(Foxtrot.getInstance(), 20, 20);
    }

    public Team findTeam() {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(this.getGroundsID());

        if (team == null || team.getOwner() != null || team.getHQ() == null) {
            return null;
        }

        return team;
    }
}