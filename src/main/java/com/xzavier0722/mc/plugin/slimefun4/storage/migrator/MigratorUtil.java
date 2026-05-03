package com.xzavier0722.mc.plugin.slimefun4.storage.migrator;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class MigratorUtil {
    protected static boolean createDirBackup(File dir) {
        try {
            var oldDataDir = new File("data-storage/Slimefun/old_data/");
            oldDataDir.mkdirs();
            var backupPath = Path.of("data-storage/Slimefun/old_data/" + dir.getName() + ".zip");

            if (Files.exists(backupPath, LinkOption.NOFOLLOW_LINKS)) {
                Slimefun.logger().log(Level.WARNING, "Detected existing backup data, skipping backup.");
                return true;
            }

            var zipPath = Files.createFile(backupPath);
            try (var zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                var src = dir.toPath();
                try (var fs = Files.walk(src).filter(path -> !Files.isDirectory(path))) {
                    fs.forEach(path -> {
                        var zipEntry = new ZipEntry(src.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            Slimefun.logger()
                                    .log(
                                            Level.WARNING,
                                            "An issue occurred while backing up old data " + dir.getName() + '.',
                                            e);
                        }
                    });
                }
            }
            return true;
        } catch (Exception e) {
            Slimefun.logger()
                    .log(Level.WARNING, "An issue occurred while backing up old data " + dir.getName() + '.', e);
            return false;
        }
    }

    protected static void deleteOldFolder(File dir) {
        try {
            if (dir.isDirectory() && dir.listFiles() != null) {
                for (File file : dir.listFiles()) {
                    Files.delete(file.toPath());
                }
            }

            Files.delete(dir.toPath());
        } catch (Exception e) {
            Slimefun.logger()
                    .log(Level.WARNING, "An issue occurred while deleting directory " + dir.getAbsolutePath() + '.', e);
        }
    }

    protected static boolean checkMigrateMark() {
        var backupData = new File("data-storage/Slimefun/old_data/");
        return backupData.exists()
                && backupData.isDirectory()
                && backupData.listFiles() != null
                && backupData.listFiles().length > 0;
    }
}
