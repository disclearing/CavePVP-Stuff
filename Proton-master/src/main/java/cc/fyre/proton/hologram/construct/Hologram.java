package cc.fyre.proton.hologram.construct;

import java.util.Collection;
import java.util.List;

import cc.fyre.proton.hologram.type.SerializedHologram;
import org.bukkit.Location;

public interface Hologram {

	int id();

	void send();

	void destroy();

	void delete();

	void move(Location location);

	void setLine(int paramInt,String paramString);

	void setLines(Collection<String> paramCollection);

	void addLines(String... paramVarArgs);

	void removeLine(int number);

	List<String> getLines();

	Location getLocation();

	SerializedHologram toSerializedHologram();
}
