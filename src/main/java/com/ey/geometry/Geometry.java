package com.ey.geometry;

record Point(double x, double y) {
}

enum AdjacencyType {
    NONE,
    PROPER,
    SUB_LINE,
    PARTIAL
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
