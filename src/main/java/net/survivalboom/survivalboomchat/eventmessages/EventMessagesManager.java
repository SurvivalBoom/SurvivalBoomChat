package net.survivalboom.survivalboomchat.eventmessages;

import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class EventMessagesManager implements Listener {

    private static final Map<String, EventMessage> messages = new HashMap<>();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new EventMessagesManager(), SurvivalBoomChat.getPlugin());
        reload();
    }

    public static void reload() {

        messages.clear();

        ConfigurationSection section = SurvivalBoomChat.getPlugin().getConfig().getConfigurationSection("events");
        if (section == null) return;

        for (String s : section.getKeys(false)) {
            try { messages.put(s, new EventMessage(s, section.getConfigurationSection(s))); }
            catch (Exception e) { PluginMessages.consoleSend(String.format("&c>> &fFailed to load event message &c%s&f: &c%s", s, e.getMessage())); }
        }

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        EventMessage message;
        if (player.hasPlayedBefore()) message = messages.get("join");
        else message = messages.get("first-join");

        if (message == null || !message.isEnabled()) return;

        Placeholders placeholders = new Placeholders();
        placeholders.add("{PREFIX}", Utils.getPrefix(player));
        placeholders.add("{SUFFIX}", Utils.getSuffix(player));
        placeholders.add("{PLAYER}", player.getName());

        if (Bukkit.getOnlinePlayers().size() > message.getOnlineLimit()) {
            event.joinMessage(null);
            return;
        }

        event.joinMessage(PluginMessages.parse(message.getMessage(), player, placeholders));

        if (message.getEvent() != null) message.getEvent().perform(player, placeholders);

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        EventMessage message = messages.get("leave");
        if (message == null || !message.isEnabled()) return;

        Placeholders placeholders = new Placeholders();
        placeholders.add("{PREFIX}", Utils.getPrefix(player));
        placeholders.add("{SUFFIX}", Utils.getSuffix(player));
        placeholders.add("{PLAYER}", player.getName());

        if (Bukkit.getOnlinePlayers().size() > message.getOnlineLimit()) {
            event.quitMessage(null);
            return;
        }

        event.quitMessage(PluginMessages.parse(message.getMessage(), player, placeholders));

        if (message.getEvent() != null) message.getEvent().perform(player, placeholders);

    }

    @EventHandler (ignoreCancelled = true)
    public void playerDoneAdvancement(PlayerAdvancementDoneEvent event) {

        Player player = event.getPlayer();

        Advancement advancement = event.getAdvancement();
        if (advancement.equals(advancement.getParent())) return;

        EventMessage message = messages.get("advancement");
        if (message == null || !message.isEnabled()) return;

        Placeholders placeholders = new Placeholders();
        placeholders.add("{PREFIX}", Utils.getPrefix(player));
        placeholders.add("{SUFFIX}", Utils.getSuffix(player));
        placeholders.add("{PLAYER}", player.getName());

        if (Bukkit.getOnlinePlayers().size() > message.getOnlineLimit()) {
            event.message(null);
            return;
        }

        //noinspection UnstableApiUsage
        event.message(PluginMessages.parse(message.getMessage(), player, placeholders).replaceText("{ADVANCEMENT}", event.getAdvancement().displayName()));

        if (message.getEvent() != null) message.getEvent().perform(player, placeholders);

    }

    @EventHandler (ignoreCancelled = true)
    public void playerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        EventMessage message = messages.get("death");
        if (message == null || !message.isEnabled()) return;

        Placeholders placeholders = new Placeholders();
        placeholders.add("{PREFIX}", Utils.getPrefix(player));
        placeholders.add("{SUFFIX}", Utils.getSuffix(player));
        placeholders.add("{PLAYER}", player.getName());

        if (Bukkit.getOnlinePlayers().size() > message.getOnlineLimit()) {
            event.deathMessage(null);
            return;
        }

        event.deathMessage(PluginMessages.parse(message.getMessage(), player, placeholders));

        if (message.getEvent() != null) message.getEvent().perform(player, placeholders);

    }

}
