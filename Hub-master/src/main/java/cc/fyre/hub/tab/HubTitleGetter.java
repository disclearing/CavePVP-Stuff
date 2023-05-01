package cc.fyre.hub.tab;

import cc.fyre.hub.Hub;
import cc.fyre.proton.scoreboard.construct.TitleGetter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HubTitleGetter extends TitleGetter {

    private String title = "CAVEPVP";
    private String text = "CAVEPVP";
    private boolean domain = false;
    private int length = 1;
    private int flicker = -1;

    public HubTitleGetter() {
        Bukkit.getServer().getScheduler().runTaskTimer(Hub.getInstance(), () -> {
            final StringBuilder title = new StringBuilder();
            boolean finish = this.length > this.text.length();

            if (this.flicker >= 0) {
                title.append((this.flicker % 2 == 0 ? ChatColor.WHITE : ChatColor.DARK_RED) + ChatColor.BOLD.toString() + this.text);

                if (this.flicker == 8) {
                    this.flicker = -1;

                    if (this.domain) {
                        this.text = UniverseAPI.getServerName();
                    } else {
                        this.text = "CAVEPVP";
                    }

                    this.domain = !this.domain;
                } else {
                    this.flicker++;
                }
            } else {

                if (finish) {
                    title.append(ChatColor.RED + ChatColor.BOLD.toString() + this.text);
                    this.length = 1;
                    this.flicker = 0;
                } else {
                    title.append(ChatColor.RED + ChatColor.BOLD.toString() + this.text.substring(0, length - 1));
                    title.append(ChatColor.DARK_RED + ChatColor.BOLD.toString() + this.text.substring(length - 1));
                    this.length++;
                }

            }

            this.title = title.toString();
        }, 6, 6);
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }
}
