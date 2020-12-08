package com.github.artyomcool.gjmatch

import org.junit.jupiter.api.Test

import static com.github.artyomcool.gjmatch.GJMatch.match
import static com.github.artyomcool.gjmatch.GJMatchers.*

class Showcase {


    @Test
    void simpleNested() {

        def json =
                """
                {
                    "someValue": {
                        "nestedValue": "hi",
                        "nestedExtraValue": "extra"
                    },
                    "extraValue": "extra"
                }
                """

        def pattern =
                """
                {
                    "someValue": {
                        "nestedValue": "hi"
                    }
                }
                """

        match(json, pattern)
    }

    @Test
    void complex() {

        def marker = "Hello"

        def json =
                """
                {
                    "version":"5.7.10",
                    "marker":"$marker",
                    "nullValue": null,
                    "hasNumberValue":7,
                    "array": [1, "4", 11, 10]
                }
                """

        def pattern =
                """
                {
                    "version":"5.7.10",
                    "marker": "$marker",
                    "nullValue": null,
                    "noValue": $expectNoValue,
                    "hasNumberValue": $anyNumber,
                    "array": [1,"4", $anyNumber, 10],
                    $unmatchUnexpected
                }
                """

        match(json, pattern)
    }

    @Test
    void vararg() {

        def json =
                """
                {
                    "array1": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                    "array2": [0, "1", "2", 3]
                }
                """

        def pattern =
                """
                {
                    "array1": [0, 1, ${atLeast(4)}, 8, 9],
                    "array2": [0, ${arraySlice(anyString)}, 3]
                }
                """

        match(json, pattern)
    }

    @Test
    void regex() {

        def json =
                """
                "myString"
                """

        def pattern =
                """
                ${regex(/my.+ring/)}
                """

        match(json, pattern)
    }

    @Test
    void allOf() {

        def lowerCase = namedClosure("lower case") { String text ->
            text.toLowerCase() == text
        }
        def length4 = namedClosure("length is 4") { String text ->
            text.length() == 4
        }

        def json =
                """
                {
                    "field": "text"
                }
                """

        def pattern =
                """
                {
                    "field": ${allOf(lowerCase, length4)}
                }
                """

        match(json, pattern)
    }

    @Test
    void capture() {
        def values = []
        def cap = capture(values)

        def json =
                """
                {
                    "f1": "v1",
                    "f2": "v2",
                    "f3": {
                        "f4": "v4"
                    },
                    "f5": "v5",
                    "f6": "skip",
                    "a": [1, "skip", 3],
                    "slice": ["a", "b", "c", "d", "e"]
                }
                """

        def pattern =
                """
                {
                    "f1": $cap,
                    "f2": $cap,
                    "f3": {
                        "f4": $cap
                    },
                    "f5": $cap,
                    "f6": "skip",
                    "a": [$cap, "skip", $cap],
                    "slice": ["a", ${arraySlice(cap)}, "e"]
                }
                """

        match(json, pattern)

        assert values == ["v1", "v2", "v4", "v5", 1, 3, "b", "c", "d"]
    }

    @Test
    void captureOnce() {
        def cap = GJMatchers.capture()

        def json =
                """
                {
                    "f1": "v1"
                }
                """

        def pattern =
                """
                {
                    "f1": $cap
                }
                """

        match(json, pattern)

        assert cap.value == "v1"
    }

    @Test
    void same() {
        def same = same(anyString)

        def json =
                """
                {
                    "f1": "v1",
                    "f2": "v1"
                }
                """

        def pattern =
                """
                {
                    "f1": $same,
                    "f2": $same
                }
                """

        match(json, pattern)
    }

    @Test
    void sameWithCapture() {
        def out = [] as Set
        def same = same(allOf(anyString, capture(out)))

        def json =
                """
                {
                    "f1": "v1",
                    "f2": "v1"
                }
                """

        def pattern =
                """
                {
                    "f1": $same,
                    "f2": $same
                }
                """

        match(json, pattern)

        assert out.first() == "v1"
    }

    @Test
    void exact() {
        def subJson =
                """
                {
                    "f3": "v2",
                    "f4": 7,
                    "f5": {
                        "a": [1, 1.0, 0.1],
                        "b": null
                    }
                }
                """
        def json =
                """
                {
                    "f1": "v1",
                    "f2": $subJson
                }
                """

        def pattern =
                """
                {
                    "f1": "v1",
                    "f2": ${exact(subJson)}
                }
                """

        match(json, pattern)
    }


}
