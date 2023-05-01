package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import net.minecraft.util.com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultKit extends Kit {

    @Getter
    @Setter
    private ItemStack icon;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int order = 1;

    @Getter
    @Setter
    private List<ItemStack> editorItems = new ArrayList<>();

    @Getter @Setter private String displayName = "";

    public DefaultKit(String kitName) {
        super(kitName);
    }

    public static class Type implements ParameterType<DefaultKit> {
        @Override
        public DefaultKit transform(CommandSender sender, String source) {
            DefaultKit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(source);

            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "Default kit '" + source + "' not found.");
                return null;
            }

            return kit;
        }

        @Override
        public List<String> tabComplete(Player player, Set<String> flags, String source) {
            List<String> completions = Lists.newArrayList();

            for (DefaultKit kit : Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKits()) {
                if (StringUtils.startsWith(kit.getName(), source)) {
                    completions.add(kit.getName());
                }
            }

            return completions;
        }
    }

}
