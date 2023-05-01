package cc.fyre.neutron.command.profile.note;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.comparator.NoteDateComparator;
import cc.fyre.neutron.profile.menu.NotesMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteListCommand {

    @Command(
            names = {"note list","notes list"},
            permission = "neutron.command.note.list"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Flag(value = "g")boolean gui) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (sender instanceof Player && gui) {
            new NotesMenu(profile).openMenu((Player)sender);
            return;
        }

        for (Note note : profile.getNotes().stream().sorted(new NoteDateComparator()).collect(Collectors.toList())) {
            sender.sendMessage(ChatColor.GOLD + TimeUtils.formatIntoCalendarString(new Date(note.getExecutedAt())) + ChatColor.GRAY + " -> " + note.getExecutedByFancyName() + ChatColor.GRAY  + " -> " + ChatColor.WHITE + note.getExecutedReason());
        }

    }

}
