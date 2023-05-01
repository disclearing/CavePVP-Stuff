package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class MobCommand {

    @Command(names = { "spawnmob", "mob" }, permission = "command.spawnmob", description = "Spawn mobs! Supports stacking")
    public static void execute(final Player sender,@Parameter(name = "mob[,mob,mob...]") final String mobs,@Parameter(name = "amount", defaultValue = "1") final int amount) {

        final String[] split = mobs.split(",");
        final List<EntityType> types = new ArrayList<>();

        for (String part : split) {

            final EntityType type = EntityUtils.parse(part);

            if (type == null) {
                sender.sendMessage(ChatColor.RED + "Mob '" +  ChatColor.YELLOW + part + ChatColor.RED + "' not found.");
                return;
            }

            if (!type.isAlive()) {
                sender.sendMessage(ChatColor.RED + "Entity type '" + ChatColor.YELLOW + part + ChatColor.RED + "' is not a valid mob.");
                return;
            }

            types.add(type);
        }

        if (sender.getTargetBlock(null,30) == null) {
            sender.sendMessage(ChatColor.RED + "Please look at a block.");
            return;
        }

        if (types.size() == 0) {
            sender.sendMessage(ChatColor.RED + "Idk how you got here but um... Nope.");
            return;
        }


        final Location location = sender.getTargetBlock(null,30).getLocation();

        int totalAmount = 0;

        for (int i = 0; i < amount; ++i) {

            final Entity current = sender.getWorld().spawnEntity(location,types.get(0));
            ++totalAmount;

            for (int x = 1; x < types.size(); ++x) {
                final Entity newEntity = sender.getWorld().spawnEntity(location,types.get(x));
                current.setPassenger(newEntity);
                ++totalAmount;
            }

        }

        sender.sendMessage(ChatColor.GOLD + "Spawned " + ChatColor.WHITE + totalAmount + ChatColor.GOLD + " entities.");
    }

}
