import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Smoke tests for the CLI wrapper. Detailed geometry behavior is covered in
 * {@code RectangleAnalyzerTest}; these tests just verify the main rendering paths.
 */
class AppTest {

    @Test
    void rendersBuiltInDemoSuiteWhenNoArgumentsAreProvided() {
        String output = App.renderForArguments(new String[0]);

        assertTrue(output.contains("Rectangles Demo Suite"));
        assertTrue(output.contains("Proper Adjacency"));
        assertTrue(output.contains("Corner Touch"));
    }

    @Test
    void rendersCustomAnalysisFromRectsFlag() {
        String output = App.renderForArguments(new String[]{"--rects", "0", "0", "10", "5", "4", "-2", "8", "3"});

        assertTrue(output.contains("Custom Analysis"));
        assertTrue(output.contains("Intersection points (2):"));
        assertTrue(output.contains("Adjacency: NONE"));
    }

    @Test
    void rendersUsageForUnknownDemo() {
        String output = App.renderForArguments(new String[]{"--demo", "missing-demo"});

        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("Built-in demos:"));
    }
}
