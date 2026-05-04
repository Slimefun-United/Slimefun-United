# Albion/Paper 26.1.2 compatibility patch

This source tree was patched from the provided Slimefun United 1.21.11-working baseline for Minecraft/Paper 26.1.2.

## Main changes

- Bumped Paper API dependency to the current 26.1.2 build range.
- Set `plugin.yml` `api-version` to `26.1.2`.
- Switched compile target to Java 25.
- Bumped Lombok to 1.18.40 for JDK 25 annotation-processing support.
- Added Slimefun runtime support for the new `26.1.x` Minecraft version scheme.
- Replaced one direct PaperLib version check with Slimefun's patched version gate.
- Added a fallback for bundled Dough version parsing so startup does not fail just because Dough does not yet understand `26.x`.
- Updated GitHub Actions Java setup to Java 25 and reduced the e2e matrix to 26.1.2/latest.

## Build command

Use JDK 25:

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

The compiled jar should appear under `target/`.

## Note

I could not compile inside this sandbox because the Maven wrapper could not download Maven/dependencies from Maven Central. Build this on your machine or GitHub Actions with Java 25.
