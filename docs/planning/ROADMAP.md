# sparkl Roadmap

**This file is an index.** One line per task, and where to read more. The reasoning lives in the task files (`active/`, `archive/`), not here.

---

## Where things stand

sparkl runs again (task 000, 2026-10-03). It uses current Clojure and Quil, renders all six surfaces, and switches surfaces, pauses and toggles axes from the keyboard.

**Now:** task 001, making the rotation time-based instead of frame-based.

---

## Task board

| # | Task | Status | One line |
|---|---|---|---|
| 000 | Revival | ✅ Done | Clojure 1.12 / Quil 4.3, keyboard controls, HUD. [archive](archive/task-000-revival.md) |
| 001 | Time-based animation | ▶ **Now** | Rotation speed independent of frame rate; video frames stay deterministic. [active](active/task-001-time-based-animation.md) |

## Backlog

These come from the README's Future Work list. Numbers get assigned when a task is opened.

| Idea | Notes |
|---|---|
| Video from rendered frames | Stitch the PNG sequence into a video, for example with ffmpeg. Builds on 001's fixed-step render mode. |
| UI for render values | Tweak constants, grid spacing and angles live, beyond selecting surfaces. |
| Rendering performance | Profile first. Per-pixel `set-pixel` calls are the likely cost. Questions whether Quil is the right tool (Tier 3). |
| Axes in the 3D illusion | Axes rotate and occlude along with the surface. |
| More complex surfaces | New equations as data in `surfaces.clj`. |
| Working test suite | Replace the template `core_test.clj` with real tests of the pure math. Task 001 may cover part of this. |
| Clean up CHANGELOG | Replace the template placeholder, or delete it. |
