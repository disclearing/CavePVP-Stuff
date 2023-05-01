package cc.fyre.proton.hologram.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.type.BaseHologram;
import lombok.Getter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.Location;

public class HologramBuilder {

	@Getter protected List<String> lines;

	@Getter private Location location;
	@Getter private Collection<UUID> viewers;


	public HologramBuilder(Collection<UUID> viewers) {
		this.viewers = viewers;
		this.lines = new ArrayList<>();
	}

	public HologramBuilder addLines(Iterable<String> lines) {

		for (String line : lines) {
			this.lines.add(line);
		}

		return this;
	}

	public HologramBuilder addLines(String... lines) {
		this.lines.addAll(Arrays.asList(lines));
		return this;
	}

	public HologramBuilder at(Location location) {
		this.location = location;
		return this;
	}

	public UpdatingHologramBuilder updates() {
		return new UpdatingHologramBuilder(this);
	}

	public Hologram build() {
		return this.build(Proton.getInstance().getHologramHandler().createId());
	}

	public Hologram build(int id) {
		return new BaseHologram(this,id);
	}

}
