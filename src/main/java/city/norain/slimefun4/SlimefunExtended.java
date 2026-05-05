package city.norain.slimefun4;

import city.norain.slimefun4.compatibillty.VersionedEvent;
import city.norain.slimefun4.listener.SlimefunMigrateListener;
import city.norain.slimefun4.utils.EnvUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import lombok.Getter;
import org.apache.logging.log4j.core.config.Configurator;

public final class SlimefunExtended {
    private static SlimefunMigrateListener migrateListener = new SlimefunMigrateListener();

    @Getter
    private static boolean databaseDebugMode = false;

    private static void checkDebug() {
        if ("true".equals(System.getProperty("slimefun.database.debug"))) {
            databaseDebugMode = true;

            Slimefun.getSQLProfiler().start();
            Slimefun.logger().log(Level.INFO, "Database debug mode enabled");
        } else {
            Configurator.setLevel(HikariConfig.class.getName(), org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel(HikariDataSource.class.getName(), org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel(HikariPool.class.getName(), org.apache.logging.log4j.Level.OFF);
        }
    }

    public static boolean checkEnvironment(@Nonnull Slimefun sf) {
        if (EnvironmentChecker.checkHybridServer()) {
            sf.getLogger().log(Level.WARNING, "#######################################################");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "Hybrid server detected, Slimefun will be disabled!");
            sf.getLogger().log(Level.WARNING, "Hybrid servers have been reported to have issues by multiple users.");
            sf.getLogger().log(Level.WARNING, "Forced environment check bypass will not receive support.");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "#######################################################");
            return false;
        }

        if (Slimefun.getConfigManager().isBypassEnvironmentCheck()) {
            sf.getLogger().log(Level.WARNING, "#######################################################");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "Environment compatibility check disabled detected!");
            sf.getLogger().log(Level.WARNING, "Failing compatibility checks will not receive support.");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "#######################################################");
            return true;
        } else {
            return !EnvironmentChecker.checkIncompatiblePlugins(sf.getLogger());
        }
    }

    public static void init(@Nonnull Slimefun sf) {
        EnvironmentChecker.scheduleSlimeGlueCheck(sf);

        EnvUtil.init();

        checkDebug();

        VaultIntegration.register(sf);

        migrateListener.register(sf);

        VersionedEvent.init();
    }

    public static void shutdown() {
        migrateListener = null;

        VaultIntegration.cleanup();

        databaseDebugMode = false;
    }
}
