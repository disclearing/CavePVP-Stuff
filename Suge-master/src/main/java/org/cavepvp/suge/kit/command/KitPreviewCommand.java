package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavepvp.coinshop.CoinShop;
import org.cavepvp.coinshop.data.Category;
import org.cavepvp.coinshop.menu.shop.CategoryMenu;
import org.cavepvp.coinshop.menu.shop.MainMenu;
import org.cavepvp.suge.kit.data.Kit;
import org.cavepvp.suge.kit.menu.KitPreviewMenu;

public class KitPreviewCommand {

    @Command(names = {"kit preview"}, permission = "")
    public static void execute(Player player, @Parameter(name = "kit") Kit kit, @Parameter(name = "hehe", defaultValue = "false")boolean hehe) {
        final Category category = CoinShop.getInstance().getCoinShopHandler().findCategory("fasts-kits");

        new KitPreviewMenu(kit, new CategoryMenu(category, new MainMenu())).openMenu(player);
    }

}
