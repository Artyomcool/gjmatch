package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.arraySlice
import static com.github.artyomcool.gjmatch.GJMatchers.getAnyNumber
import static org.junit.jupiter.api.Assertions.assertThrows

class ArraySliceTest {

    static final PATTERN = """[1, ${arraySlice(anyNumber)}, 10]"""

    @ParameterizedTest
    @ValueSource(strings = [
            """[1, 10]""",
            """[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]""",
    ])
    void positive(String json) {
        match(json, PATTERN)
    }


    @ParameterizedTest
    @ValueSource(strings = [
            """[2, 3, 4, 5, 6, 7, 8, 9]""",
            """[1, 2, 3]""",
            """[8, 9, 10]""",
            """[1, "8", 10]""",
    ])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }
}
