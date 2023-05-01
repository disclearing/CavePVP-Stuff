package net.frozenorb.foxtrot.team.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class PlayerJoinTeamEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();

	private final Player player;
	private final Team team;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
