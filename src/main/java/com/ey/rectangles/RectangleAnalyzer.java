package com.ey.rectangles;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class RectangleAnalyzer {

    private static final double EPSILON = 1.0e-9;

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

        Set<Point> points = new LinkedHashSet<>();

        addVerticalHorizontalIntersection(points, first.minX(), first.minY(), first.maxY(), second.minY(), second.minX(), second.maxX());
        addVerticalHorizontalIntersection(points, first.minX(), first.minY(), first.maxY(), second.maxY(), second.minX(), second.maxX());
        addVerticalHorizontalIntersection(points, first.maxX(), first.minY(), first.maxY(), second.minY(), second.minX(), second.maxX());
        addVerticalHorizontalIntersection(points, first.maxX(), first.minY(), first.maxY(), second.maxY(), second.minX(), second.maxX());

        addVerticalHorizontalIntersection(points, second.minX(), second.minY(), second.maxY(), first.minY(), first.minX(), first.maxX());
        addVerticalHorizontalIntersection(points, second.minX(), second.minY(), second.maxY(), first.maxY(), first.minX(), first.maxX());
        addVerticalHorizontalIntersection(points, second.maxX(), second.minY(), second.maxY(), first.minY(), first.minX(), first.maxX());
        addVerticalHorizontalIntersection(points, second.maxX(), second.minY(), second.maxY(), first.maxY(), first.minX(), first.maxX());

        return points.stream()
                .sorted(Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    public static AdjacencyType adjacencyType(Rectangle first, Rectangle second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        if (hasPositiveOverlap(first.minX(), first.maxX(), second.minX(), second.maxX())
                && hasPositiveOverlap(first.minY(), first.maxY(), second.minY(), second.maxY())) {
            return AdjacencyType.NONE;
        }

        if (touches(first.maxX(), second.minX()) || touches(second.maxX(), first.minX())) {
            double sharedLength = overlapLength(first.minY(), first.maxY(), second.minY(), second.maxY());
            if (sharedLength > 0.0) {
                return classifyAdjacency(sharedLength, first.height(), second.height());
            }
        }

        if (touches(first.maxY(), second.minY()) || touches(second.maxY(), first.minY())) {
            double sharedLength = overlapLength(first.minX(), first.maxX(), second.minX(), second.maxX());
            if (sharedLength > 0.0) {
                return classifyAdjacency(sharedLength, first.width(), second.width());
            }
        }

        return AdjacencyType.NONE;
    }

    private static void addVerticalHorizontalIntersection(Set<Point> points,
                                                          double verticalX,
                                                          double verticalMinY,
                                                          double verticalMaxY,
                                                          double horizontalY,
                                                          double horizontalMinX,
                                                          double horizontalMaxX) {
        if (isStrictlyBetween(verticalX, horizontalMinX, horizontalMaxX)
                && isStrictlyBetween(horizontalY, verticalMinY, verticalMaxY)) {
            points.add(new Point(verticalX, horizontalY));
        }
    }

    private static AdjacencyType classifyAdjacency(double sharedLength, double firstSideLength, double secondSideLength) {
        if (sameValue(sharedLength, firstSideLength) && sameValue(sharedLength, secondSideLength)) {
            return AdjacencyType.PROPER;
        }
        if (sameValue(sharedLength, firstSideLength) || sameValue(sharedLength, secondSideLength)) {
            return AdjacencyType.SUB_LINE;
        }
        return AdjacencyType.PARTIAL;
    }

    private static boolean hasPositiveOverlap(double firstMin, double firstMax, double secondMin, double secondMax) {
        return overlapLength(firstMin, firstMax, secondMin, secondMax) > 0.0;
    }

    private static double overlapLength(double firstMin, double firstMax, double secondMin, double secondMax) {
        double overlap = Math.min(firstMax, secondMax) - Math.max(firstMin, secondMin);
        return overlap > EPSILON ? overlap : 0.0;
    }

    private static boolean isStrictlyBetween(double value, double min, double max) {
        return value > min + EPSILON && value < max - EPSILON;
    }

    private static boolean touches(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }

    private static boolean sameValue(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }
}
