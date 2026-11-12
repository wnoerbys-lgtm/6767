package dev.ratclient.ui;

import dev.ratclient.feature.ConfigStore;
import dev.ratclient.feature.FeatureManager;
import dev.ratclient.feature.RatFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class RatScreen extends Screen {
    public RatScreen() { super(Text.literal("Rat Client")); }
    @Override public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        renderBackground(draw, mouseX, mouseY, delta);
        int left = width / 2 - 170, top = height / 2 - 130;
        draw.fill(left, top, left + 340, top + 260, 0xEE111722);
        draw.fill(left, top, left + 340, top + 34, 0xFF222E40);
        draw.drawTextWithShadow(textRenderer, "RAT CLIENT", left + 16, top + 12, 0xFFB8E986);
        draw.drawTextWithShadow(textRenderer, "1.21.11  ·  FABRIC  ·  Right Shift to close", left + 150, top + 12, 0xFFB5C0CF);
        int row = 0;
        for (RatFeature feature : FeatureManager.all()) {
            int y = top + 48 + row * 38;
            draw.fill(left + 12, y, left + 328, y + 33, 0xFF1A2230);
            draw.fill(left + 260, y + 5, left + 310, y + 28, feature.enabled() ? 0xFF557A45 : 0xFF3B4553);
            draw.drawTextWithShadow(textRenderer, feature.name(), left + 20, y + 5, 0xFFF0F3F8);
            String description = feature.description();
            if (description.length() > 34) description = description.substring(0, 31) + "...";
            draw.drawTextWithShadow(textRenderer, description, left + 20, y + 18, 0xFFAAB5C4);
            draw.drawTextWithShadow(textRenderer, feature.enabled() ? "ON" : "OFF", left + 278, y + 12, 0xFFFFFFFF);
            draw.drawTextWithShadow(textRenderer, "−", left + 236, y + 10, 0xFFFFFFFF);
            draw.drawTextWithShadow(textRenderer, "+", left + 320, y + 10, 0xFFFFFFFF);
            row++;
        }
        RatFeature selected = FeatureManager.all().get(Math.min(FeatureManager.all().size() - 1,
            Math.max(0, (mouseY - (top + 48)) / 38)));
        draw.drawTextWithShadow(textRenderer, selected.settingLabel() + "    ·    Single-player only: movement tools", left + 16, top + 244, 0xFFCBD6E5);
        super.render(draw, mouseX, mouseY, delta);
    }
    @Override public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y(); int button = click.button();
        int left = width / 2 - 170, top = height / 2 - 130;
        int row = ((int) mouseY - (top + 48)) / 38;
        if (row >= 0 && row < FeatureManager.all().size() && mouseY >= top + 48 && mouseY < top + 238) {
            RatFeature feature = FeatureManager.all().get(row);
            if (button == 0 && mouseX >= left + 258 && mouseX <= left + 312) feature.setEnabled(!feature.enabled());
            else if (button == 0 && mouseX >= left + 226 && mouseX < left + 256) feature.adjust(-1);
            else if (button == 0 && mouseX > left + 312) feature.adjustSecondary(1);
            else if (button == 1) feature.adjust(1);
            ConfigStore.save(FeatureManager.all()); return true;
        }
        return super.mouseClicked(click, doubled);
    }
    @Override public boolean shouldPause() { return false; }
}
