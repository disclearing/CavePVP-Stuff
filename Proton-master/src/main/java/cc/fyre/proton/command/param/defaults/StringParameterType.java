package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.command.CommandSender;

public class StringParameterType implements ParameterType<String> {

    public String transform(CommandSender sender,String value) {
        return value;
    }

}
