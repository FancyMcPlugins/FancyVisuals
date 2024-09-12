package de.oliver.fancyvisuals;

import de.oliver.fancyvisuals.api.FancyVisualsAPI;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.nametags.fake.FakeNametagRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class FancyVisuals extends JavaPlugin implements FancyVisualsAPI {

    private static FancyVisuals instance;

    private NametagRepository nametagRepository;

    public FancyVisuals() {
        instance = this;
    }

    public static FancyVisuals get() {
        return instance;
    }

    @Override
    public void onLoad() {
        nametagRepository = new FakeNametagRepository();
    }

    @Override
    public void onEnable() {

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
