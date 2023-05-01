package cc.fyre.proton.bossbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
class BossBarData {

    @Getter private final int entityId;
    @Getter @Setter private String message;
    @Getter @Setter private float health;

}
