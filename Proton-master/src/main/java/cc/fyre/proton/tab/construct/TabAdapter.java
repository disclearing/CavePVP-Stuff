package cc.fyre.proton.tab.construct;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.util.TabUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.util.UUID;

public class TabAdapter extends PacketAdapter {

    private static Field playerField;
    private static Field namedEntitySpawnField;

    private static Field propertyValueField;
    private static Field propertySignatureField;

    public TabAdapter() {
        super(Proton.getInstance(),PacketType.Play.Server.PLAYER_INFO,PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    }

    public void onPacketSending(PacketEvent event) {
        if (Proton.getInstance().getTabHandler().getLayoutProvider() != null && this.shouldForbid(event.getPlayer())) {
            if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                PacketContainer packetContainer = event.getPacket();

                if (packetContainer.getStrings() == null || packetContainer.getStrings().read(0) == null) {
                    return;
                }

                String name = packetContainer.getStrings().read(0);
                boolean isOurs = packetContainer.getStrings().read(0).startsWith("$");
                int action = packetContainer.getIntegers().read(1);
                if (!isOurs && !SpigotConfig.onlyCustomTab) {
                    if (action != 4 && shouldCancel(event.getPlayer(), (PacketPlayOutPlayerInfo) event.getPacket().getHandle())) {
                        event.setCancelled(true);
                    }
                }

                if (isOurs) {
                    packetContainer.getStrings().write(0, name.replace("$", ""));
                }
            } else if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN && TabUtils.is18(event.getPlayer()) && Proton.getInstance().getServer().getPluginManager().getPlugin("UHC") == null) {
                PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn) event.getPacket().getHandle();

                GameProfile gameProfile;

                try {
                    gameProfile = (GameProfile) namedEntitySpawnField.get(packet);
                    if (gameProfile == null) {
                        throw new IllegalStateException("Missing profile");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                if (!SpigotConfig.onlyCustomTab) {
                    Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> {
                        final Player bukkitPlayer = Proton.getInstance().getServer().getPlayer(gameProfile.getId());

                        if (bukkitPlayer != null) {
                            ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) bukkitPlayer).getHandle()));
                        }
                    }, 20L);
                }
            }

        }
    }

    public static boolean shouldCancel(Player player, PacketPlayOutPlayerInfo playerInfoPacket) {
        if (!TabUtils.is18(player)) {
            return true;
        } else {
            final EntityPlayer recipient = ((CraftPlayer)player).getHandle();

            UUID tabPacketPlayer;
            try {
                tabPacketPlayer = ((GameProfile)playerField.get(playerInfoPacket)).getId();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            final Player bukkitPlayer = Proton.getInstance().getServer().getPlayer(tabPacketPlayer);
            if (bukkitPlayer == null) {
                return true;
            } else {
                final EntityTrackerEntry trackerEntry = (EntityTrackerEntry)((WorldServer)((CraftPlayer)bukkitPlayer).getHandle().getWorld()).getTracker().trackedEntities.get(bukkitPlayer.getEntityId());
                if (trackerEntry == null) {
                    return true;
                } else {
                    return !trackerEntry.trackedPlayers.contains(recipient);
                }
            }
        }
    }

    private boolean shouldForbid(Player player) {
        String playerName = player.getName();
        Tab playerTab = Proton.getInstance().getTabHandler().getTabs().get(playerName);
        return playerTab != null && playerTab.isInitiated();
    }

    static {

        try {
            playerField = PacketPlayOutPlayerInfo.class.getDeclaredField("player");
            playerField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            namedEntitySpawnField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
            namedEntitySpawnField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            propertyValueField = Property.class.getDeclaredField("value");
            propertyValueField.setAccessible(true);

            propertySignatureField = Property.class.getDeclaredField("signature");
            propertySignatureField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}