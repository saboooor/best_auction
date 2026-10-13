# Data Model: Simplify Auction Categories

**Date**: 2026-03-24
**Feature**: 001-simplify-auction-categories

## Entity: AuctionCategory (enum)

### New Category Definitions

| Enum Value | Display Name (EN) | Legacy Pack Label (English Translation) | Icon Material | Included Legacy Categories |
|--------|------------|------------|-----------------|-----------------|
| ALL | All | All | CHEST | (no filter) |
| BLOCKS | Blocks | Blocks | STONE | BUILDING_BLOCKS, DECORATIONS |
| FOOD | Food | Food | APPLE | FOOD |
| WEAPONS_EQUIPMENT | Weapons & Equipment | Weapons & Equipment | DIAMOND_SWORD | COMBAT, TOOLS |
| ENCHANTMENTS | Enchantments | Enchantments | ENCHANTED_BOOK | ENCHANTED_BOOKS + all enchanted items |
| OTHERS | Others | Others | LAVA_BUCKET | REDSTONE, TRANSPORTATION, BREWING, MISCELLANEOUS |

### Classification Rules (In Priority Order)

1. **Enchantment check (highest priority)**: `ItemMeta.hasEnchants()` or `Material == ENCHANTED_BOOK` → ENCHANTMENTS
2. **Food check**: `Material.isEdible` → FOOD
3. **Weapons and equipment check**: Swords, bows, armor, tools (pickaxes, axes, shovels, hoes), etc. → WEAPONS_EQUIPMENT
4. **Block check**: `Material.isBlock` or decorative items → BLOCKS
5. **Fallback**: Items that match none of the above → OTHERS

### Legacy Category Mapping (For Database Compatibility)

Conversion map from legacy category names:

```
BUILDING_BLOCKS → BLOCKS
DECORATIONS     → BLOCKS
FOOD            → FOOD
COMBAT          → WEAPONS_EQUIPMENT (enchanted items belong to ENCHANTMENTS)
TOOLS           → WEAPONS_EQUIPMENT (enchanted items belong to ENCHANTMENTS)
ENCHANTED_BOOKS → ENCHANTMENTS
REDSTONE        → OTHERS
TRANSPORTATION  → OTHERS
BREWING         → OTHERS
MISCELLANEOUS   → OTHERS
```

**Note**: Legacy categories did not check enchantments, so the database category name alone cannot determine whether old COMBAT/TOOLS items are enchanted. Deserialize item data when displaying it to check enchantments again.

## Entity: AuctionItems (Table - Unchanged)

No schema change is required. Continue using the `category` column (varchar 50).

- **New listings**: Save new category names (BLOCKS, FOOD, etc.)
- **Existing data**: Keep legacy category names (BUILDING_BLOCKS, etc.)
- **Reads**: Apply legacy-to-new mapping during queries

### Query Patterns

SQL conditions for category filtering:

| New Category | WHERE Condition |
|-----------|----------|
| ALL | (no filter) |
| BLOCKS | category IN ('BLOCKS', 'BUILDING_BLOCKS', 'DECORATIONS') |
| FOOD | category IN ('FOOD') |
| WEAPONS_EQUIPMENT | category IN ('WEAPONS_EQUIPMENT', 'COMBAT', 'TOOLS') |
| ENCHANTMENTS | category IN ('ENCHANTMENTS', 'ENCHANTED_BOOKS') + recheck enchantments at runtime |
| OTHERS | category IN ('OTHERS', 'REDSTONE', 'TRANSPORTATION', 'BREWING', 'MISCELLANEOUS') |

**Special handling for ENCHANTMENTS**: Enchanted legacy COMBAT/TOOLS items retain their old database category names. Complete classification requires enchantment checks after deserialization, but performance involves a tradeoff between trusting stored categories and migrating on first startup. Recommendation: Trust database categories for legacy data and apply accurate enchantment detection only to new listings.
