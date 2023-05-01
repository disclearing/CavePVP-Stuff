package cc.fyre.neutron.stats;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.Proton;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticHandler {
    private final Neutron instance;

    @Getter private final Map<String, StatisticDay> cache = new HashMap<>();
    @Getter private final List<StatisticDay> toSave = new ArrayList<>();

    @Getter private final MongoCollection<Document> collection;

    public StatisticHandler(Neutron instance) {
        this.instance = instance;

        this.collection = this.instance.getMongoHandler().getMongoDatabase().getCollection("statistics");

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.collection.find().iterator().forEachRemaining(it -> {
            final StatisticDay statisticDay = Proton.GSON.fromJson(it.toJson(), StatisticDay.class);

            cache.put(statisticDay.getDayName(), statisticDay);
        }), 10L);
    }

    public void saveData() {

//        for (StatisticDay statisticDay : toSave) {
//            UpdateResult updateResult = this.collection.updateOne(new Document("_id", statisticDay.getUuid().toString()),new Document("$set",statisticDay),new UpdateOptions().upsert(true));
//
//        }


    }
}
