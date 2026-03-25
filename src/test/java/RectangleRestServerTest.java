import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RectangleRestServerTest {

    @Test
    void servesHtmlDemo() throws IOException, InterruptedException {
        try (TestServer server = new TestServer()) {
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(server.uri("/demo")).GET().build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());
            assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("text/html"));
            assertTrue(response.body().contains("Rectangle Demo"));
            assertTrue(response.body().contains("Intersection"));
        }
    }

    @Test
    void returnsJsonAnalysisForPostedRectangles() throws IOException, InterruptedException {
        String body = """
                {
                  "rectangleA": { "minX": 0, "minY": 0, "maxX": 10, "maxY": 5 },
                  "rectangleB": { "minX": 4, "minY": -2, "maxX": 8, "maxY": 3 }
                }
                """;

        try (TestServer server = new TestServer()) {
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(server.uri("/api/rectangles/analyze"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());
            assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("application/json"));
            assertTrue(response.body().contains("\"relationshipType\" : \"BOUNDARY_INTERSECTION\""));
            assertTrue(response.body().contains("\"adjacencyType\" : \"NONE\""));
        }
    }

    private static final class TestServer implements AutoCloseable {
        private final HttpServer server;

        private TestServer() {
            this.server = RectangleRestServer.start(0);
        }

        private URI uri(String path) {
            return URI.create("http://localhost:" + server.getAddress().getPort() + path);
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }
}
