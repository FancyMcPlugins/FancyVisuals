package de.oliver.fancyvisuals.api.nametags;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Nametag(
        @NotNull List<String> lines
) {

}
