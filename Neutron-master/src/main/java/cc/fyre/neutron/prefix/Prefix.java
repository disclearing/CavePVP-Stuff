package cc.fyre.neutron.prefix;

import cc.fyre.neutron.Neutron;

import lombok.Getter;
import lombok.Setter;

import cc.fyre.proton.pidgin.packet.Packet;

import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Prefix {

    @Getter private UUID uuid;
    @Getter @Setter private String name;
    @Getter @Setter private String display;
    @Getter @Setter private AtomicInteger weight;

    @Getter @Setter private Long createdAt;
    @Getter @Setter private UUID createdBy;

    public Prefix(UUID uuid,String name,UUID createdBy) {
        this.uuid = uuid;
        this.name = name;
        this.display = "";
        this.weight = new AtomicInteger(0);

        this.createdAt = System.currentTimeMillis();
        this.createdBy = createdBy;
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

    public PrefixHandler getHandler() {
        return Neutron.getInstance().getPrefixHandler();
    }
}
