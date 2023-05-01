package cc.fyre.proton.command.param.defaults.offlineplayer;

import java.util.*;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.Callback;
import cc.fyre.proton.util.UUIDUtils;
import lombok.Getter;
import org.bukkit.entity.*;

import org.bukkit.craftbukkit.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.*;
import net.minecraft.server.v1_7_R4.*;

public class OfflinePlayerWrapper {

    private String source;

    @Getter private UUID uniqueId;
    @Getter private String name;

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }

    public void loadAsync(Callback<Player> callback) {
        Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(),() -> callback.callback(this.loadSync()));
    }

    public Player loadSync() {
        if ((this.source.charAt(0) == '\"' || this.source.charAt(0) == '\'') && (this.source.charAt(this.source.length() - 1) == '\"' || this.source.charAt(this.source.length() - 1) == '\'')) {

            this.source = this.source.replace("'", "").replace("\"", "");

            this.uniqueId = UUIDUtils.uuid(this.source);

            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }

            this.name = UUIDUtils.name(this.uniqueId);

            if (Proton.getInstance().getServer().getPlayer(this.uniqueId) != null) {
                return Proton.getInstance().getServer().getPlayer(this.uniqueId);
            }
            if (!Proton.getInstance().getServer().getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }

            final MinecraftServer server = ((CraftServer)Proton.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();

            if (player != null) {
                player.loadData();
            }

            return player;
        } else {

            if (Proton.getInstance().getServer().getPlayer(this.source) != null) {
                return Proton.getInstance().getServer().getPlayer(this.source);
            }

            this.uniqueId = UUIDUtils.uuid(this.source);

            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }

            this.name = UUIDUtils.name(this.uniqueId);

            if (Proton.getInstance().getServer().getPlayer(this.uniqueId) != null) {
                return Proton.getInstance().getServer().getPlayer(this.uniqueId);
            }

            if (!Proton.getInstance().getServer().getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }

            final MinecraftServer server = ((CraftServer)Proton.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager((World)server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        }
    }

}