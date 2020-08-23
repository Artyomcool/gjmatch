package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyNumber
import static com.github.artyomcool.gjmatch.GJMatchers.matches
import static org.junit.jupiter.api.Assertions.assertThrows

class MatchTest {

    private static final SUB_JSON = """{"b": $anyNumber}"""
    private static final SUB_JSON_SIMPLE = """{"b": 1}"""

    private static final PATTERN =
            """
            {
                "a": ${matches(SUB_JSON)}
            }
            """

    private static final PATTERN_SIMPLE =
            """
            {
                "a": ${matches(SUB_JSON_SIMPLE)}
            }
            """

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "a": {
                    "b": 1
                }
            }
            """,

            """
            {
                "a": {
                    "b": 1,
                    "c": 2
                }
            }
            """,
    ])
    void positive(String json) {
        match(json, PATTERN)
        match(json, PATTERN_SIMPLE)
    }

    @ParameterizedTest
    @ValueSource(strings = [

            """
            {
                "a": {
                    "b": "str"
                }
            }
            """,
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

    @ParameterizedTest
    @ValueSource(strings = [

            """
            {
                "a": {
                    "b": 2
                }
            }
            """,
    ])
    void negativeSimple(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN_SIMPLE)
        })
    }

}
