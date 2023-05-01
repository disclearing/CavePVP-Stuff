package net.frozenorb.foxtrot.serialization;

import cc.fyre.proton.util.ItemBuilder;
import com.google.gson.*;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector> {
    public Vector deserialize(final JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    public JsonElement serialize(final Vector src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(final Vector src) {
        if (src == null) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        return object;
    }

    public static Vector fromJson(final JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        return new Vector(x, y, z);
    }
}
