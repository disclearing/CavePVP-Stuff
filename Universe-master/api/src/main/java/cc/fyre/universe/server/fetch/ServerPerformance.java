package cc.fyre.universe.server.fetch;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class ServerPerformance {

    @Getter @Setter private Double tPS;
    @Getter @Setter private Double fullTick;

    public ServerPerformance(JsonObject jsonObject) {
        this.tPS = jsonObject.get("tps").getAsDouble();
        this.fullTick = jsonObject.get("fullTick").getAsDouble();
    }

    public JsonObject toJsonObject() {

        final JsonObject toReturn = new JsonObject();

        toReturn.addProperty("tps",this.tPS);
        toReturn.addProperty("fullTick",this.fullTick);

        return toReturn;
    }
}
