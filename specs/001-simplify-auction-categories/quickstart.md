# Quickstart: Simplify Auction Categories

**Date**: 2026-03-24
**Feature**: 001-simplify-auction-categories

## Files to Update

### Required Changes

1. **`src/main/kotlin/com/hacklab/best_auction/data/AuctionData.kt`**
   - Change AuctionCategory enum from ten values to six
   - Rewrite fromItemStack() logic (give enchantment detection highest priority)
   - Add a companion object method to map legacy category names

2. **`src/main/kotlin/com/hacklab/best_auction/ui/AuctionUI.kt`**
   - openMainUI(): Category list → item list with category filter bar
   - openCategoryUI(): Support new categories
   - getCategoryDisplayName(): Map display names for new categories
   - createCategoryItem(): Generate icons for new categories
   - handleMainMenuClick(): Update click handling for category filters
   - handleCategoryClick(): Adjust as needed

3. **`src/main/kotlin/com/hacklab/best_auction/managers/AuctionManager.kt`**
   - getActiveListings(): Support multiple legacy category names in category filters
   - Change sort order to createdAt DESC (newest first)

4. **`src/main/resources/lang/en.yml`**
   - Update the category section with new category values

5. **`src/main/resources/lang/ja.yml`**
   - Update the category section with new category values

### No Changes Required

- `Tables.kt` - No schema changes
- `DatabaseManager.kt` - No database initialization changes
- `BidHandler.kt` - Bidding does not depend on categories
- `SearchHandler.kt` - Search is based on item names and does not depend on categories

## Implementation Order

1. Update AuctionCategory enum (data layer)
2. Update AuctionManager.getActiveListings() (business logic layer)
3. Update language files (resource layer)
4. Update AuctionUI (UI layer)

## Build and Test

```bash
./gradlew build
# Output: build/libs/best_auction-*-all.jar
# Place in the server's plugins/ directory and test
```
