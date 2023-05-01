package cc.fyre.neutron.prevention;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.neutron.prevention.packets.PreventionCreatePacket;
import cc.fyre.neutron.prevention.packets.PreventionResolvePacket;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.packet.NoteApplyPacket;
import cc.fyre.neutron.profile.packet.PermissionAddPacket;
import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class PreventionHandler implements PacketListener{
    @Getter private List<Prevention> preventionList = new ArrayList<>();
    private Neutron instance;
    @Getter private MongoCollection<Document> collection;
    public PreventionHandler(Neutron instance) {
        this.instance = instance;
        this.collection = instance.getMongoHandler().getMongoDatabase().getCollection("prevention");
        Proton.getInstance().getPidginHandler().registerPacket(PreventionCreatePacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(PreventionResolvePacket.class);

        load();
    }

    private void load() {
        for(Document document : collection.find()) {
            preventionList.add(new Prevention(document));
        }
    }
    public void save() {
        for(Prevention prevention : preventionList) {
            BasicDBObject query = new BasicDBObject();
            query.put("time", prevention.getTime());
            FindOneAndReplaceOptions opt = new FindOneAndReplaceOptions();
            opt.upsert(true);
            collection.findOneAndReplace (query, prevention.toDocument(), opt);
        }
    }
    public boolean shouldAlert() {
        for (Prevention p : preventionList) {
            if (!p.isResolved())
                return true;
        }
        return false;
    }
    @IncomingPacketHandler
    public void onPreventionCreate(PreventionCreatePacket packet) {
        packet.execute();
    }

    @IncomingPacketHandler
    public void onPreventoryResolve(PreventionResolvePacket packet) {
        packet.execute();
    }
}
