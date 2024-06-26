package net.survivalboom.survivalboomchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Utils {

    private static final JavaPlugin plugin = SurvivalBoomChat.getPlugin();
    private static final boolean vaultInstalled = Bukkit.getPluginManager().isPluginEnabled("Vault");

    public static void removeItem(@NotNull Player player, @NotNull Material material, int amount) {
        Inventory inventory = player.getInventory();
        ItemStack itemStack = new ItemStack(material);
        itemStack.setAmount(amount);
        inventory.removeItemAnySlot(itemStack);
    }

    public static boolean copyPluginFile(String plugin_file_path) {

        File file = new File(plugin.getDataFolder(), plugin_file_path);
        if (file.exists()) return false;

        String[] path_parts = plugin_file_path.split("/");

        if (!plugin_file_path.contains("/")) { plugin.saveResource(plugin_file_path, true); return true;}
        else plugin.saveResource(path_parts[path_parts.length - 1], true);

        String[] args = plugin_file_path.split("/");

        File origin = new File(plugin.getDataFolder(), args[args.length - 1]);
        origin.renameTo(file);

        return true;
    }

    public static boolean createPluginFolder(String path) {

        File file = new File(plugin.getDataFolder(), path);;

        if (!file.exists()) return file.mkdirs();
        else return false;

    }

    @Nullable
    public static String getArrayValue(@NotNull String[] array, int index) {
        try { return array[index]; } catch (Exception e) { return null; }
    }

    public static void sendPluginError(@NotNull String msg, @NotNull Exception e) {

        PluginMessages.consoleSend("&cSurvivalBoomChat &8&l► &f: " + msg);
        PluginMessages.consoleSend(String.format("&6>> &e%s", e));

        for (StackTraceElement element : e.getStackTrace()) {
            PluginMessages.consoleSend(String.format("&4>> &c%s", element));
        }

    }

    public static void sendStackTrace(@NotNull Exception e) {
        PluginMessages.consoleSend(String.format("&6>> &e%s", e));
        for (StackTraceElement element : e.getStackTrace()) PluginMessages.consoleSend(String.format("&4>> &c%s", element));
    }

    @NotNull
    public static String getPrefix(@NotNull Player player) {

        if (!vaultInstalled) return "";

        RegisteredServiceProvider<Chat> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (registeredServiceProvider == null) return "";

        return registeredServiceProvider.getProvider().getPlayerPrefix(player);

    }

    @NotNull
    public static String getSuffix(@NotNull Player player) {

        if (!vaultInstalled) return "";

        RegisteredServiceProvider<Chat> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (registeredServiceProvider == null) return "";

        return registeredServiceProvider.getProvider().getPlayerSuffix(player);

    }

    public static <T extends Enum<T>> T getEnumValue(Class<T> enumType, String name) {
        try { return Enum.valueOf(enumType, name);}
        catch (IllegalArgumentException e) { return null; }
    }

    @NotNull
    public static Component componentReplace(@NotNull Component in, @NotNull String replacement, @NotNull String value) {

        String serialised = componentDeserialize(in);

        serialised = serialised.replaceAll(String.format("(?i)%s", replacement), value);

        SurvivalBoomChat.getPlugin().getLogger().warning(serialised);

        return PluginMessages.parseOnlyColors(serialised);

    }

    @NotNull
    public static String componentDeserialize(@NotNull Component in) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        return serializer.serialize(in);
    }

}
