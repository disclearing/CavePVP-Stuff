package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CreatePollMenu extends Menu {

    private String question;
    private List<String> options;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.PAPER).name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Add Option" + ChatColor.GRAY + " [" + options.size() + "]").setLore(Arrays.asList("", ChatColor.GRAY + "Click to create a new poll.")).build();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();

                ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
                    public String getPromptText(ConversationContext context) {
                        return "§6Type the new option.";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String s) {
                        options.add(s);

                        player.sendMessage(ChatColor.GOLD + "Put a new option, question is " + ChatColor.WHITE + s + ChatColor.GOLD + ".");

                        openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false).withEscapeSequence("/null").withTimeout(20).thatExcludesNonPlayersWithMessage("Go away evil console!");

                Conversation con = factory.buildConversation(player);
                player.beginConversation(con);
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.NETHER_STAR).name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Finish").setLore(Arrays.asList("", ChatColor.GRAY + "Click to publish and announce the poll.")).build();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                if (options.size() < 2) {
                    player.sendMessage(ChatColor.RED + "There must be 2 or more questions.");
                    return;
                }

                new Poll(question, options);

                player.closeInventory();

                for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage("");
                    onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Polls");
                    onlinePlayer.sendMessage(ChatColor.translate("&7A poll named &f" + question + " &7has been created!"));
                    new FancyMessage(ChatColor.RED + "Vote on this poll! " + ChatColor.GREEN + "[Click Here]").tooltip(ChatColor.GREEN + "Click here").command("/polls").send(onlinePlayer);
                    onlinePlayer.sendMessage("");
                }
            }
        });

        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return "Poll";
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
