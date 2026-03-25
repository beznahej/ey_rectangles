import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Focused tests for the core rectangle relationship rules.
 */
class RectangleAnalyzerTest {

    @Test
    void detectsStrictContainmentButRejectsBoundaryTouch() {
        Rectangle outer = new Rectangle(0.0, 0.0, 10.0, 10.0);
        Rectangle inner = new Rectangle(2.0, 2.0, 4.0, 4.0);
        Rectangle touching = new Rectangle(0.0, 2.0, 4.0, 4.0);
        Rectangle identical = new Rectangle(0.0, 0.0, 10.0, 10.0);

        assertAll(
                () -> assertTrue(RectangleAnalyzer.contains(outer, inner)),
                () -> assertFalse(RectangleAnalyzer.contains(outer, touching)),
                () -> assertFalse(RectangleAnalyzer.contains(outer, identical))
        );
    }

    @Test
    void returnsExpectedIntersectionPointsForRepresentativeCases() {
        assertAll(
                () -> assertEquals(
                        Set.of(),
                        RectangleAnalyzer.intersectionPoints(
                                new Rectangle(0.0, 0.0, 10.0, 10.0),
                                new Rectangle(2.0, 2.0, 4.0, 4.0)
                        )
                ),
                () -> assertEquals(
                        Set.of(new Point(4.0, 0.0), new Point(8.0, 0.0)),
                        RectangleAnalyzer.intersectionPoints(
                                new Rectangle(0.0, 0.0, 10.0, 5.0),
                                new Rectangle(4.0, -2.0, 8.0, 3.0)
                        )
                ),
                () -> assertEquals(
                        Set.of(
                                new Point(4.0, 0.0),
                                new Point(8.0, 0.0),
                                new Point(4.0, 4.0),
                                new Point(8.0, 4.0)
                        ),
                        RectangleAnalyzer.intersectionPoints(
                                new Rectangle(0.0, 0.0, 10.0, 4.0),
                                new Rectangle(4.0, -2.0, 8.0, 6.0)
                        )
                ),
                () -> assertEquals(
                        Set.of(),
                        RectangleAnalyzer.intersectionPoints(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 0.0, 6.0, 4.0)
                        )
                ),
                () -> assertEquals(
                        Set.of(),
                        RectangleAnalyzer.intersectionPoints(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 4.0, 6.0, 6.0)
                        )
                )
        );
    }

    @Test
    void returnsFourIntersectionPointsForPlusSignIntersection() {
        Rectangle horizontal = new Rectangle(0.0, 4.0, 10.0, 6.0);
        Rectangle vertical = new Rectangle(4.0, 0.0, 6.0, 10.0);

        assertEquals(
                Set.of(
                        new Point(4.0, 4.0),
                        new Point(6.0, 4.0),
                        new Point(4.0, 6.0),
                        new Point(6.0, 6.0)
                ),
                RectangleAnalyzer.intersectionPoints(horizontal, vertical)
        );
    }

    @Test
    void classifiesRepresentativeAdjacencyScenarios() {
        assertAll(
                () -> assertEquals(
                        AdjacencyType.PROPER,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 0.0, 6.0, 4.0)
                        )
                ),
                () -> assertEquals(
                        AdjacencyType.SUB_LINE,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 1.0, 7.0, 3.0)
                        )
                ),
                () -> assertEquals(
                        AdjacencyType.PARTIAL,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 2.0, 7.0, 6.0)
                        )
                ),
                () -> assertEquals(
                        AdjacencyType.NONE,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(3.0, 1.0, 6.0, 3.0)
                        )
                ),
                () -> assertEquals(
                        AdjacencyType.NONE,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(5.0, 1.0, 7.0, 3.0)
                        )
                ),
                () -> assertEquals(
                        AdjacencyType.NONE,
                        RectangleAnalyzer.adjacencyType(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 4.0, 6.0, 6.0)
                        )
                )
        );
    }

    @Test
    void returnsConsolidatedAnalysisForRepresentativeCase() {
        RectangleAnalysis analysis = RectangleAnalyzer.analyze(
                new Rectangle(0.0, 0.0, 10.0, 5.0),
                new Rectangle(4.0, -2.0, 8.0, 3.0)
        );

        assertAll(
                () -> assertFalse(analysis.firstContainsSecond()),
                () -> assertFalse(analysis.secondContainsFirst()),
                () -> assertEquals(AdjacencyType.NONE, analysis.adjacencyType()),
                () -> assertEquals(Set.of(new Point(4.0, 0.0), new Point(8.0, 0.0)), analysis.intersectionPoints()),
                () -> assertEquals(RelationshipType.BOUNDARY_INTERSECTION, analysis.relationshipType())
        );
    }

    @Test
    void classifiesRepresentativeTopLevelRelationships() {
        assertAll(
                () -> assertEquals(
                        RelationshipType.FIRST_CONTAINS_SECOND,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 10.0, 10.0),
                                new Rectangle(2.0, 2.0, 4.0, 4.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.SECOND_CONTAINS_FIRST,
                        RectangleAnalyzer.analyze(
                                new Rectangle(2.0, 2.0, 4.0, 4.0),
                                new Rectangle(0.0, 0.0, 10.0, 10.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.ADJACENT,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 0.0, 6.0, 4.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.BOUNDARY_INTERSECTION,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 10.0, 5.0),
                                new Rectangle(4.0, -2.0, 8.0, 3.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.AREA_OVERLAP,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(2.0, 0.0, 6.0, 4.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.CORNER_TOUCH,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(4.0, 4.0, 6.0, 6.0)
                        ).relationshipType()
                ),
                () -> assertEquals(
                        RelationshipType.DISJOINT,
                        RectangleAnalyzer.analyze(
                                new Rectangle(0.0, 0.0, 4.0, 4.0),
                                new Rectangle(6.0, 1.0, 8.0, 3.0)
                        ).relationshipType()
                )
        );
    }
}
