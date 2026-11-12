package dev.ratclient.feature.modules;

import dev.ratclient.feature.RatFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Free camera is deliberately limited to an integrated single-player world. */
public final class FreecamFeature extends RatFeature {
    private int speed = 2;
    private OtherClientPlayerEntity camera;
    private boolean previousGlow;
    public FreecamFeature() { super("Freecam", "Independent local camera with player-body marker"); }
    @Override public String settingLabel() { return "Speed " + speed + " (1-5)"; }
    @Override public int settingValue() { return speed; }
    @Override public void loadSetting(int value) { speed = Math.max(1, Math.min(5, value)); }
    @Override public void adjust(int direction) { loadSetting(speed + direction); }
    @Override protected void onToggle(boolean value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (value) {
            if (!client.isInSingleplayer() || client.player == null || client.world == null) { setEnabled(false); return; }
            camera = new OtherClientPlayerEntity(client.world, client.player.getGameProfile());
            camera.setId(-8675309);
            camera.copyPositionAndRotation(client.player);
            camera.setNoGravity(true);
            client.world.addEntity(camera);
            previousGlow = client.player.isGlowing();
            client.player.setGlowing(true);
            client.setCameraEntity(camera);
        } else {
            if (client.player != null) { client.setCameraEntity(client.player); client.player.setGlowing(previousGlow); }
            if (camera != null) camera.discard();
            camera = null;
        }
    }
    @Override public void tick(MinecraftClient client) {
        if (!enabled()) return;
        if (!client.isInSingleplayer() || client.player == null || client.world == null || camera == null) { setEnabled(false); return; }
        double forward = (client.options.forwardKey.isPressed() ? 1 : 0) - (client.options.backKey.isPressed() ? 1 : 0);
        double strafe = (client.options.rightKey.isPressed() ? 1 : 0) - (client.options.leftKey.isPressed() ? 1 : 0);
        double radians = Math.toRadians(camera.getYaw()); double scale = speed * 0.16;
        Vec3d movement = new Vec3d(-Math.sin(radians) * forward + Math.cos(radians) * strafe,
            (client.options.jumpKey.isPressed() ? 1 : 0) - (client.options.sneakKey.isPressed() ? 1 : 0),
            Math.cos(radians) * forward + Math.sin(radians) * strafe).multiply(scale);
        camera.setPosition(camera.getPos().add(movement));
        camera.setYaw(client.player.getYaw()); camera.setPitch(client.player.getPitch());
    }
    @Override public void renderHud(DrawContext draw) {
        var player = MinecraftClient.getInstance().player;
        if (player != null) status(draw, "FREECAM  ·  speed " + speed + "  ·  glowing body at " + player.getBlockPos().toShortString(), 80);
    }
}
