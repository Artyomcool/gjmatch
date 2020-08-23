package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.*
import static org.junit.jupiter.api.Assertions.assertThrows

class AnyOfTest {

    static final PATTERN =
            """
            {
                "f":
                ${
                    anyOf(
                        expectNoValue,
                        exact('7'),
                        anyString
                    )
                }
            }
            """

    @ParameterizedTest
    @ValueSource(strings = ['{}', '{"f": 7}', '{"f": "str"}'])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"f": null}', '{"f": 1}', '{"f": []}'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

}
