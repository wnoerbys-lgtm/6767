package dev.ratclient.feature;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.List;

public final class ConfigStore {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("ratclient.json");
    private ConfigStore() {}
    public static void load(List<RatFeature> features) {
        try {
            if (!Files.exists(FILE)) return;
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (RatFeature feature : features) {
                JsonObject data = root.has(feature.name()) ? root.getAsJsonObject(feature.name()) : null;
                if (data != null && data.has("enabled")) feature.setEnabled(data.get("enabled").getAsBoolean());
                if (data != null && data.has("setting")) feature.loadSetting(data.get("setting").getAsInt());
                if (data != null && data.has("secondary")) feature.loadSecondarySetting(data.get("secondary").getAsInt());
            }
        } catch (Exception ignored) { }
    }
    public static void save(List<RatFeature> features) {
        try {
            JsonObject root = new JsonObject();
            for (RatFeature feature : features) {
                JsonObject data = new JsonObject(); data.addProperty("enabled", feature.enabled()); data.addProperty("setting", feature.settingValue());
                data.addProperty("secondary", feature.secondarySettingValue());
                root.add(feature.name(), data);
            }
            Files.createDirectories(FILE.getParent()); Files.writeString(FILE, new GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (Exception ignored) { }
    }
}
