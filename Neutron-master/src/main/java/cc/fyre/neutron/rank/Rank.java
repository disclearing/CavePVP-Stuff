package cc.fyre.neutron.rank;

import cc.fyre.neutron.Neutron;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import cc.fyre.proton.pidgin.packet.Packet;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Rank {

    @Getter private UUID uuid;
    @Getter @Setter private String name;

    @Getter @Setter private AtomicInteger weight;
    @Getter @Setter private AtomicInteger gemMultiplier;
    @Getter @Setter private long evictionRate;

    @Getter @Setter private String prefix;

    @Getter @Setter private ChatColor color;
    @Getter @Setter private ChatColor secondColor;

    @Getter @Setter private Long createdAt;
    @Getter @Setter private UUID createdBy;
    @Getter @Setter private JsonObject metaData;

    @Getter @Setter private List<Rank> inherits;
    @Getter @Setter private List<String> permissions;


    public Rank(UUID uuid,String name,UUID createdBy) {
        this.uuid = uuid;
        this.name = name;

        this.weight = new AtomicInteger(0);
        this.gemMultiplier = new AtomicInteger(1);

        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.secondColor = null;
        this.evictionRate = TimeUnit.MINUTES.toMillis(3L);

        this.createdAt = System.currentTimeMillis();
        this.createdBy = createdBy;

        this.inherits = new ArrayList<>();
        this.permissions = new ArrayList<>();

        this.metaData = new JsonObject();
    }


    public void load() {
        this.getHandler().load(this);
    }

    public void save() {
        this.getHandler().save(this);
    }

    public void save(Packet packet) {
        this.getHandler().save(this,packet);
    }

    public void save(List<Packet> packets) {
        this.getHandler().save(this,packets);
    }

    public void delete() {
        this.getHandler().delete(this);
    }

    public void delete(Packet packet) {
        this.getHandler().delete(this,packet);
    }

    public void delete(List<Packet> packets) {
        this.getHandler().delete(this,packets);
    }

    public Document findDocument() {
        return this.getHandler().findDocument(this);
    }

    public RankHandler getHandler() {
        return Neutron.getInstance().getRankHandler();
    }

    public String getFancyName() {
        return this.color + (this.secondColor == null ? "":this.secondColor.toString()) + this.name;
    }

    public List<String> getEffectivePermissions() {

        final List<String> toReturn = new ArrayList<>(this.permissions);

        this.inherits.stream().map(Rank::getEffectivePermissions).forEach(toReturn::addAll);

        return toReturn;
    }

    public List<Rank> getEffectiveInherits() {

        final List<Rank> toReturn = new ArrayList<>(this.inherits);

        this.inherits.stream().map(Rank::getEffectiveInherits).forEach(toReturn::addAll);

        return toReturn;
    }

    public JsonElement getMetaData(String value) {
        return this.metaData.get(value);
    }

    public boolean hasMetaData(String value) {
        return this.metaData.has(value);
    }

}
