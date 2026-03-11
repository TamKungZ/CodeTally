# CodeTally

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

---

## Use with Gradle

Add plugin:

```gradle
plugins {
  id 'me.tamkungz.codetally' version '1.0.0'
}
```

Run:

```bash
./gradlew countLines
```

---

## Use with Maven

Run directly:

```bash
mvn me.tamkungz:codetally-maven-plugin:1.0.0:count
```

Or configure in `pom.xml`:

```xml
<plugin>
  <groupId>me.tamkungz</groupId>
  <artifactId>codetally-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <goals>
        <goal>count</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

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

