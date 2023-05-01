package org.spigotmc;

import net.minecraft.server.MinecraftServer;

public class AsyncCatcher
{

    public static boolean enabled = org.github.paperspigot.PaperSpigotConfig.asyncCatcherFeature; // PaperSpigot - Allow disabling of AsyncCatcher from PaperSpigotConfig

    public static void catchOp(String reason)
    {
        if ( enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread )
        {
            throw new IllegalStateException( "Asynchronous " + reason + "!" );
        }
    }
}
