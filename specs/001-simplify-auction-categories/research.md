# Research: Simplify Auction Categories

**Date**: 2026-03-24
**Feature**: 001-simplify-auction-categories

## Decision 1: Category Classification Architecture

**Decision**: Redefine `AuctionCategory` with six values (ALL, BLOCKS, FOOD, WEAPONS_EQUIPMENT, ENCHANTMENTS, OTHERS), replacing the legacy enum.

**Rationale**: Keeping the enum structure while changing its values minimizes impact across the codebase. Giving enchantment detection the highest priority in `fromItemStack()` satisfies the new classification requirements.

**Alternatives considered**:
- Keep the legacy enum and add a mapping layer to a new enum → Rejected because it duplicates management
- Make categories configurable in config.yml → Excessive generalization; violates YAGNI

## Decision 2: Enchantment Detection Priority

**Decision**: Check for enchantments **first** in `fromItemStack()` and classify all enchanted items under ENCHANTMENTS.

**Rationale**: The specification covers all enchanted items. Detection is possible through ItemStack's `enchantments` property and ItemMeta's `hasEnchants()`. Identify enchanted books by Material.ENCHANTED_BOOK and other enchanted items by ItemMeta.hasEnchants().

**Alternatives considered**:
- Check each item type separately → Complex and prone to omissions
- Check only StoredEnchantMeta → Misses ordinary enchanted items

## Decision 3: Database Compatibility Strategy

**Decision**: Use runtime mapping. Keep legacy category names in the database and convert them when reading. Save new listings with new category names.

**Rationale**: Avoiding database migration minimizes risk during plugin updates. Filtering multiple legacy category names with OR conditions in `getActiveListings()` achieves this.

**Alternatives considered**:
- Bulk database UPDATE → Requires migration at plugin startup and carries significant risk if it fails
- Create a new table → Excessive change; the existing schema is sufficient

## Decision 4: UI Structure Changes

**Decision**: Change the main UI (openMainUI) to an all-items list with category filter buttons.

**Rationale**: Satisfies FR-003, which requires the initial view to be “All”. In a 54-slot inventory, place the item list in the top 36 slots and six category filter buttons plus navigation buttons in the bottom row.

**Layout**:
- Slot 0-35: Item list (up to 36 items per page)
- Slot 45: All (CHEST)
- Slot 46: Blocks (STONE)
- Slot 47: Food (APPLE)
- Slot 48: Weapons & Equipment (DIAMOND_SWORD)
- Slot 49: Enchantments (ENCHANTED_BOOK)
- Slot 50: Others (LAVA_BUCKET)
- Slot 51: Search (SPYGLASS)
- Slot 52: Mailbox (change CHEST → ENDER_CHEST to distinguish it from the “All” CHEST)
- Slot 53: Settings (WRITABLE_BOOK)

**Alternatives considered**:
- Keep the two-step UI (category selection → item list) → Conflicts with the specification's initial all-items view
- Categories at the top and items below → Conflicts with Minecraft conventions (items above, navigation below)

## Decision 5: Sort Order

**Decision**: Default to newest-first order (descending createdAt) in every category view.

**Rationale**: FR-002 explicitly requires newest-first order for “All”. Use the same ordering for other categories to keep the experience consistent. Replace the current `currentPrice ASC, createdAt ASC` ordering with newest first.
