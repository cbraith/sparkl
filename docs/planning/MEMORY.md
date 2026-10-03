# sparkl Memory

Decisions and sharp edges, newest at the bottom of each section. No code dumps.

## Environment

- **Loading `sparkl.core` opens the sketch.** `defsketch` runs when the namespace loads, not when `-main` is called. Tests and REPL sessions should require `sparkl.quadric` or `sparkl.surfaces`, never `sparkl.core`. The stock `test/sparkl/core_test.clj` from the Leiningen template does require it, and it asserts `(= 0 1)`. Replace it rather than trusting `lein test` as-is.
- **Revived 2026-10-03** on Clojure 1.12.1 and Quil 4.3.1563 (from 1.8.0 and 2.7.1) with JDK 17+. See `archive/task-000-revival.md`.

## Rendering and animation

- **`speed` is labelled rpm but isn't.** `set-angle` adds `rpm·2π / 15 / framerate` per frame, so `speed 1` is one revolution every 15 seconds at the target frame rate. The rotation also slows whenever the real frame rate falls behind the target. Task 001 addresses both.
- **Video mode writes `resources/seq4-N.png`** for `frame-count` frames when `render-frames` is true. Frames there must stay evenly spaced in angle, whatever the wall clock does.

## Housekeeping

- `CHANGELOG.md` is still the Leiningen template's placeholder ("widget maker" entries). It describes nothing real.
- The revival commit (`e038ebc`) also committed `Claude outputs/usage-band-plugin.tgz`, a Claude Code plugin unrelated to sparkl.
