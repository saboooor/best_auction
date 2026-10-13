# Best Auction Plugin Documentation

## 📋 Overview

Best Auction is an auction house plugin for Minecraft Paper/Spigot servers. It provides a complete marketplace where players can list, bid on, and purchase items.

### Key Features
- 📦 Item listing and bidding system
- 💰 Economy system with Vault integration
- 📬 Mailbox system
- 🌐 English interface and language resources
- ⚙️ Advanced configuration
- 🔍 Simple categories (All, Blocks, Food, Weapons & Equipment, Enchantments, and Others)
- 🔍 Keyword search

---

## 🛠️ Installation and Setup

### Required Plugins
- **Paper/Spigot** 1.20.6 or later
- **Vault** (for economy plugin integration)
- **Economy plugin** (such as EssentialsX)

### Installation Steps
1. Place `best_auction-1.0-SNAPSHOT-all.jar` in the `plugins/` folder
2. Restart the server
3. Adjust the settings in `plugins/best_auction/config.yml`

---

## ⚙️ Configuration File

### Main config.yml Settings

```yaml
# Language settings
language: "en"  # English

# Auction settings
auction:
  default_duration: 228  # Auction duration (hours)
  listing_fee_rate: 0.05 # Listing fee rate (5%)
  min_listing_fee: 10    # Minimum fee
  max_listing_fee: 1000  # Maximum fee

# Currency settings
currency:
  use_vault_format: true      # Use Vault formatting
  fallback_currency: "gil"    # Fallback currency name
  currency_symbol: ""         # Currency symbol

# UI settings
ui:
  items_per_page: 45          # Items per page
  date_format: "yyyy-MM-dd"   # Date display format

# Debug settings
debug:
  enable_debug_commands: false # Enable debug commands
```

### Date Format Examples
- `"yyyy-MM-dd"` → 2024-12-18
- `"MMMM dd, yyyy"` → December 18, 2024
- `"MM/dd/yyyy"` → 12/18/2024
- `"dd/MM/yyyy"` → 18/12/2024

---

## 💬 Commands

### Basic Commands

| Command | Description | Example |
|---------|------|-----|
| `/ah` | Open the auction house GUI | `/ah` |
| `/ah sell <price> [buyout]` | List the item in your hand | `/ah sell 1000 2000` |
| `/ah bid <ID> <amount>` | Bid on an item | `/ah bid 23 1500` |
| `/ah cancel <ID>` | Cancel a listing | `/ah cancel 23` |
| `/ah search <keyword>` | Search for items | `/ah search diamond` |
| `/ah mail` | Open your mailbox | `/ah mail` |
| `/ah confirm` | Confirm and execute an action | `/ah confirm` |
| `/ah help` | Show help | `/ah help` |

### Administrator Commands

| Command | Permission | Description |
|---------|------|-----|
| `/ah testdata [count]` | `auction.admin` | Generate test data (debug mode only) |

### Amount Notation
- **K/M/B notation**: `7.2K` (7,200), `1.5M` (1,500,000)
- **Automatic abbreviation**: Amounts of 1,000 or more are automatically converted to K/M notation

---

## 🎮 Player Guide

### 1. Listing Items

1. Hold the item you want to list
2. Run `/ah sell <starting_price> [buyout_price]`
3. The listing fee is collected automatically
4. Auction duration is set globally in the configuration (228 hours by default)

**Examples:**
```
/ah sell 1000      # Start at 1,000 gil with no buyout
/ah sell 1K 2.5K   # Start at 1,000 gil with a 2,500 gil buyout
```

### 2. Bidding and Buying

1. Open the auction house with `/ah`
2. Left-click an item that interests you
3. Review the bid information and enter an amount
4. Right-click to buy immediately (if a buyout price is set)

**Information on the bidding screen:**
- Current highest bid
- Minimum bid
- Buyout price (if set)
- Time remaining

### 3. Managing Bids

1. Run `/ah` → click “Your Bids”
2. **Left-click**: Change your bid amount
3. **Right-click**: Cancel your bid

**Bid status display:**
- ✓ Highest bidder (green)
- ✗ You have been outbid (red)

### 4. Search

```
/ah search diamond   # Keyword search
/ah search enchant   # Partial-match search
```

### 5. Mailbox

- Items you won
- Refunded money
- Cancelled items

All of these are sent to your mailbox.

---

## 🔧 Administrator Settings

### 1. Adjusting Economy Settings

```yaml
auction:
  listing_fee_rate: 0.05  # 5% fee
  min_listing_fee: 10     # Minimum of 10 gil
  max_listing_fee: 1000   # Maximum of 1,000 gil
```

### 2. Changing Auction Duration

```yaml
auction:
  default_duration: 168   # 7 days (168 hours)
  # default_duration: 72  # 3 days
  # default_duration: 24  # 1 day
```

### 3. Language Setting

```yaml
language: "en"  # Select English
```

### 4. Enabling Debug Mode

```yaml
debug:
  enable_debug_commands: true  # Set to true only in a development environment
```

---

## 🛡️ Permissions

### Basic Permissions
- `bestauction.use` - Basic usage permission
- `auction.admin` - Administrator permission (such as the testdata command)

### Permission Configuration Example (LuckPerms)
```
/lp group default permission set bestauction.use true
/lp group admin permission set auction.admin true
```

---

## 📊 Database

### SQLite Table Structure

**AuctionItems (auction items)**
- ID, seller, item data, price, expiration, etc.

**Bids (bids)**
- Bidder, bid amount, creation time, etc.

**MailBox (mail)**
- Recipient, item, reason, timestamp, etc.

**Settings**
- Player language settings
- Other configuration data

---

## 🔧 Troubleshooting

### Common Problems

**Q: The auction house does not open**
A: Check that Vault and an economy plugin are installed

**Q: The currency display looks wrong**
A: Check the `currency` settings in `config.yml`

**Q: I want to change the date display**
A: Change `ui.date_format` and restart the server

**Q: I cannot use the testdata command**
A: Set `debug.enable_debug_commands: true`

### Checking Logs
```
tail -f logs/latest.log | grep "best_auction"
```

---

## 🚀 Developer Information

### Building
```bash
./gradlew build           # Build the project
./gradlew shadowJar       # Generate a fat JAR
./gradlew runServer       # Start the development server
```

### Project Structure
```
src/main/kotlin/com/hacklab/best_auction/
├── Main.kt                 # Main class
├── commands/              # Command handling
├── managers/              # Business logic
├── ui/                    # GUI components
├── utils/                 # Utilities
├── data/                  # Data classes
└── database/              # Database definitions
```

### Technology Stack
- **Kotlin** 1.9.24
- **Gradle** with Kotlin DSL
- **PaperMC API** 1.20.6
- **Jetbrains Exposed** ORM
- **Vault API** economy integration

---

## 📝 Changelog

### v1.0.0
- Basic auction functionality
- Bidding system
- Mailbox
- Multi-language support

### v1.1.0 (Latest)
- Configurable date format
- Configurable auction duration
- Time remaining display
- Debug mode controls
- K/M/B notation support
- Improved Vault currency integration

---

## 🆘 Support

### Reporting Problems
- [GitHub Issues](https://github.com/your-repo/best_auction/issues)
- Contact the plugin author

### Configuration File Locations
- `plugins/best_auction/config.yml`
- `plugins/best_auction/lang/`

### Recommended Backups
- Database file: `plugins/best_auction/database.db`
- Configuration file: `plugins/best_auction/config.yml`

---

## 📄 License

MIT License - See the LICENSE file for details

---

*This documentation is based on Best Auction Plugin v1.1.0. Check the official repository for the latest information.*