package cc.fyre.proton.visibility;

import cc.fyre.proton.visibility.action.OverrideAction;
import cc.fyre.proton.visibility.action.VisibilityAction;
import cc.fyre.proton.visibility.listener.VisibilityListener;
import lombok.Getter;
import cc.fyre.proton.Proton;

import cc.fyre.proton.visibility.provider.OverrideProvider;
import cc.fyre.proton.visibility.provider.VisibilityProvider;
import org.bukkit.entity.Player;

import java.util.*;

public class VisibilityHandler {

    @Getter private final Map<String,VisibilityProvider> handlers = new LinkedHashMap<>();
    @Getter private final Map<String,OverrideProvider> overrideHandlers = new LinkedHashMap<>();

    public VisibilityHandler() {
        Proton.getInstance().getServer().getPluginManager().registerEvents(new VisibilityListener(),Proton.getInstance());
    }

    public void registerHandler(String identifier, VisibilityProvider handler) {
        this.handlers.put(identifier, handler);
    }

    public void registerOverride(String identifier, OverrideProvider handler) {
        this.overrideHandlers.put(identifier, handler);
    }

    public void update(Player player) {

        if (!this.handlers.isEmpty() || !this.overrideHandlers.isEmpty()) {
            this.updateAllTo(player);
            this.updateToAll(player);
        }

    }

    /** @deprecated */
    @Deprecated
    public void updateAllTo(Player viewer) {
        
        for (Player target : Proton.getInstance().getServer().getOnlinePlayers()) {

            if (!this.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
            
        }
        
    }

    /** @deprecated */
    @Deprecated
    public void updateToAll(Player target) {
        
        for (Player viewer : Proton.getInstance().getServer().getOnlinePlayers()) {

            if (!this.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
            
        }

    }

    public boolean treatAsOnline(Player target, Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible");
    }

    private boolean shouldSee(Player target,Player viewer) {

        for (VisibilityProvider visibilityProvider : this.handlers.values()) {

            for (OverrideProvider overrideProvider : this.overrideHandlers.values()) {
                return overrideProvider.getAction(target,viewer) == OverrideAction.SHOW;
            }

            return visibilityProvider.getAction(target,viewer) == VisibilityAction.NEUTRAL;
        }

        return true;
    }
}
