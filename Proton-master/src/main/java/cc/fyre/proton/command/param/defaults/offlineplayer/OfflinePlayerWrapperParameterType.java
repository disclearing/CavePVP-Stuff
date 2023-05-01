package cc.fyre.proton.command.param.defaults.offlineplayer;

import cc.fyre.proton.command.param.ParameterType;

import org.bukkit.command.CommandSender;

/*** @author xanderume@gmail (JavaProject)
 */
public class OfflinePlayerWrapperParameterType implements ParameterType<OfflinePlayerWrapper> {

    @Override
    public OfflinePlayerWrapper transform(final CommandSender sender,final String source) {
        return new OfflinePlayerWrapper(source);
    }

}
