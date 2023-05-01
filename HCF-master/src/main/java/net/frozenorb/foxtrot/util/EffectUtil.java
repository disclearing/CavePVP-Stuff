package net.frozenorb.foxtrot.util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EffectUtil {

	public static void splash(Player player, Location location) {
		player.playEffect(location, Effect.POTION_BREAK, 4);
	}

	public static void bleed(Player player) {
		player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
	}

}
