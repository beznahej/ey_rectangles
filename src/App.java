import java.util.Map;

/**
 * Small CLI/demo wrapper around {@link RectangleAnalyzer}. It is responsible for
 * parsing command-line arguments and formatting the analysis results for display.
 */
public final class App {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String CUSTOM_ANALYSIS_DESCRIPTION = "Analysis for the rectangles provided on the command line.";
    private static final Map<String, DemoCase> DEMOS = DemoCatalog.all();

    private App() {
    }

    public static void main(String[] args) {
        System.out.print(renderForArguments(args));
    }

    /**
     * Produces the full console output for the provided arguments. Returning the
     * rendered text instead of printing inline keeps the CLI logic easy to test.
     */
    static String renderForArguments(String[] args) {
        StringBuilder output = new StringBuilder();

        try {
            if (args.length == 0 || (args.length == 1 && "--demo".equals(args[0]))) {
                appendDemoSuite(output);
            } else if (args.length == 1 && isHelpArgument(args[0])) {
                appendUsage(output);
            } else if (args.length == 2 && "--demo".equals(args[0])) {
                appendNamedDemo(output, args[1]);
            } else if (args.length == 9 && "--rects".equals(args[0])) {
                appendAnalysis(output, "Custom Analysis", CUSTOM_ANALYSIS_DESCRIPTION, buildRectangle(args, 1), buildRectangle(args, 5));
            } else if (args.length == 8) {
                appendAnalysis(output, "Custom Analysis", CUSTOM_ANALYSIS_DESCRIPTION, buildRectangle(args, 0), buildRectangle(args, 4));
            } else {
                appendLine(output, "Unrecognized arguments.");
                appendBlankLine(output);
                appendUsage(output);
            }
        } catch (IllegalArgumentException exception) {
            appendLine(output, "Invalid input: " + exception.getMessage());
            appendBlankLine(output);
            appendUsage(output);
        }

        return output.toString();
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

    /**
     * Renders the analyzer output for one rectangle pair.
     */
    private static void appendAnalysis(StringBuilder output,
                                       String title,
                                       String description,
                                       Rectangle first,
                                       Rectangle second) {
        RectangleAnalysis analysis = RectangleAnalyzer.analyze(first, second);

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

    /**
     * Converts the analyzer's top-level relationship type into a CLI sentence.
     */
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

    private static boolean isHelpArgument(String argument) { return "--help".equals(argument) || "-h".equals(argument); }
    private static void appendLine(StringBuilder output, String line) { output.append(line).append(NEW_LINE); }
    private static void appendBlankLine(StringBuilder output) { output.append(NEW_LINE); }
}
