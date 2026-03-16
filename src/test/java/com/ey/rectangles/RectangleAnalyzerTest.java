package com.ey.rectangles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class RectangleAnalyzerTest {

    @Test
    void detectsStrictContainment() {
        Rectangle outer = new Rectangle(0.0, 0.0, 10.0, 10.0);
        Rectangle inner = new Rectangle(2.0, 2.0, 4.0, 4.0);

        assertTrue(RectangleAnalyzer.contains(outer, inner));
    }

    @Test
    void rejectsContainmentWhenRectanglesTouchTheBoundary() {
        Rectangle outer = new Rectangle(0.0, 0.0, 10.0, 10.0);
        Rectangle touching = new Rectangle(0.0, 2.0, 4.0, 4.0);

        assertFalse(RectangleAnalyzer.contains(outer, touching));
    }

    @Test
    void rejectsIdenticalRectanglesAsContainment() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(0.0, 0.0, 4.0, 4.0);

        assertFalse(RectangleAnalyzer.contains(first, second));
    }

    @Test
    void returnsNoIntersectionPointsForContainedRectangles() {
        Rectangle outer = new Rectangle(0.0, 0.0, 10.0, 10.0);
        Rectangle inner = new Rectangle(2.0, 2.0, 4.0, 4.0);

        assertEquals(Set.of(), RectangleAnalyzer.intersectionPoints(outer, inner));
    }

    @Test
    void returnsTwoIntersectionPointsForPartialOverlap() {
        Rectangle first = new Rectangle(0.0, 0.0, 10.0, 5.0);
        Rectangle second = new Rectangle(4.0, -2.0, 8.0, 3.0);

        assertEquals(
                Set.of(new Point(4.0, 0.0), new Point(8.0, 0.0)),
                RectangleAnalyzer.intersectionPoints(first, second)
        );
    }

    @Test
    void returnsFourIntersectionPointsWhenBoundariesCrossOnAllSidesOfTheOverlap() {
        Rectangle first = new Rectangle(0.0, 0.0, 10.0, 4.0);
        Rectangle second = new Rectangle(4.0, -2.0, 8.0, 6.0);

        assertEquals(
                Set.of(
                        new Point(4.0, 0.0),
                        new Point(8.0, 0.0),
                        new Point(4.0, 4.0),
                        new Point(8.0, 4.0)
                ),
                RectangleAnalyzer.intersectionPoints(first, second)
        );
    }

    @Test
    void returnsNoIntersectionPointsForSharedSideAdjacency() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 0.0, 6.0, 4.0);

        assertEquals(Set.of(), RectangleAnalyzer.intersectionPoints(first, second));
    }

    @Test
    void returnsNoIntersectionPointsForCornerTouchOnly() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 4.0, 6.0, 6.0);

        assertEquals(Set.of(), RectangleAnalyzer.intersectionPoints(first, second));
    }

    @Test
    void detectsProperAdjacency() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 0.0, 6.0, 4.0);

        assertEquals(AdjacencyType.PROPER, RectangleAnalyzer.adjacencyType(first, second));
    }

    @Test
    void detectsSubLineAdjacency() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 1.0, 7.0, 3.0);

        assertEquals(AdjacencyType.SUB_LINE, RectangleAnalyzer.adjacencyType(first, second));
    }

    @Test
    void detectsPartialAdjacency() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 2.0, 7.0, 6.0);

        assertEquals(AdjacencyType.PARTIAL, RectangleAnalyzer.adjacencyType(first, second));
    }

    @Test
    void rejectsAdjacencyWhenRectanglesOverlapByArea() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(3.0, 1.0, 6.0, 3.0);

        assertEquals(AdjacencyType.NONE, RectangleAnalyzer.adjacencyType(first, second));
    }

    @Test
    void rejectsAdjacencyForSeparatedRectangles() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(5.0, 1.0, 7.0, 3.0);

        assertEquals(AdjacencyType.NONE, RectangleAnalyzer.adjacencyType(first, second));
    }

    @Test
    void rejectsAdjacencyForCornerTouchOnly() {
        Rectangle first = new Rectangle(0.0, 0.0, 4.0, 4.0);
        Rectangle second = new Rectangle(4.0, 4.0, 6.0, 6.0);

        assertEquals(AdjacencyType.NONE, RectangleAnalyzer.adjacencyType(first, second));
    }
}
