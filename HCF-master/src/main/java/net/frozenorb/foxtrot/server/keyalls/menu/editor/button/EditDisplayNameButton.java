package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class EditDisplayNameButton extends Button {
    private Menu menu;
    private KeyAll keyAll;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "Display Name";
    }

    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList("", ChatColor.translate("&6&l┃ &fCurrent: &e" + keyAll.getDisplayName()),
                "", ChatColor.GREEN + "Click to modify this key-all's display-name");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        final ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withFirstPrompt(new DisplayNamePrompt(menu, keyAll)).withLocalEcho(false);

        player.beginConversation(factory.buildConversation(player));
    }

    @AllArgsConstructor
    public static class DisplayNamePrompt extends StringPrompt {
        @Getter private Menu menu;
        @Getter private KeyAll keyAll;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type the new display name for the keyall. Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            final Player sender = (Player) conversationContext.getForWhom();

            if (input.equalsIgnoreCase("cancel")) {
                menu.openMenu(sender);
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Key-All display name process cancelled.");
                return END_OF_CONVERSATION;
            }

            menu.openMenu(sender);
            keyAll.setDisplayName(ChatColor.translate(input));
            Foxtrot.getInstance().getKeyAllHandler().getCache().replace(input, keyAll);

            conversationContext.getForWhom().sendRawMessage(ChatColor.translate("&6Set the display-name for &f" + keyAll.getId() + " &6to &f" + input));
            return Prompt.END_OF_CONVERSATION;
        }
    }
}