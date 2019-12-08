package com.foxminded.courses.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RandomizerUtilTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @Test
    void getRandomNumberBetweenShouldThrowIllegalArgumentExceptionWhenMinGreaterThanMax() {
        int max = 5;
        int min = 10;

        assertThrows(IllegalArgumentException.class, () -> RandomizerUtil.getRandomNumberBetween(min, max));
    }

    @Test
    void getRandomNumberBetweenShouldThrowIllegalArgumentExceptionWhenMinEqualsMax() {
        int max = 5;
        int min = 10;

        assertThrows(IllegalArgumentException.class, () -> RandomizerUtil.getRandomNumberBetween(min, max));
    }

    @Test
    void getRandomNumberBetweenShouldReturnMinOrMaxWhenMaxOneGreaterThanMin() {
        int min = 0;
        int max = 1;
        int actualResult = RandomizerUtil.getRandomNumberBetween(min, max);

        assertTrue(actualResult == min || actualResult == max);
    }

    @Test
    void getRandomNumberBetweenShouldReturnNumberFromMinToMaxInclusive() {
        int min = 0;
        int max = 10;
        int actualResult = RandomizerUtil.getRandomNumberBetween(min, max);

        assertTrue(actualResult >= min && actualResult <= max);
    }
}
