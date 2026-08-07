@echo off
setlocal
where javac >nul 2>nul || (echo Java JDK is required. Install a JDK and try again.& exit /b 1)
if not exist build mkdir build
javac -d build src\main\java\App.java
if errorlevel 1 exit /b 1
java -cp build App
