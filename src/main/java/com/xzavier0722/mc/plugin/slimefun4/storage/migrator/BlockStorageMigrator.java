package com.xzavier0722.mc.plugin.slimefun4.storage.migrator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockStorageMigrator implements IMigrator {
    @Getter
    private static final BlockStorageMigrator instance = new BlockStorageMigrator();

    private static final File invFolder = new File("data-storage/Slimefun/stored-inventories/");
    private static final File chunk = new File("data-storage/Slimefun/stored-chunks/chunks.sfc");
    private static final File blockFolder = new File("data-storage/Slimefun/stored-blocks/");
    private static final Gson gson = new Gson();
    private static volatile boolean migrateLock = false;

    private BlockStorageMigrator() {}

    @Override
    public String getName() {
        return "BlockStorage";
    }

    @Override
    public boolean hasOldData() {
        return !MigratorUtil.checkMigrateMark() && (hasBlockData() || chunk.exists());
    }

    @Override
    public MigrateStatus migrateData() {
        Slimefun.getTickerTask().setPaused(true);

        var controller = Slimefun.getDatabaseManager().getBlockDataController();
        var isDelayedSavingEnabled = controller.isDelayedSavingEnabled();
        if (isDelayedSavingEnabled) {
            controller.setDelayedSavingEnable(false);
        }

        if (migrateLock) {
            return MigrateStatus.MIGRATING;
        }

        var status = MigrateStatus.SUCCESS;
        migrateLock = true;

        if (chunk.isFile()) {
            migrateChunks();

            try {
                var chunkBak = Files.createFile(Path.of("data-storage/Slimefun/old_data/chunks.sfc"));
                Files.copy(chunk.toPath(), chunkBak, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(chunk.toPath());
            } catch (Exception e) {
                Slimefun.logger()
                        .log(Level.WARNING, "An issue occurred while backing up old data " + chunk.getName() + '.', e);
                status = MigrateStatus.FAILED;
            }
        } else {
            Slimefun.logger().log(Level.WARNING, "No chunk data detected; skipping migration.");
        }

        Bukkit.getWorlds().forEach(this::migrateWorld);

        if (MigratorUtil.createDirBackup(invFolder)) {
            MigratorUtil.deleteOldFolder(invFolder);
        }

        if (MigratorUtil.createDirBackup(blockFolder)) {
            MigratorUtil.deleteOldFolder(blockFolder);
        }

        migrateLock = false;

        if (isDelayedSavingEnabled) {
            controller.setDelayedSavingEnable(true);
        }
        Slimefun.getTickerTask().setPaused(false);
        return status;
    }

    private boolean hasBlockData() {
        for (var world : Bukkit.getWorlds()) {
            var f = new File(blockFolder, world.getName());
            var fList = f.listFiles();
            if (fList != null && fList.length > 0) {
                return true;
            }
        }
        return false;
    }

    private void migrateWorld(World w) {
        Slimefun.logger().log(Level.INFO, "Starting block data migration: " + w.getName());
        var fList = new File(blockFolder, w.getName()).listFiles();
        if (fList == null) {
            return;
        }

        var count = 0;
        var total = fList.length;
        for (var f : fList) {
            var id = f.getName();
            id = id.substring(0, id.length() - 4);
            Slimefun.logger().log(Level.INFO, "Migrating block data: " + id + " (" + ++count + "/" + total + ")");

            if (SlimefunItem.getById(id) == null) {
                Slimefun.logger().log(Level.WARNING, "Detected unknown block ID (" + id + "); skipping migration.");
                continue;
            }

            var cfg = new Config(f);
            for (var key : cfg.getKeys()) {
                migrateBlock(w, id, key, cfg.getString(key));
            }
        }
    }

    private void migrateBlock(World world, String sfId, String locStr, String jsonStr) {
        try {
            var arr = locStr.split(";");
            var x = Integer.parseInt(arr[1]);
            var y = Integer.parseInt(arr[2]);
            var z = Integer.parseInt(arr[3]);

            var loc = new Location(world, x, y, z);
            var sfData = Slimefun.getDatabaseManager().getBlockDataController().createBlock(loc, sfId);
            Map<String, String> data = gson.fromJson(jsonStr, new TypeToken<Map<String, String>>() {}.getType());
            for (var each : data.entrySet()) {
                var key = each.getKey();
                if ("id".equals(key)) {
                    continue;
                }
                sfData.setData(key, each.getValue());
            }

            DirtyChestMenu menu = sfData.getBlockMenu();

            if (menu != null) {
                var f = new File(invFolder, world.getName() + ";" + x + ";" + y + ";" + z + ".sfi");
                if (!f.isFile()) {
                    return;
                }
                migrateInv(menu, f);
            }
        } catch (Throwable e) {
            Slimefun.logger().log(Level.SEVERE, "An error occurred while migrating block data: " + locStr, e);
        }
    }

    private void migrateInv(DirtyChestMenu menu, File f) {
        var cfg = new Config(f);
        var preset = menu.getPreset().getPresetSlots();
        for (var key : cfg.getKeys()) {
            if ("preset".equals(key)) {
                continue;
            }

            int slot;
            try {
                slot = Integer.parseInt(key);
            } catch (Throwable e) {
                continue;
            }

            if (preset.contains(slot)) {
                continue;
            }

            var item = cfg.getItem(key);
            if (item == null) {
                continue;
            }

            menu.replaceExistingItem(slot, item);
        }
    }

    private void migrateChunks() {
        var cfg = new Config(chunk);
        var keys = cfg.getKeys();
        var total = keys.size();
        var count = 0;
        for (var key : keys) {
            Slimefun.logger().log(Level.INFO, "Migrating chunk data: " + ++count + "/" + total);
            var arr = key.split(";");
            try {
                var w = Bukkit.getWorld(arr[0]);
                if (w == null) {
                    Slimefun.logger().log(Level.WARNING, "Chunk world not loaded, skipping: " + arr[0]);
                    continue;
                }

                var c = w.getChunkAt(Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
                Map<String, String> data =
                        gson.fromJson(cfg.getString(key), new TypeToken<Map<String, String>>() {}.getType());
                var chunkData =
                        Slimefun.getDatabaseManager().getBlockDataController().getChunkData(c);
                data.entrySet().forEach(each -> chunkData.setData(each.getKey(), each.getValue()));
            } catch (Throwable e) {
                Slimefun.logger().log(Level.SEVERE, "An error occurred while migrating chunk data: " + key, e);
            }
        }
    }
}
