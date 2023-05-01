package net.frozenorb.foxtrot.team.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.team.menu.button.ChangePromotionStatusButton;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class DemoteMembersMenu extends Menu {

    @NonNull @Getter Team team;

    @Override
    public String getTitle(Player player) {
        return "Demote captains/co-leaders";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (UUID uuid : team.getColeaders()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, team, false));
            index++;
        }

        for (UUID uuid : team.getCaptains()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, team, false));
            index++;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}