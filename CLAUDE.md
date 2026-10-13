# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Best Auction is an auction house plugin for Minecraft Paper/Spigot servers. Written in Kotlin, it lets players list, bid on, and purchase items.

## Build and Run Commands

### Basic Gradle Commands
- `./gradlew build` - Build the project (including shadowJar)
- `./gradlew shadowJar` - Generate a fat JAR (including all dependencies)
- `./gradlew runServer` - Start a development Minecraft server (version 1.20)

### Development Paths
- Plugin JAR: `build/libs/best_auction-1.0-SNAPSHOT-all.jar`
- Server configuration directory: `run/`
- Plugin configuration directory: `run/plugins/best_auction/`

## Architecture

The plugin consists of the following main components:

### Database Layer
- **DatabaseManager**: SQLite database initialization and management
- **Tables.kt**: Table definitions using Exposed ORM
  - AuctionItems: Auction item information
  - Bids: Bid information
  - MailBox: Mail system
  - AuctionSettings: Auction settings
  - PlayerLanguageSettings: Player language settings

### Manager Classes
- **AuctionManager**: Auction listing, bidding, and cancellation handling
- **MailManager**: Item mail delivery system

### UI and Handlers
- **AuctionUI**: Main auction house GUI
- **LanguageSettingsUI**: Language settings GUI
- **BidHandler**: Event handler for bidding
- **SearchHandler**: Search handler

### Data Classes
- **AuctionData.kt**: Auction-related data class definitions
- **ItemUtils**: Item utilities (serialization, price calculation, etc.)

### Language Resources
- **LangManager**: Language management system
- English language file: `src/main/resources/lang/en.yml`

## Technology Stack

- **Language**: Kotlin 1.9.24
- **Build Tool**: Gradle with Kotlin DSL
- **Framework**: PaperMC API 1.20.6
- **Database**: Jetbrains Exposed ORM + SQLite
- **Economy Plugin**: Vault API integration

## Configuration Files

- `src/main/resources/config.yml`: Main configuration (fees, duration, UI settings, etc.)
- `src/main/resources/plugin.yml`: Bukkit plugin definition
- `run/plugins/best_auction/config.yml`: Runtime configuration file

## Development Guidelines

- Perform all database operations inside `transaction` blocks
- Use `ItemUtils` for item serialization and deserialization
- Retrieve localized messages through `LangManager`
- Access the economy system through the Vault API
- Use Minecraft's inventory GUI system for the UI

## Important Message Rules

**Important**: This plugin is intended for children. Keep messages clear and easy to understand.

1. **All user-facing messages must be in English**
   - Retrieve messages through `LangManager`
   - Add new messages to `src/main/resources/lang/en.yml`
   - The project uses English only; do not reference or load `ja.yml`

2. **Procedure for Adding Messages**
   ```kotlin
   // Retrieve the English message through LangManager
   plugin.langManager.sendMessage(sender, "cloud.sync_completed", auctionCount, bidCount)
   ```

3. **Debug Messages**
   - Log output (`plugin.logger`) must also be in English

## Active Technologies
- Kotlin 1.9.24 + PaperMC API 1.21.4, Jetbrains Exposed ORM 0.44.1, Vault API (001-simplify-auction-categories)
- SQLite (via Exposed ORM) (001-simplify-auction-categories)

## Recent Changes
- 001-simplify-auction-categories: Added Kotlin 1.9.24 + PaperMC API 1.21.4, Jetbrains Exposed ORM 0.44.1, Vault API
