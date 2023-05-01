package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.listener.PortalTrapListener;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import cc.fyre.proton.cuboid.Cuboid;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Portal {

    private PortalDirection direction;
    private Cuboid portal;

    public Portal(Block block, PortalDirection direction) {
        if (block.getType() != Material.PORTAL) {
            return;
        }
        this.direction = direction;

        if (direction == null) {
            return;
        }

        if (direction == PortalDirection.NORTH_SOUTH) {

            int minX = block.getX();
            int maxX = block.getX();
            int minY = block.getY() - 1;

            int maxZ = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // z++
                Block next = block.getRelative(BlockFace.SOUTH, i);
                if (next.getType() != Material.PORTAL) {
                    maxZ = next.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getZ();
                    maxZ = maxZ < 0 ? maxZ + 1 : maxZ - 1;
                    break;
                }
            }

            int minZ = Integer.MAX_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // z--
                Block next = block.getRelative(BlockFace.NORTH, i);
                if (next.getType() != Material.PORTAL) {
                    minZ = next.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getZ();
                    minZ = minZ < 0 ? minZ - 1 : minZ + 1;
                    break;
                }
            }

            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // y++
                Block next = block.getRelative(BlockFace.UP, i);
                if (next.getType() != Material.PORTAL) {
                    maxY = next.getY();
                    break;
                }
            }

            this.portal = new Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            int minZ = block.getZ();
            int maxZ = block.getZ();
            int minY = block.getY() - 1;

            int maxX = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // x++
                Block next = block.getRelative(BlockFace.EAST, i);
                if (next.getType() != Material.PORTAL) {
                    maxX = next.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getX();
                    maxX = maxX < 0 ? maxX + 1 : maxX - 1;
                    break;
                }
            }

            int minX = Integer.MAX_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // x--
                Block next = block.getRelative(BlockFace.WEST, i);
                if (next.getType() != Material.PORTAL) {
                    minX = next.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getX();
                    minX = minX < 0 ? minX - 1 : minX + 1;
                    break;
                }
            }

            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // y++
                Block next = block.getRelative(BlockFace.UP, i);
                if (next.getType() != Material.PORTAL) {
                    maxY = next.getY();
                    break;
                }
            }

            this.portal = new Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    public void show(Cuboid cuboid) {
        for (Block block : cuboid.corners()) {
            block.setType(Material.STONE);
        }
    }

    public void patchOverworld() {
        Cuboid expanded;
        if (direction == PortalDirection.NORTH_SOUTH) {
            expanded = portal.expand(Cuboid.CuboidDirection.SOUTH, 1).expand(Cuboid.CuboidDirection.NORTH, 1);
        } else {
            expanded = portal.expand(Cuboid.CuboidDirection.EAST, 1).expand(Cuboid.CuboidDirection.WEST, 1);
        }

        first:
        for (Block block : expanded) {
            if (!portal.contains(block)) {
                for (BlockFace f : PortalTrapListener.FACES) {
                    Block relative = block.getRelative(f);
                    if (relative.getType() == Material.PORTAL) {
                        if (block.getType() != Material.AIR) {
                            Claim bClaim = LandBoard.getInstance().getClaim(block.getLocation());

                            if (bClaim == null || bClaim.contains(relative)) {
                                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
                                block.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
                            }

                            continue first;
                        }
                    }
                }
            }
        }
    }

    public void patchNether() {
        Cuboid expanded;
        if (direction == PortalDirection.NORTH_SOUTH) {
            expanded = portal.expand(Cuboid.CuboidDirection.SOUTH, 4).expand(Cuboid.CuboidDirection.NORTH, 4).expand(Cuboid.CuboidDirection.EAST, 4).expand(Cuboid.CuboidDirection.WEST, 4);
        } else {
            expanded = portal.expand(Cuboid.CuboidDirection.EAST, 4).expand(Cuboid.CuboidDirection.WEST, 4).expand(Cuboid.CuboidDirection.SOUTH, 4).expand(Cuboid.CuboidDirection.NORTH, 4);
        }
        Cuboid down = expanded.getFace(Cuboid.CuboidDirection.DOWN);

        for (Block block : expanded) {
            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }

            if (!portal.contains(block) && block.getType() != Material.AIR & !down.contains(block)) {
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
                block.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
            }
        }

        for (Block block : down) {
            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }
            if (block.getType() == Material.OBSIDIAN) {
                continue;
            }

            block.setTypeIdAndData(Material.OBSIDIAN.getId(), (byte) 0, false);
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
        }
    }
}
