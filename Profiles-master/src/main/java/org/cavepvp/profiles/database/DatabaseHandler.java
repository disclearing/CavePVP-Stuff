package org.cavepvp.profiles.database;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.cavepvp.profiles.Profiles;

import java.util.Objects;

public class DatabaseHandler {

    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;

    public DatabaseHandler(Profiles instance) {
        this.mongoClient = new MongoClient(new ServerAddress(Profiles.getInstance().getConfig().getString("mongo.ip", "167.114.185.81"),27017));

        this.mongoDatabase = this.mongoClient.getDatabase(Objects.requireNonNull(Profiles.getInstance().getConfig().getString("databaseName", "Profiles")));
    }
}
