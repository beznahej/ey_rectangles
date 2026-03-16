package com.ey.rectangles;

import java.util.Set;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        if (args.length != 8) {
            printUsage();
            return;
        }

        Rectangle first = buildRectangle(args, 0);
        Rectangle second = buildRectangle(args, 4);
        Set<Point> intersections = RectangleAnalyzer.intersectionPoints(first, second);

        System.out.println("Rectangle A: " + first);
        System.out.println("Rectangle B: " + second);
        System.out.println("A contains B: " + RectangleAnalyzer.contains(first, second));
        System.out.println("B contains A: " + RectangleAnalyzer.contains(second, first));
        System.out.println("Adjacency: " + RectangleAnalyzer.adjacencyType(first, second));
        System.out.println("Intersection points: " + intersections);
    }

    private static Rectangle buildRectangle(String[] args, int startIndex) {
        return new Rectangle(
                Double.parseDouble(args[startIndex]),
                Double.parseDouble(args[startIndex + 1]),
                Double.parseDouble(args[startIndex + 2]),
                Double.parseDouble(args[startIndex + 3])
        );
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  mvn exec:java -Dexec.args=\"minX1 minY1 maxX1 maxY1 minX2 minY2 maxX2 maxY2\"");
        System.out.println("Example:");
        System.out.println("  mvn exec:java -Dexec.args=\"0 0 10 5 4 -2 8 3\"");
    }
}
