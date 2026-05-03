package io.github.thebusybiscuit.slimefun4.utils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class CustomUtil {
    private CustomUtil() {}

    public static PlayerProfile profileFromBase64(String base64) {
        String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        String skinUrl = extractSkinUrl(json);

        Server server = Bukkit.getServer();
        UUID profileUuid = UUID.nameUUIDFromBytes(base64.getBytes(StandardCharsets.UTF_8));
        PlayerProfile profile = server.createPlayerProfile(profileUuid);

        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(skinUrl != null ? new URL(skinUrl) : null);
        } catch (Exception ex) {
            // empty
        }
        profile.setTextures(textures);

        return profile;
    }

    private static String extractSkinUrl(String json) {
        int i = json.indexOf("\"url\":\"");
        if (i < 0) return null;
        int start = i + 7;
        int end = json.indexOf('"', start);
        if (end < 0) return null;
        String url = json.substring(start, end);

        return url.contains("textures.minecraft.net/texture/") ? url : null;
    }
}
