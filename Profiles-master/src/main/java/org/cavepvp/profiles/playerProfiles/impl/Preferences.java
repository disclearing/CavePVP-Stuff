package org.cavepvp.profiles.playerProfiles.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Preferences {
    @Getter @Setter private List<UUID> ignoredPlayers = new ArrayList<>();
    @Getter @Setter private PlayerType sounds = PlayerType.EVERYONE;
    @Getter @Setter private PlayerType messages = PlayerType.EVERYONE;
    @Getter @Setter private boolean friendRequests = true;
    @Getter @Setter private PlayerType profileViewing = PlayerType.EVERYONE;
    @Getter @Setter private PlayerType toggleOfflineMessaging = PlayerType.EVERYONE;

    @Getter @Setter private boolean staffChat = false;
    @Getter @Setter private boolean adminChat = false;
    @Getter @Setter private boolean managerChat = false;
}
