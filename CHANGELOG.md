# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.4]

### Changed
- Project version bumped from `1.0.3` to `1.0.4` in [`build.gradle`](build.gradle) and [`pom.xml`](pom.xml).
- Documentation examples updated to `1.0.4` in [`README.md`](README.md).
- Source reading now uses UTF-8 explicitly in [`core/SourceAnalyzer.java`](core/SourceAnalyzer.java).
- Improved multi-line block comment detection in [`core/SourceAnalyzer.java`](core/SourceAnalyzer.java).
- Console table now includes `Comment` column in [`core/StatsReporter.java`](core/StatsReporter.java).
- Gradle task logging now uses lifecycle logger in [`gradle-plugin/CountLinesTask.java`](gradle-plugin/CountLinesTask.java).
- Reused analyzable file list to avoid duplicate scans in [`gradle-plugin/CountLinesTask.java`](gradle-plugin/CountLinesTask.java) and [`maven-plugin/CountLinesMojo.java`](maven-plugin/CountLinesMojo.java).

## [1.0.3]

### Changed
- Project version bumped from `1.0.2` to `1.0.3` in [`build.gradle`](build.gradle) and [`pom.xml`](pom.xml).
- Documentation examples updated to `1.0.3` in [`README.md`](README.md).
- Console table rendering changed to ASCII in [`core/StatsReporter.java`](core/StatsReporter.java) for cross-platform compatibility.

## [1.0.2]

### Changed
- Project version bumped from `1.0.1` to `1.0.2` in [`build.gradle`](build.gradle) and [`pom.xml`](pom.xml).
- Documentation examples updated to `1.0.2` in [`README.md`](README.md).

## [1.0.1]

### Added
- Initial [`README`](README.md) with usage for Gradle and Maven, publish instructions, and project links.
- Project homepage metadata set to `https://dev.tamkungz.me/` in [`gradle-plugin/build.gradle`](gradle-plugin/build.gradle) and [`pom.xml`](pom.xml).
- Maven Central publishing metadata in [`pom.xml`](pom.xml): description, url, scm, licenses, and developers.
- Maven release artifacts configuration in [`pom.xml`](pom.xml): sources jar, javadoc jar, GPG signing, and Sonatype Central publishing plugin.
- Linux build orchestrator script [`build-all.sh`](build-all.sh).

### Changed
- Project version bumped from `1.0.0` to `1.0.1` in [`build.gradle`](build.gradle) and [`pom.xml`](pom.xml).
- Documentation examples updated to `1.0.1` in [`README.md`](README.md).
- Removed `publish-gradle` alias from [`build-all.cmd`](build-all.cmd); use `gradle-publish`.

- Gradle plugin subproject now inherits coordinates from root in [`gradle-plugin/build.gradle`](gradle-plugin/build.gradle) via `group = rootProject.group` and `version = rootProject.version`.
- Maven plugin Javadoc example in [`maven-plugin/CountLinesMojo.java`](maven-plugin/CountLinesMojo.java) updated to use `{@code ...}` for valid Javadoc generation.

## [1.0.0] - 2026-03-11

### Added
- Initial release of CodeTally core analysis engine.
- Gradle plugin [`me.tamkungz.codetally`](gradle-plugin/build.gradle) with [`countLines`](gradle-plugin/LineCounterPlugin.java) task.
- Maven plugin goal [`count`](maven-plugin/CountLinesMojo.java) under artifact `me.tamkungz:codetally-maven-plugin`.
- Support for counting lines, characters, blank lines, and comment lines across source files.

