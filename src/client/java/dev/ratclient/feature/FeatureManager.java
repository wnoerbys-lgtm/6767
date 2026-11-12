package dev.ratclient.feature;

import dev.ratclient.feature.modules.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.List;

public final class FeatureManager {
    private static final List<RatFeature> FEATURES = List.of(
        new BoatFlyFeature(), new SchematicFeature(), new AreaMinerFeature(), new FreecamFeature(), new BaseFinderFeature());
    private FeatureManager() {}
    public static void init() { ConfigStore.load(FEATURES); }
    public static List<RatFeature> all() { return FEATURES; }
    public static RatFeature byName(String name) { return FEATURES.stream().filter(f -> f.name().equals(name)).findFirst().orElse(null); }
    public static void toggle(String name) {
        RatFeature feature = byName(name);
        if (feature != null) { feature.setEnabled(!feature.enabled()); ConfigStore.save(FEATURES); }
    }
    public static void tick(MinecraftClient client) { for (RatFeature feature : FEATURES) feature.tick(client); }
    public static void renderHud(DrawContext draw) { for (RatFeature feature : FEATURES) if (feature.enabled()) feature.renderHud(draw); }
    public static int loadSchematic(MinecraftClient client, String file) {
        if (client.player == null) return 0;
        RatFeature feature = byName("Schematic Builder");
        if (feature instanceof SchematicFeature schematic) return schematic.load(client, file);
        return 0;
    }
}
