package com.github.artyomcool.gjmatch;

import java.util.Arrays;

import static com.github.artyomcool.gjmatch.GJMatch.JSON_NULL;

public class NotMatchedException extends Exception {

    private final Object toMatch;
    private final Object pattern;
    private final GJPath path;

    public NotMatchedException(String message) {
        super(message);
        toMatch = null;
        pattern = null;
        path = null;
    }

    public NotMatchedException(String message, Object toMatch, Object pattern, GJPath path) {
        super(createMessage(message, toMatch, pattern, path));
        this.toMatch = toMatch;
        this.pattern = pattern;
        this.path = path;
    }

    public NotMatchedException(String message, Object toMatch, Object pattern, GJPath path, NotMatchedException e) {
        super(createMessage(message, toMatch, pattern, path), e);
        this.toMatch = toMatch;
        this.pattern = pattern;
        this.path = path;
    }

    public Object getToMatch() {
        return toMatch;
    }

    public Object getPattern() {
        return pattern;
    }

    public GJPath getPath() {
        return path;
    }

    private static String createMessage(String message, Object toMatch, Object pattern, GJPath path) {
        if (toMatch instanceof String) {
            toMatch = "\"" + toMatch + "\"";
        } else if (toMatch == JSON_NULL) {
            toMatch = null;
        } else if (toMatch != null && toMatch.getClass().isArray()) {
            toMatch = Arrays.toString((Object[]) toMatch);
        }
        return message + ": expected " + pattern + ", found " + toMatch + ", path: " + path;
    }
}
