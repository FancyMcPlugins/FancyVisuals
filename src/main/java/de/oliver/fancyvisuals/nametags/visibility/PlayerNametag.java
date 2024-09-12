package de.oliver.fancyvisuals.nametags.visibility;

import de.oliver.fancyvisuals.api.nametags.Nametag;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public record PlayerNametag(
        Nametag nametag,
        Player player,
        Set<UUID> viewers
) {

    public void showTo(Player viewer) {
        viewers.add(viewer.getUniqueId());
        //TODO: send packets to show nametag
    }

    public void hideFrom(Player viewer) {
        viewers.remove(viewer.getUniqueId());
        //TODO: send packets to hide nametag
    }

    public void updateFor(Player viewer) {
        //TODO: send packets to update nametag (text, entity data, ...)
    }

    public boolean isVisibleTo(Player viewer) {
        return viewers.contains(viewer.getUniqueId());
    }
}
