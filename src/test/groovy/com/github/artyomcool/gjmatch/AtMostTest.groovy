package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyNumber
import static com.github.artyomcool.gjmatch.GJMatchers.atMost
import static org.junit.jupiter.api.Assertions.assertThrows

class AtMostTest {

    static final PATTERN_SIMPLE = """[1, ${atMost(2) }, 10]"""
    static final PATTERN = """[1, ${atMost(2, anyNumber) }, 10]"""

    @ParameterizedTest
    @ValueSource(strings = [
            """[1, 2, 3, 10]""",
            """[1, 10]""",
    ])
    void positive(String json) {
        match(json, PATTERN)
        match(json, PATTERN_SIMPLE)
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """[1, {}, "3", 10]""",
    ])
    void positiveSimple(String json) {
        match(json, PATTERN_SIMPLE)
    }


    @ParameterizedTest
    @ValueSource(strings = [
            """[2, 3, 4, 5, 6, 7, 8, 9]""",
            """[1, 2, 3]""",
            """[1, 2, 3, 4, 10]""",
            """[8, 9, 10]""",
            """[1, "7", "8", 10]""",
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }


    @ParameterizedTest
    @ValueSource(strings = [
            """[2, 3, 4, 5, 6, 7, 8, 9]""",
            """[1, 2, 3]""",
            """[1, 2, 3, 4, 10]""",
            """[8, 9, 10]""",
    ])
    void negativeSimple(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN_SIMPLE)
        })
    }
}
