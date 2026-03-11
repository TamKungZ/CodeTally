#!/usr/bin/env sh

set -eu

# ============================================================================
# Build orchestrator for this repository (Linux/macOS)
# Modes:
#   build           -> Gradle build + Maven package
#   publish         -> Gradle plugin publish only
#   gradle-publish  -> Gradle plugin publish only
#   maven           -> Maven package only
#   maven-publish   -> Maven deploy only (requires MAVEN_ALT_DEPLOY_REPO)
#   help            -> show usage
#
# Optional overrides:
#   GRADLE_HOME=...   (default uses wrapper, then PATH)
#   MAVEN_HOME=...    (default uses wrapper, then PATH)
# ============================================================================

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MODE="${1:-build}"

usage() {
  echo "Usage: ./build-all.sh [build|publish|gradle-publish|maven|maven-publish|help]"
  echo
  echo "Examples:"
  echo "  ./build-all.sh"
  echo "  ./build-all.sh publish"
  echo "  MAVEN_ALT_DEPLOY_REPO=myRepo::default::https://repo.example.com/releases ./build-all.sh maven-publish"
}

resolve_tools() {
  if [ -x "$SCRIPT_DIR/gradlew" ]; then
    GRADLE_RUN="$SCRIPT_DIR/gradlew"
  elif [ -n "${GRADLE_HOME:-}" ] && [ -x "$GRADLE_HOME/bin/gradle" ]; then
    GRADLE_RUN="$GRADLE_HOME/bin/gradle"
  else
    GRADLE_RUN="gradle"
  fi

  if [ -x "$SCRIPT_DIR/mvnw" ]; then
    MAVEN_RUN="$SCRIPT_DIR/mvnw"
  elif [ -n "${MAVEN_HOME:-}" ] && [ -x "$MAVEN_HOME/bin/mvn" ]; then
    MAVEN_RUN="$MAVEN_HOME/bin/mvn"
  else
    MAVEN_RUN="mvn"
  fi

  echo "[INFO] Mode: $MODE"
  echo "[INFO] Gradle runner: $GRADLE_RUN"
  echo "[INFO] Maven runner:  $MAVEN_RUN"
}

ensure_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[ERROR] Command not found: $1" >&2
    exit 1
  fi
}

ensure_gradle() {
  case "$GRADLE_RUN" in
    */*) [ -x "$GRADLE_RUN" ] || { echo "[ERROR] Gradle runner not found: $GRADLE_RUN" >&2; exit 1; } ;;
    *) ensure_cmd "$GRADLE_RUN" ;;
  esac
}

ensure_maven() {
  case "$MAVEN_RUN" in
    */*) [ -x "$MAVEN_RUN" ] || { echo "[ERROR] Maven runner not found: $MAVEN_RUN" >&2; exit 1; } ;;
    *) ensure_cmd "$MAVEN_RUN" ;;
  esac
}

run_build() {
  ensure_gradle
  ensure_maven

  echo "[INFO] Build Gradle plugin + core (Gradle)"
  "$GRADLE_RUN" clean build

  echo
  echo "[INFO] Build Maven plugin (Maven)"
  "$MAVEN_RUN" -f "$SCRIPT_DIR/pom.xml" clean package
}

run_publish() {
  ensure_gradle
  echo "[INFO] Publish Gradle plugin only"
  "$GRADLE_RUN" :gradle-plugin:publishPlugins
}

run_maven() {
  ensure_maven
  echo "[INFO] Build Maven plugin (Maven only)"
  "$MAVEN_RUN" -f "$SCRIPT_DIR/pom.xml" clean package
}

run_maven_publish() {
  ensure_maven
  if [ -z "${MAVEN_ALT_DEPLOY_REPO:-}" ]; then
    echo "[ERROR] Maven deploy skipped. Set MAVEN_ALT_DEPLOY_REPO first." >&2
    exit 1
  fi
  echo "[INFO] Deploy Maven plugin (Maven only)"
  "$MAVEN_RUN" -f "$SCRIPT_DIR/pom.xml" clean deploy "-DaltDeploymentRepository=$MAVEN_ALT_DEPLOY_REPO"
}

case "$MODE" in
  help|-h|--help)
    usage
    exit 0
    ;;
esac

resolve_tools

case "$MODE" in
  build) run_build ;;
  publish|gradle-publish) run_publish ;;
  maven) run_maven ;;
  maven-publish) run_maven_publish ;;
  *)
    echo "[ERROR] Unknown mode: $MODE" >&2
    usage
    exit 1
    ;;
esac

echo
echo "[OK] Finished mode: $MODE"

