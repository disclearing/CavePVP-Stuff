package net.frozenorb.foxtrot.gameplay.events.koth.minikoth;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.*;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.minikoth.listener.MiniKOTHListener;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MiniKOTHHandler {
	@Getter private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

	@Getter
	@Setter
	private boolean scheduleEnabled;

	public MiniKOTHHandler() {
		loadSchedules();

		new WorldCreator("MiniKOTH").environment(World.Environment.THE_END).createWorld();
		new WorldCreator("MiniKOTH2").environment(World.Environment.THE_END).createWorld();
		new WorldCreator("MiniKOTH3").environment(World.Environment.THE_END).createWorld();

		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new MiniKOTHListener(), Foxtrot.getInstance());

		Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), this::activateKOTHs, 20L, 20L);
	}

	public List<Event> findMiniKOTHs() {
		return Foxtrot.getInstance().getEventHandler().getEvents().stream().filter(it -> it instanceof KOTH).map(it -> (KOTH)it).filter(KOTH::isMini).collect(Collectors.toList());
	}

	public void loadSchedules() {
		EventSchedule.clear();

		try {
			File eventSchedule = new File(Foxtrot.getInstance().getDataFolder(), "minieventSchedule.json");

			if (!eventSchedule.exists()) {
				eventSchedule.createNewFile();
				BasicDBObject schedule = new BasicDBObject();
				int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
				List<String> allevents = new ArrayList<>();

				for (Event event : findMiniKOTHs()) {
					if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel") || event.getName().equalsIgnoreCase("NetherCitadel")) {
						continue;
					}

					allevents.add(event.getName());
				}

				for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
					int day = (currentDay + dayOffset) % 365;
					EventScheduledTime[] times = new EventScheduledTime[]{
							new EventScheduledTime(day, 14, 0), // 02:00 PM EST
							new EventScheduledTime(day, 19, 0), // 07:00 PM EST
					};

					Collections.shuffle(allevents);

					if (!allevents.isEmpty()) {
						for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
							EventScheduledTime eventTime = times[eventTimeIndex];
							String eventName = allevents.get(eventTimeIndex % allevents.size());

							schedule.put(eventTime.toString(), eventName);
						}
					}
				}

				FileUtils.write(eventSchedule, Foxtrot.GSON.toJson(new JsonParser().parse(schedule.toString())));
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(eventSchedule));

			if (dbo != null) {
				for (Map.Entry<String, Object> entry : dbo.entrySet()) {
					EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
					this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void activateKOTHs() {
		// Don't start a KOTH during EOTW.
		if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || CustomTimerCreateCommand.isSOTWTimer()) {
			return;
		}

		// Don't start a KOTH if another one is active.
		for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
			if (koth.isActive()) {
				return;
			}
		}

		EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());

		if (Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().getEventSchedule().containsKey(scheduledTime)) {
			String resolvedName = Foxtrot.getInstance().getEventHandler().getMiniKOTHHandler().getEventSchedule().get(scheduledTime);
			Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(resolvedName);

			if (resolved == null) {
				Foxtrot.getInstance().getLogger().warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
				return;
			}

			resolved.activate();
		}
	}

}
