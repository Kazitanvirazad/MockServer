@echo OFF
setlocal EnableExtensions
TITLE "MockServer — sign Windows MSI (Authenticode)"

if not defined MOCKSERVER_CODESIGN_PFX (
  echo [sign-win-msi] MOCKSERVER_CODESIGN_PFX is not set. Skipping MSI signing.
  exit /b 0
)

if not defined MOCKSERVER_CODESIGN_PFX_PASSWORD (
  echo [sign-win-msi] MOCKSERVER_CODESIGN_PFX_PASSWORD is not set.
  exit /b 1
)

set "SIGNTOOL_EXE=%MOCKSERVER_SIGNTOOL%"
if not defined SIGNTOOL_EXE set "SIGNTOOL_EXE=signtool"

if not defined MOCKSERVER_TIMESTAMP_URL (
  set "MOCKSERVER_TIMESTAMP_URL=http://timestamp.digicert.com"
)

if not defined MOCKSERVER_CODESIGN_DESCRIPTION (
  set "MOCKSERVER_CODESIGN_DESCRIPTION=Mock Server"
)

set "MSI_DIR=%~1"
if not defined MSI_DIR set "MSI_DIR=%~dp0..\build"

if not exist "%MSI_DIR%\" (
  echo [sign-win-msi] Directory does not exist: "%MSI_DIR%"
  exit /b 1
)

echo [sign-win-msi] Signing MSI under "%MSI_DIR%" ...

dir /b "%MSI_DIR%\MockServer*.msi" 2>nul | findstr /r . >nul
if errorlevel 1 (
  echo [sign-win-msi] No MockServer*.msi files found in "%MSI_DIR%"
  exit /b 1
)

for %%F in ("%MSI_DIR%\MockServer*.msi") do (
  if exist "%%~fF" (
    echo [sign-win-msi] Signing: %%~nxF
    call :signtool_sign "%%~fF"
    IF ERRORLEVEL 1 exit /b 1
  )
)

echo [sign-win-msi] Done.
exit /b 0

:signtool_sign
setlocal DisableDelayedExpansion
"%SIGNTOOL_EXE%" sign /v /fd SHA256 /td SHA256 /tr "%MOCKSERVER_TIMESTAMP_URL%" /d "%MOCKSERVER_CODESIGN_DESCRIPTION%" /f "%MOCKSERVER_CODESIGN_PFX%" /p "%MOCKSERVER_CODESIGN_PFX_PASSWORD%" "%~1"
if errorlevel 1 (
  endlocal
  exit /b 1
)
endlocal
exit /b 0
