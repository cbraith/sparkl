# Task 001: Time-based animation

**Status:** Active, awaiting the Phase 0 plan
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

1. **What should the speed be?** Option A: keep today's look, one revolution every 15 seconds, and rename the setting to something honest such as `seconds-per-rev 15`. Option B: make `speed` truly rpm, so `speed 1` means one revolution a minute, 4× slower than today. **Recommendation: A**, since the current speed is presumably the one that looked right.
2. **After a pause, where should the rotation resume?** Recommendation: exactly where it stopped. Accumulate elapsed time only while animated; don't compute the angle from wall-clock time since startup.
3. **Big frame gaps.** If a frame takes a very long time (the window was dragged, or the machine slept), should the step be capped so the surface doesn't jump? Recommendation: cap each step at something like 100 ms.

## 4. Implementation plan
*(The Builder fills this in during Phase 0.)*

Starting points, not decisions:
- A pure function along the lines of `(advance angle dt-ms seconds-per-rev) → new angle`, wrapping with `mod`.
- `draw` measures the time since the last frame (`q/millis` or `System/nanoTime`), and passes that to `advance` when animated. In video mode it passes a fixed `1000 / framerate`.
- Replace the template `test/sparkl/core_test.clj`, which requires `sparkl.core` (opening a window) and asserts `(= 0 1)`, with `test/sparkl/quadric_test.clj`.

### Phase A: Tests (the behaviour)
* [ ]

### Phase B: Logic
* [ ]

### Phase C: Drawing and integration
* [ ]

## 5. Definition of done
- [ ] `lein test` passes. The tests cover the amount of advance for a given time step, wrapping past 2π with the remainder kept, a zero time step, and the step cap. Each assertion can fail.
- [ ] `lein run` starts cleanly.
- [ ] **On screen:** with `framerate` set to 30 and then to 60, a full revolution takes the same time, measured with a stopwatch against the expected period.
- [ ] **On screen:** pausing and resuming continues smoothly from the same angle, with no jump.
- [ ] Video mode still writes `frame-count` frames, evenly spaced in angle.
- [ ] The README drops item 1 from Future Work and describes the speed setting correctly. `MEMORY.md`'s "`speed` is labelled rpm" note is updated.
