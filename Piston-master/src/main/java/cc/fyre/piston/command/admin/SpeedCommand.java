package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpeedCommand {

    @Command(
            names = {"espeed"},
            permission = "command.speed"
    )
    public static void speed(Player sender,@Parameter(name = "speed",defaultValue = "1") int speed,@Parameter(name = "player",defaultValue = "self")Player player) {

        if (speed <= 0) {
            sender.sendMessage(ChatColor.RED + "Speed must be positive.");
            return;
        }

        if (speed > 10) {
            speed = 10;
        }

        boolean fly = sender.isFlying();

        if (fly) {
            player.setFlySpeed(getSpeed(speed,true));
        } else {
            player.setWalkSpeed(getSpeed(speed,false));
        }

        player.sendMessage(ChatColor.GOLD + (fly ? "Fly" : "Walk") + " speed set to " + ChatColor.WHITE + speed + ChatColor.GOLD + ".");

        if (!sender.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.GOLD + "Set " + player.getDisplayName() + ChatColor.GOLD + "'s " + (fly ? "fly" : "walk") + " to " + ChatColor.WHITE + speed + ChatColor.GOLD + ".");
        }

    }

    private static float getSpeed(int speed, boolean isFly) {

        final float defaultSpeed = isFly ? 0.1F : 0.2F;
        final float maxSpeed = 1.0F;

        if (speed < 1.0F) {
            return defaultSpeed * (float)speed;
        } else {
            float ratio = ((float)speed - 1.0F) / 9.0F * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }

}
