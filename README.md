# Rat Client

Rat Client is a configurable Fabric client mod for Minecraft **1.21.11**. It groups its features into small modules, saves settings to `config/ratclient.json`, exposes individual keybinds in Controls, and opens its menu with **Right Shift**.

## Build

Install Java 25 for the current Loom build environment, then run the included Gradle wrapper. The generated mod targets Java 21:

```powershell
.\gradlew.bat build
```

The mod jar is written to `build/libs/rat-client-1.0.0.jar`. Fabric Loader and Fabric API are required in the game instance. The project pins Minecraft 1.21.11, Yarn mappings, Fabric Loader 0.20.1, and Fabric API 0.141.6.

GitHub Actions is configured in `.github/workflows/build.yml`. On pushes and pull requests, it builds the mod; successful runs publish the JAR as a 30-day workflow artifact. Open the run under the repository's **Actions** tab and download the `rat-client-1.0.0` artifact.

## Modules

| Module | Behavior | Default key |
| --- | --- | --- |
| Boat Fly | Applies directional and vertical velocity to the boat you ride; speed is adjustable. Integrated single-player only. | B |
| Schematic Builder | Loads a bounded JSON block list, compares it at the player position, and shows progress plus the next missing block and coordinates. It does not place blocks. | Unbound |
| Area Miner | Counts and previews nearby blocks of a selected type, with a 3–8 block radius. It never breaks blocks. | Unbound |
| Freecam | Moves a local camera independently and marks the player with a glow outline. Integrated single-player only. | F6 |
| Base Finder | Scores likely structure chunks from block patterns in loaded chunks within 24 blocks. It does not inspect hidden or unloaded terrain. | Unbound |

Boat Fly and Freecam automatically stay off outside an integrated single-player world. Base Finder's confidence value is a heuristic score, not a statistical probability, and can report natural or ordinary builds as candidates. Large schematic inputs are rejected above 10,000 block entries and coordinates are limited to 128 blocks from the anchor; Area Miner radius is capped at 8.

## Schematic file

Create `config/ratclient/schematics/example.json` in the game directory:

```json
{
  "blocks": [
    { "pos": [0, 0, 0], "block": "minecraft:stone_bricks" },
    { "pos": [1, 0, 0], "block": "minecraft:oak_planks" },
    { "pos": [0, 1, 0], "block": "minecraft:glass" }
  ]
}
```

Enable Schematic Builder and run `/rat schematic load example.json`. The first coordinate is anchored one block above the player's feet; subsequent coordinates are relative to it. The HUD lists the next missing block so you can place it manually.

## Controls

- **Right Shift** opens the module menu.
- Click **ON/OFF** to toggle a module.
- Click **−** to change its primary setting. Click **+** for its secondary setting (Area Miner radius). Right-click a row to advance its primary setting.
- In Boat Fly, WASD steers, Space rises, and Shift descends.
- In Freecam, WASD moves, Space rises, and Shift descends. Mouse look still controls direction.

All features are client-side. The two movement features intentionally require an integrated single-player world. Previews and heuristics do not make any block changes.
