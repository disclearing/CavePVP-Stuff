package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.comparator.NoteDateComparator;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class NotesMenu extends PaginatedMenu {

    @Getter
    private Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.profile.getFancyName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {

        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Note note : this.profile.getNotes().stream().sorted(new NoteDateComparator()).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(), new Button() {

                @Override
                public String getName(Player player) {
                    return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(note.getExecutedAt()));
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "By: " + Proton.getInstance().getUuidCache().name(note.getExecutor()));
                    toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + note.getExecutedReason());
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.MAP;
                }

            });
        }

        return toReturn;
    }
}
