package net.frozenorb.foxtrot.gameplay.events.region.cavern;

import java.io.File;
import java.io.IOException;

import net.frozenorb.foxtrot.gameplay.events.region.cavern.listeners.CavernListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Claim;
import cc.fyre.proton.Proton;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class CavernHandler {

    private static File file;
    @Getter private final static String cavernTeamName = "Cavern";
    @Getter @Setter private Cavern cavern;

    public CavernHandler() {
        try {
            file = new File(Foxtrot.getInstance().getDataFolder(), "cavern.json");

            if (!file.exists()) {
                cavern = null;

                if (file.createNewFile()) {
                    Foxtrot.getInstance().getLogger().warning("Created a new Cavern json file.");
                }
            } else {
                cavern = Proton.GSON.fromJson(FileUtils.readFileToString(file), Cavern.class);
                Foxtrot.getInstance().getLogger().info("Successfully loaded the Cavern from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            if (getCavern() == null || Foxtrot.getInstance().getTeamHandler().getTeam(cavernTeamName) == null) return;
            getCavern().reset();
            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
        }, 20 * 60 * 60, 20 * 60 * 60);

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new CavernListener(), Foxtrot.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, Proton.GSON.toJson(cavern));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCavern() {
        return cavern != null;
    }

    public static Claim getClaim() {
        return Foxtrot.getInstance().getTeamHandler().getTeam(cavernTeamName).getClaims().get(0); // null if no glowmtn is set!
    }
}