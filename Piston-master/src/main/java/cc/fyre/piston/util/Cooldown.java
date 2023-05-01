package cc.fyre.piston.util;

import lombok.Getter;
import lombok.Setter;

public class Cooldown {

	@Getter @Setter private long start = System.currentTimeMillis();
	@Getter @Setter private long expire;
	@Getter @Setter private boolean notified;

	public Cooldown(long duration) {

		this.expire = this.start + duration;

		if (duration == 0) {
			this.notified = true;
		}

	}

	public long getPassed() {
		return System.currentTimeMillis() - this.start;
	}

	public long getRemaining() {
		return this.expire - System.currentTimeMillis();
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() - this.expire >= 0;
	}

}
