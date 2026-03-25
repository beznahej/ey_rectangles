import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Barebones HTTP server for demoing the rectangle analysis as HTML and JSON.
 */
final class RectangleRestServer {

    private static final int DEFAULT_PORT = 8080;
    private static final Map<String, DemoCase> DEMOS = DemoCatalog.all();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter JSON_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

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

        String demoName = readQueryValue(exchange.getRequestURI(), "name");
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
            sendJson(exchange, 200, RectangleAnalyzer.analyze(first, second));
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
        if ("all".equalsIgnoreCase(demoName)) {
            return RectangleAnalysisFormatter.renderDemoSuite(DEMOS);
        }

        DemoCase demo = DEMOS.get(demoName);
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

    private static String readQueryValue(URI uri, String key) {
        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return "intersection";
        }

        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String entryKey = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            if (entryKey.equals(key)) {
                return value;
            }
        }
        return "intersection";
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

    private static void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        sendResponse(exchange, status, "application/json; charset=utf-8", JSON_WRITER.writeValueAsString(body));
    }

    private static void sendJsonError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, new ErrorResponse(message));
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

    private record ErrorResponse(String error) {
    }
}
