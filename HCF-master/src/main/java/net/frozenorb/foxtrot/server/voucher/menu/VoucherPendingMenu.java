package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class VoucherPendingMenu extends PaginatedMenu {

    private UUID target;

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 18;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        Foxtrot.getInstance().getVoucherHandler().getCache().stream().filter(it -> !it.isUsed()).filter(it -> it.getTarget().toString().equals(target.toString())).forEach(it -> toReturn.put(toReturn.size(), new VoucherElement(it, this)));

        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.YELLOW + "Pending Vouchers";
    }
}
