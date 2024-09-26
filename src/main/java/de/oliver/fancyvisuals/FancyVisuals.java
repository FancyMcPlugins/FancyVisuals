package de.oliver.fancyvisuals;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancysitula.api.IFancySitula;
import de.oliver.fancyvisuals.analytics.AnalyticsManager;
import de.oliver.fancyvisuals.api.FancyVisualsAPI;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.config.FancyVisualsConfig;
import de.oliver.fancyvisuals.config.NametagConfig;
import de.oliver.fancyvisuals.nametags.listeners.NametagListeners;
import de.oliver.fancyvisuals.nametags.store.JsonNametagRepository;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametagScheduler;
import de.oliver.fancyvisuals.playerConfig.JsonPlayerConfigStore;
import de.oliver.fancyvisuals.utils.VaultHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FancyVisuals extends JavaPlugin implements FancyVisualsAPI {

    private static final ExtendedFancyLogger logger = IFancySitula.LOGGER;
    private static FancyVisuals instance;
    private final AnalyticsManager analyticsManager;
    private final FancyVisualsConfig fancyVisualsConfig;
    private final NametagConfig nametagConfig;
    private ExecutorService workerExecutor;

    private JsonPlayerConfigStore playerConfigStore;

    private NametagRepository nametagRepository;
    private PlayerNametagScheduler nametagScheduler;

    public FancyVisuals() {
        instance = this;
        this.analyticsManager = new AnalyticsManager("oliver", "fancyvisuals", "123456");
        this.fancyVisualsConfig = new FancyVisualsConfig();
        this.nametagConfig = new NametagConfig();
    }

    public static FancyVisuals get() {
        return instance;
    }

    public static @NotNull ExtendedFancyLogger getFancyLogger() {
        return logger;
    }

    @Override
    public void onLoad() {
        IFancySitula.LOGGER.setCurrentLevel(LogLevel.DEBUG);

        analyticsManager.init();

        fancyVisualsConfig.load();
        nametagConfig.load();

        this.workerExecutor = Executors.newFixedThreadPool(
                fancyVisualsConfig.getAmountWorkerThreads(),
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyVisualsWorker-%d")
                        .build()
        );


        // Player config
        playerConfigStore = new JsonPlayerConfigStore();

        // Nametags
        nametagRepository = new JsonNametagRepository();
        nametagScheduler = new PlayerNametagScheduler(workerExecutor, nametagConfig.getDistributionBucketSize());
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        // Vault
        VaultHelper.loadVault();

        // Nametags
        nametagScheduler.init();
        pluginManager.registerEvents(new NametagListeners(), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }

    public JsonPlayerConfigStore getPlayerConfigStore() {
        return playerConfigStore;
    }

    @Override
    public NametagRepository getNametagRepository() {
        return nametagRepository;
    }

    public PlayerNametagScheduler getNametagScheduler() {
        return nametagScheduler;
    }
}
