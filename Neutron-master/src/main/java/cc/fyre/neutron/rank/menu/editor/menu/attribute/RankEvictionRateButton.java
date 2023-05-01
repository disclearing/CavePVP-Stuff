package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.menu.grants.ConfirmGrantMenu;
import cc.fyre.neutron.profile.menu.grants.GrantMenu;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankEvictionRateButton extends Button {

    @Getter private Rank rank;
    @Getter private Menu menu;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Eviction Rate: " + ChatColor.WHITE + TimeUtils.formatIntoHHMMSS((int) (rank.getEvictionRate()/1000));
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK to change the eviciton rate");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WATCH;
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {
        player.closeInventory();

        final ConversationFactory factory = new ConversationFactory(Proton.getInstance()).withFirstPrompt(new DurationPrompt(rank, this.menu)).withLocalEcho(false);

        player.beginConversation(factory.buildConversation(player));
    }

    @AllArgsConstructor
    public class DurationPrompt extends StringPrompt {

        @Getter private Rank rank;
        @Getter private Menu menu;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type a duration for this rank's eviction rate";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            if (input.equalsIgnoreCase("cancel")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Eviction Rate process cancelled.");
                return END_OF_CONVERSATION;
            }

            DurationWrapper durationWrapper = NeutronConstants.findDurationWrapper(input);

            if (durationWrapper.isPermanent()) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Permanent eviction rate isn't allowed.");
                return END_OF_CONVERSATION;
            }

            rank.setEvictionRate(durationWrapper.getDuration());
            rank.save();
            menu.openMenu((Player) conversationContext.getForWhom());

            conversationContext.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&',"&6Set eviction rate for " + rank.getFancyName() + " &6to &f" + durationWrapper.getSource() + "&6!"));
            return Prompt.END_OF_CONVERSATION;
        }
    }

}
