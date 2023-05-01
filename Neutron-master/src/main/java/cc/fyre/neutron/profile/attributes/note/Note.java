package cc.fyre.neutron.profile.attributes.note;

import cc.fyre.neutron.profile.attributes.api.Executable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@AllArgsConstructor
public class Note implements Executable {

    @Getter private UUID uuid;

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;

    public Note(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.executor = UUID.fromString(document.getString("executor"));
        this.executedAt = document.getLong("executedAt");
        this.executedReason = document.getString("executedReason");
    }

    public Document toDocument() {

        final Document toReturn = new Document();

        toReturn.put("uuid",this.uuid.toString());
        toReturn.put("executor",this.executor.toString());
        toReturn.put("executedAt",this.executedAt);
        toReturn.put("executedReason",this.executedReason);

        return toReturn;
    }

}
