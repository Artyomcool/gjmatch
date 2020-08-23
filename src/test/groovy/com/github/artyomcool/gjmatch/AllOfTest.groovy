package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.allOf
import static com.github.artyomcool.gjmatch.GJMatchers.anyOf
import static com.github.artyomcool.gjmatch.GJMatchers.exact
import static com.github.artyomcool.gjmatch.GJMatchers.getAnyString
import static com.github.artyomcool.gjmatch.GJMatchers.getExpectNoValue
import static org.junit.jupiter.api.Assertions.assertThrows

class AllOfTest {

    static final PATTERN =
            """
            {
                "f":
                ${
                allOf(
                        { Number n -> n > 10 },
                        { Number n -> n < 15 }
                )
            }
            }
            """

    @ParameterizedTest
    @ValueSource(strings = ['{"f": 11}', '{"f": 12}'])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{}', '{"f": 7}', '{"f": 20}', '{"f": "str"}'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }

}
