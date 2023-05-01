package cc.fyre.neutron.profile.attributes.punishment.impl;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.IPunishmentType;
import cc.fyre.neutron.profile.attributes.punishment.Punishments;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Punishment implements IPunishment {

    @Override
    public IPunishment.Type getIType() {
        return IPunishment.Type.NORMAL;
    }

    @Getter private UUID uuid;
    @Getter private Type type;

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;
    @Getter private Boolean executedSilent;
    @Getter private String server;

    public Punishment(Type type,UUID executor,String reason,Boolean executedSilent, String server) {
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.executor = executor;
        this.executedAt = System.currentTimeMillis();
        this.executedReason = reason;
        this.executedSilent = executedSilent;
        this.server = server;
    }

    public Punishment(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.executor = UUID.fromString(document.getString("executor"));
        this.executedAt = document.getLong("executedAt");
        this.executedReason = document.getString("executedReason");
        this.executedSilent = document.getBoolean("executedSilent");

        this.type = Type.valueOf(document.getString("type"));
        if(document.containsKey("server")) {
            this.server = document.getString("server");
        } else {
            this.server = "Unknown";
        }
    }

    @Override
    public Document toDocument() {


        final Document toReturn = new Document();

        toReturn.put("uuid",this.uuid.toString());
        toReturn.put("executor",this.executor.toString());
        toReturn.put("executedAt",this.executedAt.longValue());
        toReturn.put("executedReason",this.executedReason);
        toReturn.put("executedSilent",this.executedSilent.booleanValue());

        toReturn.put("type",this.type.name().toUpperCase());
        toReturn.put("iType",this.getIType().name().toUpperCase());
        toReturn.put("server",this.getServer());

        return toReturn;
    }

    @Override
    public String getServer() {
        return this.server;
    }

    @Override
    public Punishments getPunishType() {
        if (this.type == Type.WARN) {
            return Punishments.WARN;
        } else if(this.type == Type.KICK) {
            return Punishments.KICK;
        }
        return Punishments.KICK;

    }

    public void execute(Player player) {

        if (player == null) {
            return;
        }

        if (this.type == Type.WARN) {
            player.sendMessage(ChatColor.RED + "You have been warned: " + ChatColor.YELLOW + this.executedReason);
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100, 10);
        } else if (this.type == Type.KICK) {

            final String kickMessage = ChatColor.RED + "You have been kicked by a staff member. \nReason: " + ChatColor.YELLOW + this.executedReason;

            Neutron.getInstance().getServer().getScheduler().runTaskLater(Neutron.getInstance(),() -> player.kickPlayer(kickMessage),5L);

        }
    }

    @AllArgsConstructor
    public enum Type implements IPunishmentType {

        WARN("Warn","warned",null),
        KICK("Kick","kicked",null);

        @Getter private String readable;
        @Getter private String executedContext;
        @Getter private String pardonedContext;

    }

}
