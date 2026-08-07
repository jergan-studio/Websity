import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {
    private static final int PORT = 8080;
    private static HttpServer server;

    public static void main(String[] args) throws Exception {
        startServer();
        launch(args);
    }

    private static void startServer() throws IOException {
        Path web = getWebDirectory();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
        server.createContext("/", exchange -> serveFile(exchange, web));
        server.createContext("/api/hello", App::apiHello);
        server.start();
    }

    private static Path getWebDirectory() {
        try {
            URI location = App.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path locationPath = Path.of(location);
            if (Files.isDirectory(locationPath)) return locationPath.resolve("web").normalize();
            return locationPath.getParent().resolve("web").normalize();
        } catch (Exception e) {
            return Path.of("web").toAbsolutePath().normalize();
        }
    }

    @Override
    public void start(Stage stage) {
        WebView browser = new WebView();
        browser.getEngine().load("http://127.0.0.1:" + PORT + "/");

        stage.setTitle("Websity");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(new Scene(browser, 1100, 700));
        stage.show();
    }

    @Override
    public void stop() {
        if (server != null) server.stop(0);
    }

    private static void apiHello(HttpExchange exchange) throws IOException {
        String json = "{\"message\":\"Hello from Java!\"}";
        send(exchange, 200, "application/json; charset=UTF-8", json);
    }

    private static void serveFile(HttpExchange exchange, Path web) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) requestPath = "/index.html";
        Path file = web.resolve(requestPath.substring(1)).normalize();

        if (!file.startsWith(web) || !Files.exists(file) || Files.isDirectory(file)) {
            send(exchange, 404, "text/plain; charset=UTF-8", "404 - File not found");
            return;
        }

        String contentType = switch (getExtension(file)) {
            case "html" -> "text/html; charset=UTF-8";
            case "js" -> "application/javascript; charset=UTF-8";
            case "css" -> "text/css; charset=UTF-8";
            case "json" -> "application/json; charset=UTF-8";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "svg" -> "image/svg+xml";
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