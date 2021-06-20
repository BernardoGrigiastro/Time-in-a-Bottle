package io.github.alkyaly.timeinabottle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModConfig {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("time-in-a-bottle.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    //I don't think using FastUtil's IntSet would have any advantage here.
    private final Set<Integer> speedLevels;
    //The duration of the AcceleratorEntity.
    private int duration;
    //The amount of time added to the Time in a Bottle in 20 ticks.
    private int timeSecond;
    //The max amount of time a Time in a Bottle can store.
    private long maxTime;

    public ModConfig() {
        //LinkedHashMap so we can preserve the order.
        speedLevels = new LinkedHashSet<>();

        try {
            load();
        } catch (IOException ignored) {
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(PATH.toString()));
                writer.setIndent("  ");
                GSON.toJson(addDefault(new JsonObject()), writer);
                writer.close();
                JsonObject obj = new JsonParser().parse(new String(Files.readAllBytes(PATH))).getAsJsonObject();
                load(obj);
            } catch (IOException e) {
                TimeInABottle.LOGGER.fatal("Something went wrong while creating the config!", e);
            }
        }
    }

    public void load() throws IOException {
        JsonObject object = new JsonParser().parse(new String(Files.readAllBytes(PATH))).getAsJsonObject();
        load(object);
    }

    private void load(JsonObject obj) {
        JsonArray speedLevelElement = obj.get("speed-levels").getAsJsonArray();
        speedLevelElement.forEach(el -> speedLevels.add(el.getAsInt()));

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

    public Set<Integer> getSpeedLevels() {
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
