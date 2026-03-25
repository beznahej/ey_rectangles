import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Barebones HTTP server for demoing the rectangle analysis as HTML and JSON.
 */
final class RectangleRestServer {

    private static final int DEFAULT_PORT = 8080;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private RectangleRestServer() {
    }

    static HttpServer start() {
        HttpServer server = start(DEFAULT_PORT);
        System.out.println("Rectangle REST server running on http://localhost:" + DEFAULT_PORT);
        System.out.println("Demo page: http://localhost:" + DEFAULT_PORT + "/demo");
        return server;
    }

    static HttpServer start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/demo", RectangleRestServer::handleDemo);
            server.createContext("/api/rectangles/analyze", RectangleRestServer::handleAnalyze);
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start REST server", exception);
        }
    }

    private static void handleDemo(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange, "GET");
            return;
        }

        Map<String, String> query = parseQuery(exchange.getRequestURI());
        String demoName = query.getOrDefault("name", "intersection");
        try {
            String report = renderDemoReport(demoName);
            String html = renderHtmlPage(demoName, report);
            sendResponse(exchange, 200, "text/html; charset=utf-8", html);
        } catch (IllegalArgumentException exception) {
            sendJsonError(exchange, 404, exception.getMessage());
        }
    }

    private static void handleAnalyze(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange, "POST");
            return;
        }

        try (InputStream body = exchange.getRequestBody()) {
            AnalyzeRequest request = OBJECT_MAPPER.readValue(body, AnalyzeRequest.class);
            Rectangle first = toRectangle("rectangleA", request.rectangleA());
            Rectangle second = toRectangle("rectangleB", request.rectangleB());
            RectangleAnalysis analysis = RectangleAnalyzer.analyze(first, second);
            AnalyzeResponse response = AnalyzeResponse.from(analysis);

            sendResponse(
                    exchange,
                    200,
                    "application/json; charset=utf-8",
                    OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response)
            );
        } catch (IllegalArgumentException | JsonProcessingException exception) {
            sendJsonError(exchange, 400, exception.getMessage());
        }
    }

    private static Rectangle toRectangle(String label, RectanglePayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException(label + " must be provided");
        }
        if (payload.minX() == null || payload.minY() == null || payload.maxX() == null || payload.maxY() == null) {
            throw new IllegalArgumentException(label + " must include minX, minY, maxX, and maxY");
        }
        return new Rectangle(payload.minX(), payload.minY(), payload.maxX(), payload.maxY());
    }

    private static String renderDemoReport(String demoName) {
        Map<String, DemoCase> demos = DemoCatalog.all();
        if ("all".equalsIgnoreCase(demoName)) {
            return RectangleAnalysisFormatter.renderDemoSuite(demos);
        }

        DemoCase demo = demos.get(demoName);
        if (demo == null) {
            throw new IllegalArgumentException("unknown demo `" + demoName + "`");
        }

        return RectangleAnalysisFormatter.renderAnalysis(
                demo.title(),
                demo.description(),
                demo.first(),
                demo.second(),
                RectangleAnalyzer.analyze(demo.first(), demo.second())
        );
    }

    private static String renderHtmlPage(String demoName, String report) {
        String escapedName = escapeHtml(demoName);
        String escapedReport = escapeHtml(report);
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <title>Rectangle Demo</title>
                  <style>
                    body { font-family: sans-serif; margin: 2rem; line-height: 1.5; }
                    code { background: #f3f3f3; padding: 0.1rem 0.3rem; }
                    pre { background: #f7f7f7; border: 1px solid #ddd; padding: 1rem; overflow-x: auto; }
                  </style>
                </head>
                <body>
                  <h1>Rectangle Demo</h1>
                  <p>Showing demo: <code>%s</code></p>
                  <p>Change demos with <code>/demo?name=proper-adjacency</code> or <code>/demo?name=all</code>.</p>
                  <pre>%s</pre>
                </body>
                </html>
                """.formatted(escapedName, escapedReport);
    }

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> query = new LinkedHashMap<>();
        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return query;
        }

        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            query.put(key, value);
        }
        return query;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void sendMethodNotAllowed(HttpExchange exchange, String allowedMethod) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowedMethod);
        sendJsonError(exchange, 405, "Only " + allowedMethod + " is allowed for this endpoint");
    }

    private static void sendJsonError(HttpExchange exchange, int status, String message) throws IOException {
        String body = OBJECT_MAPPER.writeValueAsString(new ErrorResponse(message));
        sendResponse(exchange, status, "application/json; charset=utf-8", body);
    }

    private static void sendResponse(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        } finally {
            exchange.close();
        }
    }

    private record AnalyzeRequest(RectanglePayload rectangleA, RectanglePayload rectangleB) {
    }

    private record RectanglePayload(Double minX, Double minY, Double maxX, Double maxY) {
    }

    private record AnalyzeResponse(boolean firstContainsSecond,
                                   boolean secondContainsFirst,
                                   String adjacencyType,
                                   List<PointResponse> intersectionPoints,
                                   String relationshipType) {

        static AnalyzeResponse from(RectangleAnalysis analysis) {
            List<PointResponse> points = analysis.intersectionPoints().stream()
                    .map(point -> new PointResponse(point.x(), point.y()))
                    .toList();
            return new AnalyzeResponse(
                    analysis.firstContainsSecond(),
                    analysis.secondContainsFirst(),
                    analysis.adjacencyType().name(),
                    points,
                    analysis.relationshipType().name()
            );
        }
    }

    private record PointResponse(double x, double y) {
    }

    private record ErrorResponse(String error) {
    }
}
