package de.oliver.fancyvisuals.analytics;

import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.MetricSupplier;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;

import java.util.logging.Logger;

public class AnalyticsManager {

    private final FancyAnalyticsAPI fa;

    public AnalyticsManager(String userId, String projectId, String apiKey) {
        this.fa = new FancyAnalyticsAPI(userId, projectId, apiKey);
    }

    public void init() {
        fa.registerDefaultPluginMetrics(FancyVisuals.get());
        fa.registerLogger(FancyVisuals.get().getLogger());
        fa.registerLogger(Logger.getGlobal());

        registerNametagMetrics();
    }

    private void registerNametagMetrics() {
        NametagRepository repo = FancyVisuals.get().getNametagRepository();

        fa.registerNumberMetric(new MetricSupplier<>("nametag_count", () -> {
            double count = 0;
            for (Context ctx : Context.values()) {
                count += repo.getStore(ctx).getNametags().size();
            }
            return count;
        }));
    }
}
