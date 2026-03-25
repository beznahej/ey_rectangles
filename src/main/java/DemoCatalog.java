import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Built-in demo inputs used by the CLI.
 */
final class DemoCatalog {

    private static final Map<String, DemoCase> DEMOS = createDemos();

    private DemoCatalog() {
    }

    static Map<String, DemoCase> all() {
        return DEMOS;
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
}

record DemoCase(String title, String description, Rectangle first, Rectangle second) {
}
