package cc.fyre.piston.custom.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.piston.Piston;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomRankMainMenu extends Menu {
    private Profile profile;

    @Override
    public String getTitle(Player player) {
        return "Custom Rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lReset Prefix");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Reset your prefix back to normal.");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&lCurrent"));
                toReturn.add(ChatColor.translate(NeutronConstants.formatChatDisplay(player, "Hello World!")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to reset your prefix back to normal.");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BED;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.setCustomPrefix(null);
                profile.save();

                player.sendMessage(ChatColor.RED + "Reset your prefix back to normal.");
                player.closeInventory();
            }
        });

        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lCreate Custom Prefix");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Make a custom prefix that shows in chat!");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&lCurrent"));
                toReturn.add(ChatColor.translate(NeutronConstants.formatChatDisplay(player, "Hello World!")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to start the custom prefix process.");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                final ConversationFactory factory = new ConversationFactory(Piston.getInstance()).withFirstPrompt(new DisplayNamePrompt(getMenu(), profile)).withLocalEcho(false);

                player.beginConversation(factory.buildConversation(player));
            }
        });


        toReturn.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lChoose Rank Prefix");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Disguise as another rank!");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&lCurrent"));
                toReturn.add(ChatColor.translate(NeutronConstants.formatChatDisplay(player, "Hello World!")));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to chose what rank to disguise as.");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ChooseRankMenu(profile).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    public Menu getMenu() {
        return this;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @AllArgsConstructor
    public static class DisplayNamePrompt extends StringPrompt {
        @Getter
        private Menu menu;
        @Getter private Profile profile;

        public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");

        public static List<String> filter = Arrays.asList("nigger", "highroller", "nigga", "olympus", "chink", "jigaboo", "george",
                "floyd", "spick", "hassan", "dakhil", "fuck", "cunt", "shit", "bitch", "n1gger", "n1gg3r", "georgefloyd", "dick");

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.YELLOW + "Please type the new name for the new prefix. No color codes or symbols. Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            final Player sender = (Player) conversationContext.getForWhom();

            if (input.equalsIgnoreCase("cancel")) {
                menu.openMenu(sender);
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Custom prefix process cancelled.");
                return END_OF_CONVERSATION;
            }

            if (ALPHA_NUMERIC.matcher(input).find()) {
                menu.openMenu(sender);
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Names must be alphanumeric!");
                return END_OF_CONVERSATION;
            }

            if (Piston.getInstance().getChatHandler().isFiltered(input)) {
                menu.openMenu(sender);
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "You can't use '" + Piston.getInstance().getChatHandler().isFilteredFind(input) + "'");
                return END_OF_CONVERSATION;
            }

            for (Rank rank : new ArrayList<>(Neutron.getInstance().getRankHandler().getCache().values())) {

                if (!rank.hasMetaData("STAFF")) {
                    continue;
                }

                if (input.toLowerCase().contains(rank.getName().toLowerCase())) {
                    menu.openMenu(sender);
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Can't use the name " + rank.getName() + "!");
                    return END_OF_CONVERSATION;
                }
            }

            for (Pattern pattern : Piston.getInstance().getChatHandler().getInappropriate().keySet()) {

                if (!pattern.matcher(input.toLowerCase()).find()) {
                    continue;
                }
                menu.openMenu(sender);
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Message contains inappropriate content.");
                return END_OF_CONVERSATION;
            }

            for (String label : filter) {

                if (input.toLowerCase().contains(label)) {
                    menu.openMenu(sender);
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "You can't use '" + Piston.getInstance().getChatHandler().isFilteredFind(input) + "'");
                    return END_OF_CONVERSATION;
                }

            }

            new ChooseColorMenu(profile, input).openMenu(sender);

            sender.playSound(sender.getLocation(), Sound.NOTE_PLING, 1, 1);

            conversationContext.getForWhom().sendRawMessage(ChatColor.translate("&aChoose a color for your custom prefix"));
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
