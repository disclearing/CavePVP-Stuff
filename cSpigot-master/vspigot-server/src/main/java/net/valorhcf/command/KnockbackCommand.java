package net.valorhcf.command;

import net.valorhcf.knockback.CraftKnockback;
import net.valorhcf.knockback.Knockback;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.spigotmc.SpigotConfig;

import java.util.Arrays;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("beefwellington");

        this.setAliases(Arrays.asList("knockback", "kb"));
        this.setUsage(StringUtils.join(new String[]{
                ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 35),
                ChatColor.RED + "/kb list",
                ChatColor.RED + "/kb create <name>",
                ChatColor.RED + "/kb delete <name>",
                ChatColor.RED + "/kb update <name> <f> <h> <v> <vl> <eh> <ev>",
                ChatColor.RED + "/kb setglobal <name>",
                ChatColor.RED + "/kb toggle <hcf/wtap>",
                ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 35)
        }, "\n"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Unknown command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list": {
                SpigotConfig.sendKnockbackInfo(sender);
            }
            break;
            case "toggle": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid usage!");
                } else {
                    switch (args[1].toLowerCase()) {
                        case "hcf": {
                            SpigotConfig.hcf = !SpigotConfig.hcf;

                            if (SpigotConfig.hcf) {
                                sender.sendMessage(ChatColor.GREEN + "HCF mode enabled!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "HCF mode disabled!");
                            }

                            SpigotConfig.saveKnockbackProfiles();
                        }
                        break;
                        case "wtap": {
                            SpigotConfig.wtap = !SpigotConfig.wtap;

                            if (SpigotConfig.wtap) {
                                sender.sendMessage(ChatColor.GREEN + "Auto-WTap enabled!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Auto-WTap disabled!");
                            }

                            SpigotConfig.saveKnockbackProfiles();
                        }
                        break;
                        default: {
                            sender.sendMessage(ChatColor.RED + "Invalid usage!");
                        }
                        break;
                    }
                }
            }
            break;
            case "create": {
                if (args.length > 1) {
                    String name = args[1];

                    for (Knockback profile : SpigotConfig.knockbacks) {
                        if (profile.getName().equalsIgnoreCase(name)) {
                            sender.sendMessage(ChatColor.RED + "A profile with that name already exists.");
                            return true;
                        }
                    }

                    CraftKnockback profile = new CraftKnockback(name);

                    SpigotConfig.knockbacks.add(profile);
                    SpigotConfig.saveKnockbackProfiles();

                    sender.sendMessage(ChatColor.GOLD + "New profile created.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /kb create <name>");
                }
            }
            break;
            case "delete": {
                if (args.length > 1) {
                    final String name = args[1];

                    if (SpigotConfig.defaultKnockback.getName().equalsIgnoreCase(name)) {
                        sender.sendMessage(ChatColor.RED + "You can't delete the active global knockback profile.");
                        return true;
                    } else {
                        if (SpigotConfig.knockbacks.removeIf(profile -> profile.getName().equalsIgnoreCase(name))) {
                            SpigotConfig.saveKnockbackProfiles();
                            sender.sendMessage(ChatColor.RED + "Deleted profile.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        }

                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /kb delete <name>");
                }
            }
            break;
            case "update": {
                if (args.length == 8) {
                    Knockback profile = SpigotConfig.getKnockbackByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }

                    profile.setFriction(Double.parseDouble(args[2]));
                    profile.setHorizontal(Double.parseDouble(args[3]));
                    profile.setVertical(Double.parseDouble(args[4]));
                    profile.setVerticalLimit(Double.parseDouble(args[5]));
                    profile.setExtraHorizontal(Double.parseDouble(args[6]));
                    profile.setExtraVertical(Double.parseDouble(args[7]));

                    SpigotConfig.saveKnockbackProfiles();

                    sender.sendMessage(ChatColor.GREEN + "Updated values.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "setglobal": {
                if (args.length > 1) {
                    Knockback profile = SpigotConfig.getKnockbackByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }

                    SpigotConfig.defaultKnockback = profile;
                    SpigotConfig.saveKnockbackProfiles();

                    sender.sendMessage(ChatColor.GREEN + "Global profile set to " + profile.getName() + ".");
                    return true;
                }
            }
            break;
            default: {
                sender.sendMessage(usageMessage);
            }
        }

        return true;
    }

}
