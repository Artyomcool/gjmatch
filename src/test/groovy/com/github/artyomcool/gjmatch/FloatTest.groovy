package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static org.junit.jupiter.api.Assertions.assertThrows

class FloatTest {

    public static final FLOAT_PATTERN = '1.5'

    public static final FLOAT_FIELD_PATTERN = '{"n": 1.5}'

    @ParameterizedTest
    @ValueSource(strings = ['1.5'])
    void primitivePositive(String json) {
        match(json, FLOAT_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{}', '[]', 'null', '7', '10.5', '"string"'])
    void primitiveNegative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, FLOAT_PATTERN)
        })
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": 1.5}'])
    void positive(String json) {
        match(json, FLOAT_FIELD_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": {}}', '{"n": []}', '{"n": null}', '{"n": 7}', '{"n": 10.5}', '{"n": "string"}'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, FLOAT_FIELD_PATTERN)
        })
    }

}
