@echo OFF

pushd "%~dp0signtools" >nul 2>&1 || exit /b 0
set "SIGNING_PROP_DIR=%CD%"
popd >nul

set "SIGNING_PROP_FILE=%SIGNING_PROP_DIR%\signing.properties"

if not exist "%SIGNING_PROP_FILE%" exit /b 0

for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%SIGNING_PROP_FILE%") do (
  if not "%%~B"=="" set "%%~A=%%~B"
)

call :signing_resolve_path MOCKSERVER_JARSIGN_KEYSTORE "%SIGNING_PROP_DIR%"
call :signing_resolve_path MOCKSERVER_CODESIGN_PFX "%SIGNING_PROP_DIR%"

exit /b 0

:signing_resolve_path
set "_PN=%~1"
set "_BASE=%~2"
set "_PV="
call set "_PV=%%%_PN%%%"
if not defined _PV goto :eof
if /i "%_PV:~0,2%"=="\\" goto :eof
if "%_PV:~1,1%"==":" goto :eof
set "%_PN%=%_BASE%\%_PV%"
goto :eof
