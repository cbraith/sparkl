# sparkl Agentic Workflow Protocol

This directory (`docs/planning`) is sparkl's project board. It replaces Jira and Trello so that every bit of context is visible to the agents working on the code. The shape is adapted from Gatefold, scaled down for a one-person graphics project.

## 📂 Directory structure

```text
sparkl/
├── CLAUDE.md               # Short agent conventions; points here
└── docs/planning/
    ├── README.md           # This file (the protocol)
    ├── MANIFESTO.md        # Source of truth: purpose, architecture, conventions
    ├── ROADMAP.md          # Task index and current focus
    ├── MEMORY.md           # Lessons learned and sharp edges
    ├── TEMPLATES/
    │   └── task_template.md
    ├── active/             # CURRENT task (the "Doing" column)
    │   ├── README.md       # What is in flight and why
    │   └── task-XXX-name.md
    └── archive/            # COMPLETED tasks (the "Done" column)
```

## 🤖 Roles

| Role | Who | Responsibility |
| :--- | :--- | :--- |
| **The Owner** | Chris | Picks priorities, approves plans, runs the sketch, merges. |
| **The Architect** | Claude (planning session) | Writes and deepens task files, keeps ROADMAP and MEMORY honest. |
| **The Builder** | Claude (coding session) | Plans against a task file, halts, then implements and reports. |

One session can play both Architect and Builder. The halts still apply.

### Complexity tiers

| Tier | Description | Typical tasks |
| :--- | :--- | :--- |
| **Tier 1 (Low)** | Isolated, deterministic changes. | A new colour, a surface's settings, README copy. |
| **Tier 2 (Med)** | Logic touching 2+ functions or files. | Animation timing, keyboard controls, the HUD. |
| **Tier 3 (High)** | Changes to how the program is shaped. | Replacing the renderer, a new projection model, a UI layer. |

A Tier 3 plan is deepened by the Architect before any building starts.

---

## 📝 The workflow cycle

### 0. Triage
1. Chris describes the feature or bug.
2. The Architect checks it against `MANIFESTO.md` and `ROADMAP.md`, assigns a tier, and writes `active/task-XXX-name.md` from the template.
3. The task gets a row in `ROADMAP.md`.

### 1. Plan (Phase 0, Plan-and-Pause)
1. The Builder reads the task file, `MANIFESTO.md` and `MEMORY.md`, then the code.
2. It fills in the task's Implementation Plan: the files it will touch, the tests it will write, and the open questions.
3. **HALT.** The Builder asks: *"Does this implementation plan look correct? Please approve before I begin execution."*

### 2. Build
1. Branch from `master`: `feat/task-XXX-name` or `fix/task-XXX-name`.
2. Write the failing test first, then the code.
3. Run `lein test`, and run `lein run` to check the change on screen.
4. Report what changed, what was verified, and what Chris should look at. Then **HALT.**

### 3. Review and close (Chris)
1. Run the sketch and confirm the change looks right.
2. Merge the branch to `master`.
3. Add anything learned to `MEMORY.md`.
4. Move the task file from `active/` to `archive/`.
5. Mark the task done in `ROADMAP.md` and update `active/README.md`.

---

## 📄 Artifact standards

### The task file (`task-XXX-name.md`)
Every task **must** contain:
1. **Header:** status, tier, branch.
2. **Context:** why we're doing it, with links to the relevant code.
3. **Objectives:** what "done" looks like, in a sentence or a short list.
4. **Implementation Plan:** a checklist the Builder fills in during Phase 0.
5. **Definition of Done:** checkable outcomes, including what Chris verifies on screen.

Start from [`TEMPLATES/task_template.md`](TEMPLATES/task_template.md). Number tasks sequentially. Numbers are IDs, not an order.

### The memory file (`MEMORY.md`)
* **Don't** paste code into it.
* **Do** record decisions and sharp edges, like "loading `sparkl.core` opens a window".

---

## 🚫 Rules of engagement

1. **No blind coding.** Agents always work from a task file in `active/`.
2. **Tests first.** Logic is verified by a `clojure.test` test. Drawing code is checked by eye, and the report says exactly what to look for.
3. **One task at a time.** Only one task file lives in `active/` at any moment, apart from `active/README.md`.
4. **Humans merge.** Agents work on their own branch and propose. Only Chris merges to `master`.
5. **The sandbox rule.** Never modify the host environment, such as the JDK, `~/.lein` or OS settings, to force something to work. If blocked, HALT and report.
6. **Halt for architecture.** For any Tier 3 plan, wait for explicit approval before writing code.
7. **A test must be able to fail.** For every assertion, name the change that would turn it red. If you can't, the assertion is decoration.
8. **Pure math stays pure.** Projection, rotation and surface functions take values and return values. Quil calls and atoms stay at the edges, in `setup`, `draw` and the key handlers, so the math can be tested without opening a window.
