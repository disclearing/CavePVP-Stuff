package cc.fyre.neutron.stats;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StatisticDay {
    private long dateGenerated;
    private long dateEnded = 0;
    private String dayName;
    private int anticheatBans = 0;
    private int totalLogins = 0;
    private int uniquePlayers = 0;

    // GENERAL STATISTICS
    private int commandsExecuted = 0;
    private int chatUsed = 0;
    private int peakPlayerCount = 0;

    // FASTS EXCLUSIVE
    private int vouchersGiven = 0;
    private int vouchersRedeemed = 0;
    private int rankVouchersGiven = 0;
    private int rankVouchersRedeemed = 0;
    private int moneyVouchersGiven = 0;
    private int moneyVouchersRedeemed = 0;
    private int abilityItemsUsed = 0;
    private int killBoostingAttempts = 0;
    private int airdropsOpened = 0;
    private int cratesOpened = 0;
    private int mysteryBoxesOpened = 0;
    private int mythicalChestsOpened = 0;
    private int partnerKeysOpened = 0;
    private int playersEnteringNether = 0;
    private int playersEnteringMiniKOTH = 0;
    private int playersEnteringEnd = 0;
    private int playersEnteringKOTH = 0;
    private int playersEnteringCitadel = 0;

    // FACTION STATS
    private int factionsCreated = 0;
    private int factionsDisbanded = 0;
    private int membersInvited = 0;
    private int membersJoined = 0;

    // PLAYER INTERACTION
    private int kills = 0;
    private int deaths = 0;
    private int chatsSent = 0;
    private int messagesSent = 0;
    private int repliesSent = 0;
    private int buyCommandExecuted;

    // PUNISHMENTS
    private int mutes = 0;
    private int bans = 0;
    private int warns = 0;
    private int kicks = 0;
    private int unbans = 0;
    private int unmutes = 0;
    private int blacklists = 0;
    private int unblacklists = 0;

    // STAFF
    private int nonConsoleBans = 0;
    private int nonConsoleUnbans = 0;
    private int nonConsoleMutes = 0;
    private int nonConsoleUnmutes = 0;
    private int staffGrants = 0;

    private long peakPlayerCountTime = 0;
}