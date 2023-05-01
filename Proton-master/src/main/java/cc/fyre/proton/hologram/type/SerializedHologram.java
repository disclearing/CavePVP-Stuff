package cc.fyre.proton.hologram.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class SerializedHologram {

    @Getter private int id;
    @Getter private Location location;
    @Getter private List<String> lines;

}
