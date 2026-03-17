package com.ey.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RectangleTest {

    @Test
    void calculatesWidthAndHeight() {
        Rectangle rectangle = new Rectangle(1.0, 2.0, 6.5, 9.5);

        assertEquals(5.5, rectangle.width());
        assertEquals(7.5, rectangle.height());
    }

    @Test
    void rejectsInvalidHorizontalBounds() {
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(4.0, 0.0, 4.0, 3.0));
    }

    @Test
    void rejectsInvalidVerticalBounds() {
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(0.0, 5.0, 3.0, 5.0));
    }
}
