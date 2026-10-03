# Project sparkl: State Manifesto

**Source of truth** for what sparkl is and how it is built. When this file and the code disagree, raise it. Don't silently pick one.

## 1. The core philosophy

### Why this exists

sparkl animates quadric surfaces in Clojure. It is inspired by George Haroney's article "Graphing Quadric Surfaces" in the December 1986 issue of BYTE, which drew them in BASIC. The project started in 2018 as a way to learn Clojure, sat for years, and was revived in October 2026.

It is a personal playground, not a product. The goals, in order:

1. **It is a pleasure to watch.** Smooth motion and clear, legible surfaces.
2. **It is a pleasure to read.** Idiomatic, small Clojure that a returning Chris can follow.
3. **It is faithful to the method.** The BYTE approach is the heart of the project: solve the quadric for z, build a point cloud over an xy grid, and project with three axis angles. Improve it, but don't replace it with a general 3D engine without a deliberate decision.

### The core loop

Run `lein run`. A surface spins fullscreen. Use the keyboard to change the surface, pause, or toggle the axes. Optionally, write frames to disk to make a video.

## 2. Technical architecture

### The stack

| Piece | Version | Notes |
| --- | --- | --- |
| Clojure | 1.12.1 | |
| Quil | 4.3.1563 | Processing wrapper. Fullscreen, `:p2d` renderer, `:present` mode |
| JDK | 17+ | |
| Build | Leiningen | `lein run`, `lein test` |

### The namespaces

| File | Role |
| --- | --- |
| `src/sparkl/core.clj` | Defines the sketch (`defsketch`) and `-main`. **Loading it opens the window.** |
| `src/sparkl/quadric.clj` | Point cloud, rotation, projection, runtime state, keyboard, `setup` and `draw` |
| `src/sparkl/surfaces.clj` | The surface equations and each surface's settings map |
| `src/sparkl/styling.clj` | Named colours (aliased as `hue`) |

### The rendering method

1. **Point cloud.** For each (x, y) on a grid within ±`sheet-size`, rotated about z by the current `orient` angle, solve the surface for z. Drop NaN points, where the surface is undefined, and mirror the rest as the surface's settings ask.
2. **Projection.** Each axis makes an angle (Ax, Ay, Az) with the screen. Map 3-space to the screen with
   `h = x·cosAx + y·cosAy + z·cosAz + h0` and `v = x·sinAx + y·sinAy + z·sinAz + v0`.
3. **Drawing.** Plot each point as a 2×2 pixel block. Points with y > 0 take the surface's back colour, the rest its front colour.
4. **Animation.** `orient` advances in `draw`. *(Today it advances per frame. Task 001 makes it advance with time.)*

### Surface settings

Each entry in `surfaces/settings` is keyed by a surface keyword and carries `:function`, `:constants [a b c]`, `:grid-x` and `:grid-y`, `:mirror`, `:angles [Ax Ay Az]` in degrees, `:origin [h0 v0]`, `:fore-color`, `:aft-color` and `:animated`.

### Runtime state

The atoms in `quadric.clj` hold `current-surface`, `animated?`, `axis?`, `orient` and `counter` (frames saved for video). The keyboard is the only thing that changes them, besides `draw` advancing the animation.

## 3. Conventions

- **Pure functions in the middle, effects at the edges.** See README rule 8.
- **Docstrings go before the argument vector.** Some older functions put them after, which makes them plain strings, not docstrings. Fix them when you touch them; don't sweep them in unrelated tasks.
- **Settings are data.** A new surface is a function plus a settings map, not new drawing code.
- **Keep the README's key table in step** with `key-pressed` and the HUD text.

### Git conventions

- Branches: `feat/task-XXX-name` or `fix/task-XXX-name`, cut from `master`.
- Commit messages are plain, present-tense summaries.
- Agents never commit to `master` and never merge. Chris merges.

## 4. Definition of done (every task)

- `lein test` passes, and every new assertion could fail (README rule 7).
- `lein run` starts cleanly, and the change has been checked on screen by Chris.
- The README is updated if behaviour or keys changed.
- `MEMORY.md` gains anything learned the hard way.
