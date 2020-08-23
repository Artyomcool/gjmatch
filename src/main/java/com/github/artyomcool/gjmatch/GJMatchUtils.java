package com.github.artyomcool.gjmatch;

import groovy.lang.Closure;
import groovy.lang.GString;
import org.apache.groovy.json.internal.JsonParserCharArray;
import org.codehaus.groovy.runtime.GStringImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GJMatchUtils {

    public static Object parseString(String json) {
        return convert(parseJson(json), Collections.emptyList());
    }

    static Object parseGString(GString jsonPattern) {
        jsonPattern = new GStringImpl(
                jsonPattern.getValues().clone(),
                jsonPattern.getStrings().clone()
        );
        List<Closure<?>> closures = extractClosures(jsonPattern);
        String jsonString = jsonPattern.toString();
        return convert(parseJson(jsonString), closures);
    }

    private static Object parseJson(String jsonString) {
        return new JsonParserCharArray().parse(jsonString);
    }

    private static List<Closure<?>> extractClosures(GString pattern) {
        List<Closure<?>> closures = new ArrayList<>();

        for (int i = 0; i < pattern.getValues().length; i++) {
            if (pattern.getValues()[i] instanceof Closure) {
                Closure<?> c = (Closure<?>) pattern.getValues()[i];
                if (c.getMaximumNumberOfParameters() == 1) {
                    pattern.getValues()[i] = "\"@@@$$$_" + closures.size() + "_$$$@@@\"";
                    closures.add(c);
                } else if (c.getMaximumNumberOfParameters() == 2) {
                    pattern.getValues()[i] = "\"@@@$$$_DEFAULT_KEY_$$$@@@\": \"@@@$$$_" + closures.size() + "_$$$@@@\"";
                    closures.add(c);
                }
            }
        }
        return closures;
    }

    @SuppressWarnings("unchecked")
    static Object convert(Object e, List<Closure<?>> closures) {
        if (e == null) {
            return GJMatch.JSON_NULL;
        }

        if (e instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object element : (List<?>) e) {
                result.add(convert(element, closures));
            }
            return result;
        }

        if (e instanceof Map) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> en : ((Map<String, Object>)e).entrySet()) {
                result.put(stringOrDefault(en.getKey()), convert(en.getValue(), closures));
            }
            return result;
        }

        if (e instanceof String) {
            return stringOrClosure((String) e, closures);
        }
        return e;
    }

    private static Object stringOrDefault(String key) {
        return key.equals("@@@$$$_DEFAULT_KEY_$$$@@@") ? GJMatch.DEFAULT_MATCH_KEY : key;
    }

    private static Object stringOrClosure(String value, List<Closure<?>> closures) {
        if (value.startsWith("@@@$$$_") && value.endsWith("_$$$@@@")) {
            value = value.substring("@@@$$$_".length(), value.length() - "_$$$@@@".length());
            return closures.get(Integer.parseInt(value));
        }
        return value;
    }
}
