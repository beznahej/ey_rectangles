# Rectangles Exercise

Java scaffold for the rectangle analysis exercise described in
[Rectangles Exercise.docx](./Rectangles%20Exercise.docx).

Public repository: `https://github.com/beznahej/ey_rectangles`

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
- a friendlier CLI with built-in demos, named demo selection, and custom input modes
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

Shared-edge cases do not return discrete intersection points because a shared
edge is a line segment, not a finite set of isolated crossing points. This
implementation classifies those cases under adjacency instead.

### Adjacency

`RectangleAnalyzer.adjacencyType(first, second)` returns:

- `PROPER` when both touching sides fully match in length
- `SUB_LINE` when one touching side is wholly contained within the other
- `PARTIAL` when the touching overlap is shorter than both touching sides
- `NONE` when rectangles overlap by area, are separated, or only meet at a corner

Overlap-by-area is not adjacency in this solution because adjacency means the
rectangles share boundary points without overlapping interior area.

## Demo Modes

- No arguments: runs the full built-in demo suite.
- `--demo <name>`: runs one named scenario such as `intersection` or `proper-adjacency`.
- `--demo all`: runs all built-in scenarios.
- `--rects ...`: analyzes two rectangles from explicit coordinates.
- Bare eight numbers: still supported for backward compatibility.

## Example Scenarios

- `intersection`: partial overlap with two boundary crossing points
- `four-point-intersection`: overlap where the boundaries cross at four points
- `containment`: strict containment with zero intersection points
- `proper-adjacency`: full side sharing
- `sub-line-adjacency`: one touching side fully contained within the other
- `partial-adjacency`: shorter shared boundary segment than both sides
- `corner-touch`: contact at one corner only
- `disjoint`: no contact and no overlap

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
mvn exec:java
mvn exec:java -Dexec.args="--help"
mvn exec:java -Dexec.args="--demo partial-adjacency"
mvn exec:java -Dexec.args="--rects 0 0 10 5 4 -2 8 3"
mvn exec:java -Dexec.args="0 0 10 5 4 -2 8 3"
```
