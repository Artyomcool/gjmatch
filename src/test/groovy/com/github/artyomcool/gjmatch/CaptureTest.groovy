package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.arraySlice
import static com.github.artyomcool.gjmatch.GJMatchers.capture

class CaptureTest {

    def found = []
    def cap = capture(found)

    private final pattern =
            """
            {
                "f1": $cap,
                "f2": $cap,
                "f3": $cap,
                "f4": $cap
            }
            """

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": "a",
                "f2": "b",
                "f3": "c",
                "f4": "d"
            }
            """,
    ])
    void positive(String json) {
        match(json, pattern)
        assert found == ['a', 'b', 'c', 'd']
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": "a",
                "f2": "b",
                "f3": "c"
            }
            """,
    ])
    void positivePartial(String json) {
        match(json, pattern)
        assert found == ['a', 'b', 'c']
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "f1": ["a"],
                "f2": {
                    "f3": ["b","c","d","e"]
                }
            }
            """,
    ])
    void positiveSlice(String json) {
        def pattern =
                """
                {
                    "f1": [${arraySlice(cap)}],
                    "f2": {
                        "f3": ["b", ${arraySlice(cap)}, "e"]
                    }
                }
                """
        match(json, pattern)
        assert found == ['a', 'c', 'd']
    }


}
