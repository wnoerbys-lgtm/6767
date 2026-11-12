package dev.ratclient.feature.modules;

import dev.ratclient.feature.RatFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.client.gui.DrawContext;

public final class BoatFlyFeature extends RatFeature {
    private int speed = 2;
    private AbstractBoatEntity controlledBoat;
    public BoatFlyFeature() { super("Boat Fly", "Local-world boat movement controls"); }
    @Override public String settingLabel() { return "Speed " + speed + " (1-5)"; }
    @Override public int settingValue() { return speed; }
    @Override public void loadSetting(int value) { speed = Math.max(1, Math.min(5, value)); }
    @Override public void adjust(int direction) { loadSetting(speed + direction); }
    @Override public void tick(MinecraftClient client) {
        if (!enabled() || client.player == null || client.world == null) return;
        if (!client.isInSingleplayer()) return;
        if (!(client.player.getVehicle() instanceof AbstractBoatEntity boat)) { restoreBoat(); return; }
        if (controlledBoat != null && controlledBoat != boat) restoreBoat();
        controlledBoat = boat;
        float yaw = client.player.getYaw();
        double forward = (client.options.forwardKey.isPressed() ? 1 : 0) - (client.options.backKey.isPressed() ? 1 : 0);
        double strafe = (client.options.rightKey.isPressed() ? 1 : 0) - (client.options.leftKey.isPressed() ? 1 : 0);
        double radians = Math.toRadians(yaw);
        double x = (-Math.sin(radians) * forward + Math.cos(radians) * strafe) * speed * 0.10;
        double z = (Math.cos(radians) * forward + Math.sin(radians) * strafe) * speed * 0.10;
        double y = (client.options.jumpKey.isPressed() ? 0.18 : 0) - (client.options.sneakKey.isPressed() ? 0.18 : 0);
        boat.setNoGravity(true);
        boat.setVelocity(x, y, z);
    }
    @Override public void renderHud(DrawContext draw) { status(draw, "BOAT FLY  ·  speed " + speed + "  ·  " + (MinecraftClient.getInstance().isInSingleplayer() ? "ready" : "single-player only"), 8); }
    @Override protected void onToggle(boolean value) { if (!value) restoreBoat(); }
    private void restoreBoat() { if (controlledBoat != null) controlledBoat.setNoGravity(false); controlledBoat = null; }
}
