package net.frozenorb.foxtrot.server.keyalls.menu.editor;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
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

import java.util.*;

public class EditorMainMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Editor";
    }

    public Menu findMenu() {
        return this;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "New Key-All";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to create another Key-All");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                final ConversationFactory factory = new ConversationFactory(Proton.getInstance()).withFirstPrompt(new KeyAllPrompt(findMenu())).withLocalEcho(false);

                player.beginConversation(factory.buildConversation(player));
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final List<KeyAll> sortedList = new ArrayList<>(Foxtrot.getInstance().getKeyAllHandler().getCache().values());
        sortedList.sort(Comparator.comparingLong(KeyAll::getGiveAllTime));

        for (KeyAll keyAll : sortedList) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(keyAll.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&6❙ &fID: &e" + keyAll.getId()));
                    if (keyAll.getGiveAllTime() != 0) {
                        toReturn.add(ChatColor.translate("&6❙ &fStart At: &e" + new Date(keyAll.getGiveAllTime()).toLocaleString()));
                    }

                    if (keyAll.getEnd() != 0) {
                        toReturn.add(ChatColor.translate("&6❙ &fEnd At: &e" + new Date(keyAll.getEnd()).toLocaleString()));
                    }
                    toReturn.add(ChatColor.translate("&6❙ &fItems: &e" + keyAll.getItems().size()));
                    toReturn.add(ChatColor.translate("&6❙ &fRedeemed: &e" + keyAll.getRedeemed().size()));
                    toReturn.add(ChatColor.translate("&6❙ &fBoard Display: &e" + keyAll.getScoreboardDisplay()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to edit this key-all");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {

                    if (keyAll.getItems().isEmpty()) {
                        return Material.PAPER;
                    }

                    return keyAll.getItems().get(0).getType();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new EditKeyAllMenu(keyAll).openMenu(player);
                }
            });
        }

        return toReturn;
    }


    @AllArgsConstructor
    public static class KeyAllPrompt extends StringPrompt {
        private Menu menu;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type the ID for this key-all. Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            final Player sender = (Player) conversationContext.getForWhom();

            if (input.equalsIgnoreCase("cancel")) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Key-All creation process cancelled.");
                return END_OF_CONVERSATION;
            }


            if (Foxtrot.getInstance().getKeyAllHandler().getCache().keySet().stream().anyMatch(it -> it.equalsIgnoreCase(input))) {
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "A key-all with that ID already exists!");
                return END_OF_CONVERSATION;
            }

            final KeyAll keyAll = new KeyAll(input);

            Foxtrot.getInstance().getKeyAllHandler().getCache().put(input, keyAll);

            new EditKeyAllMenu(keyAll).openMenu(sender);
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
