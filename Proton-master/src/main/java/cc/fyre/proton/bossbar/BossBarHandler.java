package cc.fyre.proton.bossbar;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.EntityUtils;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.WatchableObject;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BossBarHandler {

    @Getter private Map<UUID,BossBarData> displaying = new HashMap<>();
    @Getter private Map<UUID,Integer> lastUpdatedPosition = new HashMap<>();

    private Field spawnPacketAField = null;
    private Field spawnPacketBField = null;
    private Field spawnPacketCField = null;
    private Field spawnPacketDField = null;
    private Field spawnPacketEField = null;
    private Field spawnPacketLField = null;
    private Field metadataPacketAField = null;
    private Field metadataPacketBField = null;
    private TObjectIntHashMap classToIdMap = null;

    public BossBarHandler() {

        Proton.getInstance().getServer().getPluginManager().registerEvents(new BossBarListener(),Proton.getInstance());

        try {
            this.spawnPacketAField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
            this.spawnPacketAField.setAccessible(true);
            this.spawnPacketBField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("b");
            this.spawnPacketBField.setAccessible(true);
            this.spawnPacketCField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c");
            this.spawnPacketCField.setAccessible(true);
            this.spawnPacketDField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("d");
            this.spawnPacketDField.setAccessible(true);
            this.spawnPacketEField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("e");
            this.spawnPacketEField.setAccessible(true);
            this.spawnPacketLField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            this.spawnPacketLField.setAccessible(true);
            this.metadataPacketAField = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            this.metadataPacketAField.setAccessible(true);
            this.metadataPacketBField = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            this.metadataPacketBField.setAccessible(true);

            final Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");

            dataWatcherClassToIdField.setAccessible(true);

            this.classToIdMap = (TObjectIntHashMap)dataWatcherClassToIdField.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Proton.getInstance().getServer().getScheduler().runTaskTimer(Proton.getInstance(),() -> {

            for (UUID uuid : this.displaying.keySet()) {

                final Player player = Proton.getInstance().getServer().getPlayer(uuid);

                if (player == null) {
                    continue;
                }

                final int updateTicks = ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() != 47 ? 60 : 3;

                if (this.lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    return;
                }

                this.updatePosition(player);

                this.lastUpdatedPosition.put(player.getUniqueId(),MinecraftServer.currentTick);
            }

        },1,1);

    }


    public void setBossBar(Player player, String message, float health) {

        try {

            if (message == null) {
                removeBossBar(player);
                return;
            }

            Preconditions.checkArgument(health >= 0.0F && health <= 1.0F, "Health must be between 0 and 1");

            if (message.length() > 64) {
                message = message.substring(0, 64);
            }

            message = ChatColor.translateAlternateColorCodes('&', message);

            if (!this.displaying.containsKey(player.getUniqueId())) {
                this.sendSpawnPacket(player, message, health);
            } else {
                this.sendUpdatePacket(player, message, health);
            }

            final BossBarData bossBarData = this.displaying.get(player.getUniqueId());

            bossBarData.setMessage(message);
            bossBarData.setHealth(health);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void removeBossBar(Player player) {

        if (!this.displaying.containsKey(player.getUniqueId())) {
            return;
        }

        final int entityId = this.displaying.get(player.getUniqueId()).getEntityId();

        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{entityId}));

        this.displaying.remove(player.getUniqueId());
        this.lastUpdatedPosition.remove(player.getUniqueId());
    }

    private void sendSpawnPacket(Player bukkitPlayer, String message, float health) throws Exception {

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = player.playerConnection.networkManager.getVersion();

        this.displaying.put(bukkitPlayer.getUniqueId(), new BossBarData(EntityUtils.getFakeEntityId(), message, health));

        final BossBarData stored = this.displaying.get(bukkitPlayer.getUniqueId());

        final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        this.spawnPacketAField.set(packet,stored);

        final DataWatcher watcher = new DataWatcher((Entity)null);

        if (version != 47) {
            this.spawnPacketBField.set(packet, (byte)EntityType.ENDER_DRAGON.getTypeId());
            watcher.a(6, health * 200.0F);
            this.spawnPacketCField.set(packet, (int)(player.locX * 32.0D));
            this.spawnPacketDField.set(packet, -6400);
            this.spawnPacketEField.set(packet, (int)(player.locZ * 32.0D));
        } else {
            this.spawnPacketBField.set(packet, (byte)EntityType.WITHER.getTypeId());

            watcher.a(6, health * 300.0F);
            watcher.a(20, 880);

            final double pitch = Math.toRadians((double)player.pitch);
            final double yaw = Math.toRadians((double)player.yaw);

            this.spawnPacketCField.set(packet, (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D));
            this.spawnPacketDField.set(packet, (int)((player.locY - Math.sin(pitch) * 32.0D) * 32.0D));
            this.spawnPacketEField.set(packet, (int)((player.locZ + Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D));
        }

        watcher.a(version != 47 ? 10 : 2, message);
        this.spawnPacketLField.set(packet, watcher);
        player.playerConnection.sendPacket(packet);
    }

    private void sendUpdatePacket(Player bukkitPlayer, String message, float health) throws IllegalAccessException {

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = player.playerConnection.networkManager.getVersion();
        final BossBarData stored = this.displaying.get(bukkitPlayer.getUniqueId());
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();

        this.metadataPacketAField.set(packet,stored);

        final List<WatchableObject> objects = new ArrayList<>();

        if (health != stored.getHealth()) {

            if (version != 47) {
                objects.add(createWatchableObject(6, health * 200.0F));
            } else {
                objects.add(createWatchableObject(6, health * 300.0F));
            }

        }

        if (!message.equals(stored.getMessage())) {
            objects.add(createWatchableObject(version != 47 ? 10 : 2, message));
        }

        this.metadataPacketBField.set(packet, objects);
        player.playerConnection.sendPacket(packet);
    }

    private WatchableObject createWatchableObject(int id, Object object) {
        return new WatchableObject(this.classToIdMap.get(object.getClass()), id, object);
    }

    private void updatePosition(Player bukkitPlayer) {

        if (!this.displaying.containsKey(bukkitPlayer.getUniqueId())) {
            return;
        }

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = player.playerConnection.networkManager.getVersion();

        int x;
        int y;
        int z;

        if (version != 47) {
            x = (int)(player.locX * 32.0D);
            y = -6400;
            z = (int)(player.locZ * 32.0D);
        } else {
            final double pitch = Math.toRadians((double)player.pitch);
            final double yaw = Math.toRadians((double)player.yaw);
            x = (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
            y = (int)((player.locY - Math.sin(pitch) * 32.0D) * 32.0D);
            z = (int)((player.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
        }

        player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(this.displaying.get(bukkitPlayer.getUniqueId()).getEntityId(),x,y,z,(byte)0,(byte)0));
    }
}
