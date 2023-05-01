package cc.fyre.proton.hologram.builder;

import cc.fyre.proton.hologram.type.UpdatingHologram;
import lombok.Getter;
import cc.fyre.proton.hologram.construct.Hologram;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class UpdatingHologramBuilder extends HologramBuilder {

	@Getter private long interval;
	@Getter private Consumer<Hologram> updateFunction;

	public UpdatingHologramBuilder(HologramBuilder hologramBuilder) {
		super(hologramBuilder.getViewers());

		this.lines = hologramBuilder.getLines();
		this.at(hologramBuilder.getLocation());
	}

	public UpdatingHologramBuilder interval(long time, TimeUnit unit) {
		this.interval = unit.toSeconds(time);
		return this;
	}

	public UpdatingHologramBuilder onUpdate(Consumer<Hologram> onUpdate) {
		this.updateFunction = onUpdate;
		return this;
	}

	public Hologram build() {
		return new UpdatingHologram(this);
	}
}
