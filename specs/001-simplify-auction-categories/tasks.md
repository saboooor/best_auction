# Tasks: Simplify Auction Categories

**Input**: Design documents from `/specs/001-simplify-auction-categories/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, quickstart.md

**Tests**: Not requested - test tasks are not included.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: No project structure changes needed. Existing codebase is the starting point.

(No setup tasks - existing project structure is maintained as-is)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Redefine the AuctionCategory enum and update related infrastructure. Prerequisite for all user stories.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T001 Redefine AuctionCategory with six values (ALL, BLOCKS, FOOD, WEAPONS_EQUIPMENT, ENCHANTMENTS, OTHERS) and update displayName and material properties to match in `src/main/kotlin/com/hacklab/best_auction/data/AuctionData.kt`
- [x] T002 Rewrite fromItemStack() for new categories. Priority: (1) enchantments (hasEnchants() or ENCHANTED_BOOK → ENCHANTMENTS), (2) food (isEdible → FOOD), (3) weapons and equipment (swords, bows, armor, tools → WEAPONS_EQUIPMENT), (4) blocks (isBlock or decorative items → BLOCKS), (5) fallback → OTHERS in `src/main/kotlin/com/hacklab/best_auction/data/AuctionData.kt`
- [x] T003 Add a legacy-to-new category map to AuctionCategory's companion object. Implement fromLegacyName(name: String): AuctionCategory in `src/main/kotlin/com/hacklab/best_auction/data/AuctionData.kt`
- [x] T004 [P] Update the category section of en.yml (all: "All", blocks: "Blocks", food: "Food", weapons_equipment: "Weapons & Equipment", enchantments: "Enchantments", others: "Others"). Remove obsolete category keys in `src/main/resources/lang/en.yml`
- [x] T005 [P] Update the category section of ja.yml with the corresponding localized labels (English translations: All, Blocks, Food, Weapons & Equipment, Enchantments, Others). Remove obsolete category keys in `src/main/resources/lang/ja.yml`

**Checkpoint**: AuctionCategory defines six new categories, fromItemStack() uses the new logic, and language files are updated

---

## Phase 3: User Story 1 - Browse All Items Newest First (Priority: P1) 🎯 MVP

**Goal**: Opening the auction house displays all items newest first

**Independent Test**: Open the auction house and verify that all items appear in descending listing time. An empty message appears when there are no items.

### Implementation for User Story 1

- [x] T006 [US1] Change getActiveListings() sort order to createdAt DESC (newest first). Preserve the existing behavior of returning all items when category=null in `src/main/kotlin/com/hacklab/best_auction/managers/AuctionManager.kt`
- [x] T007 [US1] Rewrite openMainUI() from category selection to an all-items list. Paginate getActiveListings(category=null) results in slots 0-35 and place navigation buttons in slots 45-53 in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T008 [US1] Update getCategoryDisplayName() for all six categories. Add the LangManager key for ALL in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T009 [US1] Update handleMainMenuClick() to handle item clicks (bid/buy) and navigation button clicks in the item list in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`

**Checkpoint**: Opening the auction house displays all items newest first. Pagination and item clicks (bid/buy) work.

---

## Phase 4: User Story 2 - Filter with Simplified Categories (Priority: P1)

**Goal**: Six category filter buttons narrow the item list by category

**Independent Test**: Click each category button and verify that only matching items appear. The “All” button clears the filter.

### Implementation for User Story 2

- [x] T010 [US2] Add category filtering to getActiveListings(). Accept new category names and filter using IN clauses that include corresponding legacy names (for example, BLOCKS → category IN ('BLOCKS', 'BUILDING_BLOCKS', 'DECORATIONS')). Apply no filter for ALL in `src/main/kotlin/com/hacklab/best_auction/managers/AuctionManager.kt`
- [x] T011 [US2] Add six category filter buttons to the bottom row of openMainUI(). Slots: 45 All (CHEST), 46 Blocks (STONE), 47 Food (APPLE), 48 Weapons & Equipment (DIAMOND_SWORD), 49 Enchantments (ENCHANTED_BOOK), 50 Others (LAVA_BUCKET). Highlight the selected category with enchantment glow in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T012 [US2] Update createCategoryItem() and icon generation for the new categories in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T013 [US2] Add category filter button handling to handleMainMenuClick(). Redraw the item list for the selected category when clicked in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T014 [US2] Add a category parameter to openMainUI() and retain the selected category during pagination in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`
- [x] T015 [US2] Adjust mailbox, listings, bids, settings, and search button positions for the new UI layout (slots 51-53, etc.) in `src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`

**Checkpoint**: Category clicks show only matching items. “All” clears the filter. The category persists across pages.

---

## Phase 5: User Story 3 - Preserve Compatibility with Existing Data (Priority: P2)

**Goal**: Items saved with legacy database category names display correctly in the new system

**Independent Test**: With legacy category data (BUILDING_BLOCKS, REDSTONE, etc.), verify correct filtering in the new UI. All items appear without omissions.

### Implementation for User Story 3

- [x] T016 [US3] Verify and fix category filter IN clauses in getActiveListings() so every legacy category name is included. Specifically verify ENCHANTMENTS → ('ENCHANTMENTS', 'ENCHANTED_BOOKS') in `src/main/kotlin/com/hacklab/best_auction/managers/AuctionManager.kt`
- [x] T017 [US3] Verify listItem saves the new category returned by AuctionCategory.fromItemStack() to the database. Confirm category.name uses the new enum value in `src/main/kotlin/com/hacklab/best_auction/managers/AuctionManager.kt`

**Checkpoint**: Legacy items appear correctly in new categories. New listings use new names. Zero data loss.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Build verification and final adjustments

- [x] T018 Build the project and verify there are no compilation errors (`./gradlew build`)
- [x] T019 Search all AuctionCategory references and verify no obsolete category values remain. Check for effects on related files such as SearchHandler

---

## Dependencies & Execution Order

### Phase Dependencies

- **Foundational (Phase 2)**: Ready to start - update AuctionCategory enum and language files
- **US1 (Phase 3)**: After Phase 2 - depends on the new enum
- **US2 (Phase 4)**: After Phase 3 - depends on the main UI structure
- **US3 (Phase 5)**: After Phase 4 - depends on filtering logic
- **Polish (Phase 6)**: After Phase 5 - verify the build after all changes

### User Story Dependencies

- **US1 (P1)**: Can start after Phase 2. Independent of other user stories.
- **US2 (P1)**: Depends on US1's UI changes (extensive changes to the same file)
- **US3 (P2)**: Depends on US2's completed filtering logic (verifies IN clause accuracy)

### Within Each User Story

- Update AuctionManager before AuctionUI (data layer → UI layer)
- Execute tasks in the same file sequentially

### Parallel Opportunities

- T004 and T005 can run in parallel (en.yml and ja.yml are separate files)
- In Phase 2, T001-T003 (AuctionData.kt) can run in parallel with T004-T005 (language files)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2: Foundational（enum + lang files）
2. Complete Phase 3: User Story 1 (all items newest first)
3. **STOP and VALIDATE**: Open the auction house and verify all items appear newest first
4. A minimal working plugin is available at this point

### Incremental Delivery

1. Phase 2 → Complete enum foundation
2. Phase 3 (US1) → Display all items → Verify behavior (MVP!)
3. Phase 4 (US2) → Add category filters → Verify behavior
4. Phase 5 (US3) → Verify legacy data compatibility → Verify behavior
5. Phase 6 → Build verification and final validation

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- US1 and US2 extensively modify the same file (AuctionUI.kt), so parallel execution is not recommended
- Trust stored database category names for legacy enchanted items (saved under COMBAT/TOOLS). Apply accurate enchantment detection only to new listings
- The user starts the Minecraft server. Do not use `./gradlew runServer`
