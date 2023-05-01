package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.EnumMap;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class SetSpawnCommand {

    static final BlockFace[] RADIAL = new BlockFace[] {BlockFace.WEST,BlockFace.NORTH_WEST,BlockFace.NORTH,BlockFace.NORTH_EAST,BlockFace.EAST,BlockFace.SOUTH_EAST,BlockFace.SOUTH,BlockFace.SOUTH_WEST};
    static final EnumMap<BlockFace,Integer> notches;

    @Command(names = { "setspawn" }, permission = "command.setspawn")
    public static void setspawn(Player sender) {

        final Location location = sender.getLocation();
        final BlockFace face = yawToFace(location.getYaw());

        sender.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), (float)faceToYaw(face), 0.0f);
        sender.sendMessage(ChatColor.GOLD + "Set the spawn for " + ChatColor.WHITE + sender.getWorld().getName() + ChatColor.GOLD + ".");
    }

    private static BlockFace yawToFace(final float yaw) {
        return SetSpawnCommand.RADIAL[Math.round(yaw / 45.0f) & 0x7];
    }

    public static int faceToYaw(final BlockFace face) {
        return wrapAngle(45 * faceToNotch(face));
    }

    public static int faceToNotch(final BlockFace face) {

        final Integer notch = notches.get(face);

        return (notch == null) ? 0 : notch;
    }

    public static int wrapAngle(final int angle) {

        int wrappedAngle;

        for (wrappedAngle = angle; wrappedAngle <= -180; wrappedAngle += 360) {}

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

    static {
        notches = new EnumMap<>(BlockFace.class);

        for (int i = 0; i < SetSpawnCommand.RADIAL.length; ++i) {
            notches.put(SetSpawnCommand.RADIAL[i],i);
        }

    }

}
