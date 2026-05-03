package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import city.norain.slimefun4.utils.StringUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.StorageType;
import io.github.thebusybiscuit.slimefun4.core.debug.Debug;
import io.github.thebusybiscuit.slimefun4.core.debug.TestCase;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DataUtils {
    /**
     * Serializes an {@link ItemStack} using {@link BukkitObjectOutputStream}
     * into a Base64 string for database storage.
     *
     * @param itemStack the {@link ItemStack} to serialize
     * @return the serialized Base64 string
     */
    public static String serializeItemStack(ItemStack itemStack) {
        Debug.log(TestCase.BACKPACK, "Serializing itemstack: " + itemStack);

        if (itemStack == null) {
            return "";
        }

        try (var stream = new ByteArrayOutputStream();
                var bs = new BukkitObjectOutputStream(stream)) {
            bs.writeObject(itemStack);
            var itemStr = Base64.getEncoder().encodeToString(stream.toByteArray());

            if (!Slimefun.getConfigManager().isBypassItemLengthCheck()
                    && Slimefun.getDatabaseManager().getBlockDataStorageType() == StorageType.MYSQL
                    && itemStr.length() > 65535) {
                throw new IllegalArgumentException(
                        "Detected an oversized item. Please contact the plugin developer responsible for that item: "
                                + StringUtil.itemStackToString(itemStack) + ", size = " + itemStr.length());
            }

            return itemStr;
        } catch (Throwable e) {
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            "An error occurred during serialisation of the item; an empty value will be stored.",
                            e);
            return "";
        }
    }

    /**
     * 使用 {@link BukkitObjectInputStream} 反序列化 Base64 字符串
     * 为 {@link ItemStack} 对象.
     *
     * @param base64Str 要反序列化的 Base64 字符串
     * @return 反序列化后的 {@link ItemStack} 对象
     */
    @Nullable public static ItemStack deserializeItemStack(String base64Str) {
        if (base64Str == null || base64Str.isEmpty() || base64Str.isBlank()) {
            return null;
        }

        Debug.log(TestCase.BACKPACK, "Deserializing itemstack: " + base64Str);

        try (var stream = new ByteArrayInputStream(Base64.getMimeDecoder().decode(base64Str));
                var bs = new BukkitObjectInputStream(stream)) {
            var result = (ItemStack) bs.readObject();

            Debug.log(TestCase.BACKPACK, "Deserialized itemstack: " + result);

            if (result.getType().isAir()) {
                Slimefun.logger()
                        .log(
                                Level.SEVERE,
                                "Failed to deserialize item from the database! The corresponding item cannot be displayed.");
            }

            return result;
        } catch (Exception ex) {
            throw new RuntimeException(
                    "An error occurred during deserialisation of the item; the corresponding item cannot be displayed.",
                    ex);
        }
    }

    public static String blockDataBase64(String text) {
        return Slimefun.getDatabaseManager().isBlockDataBase64Enabled() ? base64Encode(text) : text;
    }

    public static String blockDataDebase64(String base64Str) {
        return Slimefun.getDatabaseManager().isBlockDataBase64Enabled() ? base64Decode(base64Str) : base64Str;
    }

    public static String profileDataBase64(String text) {
        return Slimefun.getDatabaseManager().isProfileDataBase64Enabled() ? base64Encode(text) : text;
    }

    public static String profileDataDebase64(String base64Str) {
        return Slimefun.getDatabaseManager().isProfileDataBase64Enabled() ? base64Decode(base64Str) : base64Str;
    }

    public static String base64Encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String base64Str) {
        return new String(Base64.getDecoder().decode(base64Str), StandardCharsets.UTF_8);
    }
}
