package net.frozenorb.foxtrot.gameplay.events.outposts.command.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.command.CommandSender;

public class OutpostParameterType implements ParameterType<Outpost> {
    @Override
    public Outpost transform(CommandSender commandSender, String s) {
        return Foxtrot.getInstance().getOutpostHandler().findOutpost(s);
    }
}
