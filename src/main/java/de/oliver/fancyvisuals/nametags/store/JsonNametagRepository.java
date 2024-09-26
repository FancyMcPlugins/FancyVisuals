package de.oliver.fancyvisuals.nametags.store;

import de.oliver.fancylib.jdb.JDB;
import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.api.nametags.NametagStore;
import de.oliver.fancyvisuals.utils.VaultHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JsonNametagRepository implements NametagRepository {

    private static final String BASE_PATH = "plugins/FancyVisuals/data/nametags/";
    private final JDB jdb;
    private final Map<Context, NametagStore> stores;

    public JsonNametagRepository() {
        this.jdb = new JDB(BASE_PATH);
        stores = new ConcurrentHashMap<>();

        for (Context ctx : Context.values()) {
            stores.put(ctx, new JsonNametagStore(jdb, ctx));
        }

        initialConfig();
    }

    @Override
    public @NotNull NametagStore getStore(@NotNull Context context) {
        return stores.get(context);
    }

    @Override
    @NotNull
    public Nametag getNametagForPlayer(@NotNull Player player) {
        Nametag nametag = getNametag(Context.PLAYER, player.getUniqueId().toString());
        if (nametag != null) {
            return nametag;
        }

        if (VaultHelper.isVaultLoaded()) {
            nametag = getNametag(Context.GROUP, VaultHelper.getPermission().getPrimaryGroup(player));
            if (nametag != null) {
                return nametag;
            }
        }

        nametag = getNametag(Context.WORLD, player.getWorld().getName());
        if (nametag != null) {
            return nametag;
        }

        nametag = getNametag(Context.SERVER, "global");
        if (nametag != null) {
            return nametag;
        }

        return DEFAULT_NAMETAG;
    }

    private void initialConfig() {
        File baseDir = new File(BASE_PATH);
        if (baseDir.exists()) {
            return;
        }

        NametagStore serverStore = getStore(Context.SERVER);
        serverStore.setNametag("global", DEFAULT_NAMETAG);

        NametagStore worldStore = getStore(Context.WORLD);
        worldStore.setNametag("world", new Nametag(
                List.of("Overworld", "%player%"),
                "#C800AA00",
                true,
                Nametag.TextAlignment.CENTER
        ));
        worldStore.setNametag("world_nether", new Nametag(
                List.of("Nether", "%player%"),
                "#C8AA0000",
                true,
                Nametag.TextAlignment.CENTER
        ));
        worldStore.setNametag("world_the_end", new Nametag(
                List.of("The End", "%player%"),
                "#C80000AA",
                true,
                Nametag.TextAlignment.CENTER
        ));

        NametagStore groupStore = getStore(Context.GROUP);
        groupStore.setNametag("admin", new Nametag(
                List.of("Admin", "%player%"),
                "#C8FF0000",
                true,
                Nametag.TextAlignment.CENTER
        ));
        groupStore.setNametag("moderator", new Nametag(
                List.of("Mod", "%player%"),
                "#C8FFAA00",
                true,
                Nametag.TextAlignment.CENTER
        ));

        NametagStore playerStore = getStore(Context.PLAYER);
        playerStore.setNametag(UUID.randomUUID().toString(), new Nametag(
                List.of("Player", "%player%"),
                "#C800FF00",
                true,
                Nametag.TextAlignment.CENTER
        ));
    }
}
