package net.survivalboom.survivalboomchat.chatheads;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import net.survivalboom.survivalboomchat.SurvivalBoomChat;
import net.survivalboom.survivalboomchat.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ChatHeads {

    private static final SkinsRestorer skinsRestorer = getSkinsRestorer();
    private static ConfigurationSection section = null;
    private static String pixel = null;

    public static void reload() {
        section = SurvivalBoomChat.getPlugin().getConfig().getConfigurationSection("chat-heads");
        if (section == null) pixel = "█";
        pixel = section.getString("pixel-symbol", "█");
    }

    public static String[] createHeadImage(@NotNull Player player, @NotNull String headMsgName) {

        BufferedImage image;
        if (skinsRestorer != null) {
            SkinProperty skin = getSkin(player);
            if (skin == null) image = getCrafatarAvatar(player.getUniqueId().toString());
            else image = getSkinHead(getSkinsRestorerSkin(skin));
        }

        else image = getCrafatarAvatar(player.getUniqueId().toString());

        return addCustomLinesToASCIIHeadImage(generateASCIIImage(image), headMsgName, player);
    }

    public static BufferedImage getSkinHead(@NotNull BufferedImage image) {
        try { return image.getSubimage(8, 8, image.getWidth() / 8, image.getHeight() / 8); }
        catch (Exception e) { return image.getSubimage(8, 8, image.getWidth() / 8, image.getHeight() / 4); }
    }

    public static BufferedImage getCrafatarAvatar(String UUID) {

        try {

            // Отримання картинки голови гравця.
            URL head_image = new URL("https://crafatar.com/avatars/" + UUID);
            HttpURLConnection connection = (HttpURLConnection) head_image.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

            // Отримуємо картинку.
            BufferedImage image = ImageIO.read(connection.getInputStream());

            // Трансформуємо картинку до необхідного розміру.
            AffineTransform transform = new AffineTransform();
            transform.scale(8 / (double) image.getWidth(), 8 / (double) image.getHeight());
            AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            // Повернення картинки.
            return operation.filter(image, null);

        } catch (IOException e) {throw new RuntimeException(e);}

    }

    public static BufferedImage getSkinsRestorerSkin(@NotNull SkinProperty property) {

        String decoded = new String(Base64.getDecoder().decode(property.getValue()));
        JSONObject json = new JSONObject(decoded);

        String urlStr = json.getJSONObject("textures").getJSONObject("SKIN").getString("url");

        try {

            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

            return ImageIO.read(connection.getInputStream());

        }

        catch (Exception e) { throw new RuntimeException(e); }

    }

    private static String[] generateASCIIImage(@NotNull BufferedImage image) {

        ArrayList<String> lines = new ArrayList<>();

        for (int y = 0; y < image.getWidth(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < image.getHeight(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                line.append(String.format("&#%02x%02x%02x%s", color.getRed(), color.getGreen(), color.getBlue(), pixel));
            }
            lines.add(line.toString());
        }

        return lines.toArray(new String[0]);
    }

    private static String[] addCustomLinesToASCIIHeadImage(String[] image_lines, String head_msg_name, Player player) {

        String msg = "chat-heads.messages." + head_msg_name;

        msg = msg.replace(" ", "");

        List<String> user_lines = SurvivalBoomChat.getPlugin().getConfig().getStringList(msg);

        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < image_lines.length; i++) {

            String ы;
            if (i >= user_lines.size()) ы = image_lines[i];
            else ы = image_lines[i] + user_lines.get(i);

            Placeholders placeholders = new Placeholders();
            placeholders.add("{ONLINE}", Bukkit.getOnlinePlayers().size());
            placeholders.add("{MAX-ONLINE}", Bukkit.getMaxPlayers());

            out.add(Placeholders.parseFull(ы, player, placeholders));
        }

        return out.toArray(new String[0]);

    }

    private static SkinsRestorer getSkinsRestorer() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) return null;
        return SkinsRestorerProvider.get();
    }

    private static SkinProperty getSkin(Player player) {
        return skinsRestorer.getPlayerStorage().getSkinOfPlayer(player.getUniqueId()).orElse(null);
    }
}
