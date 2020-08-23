package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.unmatchUnexpected
import static org.junit.jupiter.api.Assertions.assertThrows

class UnmatchUnexpectedTest {
    private static final PATTERN =
            """
            {
                "a": {
                    "b": 1
                },
                $unmatchUnexpected
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
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "a": {}
            }
            """,

            """
            {
                "a": {
                    "b": 1
                },
                "unexpected": 7
            }
            """,

            """
            {
                "a": {
                    "b": 1
                },
                "unexpected": null
            }
            """,
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

}
