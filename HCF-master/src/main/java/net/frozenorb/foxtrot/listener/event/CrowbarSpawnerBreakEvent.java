package net.frozenorb.foxtrot.listener.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class CrowbarSpawnerBreakEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	@Getter private final Player player;
	@Getter private final Block block;
	@Getter @Setter private boolean cancelled;

	public HandlerList getHandlers() {
		return (handlers);
	}

	public static HandlerList getHandlerList() {
		return (handlers);
	}

}
