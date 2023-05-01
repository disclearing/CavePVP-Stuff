package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class Message {
    @Getter private UUID target;
    @Getter private UUID sender;
    @Getter private boolean read;
    @Getter private String comment;
}
