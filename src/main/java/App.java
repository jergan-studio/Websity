import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Websity 1.0
 *
 * Java powers the local app server while web/index.html is the app UI.
 * Run with:
 *   javac App.java
 *   java App
 */
public class App {
    private static final int PORT = 8080;
    private static Path webDirectory;
    private static HttpServer server;

    public static void main(String[] args) throws Exception {
        webDirectory = findWebDirectory();
        Path index = webDirectory.resolve("index.html");
        if (!Files.exists(index)) {
            throw new IOException("Websity could not find web/index.html: " + index);
        }

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
        server.createContext("/", App::serveFile);
        server.createContext("/api/hello", App::hello);
        server.start();

        String url = "http://127.0.0.1:" + PORT + "/index.html";
        System.out.println("Websity 1.0 is running.");
        System.out.println("Web folder: " + webDirectory);
        System.out.println("App: " + url);
        System.out.println("Press Ctrl+C to stop Websity.");

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception e) {
            System.out.println("Open this URL manually: " + url);
        }
    }

    private static Path findWebDirectory() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path web = current.resolve("web");
        if (Files.exists(web.resolve("index.html"))) return web;

        Path source = Path.of("src", "main", "web").toAbsolutePath().normalize();
        if (Files.exists(source.resolve("index.html"))) return source;

        return web;
    }

    private static void hello(HttpExchange exchange) throws IOException {
        send(exchange, 200, "application/json; charset=UTF-8", "{\"message\":\"Hello from Java! Websity is working.\"}");
    }

    private static void serveFile(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        if (request.equals("/") || request.isBlank()) request = "/index.html";

        Path file = webDirectory.resolve(request.substring(1)).normalize();
        if (!file.startsWith(webDirectory) || !Files.exists(file) || Files.isDirectory(file)) {
            send(exchange, 404, "text/plain; charset=UTF-8", "404 - File not found");
            return;
        }

        byte[] data = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType(file));
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, data.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }

    private static String contentType(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (name.endsWith(".json")) return "application/json; charset=UTF-8";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
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
