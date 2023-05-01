package net.frozenorb.foxtrot.server.keyalls;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KeyAll {
    @Getter @Setter private String id;
    @Getter @Setter private List<ItemStack> items;
    @Getter @Setter private String displayName;
    @Getter @Setter private boolean giving;
    @Getter @Setter private String scoreboardDisplay;
    @Getter @Setter private long giveAllTime;
    @Getter @Setter private long end; // Expire will be CURRENT SYSTEM TIME + 10 mins
    @Getter @Setter private List<UUID> redeemed;

    public KeyAll(String id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.displayName = id;
        this.scoreboardDisplay = "";
        this.giveAllTime = 0;
        this.end = 0;
        this.redeemed = new ArrayList<>();
    }
}
