package net.valorhcf;

import net.valorhcf.handler.MovementHandler;
import net.valorhcf.handler.PacketHandler;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public enum ValorSpigot {

    INSTANCE;

    private Set<PacketHandler> packetHandlers = new HashSet<>();
    private Set<MovementHandler> movementHandlers = new HashSet<>();

    public Set<PacketHandler> getPacketHandlers() {
        return this.packetHandlers;
    }

    public Set<MovementHandler> getMovementHandlers() {
        return this.movementHandlers;
    }

    public void addPacketHandler(PacketHandler handler) {
        Bukkit.getLogger().info("Adding packet handler: " + handler.getClass().getPackage().getName() + "." + handler.getClass().getName());
        this.packetHandlers.add(handler);
    }

    public void addMovementHandler(MovementHandler handler) {
        Bukkit.getLogger().info("Adding movement handler: " + handler.getClass().getPackage().getName() + "." + handler.getClass().getName());
        this.movementHandlers.add(handler);
    }

}
