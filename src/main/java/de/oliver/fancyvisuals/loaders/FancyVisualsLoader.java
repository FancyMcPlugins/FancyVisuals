package de.oliver.fancyvisuals.loaders;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class FancyVisualsLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("fancyplugins", "default", "https://repo.fancyplugins.de/releases").build());
        resolver.addRepository(new RemoteRepository.Builder("mavencentral", "default", "https://repo1.maven.org/maven2/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver:FancyLib:33"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver:FancySitula:0.0.9"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver.FancyAnalytics:api:0.0.5"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("de.oliver.FancyAnalytics:logger:0.0.5"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-core:2.0.0"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.10"), "compile"));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-annotations:2.0.0"), "compile"));


        classpathBuilder.addLibrary(resolver);
    }

}
