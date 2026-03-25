import java.util.Map;

/**
 * Renders rectangle analysis results into CLI-friendly text.
 */
final class RectangleAnalysisFormatter {

    private static final String NEW_LINE = System.lineSeparator();

    private RectangleAnalysisFormatter() {
    }

    static String renderDemoSuite(Map<String, DemoCase> demos) {
        StringBuilder output = new StringBuilder();

        appendLine(output, "Rectangles Demo Suite");
        appendLine(output, "=====================");
        appendLine(output, "Running the built-in scenarios that mirror the main requirement types.");
        appendBlankLine(output);

        int demoIndex = 1;
        int demoCount = demos.size();
        for (DemoCase demo : demos.values()) {
            RectangleAnalysis analysis = RectangleAnalyzer.analyze(demo.first(), demo.second());
            appendAnalysis(output, demoIndex + ". " + demo.title(), demo.description(), demo.first(), demo.second(), analysis);
            if (demoIndex < demoCount) {
                appendBlankLine(output);
            }
            demoIndex++;
        }

        appendBlankLine(output);
        appendLine(output, "Tip: run `mvn exec:java -Dexec.args=\"--help\"` to see all CLI options.");
        return output.toString();
    }

    static String renderAnalysis(String title,
                                 String description,
                                 Rectangle first,
                                 Rectangle second,
                                 RectangleAnalysis analysis) {
        StringBuilder output = new StringBuilder();
        appendAnalysis(output, title, description, first, second, analysis);
        return output.toString();
    }

    static String renderUsage(Map<String, DemoCase> demos) {
        StringBuilder output = new StringBuilder();

        appendLine(output, "Usage:");
        appendLine(output, "  mvn exec:java");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--help\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--demo\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--demo partial-adjacency\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"--rects minX1 minY1 maxX1 maxY1 minX2 minY2 maxX2 maxY2\"");
        appendLine(output, "  mvn exec:java -Dexec.args=\"0 0 10 5 4 -2 8 3\"");
        appendBlankLine(output);
        appendLine(output, "Built-in demos:");
        for (Map.Entry<String, DemoCase> entry : demos.entrySet()) {
            appendLine(output, "  " + entry.getKey() + " - " + entry.getValue().description());
        }

        return output.toString();
    }

    static String renderUsageWithMessage(String message, Map<String, DemoCase> demos) {
        return message + NEW_LINE + NEW_LINE + renderUsage(demos);
    }

    private static void appendAnalysis(StringBuilder output,
                                       String title,
                                       String description,
                                       Rectangle first,
                                       Rectangle second,
                                       RectangleAnalysis analysis) {
        appendLine(output, title);
        appendLine(output, "-".repeat(title.length()));
        appendLine(output, description);
        appendLine(output, "Rectangle A: " + formatRectangle(first));
        appendLine(output, "Rectangle B: " + formatRectangle(second));
        appendLine(output, "A contains B: " + analysis.firstContainsSecond());
        appendLine(output, "B contains A: " + analysis.secondContainsFirst());
        appendLine(output, "Adjacency: " + analysis.adjacencyType());
        appendLine(output, "Intersection points (" + analysis.intersectionPoints().size() + "): " + analysis.intersectionPoints());
        appendLine(output, "Relationship: " + summarizeRelationship(analysis));
    }

    private static String summarizeRelationship(RectangleAnalysis analysis) {
        return switch (analysis.relationshipType()) {
            case FIRST_CONTAINS_SECOND -> "Rectangle A strictly contains Rectangle B.";
            case SECOND_CONTAINS_FIRST -> "Rectangle B strictly contains Rectangle A.";
            case ADJACENT -> "The rectangles are adjacent with " + analysis.adjacencyType() + " side sharing.";
            case BOUNDARY_INTERSECTION ->
                    "The rectangles overlap and their boundaries cross at " + analysis.intersectionPoints().size() + " discrete point(s).";
            case AREA_OVERLAP ->
                    "The rectangles overlap by area, which is not adjacency because adjacency requires boundary contact without interior overlap.";
            case CORNER_TOUCH ->
                    "The rectangles only touch at a corner, which this solution treats as neither adjacency nor intersection.";
            case DISJOINT -> "The rectangles are disjoint.";
        };
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

    private static void appendLine(StringBuilder output, String line) {
        output.append(line).append(NEW_LINE);
    }

    private static void appendBlankLine(StringBuilder output) {
        output.append(NEW_LINE);
    }
}
