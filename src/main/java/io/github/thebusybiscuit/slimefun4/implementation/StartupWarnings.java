package io.github.thebusybiscuit.slimefun4.implementation;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class stores some startup warnings we occasionally need to print.
 * If you set up your server the recommended way, you will never see
 * any of these messages.
 *
 * @author TheBusyBiscuit
 */
final class StartupWarnings {

    private static final String BORDER = "****************************************************";
    private static final String PREFIX = "* ";

    private StartupWarnings() {}

    @ParametersAreNonnullByDefault
    static void discourageCSCoreLib(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "It appears you are still using CS-CoreLib.");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "Slimefun has not required CS-CoreLib");
        logger.log(Level.SEVERE, PREFIX + "since January 30, 2021.");
        logger.log(Level.SEVERE, PREFIX + "You must remove CS-CoreLib");
        logger.log(Level.SEVERE, PREFIX + "for Slimefun to function properly.");
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidMinecraftVersion(Logger logger, String detectedVer, String slimefunVersion) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun was not installed correctly!");
        logger.log(Level.SEVERE, PREFIX + "You are using an unsupported Minecraft version!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "You are running Minecraft {0}", detectedVer);
        logger.log(Level.SEVERE, PREFIX + "but Slimefun {0} only supports", slimefunVersion);
        logger.log(Level.SEVERE, PREFIX + "Minecraft {0}", String.join(" / ", Slimefun.getSupportedVersions()));
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidServerSoftware(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun was not installed correctly!");
        logger.log(Level.SEVERE, PREFIX + "CraftBukkit is no longer supported!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "Slimefun requires you to use Spigot, Paper,");
        logger.log(Level.SEVERE, PREFIX + "or any Spigot/Paper fork.");
        logger.log(Level.SEVERE, PREFIX + "(We recommend Paper)");
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void oldJavaVersion(Logger logger, int recommendedJavaVersion) {
        int javaVersion = NumberUtils.getJavaVersion();

        logger.log(Level.WARNING, BORDER);
        logger.log(Level.WARNING, PREFIX + "The Java version in use (Java {0}) is outdated.", javaVersion);
        logger.log(Level.WARNING, PREFIX);
        logger.log(
                Level.WARNING, PREFIX + "Due to newer Minecraft versions requiring Java {0},", recommendedJavaVersion);
        logger.log(
                Level.WARNING,
                PREFIX + "we strongly recommend upgrading to Java {0} as soon as possible.",
                recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "To take advantage of features in newer Java versions,");
        logger.log(
                Level.WARNING,
                PREFIX + "Slimefun will also require Java {0} in the near future.",
                recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "To avoid issues later, please update soon!");
        logger.log(Level.WARNING, BORDER);
    }
}
