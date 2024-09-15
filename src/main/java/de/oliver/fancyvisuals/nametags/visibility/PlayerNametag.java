package de.oliver.fancyvisuals.nametags.visibility;

import de.oliver.fancysitula.api.entities.FS_Display;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.entities.FS_TextDisplay;
import de.oliver.fancysitula.factories.FancySitula;
import de.oliver.fancyvisuals.api.nametags.Nametag;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerNametag {

    private final Nametag nametag;
    private final Player player;
    private final Set<UUID> viewers;
    private FS_TextDisplay fsTextDisplay;

    public PlayerNametag(Nametag nametag, Player player) {
        this.nametag = nametag;
        this.player = player;
        this.viewers = new HashSet<>();
        this.fsTextDisplay = new FS_TextDisplay();
    }

    public void showTo(Player viewer) {
        viewers.add(viewer.getUniqueId());

        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsViewer, fsTextDisplay);
        updateFor(viewer);
        letDisplayRidePlayer(viewer);
    }

    public void hideFrom(Player viewer) {
        viewers.remove(viewer.getUniqueId());

        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsViewer, fsTextDisplay);
    }

    public void updateFor(Player viewer) {
        fsTextDisplay.setBillboard(FS_Display.Billboard.CENTER);
        fsTextDisplay.setBackground(0x0);

        fsTextDisplay.setStyleFlags((byte) 0);
        fsTextDisplay.setShadow(true);

        StringBuilder text = new StringBuilder();
        for (String line : nametag.lines()) {
            text.append(line).append('\n');
        }

        text.deleteCharAt(text.length() - 1);
        fsTextDisplay.setText(ModernChatColorHandler.translate(text.toString(), player));

        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);
        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsViewer, fsTextDisplay);
    }

    public void letDisplayRidePlayer(Player viewer) {
        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);

        FancySitula.PACKET_FACTORY.createSetPassengersPacket(
                viewer.getEntityId(),
                List.of(fsTextDisplay.getId())
        ).send(fsViewer);
    }

    public boolean isVisibleTo(Player viewer) {
        return viewers.contains(viewer.getUniqueId());
    }

    public Nametag getNametag() {
        return nametag;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<UUID> getViewers() {
        return viewers;
    }
}
