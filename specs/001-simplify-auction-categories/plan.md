# Implementation Plan: Simplify Auction Categories

**Branch**: `001-simplify-auction-categories` | **Date**: 2026-03-24 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-simplify-auction-categories/spec.md`

## Summary

Simplify the current ten categories (BUILDING_BLOCKS, DECORATIONS, REDSTONE, TRANSPORTATION, MISCELLANEOUS, FOOD, TOOLS, COMBAT, BREWING, ENCHANTED_BOOKS) to six (ALL, BLOCKS, FOOD, WEAPONS_EQUIPMENT, ENCHANTMENTS, OTHERS). Replace the category selection screen with an all-items list ordered newest first and category filters. Classify all enchanted items under ENCHANTMENTS regardless of type. No database schema changes are required; runtime mapping preserves backward compatibility with existing data.

## Technical Context

**Language/Version**: Kotlin 1.9.24
**Primary Dependencies**: PaperMC API 1.21.4, Jetbrains Exposed ORM 0.44.1, Vault API
**Storage**: SQLite (via Exposed ORM)
**Testing**: Manual testing (verify behavior on a Minecraft server)
**Target Platform**: PaperMC / Spigot 1.21.x servers
**Project Type**: Minecraft server plugin
**Performance Goals**: Immediate UI responses (no perceptible delay)
**Constraints**: 54-slot inventory UI, consideration for children, localization (EN/JA)
**Scale/Scope**: Small to medium servers (tens to hundreds of players)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution is not configured (still the template), so the gate check is skipped.

## Project Structure

### Documentation (this feature)

```text
specs/001-simplify-auction-categories/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0: Technical decisions
├── data-model.md        # Phase 1: Entity model & mapping
├── quickstart.md        # Phase 1: Implementation guide
└── checklists/
    └── requirements.md  # Specification quality checklist
```

### Source Code (repository root)

```text
src/main/kotlin/com/hacklab/best_auction/
├── data/
│   └── AuctionData.kt          # ★ Change AuctionCategory enum
├── managers/
│   └── AuctionManager.kt       # ★ Update getActiveListings() filters
├── ui/
│   └── AuctionUI.kt            # ★ Change main UI structure and category filters
├── handlers/
│   ├── BidHandler.kt           # No changes
│   └── SearchHandler.kt        # No changes
├── database/
│   ├── DatabaseManager.kt      # No changes
│   └── Tables.kt               # No changes (schema unchanged)
└── Main.kt                     # No changes

src/main/resources/
├── lang/
│   ├── en.yml                   # ★ Update category names
│   └── ja.yml                   # ★ Update category names
├── config.yml                   # No changes
└── plugin.yml                   # No changes
```

**Structure Decision**: Keep the existing project structure. No new files are required; all work involves updating existing files.

## Complexity Tracking

Not applicable. No constitution violations because the constitution is not configured.
