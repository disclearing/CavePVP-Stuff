package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class Notification {

    @Getter private UUID uuid;
    @Getter private long dateSent;
    @Getter private List<String> notification;
}
