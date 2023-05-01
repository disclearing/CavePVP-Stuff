package net.frozenorb.foxtrot.util;

import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Effect;

public class FastBlockUpdate {

    private JavaPlugin javaPlugin;

    @Getter
    @Setter
    private int blocksPerTick;

    @Getter
    @Setter
    private Map<Location, Map<Material, Byte>> blocks = Maps.newLinkedHashMap();

    @Getter
    private FastBlockUpdateTask fastBlockUpdateTask = null;

    public FastBlockUpdate(JavaPlugin javaPlugin, int blocksPerTick) {
        this.javaPlugin = javaPlugin;
        this.blocksPerTick = blocksPerTick;
    }

    public void addBlock(Location location, Material material, Byte b) {
        Map<Material, Byte> inner = Maps.newHashMap();
        inner.put(material, b);

        getBlocks().put(location, inner);
    }

    public void start() {
        fastBlockUpdateTask = new FastBlockUpdateTask(getBlocksPerTick(), getBlocks());
        fastBlockUpdateTask.start();
    }

    public void cancel() {
        if (isComplete()) {
            getBlocks().clear();
            fastBlockUpdateTask = null;
        }
    }

    public boolean isComplete() {
        return fastBlockUpdateTask != null && fastBlockUpdateTask.isComplete();
    }

    public boolean isRunning() {
        return fastBlockUpdateTask != null && fastBlockUpdateTask.isRunning();
    }

    public class FastBlockUpdateTask {

        @Getter
        Map<Location, Map<Material, Byte>> blocksRemaining = Maps.newLinkedHashMap();

        private List<BukkitRunnable> tasks = Lists.newArrayList();

        private int blocksPerTicks;

        @Getter
        @Setter
        private boolean complete;

        @Getter
        @Setter
        private boolean running = true;

        FastBlockUpdateTask(int blocksPerSecond, Map<Location, Map<Material, Byte>> blocks) {
            this.blocksPerTicks = blocksPerSecond;
            this.blocksRemaining.putAll(blocks);
        }

        public void start() {
            for (int i = 0; i < (blocksRemaining.size() / blocksPerTicks) + 1; i++) {
                tasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int blocks = 0; blocks < blocksPerTicks; blocks++) {
                            if (!blocksRemaining.entrySet().iterator().hasNext()) {
                                break;
                            }

                            Map.Entry<Location, Map<Material, Byte>> entry = blocksRemaining.entrySet().iterator().next();
                            Location key = entry.getKey();
                            Map<Material, Byte> value = entry.getValue();
                            Material material = null;
                            for (Material mat : value.keySet()) {
                                material = mat;
                            }

                            Byte data = value.get(material);
                            key.getBlock().setTypeIdAndData(material.getId(), data, false);
                            key.getBlock().getWorld().playEffect(key.getBlock().getLocation(), Effect.STEP_SOUND, key.getBlock().getType());

                            blocksRemaining.remove(key);
                        }

                        if (blocksRemaining.size() < 1) {
                            setComplete(true);
                            setRunning(false);

                            for (BukkitRunnable runnable : tasks) {
                                runnable.cancel();
                            }
                        }
                    }
                });
            }

            for (int i = 0; i < tasks.size(); i++) {
                BukkitRunnable runnable = tasks.get(i);
                runnable.runTaskLater(javaPlugin, (i + 1));
            }
        }
    }
}
