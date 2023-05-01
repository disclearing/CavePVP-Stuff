package org.cavepvp.profiles.playerProfiles.impl;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Comment {
    @Getter private UUID target;
    @Getter private UUID player;
    @Getter private String comment;
    @Getter private List<UUID> likes = new ArrayList<>();

}
