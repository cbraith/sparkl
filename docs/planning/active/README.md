# Active Tasks

This directory holds the task in flight. Completed tasks move to [`../archive/`](../archive/).

**Currently active:** [Task 001: time-based animation](task-001-time-based-animation.md). It is waiting for its Phase 0 plan.

[`../ROADMAP.md`](../ROADMAP.md) is the index.

## If you are picking this up cold

Read these in order: [`../MANIFESTO.md`](../MANIFESTO.md), then [`../MEMORY.md`](../MEMORY.md), then the active task file.

Traps:

- **Requiring `sparkl.core` opens a fullscreen window.** Test `sparkl.quadric` instead.
- **`lein test` fails out of the box.** The template test asserts `(= 0 1)`.

## Recently completed

**Task 000, revival (2026-10-03).** Upgraded the dependencies, added keyboard surface selection, pause, the axes toggle and the HUD. Committed as `e038ebc`. See [`../archive/task-000-revival.md`](../archive/task-000-revival.md).
