@echo off
setlocal EnableExtensions
cd /d "%~dp0"

title Websity App Builder

echo ========================================
echo          Websity App Builder
echo ========================================
echo.

where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java JDK was not found.
    echo Install a JDK with javac, then run this file again.
    echo.
    pause
    exit /b 1
)

where jar >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java JDK jar tool was not found.
    echo Make sure the JDK is installed and on PATH.
    echo.
    pause
    exit /b 1
)

where jpackage >nul 2>nul
if errorlevel 1 (
    echo ERROR: jpackage was not found.
    echo A full JDK 14 or newer is required to create the EXE.
    echo.
    pause
    exit /b 1
)

echo [1/4] Cleaning old build files...
if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build
mkdir dist
mkdir dist\app

echo [2/4] Compiling Java...
javac -d build src\main\java\App.java
if errorlevel 1 (
    echo.
    echo ERROR: Java compilation failed.
    pause
    exit /b 1
)

echo [3/4] Creating JAR...
jar --create --file dist\Websity.jar --main-class App -C build .
if errorlevel 1 (
    echo.
    echo ERROR: JAR creation failed.
    pause
    exit /b 1
)

if exist web xcopy /E /I /Y web dist\app\web >nul

if not exist dist\app\web mkdir dist\app\web

copy /Y dist\Websity.jar dist\app\Websity.jar >nul

echo [4/4] Creating Windows EXE...
jpackage ^
    --type exe ^
    --name Websity ^
    --input dist\app ^
    --main-jar Websity.jar ^
    --main-class App ^
    --dest dist ^
    --app-version 1.0.0 ^
    --win-dir-chooser ^
    --win-menu ^
    --win-shortcut

if errorlevel 1 (
    echo.
    echo ERROR: EXE creation failed.
    echo The JAR was created successfully at dist\Websity.jar
    pause
    exit /b 1
)

echo.
echo ========================================
echo             BUILD COMPLETE!
echo ========================================
echo.
echo JAR: %CD%\dist\Websity.jar
echo EXE: %CD%\dist\Websity-1.0.0.exe
echo.
echo You can now install the EXE on Windows.
echo ========================================
echo.
pause
