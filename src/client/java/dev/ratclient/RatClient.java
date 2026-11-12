package dev.ratclient;

import dev.ratclient.feature.FeatureManager;
import dev.ratclient.ui.RatScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import org.lwjgl.glfw.GLFW;

public final class RatClient implements ClientModInitializer {
    public static final String MOD_ID = "ratclient";
    private static KeyBinding menu, boatFly, schematic, areaMiner, freecam, baseFinder;

    @Override public void onInitializeClient() {
        FeatureManager.init();
        var category = KeyBinding.Category.create(net.minecraft.util.Identifier.of(MOD_ID, "main"));
        menu = register("open_menu", GLFW.GLFW_KEY_RIGHT_SHIFT, category);
        boatFly = register("toggle_boat_fly", GLFW.GLFW_KEY_B, category);
        schematic = register("toggle_schematic", GLFW.GLFW_KEY_UNKNOWN, category);
        areaMiner = register("toggle_area_miner", GLFW.GLFW_KEY_UNKNOWN, category);
        freecam = register("toggle_freecam", GLFW.GLFW_KEY_F6, category);
        baseFinder = register("toggle_base_finder", GLFW.GLFW_KEY_UNKNOWN, category);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menu.wasPressed()) client.setScreen(new RatScreen());
            if (boatFly.wasPressed()) FeatureManager.toggle("Boat Fly");
            if (schematic.wasPressed()) FeatureManager.toggle("Schematic Builder");
            if (areaMiner.wasPressed()) FeatureManager.toggle("Area Miner");
            if (freecam.wasPressed()) FeatureManager.toggle("Freecam");
            if (baseFinder.wasPressed()) FeatureManager.toggle("Base Finder");
            FeatureManager.tick(client);
        });
        HudRenderCallback.EVENT.register((draw, tickCounter) -> FeatureManager.renderHud(draw));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("rat").then(ClientCommandManager.literal("schematic")
                .then(ClientCommandManager.literal("load").then(ClientCommandManager.argument("file", net.minecraft.command.argument.StringArgumentType.string())
                    .executes(ctx -> FeatureManager.loadSchematic(ctx.getSource().getClient(), net.minecraft.command.argument.StringArgumentType.getString(ctx, "file"))))))));
    }

    private static KeyBinding register(String name, int key, KeyBinding.Category category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ratclient." + name,
            InputUtil.Type.KEYSYM, key, category));
    }
}
