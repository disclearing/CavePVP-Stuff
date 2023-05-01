package net.frozenorb.foxtrot.chat.trivia;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TriviaHandler implements Listener {
    private Foxtrot instance;

    @Getter private Map<String, String> questions = new HashMap<>();
    @Getter private String chosenQuestion = null;
    @Getter private String chosenAnswer = null;
    @Getter @Setter private Integer riggedNumber = null;
    @Getter @Setter private boolean chatReaction = false;

    public TriviaHandler(Foxtrot instance) {
        questions.put("In what year was Minecraft released?", "2011");
        questions.put("What is the current top rank on CavePvP?", "Cave");
        questions.put("What is the lowest rank on CavePvP?", "Iron");
        questions.put("What is CavePvP's Discord link?", "discord.gg/cavepvp");
        questions.put("How many states are there in the United States?", "50");
        questions.put("Is tomato a fruit?", "Yes");
        questions.put("What country has the 2nd largest GDP?", "China");
        questions.put("What country is largest in population?", "India");
        questions.put("What country is largest in land size?", "Russia");
        questions.put("What's 6x6?", "36");
        questions.put("What's 829+219?", "1048");
        questions.put("What's 3x3+349?", "358");
        questions.put("What's 348+233?", "358");
        questions.put("What's the first 5 numbers of PI?", "3.1415");
        questions.put("What's 8 squared?", "64");
        questions.put("What's 12 squared?", "144");
        questions.put("What's 3 squared?", "9");
        questions.put("What's 9 cubed?", "729");
        questions.put("What's 48+16", "64");
        questions.put("How many months have 28 days?", "12");
        questions.put("When was the CavePvP Christmas Update?", "December");
        questions.put("How many continents are there?", "7");
        questions.put("What's the largest crypto currency?", "Bitcoin");
        questions.put("What company invented the iPhone?", "Apple");
        questions.put("What company invented the Playstation?", "Sony");
        questions.put("What company invented the Xbox?", "Microsoft");
        questions.put("Who created Minecraft?", "Notch");
        questions.put("What's the name of the Minecraft convention?", "Minecon");
        questions.put("What's the name of the Twitch convention?", "Twitchcon");
        questions.put("What's the final boss in Minecraft?", "Enderdragon");
        questions.put("What version is Minecraft currently on?", "1.17");
        questions.put("What is the IP to CavePvP?", "cavepvp.org");
        questions.put("What is the new Minecraft mob that was voted upon?", "Allay");
        questions.put("What is the 8th letter of the alphabet?", "H");
        questions.put("What is the 12th letter of the alphabet?", "L");
        questions.put("What is the 18th letter of the alphabet?", "R");
        questions.put("What is the 24th letter of the alphabet?", "W");
        questions.put("chatreaction:SimplyTrash", "SimplyTrash");
        questions.put("chatreaction:DrePvP", "DrePvP");
        questions.put("chatreaction:iMakeMcVids", "iMakeMcVids");
        questions.put("chatreaction:Headed", "Headed");
        questions.put("chatreaction:Vik", "Vik");
        questions.put("chatreaction:Lectors", "Lectors");
        questions.put("chatreaction:Frozeado", "Frozeado");
        questions.put("chatreaction:SamHCF", "SamHCF");
        questions.put("chatreaction:CavePvP", "CavePvP");
        questions.put("chatreaction:CavePvP On Top", "CavePvP On Top");
        questions.put("chatreaction:Trojan", "Trojan");

        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
        this.instance.getServer().getScheduler().runTaskTimer(instance, this::start, (20*60)*90+80, (20*60)*90+80);
    }

    public void start() {
        final Server server = this.instance.getServer();

        final List<String> questionList = new ArrayList<>(questions.keySet());

        final String question = questionList.get(ThreadLocalRandom.current().nextInt(questionList.size()-1));
        final String answer = questions.get(question);
        this.chosenQuestion = question;
        this.chosenAnswer = answer;

        if (question.startsWith("chatreaction:")) {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.translate("&4&lChat Reaction"));
            server.broadcastMessage(answer);
            server.broadcastMessage(ChatColor.translate("&cFirst person to type the message above will receive &c&la random prize&7!"));
            server.broadcastMessage("");
        } else {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.translate("&4&lTrivia"));
            server.broadcastMessage(ChatColor.translate(question));
            server.broadcastMessage(ChatColor.GRAY + "First person to get the answer right will get a " + ChatColor.RED + ChatColor.BOLD.toString() + "random prize" + ChatColor.GRAY + "!");
            server.broadcastMessage("");
        }

        server.getScheduler().runTaskLater(instance, () -> {
            if (!this.chosenAnswer.equalsIgnoreCase(answer)) {
                return;
            }

            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.RED + "Nobody got the answer in time!");
            server.broadcastMessage("");

            this.chosenQuestion = null;
            this.chosenAnswer = null;
            this.riggedNumber = null;
        }, 20*30);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(PlayerChatEvent event) {
        final String message = event.getMessage().replace("!", "");
        if (event.getMessage().equalsIgnoreCase("null") || this.chosenQuestion == null || this.chosenAnswer == null || !this.chosenAnswer.equalsIgnoreCase(message)) {
            return;
        }

        final Player player = event.getPlayer();
        final Server server = this.instance.getServer();
        final int randomNumber = this.riggedNumber == null ? ThreadLocalRandom.current().nextInt(0, 100) : this.riggedNumber;
        String prize;

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            prize = ChatColor.translate("&a&l100x Gems");
            server.dispatchCommand(server.getConsoleSender(), "gems add " + player.getName() + " 100");
        } else if (randomNumber == -1) {
            prize = ChatColor.translate("&7an &b&lAirdrop");
            server.dispatchCommand(server.getConsoleSender(), "airdrops give " + player.getName() + " 1");
        } else if (randomNumber < 3) {
            prize = ChatColor.translate("&7a &e&ki&6&lHalloween Key&e&ki&r");
            server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Seasonal 1");
        } else if (randomNumber < 6) {
            prize = ChatColor.translate("&7a &6&lFall Lootbox");
            server.dispatchCommand(server.getConsoleSender(), "crates give " + player.getName() + " Seasonal 1");
        } else {
            prize = ChatColor.translate("&5&l3x Legendary Keys");
            server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Legendary 3");
        }

        server.broadcastMessage(ChatColor.translate("&4&lTrivia &8┃ &f" + player.getName() + " &7got the answer &f'" + this.chosenAnswer + "' &7and received " + prize + "&7!"));

        this.chosenAnswer = null;
        this.chosenQuestion = null;
        this.riggedNumber = null;
    }

    @Command(names = {"trivia start"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "number", defaultValue = "-5")int number) {
        Foxtrot.getInstance().getTriviaHandler().start();

        if (number != -5) {
            Foxtrot.getInstance().getTriviaHandler().setRiggedNumber(number);
        } else {
            Foxtrot.getInstance().getTriviaHandler().setRiggedNumber(null);
        }
    }
}
