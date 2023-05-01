package net.frozenorb.foxtrot.team.track;

import net.minecraft.util.com.google.common.base.CaseFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TeamActionType {

    // Chat Messages
    ALLY_CHAT_MESSAGE(false, TrackCategory.CHAT, "Ally Chat Message"),
    TEAM_CHAT_MESSAGE(false, TrackCategory.CHAT, "Team Chat Message"),
    OFFICER_CHAT_MESSAGE(false, TrackCategory.CHAT, "Officer Chat Message"),

    // Financial + Land
    PLAYER_WITHDRAW_MONEY(true, TrackCategory.FINANCIAL_LAND, "Withdrawn Money"),
    PLAYER_DEPOSIT_MONEY(true, TrackCategory.FINANCIAL_LAND, "Deposited Money"),
    PLAYER_CLAIM_LAND(true, TrackCategory.FINANCIAL_LAND, "Claimed Land"),
    PLAYER_UNCLAIM_LAND(true, TrackCategory.FINANCIAL_LAND, "Unclaimed Land"),
    PLAYER_RESIZE_LAND(true, TrackCategory.FINANCIAL_LAND, "Resized Land"),

    // Create + Delete
    PLAYER_CREATE_TEAM(true, TrackCategory.GENERAL, "Faction Created"),
    PLAYER_DISBAND_TEAM(true, TrackCategory.GENERAL, "Faction Disbanded"),

    // Mutes
    TEAM_MUTE_CREATED(false, TrackCategory.STAFF, "Faction Muted"),
    TEAM_MUTE_EXPIRED(false, TrackCategory.STAFF, "Faction Mute Expired"),

    // Connections
    MEMBER_CONNECTED(true, TrackCategory.CONNECTIONS, "Member Connected"),
    MEMBER_DISCONNECTED(true, TrackCategory.CONNECTIONS, "Member Disconnected"),

    // Basic
    ANNOUNCEMENT_CHANGED(true, TrackCategory.GENERAL, "Announcement Update"),
    HEADQUARTERS_CHANGED(true, TrackCategory.GENERAL, "Home Update"),
    POWER_FAC_STATUS_CHANGED(true, TrackCategory.STAFF, "Power Faction Status Applied"),

    // Invites
    PLAYER_INVITE_SENT(false, TrackCategory.MEMBERS, "Invite Sent"),
    PLAYER_INVITE_REVOKED(false, TrackCategory.MEMBERS, "Invite Revoked"),

    // Player Ranks
    PLAYER_JOINED(true, TrackCategory.MEMBERS, "Member Joined"),
    MEMBER_KICKED(true, TrackCategory.MEMBERS, "Member Kicked"),
    MEMBER_REMOVED(true, TrackCategory.MEMBERS, "Member Removed"),
    LEADER_CHANGED(true, TrackCategory.MEMBERS, "Leadership Changed"),
    PROMOTED_TO_CAPTAIN(true, TrackCategory.MEMBERS, "Promotion to Captain"),
    PROMOTED_TO_CO_LEADER(true, TrackCategory.MEMBERS, "Promotion to Co-Leader"),
    DEMOTED_FROM_CAPTAIN(true, TrackCategory.MEMBERS, "Demoted from Captain"),
    DEMOTED_FROM_CO_LEADER(true, TrackCategory.MEMBERS, "Demoted from Co-Leader"),

    // PvP Deaths
    MEMBER_KILLED_ENEMY_IN_PVP(true, TrackCategory.KILLS, "Member Killed Enemy"),
    MEMBER_KILLED_BY_ENEMY_IN_PVP(true, TrackCategory.DEATHS, "Member Killed by Enemy"),

    // DTR
    MEMBER_DEATH(true, TrackCategory.DEATHS, "Member Death"),
    TEAM_NOW_RAIDABLE(true, TrackCategory.GENERAL, "Faction Now Raidable"),
    MADE_FACTION_RAIDABLE(true, TrackCategory.KILLS, "Made Faction Raidable"),
    TEAM_NO_LONGER_RAIDABLE(true, TrackCategory.GENERAL, "Faction No Longer Raidable");

    @Getter private boolean loggedToDatabase;
    @Getter private TrackCategory trackCategory;
    @Getter private String fancyName;

    public String getInternalName() {
        // thanks guava!
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

}