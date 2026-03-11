@echo off
setlocal

REM Fixed tool locations
set "GRADLE_HOME=G:\.Gradle\gradle-8.12.1"
set "MAVEN_HOME=C:\.Apache\apache-maven-3.9.11"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"

REM Usage:
REM   build-all.cmd                 -> build only (safe default)
REM   build-all.cmd publish         -> build + publish (Gradle + Maven deploy)
REM Optional for Maven deploy in publish mode:
REM   set "MAVEN_ALT_DEPLOY_REPO=id::https://your.repo.url/repository/releases"
set "MODE=%~1"
if "%MODE%"=="" set "MODE=build"

if not exist "%GRADLE_BIN%" (
  echo [ERROR] Gradle not found: %GRADLE_BIN%
  exit /b 1
)

if not exist "%MAVEN_BIN%" (
  echo [ERROR] Maven not found: %MAVEN_BIN%
  exit /b 1
)

echo === Build Gradle plugin + core (Gradle) ===
call "%GRADLE_BIN%" clean build
if errorlevel 1 exit /b %errorlevel%

echo.
echo === Build Maven plugin (Maven) ===
call "%MAVEN_BIN%" -f pom.xml clean package
if errorlevel 1 exit /b %errorlevel%

if /I not "%MODE%"=="publish" goto :donePublish

echo.
echo === Publish Gradle plugin to Plugin Portal ===
echo [INFO] Requires gradle.publish.key / gradle.publish.secret in %%USERPROFILE%%\.gradle\gradle.properties
call "%GRADLE_BIN%" :gradle-plugin:publishPlugins
if errorlevel 1 exit /b %errorlevel%

echo.
echo === Deploy Maven plugin (GPG signing configured in Maven settings) ===
if "%MAVEN_ALT_DEPLOY_REPO%"=="" (
  echo [WARN] Maven deploy skipped. Set MAVEN_ALT_DEPLOY_REPO to enable deployment.
  echo [WARN] Example: set "MAVEN_ALT_DEPLOY_REPO=repoId::https://repo.example.com/releases"
) else (
  call "%MAVEN_BIN%" -f pom.xml clean deploy -DaltDeploymentRepository=%MAVEN_ALT_DEPLOY_REPO%
  if errorlevel 1 exit /b %errorlevel%
)

:donePublish

echo.
if /I "%MODE%"=="publish" (
  echo [OK] Build and publish completed for Gradle + Maven.
) else (
  echo [OK] Build finished for both Gradle and Maven.
)
exit /b 0
