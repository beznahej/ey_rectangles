package com.ey.rectangles;

public record Rectangle(double minX, double minY, double maxX, double maxY) {

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

