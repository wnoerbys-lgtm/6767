package dev.ratclient.feature.modules;

import dev.ratclient.feature.RatFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Map;

/** Heuristic over loaded blocks only. It does not reveal hidden or unloaded areas. */
public final class BaseFinderFeature extends RatFeature {
    private record Candidate(ChunkPos chunk, int count, int types, int confidence) {}
    private Candidate best;
    private long lastScan;
    public BaseFinderFeature() { super("Base Finder", "Heuristically score structures in already-loaded chunks"); }
    @Override public String settingLabel() { return "Scan loaded chunks · 24 block radius"; }
    @Override public void tick(MinecraftClient client) {
        if (!enabled() || client.player == null || client.world == null || client.world.getTime() - lastScan < 100) return;
        lastScan = client.world.getTime(); Map<Long, int[]> scores = new HashMap<>();
        BlockPos origin = client.player.getBlockPos(); ChunkPos center = new ChunkPos(origin);
        for (int cx = center.x - 2; cx <= center.x + 2; cx++) for (int cz = center.z - 2; cz <= center.z + 2; cz++) {
            ChunkPos chunk = new ChunkPos(cx, cz); if (!client.world.isChunkLoaded(cx, cz)) continue;
            for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = origin.getY() - 8; y <= origin.getY() + 8; y++) {
                BlockPos pos = new BlockPos(cx * 16 + x, y, cz * 16 + z);
                if (pos.getSquaredDistance(origin) > 24 * 24) continue;
                String id = Registries.BLOCK.getId(client.world.getBlockState(pos).getBlock()).getPath();
                if (!isHumanMadeSignal(id)) continue;
                int[] score = scores.computeIfAbsent(chunk.toLong(), ignored -> new int[2]); score[0]++;
                if (id.contains("chest") || id.contains("crafting") || id.contains("furnace") || id.contains("bed") || id.contains("door") || id.contains("glass")) score[1]++;
            }
        }
        best = null;
        for (var entry : scores.entrySet()) {
            int count = entry.getValue()[0], types = entry.getValue()[1];
            if (count < 4) continue;
            ChunkPos chunk = new ChunkPos(entry.getKey()); int confidence = Math.min(96, 28 + count * 2 + types * 4);
            if (best == null || confidence > best.confidence) best = new Candidate(chunk, count, types, confidence);
        }
    }
    private boolean isHumanMadeSignal(String id) {
        return id.contains("chest") || id.contains("crafting") || id.contains("furnace") || id.contains("bed")
            || id.contains("door") || id.contains("glass") || id.contains("sign") || id.contains("barrel") || id.contains("shulker");
    }
    @Override public void renderHud(DrawContext draw) {
        status(draw, best == null ? "BASE FINDER  ·  no candidates in loaded chunks" :
            "BASE FINDER  ·  chunk " + best.chunk.x + ", " + best.chunk.z + "  ·  confidence " + best.confidence + "%", 98);
    }
}
