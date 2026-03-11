@echo off
setlocal EnableExtensions

REM ============================================================================
REM Build orchestrator for this repository
REM Modes:
REM   build           -> Gradle build + Maven package
REM   publish         -> Gradle plugin publish only
REM   gradle-publish  -> Gradle plugin publish only
REM   publish-gradle  -> Gradle plugin publish only (alias)
REM   maven           -> Maven package only
REM   maven-publish   -> Maven deploy only (requires MAVEN_ALT_DEPLOY_REPO)
REM   help            -> show usage
REM
REM Optional overrides:
REM   set GRADLE_HOME=...    (default is pinned below)
REM   set MAVEN_HOME=...     (default is pinned below)
REM ============================================================================

set "SCRIPT_DIR=%~dp0"
set "EXIT_CODE=0"

pushd "%SCRIPT_DIR%" >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Unable to enter script directory: %SCRIPT_DIR%
  exit /b 1
)

set "MODE=%~1"
if not defined MODE set "MODE=build"

if /I "%MODE%"=="help" goto :usage
if /I "%MODE%"=="-h" goto :usage
if /I "%MODE%"=="--help" goto :usage

call :resolveTools
if errorlevel 1 (
  set "EXIT_CODE=%errorlevel%"
  goto :finish
)

if /I "%MODE%"=="build" goto :modeBuild
if /I "%MODE%"=="publish" goto :modePublish
if /I "%MODE%"=="gradle-publish" goto :modeGradlePublish
if /I "%MODE%"=="publish-gradle" goto :modeGradlePublish
if /I "%MODE%"=="maven" goto :modeMaven
if /I "%MODE%"=="maven-publish" goto :modeMavenPublish

echo [ERROR] Unknown mode: %MODE%
goto :usageError

:modeBuild
call :ensureGradle
if errorlevel 1 goto :fail
call :ensureMaven
if errorlevel 1 goto :fail

echo [INFO] Build Gradle plugin + core (Gradle)
call "%GRADLE_RUN%" clean build
if errorlevel 1 goto :fail

echo.
echo [INFO] Build Maven plugin (Maven)
call "%MAVEN_RUN%" -f pom.xml clean package
if errorlevel 1 goto :fail
goto :finish

:modePublish
call :ensureGradle
if errorlevel 1 goto :fail

echo.
echo [INFO] Publish Gradle plugin only
call "%GRADLE_RUN%" :gradle-plugin:publishPlugins
if errorlevel 1 goto :fail
goto :finish

:modeGradlePublish
call :ensureGradle
if errorlevel 1 goto :fail

echo [INFO] Publish Gradle plugin only
call "%GRADLE_RUN%" :gradle-plugin:publishPlugins
if errorlevel 1 goto :fail
goto :finish

:modeMaven
call :ensureMaven
if errorlevel 1 goto :fail

echo [INFO] Build Maven plugin (Maven only)
call "%MAVEN_RUN%" -f pom.xml clean package
if errorlevel 1 goto :fail
goto :finish

:modeMavenPublish
call :ensureMaven
if errorlevel 1 goto :fail

if not defined MAVEN_ALT_DEPLOY_REPO (
  echo [ERROR] Maven deploy skipped. Set MAVEN_ALT_DEPLOY_REPO first.
  set "EXIT_CODE=1"
  goto :finish
)

echo [INFO] Deploy Maven plugin (Maven only)
call "%MAVEN_RUN%" -f pom.xml clean deploy -DaltDeploymentRepository=%MAVEN_ALT_DEPLOY_REPO%
if errorlevel 1 goto :fail
goto :finish

:resolveTools
set "GRADLE_HOME_DEFAULT=G:\.Gradle\gradle-8.12.1"
set "MAVEN_HOME_DEFAULT=C:\.Apache\apache-maven-3.9.11"

if not defined GRADLE_HOME set "GRADLE_HOME=%GRADLE_HOME_DEFAULT%"
if not defined MAVEN_HOME set "MAVEN_HOME=%MAVEN_HOME_DEFAULT%"

set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"
set "GRADLE_WRAPPER=%SCRIPT_DIR%gradlew.bat"
set "MAVEN_WRAPPER=%SCRIPT_DIR%mvnw.cmd"

if exist "%GRADLE_WRAPPER%" (
  set "GRADLE_RUN=%GRADLE_WRAPPER%"
) else (
  set "GRADLE_RUN=%GRADLE_BIN%"
)

if exist "%MAVEN_WRAPPER%" (
  set "MAVEN_RUN=%MAVEN_WRAPPER%"
) else (
  set "MAVEN_RUN=%MAVEN_BIN%"
)

echo [INFO] Mode: %MODE%
echo [INFO] Gradle runner: %GRADLE_RUN%
echo [INFO] Maven runner:  %MAVEN_RUN%
exit /b 0

:ensureGradle
if exist "%GRADLE_RUN%" exit /b 0
echo [ERROR] Gradle runner not found: %GRADLE_RUN%
exit /b 1

:ensureMaven
if exist "%MAVEN_RUN%" exit /b 0
echo [ERROR] Maven runner not found: %MAVEN_RUN%
exit /b 1

:usage
echo Usage: build-all.cmd [build^|publish^|gradle-publish^|publish-gradle^|maven^|maven-publish^|help]
echo.
echo Examples:
echo   build-all.cmd
echo   build-all.cmd publish
echo   set MAVEN_ALT_DEPLOY_REPO=myRepo::default::https://repo.example.com/releases
echo   build-all.cmd maven-publish
set "EXIT_CODE=0"
goto :finish

:usageError
call :usage >nul
set "EXIT_CODE=1"
goto :finish

:fail
set "EXIT_CODE=%errorlevel%"
goto :finish

:finish
echo.
if "%EXIT_CODE%"=="0" (
  echo [OK] Finished mode: %MODE%
) else (
  echo [ERROR] Failed mode: %MODE% (exit code %EXIT_CODE%^)
)
popd >nul 2>&1
exit /b %EXIT_CODE%
