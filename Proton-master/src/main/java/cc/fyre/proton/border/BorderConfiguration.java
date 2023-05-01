package cc.fyre.proton.border;

import cc.fyre.proton.border.action.DefaultBorderActions;
import cc.fyre.proton.border.event.border.BorderChangeEvent;
import cc.fyre.proton.border.event.player.PlayerEnterBorderEvent;
import cc.fyre.proton.border.event.player.PlayerExitBorderEvent;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BorderConfiguration {

    public static final BorderConfiguration DEFAULT_CONFIGURATION = new BorderConfiguration();

    @Getter private final Set<Consumer<BorderChangeEvent>> defaultBorderChangeActions = new HashSet<>();
    @Getter private final Set<Consumer<PlayerEnterBorderEvent>> defaultBorderEnterActions = new HashSet<>();
    @Getter private final Set<Consumer<PlayerExitBorderEvent>> defaultBorderExitActions = new HashSet<>();

    public BorderConfiguration() {
        this.defaultBorderChangeActions.add(DefaultBorderActions.ENSURE_PLAYERS_IN_BORDER);
        this.defaultBorderExitActions.add(DefaultBorderActions.PUSHBACK_ON_EXIT);
        this.defaultBorderExitActions.add(DefaultBorderActions.CANCEL_EXIT);
    }


}
