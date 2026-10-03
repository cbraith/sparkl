# Agent Conventions — sparkl

This repo runs on an Architect/Builder workflow borrowed from Gatefold. **Source of truth lives in `docs/planning/`.** Read these before doing anything:

- `MANIFESTO.md`: what sparkl is, how the code is shaped, and its conventions
- `ROADMAP.md`: the task index and the current focus
- `MEMORY.md`: hard-won facts and sharp edges
- `README.md`: the workflow protocol (Plan-and-Pause; one task in `active/` at a time)

## Rules that matter most

- **No blind coding.** Every change traces to a task file in `docs/planning/active/`.
- **Plan, then HALT.** A plan request means zero code changes. Build only after Chris approves the plan.
- **Humans merge; agents propose.** Work on a `feat/task-XXX-name` or `fix/task-XXX-name` branch. Never commit to `master` and never merge.
- **Don't load `sparkl.core` in tests or at the REPL by accident.** Its `defsketch` opens a fullscreen window the moment the namespace loads. Test `sparkl.quadric` and `sparkl.surfaces` directly.

## Commands

```bash
lein run      # open the sketch fullscreen (esc quits)
lein test     # run clojure.test suites under test/
```
