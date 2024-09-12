package de.oliver.fancyvisuals;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyvisuals.api.FancyVisualsAPI;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.config.FancyVisualsConfig;
import de.oliver.fancyvisuals.config.NametagConfig;
import de.oliver.fancyvisuals.nametags.fake.FakeNametagRepository;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametagScheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FancyVisuals extends JavaPlugin implements FancyVisualsAPI {

    private static FancyVisuals instance;
    private final FancyVisualsConfig fancyVisualsConfig;
    private final NametagConfig nametagConfig;
    private ExecutorService workerExecutor;
    private NametagRepository nametagRepository;

    public FancyVisuals() {
        instance = this;
        this.fancyVisualsConfig = new FancyVisualsConfig();
        this.nametagConfig = new NametagConfig();
    }

    public static FancyVisuals get() {
        return instance;
    }

    @Override
    public void onLoad() {
        fancyVisualsConfig.load();
        nametagConfig.load();

        nametagRepository = new FakeNametagRepository();

        this.workerExecutor = Executors.newFixedThreadPool(
                fancyVisualsConfig.getAmountWorkerThreads(),
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyVisualsWorker-%d")
                        .build()
        );
    }

    @Override
    public void onEnable() {
        PlayerNametagScheduler playerNametagScheduler = new PlayerNametagScheduler(workerExecutor, nametagConfig.getDistributionBucketSize());
        playerNametagScheduler.init();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }

    @Override
    public NametagRepository getNametagRepository() {
        return nametagRepository;
    }
}
