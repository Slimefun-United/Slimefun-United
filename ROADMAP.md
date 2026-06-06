# 🧭 Slimefun United – Project Roadmap

This document outlines the planned development direction for **Slimefun United**.  
The goal is to provide a **stable**, **localized**, **modular**, and **future‑proof** continuation of Slimefun while avoiding breaking changes until Pylon becomes available.

This roadmap focuses on **stability**, **compatibility**, and **maintainability**.

---

# 1. Storage Migration & Stability (High Priority)

## Goals

- Ensure safe, reliable storage for all servers.
- Support multiple storage backends.
- Provide optional migration paths for legacy servers.

## Tasks

- Add YAML‑based storage support (matching original Slimefun behavior).
- Implement optional migration for old servers (manual trigger required).
- Guarantee data integrity during migration (no item or machine loss).
- Add cross‑storage migration (YAML → SQLite → MySQL, etc.) if missing.
- Improve error handling and logging for storage operations.
- Add automated consistency checks for BlockStorage and PlayerStorage.

## Outcome

A stable, predictable storage layer that server owners can trust.

---

# 2. Localization System (High Priority)

## Goals

- Provide full multilingual support for items, messages, and GUIs.
- Make the plugin accessible to global communities.

## Tasks

- Implement item localization using the same system as messages/GUI.
- Add missing translations for commands and messages.
- Introduce a unified translation key structure (`item.*`, `messages.*`).
- Add fallback language support (e.g., fallback to English).
- Prepare the system for future packet‑based translation.

## Future Work

- Packet‑based translation (similar to [SlimefunTranslation add-on](https://github.com/GuizhanCraft/SlimefunTranslation)):
  - Dynamic item names/lore per player.
  - Language‑agnostic item trading.
  - No language stored in NBT.

## Outcome

A clean, modern localization system that supports English, Simplified Chinese, Russian, and more.

---

# 3. Module System (Medium Priority)

## Goals

- Reduce dependency on external addons.
- Allow servers to enable/disable features cleanly.
- Improve maintainability and performance.

## Tasks

- Integrate essential addons into core or a unified module package.
- Introduce `modules.yml` for toggling modules:
  ```yaml
  dynatech:
    enabled: true
  coloredbackpacks:
    enabled: false
  ```
- Allow enabling/disabling:
  - modules
  - items
  - researches
  - categories
- Avoid config bloat by generating only necessary examples.
- Add module dependency checks (e.g., “Module A requires Module B”).

## Outcome

A flexible, modular Slimefun experience tailored to each server.

---

# 4. Folia Compatibility Layer (Medium Priority)

## Goals

- Ensure safe operation on Folia without breaking Paper.
- Avoid blocking region threads or unsafe async access.

## Tasks

- Introduce a scheduler abstraction, akin to:
  ```java
  Scheduler.runAt(location, task);
  ```
- Folia → region scheduler
- Paper → Bukkit main thread
- Remove blocking `CompletableFuture.get()` patterns.
- Audit event listeners for unsafe world access.
- Ensure storage and tickers behave correctly on Folia.

## Outcome

A stable, non‑breaking compatibility layer that supports both Paper and Folia.

---

# 5. API Stabilization (Low Priority, Non‑Breaking)

## Goals

- Provide clarity for addon developers.
- Avoid accidental reliance on internal classes.

## Tasks

- Mark stable API classes with `@SlimefunAPI`.
- Mark internal classes with `@SlimefunInternal`.
- Deprecate dangerous or legacy classes without removing them.
- Document safe extension points for addons.

## Outcome

A clearer, safer API surface without breaking existing addons.

---

# 6. Technical Debt Cleanup (Ongoing)

## Goals

- Improve maintainability without breaking compatibility.

## Tasks

- Remove unused or duplicate classes.
- Clean up inconsistent naming and formatting.
- Remove leftover debug code.
- Consolidate utility classes.
- Improve logging and error messages.

## Outcome

A cleaner, more maintainable codebase.

---

# 7. Documentation & ADRs (Ongoing)

## Goals

- Provide transparency and clarity for contributors and server owners.

## Tasks

- Add ADRs for major decisions:
  - Storage migration
  - Localization system
  - Module system
  - Folia compatibility layer
  - Addon integration strategy
- Improve README with:
  - roadmap link
  - installation instructions
  - module documentation
  - translation guidelines
- Create a documentation website with [Docusaurus](https://docusaurus.io/)
  - merge the original docs with Gugu Project's
  - provide support for community (CrownIn) translations

## Outcome

A well‑documented project that encourages contributions and reduces confusion.

---

# 8. Future Considerations (Post‑Pylon)

These changes are intentionally postponed until Pylon releases:

- Architectural refactoring
- API redesign
- Package reorganization
- Deep concurrency model changes
- Machine system rewrite

These will be revisited once the ecosystem stabilizes around Pylon.

---

# Conclusion

This roadmap focuses on **stability**, **localization**, **modularity**, and **compatibility** — the areas where Slimefun United needs the most care right now.  
Breaking changes are intentionally avoided until the release of Pylon, which will define the future architecture of the ecosystem.
