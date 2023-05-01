package cc.fyre.proton.hologram.type;

import java.util.function.Consumer;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.hologram.builder.UpdatingHologramBuilder;

import org.bukkit.scheduler.BukkitRunnable;

public final class UpdatingHologram extends BaseHologram {

	private long interval;

	private Consumer<Hologram> updateFunction;
	private boolean showing = false;

	public UpdatingHologram(UpdatingHologramBuilder builder) {
		super(builder);

		this.interval = builder.getInterval();
		this.updateFunction = builder.getUpdateFunction();
	}


	public void send() {

		if (this.showing) {
			this.update();
			return;
		}

		super.send();

		this.showing = true;

		Proton.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Proton.getInstance(),new BukkitRunnable() {
			@Override
			public void run() {

				if (showing) {
					update();
				} else {
					cancel();
				}

			}
		},0L,this.interval*20L);

	}

	public void destroy() {
		super.destroy();
		this.showing = false;
	}

	public void update() {
		this.updateFunction.accept(this);
		super.update();
	}

}
