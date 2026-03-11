# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial [`README`](README.md) with usage for Gradle and Maven, publish instructions, and project links.
- Project homepage metadata set to `https://dev.tamkungz.me/` in [`gradle-plugin/build.gradle`](gradle-plugin/build.gradle) and [`pom.xml`](pom.xml).
- Maven Central publishing metadata in [`pom.xml`](pom.xml): description, url, scm, licenses, and developers.
- Maven release artifacts configuration in [`pom.xml`](pom.xml): sources jar, javadoc jar, GPG signing, and Sonatype Central publishing plugin.

### Changed
- Gradle plugin subproject now inherits coordinates from root in [`gradle-plugin/build.gradle`](gradle-plugin/build.gradle) via `group = rootProject.group` and `version = rootProject.version`.
- Maven plugin Javadoc example in [`maven-plugin/CountLinesMojo.java`](maven-plugin/CountLinesMojo.java) updated to use `{@code ...}` for valid Javadoc generation.

## [1.0.0] - 2026-03-11

### Added
- Initial release of CodeTally core analysis engine.
- Gradle plugin [`me.tamkungz.codetally`](gradle-plugin/build.gradle) with [`countLines`](gradle-plugin/LineCounterPlugin.java) task.
- Maven plugin goal [`count`](maven-plugin/CountLinesMojo.java) under artifact `me.tamkungz:codetally-maven-plugin`.
- Support for counting lines, characters, blank lines, and comment lines across source files.

