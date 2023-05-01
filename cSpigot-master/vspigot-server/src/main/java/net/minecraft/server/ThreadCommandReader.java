package net.minecraft.server;

import java.io.IOException;

import static org.bukkit.craftbukkit.Main.useConsole;
import static org.bukkit.craftbukkit.Main.useJline;

class ThreadCommandReader extends Thread {

    final DedicatedServer server;

    ThreadCommandReader(DedicatedServer dedicatedserver, String s) {
        super(s);
        this.server = dedicatedserver;
    }

    public void run() {
        // CraftBukkit start
        if (!useConsole) {
            return;
        }
        // CraftBukkit end

        jline.console.ConsoleReader bufferedreader = this.server.reader; // CraftBukkit
        String s;

        try {
            // CraftBukkit start - JLine disabling compatibility
            while (!this.server.isStopped() && this.server.isRunning()) {
                if (useJline) {
                    s = bufferedreader.readLine(">", null);
                } else {
                    s = bufferedreader.readLine();
                }
                if (s != null) {
                    this.server.issueCommand(s, this.server);
                }
                // CraftBukkit end
            }
        } catch (IOException ioexception) {
            DedicatedServer.aF().error("Exception handling console input", ioexception);
        }
    }
}
