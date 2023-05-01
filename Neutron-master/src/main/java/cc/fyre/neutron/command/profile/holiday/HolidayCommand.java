package cc.fyre.neutron.command.profile.holiday;

import cc.fyre.neutron.command.profile.holiday.menu.HolidayMenu;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class HolidayCommand {
    @Command(names = {"holiday", "holiday theme", "holiday layout"}, permission = "command.holidaytheme")
    public static void execute(Player player) {
        new HolidayMenu().openMenu(player);
    }
}
