# Rectangles Exercise

Java scaffold for the rectangle analysis exercise described in
[Rectangles Exercise.docx](./Rectangles%20Exercise.docx).

## Goal

Build a Linux-runnable Java solution that can determine:

- intersection points between two rectangles
- whether one rectangle is wholly contained in another
- whether two rectangles are adjacent, and if so whether that adjacency is `PROPER`,
  `SUB_LINE`, or `PARTIAL`

## Current Status

This repository is now scaffolded as a Maven project with:

- immutable domain types
- algorithm entry points
- a minimal test baseline
- a Java-specific implementation checklist

The local machine used for scaffolding does not currently have a JDK or Maven
installed, so compile/test execution has not been verified here yet.

## Technical Decisions

- Language: Java 17
- Build tool: Maven
- Rectangle model: axis-aligned rectangles
- Coordinate type: `double`
- Default containment rule: strict containment, meaning the inner rectangle must
  be wholly inside the outer rectangle and may not share a boundary
- Shared-side cases will be classified as adjacency, not as boundary intersection
- Corner-only contact should be treated as neither adjacency nor intersection

If any of those assumptions change, update the tests first and then adjust the
implementation.

## Proposed Package Layout

```text
src/main/java/com/ey/rectangles/
  App.java
  AdjacencyType.java
  Point.java
  Rectangle.java
  RectangleAnalyzer.java

src/test/java/com/ey/rectangles/
  RectangleAnalyzerTest.java
  RectangleTest.java
```

## Task List

1. Finish the geometry helper layer.
   Add side/segment helpers so intersection and adjacency logic stay small and testable.
2. Implement `intersectionPoints(Rectangle, Rectangle)`.
   Return all distinct perimeter intersection points in deterministic order.
3. Implement `adjacencyType(Rectangle, Rectangle)`.
   Distinguish `PROPER`, `SUB_LINE`, `PARTIAL`, and `NONE`.
4. Decide and document the identical-rectangle rule.
   The current scaffold assumes identical rectangles are not containment.
5. Expand the unit test suite.
   Cover all appendix cases plus corner-touch, disjoint, containment with shared edge,
   identical rectangles, and overlap without containment.
6. Replace the placeholder CLI behavior in `App`.
   Accept rectangle inputs and print the analysis results.
7. Verify on a Linux environment.
   Run `mvn test` and `mvn exec:java`, then document exact commands used.
8. Publish the repository.
   Add the public GitHub link requested in the exercise submission.

## Suggested Acceptance Criteria

- `Rectangle` rejects invalid bounds
- containment tests cover inside, outside, touching edge, and identical rectangles
- intersection tests cover no overlap, partial overlap, and multi-point boundary crossings
- adjacency tests cover proper, sub-line, partial, and non-adjacent cases
- all tests pass with `mvn test`
- the demo runs on Linux with documented prerequisites

## Commands

Once Java and Maven are installed:

```bash
mvn test
mvn exec:java
```

