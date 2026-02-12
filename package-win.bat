@echo OFF
TITLE "MockServer Packaging"

echo "MockServer Packaging"

echo "Building Maven project..."
call mvn clean
call mvn package -DskipTests
IF ERRORLEVEL 1 EXIT /B 1
echo "Copying jar..."
IF NOT EXIST target\libs mkdir target\libs
call copy target\MockServer.jar target\libs\MockServer.jar

IF NOT EXIST target\installer mkdir target\installer
IF NOT EXIST target\installer-work mkdir target\installer-work

echo "Packaging Windows MSI..."

call jpackage ^
 --name MockServer ^
 --vendor "Kazi Tanvir Azad" ^
 --verbose ^
 --app-version 1.0 ^
 --main-jar MockServer.jar ^
 --main-class com.server.app.Launcher ^
 --icon appicon.ico ^
 --input target\libs\ ^
 --temp target\installer-work\ ^
 --dest target\installer\ ^
 --type msi ^
 --win-menu ^
 --win-shortcut ^
 --win-per-user-install ^
 --win-dir-chooser ^
 --win-shortcut-prompt ^
 --win-menu-group 'MockServer'

echo "Packaging script execution complete!"
