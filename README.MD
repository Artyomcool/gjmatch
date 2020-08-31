# GJMatch
[![Build Status](https://travis-ci.com/Artyomcool/gjmatch.svg?branch=master)](https://travis-ci.com/artyomcool/gjmatch)
[![codecov](https://codecov.io/gh/Artyomcool/gjmatch/branch/master/graph/badge.svg)](https://codecov.io/gh/Artyomcool/gjmatch)

GJMatch is a library that can help with complex JSON verification or pattern-matching.

The main feature is using Groovy GString syntax to create lightweight and readable JSON-patterns, that can be used as
verifiers as well as documentation:

```groovy
def marker = "Some String ID"

def json =
        """
        {
            "version":"5.7.10",
            "marker":"$marker",
            "numberValue":7,
            "someExtraValue": "Extra Value"
        }
        """

def pattern =
        """
        {
            "version":"5.7.10",
            "marker": "$marker",
            "numberValue": $anyNumber
        }
        """

match(json, pattern)
```

In example above it is validated, that all fields in the pattern exist and `numberValue` has a numeric value.
If `numberValue` is not a number, e.g. "str", exception will be thrown:
```
NotMatchedException: Can't cast class java.lang.String to class java.lang.Number: expected any number, found "str", path: $.numberValue
```
Any level of json nesting is available, so you can validate even very deep parts of JSON (and detailed exception,
 pointing to a wrong part of JSON).


There are plenty build-in validators available in GMatchers class, but all of them can be created manually.
It is also possible to validate exact matching part of json, or mark extra fields as unexpected, or mark concrete
field as unexpected.

Most of typical usages can be found at `com.github.artyomcool.gjmatch.Showcase`, e.g.:
```groovy
@Test
void complex() {

    def marker = "Hello"

    def json =
            """
            {
                "version":"5.7.10",
                "marker":"$marker",
                "nullValue": null,
                "hasNumberValue":"str",
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
```

For complex manual verification you can collect interested fields with ```$capture``` verifier:
```groovy
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
```

**Note**: it is non-goal to provide runtime-verifiers for production code. The main goal is to help with api testings.

Gradle dependency:
```groovy
dependencies {
    testImplementation 'org.codehaus.groovy:groovy-all:3.0.3'
    testImplementation 'com.github.artyomcool:gjmatch:0.1'
}
```