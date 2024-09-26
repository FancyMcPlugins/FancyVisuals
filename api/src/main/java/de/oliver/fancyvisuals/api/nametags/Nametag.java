package de.oliver.fancyvisuals.api.nametags;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Nametag(
        @NotNull List<String> lines,
        @NotNull String backgroundColor,
        @NotNull Boolean textShadow,
        @NotNull TextAlignment textAlignment
) {

    public enum TextAlignment {
        LEFT,
        RIGHT,
        CENTER
    }

}
