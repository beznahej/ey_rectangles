package com.ey.rectangles;

import java.util.Objects;
import java.util.Set;

public final class RectangleAnalyzer {

    private RectangleAnalyzer() {
    }

    public static boolean contains(Rectangle outer, Rectangle inner) {
        Objects.requireNonNull(outer, "outer must not be null");
        Objects.requireNonNull(inner, "inner must not be null");

        return inner.minX() > outer.minX()
                && inner.maxX() < outer.maxX()
                && inner.minY() > outer.minY()
                && inner.maxY() < outer.maxY();
    }

    public static Set<Point> intersectionPoints(Rectangle first, Rectangle second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        throw new UnsupportedOperationException("Intersection point detection is not implemented yet");
    }

    public static AdjacencyType adjacencyType(Rectangle first, Rectangle second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        throw new UnsupportedOperationException("Adjacency classification is not implemented yet");
    }
}

