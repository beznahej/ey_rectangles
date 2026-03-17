package com.ey.rectangles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class App {

    private static final double EPSILON = 1.0e-9;
    private static final String NEW_LINE = System.lineSeparator();
    private static final Map<String, DemoCase> DEMOS = createDemos();

    private App() {
    }

    public static void main(String[] args) {
        System.out.print(renderForArguments(args));
    }

    static String renderForArguments(String[] args) {
        StringBuilder output = new StringBuilder();

        try {
            if (args.length == 0) {
                appendDemoSuite(output);
                return output.toString();
            }

            if (args.length == 1 && isHelpArgument(args[0])) {
                appendUsage(output);
                return output.toString();
            }

            if (args.length == 1 && "--demo".equals(args[0])) {
                appendDemoSuite(output);
                return output.toString();
            }

            if (args.length == 2 && "--demo".equals(args[0])) {
                appendNamedDemo(output, args[1]);
                return output.toString();
            }

            if (args.length == 9 && "--rects".equals(args[0])) {
                appendCustomAnalysis(output, buildRectangle(args, 1), buildRectangle(args, 5));
                return output.toString();
            }

            if (args.length == 8) {
                appendCustomAnalysis(output, buildRectangle(args, 0), buildRectangle(args, 4));
                return output.toString();
            }

            appendLine(output, "Unrecognized arguments.");
            appendBlankLine(output);
            appendUsage(output);
            return output.toString();
        } catch (IllegalArgumentException exception) {
            appendLine(output, "Invalid input: " + exception.getMessage());
            appendBlankLine(output);
            appendUsage(output);
            return output.toString();
        }
    }

    private static void appendDemoSuite(StringBuilder output) {
        appendLine(output, "Rectangles Demo Suite");
        appendLine(output, "=====================");
        appendLine(output, "Running the built-in scenarios that mirror the main requirement types.");
        appendBlankLine(output);

        int demoIndex = 1;
        int demoCount = DEMOS.size();
        for (DemoCase demo : DEMOS.values()) {
            appendAnalysis(output, demoIndex + ". " + demo.title(), demo.description(), demo.first(), demo.second());
            if (demoIndex < demoCount) {
                appendBlankLine(output);
            }
            demoIndex++;
        }

        appendBlankLine(output);
        appendLine(output, "Tip: run `mvn exec:java -Dexec.args=\"--help\"` to see all CLI options.");
    }

    private static void appendNamedDemo(StringBuilder output, String demoName) {
        if ("all".equalsIgnoreCase(demoName)) {
            appendDemoSuite(output);
            return;
        }

        DemoCase demo = DEMOS.get(demoName);
        if (demo == null) {
            throw new IllegalArgumentException("unknown demo `" + demoName + "`");
        }

        appendAnalysis(output, demo.title(), demo.description(), demo.first(), demo.second());
    }

    private static void appendCustomAnalysis(StringBuilder output, Rectangle first, Rectangle second) {
        appendAnalysis(output, "Custom Analysis", "Analysis for the rectangles provided on the command line.", first, second);
    }

    private static void appendAnalysis(StringBuilder output,
                                       String title,
                                       String description,
                                       Rectangle first,
                                       Rectangle second) {
        boolean firstContainsSecond = RectangleAnalyzer.contains(first, second);
        boolean secondContainsFirst = RectangleAnalyzer.contains(second, first);
        AdjacencyType adjacency = RectangleAnalyzer.adjacencyType(first, second);
        Set<Point> intersections = RectangleAnalyzer.intersectionPoints(first, second);

        appendLine(output, title);
        appendLine(output, "-".repeat(title.length()));
        appendLine(output, description);
        appendLine(output, "Rectangle A: " + formatRectangle(first));
        appendLine(output, "Rectangle B: " + formatRectangle(second));
        appendLine(output, "A contains B: " + firstContainsSecond);
        appendLine(output, "B contains A: " + secondContainsFirst);
        appendLine(output, "Adjacency: " + adjacency);
        appendLine(output, "Intersection points (" + intersections.size() + "): " + intersections);
        appendLine(output, "Relationship: " + summarizeRelationship(first, second, firstContainsSecond, secondContainsFirst, adjacency, intersections));
    }

    private static String summarizeRelationship(Rectangle first,
                                                Rectangle second,
                                                boolean firstContainsSecond,
                                                boolean secondContainsFirst,
                                                AdjacencyType adjacency,
                                                Set<Point> intersections) {
        if (firstContainsSecond) {
            return "Rectangle A strictly contains Rectangle B.";
        }
        if (secondContainsFirst) {
            return "Rectangle B strictly contains Rectangle A.";
        }
        if (adjacency != AdjacencyType.NONE) {
            return "The rectangles are adjacent with " + adjacency + " side sharing.";
        }
        if (!intersections.isEmpty()) {
            return "The rectangles overlap and their boundaries cross at " + intersections.size() + " discrete point(s).";
        }
        if (overlapsByArea(first, second)) {
            return "The rectangles overlap by area, which is not adjacency because adjacency requires boundary contact without interior overlap.";
        }
        if (touchesAtCornerOnly(first, second)) {
            return "The rectangles only touch at a corner, which this solution treats as neither adjacency nor intersection.";
        }
        return "The rectangles are disjoint.";
    }

    private static Rectangle buildRectangle(String[] args, int startIndex) {
        return new Rectangle(
                Double.parseDouble(args[startIndex]),
                Double.parseDouble(args[startIndex + 1]),
                Double.parseDouble(args[startIndex + 2]),
                Double.parseDouble(args[startIndex + 3])
        );
    }

    private static void appendUsage(StringBuilder output) {
        appendLine(output, "Usage:");
        appendLine(output, "  mvn exec:java");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--help\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--demo\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--demo partial-adjacency\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--rects minX1 minY1 maxX1 maxY1 minX2 minY2 maxX2 maxY2\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"0 0 10 5 4 -2 8 3\"");
        appendBlankLine(output);
        appendLine(output, "Built-in demos:");
        for (Map.Entry<String, DemoCase> entry : DEMOS.entrySet()) {
            appendLine(output, "  " + entry.getKey() + " - " + entry.getValue().description());
        }
    }

    private static String formatRectangle(Rectangle rectangle) {
        return "[minX=" + rectangle.minX()
                + ", minY=" + rectangle.minY()
                + ", maxX=" + rectangle.maxX()
                + ", maxY=" + rectangle.maxY()
                + ", width=" + rectangle.width()
                + ", height=" + rectangle.height()
                + "]";
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

    private static boolean touches(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }

    private static boolean isHelpArgument(String argument) {
        return "--help".equals(argument) || "-h".equals(argument);
    }

    private static void appendLine(StringBuilder output, String line) {
        output.append(line).append(NEW_LINE);
    }

    private static void appendBlankLine(StringBuilder output) {
        output.append(NEW_LINE);
    }

    private static Map<String, DemoCase> createDemos() {
        Map<String, DemoCase> demos = new LinkedHashMap<>();
        demos.put("intersection", new DemoCase(
                "Intersection",
                "Partial overlap with two boundary crossing points.",
                new Rectangle(0.0, 0.0, 10.0, 5.0),
                new Rectangle(4.0, -2.0, 8.0, 3.0)
        ));
        demos.put("four-point-intersection", new DemoCase(
                "Four-Point Intersection",
                "Overlap where the boundaries cross at four discrete points.",
                new Rectangle(0.0, 0.0, 10.0, 4.0),
                new Rectangle(4.0, -2.0, 8.0, 6.0)
        ));
        demos.put("containment", new DemoCase(
                "Containment",
                "Rectangle B is strictly contained inside Rectangle A.",
                new Rectangle(0.0, 0.0, 10.0, 10.0),
                new Rectangle(2.0, 2.0, 4.0, 4.0)
        ));
        demos.put("proper-adjacency", new DemoCase(
                "Proper Adjacency",
                "Touching sides have the same full length.",
                new Rectangle(0.0, 0.0, 4.0, 4.0),
                new Rectangle(4.0, 0.0, 6.0, 4.0)
        ));
        demos.put("sub-line-adjacency", new DemoCase(
                "Sub-Line Adjacency",
                "One touching side is fully contained within the other.",
                new Rectangle(0.0, 0.0, 4.0, 4.0),
                new Rectangle(4.0, 1.0, 7.0, 3.0)
        ));
        demos.put("partial-adjacency", new DemoCase(
                "Partial Adjacency",
                "The shared side segment is shorter than both touching sides.",
                new Rectangle(0.0, 0.0, 4.0, 4.0),
                new Rectangle(4.0, 2.0, 7.0, 6.0)
        ));
        demos.put("corner-touch", new DemoCase(
                "Corner Touch",
                "The rectangles meet at exactly one corner and nothing more.",
                new Rectangle(0.0, 0.0, 4.0, 4.0),
                new Rectangle(4.0, 4.0, 6.0, 6.0)
        ));
        demos.put("disjoint", new DemoCase(
                "Disjoint",
                "The rectangles are fully separated.",
                new Rectangle(0.0, 0.0, 4.0, 4.0),
                new Rectangle(6.0, 1.0, 8.0, 3.0)
        ));
        return demos;
    }

    private record DemoCase(String title, String description, Rectangle first, Rectangle second) {
    }
}
