package org.cavepvp.profiles.menu;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MainMenu extends Menu {
    private Profile profile;
    private PlayerProfile playerProfile;

    @Override
    public String getTitle(Player player) {
        return "Your Profile";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return new HashMap<>(ProfilesSharedButtons.mainButtons(player, playerProfile, profile, MenuType.MAIN_MENU, false));
    }
}
