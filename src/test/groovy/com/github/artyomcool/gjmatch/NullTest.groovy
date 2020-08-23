package com.github.artyomcool.gjmatch

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static com.github.artyomcool.gjmatch.GJMatch.match
import static org.junit.jupiter.api.Assertions.assertThrows

class NullTest {

    public static final NULL_PATTERN = 'null'

    public static final NULL_FIELD_PATTERN = '{"n": null}'

    @ParameterizedTest
    @ValueSource(strings = ['null'])
    void primitivePositive(String json) {
        match(json, NULL_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{}', '[]', '7', '10.5', '"string"'])
    void primitiveNegative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, NULL_PATTERN)
        })
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": null}'])
    void positive(String json) {
        match(json, NULL_FIELD_PATTERN)
    }

    @ParameterizedTest
    @ValueSource(strings = ['{"n": {}}', '{"n": []}', '{"n": 7}', '{"n": 10.5}', '{"n": "string"}'])
    void negative(String json) {
        assertThrows(NotMatchedException.class, () -> {
            match(json, NULL_FIELD_PATTERN)
        })
    }

}
