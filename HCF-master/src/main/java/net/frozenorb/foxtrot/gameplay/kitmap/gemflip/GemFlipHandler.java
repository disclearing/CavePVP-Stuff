package net.frozenorb.foxtrot.gameplay.kitmap.gemflip;

import cc.fyre.piston.util.Cooldown;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipWager;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class GemFlipHandler {
    private final List<GemFlipEntry> queue;
    private final List<GemFlipWager> activeWagers;
    private final Map<UUID, Cooldown> cooldowns;
    @Getter @Setter private long totalWagered = 0;

    public GemFlipHandler() {
        this.queue = new ArrayList<>(8);
        this.activeWagers = new ArrayList<>();
        this.cooldowns = new HashMap<>();

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new GemFlipListener(this), Foxtrot.getInstance());

        this.totalWagered = Foxtrot.getInstance().getConfig().getLong("totalWagered", 0);
    }

    public void onDisable() {
        for (GemFlipEntry entry : this.queue) {
            final Player player = entry.getCreator();

            if (player == null) {
                continue;
            }

            Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), entry.getAmount(), true);
        }

        Foxtrot.getInstance().getConfig().set("totalWagered", this.totalWagered);
    }

    public GemFlipEntry getEntry(Player player) {
        return queue.stream()
                .filter(entry -> entry.getCreator().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public GemFlipWager getActiveWager(Player player) {
        return activeWagers.stream()
                .filter(wager -> wager.getCreator().getUniqueId().equals(player.getUniqueId()) || wager.getOpponent().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public GemFlipEntry removeEntry(Player player) {
        GemFlipEntry entry = getEntry(player);
        if(entry == null)
            return null;

        queue.remove(entry);
        return entry;
    }
}
