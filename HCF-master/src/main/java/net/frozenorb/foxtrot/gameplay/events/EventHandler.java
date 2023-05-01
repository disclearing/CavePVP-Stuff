package net.frozenorb.foxtrot.gameplay.events;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTCListener;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.listeners.KOTHListener;
import cc.fyre.proton.Proton;
import net.frozenorb.foxtrot.gameplay.events.koth.minikoth.MiniKOTHHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

public class EventHandler {

	@Getter private final Set<Event> events = new HashSet<>();
	@Getter private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

	@Getter
	@Setter
	private boolean scheduleEnabled;

	@Getter private MiniKOTHHandler miniKOTHHandler;

	public EventHandler() {

		if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
			this.miniKOTHHandler = new MiniKOTHHandler();
		}

		loadEvents();
		loadSchedules();

		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), Foxtrot.getInstance());
		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new DTCListener(), Foxtrot.getInstance());
		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), Foxtrot.getInstance());
		Proton.getInstance().getCommandHandler().registerParameterType(Event.class, new EventParameterType());

		new BukkitRunnable() {
			public void run() {
				for (Event event : events) {
					if (event.isActive()) {
						event.tick();
					}
				}
			}
		}.runTaskTimer(Foxtrot.getInstance(), 5L, 20L);

		Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), this::activateKOTHs, 20L, 20L);
		// The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
	}

	public KOTH getActiveKOTH() {
		return events.stream().filter(it -> it instanceof KOTH && it.isActive()).map(it -> (KOTH)it).findFirst().orElse(null);
	}

	public void loadEvents() {
		try {
			File eventsBase = new File(Foxtrot.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {
				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					if (eventFile.getName().endsWith(".json")) {
						events.add(Foxtrot.GSON.fromJson(FileUtils.readFileToString(eventFile), eventType == EventType.KOTH ? KOTH.class : DTC.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// look for a previously active Event, if present deactivate and start it after 15 seconds
		events.stream().filter(Event::isActive).findFirst().ifPresent((event) -> {
			event.setActive(false);
			Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
				// if anyone had started a Event within the last 15 seconds,
				// don't activate previously active one
				if (events.stream().noneMatch(Event::isActive)) {
					event.activate();
				}
			}, 300L);
		});
	}

	public void fillSchedule() {
		List<String> allevents = new ArrayList<>();

		for (Event event : getEvents()) {
			if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel") || event.getName().equalsIgnoreCase("NetherCitadel")) {
				continue;
			}

			allevents.add(event.getName());
		}

		for (int minute = 0; minute < 60; minute++) {
			for (int hour = 0; hour < 24; hour++) {
				this.EventSchedule.put(new EventScheduledTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + hour) % 24, minute), allevents.get(0));
			}
		}
	}

	public void loadSchedules() {
		EventSchedule.clear();

		try {
			File eventSchedule = new File(Foxtrot.getInstance().getDataFolder(), "eventSchedule.json");

			if (!eventSchedule.exists()) {
				eventSchedule.createNewFile();
				BasicDBObject schedule = new BasicDBObject();
				int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
				List<String> allevents = new ArrayList<>();

				for (Event event : getEvents()) {
					if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel") || event.getName().equalsIgnoreCase("NetherCitadel") || event.getName().contains("Outpost")) {
						continue;
					}

					if (event instanceof KOTH && ((KOTH) event).isMini()) {
						continue;
					}

					allevents.add(event.getName());
				}

				for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
					int day = (currentDay + dayOffset) % 365;
					EventScheduledTime[] times = new EventScheduledTime[]{
							new EventScheduledTime(day, 0, 0), // 12:00 AM EST
							new EventScheduledTime(day, 1, 30), // 1:30 AM EST
							new EventScheduledTime(day, 3, 0), // 3:00 AM EST
							new EventScheduledTime(day, 4, 30), // 04:30 AM EST
							new EventScheduledTime(day, 6, 0), // 06:00 AM EST
							new EventScheduledTime(day, 7, 30), // 07:30 AM EST
							new EventScheduledTime(day, 9, 0), // 09:00 AM EST
							new EventScheduledTime(day, 10, 30), // 10:30 AM EST
							new EventScheduledTime(day, 12, 0), // 12:00 PM EST
							new EventScheduledTime(day, 13, 30), // 01:30 PM EST
							new EventScheduledTime(day, 15, 0), // 3:00 PM EST
							new EventScheduledTime(day, 16, 30), // 04:30 PM EST
							new EventScheduledTime(day, 18, 0), // 06:00 PM EST
							new EventScheduledTime(day, 19, 30), // 07:30 PM EST
							new EventScheduledTime(day, 21, 0), // 09:00 PM EST
							new EventScheduledTime(day, 22, 30), // 10:30 PM EST
					};

					if (Foxtrot.getInstance().getServerHandler().isAu()) {
						times = new EventScheduledTime[]{
								new EventScheduledTime(day, 0, 0), // 12:00 AM EST
								new EventScheduledTime(day, 2, 0), // 02:00 AM EST
								new EventScheduledTime(day, 4, 0), // 04:00 AM EST
								new EventScheduledTime(day, 6, 0), // 06:00 AM EST
								new EventScheduledTime(day, 8, 0), // 08:00 AM EST
								new EventScheduledTime(day, 10, 0), // 10:00 AM EST
								new EventScheduledTime(day, 12, 0), // 12:00 PM EST
								new EventScheduledTime(day, 14, 0), // 02:00 PM EST
								new EventScheduledTime(day, 16, 0), // 04:00 PM EST
								new EventScheduledTime(day, 18, 0), // 06:30 PM EST
								new EventScheduledTime(day, 20, 0), // 08:00 PM EST
								new EventScheduledTime(day, 22, 0), // 10:00 PM EST
						};
					}

					if (Foxtrot.getInstance().getServerHandler().isTeams()) {
						times = new EventScheduledTime[]{
								new EventScheduledTime(day, 12, 0), // 12:00 PM EST
								new EventScheduledTime(day, 15, 0), // 03:00 PM EST
								new EventScheduledTime(day, 18, 0), // 06:00 PM EST
								new EventScheduledTime(day, 21, 0), // 09:00 PM EST
						};
					}

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

	public void saveEvents() {
		try {
			File eventsBase = new File(Foxtrot.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {

				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					eventFile.delete();
				}
			}

			for (Event event : events) {
				File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()), event.getName() + ".json");
				FileUtils.write(eventFile, Foxtrot.GSON.toJson(event));
				Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Event getEvent(String name) {
		for (Event event : events) {
			if (event.getName().equalsIgnoreCase(name)) {
				return (event);
			}
		}

		return (null);
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

		if (Foxtrot.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
			String resolvedName = Foxtrot.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);
			Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(resolvedName);

			if (resolved == null) {
				Foxtrot.getInstance().getLogger().warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
				return;
			}

			resolved.activate();
		}
	}

}
