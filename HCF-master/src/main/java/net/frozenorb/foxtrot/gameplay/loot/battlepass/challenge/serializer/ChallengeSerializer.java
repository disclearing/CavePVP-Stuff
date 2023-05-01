package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer;

import com.google.gson.*;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;

import java.lang.reflect.Type;

public class ChallengeSerializer implements JsonSerializer<Challenge>, JsonDeserializer<Challenge> {

    @Override
    public JsonElement serialize(Challenge challenge, Type type, JsonSerializationContext jsonSerializationContext) {
        Type abstractType = challenge.getAbstractType();
        JsonObject json = new JsonObject();
        json.addProperty("type", abstractType.getTypeName());
        json.add("properties", jsonSerializationContext.serialize(challenge, challenge.getAbstractType()));
        return json;
    }

    @Override
    public Challenge deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String typeName = jsonElement.getAsJsonObject().get("type").getAsString();
        JsonElement properties = jsonElement.getAsJsonObject().get("properties");

        try {
            return jsonDeserializationContext.deserialize(properties, Class.forName(typeName));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown type: $type", e);
        }
    }

}