# Task 000: Revival

**Status:** Done (2026-10-03)
**Tier:** 2
**Owner:** Chris
**Commit:** `e038ebc` "restart sparkl and add keyboard controls" (on `master`, before this workflow existed)

*This file was written after the fact, from the commit, to give the archive a starting point.*

## 1. Context

sparkl was last touched in 2019, on Clojure 1.8 and Quil 2.7.1. Chris restarted it with an agent in October 2026.

## 2. What changed

- `project.clj`: Clojure 1.8.0 → 1.12.1, Quil 2.7.1 → 4.3.1563.
- `quadric.clj`: the current surface became an atom, so it can be switched at runtime. Added `select-surface!`, `step-surface!` and `key-pressed`, a HUD line (`draw-hud`), an axes toggle and a pause toggle. Points where the surface is undefined (NaN) are dropped from the point cloud.
- `surfaces.clj`: the surface settings were adjusted, and the hyperboloid of one sheet handles its undefined waist.
- `core.clj`: wired `:key-pressed` and printed the key help at startup.
- `README.md`: added the key table and noted the JDK 17 requirement.

## 3. Left for later

- The rotation is still frame-based. That is task 001.
- There is no real test suite. The template `core_test.clj` remains.
