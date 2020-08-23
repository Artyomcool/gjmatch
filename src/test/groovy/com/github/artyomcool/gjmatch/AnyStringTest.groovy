package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyString
import static org.junit.jupiter.api.Assertions.assertThrows

class AnyStringTest {
    public static final PATTERN = """$anyString"""

    @ParameterizedTest
    @ValueSource(strings = ['"str"', '""'])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['7', 'null', '{}', '[]'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }
}
