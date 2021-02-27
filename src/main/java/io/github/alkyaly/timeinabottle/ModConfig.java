package io.github.alkyaly.timeinabottle;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class ModConfig implements ConfigData {
    @Comment(" How many ticks must have passed for a Time in a Bottle gain 1 second.\n Default: 20")
    public static int TIME_SECOND = 20;

    @Comment(" How fast random ticks should tick with the Time in a Bottle Effect.\n Default: 15")
    public static int RANDOM_TICK = 15;

    @Comment(" Max amount of ticks inside of a Time in a Bottle.\n Default: 622080000")
    public static int MAX_TIME = 622080000;
}
