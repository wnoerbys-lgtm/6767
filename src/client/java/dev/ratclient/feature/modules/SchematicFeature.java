package dev.ratclient.feature.modules;

import com.google.gson.*;
import dev.ratclient.feature.RatFeature;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Reads a small JSON placement list and provides a non-destructive guide. */
public final class SchematicFeature extends RatFeature {
    private record Entry(int x, int y, int z, Block block) {}
    private final List<Entry> entries = new ArrayList<>();
    private String loadedName = "none";
    private int missing;
    private int checked;
    private BlockPos firstMissing;
    private String firstMissingBlock = "";
    private BlockPos anchor;
    private long lastScan;
    public SchematicFeature() { super("Schematic Builder", "Compare a JSON schematic with blocks around you"); }
    @Override public String settingLabel() { return "Loaded: " + loadedName + " · /rat schematic load <file>"; }
    public int load(MinecraftClient client, String file) {
        if (file.contains("/") || file.contains("\\") || file.contains("..") || !file.endsWith(".json")) {
            client.player.sendMessage(net.minecraft.text.Text.literal("Use a JSON filename inside config/ratclient/schematics."), false); return 0;
        }
        Path folder = FabricLoader.getInstance().getConfigDir().resolve("ratclient").resolve("schematics");
        try {
            Files.createDirectories(folder);
            JsonObject root = JsonParser.parseString(Files.readString(folder.resolve(file))).getAsJsonObject();
            JsonArray blocks = root.getAsJsonArray("blocks");
            if (blocks == null || blocks.size() > 10000) throw new IllegalArgumentException("Missing blocks list or over 10,000 entries");
            List<Entry> parsed = new ArrayList<>();
            for (JsonElement element : blocks) {
                JsonObject row = element.getAsJsonObject(); JsonArray pos = row.getAsJsonArray("pos");
                Identifier id = Identifier.tryParse(row.get("block").getAsString());
                if (pos == null || pos.size() != 3 || id == null) continue;
                int x = pos.get(0).getAsInt(), y = pos.get(1).getAsInt(), z = pos.get(2).getAsInt();
                if (x < -128 || x > 128 || y < -128 || y > 128 || z < -128 || z > 128) continue;
                Block block = Registries.BLOCK.get(id);
                if (block == null) continue;
                parsed.add(new Entry(x, y, z, block));
            }
            entries.clear(); entries.addAll(parsed); loadedName = file;
            anchor = client.player.getBlockPos().up();
            client.player.sendMessage(net.minecraft.text.Text.literal("Loaded " + entries.size() + " schematic blocks: " + file), false);
            return 1;
        } catch (Exception exception) {
            client.player.sendMessage(net.minecraft.text.Text.literal("Could not load schematic: " + exception.getMessage()), false); return 0;
        }
    }
    @Override public void tick(MinecraftClient client) {
        if (!enabled() || entries.isEmpty() || client.player == null || client.world == null || client.world.getTime() - lastScan < 10) return;
        lastScan = client.world.getTime(); missing = 0; checked = 0; firstMissing = null; firstMissingBlock = "";
        if (anchor == null) anchor = client.player.getBlockPos().up();
        for (Entry entry : entries) {
            BlockPos target = anchor.add(entry.x, entry.y, entry.z);
            if (!client.world.isChunkLoaded(target.getX() >> 4, target.getZ() >> 4)) continue;
            checked++;
            if (!client.world.getBlockState(target).isOf(entry.block)) {
            missing++;
                if (firstMissing == null) { firstMissing = target; firstMissingBlock = Registries.BLOCK.getId(entry.block).toString(); }
            }
        }
    }
    @Override public void renderHud(DrawContext draw) {
        int progress = checked == 0 ? 0 : (checked - missing) * 100 / checked;
        status(draw, "SCHEMATIC  ·  " + loadedName + "  ·  " + progress + "%  ·  " + missing + " missing / " + checked + " checked", 26);
        if (firstMissing != null) status(draw, "NEXT  ·  " + firstMissingBlock + " at " + firstMissing.toShortString(), 44);
    }
}
