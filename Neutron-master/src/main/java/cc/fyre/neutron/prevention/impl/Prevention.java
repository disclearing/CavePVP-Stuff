package cc.fyre.neutron.prevention.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;
@Getter @Setter
public class Prevention {
    private UUID uuid;
    private String command;
    private long time;
    private boolean resolved;
    public Prevention(UUID uuid, String command, long time, boolean resolved) {
        this.uuid = uuid;
        this.command = command;
        this.time = time;
        this.resolved = resolved;
    }
    public Prevention(Document document) {
        this.uuid = UUID.fromString((String) document.get("uuid"));
        this.command = (String) document.get("command");
        this.time = (long) document.get("time");
        this.resolved =(boolean) document.get("resolved");
    }


    public Document toDocument() {
        Document document = new Document();
        document.append("uuid", uuid.toString());
        document.append("command", command);
        document.append("time", time);
        document.append("resolved", resolved);
        return document;
    }


}
