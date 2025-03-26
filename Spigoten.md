# GlobalShop - Global Auction House Plugin

## Introduction
GlobalShop is a plugin that provides World of Warcraft style auction house functionality for Minecraft servers, supporting Spigot 1.21.4 version.

## Core Features

### Auction System
- **Item Listing Features**
  - Players can list items via GUI or commands
  - Support for setting starting price and buyout price
  - Customizable auction duration (Left-click +1 minute, Right-click +1 hour, Shift+Left-click +10 minutes, Shift+Right-click +10 hours)
  - Listing quantity limit functionality, configurable maximum listings per player
  - Price digit limit feature, preventing excessively high-priced items

- **Purchase and Bidding System**
  - Direct buyout purchase
  - Bidding system
  - Optimized bid increment system, supporting pre-increment buttons
  - Automatic settlement when auctions end
  - Comprehensive transaction record system, tracking buyers, sellers, transaction times and more

- **Item Collection System**
  - Automatic handling of items for offline players or those with full inventories
  - Secure item mailbox functionality
  - Command to collect pending items

### Economy System
- **Dual Currency Support**
  - Complete Vault economy integration
  - PlayerPoints point integration (soft dependency, not mandatory)
  - Option to choose currency type when listing
  - Consistent currency type throughout the transaction process

- **Fee System**
  - Configurable buyer/seller fees
  - Independent fee settings for both currency types

### GUI System
- **Main Interface**
  - Intuitive auction item display
  - Complete pagination system, displaying 45 items per page
  - Simplified operations: Left-click to bid, Right-click for instant buyout

- **My Auctions Interface**
  - View currently listed items
  - View sold auctions, displaying buyers and actual selling prices
  - View expired auctions
  - Display player's current listing count and maximum limit

- **Search System**
  - Fuzzy item name search
  - Support for item names with color codes
  - Search history functionality
  - Support for Chinese vanilla item name search (e.g., searching "redstone" finds all redstone-related items)

### Broadcast System
- **Multi-location Broadcasting**
  - Chat box broadcasts
  - Boss bar broadcasts
  - Title broadcasts
  - Subtitle broadcasts
  - Action bar broadcasts

- **Multiple Event Broadcasting**
  - Item listing broadcasts
  - Successful bid broadcasts
  - Buyout purchase broadcasts

- **Broadcast Optimizations**
  - Interactive detailed item information in chat box
  - Complete item information display on mouse hover (including all gradient color effects)
  - Fully customizable broadcast messages

### Multi-language Support
- Complete support for 10 languages:
  - Chinese, English, German, Russian, Spanish, French, Italian, Japanese, Portuguese
  - Partial support for Arabic and Hindi
- All GUI texts and message prompts support multiple languages

### Administration Features
- **Admin Tools**
  - Force delist functionality (admins can force delist any item, including their own)
  - Configuration hot reload functionality
  - Manual command to check expired items

## Command System
```
/auction help - Display help information
/auction open - Open the auction house interface
/auction sell <starting price> [buyout price] [currency type] - List item (1=money, 2=points)
/auction buy <item ID> - Purchase an item
/auction search <keyword> - Search for items
/auction my - View my auctions
/auction collect - Collect pending items
/auction reload - Reload configuration files (requires admin permission)
/auction close - Force close all auctions (admin only)
/auction checkexpired - Manually check expired items (admin only)
```

## Permission Nodes
```
globalshop.use - Basic usage permission
globalshop.sell - Listing permission
globalshop.buy - Purchase permission
globalshop.admin - Administrator permission
```

## Technical Features
- SQLite database storage, efficient and reliable
- Complete item serialization system, supporting all Minecraft item types
- Full compatibility with custom items (preserves all NBT data)
- Asynchronous processing mechanism ensuring server performance
- Highly customizable configuration system 