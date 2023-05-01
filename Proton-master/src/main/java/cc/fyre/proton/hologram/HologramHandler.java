package cc.fyre.proton.hologram;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.builder.HologramBuilder;
import cc.fyre.proton.hologram.type.SerializedHologram;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import cc.fyre.proton.hologram.construct.Hologram;

import cc.fyre.proton.hologram.listener.HologramListener;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.entity.Player;

public class HologramHandler {

	@Getter private Map<Integer,Hologram> cache = new HashMap<>();

	//TODO: hologram insertline command

	public HologramHandler() {

		final File file = new File(Proton.getInstance().getDataFolder(),"holograms.json");

		List<SerializedHologram> holograms = null;

		if (file.exists()) {

			try {
				holograms = Proton.GSON.fromJson(FileUtils.readFileToString(file),new TypeToken<List<SerializedHologram>>(){}.getType());
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		if (holograms != null) {
			holograms.forEach(hologram -> this.cache.put(hologram.getId(),this.createHologram().addLines(hologram.getLines()).at(hologram.getLocation()).build(hologram.getId())));
		}

		Proton.getInstance().getServer().getPluginManager().registerEvents(new HologramListener(),Proton.getInstance());
		Proton.getInstance().getCommandHandler().registerPackage(Proton.getInstance(),"cc.fyre.proton.hologram.command");
	}

	public void register(Hologram hologram) {
		this.cache.put(hologram.id(),hologram);
		this.save();
	}

	public void unRegister(Hologram hologram) {
		this.cache.remove(hologram.id());
		this.save();
	}

	public void save() {

		final List<SerializedHologram> toSerialize = this.cache.values().stream().map(Hologram::toSerializedHologram).collect(Collectors.toList());
		final File file = new File(Proton.getInstance().getDataFolder(),"holograms.json");

		if (!file.exists()) {

			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		try {
			FileUtils.write(file,Proton.GSON.toJson(toSerialize));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public int createId() {
		int id;

		for (id = this.cache.size() + 1; this.cache.get(id) != null; ++id) {}

		return id;
	}


	public HologramBuilder forPlayer(Player player) {
		return new HologramBuilder(Collections.singleton(player.getUniqueId()));
	}

	public HologramBuilder forPlayers(Collection<Player> players) {

		if (players == null) {
			return new HologramBuilder(null);
		}

		return new HologramBuilder(players.stream().map(Player::getUniqueId).collect(Collectors.toList()));
	}

	public HologramBuilder createHologram() {
		return forPlayers(null);
	}

	public Hologram fromId(int id) {
		return this.cache.get(id);
	}
}
