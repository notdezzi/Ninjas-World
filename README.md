# Ninjas-World

**Ninjas-World** is a 2D platformer game written in Java, originally created as a school project (graded 95/100). It runs on a small custom-built game engine — a software-rendered game loop with its own window, renderer, input, audio, and lighting systems — and features pixel-art graphics, enemies to fight, coins to collect, and doors to unlock.

## Features

**Game**
- Side-scrolling 2D platformer gameplay with a player character and enemy entities
- Collectible coins, doors and stone doors, buttons, waterfalls, and other interactive level objects
- Camera that follows the player
- Main menu and in-game HUD
- Simple 2D lighting effects (light blocks, dynamic light rendering)
- Sound effects via a lightweight audio clip player
- Pixel-art style: renders at a native 384x216 resolution, upscaled ~2.5x for a retro look

**Custom engine (`src/engine/`)**
- Fixed-timestep game loop on a dedicated thread (60 FPS update target)
- Software renderer drawing into an `int[]` pixel buffer (`BufferedImage` raster)
- Sprite/animation support (`Image`, `ImageTile`), bitmap font rendering, light rendering
- Keyboard input handling and window management

## Tech Stack

- Java (standard edition, Swing/AWT for the window, `ImageIO` for assets)
- No external libraries or build tools — just the JDK

## Prerequisites

- A JDK (Java 8 or newer should work; the code uses no modern language features)

## Building & Running

From the repository root:

```bash
# Compile (all sources live under src/, entry point is Start.java in the default package)
javac -d out $(find src -name "*.java")

# Run (res/ must be on the classpath so textures and sounds can be loaded)
java -cp "out;res" Start      # Windows
java -cp "out:res" Start      # Linux / macOS
```

On Windows with `cmd`, list the sources first, e.g.:

```bat
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
```

The game window opens as "Far Meadow - dev Build" — the title reflects the currently loaded level.

## Project Structure

```
Ninjas-World/
├── src/
│   ├── Start.java          # Entry point: creates GameManager + GameContainer
│   ├── engine/             # Custom mini game engine
│   │   ├── GameContainer.java  # Game loop, threading, scale/window config
│   │   ├── Window.java         # Swing window
│   │   ├── Renderer.java       # Software pixel renderer
│   │   ├── Input.java          # Keyboard/mouse input
│   │   ├── AbstractGame.java   # Base class for games (init/update/render)
│   │   ├── audio/              # SoundClip playback
│   │   └── gfx/                # Images, sprite tiles, fonts, lighting
│   └── game/               # The game itself
│       ├── GameManager.java    # Game state, level loading, main menu
│       ├── Camera.java         # Player-following camera
│       ├── Physics.java        # Collision/physics helpers
│       ├── entities/           # Player and enemies
│       ├── objects/            # Coins, doors, buttons, waterfalls, ...
│       └── components/         # Reusable gameplay components
├── res/                    # Textures, sprites, GUI images, sounds
├── imgs/                   # Screenshots
└── LICENSE
```

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE).
