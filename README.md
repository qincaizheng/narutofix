# NarutoFix

NarutoFix is a Minecraft Forge 1.12.2 addon for NarutoMod. It fixes and extends gameplay around dojutsu, Susanoo, chakra/body/soul energy, jutsu controls, and related NarutoMod systems.

## Environment

- Minecraft: 1.12.2
- Java: 8
- Mod id: `narutofix`
- Version: `0.2.9`
- Build system: Gradle with RetroFuturaGradle
- Required runtime mods:
  - NarutoMod
  - MixinBooter 7 or newer

## Main Features

- Adds NarutoFix-managed Susanoo entities and combat behavior.
- Supports dojutsu eye storage, equipped-eye handling, and related GUI/network sync.
- Adds body energy and soul energy capabilities with server/client synchronization.
- Adds awakening and bloodline capability handling.
- Adjusts NarutoMod behavior through Mixins and a coremod transformer.
- Registers helper commands for chakra, energy, dojutsu state, awakening, Susanoo color, cooldown, power, and ninja realm utilities.
- Includes client overlays and inventory render helpers for energy and NarutoMod HUD behavior.

## Project Layout

- `src/main/java/com/qdd/narutofix/` - mod source code
- `src/main/resources/` - Forge metadata, Mixin configs, access transformer, and assets
- `gradle/scripts/dependencies.gradle` - dependency declarations
- `build.gradle` and `gradle.properties` - build configuration

## Build

Use Java 8.

```bash
./gradlew build
```

The generated jar is written under `build/libs/`.

## Development Notes

- This project uses Mixins and a coremod plugin. Runtime JVM arguments are configured through `gradle.properties` and `build.gradle`.
- NarutoMod is pulled through CurseMaven in the Gradle dependency script.
- Do not remove runtime mod dependencies from the Gradle configuration.
- Local workspace notes and logs are intentionally ignored by Git.
