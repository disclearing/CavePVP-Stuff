package net.frozenorb.foxtrot.gameplay.events.region.glowmtn;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.HashSet;
import java.util.Set;


public class GlowMountain {

    @Getter private final Set<BlockVector> glowstone = new HashSet<>();
    @Getter @Setter private int remaining = 0; // We don't need a whole set for numbers???

    public void scan() {
        glowstone.clear(); // clean storage

        Claim claim = GlowHandler.getClaim();

        World world = Bukkit.getWorld(claim.getWorld());
        for(int x = claim.getX1(); x < claim.getX2(); x++) {
            for(int y = claim.getY1(); y < claim.getY2(); y++) {
                for(int z = claim.getZ1(); z < claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(block.getType() == Material.GLOWSTONE) {
                        glowstone.add(block.getLocation().toVector().toBlockVector());
                    }
                }
            }
        }
        remaining = glowstone.size();
    }

    public void reset() {
        // So we don't have to do any math later
        remaining = glowstone.size();

        World world = Bukkit.getWorld(GlowHandler.getClaim().getWorld());

        for(BlockVector vector : glowstone) {
            world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).setType(Material.GLOWSTONE);
        }

    }
}