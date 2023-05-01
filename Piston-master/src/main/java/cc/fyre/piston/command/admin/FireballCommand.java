package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class FireballCommand {

    @Command(
            names = {"fireball"},
            permission = "command.fireball"
    )
    public static void execute(Player player,@Parameter(name = "speed",defaultValue = "1") int speed) {

        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);

        final Projectile projectile = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(),direction.getY(),direction.getZ()), LargeFireball.class);
        projectile.setShooter(player);
        projectile.setVelocity(direction);


    }

}
