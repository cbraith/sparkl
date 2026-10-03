# sparkl Memory

Decisions and sharp edges, newest at the bottom of each section. No code dumps.

## Environment

- **Loading `sparkl.core` opens the sketch.** `defsketch` runs when the namespace loads, not when `-main` is called. Tests and REPL sessions should require `sparkl.quadric` or `sparkl.surfaces`, never `sparkl.core`. The stock template test that required it was deleted in task 001; tests live in `test/sparkl/quadric_test.clj`.
- **Revived 2026-10-03** on Clojure 1.12.1 and Quil 4.3.1563 (from 1.8.0 and 2.7.1) with JDK 17+. See `archive/task-000-revival.md`.

## Rendering and animation

- **Rotation is time-based (task 001).** `rpm` is true revolutions per minute; the default of 4 matches the pre-001 look (one turn every 15 s). Before 001, `speed 1` was labelled rpm but meant 4 rpm, and rotation slowed whenever the frame rate dropped.
- **The angle accumulates per frame; it is not computed from time since start.** That's what makes pause free: the clock is read every frame, even while paused, and only the advance is skipped. Each frame's step is capped at `max-step-ms` (100), so a stall such as a window drag doesn't make the surface leap.
- **Speed is two atoms: `rpm` (0 to `max-rpm` 60) and `direction` (±1) (task 002).** `draw` passes their product to `advance`. Keeping them apart means `tab` keeps the speed and `↓` can never reverse it. `start-rpm` is the starting value.
- **Quil has no keyword for tab.** `key-as-keyword` turns it into `(keyword "\t")`, so `key-pressed` matches `(q/raw-key)` against `\tab` instead. Shift comes from `(q/key-modifiers)`.
- **Video mode writes `resources/seq4-N.png`** for `frame-count` frames when `render-frames` is true. Frames there must stay evenly spaced in angle, whatever the wall clock does, so video mode advances a fixed `1000 / framerate` ms per saved frame and never reads the clock.

## Housekeeping

- `CHANGELOG.md` is still the Leiningen template's placeholder ("widget maker" entries). It describes nothing real.
- The revival commit (`e038ebc`) also committed `Claude outputs/usage-band-plugin.tgz`, a Claude Code plugin unrelated to sparkl.
