# Task 002: Speed and direction keys

**Status:** Active. Phase 0 plan written, awaiting approval
**Tier:** 2
**Owner:** Chris
**Branch:** `feat/task-002-speed-and-direction-keys`
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

Task 001 made `rpm` a true revolutions-per-minute setting, but it is a constant in `quadric.clj`, so changing the speed means editing code and restarting. Chris wants to change the speed and direction live from the keyboard. *(This was the "Keyboard control for rpm" item on the ROADMAP backlog.)*

## 2. Objectives

As Chris asked:
* `↑` raises the rpm by 1, and `↓` lowers it by 1.
* `shift`+`↑` raises the rpm by 10, and `shift`+`↓` lowers it by 10.
* `tab` reverses the direction of rotation.

Also:
* The HUD shows the current speed and direction, so the effect of a key press is readable, not just visible.
* The README key table, the HUD key hints and the `-main` help text list the new keys.

## 3. Open questions for Chris

1. **How slow can it go?** Recommendation: stop at **0 rpm**. `↓` at 0 does nothing, and direction is only ever changed with `tab`. The alternative is to let `↓` carry on through 0 into the reverse direction, but then `↓` and `tab` would overlap.
2. **How fast can it go?** Recommendation: cap at **60 rpm** (one turn a second). At 30 fps that is already 12° per frame. Much beyond it, the point cloud starts to strobe, and the wagon-wheel effect makes it look as if it's slowing down.
3. **What should the HUD show?** Recommendation: `4 rpm` normally, and `4 rpm reversed` after `tab`, placed next to the surface name.
4. **Should switching surfaces reset the speed?** Recommendation: **no**. The speed and direction are global, the same way pause and the axes toggle already are.

## 4. Implementation plan

*Phase 0 plan written 2026-10-03. Awaiting approval.*

**The shape.** Speed is held as two values: a magnitude `rpm` (0 to 60) and a `direction` (+1 or −1). `draw` passes `(* direction rpm)` to `advance`, which already handles negative values correctly through `mod`. Keeping them separate means `tab` doesn't lose the speed, and `↓` can't flip the direction by accident. The key decisions go through one small pure function, so they can be tested without a window.

**Files touched:** `src/sparkl/quadric.clj`, `src/sparkl/core.clj` (help text only), `test/sparkl/quadric_test.clj`, `README.md`, `docs/planning/MEMORY.md`, `docs/planning/ROADMAP.md`.

### Phase A: Tests (the behaviour)
Added to `test/sparkl/quadric_test.clj`. After each assertion, the change that would turn it red:

* [ ] **`adjust-rpm`, the step and the clamp.** From 4: +1 gives 5, −1 gives 3, +10 gives 14. From 3, −10 gives 0, not −7. From 55, +10 gives 60, not 65. From 0, −1 gives 0. *Red if* the clamp is missing at either end, or the step is wrong.
* [ ] **`speed-delta`, mapping a key to a step.** `:up` gives +1, `:down` −1, `:up` with shift +10, `:down` with shift −10, and any other key nil. *Red if* shift is ignored, or the signs are swapped.
* [ ] **Reverse rotation.** `(advance a dt -4)` turns backwards by exactly as much as `(advance a dt 4)` turns forwards, and the result stays in [0, τ). Example: at −1 rpm, from τ/4, 15 000 ms gives 0. *Red if* `advance` mishandles negative rpm, such as a `rem` instead of a `mod`.

### Phase B: Logic (`quadric.clj`)
* [ ] `rpm` becomes the starting value: `(def start-rpm 4)`. Add `(def max-rpm 60)`.
* [ ] New runtime atoms: `(def rpm (atom start-rpm))` and `(def direction (atom 1))`.
* [ ] `adjust-rpm [rpm delta]`: a pure function returning `(-> (+ rpm delta) (max 0) (min max-rpm))`.
* [ ] `speed-delta [k shift?]`: a pure function returning ±1, ±10 or nil.

### Phase C: Keys, drawing and integration
* [ ] In `key-pressed`, read `(q/key-modifiers)` for `:shift`. When `speed-delta` returns a step, `swap! rpm adjust-rpm step`.
* [ ] **Tab.** Quil's `key-as-keyword` has no name for tab: it would produce the awkward `(keyword "\t")`. So check `(= (q/raw-key) \tab)` instead, and `swap! direction -` on a match.
* [ ] `draw`: in both live and video mode, pass `(* @direction @rpm)` to `advance`.
* [ ] `draw-hud`: show `N rpm`, plus `reversed` when the direction is −1. Add the new keys to the hints: `↑/↓ speed (shift ×10)   tab: reverse`.
* [ ] Update the help line in `core.clj`'s `-main`, the comment block at the top of `quadric.clj`, and the README key table.
* [ ] MEMORY: note the rpm/direction split and the tab detection. ROADMAP: replace the backlog row with task 002.

**Risks.**
* **Tab might never reach the sketch.** Java's windowing toolkit uses tab to move focus between controls, and with the `:p2d` (JOGL) renderer, Processing may consume tab before Quil sees it. This can't be tested headless. If it fails on screen, the fallback is to ask you for a different key, such as `r` for reverse. I won't silently pick one.
* **The HUD line gets long.** At small window widths it may get clipped. It's fullscreen today, so it's acceptable for now.

## 5. Definition of done
- [ ] `lein test` passes, and each new assertion can fail (checked by breaking the code on purpose, as in 001).
- [ ] `lein run` starts cleanly.
- [ ] **On screen:** `↑` and `↓` change the HUD's rpm by 1, and with shift by 10. The speed visibly follows. It stops at 0 and at 60.
- [ ] **On screen:** `tab` reverses the rotation with no jump, the HUD shows `reversed`, and a second `tab` restores it.
- [ ] The README key table, the HUD hints and the `-main` help all list the new keys.
