package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.regex
import static org.junit.jupiter.api.Assertions.assertThrows

class RegexTest {

    public static final PATTERN = """${regex('\\d')}"""

    @ParameterizedTest
    @ValueSource(strings = ['"1"', '"2"'])
    void positive(String json) {
        match(json, PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['"z"', '"10"', 'null', '7'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, PATTERN)
        })
    }
}
