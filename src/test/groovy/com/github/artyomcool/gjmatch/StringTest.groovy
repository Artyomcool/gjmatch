package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static org.junit.jupiter.api.Assertions.assertThrows

class StringTest {

    public static final STRING_PATTERN = '"hello"'

    public static final STRING_FIELD_PATTERN = '{"n": "hello"}'

    @ParameterizedTest
    @ValueSource(strings = ['"hello"'])
    void primitivePositive(String json) {
        match(json, STRING_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{}', '[]', 'null', '7', '10.5', '"string"'])
    void primitiveNegative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, STRING_PATTERN)
        })
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": "hello"}'])
    void positive(String json) {
        match(json, STRING_FIELD_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": {}}', '{"n": []}', '{"n": null}', '{"n": 7}', '{"n": 10.5}', '{"n": "string"}'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, STRING_FIELD_PATTERN)
        })
    }

}
