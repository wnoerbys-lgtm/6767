package dev.ratclient.feature.modules;

import dev.ratclient.feature.RatFeature;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import java.util.List;

/** A scan and highlight aid only; it never sends break actions. */
public final class AreaMinerFeature extends RatFeature {
    private static final List<Block> TARGETS = List.of(Blocks.STONE, Blocks.DEEPSLATE, Blocks.NETHER_QUARTZ_ORE, Blocks.IRON_ORE, Blocks.DIAMOND_ORE);
    private int index, matches, radius = 6;
    private BlockPos nearestMatch;
    private long lastScan;
    public AreaMinerFeature() { super("Area Miner", "Preview nearby blocks that match a selected type"); }
    @Override public String settingLabel() { return "Target " + TARGETS.get(index).getName().getString() + " · radius " + radius + " (− target / + range)"; }
    @Override public int settingValue() { return index; }
    @Override public void loadSetting(int value) { index = Math.floorMod(value, TARGETS.size()); }
    @Override public void adjust(int direction) { loadSetting(index + direction); }
    @Override public int secondarySettingValue() { return radius; }
    @Override public void loadSecondarySetting(int value) { radius = Math.max(3, Math.min(8, value)); }
    @Override public void adjustSecondary(int direction) { loadSecondarySetting(radius + direction); }
    @Override public void tick(MinecraftClient client) {
        if (!enabled() || client.player == null || client.world == null || client.world.getTime() - lastScan < 10) return;
        lastScan = client.world.getTime(); matches = 0; nearestMatch = null;
        BlockPos center = client.player.getBlockPos(); Block target = TARGETS.get(index);
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            if (pos.getSquaredDistance(center) <= radius * radius && client.world.getBlockState(pos).isOf(target)) {
                matches++;
                if (nearestMatch == null || pos.getSquaredDistance(center) < nearestMatch.getSquaredDistance(center)) nearestMatch = pos.toImmutable();
            }
        }
    }
    @Override public void renderHud(DrawContext draw) {
        status(draw, "AREA MINER  ·  " + TARGETS.get(index).getName().getString() + "  ·  " + matches + " within " + radius, 44);
        if (nearestMatch != null) status(draw, "PREVIEW  ·  nearest matching block at " + nearestMatch.toShortString(), 62);
    }
}
