# Websity

Websity is a starter template for building apps with **Java + JavaScript + HTML + CSS**.

## Project structure

```text
Websity/
├── src/
│   └── main/
│       └── java/
│           └── App.java        # Java backend/server
├── web/
│   ├── index.html               # App UI
│   ├── app.js                   # Frontend JavaScript
│   └── style.css                # App styling
├── Mods/                        # Optional JavaScript extensions
├── run.bat                      # Run on Windows
├── run.sh                       # Run on macOS/Linux
└── README.md
```

## Requirements

- JDK 17 or newer
- A modern web browser

## Run

### Windows

Double-click `run.bat`, or run:

```bat
run.bat
```

### macOS/Linux

```bash
chmod +x run.sh
./run.sh
```

Then open **http://localhost:8080**.

## How it works

- **Java** runs the local application server and provides API endpoints.
- **HTML** creates the app interface.
- **JavaScript** handles interaction and communicates with Java through `/api/...` endpoints.
- **CSS** controls the appearance.

## Make your own app

1. Fork this repository.
2. Change `web/index.html` to build your interface.
3. Change `web/app.js` for frontend behavior.
4. Change `web/style.css` for your design.
5. Add Java API endpoints in `src/main/java/App.java`.
6. Run the app locally with `run.bat` or `run.sh`.

This template is intended for local Java-powered apps and can be extended with a database, additional APIs, authentication, or a desktop wrapper later.
