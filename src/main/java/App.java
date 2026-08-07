import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    private static final int PORT = 8080;
    private static final Path WEB = Path.of("web");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", App::serveFile);
        server.createContext("/api/hello", App::apiHello);
        server.start();

        System.out.println("Websity app running at http://localhost:" + PORT);
    }

    private static void apiHello(HttpExchange exchange) throws IOException {
        String json = "{\"message\":\"Hello from Java!\"}";
        send(exchange, 200, "application/json", json);
    }

    private static void serveFile(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) requestPath = "/index.html";

        Path file = WEB.resolve(requestPath.substring(1)).normalize();
        if (!file.startsWith(WEB) || !Files.exists(file) || Files.isDirectory(file)) {
            send(exchange, 404, "text/plain", "404 - File not found");
            return;
        }

        String contentType = switch (getExtension(file)) {
            case "html" -> "text/html; charset=UTF-8";
            case "js" -> "application/javascript; charset=UTF-8";
            case "css" -> "text/css; charset=UTF-8";
            case "json" -> "application/json; charset=UTF-8";
            default -> "application/octet-stream";
        };

        byte[] data = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, data.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }

    private static String getExtension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot == -1 ? "" : name.substring(dot + 1).toLowerCase();
    }

    private static void send(HttpExchange exchange, int status, String type, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(status, data.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }
}
