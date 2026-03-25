import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Stateless geometry helpers for classifying relationships between two
 * axis-aligned rectangles.
 */
public final class RectangleAnalyzer {

    private static final double EPSILON = 1.0e-9;

    private RectangleAnalyzer() {
    }

    /**
     * Returns {@code true} when {@code inner} lies strictly inside {@code outer}.
     * Boundary contact does not count as containment.
     * Extension point: switch to inclusive comparisons here if business rules
     * later decide that boundary touch should still count as containment.
     */
    public static boolean contains(Rectangle outer, Rectangle inner) {
        Objects.requireNonNull(outer, "outer must not be null");
        Objects.requireNonNull(inner, "inner must not be null");

        return inner.minX() > outer.minX()
                && inner.maxX() < outer.maxX()
                && inner.minY() > outer.minY()
                && inner.maxY() < outer.maxY();
    }

    /**
     * Returns the discrete points where the rectangle boundaries cross.
     * Shared edges and corner touches are intentionally excluded.
     * Extension point: revise this method if shared boundary segments should
     * also count as intersections under a different business definition.
     */
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

    /**
     * Classifies side-sharing adjacency and returns {@link AdjacencyType#NONE}
     * when the rectangles overlap by area, only touch at a corner, or are disjoint.
     */
    public static AdjacencyType adjacencyType(Rectangle first, Rectangle second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        if (overlapsByArea(first, second)) {
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

    /**
     * Computes the full rectangle analysis in one pass for downstream consumers.
     * Extension point: populate any future derived outputs here, such as
     * overlap area, before returning the analysis record.
     */
    public static RectangleAnalysis analyze(Rectangle first, Rectangle second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        boolean firstContainsSecond = contains(first, second);
        boolean secondContainsFirst = contains(second, first);
        AdjacencyType adjacency = adjacencyType(first, second);
        Set<Point> intersections = intersectionPoints(first, second);
        boolean hasIntersections = !intersections.isEmpty();
        boolean areaOverlap = overlapsByArea(first, second);
        boolean cornerTouchOnly = touchesAtCornerOnly(first, second);
        RelationshipType relationship = classifyRelationship(
                firstContainsSecond,
                secondContainsFirst,
                adjacency,
                hasIntersections,
                areaOverlap,
                cornerTouchOnly
        );

        return new RectangleAnalysis(
                firstContainsSecond,
                secondContainsFirst,
                adjacency,
                intersections,
                relationship
        );
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

    private static RelationshipType classifyRelationship(boolean firstContainsSecond,
                                                         boolean secondContainsFirst,
                                                         AdjacencyType adjacency,
                                                         boolean hasIntersections,
                                                         boolean areaOverlap,
                                                         boolean cornerTouchOnly) {
        // Extension point: reorder these checks if business precedence changes.
        if (firstContainsSecond) {
            return RelationshipType.FIRST_CONTAINS_SECOND;
        }
        if (secondContainsFirst) {
            return RelationshipType.SECOND_CONTAINS_FIRST;
        }
        if (adjacency != AdjacencyType.NONE) {
            return RelationshipType.ADJACENT;
        }
        if (hasIntersections) {
            return RelationshipType.BOUNDARY_INTERSECTION;
        }
        if (areaOverlap) {
            return RelationshipType.AREA_OVERLAP;
        }
        if (cornerTouchOnly) {
            return RelationshipType.CORNER_TOUCH;
        }
        return RelationshipType.DISJOINT;
    }

    private static boolean overlapsByArea(Rectangle first, Rectangle second) {
        return overlapLength(first.minX(), first.maxX(), second.minX(), second.maxX()) > 0.0
                && overlapLength(first.minY(), first.maxY(), second.minY(), second.maxY()) > 0.0;
    }

    private static boolean touchesAtCornerOnly(Rectangle first, Rectangle second) {
        boolean xBoundaryTouch = touches(first.maxX(), second.minX()) || touches(second.maxX(), first.minX());
        boolean yBoundaryTouch = touches(first.maxY(), second.minY()) || touches(second.maxY(), first.minY());

        return xBoundaryTouch
                && yBoundaryTouch
                && overlapLength(first.minX(), first.maxX(), second.minX(), second.maxX()) == 0.0
                && overlapLength(first.minY(), first.maxY(), second.minY(), second.maxY()) == 0.0;
    }

    private static double overlapLength(double firstMin, double firstMax, double secondMin, double secondMax) {
        double overlap = Math.min(firstMax, secondMax) - Math.max(firstMin, secondMin);
        return overlap > EPSILON ? overlap : 0.0;
    }

    private static boolean isStrictlyBetween(double value, double min, double max) {
        return value > min + EPSILON && value < max - EPSILON;
    }

    private static boolean touches(double first, double second) {
        return sameValue(first, second);
    }

    private static boolean sameValue(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }
}
