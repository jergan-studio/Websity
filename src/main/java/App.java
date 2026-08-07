import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {
    private static Path webDirectory;

    public static void main(String[] args) {
        try {
            webDirectory = findWebDirectory();
            Path index = webDirectory.resolve("index.html");
            if (!Files.exists(index)) {
                System.err.println("Websity could not find web/index.html: " + index);
                System.exit(1);
            }
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Path findWebDirectory() {
        String configured = System.getProperty("websity.webdir");
        if (configured != null && !configured.isBlank()) {
            Path path = Path.of(configured).toAbsolutePath().normalize();
            if (Files.exists(path.resolve("index.html"))) return path;
        }

        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (Files.exists(current.resolve("web/index.html"))) return current.resolve("web");

        try {
            Path location = Path.of(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isDirectory(location) ? location : location.getParent();
            Path bundled = base.resolve("web");
            if (Files.exists(bundled.resolve("index.html"))) return bundled;
            Path parentBundled = base.getParent().resolve("web");
            if (Files.exists(parentBundled.resolve("index.html"))) return parentBundled;
        } catch (Exception ignored) { }

        return current.resolve("web");
    }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        webView.setContextMenuEnabled(true);
        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().load(webDirectory.resolve("index.html").toUri().toString());

        stage.setTitle("Websity");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.setScene(new Scene(webView));
        stage.show();
    }
}