package com.github.artyomcool.gjmatch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GJPath {

    private interface Segment {
    }

    private static class NameSegment implements Segment {
        private final String name;

        public NameSegment(String name) {
            this.name = name;
        }
    }

    private static class IndexSegment implements Segment {
        private final int index;
        final SubarraySegment subArray;

        public IndexSegment(int index) {
            this.index = index;
            this.subArray = null;
        }

        public IndexSegment(SubarraySegment subArray, int delta) {
            this.index = subArray.startIndex + delta;
            this.subArray = subArray;
        }
    }

    private static class SubarraySegment implements Segment {
        private final int startIndex;
        private final int endIndex;

        public SubarraySegment(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    public static class Builder {
        private final Deque<Segment> path = new ArrayDeque<>();

        public void push(int index) {
            path.addLast(new IndexSegment(index));
        }
        public void push(int startIndex, int endIndex) {
            path.addLast(new SubarraySegment(startIndex, endIndex));
        }

        public void push(String name) {
            path.addLast(new NameSegment(name));
        }

        public void pop() {
            path.removeLast();
        }

        public GJPath build() {
            return new GJPath(new ArrayList<>(path));
        }

        public void clarifySubarray(int delta) {
            SubarraySegment toClarify = (SubarraySegment) path.removeLast();
            path.addLast(new IndexSegment(toClarify, delta));
        }

        public void declarifySubarray() {
            IndexSegment toDeclarify = (IndexSegment) path.removeLast();
            path.addLast(toDeclarify.subArray);
        }
    }

    private final List<Segment> path;

    private GJPath(List<Segment> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("$");
        for (Segment segment : path) {
            if (segment instanceof NameSegment) {
                result.append(".").append(((NameSegment) segment).name);
            } else if (segment instanceof IndexSegment) {
                result.append("[").append(((IndexSegment) segment).index).append("]");
            } else if (segment instanceof SubarraySegment) {
                SubarraySegment subarray = (SubarraySegment) segment;
                result.append("[").append(subarray.startIndex).append("..").append(subarray.endIndex).append(")");
            }
        }
        return result.toString();
    }
}
