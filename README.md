# 🏪 Best Auction Plugin

[![Release](https://img.shields.io/github/v/release/0x48lab/best_auction)](https://github.com/0x48lab/best_auction/releases)
[![Modrinth](https://img.shields.io/modrinth/dt/best-auction?logo=modrinth&label=downloads)](https://modrinth.com/plugin/best-auction)
[![License](https://img.shields.io/github/license/0x48lab/best_auction)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://adoptium.net/)

A fully featured auction house plugin for Minecraft Paper/Spigot servers. It provides a comprehensive marketplace where players can list, bid on, and purchase items.

## ✨ Key Features

- 🏪 **Complete Auction System** - Listing, bidding, and buy-it-now purchases
- 💰 **Vault Economy Integration** - Seamless integration with existing economy plugins
- 📬 **Mail System** - Automatic delivery of items and refunds
- 🌐 **English Interface** - English messages and language resources
- 🔍 **Simple Categories** - Six intuitive categories (All, Blocks, Food, Weapons & Equipment, Enchantments, and Others)
- 🔍 **Advanced Search** - Keyword search
- ⏰ **Time Remaining** - Real-time display of auction end times
- ⚙️ **Advanced Configuration** - Fine control over fees, duration, currency display, and more
- 📊 **K/M/B Notation** - Readable display of large amounts
- ☁️ **Cloud Integration** - External synchronization of auction data and event reporting

## 🛠️ Requirements

- **Minecraft**: 1.21.10+
- **Server**: Paper
- **Java**: 21+
- **Dependencies**:
  - Vault (recommended)
  - An economy plugin (such as EssentialsX)

The main `/ah` menu uses a native dialog for Java players and a native form for
Bedrock players connected through Geyser and Floodgate. Floodgate is optional;
install it on the server running this plugin to enable Bedrock player detection
and forms. Category listings, bids, and mailbox open the existing inventory screens.
Language follows the client locale, falling back to the server default when unavailable. Search uses the text field in the Java dialog or a native input form on Bedrock.

## 📥 Installation

### From Modrinth (Recommended)
1. Download from the [Modrinth page](https://modrinth.com/plugin/best-auction)
2. Place the file in the `plugins/` folder
3. Restart the server

### From GitHub
1. Download the latest version from [Releases](https://github.com/0x48lab/best_auction/releases)
2. Place the file in the `plugins/` folder
3. Restart the server

## 🎮 Usage

### Basic Commands
```
/ah                          # Open the auction house
/ah sell <price> [buyout]     # List an item
/ah bid <ID> <amount>        # Bid on an item
/ah search <keyword>         # Search for items
/ah mail                     # Open your mailbox
```

### Configuration Example
```yaml
# config.yml
auction:
  default_duration: 168      # 7 days (in hours)
  listing_fee_rate: 0.05     # 5% fee
  
ui:
  date_format: "yyyy-MM-dd"   # Date display format
  
language: "en"               # Language setting

# Cloud integration (optional)
cloud:
  enabled: false
  base_url: "https://your-worker.your-subdomain.workers.dev"
  api_token: ""
  server_id: "default-server"
```

## ☁️ Cloud Integration

Best Auction can synchronize auction data with external cloud services, enabling data sharing between servers, analytics, and backups.

### Cloud Integration Features

- **Real-Time Event Reporting** - Immediately send events such as listings, bids, and sales
- **Data Synchronization** - Periodic synchronization of auction data
- **API Token Authentication** - Secure authentication
- **Automatic Retries** - Automatically resend failed requests

### Event Types

1. **ITEM_LISTED** - When an item is listed
2. **BID_PLACED** - When a bid is placed
3. **BID_CANCELLED** - When a bid is cancelled
4. **AUCTION_CANCELLED** - When an auction is cancelled
5. **ITEM_SOLD** - When an item is sold

### Data Format

Events sent to the cloud use JSON and include ISO 8601 timestamps (UTC):

```json
{
  "event_type": "ITEM_LISTED",
  "server_id": "server-1",
  "timestamp": "2025-06-28T14:30:45.123Z",
  "auction_id": 123,
  "data": {
    "seller_uuid": "uuid-here",
    "seller_name": "PlayerName",
    "item_name": "Diamond Sword",
    "item_type": "DIAMOND_SWORD",
    "quantity": 1,
    "start_price": 1000,
    "buyout_price": 5000
  }
}
```

### Cloud Commands

```
/ah cloud status       # Show cloud integration status
/ah cloud validate     # Validate the API token
/ah cloud sync [force] # Manually synchronize data
```

### Setup

1. Deploy a cloud service (such as Cloudflare Workers)
2. Obtain an API token
3. Enable cloud settings in `config.yml`
4. Restart the server

See [CLOUD_SETUP_GUIDE.md](CLOUD_SETUP_GUIDE.md) for detailed setup instructions.

## 📚 Documentation

See [PLUGIN_DOCUMENTATION.md](PLUGIN_DOCUMENTATION.md) for detailed usage and configuration instructions.

## 🔧 Development

### Build
```bash
git clone https://github.com/0x48lab/best_auction.git
cd best_auction
./gradlew shadowJar
```

### Start the Development Server
```bash
./gradlew runServer
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

## 🚀 Deployment

This project uses GitHub Actions for automatic deployment:

1. Push a `v*` tag (for example, `v1.0.0`)
2. The build runs automatically
3. A release is created on GitHub Releases
4. The plugin is automatically deployed to Modrinth

### Release Steps
```bash
git tag v1.0.0
git push origin v1.0.0
```

## 🤝 Contributing

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push the branch (`git push origin feature/amazing-feature`)
5. Create a pull request

## 📄 License

This project is released under the MIT License. See [LICENSE](LICENSE) for details.

## 🆘 Support

- 🐛 Bug reports: [GitHub Issues](https://github.com/0x48lab/best_auction/issues)
- 💡 Feature requests: [GitHub Discussions](https://github.com/0x48lab/best_auction/discussions)
- 📧 Direct contact: Contact the project maintainer

## 🏆 Acknowledgments

This plugin was developed with assistance from [Claude Code](https://claude.ai/code).

---

**🌟 If you find this plugin useful, please give it a star!**