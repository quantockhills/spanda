@echo off
rem Build the Spanda Windows MSI locally.
rem Requires: JDK 17 (set JAVA_HOME if needed) — WiX is downloaded automatically.
cd /d "%~dp0"
call gradlew.bat :desktop:packageMsi --no-daemon
if errorlevel 1 (
    echo.
    echo MSI build FAILED.
    exit /b 1
)
for %%f in (desktop\build\compose\binaries\main\msi\*.msi) do set MSI_PATH=%%f
echo.
echo === MSI built: %MSI_PATH% ===
echo Install with: msiexec /i "%MSI_PATH%"
