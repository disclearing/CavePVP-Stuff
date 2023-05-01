package net.frozenorb.foxtrot.serialization;

import com.google.gson.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Type;

public class PotionAdapter implements JsonDeserializer<Potion>, JsonSerializer<Potion> {
    public JsonElement serialize(final Potion src, Type typeOfSrc, JsonSerializationContext context) {
        return toJson(src);
    }

    public Potion deserialize(final JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(json);
    }

    public static JsonObject toJson(final Potion potion) {

        if (potion == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",potion.getType().ordinal());
        jsonObject.addProperty("level",potion.getLevel());
        jsonObject.addProperty("splash",potion.isSplash());
        jsonObject.addProperty("extended",potion.hasExtendedDuration());
        return jsonObject;
    }

    public static Potion fromJson(final JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        PotionType type = PotionType.values()[jsonObject.get("type").getAsInt()];
        Potion potion = new Potion(type);

        int level = jsonObject.get("level").getAsInt();
        boolean splash = jsonObject.get("splash").getAsBoolean();
        boolean extended = jsonObject.get("extended").getAsBoolean();

        if (level > 0) {
            potion.setLevel(level);
        }

        if (splash) {
            potion.splash();
        }

        if (!potion.getType().isInstant() && extended) {
            potion.extend();
        }

        return potion;
    }
}