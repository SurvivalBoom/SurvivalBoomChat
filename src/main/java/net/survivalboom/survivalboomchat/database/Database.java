package net.survivalboom.survivalboomchat.database;

import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.configuration.PluginMessages;
import net.survivalboom.survivalboomchat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.UUID;

public class Database implements Listener {

    private static DatabaseType type = null;
    private static String host = null;
    private static String user = null;
    private static String database = null;
    private static String password = null;
    private static Connection connection = null;
    private static boolean enabled = false;

    public static void init() {
        PluginMessages.consoleSend("&b>> &fLoading database...");
        reload();

        Bukkit.getPluginManager().registerEvents(new Database(), SurvivalBoomChat.getPlugin());

        if (!enabled) return;

        PluginMessages.consoleSend("&a>> &fConnected to database successfully!");
        checkTables();
    }

    public static void reload() {
        enabled = false;
        try { disconnect(); } catch (SQLException e) { throw new RuntimeException(e); }

        ConfigurationSection section = SurvivalBoomChat.getPlugin().getConfig().getConfigurationSection("database-settings");
        if (section == null) {
            PluginMessages.consoleSend("&c>> &fDatabase section not found in config.yml!");
            return;
        }

        if (!section.getBoolean("enabled")) {
            PluginMessages.consoleSend("&e>> &fDatabase is disabled in config.yml.");
            return;
        }

        String typeRaw = section.getString("type");
        type = Utils.getEnumValue(DatabaseType.class, typeRaw);
        if (type == null) {
            PluginMessages.consoleSend("&c>> &fInvalid database type. Database disabled!");
            return;
        }

        host = section.getString("host");
        if (host == null) {
            PluginMessages.consoleSend("&c>> &fInvalid database host. Database disabled!");
            return;
        }

        user = section.getString("user");
        if (user == null) {
            PluginMessages.consoleSend("&c>> &fInvalid database username. Database disabled!");
            return;
        }

        password = section.getString("password");
        if (password == null) {
            PluginMessages.consoleSend("&c>> &fInvalid database password. Database disabled!");
            return;
        }

        database = section.getString("database");
        if (database == null) {
            PluginMessages.consoleSend("&c>> &fInvalid database name. Database disabled!");
            return;
        }

        try { connect(); }
        catch (Exception e) {
            PluginMessages.consoleSend("&c>> &fFailed to connect to database! Database disabled!");
            PluginMessages.consoleSend(String.format("&6>> &e%s", e));
            return;
        }

        enabled = true;
    }

    public static void connect() throws SQLException {
        if (type == DatabaseType.MySQL) {
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", host, database), user, password);
        } else {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/SurvivalBoomChat/database.db");
        }
        connection.setAutoCommit(true);
    }

    public static void disconnect() throws SQLException {
        if (connection == null) return;
        connection.close();
    }

    public static void shutdown() {
        try { disconnect(); }
        catch (Exception e) {
            PluginMessages.consoleSend("&4>> &fDatabase disconnect failed with exception:");
            PluginMessages.consoleSend(String.format("&6>> &e%s", e));
        }
        enabled = false;
    }

    public static void execute(@NotNull String sql) {
        if (!enabled) throw new IllegalStateException("Database is not enabled!");

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeWithResult(@NotNull String sql) {
        if (!enabled) throw new IllegalStateException("Database is not enabled!");

        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void statementClose(@NotNull ResultSet resultSet) {
        try { resultSet.getStatement().close(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    // Methods for ping mute system
    public static void mutePing(@NotNull Player mutingPlayer, @NotNull Player mutedPlayer) {
        UUID mutingUUID = mutingPlayer.getUniqueId();
        UUID mutedUUID = mutedPlayer.getUniqueId();

        execute(String.format("INSERT INTO `ping_mutes` (muting_player_uuid, muted_player_uuid) VALUES ('%s', '%s')",
                mutingUUID.toString(), mutedUUID.toString()));

    }

    public static void unmutePing(@NotNull Player mutingPlayer, @NotNull Player mutedPlayer) {
        UUID mutingUUID = mutingPlayer.getUniqueId();
        UUID mutedUUID = mutedPlayer.getUniqueId();

        execute(String.format("DELETE FROM `ping_mutes` WHERE muting_player_uuid = '%s' AND muted_player_uuid = '%s'",
                mutingUUID.toString(), mutedUUID.toString()));


    }

    public static boolean isPingMuted(@NotNull Player mutingPlayer, @NotNull Player mutedPlayer) {
        UUID mutingUUID = mutingPlayer.getUniqueId();
        UUID mutedUUID = mutedPlayer.getUniqueId();

        ResultSet result = executeWithResult(String.format(
                "SELECT * FROM `ping_mutes` WHERE muting_player_uuid = '%s' AND muted_player_uuid = '%s'",
                mutingUUID.toString(), mutedUUID.toString()));

        try {
            return result.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            statementClose(result);
        }
    }

    private static void checkTables() {
        execute("CREATE TABLE IF NOT EXISTS `ping_mutes` (" +
                "`muting_player_uuid` VARCHAR(36) NOT NULL," +
                "`muted_player_uuid` VARCHAR(36) NOT NULL," +
                "PRIMARY KEY (`muting_player_uuid`, `muted_player_uuid`))");
    }
}
