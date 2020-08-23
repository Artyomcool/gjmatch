package com.github.artyomcool.gjmatch;

import groovy.lang.Closure;
import groovy.lang.GString;
import org.codehaus.groovy.runtime.GStringImpl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.artyomcool.gjmatch.GJMatchUtils.parseGString;
import static com.github.artyomcool.gjmatch.GJMatchUtils.parseString;

public class GJMatch {

    public static final Object NO_VALUE = new Object();
    public static final Object JSON_NULL = new Object() {
        @Override
        public String toString() {
            return "null";
        }
    };
    static final Object DEFAULT_MATCH_KEY = new Object();

    private final Object pattern;

    public static void match(String toMatch, String pattern) throws NotMatchedException {
        matchInternal(parseString(toMatch), GJMatch.toGString(pattern));
    }

    public static void match(String toMatch, GString pattern) throws NotMatchedException {
        matchInternal(parseString(toMatch), pattern);
    }

    private static void matchInternal(Object e, GString pattern) throws NotMatchedException {
        matchInternal(e, parseGString(pattern));
    }

    private static void matchInternal(Object e, Object pattern) throws NotMatchedException {
        new GJMatch(pattern).match(e);
    }

    GJMatch(Object pattern) {
        this.pattern = pattern;
    }

    public static GString toGString(String pattern) {
        return new GStringImpl(new Object[]{}, new String[]{pattern});
    }

    void match(Object toMatch) throws NotMatchedException {
        match(toMatch, pattern, new GJPath.Builder());
    }

    private void match(Object toMatch, Object pattern, GJPath.Builder path) throws NotMatchedException {
        if (pattern == null) {
            return;
        }

        if (pattern instanceof Closure) {
            validateWithClosure(toMatch, (Closure<?>) pattern, path);
            return;
        }

        if (toMatch == null) {
            throw validateFail("Not found", toMatch, pattern, path);
        }

        if (toMatch instanceof List) {
            if (!(pattern instanceof List)) {
                throw validateFail("Not array expected", toMatch, pattern, path);
            }

            List<?> toMatchList = (List<?>) toMatch;
            List<?> patternList = (List<?>) pattern;

            validateListEquals(toMatchList, patternList, path);
        } else if (toMatch instanceof Map) {
            if (!(pattern instanceof Map)) {
                throw validateFail("Object expected", toMatch, pattern, path);
            }
            validateMapEquals((Map<?, ?>) toMatch, (Map<?, ?>) pattern, path);
        } else if (toMatch == JSON_NULL) {
            if (pattern != JSON_NULL) {
                throw validateFail("Not null expected", toMatch, pattern, path);
            }
        } else {
            if (pattern instanceof Map) {
                throw validateFail("Object expected", toMatch, pattern, path);
            }
            if (pattern instanceof List) {
                throw validateFail("List expected", toMatch, pattern, path);
            }
            if (pattern == JSON_NULL) {
                throw validateFail("Null expected", toMatch, pattern, path);
            }
            validateLenientEquals(toMatch, pattern, path);
        }
    }

    private void validateWithClosure(Object toMatch, Closure<?> pattern, GJPath.Builder path) throws NotMatchedException {
        Object result;
        int params = pattern.getMaximumNumberOfParameters();
        if (params == 0) {
             result = patternCall(toMatch, pattern, path);
        } else if (params == 1) {
            Object arg = calculateArg(toMatch, pattern, path);
            result = patternCall(toMatch, pattern, path, arg);
        } else {
            throw validateFail("Wrong closure number params (" + params + ")", toMatch, pattern, path);
        }

        if (result instanceof Boolean) {
            if (!(Boolean) result) {
                throw validateFail("Closure mismatched", toMatch, pattern, path);
            }
        }
    }

    public static Object calculateArg(Object toMatch, Closure<?> pattern) throws NotMatchedException {
        return calculateArg(toMatch, pattern, new GJPath.Builder());
    }

    public static Object calculateArg(Object toMatch, Closure<?> pattern, GJPath.Builder path)
            throws NotMatchedException {
        return calculateArg(toMatch, pattern, path, pattern.getParameterTypes()[0]);
    }

    public static Object calculateArg(Object toMatch, Closure<?> pattern, GJPath.Builder path, Class<?> parameterType)
            throws NotMatchedException {
        if (toMatch == JSON_NULL && parameterType == Object.class) {
            return null;
        }

        if (parameterType.isInstance(toMatch)) {
            return toMatch;
        }

        throw validateFail("Can't cast " + (toMatch == null ? null : toMatch.getClass()) + " to " + parameterType, toMatch, pattern, path);
    }

    private Object patternCall(Object toMatch, Closure<?> pattern, GJPath.Builder path, Object... args) throws NotMatchedException {
        try {
            return patternCall(pattern, args);
        } catch (NotMatchedException e) {
            if (e.getPath() == null) {
                throw new NotMatchedException(e.getMessage(), toMatch, pattern, path.build());
            }
            throw new NotMatchedException("Nested mismatch", toMatch, pattern, path.build(), e);
        }
    }

    @SuppressWarnings("RedundantThrows")
    private Object patternCall(Closure<?> pattern, Object... args) throws NotMatchedException {
        return pattern.call(args);
    }

    private void validateListEquals(List<?> toMatchList, List<?> patternList, GJPath.Builder path) throws NotMatchedException {
        int closureIndex = -1;
        int i = 0;
        for (Object obj : patternList) {
            if (obj instanceof Closure) {
                Class<?>[] parameterTypes = ((Closure<?>) obj).getParameterTypes();
                if (parameterTypes.length == 1) {
                    if (parameterTypes[0].isArray()) {
                        if (closureIndex != -1) {
                            throw validateFail("Multiple varargs closures unsupported",
                                    toMatchList,
                                    patternList,
                                    path
                            );
                        }

                        closureIndex = i;
                    }
                }
            }
            i++;
        }

        if (toMatchList.size() != patternList.size()) {
            if (closureIndex == -1) {
                throw validateFail("Size mismatched: " + toMatchList.size() + ", expected " + patternList.size(),
                        toMatchList,
                        patternList,
                        path
                );
            }
            if (patternList.size() - 1 > toMatchList.size()) {
                throw validateFail("Size mismatched: " + toMatchList.size() + ", expected at least " + (patternList.size() - 1),
                        toMatchList,
                        patternList,
                        path
                );
            }
        }

        if (closureIndex == -1) {
            Iterator<?> patternIterator = patternList.iterator();

            i = 0;
            for (Object object : toMatchList) {
                path.push(i);
                match(object, patternIterator.next(), path);
                path.pop();
                i++;
            }
        } else {
            List<?> startPattern = patternList.subList(0, closureIndex);
            Object varargClosure = patternList.get(closureIndex);
            List<?> endPattern = patternList.subList(closureIndex + 1, patternList.size());

            List<?> startToMatch = toMatchList.subList(0, startPattern.size());
            List<?> middleToMatch = toMatchList.subList(startPattern.size(), toMatchList.size() - endPattern.size());
            List<?> endToMatch = toMatchList.subList(toMatchList.size() - endPattern.size(), toMatchList.size());

            Iterator<?> startPatternIterator = startPattern.iterator();
            Iterator<?> endPatternIterator = endPattern.iterator();

            i = 0;
            for (Object object : startToMatch) {
                path.push(i);
                match(object, startPatternIterator.next(), path);
                path.pop();
                i++;
            }

            path.push(i, toMatchList.size() - endPattern.size());
            matchVararg(middleToMatch, (Closure<?>) varargClosure, path);
            path.pop();

            i = toMatchList.size() - endPattern.size();
            for (Object object : endToMatch) {
                path.push(i);
                match(object, endPatternIterator.next(), path);
                path.pop();
                i++;
            }
        }
    }

    private void matchVararg(List<?> toMatch, Closure<?> varArgClosure, GJPath.Builder path) throws NotMatchedException {
        Class<?> type = varArgClosure.getParameterTypes()[0];
        Class<?> componentType = type.getComponentType();
        Object array = Array.newInstance(componentType, toMatch.size());
        int i = 0;
        for (Object objToMatch : toMatch) {
            path.clarifySubarray(i);
            Array.set(array, i++, calculateArg(objToMatch, varArgClosure, path, componentType));
            path.declarifySubarray();
        }
        Object result = patternCall(toMatch, varArgClosure, path, (Object[]) array);
        if (result instanceof Boolean) {
            if (!(Boolean) result) {
                throw validateFail("Array slice mismatched", toMatch, pattern, path);
            }
        }
    }

    private void validateMapEquals(Map<?, ?> toMatch, Map<?, ?> pattern, GJPath.Builder path) throws NotMatchedException {
        Set<Object> verified = new HashSet<>();
        Object defaultMatch = null;
        for (Map.Entry<?, ?> entry : pattern.entrySet()) {
            if (entry.getKey() == DEFAULT_MATCH_KEY) {
                defaultMatch = entry.getValue();
            } else {
                path.push(entry.getKey().toString());
                Object o = toMatch.get(entry.getKey());
                match(o == null ? NO_VALUE : o, entry.getValue(), path);
                path.pop();
                verified.add(entry.getKey());
            }
        }

        if (defaultMatch != null) {
            for (Map.Entry<?, ?> entry : toMatch.entrySet()) {
                if (!verified.contains(entry.getKey())) {
                    path.push(entry.getKey().toString());
                    if (defaultMatch instanceof Closure &&
                            ((Closure<?>) defaultMatch).getMaximumNumberOfParameters() == 2) {
                        validateWithClosure(entry.getValue(), ((Closure<?>) defaultMatch).curry(entry.getKey()), path);
                    } else {
                        match(entry.getValue(), defaultMatch, path);
                    }
                    path.pop();
                    verified.add(entry.getKey());
                }
            }
        }
    }

    private void validateLenientEquals(Object toMatch, Object pattern, GJPath.Builder path) throws NotMatchedException {
        if (pattern.equals(toMatch)) {
            return;
        }

        if (pattern instanceof String || pattern instanceof GString) {
            if (toMatch instanceof String || toMatch instanceof GString) {
                if (toMatch.toString().equals(pattern.toString())) {
                    return;
                }
                throw validateFail("Strings mismatched", toMatch, pattern, path);
            }
            throw validateFail("String expected, but " + toMatch.getClass() + " found", toMatch, pattern, path);
        }

        if (pattern instanceof Number) {
            if (!(toMatch instanceof Number)) {
                throw validateFail("Number expected, but " + toMatch.getClass() + " found", toMatch, pattern, path);
            }
            if (pattern instanceof Double || pattern instanceof Float) {
                if (((Number) pattern).doubleValue() == ((Number) toMatch).doubleValue()) {
                    return;
                }
                throw validateFail("Numbers mismatched", toMatch, pattern, path);
            }
            if (toMatch instanceof Double || toMatch instanceof Float) {
                throw validateFail("Ordinal expected, floating-point found", toMatch, pattern, path);
            }

            if (((Number) pattern).longValue() == ((Number) toMatch).longValue()) {
                return;
            }
            throw validateFail("Numbers mismatched", toMatch, pattern, path);
        }

        throw validateFail("Values mismatched", toMatch, pattern, path);
    }

    private static NotMatchedException validateFail(String message, Object toMatch, Object pattern, GJPath.Builder path)
            throws NotMatchedException {

        throw new NotMatchedException(
                message,
                toMatch,
                pattern,
                path.build()
        );
    }

}
