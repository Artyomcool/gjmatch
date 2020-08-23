package com.github.artyomcool.gjmatch


import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.anyNumber
import static com.github.artyomcool.gjmatch.GJMatchers.anyString
import static org.junit.jupiter.api.Assertions.assertThrows

class NestedArrayTest {

    static final PATTERN = """
                [
                    [],
                    [1, 2, 3],
                    [$anyString],
                    [
                        {
                            "x": 1
                        },
                        {
                            "a": [1, $anyNumber, {}]
                        }
                    ]
                ]
                """

    @ParameterizedTest
    @ValueSource(strings = [
            """
            [
                [],
                [1, 2, 3],
                ["hey"],
                [
                    {
                        "x": 1,
                        "y": 2
                    },
                    {
                        "a": [1, 2, {"b": "hi"}]
                    }
                ]
            ]
            """,

            """
            [
                [],
                [1, 2, 3],
                ["hi"],
                [
                    {
                        "x": 1,
                        "y": "another"
                    },
                    {
                        "a": [1, 100, {"c": "hello"}]
                    }
                ]
            ]
            """,
    ])
    void positive(String json) {
        match(json, PATTERN)
    }


    @ParameterizedTest
    @ValueSource(strings = [
            """
            [
                [],
                [1, 2, 4],
                ["hey"],
                [
                    {
                        "x": 1,
                        "y": 2
                    },
                    {
                        "a": [1, 2, {"b": "hi"}]
                    }
                ]
            ]
            """,

            """
            []
            """,

            """
            [[], [], [], []]
            """,

            """
            [
                [],
                [1, 2, 3],
                ["hi"],
                [{}]
            ]
            """,

            """
            [
                [],
                [1, 2, 3],
                ["hi"],
                [
                    {
                        "x": 1
                    },
                    {
                        "a": [1, "string", {}]
                    }
                ]
            ]
            """,
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

}
