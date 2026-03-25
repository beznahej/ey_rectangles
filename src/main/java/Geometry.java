import java.util.Set;

record Point(double x, double y) {
}

enum AdjacencyType {
    NONE,
    PROPER,
    SUB_LINE,
    PARTIAL
}

enum RelationshipType {
    FIRST_CONTAINS_SECOND,
    SECOND_CONTAINS_FIRST,
    ADJACENT,
    BOUNDARY_INTERSECTION,
    AREA_OVERLAP,
    CORNER_TOUCH,
    DISJOINT
}

// Extension point: add more derived outputs here, such as overlap area, if the
// analysis contract needs to return richer data.
record RectangleAnalysis(boolean firstContainsSecond,
                         boolean secondContainsFirst,
                         AdjacencyType adjacencyType,
                         Set<Point> intersectionPoints,
                         RelationshipType relationshipType) {
}

record Rectangle(double minX, double minY, double maxX, double maxY) {

    public Rectangle {
        if (maxX <= minX) {
            throw new IllegalArgumentException("maxX must be greater than minX");
        }
        if (maxY <= minY) {
            throw new IllegalArgumentException("maxY must be greater than minY");
        }
    }

    public double width() {
        return maxX - minX;
    }

    public double height() {
        return maxY - minY;
    }
}
