package cc.fyre.proton.scoreboard;

import cc.fyre.proton.Proton;
import cc.fyre.proton.packet.ScoreboardTeamPacketMod;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.Field;
import java.util.*;

final class Scoreboard {

    private Player player;
    private Objective objective;
    private Map<String, Integer> displayedScores = new HashMap<>();
    private Map<String, String> scorePrefixes = new HashMap<>();
    private Map<String, String> scoreSuffixes = new HashMap<>();
    private Set<String> sentTeamCreates = new HashSet<>();
    private final Set<String> recentlyUpdatedScores = new HashSet<>();
    private final Set<String> usedBaseScores = new HashSet<>();
    private final ThreadLocal<LinkedList<String>> localList = ThreadLocal.withInitial(LinkedList::new);


    public Scoreboard(Player player) {
        this.player = player;

        final org.bukkit.scoreboard.Scoreboard board = Proton.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        this.objective = board.registerNewObjective("Proton", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(board);
    }

    public void update() {

        final String untranslatedTitle = Proton.getInstance().getScoreboardHandler().getConfiguration().getTitleGetter().getTitle(this.player);
        final String title = ChatColor.translateAlternateColorCodes('&', untranslatedTitle);
        final List<String> lines = this.localList.get();

        if (!lines.isEmpty()) {
            lines.clear();
        }

        Proton.getInstance().getScoreboardHandler().getConfiguration().getScoreGetter().getScores(this.localList.get(), this.player);

        this.recentlyUpdatedScores.clear();
        this.usedBaseScores.clear();

        int nextValue = lines.size();

        Preconditions.checkArgument(lines.size() < 16, "Too many lines passed!");
        Preconditions.checkArgument(title.length() < 32, "Title is too long!");

        if (!this.objective.getDisplayName().equals(title)) {
            this.objective.setDisplayName(title);
        }

        String displayedScore;

        for(Iterator var5 = lines.iterator(); var5.hasNext(); --nextValue) {

            displayedScore = (String)var5.next();

            if (48 <= displayedScore.length()) {
                throw new IllegalArgumentException("Line is too long! Offending line: " + displayedScore);
            }

            final String[] separated = this.separate(displayedScore, this.usedBaseScores);
            final String prefix = separated[0];
            final String score = separated[1];
            final String suffix = separated[2];

            this.recentlyUpdatedScores.add(score);

            if (!this.sentTeamCreates.contains(score)) {
                this.createAndAddMember(score);
            }

            if (!this.displayedScores.containsKey(score) || this.displayedScores.get(score) != nextValue) {
                this.setScore(score, nextValue);
            }

            if (!this.scorePrefixes.containsKey(score) || !(this.scorePrefixes.get(score)).equals(prefix) || !((String)this.scoreSuffixes.get(score)).equals(suffix)) {
                this.updateScore(score, prefix, suffix);
            }
        }

        UnmodifiableIterator unmodifiableIterator = ImmutableSet.copyOf(this.displayedScores.keySet()).iterator();

        while(unmodifiableIterator.hasNext()) {

            displayedScore = (String)unmodifiableIterator.next();

            if (!this.recentlyUpdatedScores.contains(displayedScore)) {
                this.removeScore(displayedScore);
            }
        }
    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This is here so that the score joins itself, this way
    // #updateScore will work as it should (that works on a 'player'), which technically we are adding to ourselves
    private void createAndAddMember(String scoreTitle) {

        final ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", new ArrayList<String>(), 0);
        final ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, Arrays.asList(scoreTitle), 3);

        scoreboardTeamAdd.sendToPlayer(player);
        scoreboardTeamAddMember.sendToPlayer(player);

        sentTeamCreates.add(scoreTitle);
    }

    private void setScore(String score, int value) {

        final PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

        setField(scoreboardScorePacket, "a", score);
        setField(scoreboardScorePacket, "b", objective.getName());
        setField(scoreboardScorePacket, "c", value);
        setField(scoreboardScorePacket, "d", 0);

        this.displayedScores.put(score, value);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
    }

    private void removeScore(String score) {
        displayedScores.remove(score);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2);
        scoreboardTeamModify.sendToPlayer(player);
    }

    // Here be dragons.
    // Good luck maintaining this code.
    private String[] separate(String line, Collection<String> usedBaseScores) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        String prefix = "";
        String score = "";
        String suffix = "";

        List<String> working = new ArrayList<>();
        StringBuilder workingStr = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '*' || (workingStr.length() == 16 && working.size() < 3)) {
                working.add(workingStr.toString());
                workingStr = new StringBuilder();

                if (c == '*') {
                    continue;
                }
            }

            workingStr.append(c);
        }

        working.add(workingStr.toString());

        switch (working.size()) {
            case 1:
                score = working.get(0);
                break;
            case 2:
                score = working.get(0);
                suffix = working.get(1);
                break;
            case 3:
                prefix = working.get(0);
                score = working.get(1);
                suffix = working.get(2);
                break;
            default:
                Proton.getInstance().getLogger().warning("Failed to separate scoreboard line. Input: " + line);
                break;
        }

        if (usedBaseScores.contains(score)) {
            if (score.length() <= 14) {
                for (ChatColor chatColor : ChatColor.values()) {
                    String possibleScore = chatColor + score;

                    if (!usedBaseScores.contains(possibleScore)) {
                        score = possibleScore;
                        break;
                    }
                }

                if (usedBaseScores.contains(score)) {
                    Proton.getInstance().getLogger().warning("Failed to find alternate color code for: " + score);
                }
            } else {
                Proton.getInstance().getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }

        if (prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if (score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if (suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        usedBaseScores.add(score);
        return (new String[]{prefix, score, suffix});
    }

}