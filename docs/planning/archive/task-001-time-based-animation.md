# Task 001: Time-based animation

**Status:** Done (2026-10-03). Merged to `master` as `7c9a94d` and checked on screen by Chris
**Tier:** 2
**Owner:** Chris
**Branch:** `feat/task-001-time-based-animation`
**Source of Truth:** `MANIFESTO.md`

## 🛑 AGENT DIRECTIVE: THE "PLAN AND PAUSE" PROTOCOL
**CRITICAL INSTRUCTION FOR AI AGENTS:** You may NOT write application code or modify project files, other than this task file's plan section, until Phase 0 is approved.

* **Phase 0: Planning and alignment**
    1. Read the Context and Objectives below.
    2. Read `MANIFESTO.md`, `MEMORY.md` and `src/sparkl/quadric.clj`.
    3. Fill in the Implementation Plan: the file changes, the tests, and your answer to each open question.
    4. **HALT.** Ask Chris: *"Does this implementation plan look correct? Please approve before I begin execution."*

---

## 1. Context and problem statement

The first item on the README's Future Work list: *"Base rotation on time so the rotation speed will be independent of the frame rate."*

Today the rotation advances by a fixed amount each frame. In `src/sparkl/quadric.clj`:

- `set-angle` adds `(/ (/ (* rpm rotation) 15) framerate)` to `orient` on every `draw` call.
- So the speed on screen is that amount times the frame rate the machine *actually* achieves. A heavy surface or a slow machine makes the rotation slower. The comment on `framerate` even suggests lowering it when the animation is choppy, which (as written) changes nothing except the size of each step.
- `speed` is labelled "rpm", but `speed 1` produces one revolution every **15 seconds** at the target rate, which is 4 rpm. The `/ 15` is unexplained.
- When `orient` passes 2π, it resets to `0` instead of wrapping. That throws away the overshoot and causes a tiny hitch once per revolution.
- Pausing (`space`) works by skipping `set-angle`, so the angle simply freezes. That has to keep working.
- **Video mode** (`render-frames true`) saves one PNG per `draw` call for `frame-count` frames. Saving is slow, so if this mode followed the wall clock, the frames would come out unevenly spaced. Video frames must stay evenly spaced in angle.

## 2. Objectives

* The rotation speed on screen is the same whatever frame rate the machine achieves. Doubling or halving `framerate` changes only how smooth the motion is, not how fast it turns.
* `speed` means something exact, with a name and comment that say what it is (see question 1).
* Pause and resume, including switching surfaces while paused, keep working with no jump when resuming.
* Video mode stays deterministic: frame *n* is at the angle for time *n / framerate*, however long each save takes.
* The angle wraps continuously, with no hitch once per revolution.
* The angle math is a pure function, tested without opening a window (README rule 8).

## 3. Open questions for Chris

1. **What should the speed be?** ✅ **Decided by Chris (2026-10-03):** make the label correct. The setting is true revolutions per minute. A keyboard control for changing the rpm comes later, in its own task.
   * *Remaining:* the default value. `rpm 1` is 4× slower than today. `rpm 4` keeps today's look (one revolution every 15 s). **Recommendation: `rpm 4`.**
2. **After a pause, where should the rotation resume?** Recommendation: exactly where it stopped. Accumulate elapsed time only while animated; don't compute the angle from wall-clock time since startup.
3. **Big frame gaps.** If a frame takes a very long time (the window was dragged, or the machine slept), should the step be capped so the surface doesn't jump? Recommendation: cap each step at 100 ms.

## 4. Implementation plan
*Phase 0 plan written 2026-10-03. Approved by Chris the same day, with `rpm 4` and the recommended answers to questions 2 and 3.*

**The shape.** Two pure functions do all the math, and `draw` only feeds them a clock reading. The angle accumulates from per-frame time steps rather than being computed from time since startup, which is what makes pause and resume free (question 2).

**Files touched:** `src/sparkl/quadric.clj`, `test/sparkl/quadric_test.clj` (new), `test/sparkl/core_test.clj` (deleted), `README.md`, `docs/planning/MEMORY.md`, `docs/planning/ROADMAP.md`. `core.clj` and `surfaces.clj` are untouched.

### Phase A: Tests (the behaviour)
New `test/sparkl/quadric_test.clj`, requiring `sparkl.quadric` (never `sparkl.core`). Compare floats with a small tolerance. After each assertion, the change that would turn it red:

* [x] **The rate is correct.** At 1 rpm, a 15 000 ms step from angle 0 gives τ/4. *Red if* the old `/ 15` factor survives, or the units are wrong (seconds versus ms).
* [x] **rpm scales.** At 4 rpm, a 15 000 ms step gives one full turn, back to ~0 (mod τ). At 2 rpm, 7 500 ms gives τ/4. *Red if* rpm is ignored or inverted.
* [x] **The wrap keeps the remainder.** At 1 rpm, from 3τ/4, a 30 000 ms step gives τ/4, not 0. *Red if* the reset-to-zero behaviour comes back. The result also stays in [0, τ).
* [x] **A zero step changes nothing.** `advance` with dt 0 returns the angle unchanged.
* [x] **Frame-rate independence.** Starting from the same angle, 60 steps of 1000/60 ms, 30 steps of 1000/30 ms, and a single 1000 ms step all land on the same angle. *Red if* the step depends on `framerate`.
* [x] **Measuring the step** (`frame-step prev now cap`): no previous reading (`nil`) gives 0, so there's no jump on the first frame; 16 → 16 ms; a 5 000 ms gap gives the 100 ms cap; a negative gap gives 0. *Red if* the cap or the guards are missing.
* [x] Delete `test/sparkl/core_test.clj` (the template test that asserts `(= 0 1)` and opens the window).

### Phase B: Logic (`quadric.clj`)
* [x] Replace `speed`, `rotation`, `zero` and `set-angle` with:
  * `(def rpm 4)`: revolutions per minute, with an exact comment.
  * `(def tau (* 2 Math/PI))`: one full revolution, in radians.
  * `(def max-step-ms 100)`: the longest time step one frame may advance.
  * `advance [angle dt-ms rpm]`: a pure function returning `(mod (+ angle (* tau rpm (/ dt-ms 60000.0))) tau)`.
  * `frame-step [prev-ms now-ms cap-ms]`: a pure function returning the clamped elapsed time; 0 when `prev-ms` is nil.
* [x] Add `(def last-millis (atom nil))` beside the other runtime atoms.
* [x] Fix the comment on `framerate`. It now only affects smoothness and how frames are spaced in video mode.

### Phase C: Drawing and integration (`draw`)
* [x] At the end of `draw`, where `set-angle` is called today:
  * **Live mode:** read `q/millis`, compute `frame-step` from `last-millis`, and store the new reading **on every frame, paused or not**. That way the first frame after resuming advances by one frame's time, not by the length of the pause. When `animated?`, `swap! orient advance dt rpm`.
  * **Video mode:** unchanged in structure, but each saved frame advances by a fixed `(/ 1000.0 framerate)` ms instead of using the clock, so frame *n* sits at time *n / framerate*.
* [x] Switching surfaces needs no change, because `orient` is shared across surfaces, as it is today.
* [x] Update the README: the speed setting is described as `rpm`, item 1 is removed from Future Work, and "rotation speed, framerate" in Usage reads correctly.
* [x] Update MEMORY: replace the "`speed` is labelled rpm but isn't" note with how timing now works.
* [x] Add a "keyboard control for rpm" row to the ROADMAP backlog.

**Risk.** `q/millis` counts from sketch start, so the first frame's step is 0. That is handled by the `nil` guard, and the test covers it. No other risks found: `orient` is read only in `point-cloud`, and `set-angle` is called only from `draw`.

## 5. Definition of done
- [x] `lein test` passes. The tests cover the amount of advance for a given time step, wrapping past 2π with the remainder kept, a zero time step, and the step cap. Each assertion can fail.
- [ ] `lein run` starts cleanly.
- [ ] **On screen:** with `framerate` set to 30 and then to 60, a full revolution takes the same time, measured with a stopwatch against the expected period.
- [ ] **On screen:** pausing and resuming continues smoothly from the same angle, with no jump.
- [ ] Video mode still writes `frame-count` frames, evenly spaced in angle.
- [x] The README drops item 1 from Future Work and describes the speed setting correctly. `MEMORY.md`'s "`speed` is labelled rpm" note is updated.

## 6. Builder report (2026-10-03)

**Branch:** `feat/task-001-time-based-animation`

**Changed:**
* `src/sparkl/quadric.clj`: `speed`, `rotation`, `zero` and `set-angle` are gone. In their place are `rpm 4`, `tau`, `max-step-ms 100`, the pure functions `advance` and `frame-step`, and a `last-millis` atom. `draw` now advances by measured time in live mode and by a fixed `1000 / framerate` ms per saved frame in video mode.
* `test/sparkl/quadric_test.clj`: new. 5 tests, 13 assertions. The template `core_test.clj` is deleted.
* README, MEMORY and the ROADMAP backlog are updated as planned.

**Verified:**
* `lein test`: 5 tests, 13 assertions, 0 failures.
* **Mutation check.** Each breakage was applied, the tests run, and the file restored:
  * the old rate factor (`/ 15000`): 3 failures
  * the reset-to-zero wrap: 1 failure
  * removing the cap and guards from `frame-step`: 2 failures
* The frame-rate independence test is the weakest. With `advance` taking milliseconds it can only fail if someone reintroduces `framerate` into the step.

**Not verified (Chris, on screen):**
* That `lein run` starts and the rotation looks the same as before: one turn every 15 s.
* The stopwatch check at `framerate` 30 and 60.
* That pause and resume are smooth.
* Video mode (`render-frames true`) was not run.
