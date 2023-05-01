package cc.fyre.piston.chat;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.Piston;
import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ChatHandler implements Listener {

    public static final int MAX_WARNINGS = 10;
    public static final long AUTO_MUTE_DURATION = 60 * 1000L;

    @Getter
    private Piston instance;

    @Getter
    @Setter
    private int slowTime = 10;
    @Getter
    @Setter
    private boolean muted = false;
    @Getter
    @Setter
    private boolean caveRankOnly = false;

    @Getter
    @Setter
    private String punishmentCommand = "ban {player} -s 1d Spam (Piston ChatHandler)";
    @Getter
    @Setter
    private String inappropriateCommand = "mute {player} -p {duration} {reason}";

    @Getter
    @Setter
    private Map<UUID, Long> slowCache = new HashMap<>();
    @Getter
    @Setter
    private Map<UUID, Long> mutedCache = new HashMap<>();
    @Getter
    @Setter
    private Map<UUID, AtomicInteger> warningsCache = new HashMap<>();
    @Getter
    @Setter
    private Map<UUID, List<ChatMessage>> messageCache = new HashMap<>();

    // Just a regular filter
    @Getter
    private List<String> lowFilter;
    @Getter
    private List<String> linkWhitelist;

    private Pattern linkFilter;
    private Pattern ipFilter;

    @Getter
    private Map<Pattern, String> inappropriate = new HashMap<>();

    public ChatHandler(Piston instance) {
        this.instance = instance;

        this.inappropriate.put(Pattern.compile("n+[i1l|]+gg+[e3]+r+", Pattern.CASE_INSENSITIVE), "7d");
        this.inappropriate.put(Pattern.compile("k+i+l+l+ *y*o*u+r+ *s+e+l+f+", Pattern.CASE_INSENSITIVE), "7d");
        this.inappropriate.put(Pattern.compile("f+a+g+[o0]+t+", Pattern.CASE_INSENSITIVE), "7d");
        this.inappropriate.put(Pattern.compile("\\bk+y+s+\\b", Pattern.CASE_INSENSITIVE), "7d");
        this.inappropriate.put(Pattern.compile("b+e+a+n+e+r+", Pattern.CASE_INSENSITIVE), "7d");

        this.linkFilter = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
        this.ipFilter = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

        this.lowFilter = Arrays.asList(
                "sage", "sagepvp", "hcrival", "rival", "arcane", "fierce", "velt", "viper", "faithful", "faggot", "vipermc", "hcrival", "fiercepvp", "veltpvp", "arcane"
        );

        this.linkWhitelist = Arrays.asList(
                "cavepvp.org", "forums.cavepvp.org", "twitch.tv",
                "youtube.com", "youtu.be", "discord.gg", "twitter.com",
                "prnt.sc", "gyazo.com", "imgur.com", "mythic.gg");

        instance.getServer().getOnlinePlayers().forEach(loopPlayer -> {
            this.messageCache.put(loopPlayer.getUniqueId(), new ArrayList<>());
            this.warningsCache.put(loopPlayer.getUniqueId(), new AtomicInteger(MAX_WARNINGS));
        });

        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {

        if (this.mutedCache.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        this.messageCache.put(event.getPlayer().getUniqueId(), new ArrayList<>());
        this.warningsCache.put(event.getPlayer().getUniqueId(), new AtomicInteger(MAX_WARNINGS));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncChat(AsyncPlayerChatEvent event) {

        final Player player = event.getPlayer();

        if (player.hasPermission(NeutronConstants.STAFF_PERMISSION) || player.hasPermission("piston.chat.bypass")) {
            return;
        }

        if (event.getPlayer().hasMetadata("STAFF_CHAT")) {
            return;
        }

        if (this.muted) {
            player.sendMessage(ChatColor.RED + "Public chat is currently muted.");
            event.setCancelled(true);
            return;
        }

        if (this.caveRankOnly && !player.hasPermission("piston.caverank.chat")) {
            player.sendMessage(ChatColor.RED + "You must have Cave Rank to talk in chat at this time! Purchase Cave Rank at store.cavepvp.org");
            event.setCancelled(true);
            return;
        }

        if (this.slowTime > 0) {

            if (this.getSlowChatRemaining(player.getUniqueId()) > 0) {
                player.sendMessage(ChatColor.RED + "Public chat is currently slowed. You can only chat every " + this.slowTime + " seconds.");
                player.sendMessage(ChatColor.RED + "Please wait another " + ChatColor.BOLD + TimeUtils.formatIntoMMSS((int) this.getSlowChatRemaining(player.getUniqueId()) / 1000) + ChatColor.RED + ".");
                event.setCancelled(true);
                return;
            }

            this.slowCache.put(player.getUniqueId(), System.currentTimeMillis());
        }

        String msg = event.getMessage().toLowerCase();

        if (this.isFiltered(msg)) {
            Piston.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.hasPermission("neutron.staff")).forEach(it -> it.sendMessage(ChatColor.translate("&c[Filtered] &f" + NeutronConstants.formatChatDisplay(player, msg))));

            player.sendMessage(NeutronConstants.formatChatDisplay(player, msg));
            event.setCancelled(true);
            return;
        }

        for (Pattern pattern : this.inappropriate.keySet()) {

            if (!pattern.matcher(event.getMessage().toLowerCase()).find()) {
                continue;
            }

            final String value = this.inappropriate.get(pattern);

            if (value != null) {
                this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), this.inappropriateCommand.replace("{player}", event.getPlayer().getName()).replace("{duration}", value).replace("{reason}", event.getMessage()));
            }

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Message contains inappropriate content.");
            break;
        }

        this.messageCache.get(player.getUniqueId()).add(new ChatMessage(System.currentTimeMillis(), event.getMessage()));
    }

    public long getSlowChatRemaining(UUID uuid) {

        if (!this.slowCache.containsKey(uuid)) {
            return 0L;
        }

        return (this.slowCache.get(uuid) + (this.slowTime * 1000)) - System.currentTimeMillis();
    }

    public boolean isFiltered(String message) {
        String msg = message.toLowerCase()
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("@", "a")
                .replace("7", "t")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replaceAll("\\p{Punct}|\\d", "").trim();

        String[] words = msg.trim().split(" ");

        for (String word : words) {
            for (String filteredWord : lowFilter) {
                if (word.contains(filteredWord)) {
                    return true;
                }
            }
        }

        for (String word : message.replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
            boolean continueIt = false;

            for (String phrase : this.linkWhitelist) {
                if (word.toLowerCase().contains(phrase)) {
                    continueIt = true;
                    break;
                }
            }

            if (continueIt) {
                continue;
            }

            if (ipFilter.matcher(word).matches() || linkFilter.matcher(word).matches()) {
                return true;
            }
        }
        return false;
    }

    public String isFilteredFind(String message) {
        String msg = message.toLowerCase()
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("@", "a")
                .replace("7", "t")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replaceAll("\\p{Punct}|\\d", "").trim();

        String[] words = msg.trim().split(" ");

        for (String word : words) {
            for (String filteredWord : lowFilter) {
                if (word.contains(filteredWord)) {
                    return word;
                }
            }
        }

        for (String word : message.replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
            boolean continueIt = false;

            for (String phrase : this.linkWhitelist) {
                if (word.toLowerCase().contains(phrase)) {
                    continueIt = true;
                    break;
                }
            }

            if (continueIt) {
                continue;
            }

            if (ipFilter.matcher(word).matches() || linkFilter.matcher(word).matches()) {
                return word;
            }
        }
        return null;
    }
}
