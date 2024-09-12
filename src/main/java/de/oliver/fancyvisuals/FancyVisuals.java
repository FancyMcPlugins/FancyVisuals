package de.oliver.fancyvisuals;

import de.oliver.fancyvisuals.api.FancyVisualsAPI;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.config.NametagConfig;
import de.oliver.fancyvisuals.nametags.fake.FakeNametagRepository;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametagScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public final class FancyVisuals extends JavaPlugin implements FancyVisualsAPI {

    private static FancyVisuals instance;

    private NametagConfig nametagConfig;
    private NametagRepository nametagRepository;

    public FancyVisuals() {
        instance = this;
    }

    public static FancyVisuals get() {
        return instance;
    }

    @Override
    public void onLoad() {
        nametagConfig = new NametagConfig();
        nametagConfig.load();
        nametagRepository = new FakeNametagRepository();
    }

    @Override
    public void onEnable() {
        PlayerNametagScheduler playerNametagScheduler = new PlayerNametagScheduler(nametagConfig.getAmountWorkerThreads(), nametagConfig.getDistributionBucketSize());
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
