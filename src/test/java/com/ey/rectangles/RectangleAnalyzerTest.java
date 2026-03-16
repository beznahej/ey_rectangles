package com.ey.rectangles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
