package com.github.artyomcool.gjmatch

import org.junit.jupiter.api.Test

import static com.github.artyomcool.gjmatch.GJMatchers.*

class NamedClosureTest {

    @Test
    void print() {
        def closure = namedClosure("Hello") { Number n -> n > 0 }
        assert closure.toString() == "Hello"
        assert "$closure".toString() == "Hello"
    }

}
