package cc.fyre.proton.tab;

import cc.fyre.proton.Proton;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread extends Thread {

    private Plugin protocolLib = Proton.getInstance().getServer().getPluginManager().getPlugin("ProtocolLib");

    public TabThread() {
        this.setName("Proton - Tab Thread");
        this.setDaemon(true);
    }

    public void run() {
        while (Proton.getInstance().isEnabled() && this.protocolLib != null && this.protocolLib.isEnabled()) {

            for (Player online : Proton.getInstance().getServer().getOnlinePlayers()) {

                try {
                    Proton.getInstance().getTabHandler().updatePlayer(online);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            try {
                Thread.sleep(250L);
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }
        }

    }
}

