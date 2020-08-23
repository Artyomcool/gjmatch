package com.github.artyomcool.gjmatch

import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.same
import static org.junit.jupiter.api.Assertions.assertThrows

class SameSimpleTest {

    def same = same()

    private final pattern =
            """
            {
                "f1": $same,
                "f2": $same
            }
            """

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": "a",
                "f2": "a"
            }
            """,

            """
            {
                "f1": 1.0e40,
                "f2": 1.0e40
            }
            """,

            """
            {
                "f1": [{"a": {"b": "c"}}, 4],
                "f2": [{"a": {"b": "c"}}, 4]
            }
            """,
    ])
    void positive(String json) {
        match(json, pattern)
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": "a",
                "f2": "a"
            }
            """,
    ])
    void capture(String json) {
        match(json, pattern)

        assert same.delegate == 'a'
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": "a",
                "f2": "b"
            }
            """,

            """
            {
            }
            """,

            """
            {
                "f1": 1.0e41,
                "f2": 1.0e40
            }
            """,

            """
            {
                "f1": [{"a": {"b": "c"}}, 4],
                "f2": [{"a": {"b": "d"}}, 4]
            }
            """,
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, pattern)
        })
    }

}
