package io.github.alkyaly.timeinabottle;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("time-in-a-bottle.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private int[] speedLevels;
    //The duration of the AcceleratorEntity.
    private int duration;
    //The amount of time added to the Time in a Bottle in 20 ticks.
    private int timeSecond;
    //The max amount of time a Time in a Bottle can store.
    private long maxTime;

    public ModConfig() {
        try {
            load();
        } catch (IOException ignored) {
            try (var writer = new JsonWriter(new FileWriter(PATH.toFile()))) {
                writer.setIndent("  ");
                GSON.toJson(addDefault(new JsonObject()), writer);
                load();
            } catch (IOException e) {
                TimeInABottle.LOGGER.fatal("Something went wrong while creating the config!", e);
            }
        }
    }

    public void load() throws IOException {
        JsonObject object = JsonParser.parseString(new String(Files.readAllBytes(PATH))).getAsJsonObject();
        load(object);
    }

    private void load(JsonObject obj) {
        JsonArray speedLevelElement = obj.get("speed-levels").getAsJsonArray();
        speedLevels = new int[speedLevelElement.size()];
        for (int i = 0; i < speedLevelElement.size(); i++) {
            speedLevels[i] = speedLevelElement.get(i).getAsInt();
        }
        this.duration = obj.get("duration").getAsInt();
        this.timeSecond = obj.get("time-second").getAsInt();
        this.maxTime = obj.get("max-time").getAsLong();
    }

    private JsonObject addDefault(JsonObject obj) {
        JsonArray arr = new JsonArray();

        for (int i = 1; i <= 32; i *= 2) {
            arr.add(i);
        }

        obj.add("speed-levels", arr);
        obj.addProperty("duration", 30);
        obj.addProperty("time-second", 20);
        obj.addProperty("max-time", 622080000);

        return obj;
    }

    public int[] getSpeedLevels() {
        return speedLevels;
    }

    public int getDuration() {
        return duration;
    }

    public int getTimeSecond() {
        return timeSecond;
    }

    public long getMaxTime() {
        return maxTime;
    }
}
