#!/usr/bin/env bash
set -e
command -v javac >/dev/null || { echo "Java JDK is required. Install a JDK and try again."; exit 1; }
mkdir -p build
javac -d build src/main/java/App.java
java -cp build App
