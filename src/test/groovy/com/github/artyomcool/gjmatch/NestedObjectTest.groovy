package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static org.junit.jupiter.api.Assertions.assertThrows

class NestedObjectTest {

    private static final PATTERN =
            """
            {
                "someValue": {
                    "nestedValue": "hi"
                }
            }
            """

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {
                "someValue": {
                    "nestedValue": "hi",
                    "nestedExtraValue": "extra"
                },
                "extraValue": "extra"
            }
            """,

            """
            {
                "someValue": {
                    "nestedValue": "hi",
                    "nestedExtraValue": "anotherExtra",
                    
                },
                "extraValue": "extra2",
                "extraValue": 7
            }
            """,
    ])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = [
            """
            {}
            """,

            """
            {
                "someValue": "string"
            }
            """,

            """
            {
                "someValue": {}
            }
            """,

            """
            {
                "someValue": {
                    "nestedValue": {},
                }
            }
            """,

            """
            {
                "someValue": {
                    "nestedValue": 7,
                }
            }
            """,

            """
            {
                "someValue": {
                    "nestedValue": "hello",
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
