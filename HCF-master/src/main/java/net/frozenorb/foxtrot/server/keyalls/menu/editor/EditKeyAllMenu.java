package net.frozenorb.foxtrot.server.keyalls.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class EditKeyAllMenu extends Menu {
    private KeyAll keyAll;

    @Override
    public String getTitle(Player player) {
        return "Edit Key-All";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(10, new EditDisplayNameButton(this, keyAll));
        toReturn.put(11, new EditScoreboardNameButton(this, keyAll));
        toReturn.put(12, new EditGiveAllButton(this, keyAll));
        toReturn.put(13, new EditEndButton(this, keyAll));
        toReturn.put(14, new EditItemsButton(keyAll));
        toReturn.put(15, new EditRedeemedButton(this, keyAll));
        toReturn.put(16, new EditGivingButton(this, keyAll));

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public void onClose(Player player) {
        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> new EditorMainMenu().openMenu(player), 1);
    }
}
