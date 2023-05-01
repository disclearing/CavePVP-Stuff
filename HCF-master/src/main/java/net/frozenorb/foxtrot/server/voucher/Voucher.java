package net.frozenorb.foxtrot.server.voucher;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.UUID;

public class Voucher {

    @Getter private UUID target;
    @Getter private String voucher;
    @Getter private String addedBy;
    @Getter private long addedTime;

    @Getter @Setter private String usedBy;
    @Getter @Setter private String code;
    @Getter @Setter private String bundleID;
    @Getter @Setter private int amount;
    @Getter @Setter private boolean rank;
    @Getter @Setter private boolean bundle;
    @Getter @Setter private boolean generating;
    @Getter @Setter private String rankName;
    @Getter @Setter private String rankDuration;
    @Getter @Setter private long usedTime;
    @Getter @Setter private boolean used;

    public Voucher(UUID target, String voucher, String addedBy) {
        this.target = target;
        this.voucher = voucher;
        this.addedBy = addedBy;
        this.addedTime = System.currentTimeMillis();
        this.usedBy = "";
        this.code = "";
        this.bundleID = "";
        this.usedTime = 0;
        this.bundle = false;
        this.used = false;
        this.rank = false;
        this.rankName = "";
        this.rankDuration = "";
        this.amount = 0;

        Foxtrot.getInstance().getVoucherHandler().getCache().add(this);
    }

    public Voucher(UUID target, String voucher, String addedBy, long addedTime, String usedBy, String code, long usedTime, boolean used, boolean rank, boolean bundle, String rankname, String rankDuration, String bundleID, int amount) {
        this.target = target;
        this.voucher = voucher;
        this.addedBy = addedBy;
        this.addedTime = addedTime;
        this.usedBy = usedBy;
        this.code = code;
        this.usedTime = usedTime;
        this.used = used;
        this.bundle = bundle;
        this.bundleID = bundleID;
        this.rank = rank;
        this.rankName = rankname;
        this.rankDuration = rankDuration;
        this.amount = amount;

        Foxtrot.getInstance().getVoucherHandler().getCache().add(this);
    }

}
