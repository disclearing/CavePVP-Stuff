package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerCommand {

    @Command(
            names = {"spawner"},
            permission = "hcf.command.spawner"
    )
    public static void execute(Player sender, @Parameter(name = "mob") String mob) {

        final EntityType type = EntityUtils.parse(mob);

        if (type == null || !type.isAlive()) {
            sender.sendMessage(ChatColor.RED + "Mob " + ChatColor.YELLOW + mob + ChatColor.RED + " not found.");
            return;
        }

        final Block block = sender.getTargetBlock(null,5);

        if (block == null || !(block.getState() instanceof CreatureSpawner)) {
            sender.sendMessage(ChatColor.RED + "You are not looking at a spawner.");
            return;
        }

        final CreatureSpawner creatureSpawner = (CreatureSpawner)block.getState();

        creatureSpawner.setSpawnedType(type);
        creatureSpawner.update();
        sender.sendMessage(ChatColor.GOLD + "Updated this spawner to a " + ChatColor.WHITE + EntityUtils.getName(type) + ChatColor.GOLD + " spawner.");
    }
}

