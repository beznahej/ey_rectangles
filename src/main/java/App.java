import java.util.Map;

/**
 * Small CLI/demo wrapper around {@link RectangleAnalyzer}. It is responsible for
 * parsing command-line arguments and dispatching to the rectangle analyzer.
 */
public final class App {

    private static final String CUSTOM_ANALYSIS_DESCRIPTION = "Analysis for the rectangles provided on the command line.";
    private static final Map<String, DemoCase> DEMOS = DemoCatalog.all();

    private App() {
    }

    public static void main(String[] args) {
        if (args.length == 1 && "--rest".equals(args[0])) {
            RectangleRestServer.start();
            return;
        }
        System.out.print(renderForArguments(args));
    }

    /**
     * Produces the full console output for the provided arguments. Returning the
     * rendered text instead of printing inline keeps the CLI logic easy to test.
     */
    static String renderForArguments(String[] args) {
        try {
            if (args.length == 0 || (args.length == 1 && "--demo".equals(args[0]))) {
                return RectangleAnalysisFormatter.renderDemoSuite(DEMOS);
            }
            if (args.length == 1 && isHelpArgument(args[0])) {
                return RectangleAnalysisFormatter.renderUsage(DEMOS);
            }
            if (args.length == 2 && "--demo".equals(args[0])) {
                return renderNamedDemo(args[1]);
            }
            if (args.length == 9 && "--rects".equals(args[0])) {
                return renderAnalysis(
                        "Custom Analysis",
                        CUSTOM_ANALYSIS_DESCRIPTION,
                        buildRectangle(args, 1),
                        buildRectangle(args, 5)
                );
            }
            if (args.length == 8) {
                return renderAnalysis(
                        "Custom Analysis",
                        CUSTOM_ANALYSIS_DESCRIPTION,
                        buildRectangle(args, 0),
                        buildRectangle(args, 4)
                );
            }
            return RectangleAnalysisFormatter.renderUsageWithMessage("Unrecognized arguments.", DEMOS);
        } catch (IllegalArgumentException exception) {
            return RectangleAnalysisFormatter.renderUsageWithMessage(
                    "Invalid input: " + exception.getMessage(),
                    DEMOS
            );
        }
    }

    private static String renderNamedDemo(String demoName) {
        if ("all".equalsIgnoreCase(demoName)) {
            return RectangleAnalysisFormatter.renderDemoSuite(DEMOS);
        }

        DemoCase demo = DEMOS.get(demoName);
        if (demo == null) {
            throw new IllegalArgumentException("unknown demo `" + demoName + "`");
        }

        return renderAnalysis(demo.title(), demo.description(), demo.first(), demo.second());
    }

    private static String renderAnalysis(String title,
                                         String description,
                                         Rectangle first,
                                         Rectangle second) {
        return RectangleAnalysisFormatter.renderAnalysis(
                title,
                description,
                first,
                second,
                RectangleAnalyzer.analyze(first, second)
        );
    }

    private static Rectangle buildRectangle(String[] args, int startIndex) {
        return new Rectangle(
                Double.parseDouble(args[startIndex]),
                Double.parseDouble(args[startIndex + 1]),
                Double.parseDouble(args[startIndex + 2]),
                Double.parseDouble(args[startIndex + 3])
        );
    }

    private static boolean isHelpArgument(String argument) {
        return "--help".equals(argument) || "-h".equals(argument);
    }
}
