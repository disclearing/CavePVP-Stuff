package cc.fyre.proton.util.paginate;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PaginatedOutput<T> {
    private final int resultsPerPage;

    public PaginatedOutput() {
        this(9);
    }

    public PaginatedOutput(int resultsPerPage) {
        Preconditions.checkArgument(resultsPerPage > 0);
        this.resultsPerPage = resultsPerPage;
    }

    public abstract String getHeader(int var1, int var2);

    public abstract String format(T var1, int var2);

    public final void display(CommandSender sender,int page,Collection<? extends T> results) {
        this.display(sender, page, (new ArrayList<>(results)));
    }

    public final void display(CommandSender sender, int page, List<? extends T> results) {
        if (results.size() == 0) {
            sender.sendMessage(ChatColor.RED + "No entries found.");
        } else {
            int maxPages = results.size() / this.resultsPerPage + 1;
            if (page > 0 && page <= maxPages) {
                sender.sendMessage(this.getHeader(page, maxPages));

                for(int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
                    sender.sendMessage(this.format(results.get(i), i));
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Page " + ChatColor.YELLOW + page + ChatColor.RED + " is out of bounds. (" + ChatColor.YELLOW + "1 - " + maxPages + ChatColor.RED + ")");
            }
        }
    }
}