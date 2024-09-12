package de.oliver.fancyvisuals.nametags.fake;

import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.api.nametags.NametagStore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeNametagRepository implements NametagRepository {

    private final Map<Context, NametagStore> stores;

    public FakeNametagRepository() {
        this.stores = new ConcurrentHashMap<>();

        for (Context ctx : Context.values()) {
            stores.put(ctx, new FakeNametagStore());
        }
    }

    @Override
    public @NotNull NametagStore getStore(@NotNull Context context) {
        return stores.get(context);
    }
}
