package cc.fyre.neutron.profile.disguise;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@AllArgsConstructor
public class DisguiseProfile {

    @Getter private String name;
    @Getter private UUID rankUuid;
    @Getter private String texture;
    @Getter private String signature;

    public DisguiseProfile(Document document) {
        this.name = document.getString("name");

        if (document.containsKey("texture")) {
            this.texture = document.getString("texture");
        }

        if (document.containsKey("signature")) {
            this.signature = document.getString("signature");
        }

        if (document.containsKey("rankUuid")) {
            this.rankUuid = UUID.fromString(document.getString("rankUuid"));
        }

    }

    public Document toDocument() {

        final Document document = new Document();

        document.put("name",this.name);

        if (this.texture != null) {
            document.put("texture",this.texture);
        }

        if (this.signature != null) {
            document.put("signature",this.signature);
        }

        if (this.rankUuid != null) {
            document.put("rankUuid",this.rankUuid.toString());
        }

        return document;
    }

    public Rank getRank() {
        return Neutron.getInstance().getRankHandler().fromUuid(this.rankUuid);
    }

}
