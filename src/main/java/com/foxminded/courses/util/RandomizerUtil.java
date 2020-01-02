package com.foxminded.courses.util;

import java.util.Random;

public final class RandomizerUtil {

    private RandomizerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static int getRandomNumberBetween(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return new Random().ints(min, max + 1).findFirst().orElse(min);
    }
}
