package com.github.artyomcool.gjmatch


import groovy.transform.CompileStatic

import static com.github.artyomcool.gjmatch.GJMatch.NO_VALUE
import static com.github.artyomcool.gjmatch.GJMatch.calculateArg
import static com.github.artyomcool.gjmatch.GJMatchUtils.parseGString
import static com.github.artyomcool.gjmatch.GJMatchUtils.parseString

@CompileStatic
class GJMatchers {

    static Closure<Boolean> expectNoValue = namedClosure("no value") { obj ->
        obj == NO_VALUE
    }

    static Closure<Boolean> anyNumber = namedClosure("any number") { Number number ->
        true
    }

    static Closure<Boolean> anyString = namedClosure("any string") { String str ->
        true
    }

    static Closure<Boolean> zeroOrMore = namedClosure("zero or more values") { ... var ->
        true
    }

    static Closure<Boolean> unmatchUnexpected = namedClosure("no extra elements") {
        String anyUnexpectedKey, Object value -> false
    }

    static Closure<Boolean> atLeast(int count) {
        return namedClosure("at least $count elements") { ... var ->
            var.length >= count
        }
    }

    static Closure<Boolean> atLeast(int count, Closure<?> d) {
        return namedClosure("at least $count elements and " + d) { ... var ->
            if (var.length >= count) {
                checkVarArg(var, d)
                true
            } else {
                false
            }
        }
    }

    static Closure<Boolean> regex(String regex) {
        return namedClosure("regex $regex") { String text ->
            text ==~ regex
        }
    }

    static Closure<Boolean> atMost(int count) {
        return namedClosure("at most $count elements") { ... var ->
            var.length <= count
        }
    }

    static Closure<Boolean> atMost(int count, Closure<?> d) {
        return namedClosure("at most $count elements and " + d) { ... var ->
            if (var.length <= count) {
                checkVarArg(var, d)
                true
            } else {
                false
            }
        }
    }

    static Closure<Boolean> arraySlice(Closure<?> d) {
        return namedClosure("all values " + d) { ... var ->
            checkVarArg(var, d)
        }
    }

    private static boolean checkVarArg(Object[] var, Closure<?> d) {
        var.every {
            d(calculateArg(it, d))
        }
    }

    static Closure<Boolean> exact(String json) {
        Object parsed = parseString(json)
        return namedClosure("exact json match: " + String.valueOf(parsed)) { Object obj ->
            obj == parsed
        }
    }

    static Closure<Boolean> matches(String pattern) {
        return matches("$pattern")
    }

    static Closure<Boolean> matches(GString pattern) {
        def parsedPattern = parseGString(pattern)
        def match = new GJMatch(parsedPattern)
        return namedClosure("pattern match: " + parsedPattern) { Object obj ->
            match.match(obj)
            return true
        }
    }

    static Closure<Boolean> same(Closure<?> d) {
        return sameInternal("always the same value and " + d, d)
    }

    static Closure<Boolean> same() {
        return sameInternal("always the same value", { obj -> true })
    }

    private static Closure<Boolean> sameInternal(String name, Closure<?> d) {
        return sameInternal("$name", d)
    }

    private static Closure<Boolean> sameInternal(GString name, Closure<?> d) {
        def set = false
        Closure<Boolean> closure
        closure = namedClosure(name) { obj ->
            if (obj == NO_VALUE) {
                return false
            }
            if (set) {
                if (closure.delegate != obj) {
                    throw new NotMatchedException("Previous value is " + closure.delegate + " and now is " + obj)
                }
                return true
            }
            if (!d.call(calculateArg(obj, d))) {
                return false
            }
            closure.delegate = obj
            set = true
            return true
        }
        return closure
    }

    static Closure<Boolean> capture(Collection<?> out) {
        return namedClosure("<capture>") { obj ->
            if (obj != NO_VALUE) {
                out.add(obj)
            }
            true
        }
    }

    static Closure<Boolean> anyOf(Closure<?>... closures) {
        return namedClosure("any of " + Arrays.toString(closures)) { obj ->
            closures.any { Closure<?> it ->
                try {
                    return it(calculateArg(obj, it))
                } catch (NotMatchedException ignored) {
                    return false
                }
            }
        }
    }

    static Closure<Boolean> allOf(Closure<?>... closures) {
        return namedClosure("all of " + Arrays.toString(closures)) { obj ->
            closures.every { Closure<?> it ->
                it(calculateArg(obj, it))
            }
        }
    }

    static <T> Closure<T> namedClosure(String name, Closure<T> first) {
        return namedClosure(GJMatch.toGString(name), first)
    }

    static <T> Closure<T> namedClosure(GString name, Closure<T> first) {
        return new NamedClosure<>(name, first)
    }

}
