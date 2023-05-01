package net.frozenorb.foxtrot.team.claims;

import net.minecraft.util.com.google.common.base.Joiner;
import com.mongodb.BasicDBObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.util.UUIDUtils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper=false)
@Data
public class Subclaim {

    @NonNull private Location loc1, loc2;
    @NonNull private String name;
    private List<UUID> members = new ArrayList<>();

    public static Subclaim fromJson(BasicDBObject obj) {
        Subclaim c = new Subclaim(LocationSerializer.deserialize((BasicDBObject) obj.get("Location1")), LocationSerializer.deserialize((BasicDBObject) obj.get("Location2")), obj.getString("Name"));
        c.setMembers(new ArrayList<>());
        return c;
    }

    public void addMember(UUID member) {
        members.add(member);
    }

    public boolean isMember(UUID check) {
        return (members.contains(check));
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public BasicDBObject json() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("Name", name);
        dbObject.put("Members", UUIDUtils.uuidsToStrings(members));
        dbObject.put("Location1", LocationSerializer.serialize(loc1));
        dbObject.put("Location2", LocationSerializer.serialize(loc2));

        return (dbObject);
    }

    @Override
    public String toString() {
        return (loc1.getBlockX() + ":" + loc1.getBlockY() + ":" + loc1.getBlockZ() + ":" + loc2.getBlockX() + ":" + loc2.getBlockY() + ":" + loc2.getBlockZ() + ":" + name + ":" + Joiner.on(", ").join(members));
    }

}