package dev.ratclient.feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class RatFeature {
    private final String name, description;
    private boolean enabled;
    protected RatFeature(String name, String description) { this.name = name; this.description = description; }
    public String name() { return name; }
    public String description() { return description; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; onToggle(value); }
    protected void onToggle(boolean value) {}
    public String settingLabel() { return "No settings"; }
    public int settingValue() { return 0; }
    public void loadSetting(int value) {}
    public int secondarySettingValue() { return 0; }
    public void loadSecondarySetting(int value) {}
    public void adjustSecondary(int direction) { adjust(direction); }
    public void adjust(int direction) {}
    public void tick(MinecraftClient client) {}
    public void renderHud(DrawContext draw) {}
    protected void status(DrawContext draw, String text, int y) {
        draw.fill(8, y, 210, y + 14, 0xB8141822);
        draw.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, 14, y + 3, 0xFFE8EEF7);
    }
}
