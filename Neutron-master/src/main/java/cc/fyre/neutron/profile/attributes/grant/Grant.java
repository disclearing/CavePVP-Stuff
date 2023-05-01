package cc.fyre.neutron.profile.attributes.grant;

import cc.fyre.neutron.profile.attributes.api.Executable;
import cc.fyre.neutron.profile.attributes.api.Pardonable;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.FormatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class Grant implements Executable, Pardonable {

    @Getter private UUID uuid;
    @Getter private UUID rankUuid;

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;

    @Getter @Setter private UUID pardoner;
    @Getter @Setter private Long pardonedAt;
    @Getter @Setter private String pardonedReason;

    @Getter @Setter private Long duration;

    public Grant(Rank rank,UUID executor,Long duration,String reason) {
        this.uuid = UUID.randomUUID();
        this.rankUuid = rank.getUuid();
        this.executor = executor;
        this.duration = duration;
        this.executedAt = System.currentTimeMillis();
        this.executedReason = reason;
    }

    public Grant(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.rankUuid = UUID.fromString(document.getString("rankUuid"));
        this.executor = UUID.fromString(document.getString("executor"));
        this.executedAt = document.getLong("executedAt");
        this.executedReason = document.getString("executedReason");

        if (document.containsKey("pardoner") && document.containsKey("pardonedAt") && document.containsKey("pardonedReason")) {
            this.pardoner = UUID.fromString(document.getString("pardoner"));
            this.pardonedAt = document.getLong("pardonedAt");
            this.pardonedReason = document.getString("pardonedReason");
        }

        this.duration = document.getLong("duration");
    }

    public Document toDocument() {

        final Document toReturn = new Document();

        toReturn.put("uuid",this.uuid.toString());
        toReturn.put("rankUuid",this.rankUuid.toString());
        toReturn.put("executor",this.executor.toString());
        toReturn.put("executedAt",this.executedAt);
        toReturn.put("executedReason",this.executedReason);

        if (this.isPardoned()) {
            toReturn.put("pardoner",this.pardoner.toString());
            toReturn.put("pardonedAt",this.pardonedAt);
            toReturn.put("pardonedReason",this.pardonedReason);
        }

        toReturn.put("duration",this.duration);

        return toReturn;
    }

    @Override public boolean isPardoned() {
        return this.pardoner != null && this.pardonedAt != null && this.pardonedReason != null;
    }

    public Rank getRank() {
        return Neutron.getInstance().getRankHandler().fromUuid(this.rankUuid);
    }

    public boolean isPermanent() {
        return this.duration == Integer.MAX_VALUE;
    }

    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.executedAt + this.duration);
    }

    public Long getExpiredAt() {
        return this.executedAt + this.duration;
    }

    public Long getRemaining() {
        if(this.duration == Integer.MAX_VALUE) {
            return 2147483647L;
        }

        return (this.executedAt + this.duration) - System.currentTimeMillis();
    }

    public boolean isActive() {

        if (isPermanent() && !this.isPardoned()) {
            return true;
        }

        if (this.getRemaining() < 0) {
            return false;
        }

        if (this.isPardoned()) {
            return false;
        }

        return true;
    }

    public String getRemainingString() {

        if (this.duration == Integer.MAX_VALUE) {
            return "Never";
        }

        return ChatColor.YELLOW + FormatUtil.millisToRoundedTime(this.getRemaining(),true);
    }

}
