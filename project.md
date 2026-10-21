# Ninjas-World — project overview

A 2D side-scrolling pixel-art platformer written in pure Java (Swing/AWT, zero
external libraries), originally built as a school project in 2021 and graded
95/100 according to its own README and CASE-STUDY. It runs on a small
self-written game engine: a fixed-timestep 60 FPS loop on a dedicated thread, a
software renderer that draws into an `int[]` pixel buffer at a native
384x216 (upscaled ~2.5-3x), a z-buffer with deferred z-sorted draw requests, a
Bresenham-based 2D lighting system with a light-block mask, bitmap fonts, and
raw AWT keyboard/mouse input.

## The game

- Player: run, jump, double jump, sword swing on mouse click, 3 lives, coins,
  an attack-cooldown HUD, and a player-following camera.
- Level 1: push a stone onto a button to open a stone door, avoid waterfalls
  that drain lives, collect coins, reach the level exit.
- Level 2: patrolling mushroom enemies, invisible blocks, a second exit.
- Enemies hurt on contact; the sword swing grants an invulnerability window
  rather than killing anything. No enemy is ever despawned.
- Levels are PNG collision masks (opaque black pixels are solid); entity
  placement is hardcoded per level in `GameManager.reloadentities()`.

## Scale (measured 2026-10-10)

- 33 Java files, ~4,600 lines total (32 game + engine files, 1 tooling file).
- 49 image assets in `res/`, zero audio files.
- 2 playable levels with assets; the code counts 3, and finishing level 2
  crashes on the missing `res/levels/l3.png`, a bug the project's own AutoShot
  bot documents and routes around.
- No tests, no build tool: plain `javac`, then `java -cp "out;res" Start`.

## Systems

- `engine/GameContainer`: accumulator loop locked to 1/60 s updates; renders
  only after a step ran; per-second FPS counting.
- `engine/Renderer`: five full-screen int buffers (frame, z, light map, light
  block, flags), alpha blending with z-test, deferred `RenderRequest`s sorted
  by depth, per-scanline `drawLightLine` ray filling blocked by the tileset
  mask.
- `engine/Window`: Swing JFrame + Canvas with double `BufferStrategy`.
- `engine/Input`: 600-key / 5-button arrays with edge detection, mouse scaled
  to native resolution.
- `game/`: component-based `GameObject`s, O(n^2) AABB pairwise physics,
  exponential-follow camera clamped to level bounds, tile-grid collision from
  the PNG masks.
- `engine/gfx/FontGenerator`: a standalone Swing tool that bakes any installed
  system font into the engine's bitmap-font PNG format (blue/yellow sentinel
  pixels mark glyph bounds).
- `autoshot/AutoShot` (2026): a 374-line harness that launches the real game,
  injects synthetic AWT key/mouse events into the engine's listeners, scripts
  a full playthrough (menu, controls, credits, level 1 bot run, level 2
  stills) with a jump bot, stuck detection and a 60 s safety kill, and saves
  pixel-perfect PNGs and GIF frames straight from the engine framebuffer.
  Source of everything in `shots/`.

## History and status

- Built July 2021 as school work. Uploaded to GitHub in one batch on
  2021-07-25 (3 commits that day; a documentation commit followed on
  2026-10-06; 4 commits total). No incremental commit history exists.
- Archived as a game. The 2026 pass added documentation (README, CASE-STUDY),
  the AutoShot screenshot bot, and two filename-case fixes; gameplay code is
  untouched since 2021.
- Honest gaps: the lighting pipeline is implemented but its only runtime call
  is commented out (only the ambient multiply is visible); no audio ships
  despite the `SoundClip` player; dead code (`Sword`, `menuclear()`,
  `setRenderLast()`, `setDead()`); three near-identical 285-line enemy
  classes; static mutable game state; a per-menu-tick `new Renderer(gc)`
  allocation (~1.7 MB, 60x per second); and the published git tree lacks the
  untracked assets (fonts, levels, player/enemy sprites) needed to run, so
  the committed tree alone does not compile-and-run. The working tree here is
  the source of truth.

## Naming

The repo is Ninjas-World. The shipped window title reads "Far Meadow - dev
Build" (`Start.java`), and an unused second entry point in `GameManager` says
"Ninja's World - dev Build".

See also CASE-STUDY.md for the project's own self-assessment.
