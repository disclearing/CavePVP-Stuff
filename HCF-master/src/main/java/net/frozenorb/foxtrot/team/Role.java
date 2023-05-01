package net.frozenorb.foxtrot.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Role {
    MEMBER("Member", 1),
    CAPTAIN("Captain", 2),
    COLEADER("Co-Leader", 3);

    @Getter String roleName;
    @Getter int number;
}
