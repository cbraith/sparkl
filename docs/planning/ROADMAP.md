# sparkl Roadmap

**This file is an index.** One line per task, and where to read more. The reasoning lives in the task files (`active/`, `archive/`), not here.

---

## Where things stand

sparkl runs again (task 000, 2026-10-03). It uses current Clojure and Quil, renders all six surfaces, and switches surfaces, pauses and toggles axes from the keyboard.

**Now:** nothing active. Tasks 001 (time-based rotation) and 002 (speed and direction keys) are done. Pick the next task from the backlog.

---

## Task board

| # | Task | Status | One line |
|---|---|---|---|
| 000 | Revival | ✅ Done | Clojure 1.12 / Quil 4.3, keyboard controls, HUD. [archive](archive/task-000-revival.md) |
| 001 | Time-based animation | ✅ Done | Rotation speed independent of frame rate; true `rpm`; deterministic video frames. [archive](archive/task-001-time-based-animation.md) |
| 002 | Speed and direction keys | ✅ Done | `↑`/`↓` change rpm by 1 (shift: 10), `tab` reverses. [archive](archive/task-002-speed-and-direction-keys.md) |

## Backlog

These come from the README's Future Work list. Numbers get assigned when a task is opened.

| Idea | Notes |
|---|---|
| Video from rendered frames | Stitch the PNG sequence into a video, for example with ffmpeg. Builds on 001's fixed-step render mode. |
| UI for render values | Tweak constants, grid spacing and angles live, beyond selecting surfaces. |
| Rendering performance | Profile first. Per-pixel `set-pixel` calls are the likely cost. Questions whether Quil is the right tool (Tier 3). |
| Axes in the 3D illusion | Axes rotate and occlude along with the surface. |
| More complex surfaces | New equations as data in `surfaces.clj`. |
| Wider test suite | 001 started `quadric_test.clj` (timing only). Projection, rotation and the surface functions are still untested. |
| Clean up CHANGELOG | Replace the template placeholder, or delete it. |
