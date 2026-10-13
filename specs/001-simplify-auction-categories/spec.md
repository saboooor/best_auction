# Feature Specification: Simplify Auction Categories

**Feature Branch**: `001-simplify-auction-categories`
**Created**: 2026-03-24
**Status**: Draft
**Input**: User description: "The auction categories are confusing. I think an all-items list ordered newest first, Blocks, Food, Weapons & Equipment, Enchantments, and Others would be enough."

## Clarifications

### Session 2026-03-24

- Q: What should the “Enchantments” category include: only enchanted books, or all enchanted items? → A: All enchanted items (including books, weapons, armor, and tools)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse All Items Newest First (Priority: P1)

When a player opens the auction house, the “All” category appears first, displaying all listed items in newest-first order (most recent listing time first). This lets players quickly check the latest listings.

**Why this priority**: Getting an overview immediately after opening the auction house is the most important experience. Players want to see what is listed at a glance before choosing a category.

**Independent Test**: Open the auction house and verify that all items appear newest first. List several items at different times and verify their order.

**Acceptance Scenarios**:

1. **Given** multiple items are listed in the auction house, **When** a player opens the auction house, **Then** the “All” category appears initially and all items are displayed newest first
2. **Given** the “All” category is displayed, **When** a new item is listed, **Then** it appears at the top of the list the next time the view is displayed
3. **Given** the auction house has no items, **When** a player opens it, **Then** the “All” category appears with a message indicating there are no listings

---

### User Story 2 - Filter with Simplified Categories (Priority: P1)

Players searching for a particular type of item can narrow the list using five simple categories (Blocks, Food, Weapons & Equipment, Enchantments, and Others). Reducing the current ten categories to five plus “All” makes it intuitive even for children to find the category they need.

**Why this priority**: Simplifying categories is the core of this feature and directly addresses the user's concern that they are confusing.

**Independent Test**: Select each category and verify that only its items appear. Verify that items from legacy categories map correctly to the new categories.

**Acceptance Scenarios**:

1. **Given** the auction house is displayed, **When** a player selects “Blocks”, **Then** only items classified as building or decorative blocks appear
2. **Given** the auction house is displayed, **When** a player selects “Food”, **Then** only items classified as food or drinks appear
3. **Given** the auction house is displayed, **When** a player selects “Weapons & Equipment”, **Then** only non-enchanted weapons, armor, and tools appear
4. **Given** the auction house is displayed, **When** a player selects “Enchantments”, **Then** all enchanted items (enchanted books, weapons, armor, and tools) appear
5. **Given** the auction house is displayed, **When** a player selects “Others”, **Then** items outside the above categories (redstone, transportation, brewing, and miscellaneous items) appear

---

### User Story 3 - Preserve Compatibility with Existing Data (Priority: P2)

Items listed before the category change still display correctly under the new category system. Legacy category names stored in the database map correctly to the new categories, with no items lost.

**Why this priority**: Existing data integrity directly affects system reliability, but new installations may have no existing data, so this is P2.

**Independent Test**: Prepare items saved in the database with legacy category names and verify that they display correctly in the new categories.

**Acceptance Scenarios**:

1. **Given** an item is saved under the legacy “BUILDING_BLOCKS” category, **When** the auction house is displayed using the new category system, **Then** the item appears under “Blocks”
2. **Given** an item is saved under the legacy “REDSTONE” category, **When** the auction house is displayed using the new category system, **Then** the item appears under “Others”
3. **Given** multiple items use legacy categories, **When** the “All” category is displayed, **Then** every item appears without omissions

---

### Edge Cases

- Items added in new Minecraft versions that cannot be classified into an existing category fall back to “Others”
- Enchanted items (including weapons, armor, and tools) belong to “Enchantments”. The same item type may have different categories depending on whether it is enchanted
- Pagination allows normal browsing even when many items belong to a single category

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system must provide six categories: “All”, “Blocks”, “Food”, “Weapons & Equipment”, “Enchantments”, and “Others”
- **FR-002**: The “All” category must display every listed item newest first (descending listing time)
- **FR-003**: The initial view when opening the auction house must be the “All” category
- **FR-004**: “Blocks” must include items from the legacy BUILDING_BLOCKS and DECORATIONS categories
- **FR-005**: “Food” must include items from the legacy FOOD category
- **FR-006**: “Weapons & Equipment” must include non-enchanted items from the legacy COMBAT and TOOLS categories
- **FR-007**: “Enchantments” must include all enchanted items (enchanted books, weapons, armor, tools, etc.)
- **FR-008**: “Others” must include items from the legacy REDSTONE, TRANSPORTATION, BREWING, and MISCELLANEOUS categories
- **FR-009**: Items saved in the database with legacy category names must display correctly in the new category system
- **FR-010**: All category names and UI displays must support localization (English and Japanese in the original specification)
- **FR-011**: Each category must have an icon that visually represents it

### Category Mapping

| New Category           | Legacy Categories                                    | Suggested Icon |
|------------------------|------------------------------------------------------|----------------|
| All                    | All categories                                       | CHEST          |
| Blocks                 | BUILDING_BLOCKS, DECORATIONS                          | STONE          |
| Food                   | FOOD                                                 | APPLE          |
| Weapons & Equipment    | COMBAT, TOOLS                                        | DIAMOND_SWORD  |
| Enchantments           | ENCHANTED_BOOKS + all enchanted items                 | ENCHANTED_BOOK |
| Others                 | REDSTONE, TRANSPORTATION, BREWING, MISCELLANEOUS      | LAVA_BUCKET    |

### Key Entities

- **AuctionCategory**: Classification of auction items, reorganized from ten legacy categories into six new categories. Items are classified automatically by type.
- **Category mapping**: Relationship between legacy and new category names, required for backward compatibility with existing data.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Players can find their target category with at most one click (category selection only) after opening the auction house
- **SC-002**: The category count is reduced from ten to six, with meanings intuitive enough for children to understand
- **SC-003**: Every item listed under a legacy category appears under the new category system without omissions (zero data loss)
- **SC-004**: All items in “All” display correctly sorted newest first
- **SC-005**: All category names display correctly in both language packs (English and Japanese in the original specification)

## Assumptions

- “Enchantments” includes all enchanted items, including enchanted weapons, armor, and tools as well as enchanted books
- “Blocks” includes decorative blocks (DECORATIONS). Grouping building-related items together makes the category intuitive
- “Weapons & Equipment” includes tools (TOOLS). Pickaxes, axes, shovels, and similar equipment are grouped with weapons. Enchanted items take priority in the “Enchantments” category
- “Others” includes redstone, transportation, brewing, and unclassifiable items
- The database category column stores new category names while retaining support for reading legacy names
- Changing the initial main UI to “All” (newest first) means players see the item list before a category selection screen
