package cc.fyre.neutron.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public class DurationWrapper {

    @Getter @Setter private String source;
    @Getter @Setter private Long duration;

    public boolean isPermanent() {
        return this.duration.equals((long) Integer.MAX_VALUE);
    }
}
