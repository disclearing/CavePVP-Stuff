package cc.fyre.proton.hologram.construct;
import cc.fyre.proton.util.EntityUtils;
import lombok.Getter;
import lombok.Setter;

public class HologramLine {

	@Getter private final int skullId;
	@Getter private final int horseId;

	@Getter @Setter private String text;

	public HologramLine(String text) {
		this.skullId = EntityUtils.getFakeEntityId();
		this.horseId = EntityUtils.getFakeEntityId();
		this.text = text;
	}

}
