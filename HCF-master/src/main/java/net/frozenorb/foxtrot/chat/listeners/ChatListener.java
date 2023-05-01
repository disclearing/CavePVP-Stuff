package net.frozenorb.foxtrot.chat.listeners;

import cc.fyre.piston.Piston;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.frozenorb.foxtrot.FoxConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamMuteCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {
  
	private String getCustomPrefix(UUID uuid) {
		Map<Integer, UUID> placesMap = Foxtrot.getInstance().getMapHandler().getStatsHandler() != null ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getTopKills() : null;
		if (placesMap == null) {
			return "";
		}

		int place = placesMap.size() == 3 ? placesMap.get(1).equals(uuid) ? 1 : placesMap.get(2).equals(uuid) ? 2 : placesMap.get(3).equals(uuid) ? 3 : 99 : 99;

		if (place == 99) {
			return "";
		}

		return ChatColor.translateAlternateColorCodes('&', place == 1 ? "&2➊" : place == 2 ? "&a➋" : "&e➌") + " ";
	}

	@EventHandler(priority = EventPriority.LOWEST) // this handler prevents people from getting banned for spam in faction (or ally) chat
	public void onAsyncPlayerChatEarly(AsyncPlayerChatEvent event) {
		ChatMode playerChatMode = Foxtrot.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
		ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
		ChatMode finalChatMode;

		if (forcedChatMode != null) {
			finalChatMode = forcedChatMode;
		} else {
			finalChatMode = playerChatMode;
		}

		if (finalChatMode != ChatMode.PUBLIC) {
			event.getPlayer().setMetadata("NoSpamCheck", new FixedMetadataValue(Foxtrot.getInstance(), true));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.getPlayer().removeMetadata("NoSpamCheck", Foxtrot.getInstance());

		Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
		ChatMode playerChatMode = Foxtrot.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
		ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
		ChatMode finalChatMode;

		if (Profiles.getInstance().hasStaffChatEnabled(event.getPlayer())) {
			return;
		}

		// If they provided us with a forced chat mode, we have to remove it from the final message.
		// We also .trim() the message because people will do things like '! hi', which just looks annoying in chat.
		if (forcedChatMode != null) {
			event.setMessage(event.getMessage().substring(1).trim());
		}

		if (forcedChatMode != null) {
			finalChatMode = forcedChatMode;
		} else {
			finalChatMode = playerChatMode;
		}

		// If another plugin cancelled this event before it got to us (we are on MONITOR, so it'll happen)
		if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) { // Only respect cancelled events if this is public chat. Who cares what their team says.
			return;
		}

		// Any route we go down will cancel the event eventually.
		// Let'	s just do it here.
		event.setCancelled(true);

		// If someone's not in a team, instead of forcing their 'channel' to public,
		// we just tell them they can't.
		if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
			return;
		}

		if (finalChatMode == ChatMode.OFFICER && !playerTeam.isCaptain(event.getPlayer().getUniqueId()) && !playerTeam.isCoLeader(event.getPlayer().getUniqueId()) && !playerTeam.isOwner(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
			return;
		}

		if (finalChatMode == ChatMode.PUBLIC && TeamMuteCommand.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
			return;
		}

		// and here starts the big logic switch
		switch (finalChatMode) {
			case PUBLIC:

				String publicChatFormat = FoxConstants.publicChatFormat(event.getPlayer(), playerTeam, event.getMessage(), getCustomPrefix(event.getPlayer().getUniqueId()));

				TextComponent component = null;
				if (event.getPlayer().hasMetadata("stream_link")) {
					String streamLink = (String) event.getPlayer().getMetadata("stream_link").get(0).value();
					component = new TextComponent();
					component.setText(ChatColor.RED + CC.BOLD + "[LIVE] ");
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(CC.GRAY + "Click to open stream!")}));
					component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, streamLink));
					component.addExtra(publicChatFormat);
				}

				// Loop those who are to receive the message (which they won't if they have the sender /ignore'd or something),
				// not online players
				for (Player player : event.getRecipients()) {
//					if (!event.getPlayer().isOp() && Piston.getInstance().getIgnoreCache().containsKey(player.getUniqueId()) &&
//							Piston.getInstance().getIgnoreCache().get(player.getUniqueId()).contains(event.getPlayer().getUniqueId())) {
//						continue;
//					}

					if (playerTeam == null) {
						// If the player sending the message is shadowmuted (if their team was and they left it)
						// then we don't allow them to. We probably could move this check "higher up", but oh well.
						if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
							continue;
						}

						// If their chat is enabled (which it is by default) or the sender is op, send them the message
						// The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
						if (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
							if (component != null) {
								player.spigot().sendMessage(component);
							} else {
								player.sendMessage(publicChatFormat);
							}
						}
					} else {
						if (playerTeam.isMember(player.getUniqueId())) {
							// Gypsie way to get a custom color if they're allies/teammates
							if (component != null) {
								player.spigot().sendMessage(component);
							} else {
								player.sendMessage(publicChatFormat.replace(ChatColor.GOLD + "[" + Foxtrot.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));
							}
						} else if (playerTeam.isAlly(player.getUniqueId())) {
							// Gypsie way to get a custom color if they're allies/teammates
							if (component != null) {
								player.spigot().sendMessage(component);
							} else {
								player.sendMessage(publicChatFormat.replace(ChatColor.GOLD + "[" + Foxtrot.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + Team.ALLY_COLOR));
							}
						} else {
							// We only check this here as...
							// Team members always see their team's messages
							// Allies always see their allies' messages, 'cause they'll probably be in a TS or something
							// and they could figure out this code even exists
							if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
								continue;
							}

							// If their chat is enabled (which it is by default) or the sender is op, send them the message
							// The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
							if (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
								if (component != null) {
									player.spigot().sendMessage(component);
								} else {
									player.sendMessage(publicChatFormat);
								}
							}
						}
					}
				}

				Foxtrot.getInstance().getServer().getConsoleSender().sendMessage(publicChatFormat);
				break;
			case ALLIANCE:
				String allyChatFormat = FoxConstants.allyChatFormat(event.getPlayer(), event.getMessage());
				String allyChatSpyFormat = FoxConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

				// Loop online players and not recipients just in case you're weird and
				// /ignore your teammates/allies
				for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
					if (playerTeam.isMember(player.getUniqueId()) || playerTeam.isAlly(player.getUniqueId())) {
						player.sendMessage(allyChatFormat);
					} else if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
						player.sendMessage(allyChatSpyFormat);
					}
				}

				// Log to ally's allychat log.
				for (ObjectId allyId : playerTeam.getAllies()) {
					Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

					if (ally != null) {
						TeamActionTracker.logActionAsync(ally, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.<String, Object>builder()
								.put("allyTeamId", playerTeam.getUniqueId())
								.put("allyTeamName", playerTeam.getName())
								.put("playerId", event.getPlayer().getUniqueId())
								.put("playerName", event.getPlayer().getName())
								.put("message", event.getMessage())
								.build()
						);
					}
				}

				TeamActionTracker.logActionAsync(playerTeam, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.of(
						"playerId", event.getPlayer().getUniqueId(),
						"playerName", event.getPlayer().getName(),
						"message", event.getMessage()
				));

				Foxtrot.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
				break;
			case TEAM:
				String teamChatFormat = FoxConstants.teamChatFormat(event.getPlayer(), event.getMessage());
				String teamChatSpyFormat = FoxConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

				// Loop online players and not recipients just in case you're weird and
				// /ignore your teammates
				for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
					if (playerTeam.isMember(player.getUniqueId())) {
						player.sendMessage(teamChatFormat);
					} else if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
						player.sendMessage(teamChatSpyFormat);
					}
				}

				// Log to our teamchat log.
				TeamActionTracker.logActionAsync(playerTeam, TeamActionType.TEAM_CHAT_MESSAGE, ImmutableMap.of(
						"playerId", event.getPlayer().getUniqueId(),
						"playerName", event.getPlayer().getName(),
						"message", event.getMessage()
				));

				Foxtrot.getInstance().getServer().getLogger().info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
				break;
			case OFFICER:
				String officerChatFormat = FoxConstants.officerChatFormat(event.getPlayer(), event.getMessage());

				// Loop online players and not recipients just in case you're weird and
				// /ignore your teammates
				for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
					if (playerTeam.isCaptain(player.getUniqueId()) || playerTeam.isCoLeader(player.getUniqueId()) || playerTeam.isOwner(player.getUniqueId())) {
						player.sendMessage(officerChatFormat);
					}
				}

				// Log to our teamchat log.
				TeamActionTracker.logActionAsync(playerTeam, TeamActionType.OFFICER_CHAT_MESSAGE, ImmutableMap.of(
						"playerId", event.getPlayer().getUniqueId(),
						"playerName", event.getPlayer().getName(),
						"message", event.getMessage()
				));

				Foxtrot.getInstance().getServer().getLogger().info("[Officer Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
				break;
		}
	}

}