package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyNumber
import static org.junit.jupiter.api.Assertions.assertThrows

class AnyNumberTest {
    public static final PATTERN = """$anyNumber"""

    @ParameterizedTest
    @ValueSource(strings = ['1', '-5.6e1'])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['"z"', '"1"', 'null', '{}', '[]'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }
}
