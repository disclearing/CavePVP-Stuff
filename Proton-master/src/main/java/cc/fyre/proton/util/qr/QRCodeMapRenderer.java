package cc.fyre.proton.util.qr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class QRCodeMapRenderer extends MapRenderer {

    @Getter private UUID uuid;
    @Getter private BufferedImage image;

    public void render(MapView map,MapCanvas canvas,Player player) {

        if (player.getUniqueId().equals(this.uuid)) {
            canvas.drawImage(0, 0, this.image);
            this.image = null;
        }

    }
}