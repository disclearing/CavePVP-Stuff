package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import net.frozenorb.foxtrot.team.upgrade.effects.PurchaseableEffects;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LettingInButton extends Button {
    private Team team;
    private Menu menu;

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Letting In";
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.GRAY + "Signal to everyone that you're");
        toReturn.add(ChatColor.GRAY + "letting players into your base!");
        toReturn.add("");
        toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &cDisabled"));
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Click to signal players that you're letting in");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOD;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        player.beginConversation(new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return ChatColor.translate("&cType your base height in chat");
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {

                final Integer number = Ints.tryParse(s);

                if (number == null) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                    return END_OF_CONVERSATION;
                }

                Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                    player.chat("/lettingin " + number);
                    menu.openMenu(player);
                }, 2);
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!").buildConversation(player));
    }
}
