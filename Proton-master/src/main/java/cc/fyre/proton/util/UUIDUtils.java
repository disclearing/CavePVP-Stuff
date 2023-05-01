package cc.fyre.proton.util;

import cc.fyre.proton.Proton;
import lombok.experimental.UtilityClass;
import com.mongodb.BasicDBList;

import java.util.Collection;
import java.util.UUID;

@UtilityClass
public final class UUIDUtils {

    /**
     * Gets the name associated with a UUID.
     *
     * @param uuid The UUID object to fetch the name for.
     * @return The name associated with the UUID given.
     */
    public static String name(UUID uuid) {

        final String toReturn = Proton.getInstance().getUuidCache().name(uuid);

        return toReturn == null ? "null":toReturn;
    }

    /**
     * Gets the UUID associated with a name.
     *
     * @param name The name to fetch the UUID for.
     * @return The UUID associated with the name given.
     */
    public static UUID uuid(String name) {
        return Proton.getInstance().getUuidCache().uuid(name);
    }

    /**
     * Returns whether or not the uuid is cached inside the UUIDCache.
     *
     * @param uuid The uuid to fetch whether the uuid is cached.
     * @return Boolean whether the uuid is cached.
     */
    public static boolean cached(UUID uuid) {
        return Proton.getInstance().getUuidCache().cached(uuid);
    }

    /**
     * Returns whether or not the name is cached inside the UUIDCache.
     *
     * @param name The name to fetch whether the name is cached.
     * @return Boolean whether the name is cached.
     */
    public static boolean cached(String name) {
        return Proton.getInstance().getUuidCache().cached(name);
    }

    /**
     * Formats a UUID and its name.
     *
     * @param uuid The UUID to format.
     * @return The formatted String.
     */
    public static String formatPretty(UUID uuid) {
        return (name(uuid) + " [" + uuid + "]");
    }

    /**
     * Converts a Collection of UUIDs into a String-based BasicDBList (for storage in MongoDB)
     *
     * @param toConvert The UUIDs to convert.
     * @return A BasicDBList containing the UUIDs in String form.
     */
    public static BasicDBList uuidsToStrings(Collection<UUID> toConvert) {
        if (toConvert == null || toConvert.isEmpty()) {
            return (new BasicDBList());
        }

        BasicDBList dbList = new BasicDBList();

        for (UUID uuid : toConvert) {
            dbList.add(uuid.toString());
        }

        return (dbList);
    }

}