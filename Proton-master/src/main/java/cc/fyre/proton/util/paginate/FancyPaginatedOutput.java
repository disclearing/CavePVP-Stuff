package cc.fyre.proton.util.paginate;

import com.google.common.base.Preconditions;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FancyPaginatedOutput <T> {

    private final int resultsPerPage;

    public FancyPaginatedOutput() {
        this(9);
    }

    public FancyPaginatedOutput(int resultsPerPage) {
        Preconditions.checkArgument(resultsPerPage > 0);
        this.resultsPerPage = resultsPerPage;
    }

    public abstract FancyMessage getHeader(int var1,int var2);

    public abstract FancyMessage format(T var1, int var2);

    public final void display(CommandSender sender,int page,Collection<? extends T> results) {
        this.display(sender, page, (List)(new ArrayList(results)));
    }

    public final void display(CommandSender sender, int page, List<? extends T> results) {
        if (results.size() == 0) {
            sender.sendMessage(ChatColor.RED + "No entries found.");
        } else {
            int maxPages = results.size() / this.resultsPerPage + 1;
            if (page > 0 && page <= maxPages) {
                this.getHeader(page, maxPages).send(sender);

                for(int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
                    this.format(results.get(i), i).send(sender);
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Page " + ChatColor.YELLOW + page + ChatColor.RED + " is out of bounds. (" + ChatColor.YELLOW + "1 - " + maxPages + ChatColor.RED + ")");
            }
        }
    }
}
