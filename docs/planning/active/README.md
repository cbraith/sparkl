# Active Tasks

This directory holds the task in flight. Completed tasks move to [`../archive/`](../archive/).

**Currently active:** none. The next task comes from the backlog in [`../ROADMAP.md`](../ROADMAP.md).

[`../ROADMAP.md`](../ROADMAP.md) is the index.

## If you are picking this up cold

Read these in order: [`../MANIFESTO.md`](../MANIFESTO.md), then [`../MEMORY.md`](../MEMORY.md), then the active task file.

Traps:

- **Requiring `sparkl.core` opens a fullscreen window.** Test `sparkl.quadric` instead.

## Recently completed

**Task 002, speed and direction keys (2026-10-03).** `↑`/`↓` change the rpm by 1 (shift: 10, clamped to 0 to 60), and `tab` reverses. Merged as `071eb1e`. See [`../archive/task-002-speed-and-direction-keys.md`](../archive/task-002-speed-and-direction-keys.md).

**Task 001, time-based animation (2026-10-03).** Rotation follows elapsed time, `rpm` is true revolutions per minute, and video frames stay evenly spaced. Merged as `7c9a94d`. See [`../archive/task-001-time-based-animation.md`](../archive/task-001-time-based-animation.md).

**Task 000, revival (2026-10-03).** Upgraded the dependencies, added keyboard surface selection, pause, the axes toggle and the HUD. Committed as `e038ebc`. See [`../archive/task-000-revival.md`](../archive/task-000-revival.md).
