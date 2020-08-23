package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyString
import static com.github.artyomcool.gjmatch.GJMatchers.same
import static org.junit.jupiter.api.Assertions.assertThrows

class SameWithDelegateTest {

    def same = same(anyString)

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
    ])
    void positive(String json) {
        match(json, pattern)
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
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, pattern)
        })
    }

}
