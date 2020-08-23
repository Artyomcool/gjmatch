package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.exact
import static org.junit.jupiter.api.Assertions.assertThrows

class ExactTest {

    private static final SUB_JSON = '{"b": 1}'

    private static final PATTERN =
            """
            {
                "a": ${exact(SUB_JSON)}
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
                    "b": 1.0,
                }
            }
            """,
    ])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "a": {
                    "b": 1,
                    "c": 2
                }
            }
            """,

            """
            {
                "a": {
                    "b": 2
                }
            }
            """,
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

}
