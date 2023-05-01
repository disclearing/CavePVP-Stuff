package net.frozenorb.foxtrot.listener.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class EnderpearlCooldownAppliedEvent extends Event {

	@Getter
	private static HandlerList handlerList = new HandlerList();

	@Getter
	private final Player player;

	@Getter
	@Setter
	private long timeToApply;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
