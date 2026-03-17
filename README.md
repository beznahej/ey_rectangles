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

This repository now includes:

- immutable domain types for rectangles and points
- containment detection
- intersection point detection for isolated boundary crossings
- adjacency classification for `PROPER`, `SUB_LINE`, and `PARTIAL`
- unit tests covering the main appendix-style and edge-case scenarios
- a small CLI entry point for running the analysis manually
- local verification with Java 17 and Maven

## Technical Decisions

- Language: Java 17
- Build tool: Maven
- Rectangle model: axis-aligned rectangles
- Coordinate type: `double`
- Assumptions: rectangles are axis-aligned, containment is strict, corner touch is not intersection, and shared-edge cases are treated as adjacency.
- Default containment rule: strict containment, meaning the inner rectangle must
  be wholly inside the outer rectangle and may not share a boundary
- Identical rectangles do not count as containment
- Shared-side cases will be classified as adjacency, not as boundary intersection
- Corner-only contact should be treated as neither adjacency nor intersection
- `intersectionPoints` returns isolated boundary-crossing points only; shared edge
  segments are handled by adjacency instead

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

## Implemented Rules

### Containment

`RectangleAnalyzer.contains(outer, inner)` uses strict containment:

- every side of `inner` must be strictly inside `outer`
- touching the outer boundary is not containment
- identical rectangles are not containment

### Intersection

`RectangleAnalyzer.intersectionPoints(first, second)` returns the distinct points
where the two rectangle boundaries cross as isolated points.

- contained rectangles return no intersection points
- shared-side adjacency returns no intersection points
- corner-only contact returns no intersection points
- common overlap cases produce two or four points

### Adjacency

`RectangleAnalyzer.adjacencyType(first, second)` returns:

- `PROPER` when both touching sides fully match in length
- `SUB_LINE` when one touching side is wholly contained within the other
- `PARTIAL` when the touching overlap is shorter than both touching sides
- `NONE` when rectangles overlap by area, are separated, or only meet at a corner

## Remaining Tasks

1. Add the public GitHub link to the final submission package.
2. Optionally add more examples or a richer CLI if you want to demo the solution live.

## Suggested Acceptance Criteria

- `Rectangle` rejects invalid bounds
- containment tests cover inside, outside, touching edge, and identical rectangles
- intersection tests cover no overlap, containment, partial overlap, and four-point crossings
- adjacency tests cover proper, sub-line, partial, and non-adjacent cases
- all tests pass with `mvn test`
- the demo runs on Linux with documented prerequisites

## How To Run

```bash
mvn test
mvn exec:java -Dexec.args="0 0 10 5 4 -2 8 3"
```
