package net.valorhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;

public class SetViewDistanceCommand extends Command {

    public SetViewDistanceCommand(String name) {
        super(name);
        this.usageMessage = "/" + name + " <world name> <view distance>";
        this.description = "Adjusts a world's viewdistance";
        this.setPermission( "valor.command.setviewdistance" );
    }

    @Override
    public boolean execute(CommandSender sender, String arg1, String[] args) {
        if(!testPermission(sender)) { return true; }
        
        if(args != null && args.length == 2) {
            String worldName = args[0];
            String newVD = args[1];
            int vd = -1;
            try {
                vd = Integer.parseInt(newVD);
            } catch (NumberFormatException e) {
                sender.sendMessage("'" + newVD + "' is not a valid integer.");
                return false;
            }
            World world = Bukkit.getWorld(worldName);
            if(world != null) {
                CraftWorld craftworld = (CraftWorld) world;
                int before = craftworld.getHandle().getPlayerChunkMap().getWorldViewDistance();
                craftworld.getHandle().getPlayerChunkMap().a(vd);
                int after = craftworld.getHandle().getPlayerChunkMap().getWorldViewDistance();
                if(before != after) {
                    sender.sendMessage("The view distance of world '" + world.getName() + "' was set from " + before + " to " + after);
                } else {
                    sender.sendMessage("The view distance of world '" + world.getName() + "' remained the same ( " + before + " ).");
                }
            } else {
                sender.sendMessage("World '" + worldName + "' was not found!");
            }
            return true;
        } else {
            sender.sendMessage("[mSpigot] Command - setviewdistance: " + this.description + "\nUsage: " + this.usageMessage);
        }
        return false;
    }

}
