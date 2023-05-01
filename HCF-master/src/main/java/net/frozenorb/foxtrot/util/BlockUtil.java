package net.frozenorb.foxtrot.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class BlockUtil {

	public List<Block> getBlocksAroundCenter(Location loc, int radius) {
		List<Block> blocks = new ArrayList<>();

		for (int x = (loc.getBlockX()-radius); x <= (loc.getBlockX()+radius); x++) {
			for (int y = (loc.getBlockY()-radius); y <= (loc.getBlockY()+radius); y++) {
				for (int z = (loc.getBlockZ()-radius); z <= (loc.getBlockZ()+radius); z++) {
					Location l = new Location(loc.getWorld(), x, y, z);
					if (l.distance(loc) <= radius) {
						blocks.add(l.getBlock());
					}
				}
			}
		}

		return blocks;
	}
}
