# CodeTally

[![MvnRepository](https://badges.mvnrepository.com/badge/me.tamkungz/codetally-maven-plugin/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/me.tamkungz/codetally-maven-plugin)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/me.tamkungz.codetally?&color=72ada4)](https://plugins.gradle.org/plugin/me.tamkungz.codetally)
[![License](https://img.shields.io/github/license/TamKungZ/CodeTally)](LICENSE)

Code metrics plugin for both **Gradle** and **Maven**.

- Website: https://dev.tamkungz.me/
- Gradle Plugin ID: `me.tamkungz.codetally`
- Maven Artifact: `me.tamkungz:codetally-maven-plugin`

---

## Features

Analyze source files under `src` and report:

- total files
- lines
- characters
- blank lines
- comment lines

Options:

- skip blank lines
- skip comment lines
- verbose per-file output
- export machine-readable report (`json`/`csv`)
- threshold gate (`maxLines`) to fail build
- optional git blame ownership (author -> lines)

---

## Use with Gradle

Add plugin:

```gradle
plugins {
  id 'me.tamkungz.codetally' version '1.0.4'
}
```

Run:

```bash
./gradlew countLines
```

Configure task options:

```gradle
tasks.named('countLines', me.tamkungz.codetally.CountLinesTask) {
  skipBlankLines = true
  skipCommentLines = true
  verbose = false

  reportFormat = 'json' // json or csv
  outputFile = layout.buildDirectory.file('reports/codetally/report.json')

  maxLines = 50000L     // 0 = disabled
  gitBlame = false      // true = aggregate author ownership via git blame
}
```

Run automatically at the end of build:

```gradle
tasks.named('build') {
  finalizedBy(tasks.named('countLines'))
}
```

---

## Use with Maven

Run directly:

```bash
mvn me.tamkungz:codetally-maven-plugin:1.0.4:count
```

With options:

```bash
mvn me.tamkungz:codetally-maven-plugin:1.0.4:count ^
  -Dcodetally.skipBlankLines=true ^
  -Dcodetally.skipCommentLines=true ^
  -Dcodetally.verbose=false ^
  -Dcodetally.reportFormat=json ^
  -Dcodetally.outputFile=target/codetally-report.json ^
  -Dcodetally.maxLines=50000 ^
  -Dcodetally.gitBlame=false
```

Or configure in `pom.xml`:

```xml
<plugin>
  <groupId>me.tamkungz</groupId>
  <artifactId>codetally-maven-plugin</artifactId>
  <version>1.0.4</version>
  <executions>
    <execution>
      <goals>
        <goal>count</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Run automatically near the end of package/build lifecycle:

```xml
<plugin>
  <groupId>me.tamkungz</groupId>
  <artifactId>codetally-maven-plugin</artifactId>
  <version>1.0.4</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>count</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

---

## CI features now available

- Export CSV/JSON via output file + report format option
- Threshold checks via `maxLines`
- Git blame integration via `gitBlame`
- Incremental/cached Gradle task via `@InputFiles` + `@OutputFile`

---

## Build & Publish (maintainer)

Build both plugins:

```bat
build-all.cmd
```

Build + publish:

```bat
build-all.cmd publish
```

---

## Repositories

- Source: https://github.com/TamKungZ/CodeTally
- Gradle Plugin Portal: https://plugins.gradle.org/plugin/me.tamkungz.codetally
- Maven Central: https://repo1.maven.org/maven2/me/tamkungz/codetally-maven-plugin/

