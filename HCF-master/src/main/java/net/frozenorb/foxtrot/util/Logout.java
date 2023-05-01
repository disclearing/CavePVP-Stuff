package net.frozenorb.foxtrot.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Logout {

    private final int taskId;
    private final long logoutTime;
}
