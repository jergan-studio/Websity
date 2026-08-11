@echo off
setlocal
cd /d "%~dp0"
title Websity 1.0 Builder

echo ========================================
echo          Websity 1.0 Builder
echo ========================================
where javac >nul 2>nul
if errorlevel 1 ( echo ERROR: javac not found. Install a JDK and add it to PATH. & pause & exit /b 1 )
where jar >nul 2>nul
if errorlevel 1 ( echo ERROR: jar not found. Install a full JDK. & pause & exit /b 1 )
if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build
mkdir dist
mkdir dist\app

echo Compiling App.java...
javac -d build src\main\java\App.java
if errorlevel 1 ( echo Build failed. & pause & exit /b 1 )

echo Creating Websity.jar...
jar --create --file dist\Websity.jar --main-class App -C build .
if errorlevel 1 ( echo JAR creation failed. & pause & exit /b 1 )
xcopy /E /I /Y web dist\app\web >nul
copy /Y dist\Websity.jar dist\app\Websity.jar >nul

echo JAR created: dist\Websity.jar
where jpackage >nul 2>nul
if errorlevel 1 (
  echo jpackage not found. JAR build is complete.
  echo Run: java -jar dist\Websity.jar
  pause
  exit /b 0
)

echo Creating Windows EXE...
jpackage --type exe --name Websity --input dist\app --main-jar Websity.jar --main-class App --dest dist --app-version 1.0.0 --win-dir-chooser --win-menu --win-shortcut
if errorlevel 1 ( echo EXE creation failed, but the JAR was created. & pause & exit /b 1 )
echo.
echo BUILD COMPLETE
echo JAR: %CD%\dist\Websity.jar
echo EXE: %CD%\dist\Websity-1.0.0.exe
pause
