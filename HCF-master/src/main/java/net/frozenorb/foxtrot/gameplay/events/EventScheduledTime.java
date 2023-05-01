package net.frozenorb.foxtrot.gameplay.events;

import java.util.Calendar;
import java.util.Date;

import net.minecraft.util.com.google.common.primitives.Ints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.com.google.common.base.Objects;

@AllArgsConstructor
public class EventScheduledTime implements Comparable<EventScheduledTime> {

    @Getter private int day;
    @Getter private int hour;
    @Getter private int minutes;

    public static EventScheduledTime parse(String input) {
        String[] inputSplit = input.split(" ");

        int days = Integer.parseInt(inputSplit[0]);

        String[] timeSplit = inputSplit[1].split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minutes = timeSplit.length > 1 ? Integer.parseInt(timeSplit[1]) : 0;

        return (new EventScheduledTime(days, hour, minutes));
    }

    public static EventScheduledTime parse(Date input) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(input);

        int days = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return (new EventScheduledTime(days, hour, minutes));
    }

    public Date toDate() {
        Calendar activationTime = Calendar.getInstance();

        activationTime.set(Calendar.DAY_OF_YEAR, day);
        activationTime.set(Calendar.HOUR_OF_DAY, hour);
        activationTime.set(Calendar.MINUTE, minutes);
        activationTime.set(Calendar.SECOND, 0);
        activationTime.set(Calendar.MILLISECOND, 0);

        return (activationTime.getTime());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof EventScheduledTime) {
            EventScheduledTime other = (EventScheduledTime) object;

            return (other.day == this.day && other.hour == this.hour && other.minutes == this.minutes);
        }

        return (false);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(day, hour, minutes);
    }

    @Override
    public int compareTo(EventScheduledTime other) {
        int result = Ints.compare(day, other.day);

        if (result == 0) {
            result = Ints.compare(hour, other.hour);
        }

        if (result == 0) {
            result = Ints.compare(minutes, other.minutes);
        }

        return (result);
    }

    @Override
    public String toString() {
        return (day + " " + hour + ":" + minutes);
    }

}