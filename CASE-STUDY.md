# Ninjas-World

A 2D pixel-art platformer in Java that runs on a small game engine I wrote myself — software-rendered game loop, lighting, audio, input, and windowing, with no external libraries.

## Overview

Ninjas-World is a side-scrolling platformer: a player character fights enemies, collects coins, and unlocks doors across levels, with a following camera, main menu, HUD, and simple dynamic lighting. It was originally created as a school project and was **graded 95/100** — the only concrete metric documented for this project. It runs on a self-written mini engine (`src/engine/`) that renders into an `int[]` pixel buffer. The project is archived and serves as supporting evidence of low-level fundamentals.

## Why I Built It

It was a school project (grade documented above) with the additional self-imposed constraint of not using an existing game framework, so that rendering, timing, and input were implemented from scratch rather than consumed from a library.

## My Role

I wrote all of it: the engine (game loop, renderer, lighting, fonts, audio, input, window) and the game content on top of it (player, enemies, level objects, camera, game manager). Third-party usage is limited to the JDK itself (Swing/AWT for the window, `ImageIO` for asset loading).

## Technology Stack

| Technology | Purpose |
|---|---|
| Java (SE) | Language, JDK only — no external libraries or build tools |
| Swing/AWT | Window and image raster |
| `ImageIO` | Loading sprite/sound assets |

## Implementation

**Engine (`src/engine/`):**
- `GameContainer` — fixed-timestep game loop on a dedicated thread with a 60 FPS update target, delta-time accumulation, and per-second frame counting.
- `Renderer` — software renderer drawing into an `int[]` pixel buffer backed by a `BufferedImage` raster; z-depth-sorted deferred render requests (`RenderRequest`), a shadow-blocking mask (`lightable` bitmap), and additive light rendering via per-scanline `drawLightLine` ray filling (`LightRequest`).
- `Image`/`ImageTile` sprites, bitmap `Font` with a `FontGenerator`, keyboard `Input`, and `Window` management.
- `SoundClip` — lightweight audio clip playback.

**Game (`src/game/`):** component-based `GameObject`s with an `AABBComponent` for collision, a `Camera` that follows the player, player with sword attack, mushroom enemies, and interactive objects (coins, level/stone doors, buttons, waterfalls, invisible blocks). The game renders at a native 384×216 and is upscaled ~2.5× for a pixel-art look.

## What I Learned

- How a fixed-timestep loop is actually structured: update/dt accumulation, sleeping the thread, and frame-time measurement.
- Software rendering fundamentals: writing pixels into a raster, z-ordering via deferred sorted draw requests, and the cost of per-pixel work.
- Implementing 2D lighting as a geometric problem (scanline/ray light fills) rather than a shader.
- Structuring a component-based entity/object system instead of one monolithic class.

## Performance / Scale / Results

No formal performance measurements were recorded for this project. The 95/100 school grade is the only documented evaluation. Metrics worth adding: frame time at the native resolution, worst-case number of render/light requests per frame.

## Testing

None. There are no automated tests; verification was manual play. The highest-value additions would be unit tests for the loop's time accumulation and the collision math in `Physics`/`AABBComponent`.

## Current Status

🟡 Archived. The game was uploaded in one batch (July 2021) with no incremental commit history; it compiles with a plain `javac` and runs on Java 8+. I am not actively developing it.

## Portfolio Relevance

- **What this project demonstrates:** understanding of what a game engine actually does below the framework level — loops, rasterization, lighting, input, audio — in ~32 Java source files with zero dependencies.
- **Why it is interesting:** most candidates can use Unity or LibGDX; far fewer can explain or implement the fixed-timestep loop and software rasterization underneath.
- **Limitations:** it is a school project with no users, no tests, no measurements, and no multiplayer or content pipeline. It does not demonstrate production engineering practices.
- **Best thing to show a recruiter:** the `Renderer` and `GameContainer` source — short, readable, and easy to talk through in an interview.

## Interview Discussion

1. Why a fixed-timestep loop, and what breaks if you tie updates to frame rate?
2. How does your lighting implementation work, and what is its performance profile per light?
3. Why sort deferred render requests instead of drawing immediately?
4. How does the AABB collision resolve when the player moves fast enough to tunnel through a block?
5. What would you change to render at a modern resolution without changing the pixel-art aesthetic?

## Honest Assessment

Internal portfolio-evaluation ratings (1–10, not objective measurements):

| Aspect | Score | Notes |
|---|---|---|
| Technical Depth | 4 | Hand-written loop, renderer, and lighting are real fundamentals work, but scope is small |
| Engineering Quality | 3 | No tests, no build tooling, single-batch upload without development history |
| Uniqueness | 5 | Custom software renderer and lighting are uncommon at this level |
| Real-World Value | 5 | Strong signal of genuine low-level understanding for junior roles |
| Portfolio Value | 4.5 | Supporting project; valuable next to framework-based work, not a headline |

No projected change: the project is archived and I do not plan further development.

## Recruiter Summary

- **In one sentence:** A Java 2D platformer built on a from-scratch, software-rendered mini engine (fixed-timestep loop, z-sorted rendering, 2D lighting), created as a school project graded 95/100.
- **The strongest signal this project sends:** I understand rendering and game-loop fundamentals below the level of any framework.
- **The biggest weakness:** no tests, no measurements, and no development history — it was uploaded as a finished batch.
- **What I would improve before showcasing it:** nothing on the code itself; I would present it alongside a larger, actively maintained project.

## Evidence & Missing Information

- **Evidence:** repository inspection (README.md, `src/engine/` and `src/game/` source files including `GameContainer.java` and `Renderer.java`), git history (initial upload July 2021, README added 2026), and the project dossier.
- **Missing Information:**
  - [TODO: school/course name and year for the 95/100 grade context]
  - [TODO: measured frame times on reference hardware]
  - [TODO: asset sources (were sprites/audio self-made or third-party?)]
