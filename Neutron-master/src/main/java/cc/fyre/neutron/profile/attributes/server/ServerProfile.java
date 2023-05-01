package cc.fyre.neutron.profile.attributes.server;

import cc.fyre.neutron.Neutron;
import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cc.fyre.proton.util.TimeUtils;
import org.bson.Document;

@AllArgsConstructor
public class ServerProfile {

    @Getter @Setter private boolean online;

    @Getter @Setter private Long firstLogin;
    @Getter @Setter private Long lastLogin;
    @Getter @Setter private String currentServer;
    @Getter @Setter private String lastServer;

    public ServerProfile(Document document) {
        this.online = document.getBoolean("online");
        this.firstLogin = document.getLong("firstLogin");
        this.lastLogin = document.getLong("lastLogin");
        this.lastServer = document.getString("lastServer");
        if(document.containsKey("currentServer"))
            this.currentServer = document.getString("currentServer");
        else
            this.currentServer = UniverseAPI.getServerName();
    }

    public Document toDocument() {

        final Document toReturn = new Document();

        toReturn.put("online",this.online);
        toReturn.put("firstLogin",this.firstLogin);
        toReturn.put("lastLogin",this.lastLogin);
        toReturn.put("lastServer",this.lastServer);
        toReturn.put("currentServer",this.currentServer);

        return toReturn;
    }

    public long getLastSeen() {
        return System.currentTimeMillis() - this.lastLogin;
    }

    public String getLastSeenString() {
        return TimeUtils.formatIntoDetailedString((int)(this.getLastSeen()/1000));
    }
}
