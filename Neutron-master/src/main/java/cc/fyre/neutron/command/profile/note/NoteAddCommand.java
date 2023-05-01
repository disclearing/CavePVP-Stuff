package cc.fyre.neutron.command.profile.note;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.packet.NoteApplyPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NoteAddCommand {

    @Command(
            names = {"note add","notes add"},
            permission = "neutron.command.note.add",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "reason",wildcard = true)String reason) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        final Note note = new Note(UUID.randomUUID(),uuid,System.currentTimeMillis(),reason);

        profile.getNotes().add(note);
        profile.save();

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new NoteApplyPacket(profile.getUuid(),note.toDocument()));
        }

        sender.sendMessage(ChatColor.GOLD + "Added a note to " + profile.getFancyName() + ChatColor.GOLD + "'s account.");
    }

}
