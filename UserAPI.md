# 🏪 GlobalShop - 开发者API文档

## 📋 项目概述
GlobalShop是一个功能强大的Minecraft拍卖行插件，基于Paper 1.20.1开发，兼容1.21.5版本。提供类似魔兽世界拍卖行的完整功能体验。

## 🏗️ 项目架构

### 📁 文件结构树
```
src/main/java/cn/i7mc/globalshop/
├── 🏠 GlobalShop.java                    # 插件主类
├── 📝 commands/
│   └── AuctionCommand.java               # 命令处理器
├── ⚙️ config/
│   ├── ConfigManager.java                # 配置管理器
│   ├── MessageManager.java               # 消息管理器
│   └── DebugMessageManager.java          # 调试消息管理器
├── 🗄️ database/
│   └── DatabaseManager.java              # 数据库管理器
├── 💰 economy/
│   └── EconomyManager.java               # 经济系统管理器
├── 📊 enums/
│   └── SortType.java                     # 排序类型枚举
├── 🖥️ gui/
│   └── GuiManager.java                   # GUI界面管理器
├── 🌟 hologram/
│   ├── HologramDisplayManager.java       # 全息显示管理器
│   ├── ItemDisplayManager.java           # 物品显示管理器
│   ├── TextDisplayManager.java           # 文本显示管理器
│   ├── AuctionHistoryManager.java        # 拍卖历史管理器
│   ├── HologramConfigManager.java        # 全息配置管理器
│   ├── HologramCommandManager.java       # 全息命令管理器
│   └── HologramUpdateTask.java           # 全息更新任务
├── 👂 listeners/
│   └── GuiListener.java                  # GUI事件监听器
├── 📈 metrics/
│   └── Metrics.java                      # 插件统计
├── 📦 models/
│   └── AuctionItem.java                  # 拍卖物品模型
├── ⏰ tasks/
│   ├── AuctionTask.java                  # 拍卖定时任务
│   ├── CheckAllAuctionsTask.java         # 检查所有拍卖任务
│   └── CloseAllAuctionsTask.java         # 关闭所有拍卖任务
├── 🛠️ utils/
│   ├── BroadcastManager.java             # 广播管理器
│   ├── ChatUtils.java                    # 聊天工具类
│   ├── MinecraftLanguageManager.java     # 语言管理器
│   ├── SearchHistoryManager.java         # 搜索历史管理器
│   └── SortManager.java                  # 排序管理器
└── 🌐 web/
    ├── WebServer.java                    # Web服务器
    ├── WebConfig.java                    # Web配置
    ├── WebController.java                # Web控制器
    └── WebDataProvider.java              # Web数据提供者
```

## 🚀 核心API功能

### 🏠 主插件类 (GlobalShop.java)
```java
// 获取插件实例
GlobalShop plugin = GlobalShop.getInstance();

// 获取各种管理器
ConfigManager configManager = plugin.getConfigManager();
DatabaseManager databaseManager = plugin.getDatabaseManager();
EconomyManager economyManager = plugin.getEconomyManager();
GuiManager guiManager = plugin.getGuiManager();
```

### 🗄️ 数据库操作 (DatabaseManager.java)
```java
// 获取活跃拍卖物品
List<AuctionItem> items = databaseManager.getActiveAuctionItems(page, limit, orderBy);

// 搜索拍卖物品
List<AuctionItem> searchResults = databaseManager.searchAuctionItems(keyword, page, limit);

// 创建新拍卖
int auctionId = databaseManager.createAuction(sellerUuid, sellerName, item, startPrice, buyNowPrice, currencyType, duration);

// 处理过期拍卖
int expiredCount = databaseManager.processExpiredAuctions();
```

### 💰 经济系统 (EconomyManager.java)
```java
// 检查余额
boolean hasEnough = economyManager.hasEnough(player, amount, currencyType);

// 扣款
boolean success = economyManager.withdraw(player, amount, currencyType);

// 充值
boolean success = economyManager.deposit(player, amount, currencyType);

// 格式化金额显示
String formatted = economyManager.formatAmount(amount, currencyType);
```

### 🖥️ GUI界面管理 (GuiManager.java)
```java
// 打开主菜单
guiManager.openMainMenu(player);

// 打开搜索界面
guiManager.openSearchMenu(player);

// 打开搜索结果
guiManager.openSearchResultMenu(player, keyword, page);

// 打开我的拍卖
guiManager.openMyAuctionsMenu(player, page);
```

### 🌟 全息显示系统
```java
// 创建全息显示
HologramDisplayManager hologramManager = plugin.getHologramDisplayManager();
hologramManager.createHologram(location, name);

// 更新全息内容
hologramManager.updateHologram(name, newContent);

// 移除全息显示
hologramManager.removeHologram(name);
```

### 🛠️ 工具类API

#### 📢 广播管理器 (BroadcastManager.java)
```java
// 发送拍卖广播
broadcastManager.broadcastAuctionListed(item, seller);
broadcastManager.broadcastBidSuccess(item, bidder, bidAmount);
broadcastManager.broadcastBuyNowSuccess(item, buyer);
```

#### 🔍 搜索历史管理 (SearchHistoryManager.java)
```java
// 添加搜索历史
searchHistoryManager.addSearchHistory(player, keyword);

// 获取搜索历史
List<String> history = searchHistoryManager.getSearchHistory(player);

// 清除搜索历史
searchHistoryManager.clearSearchHistory(player);
```

#### 🌍 语言管理器 (MinecraftLanguageManager.java)
```java
// 获取中文物品名称
String chineseName = languageManager.getChineseName(itemId);

// 根据中文名称查找物品ID
List<String> itemIds = languageManager.findItemIdsByChineseName(chineseName);
```

## 📦 数据模型

### 🏷️ 拍卖物品模型 (AuctionItem.java)
```java
// 创建拍卖物品
AuctionItem item = new AuctionItem(id, sellerUuid, sellerName, itemStack, 
    startPrice, buyNowPrice, currentPrice, currencyType, startTime, endTime, status);

// 常用方法
boolean isActive = item.isActive();
boolean isExpired = item.isExpired();
boolean canBuyNow = item.canBuyNow();
String remainingTime = item.getFormattedRemainingTime();
```

### 📊 排序类型枚举 (SortType.java)
```java
// 排序类型
SortType.TIME_ASC      // 最近上架
SortType.TIME_DESC     // 即将结束
SortType.PRICE_HIGH    // 最高价格
SortType.PRICE_LOW     // 最低价格
SortType.NAME          // 物品名称

// 获取下一个排序类型
SortType nextSort = currentSort.getNext();
```

## 🌐 Web API

### 🖥️ Web服务器 (WebServer.java)
```java
// 启动Web服务
WebServer webServer = plugin.getWebServer();
boolean started = webServer.start();

// 停止Web服务
webServer.stop();

// 重启Web服务
boolean restarted = webServer.restart();
```

## 📝 命令系统

### 🎯 基础命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction` | 打开拍卖行主界面 | `globalshop.use` |
| `/auction help` | 显示帮助信息 | `globalshop.use` |
| `/auction sell <起拍价> [一口价] [货币类型]` | 上架物品 | `globalshop.sell` |
| `/auction search <关键词>` | 搜索物品 | `globalshop.use` |
| `/auction my` | 查看我的拍卖 | `globalshop.use` |
| `/auction collect` | 领取待领取物品 | `globalshop.use` |

### 🔧 管理员命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction reload` | 重新加载配置 | `globalshop.admin` |
| `/auction close` | 强制关闭所有拍卖 | `globalshop.admin` |
| `/auction checkexpired` | 检查过期物品 | `globalshop.admin` |
| `/auction info <玩家>` | 查询玩家信息 | `globalshop.admin.info` |

### 🌟 全息命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction hud create <名称>` | 创建全息拍卖行 | `globalshop.admin.hud` |
| `/auction hud remove <名称>` | 移除全息拍卖行 | `globalshop.admin.hud` |
| `/auction hud list` | 列出所有全息拍卖行 | `globalshop.admin.hud` |
| `/auction hud reload` | 重新加载全息配置 | `globalshop.admin.hud` |

## 🔐 权限系统

### 👤 基础权限
- `globalshop.use` - 基础使用权限 (默认: true)
- `globalshop.sell` - 上架物品权限 (默认: true)
- `globalshop.buy` - 购买物品权限 (默认: true)

### 👑 管理员权限
- `globalshop.admin` - 管理员总权限 (默认: op)
- `globalshop.admin.reload` - 重载配置权限 (默认: op)
- `globalshop.admin.close` - 关闭拍卖权限 (默认: op)
- `globalshop.admin.checkexpired` - 检查过期权限 (默认: op)
- `globalshop.admin.hud` - 全息管理权限 (默认: op)
- `globalshop.admin.info` - 查询玩家信息权限 (默认: op)

## 🎨 事件监听

### 📱 GUI事件处理 (GuiListener.java)
```java
// 主要事件监听方法
@EventHandler
public void onInventoryClick(InventoryClickEvent event)

@EventHandler  
public void onInventoryDrag(InventoryDragEvent event)

@EventHandler
public void onInventoryClose(InventoryCloseEvent event)

@EventHandler
public void onPlayerChat(AsyncPlayerChatEvent event)
```

## 🌍 多语言支持

### 📚 支持的语言
- 🇨🇳 中文 (默认)
- 🇺🇸 英语 (en)
- 🇪🇸 西班牙语 (es)
- 🇩🇪 德语 (de)
- 🇫🇷 法语 (fr)
- 🇯🇵 日语 (ja)
- 🇷🇺 俄语 (ru)
- 🇻🇳 越南语 (vi)

### 💬 消息管理 (MessageManager.java)
```java
// 获取消息
String message = messageManager.getCommandHelpHeaderMessage();

// 带占位符的消息
String formatted = messageManager.getAuctionStartPriceFormat()
    .replace("%price%", formattedPrice);
```

## 💾 数据库支持

### 🗃️ 支持的数据库类型
- SQLite (默认)
- MySQL (可配置)

### 📋 主要数据表
- `auction_items` - 拍卖物品表
- `pending_items` - 待领取物品表

## 🔧 配置系统

### ⚙️ 主要配置项
- 数据库配置 (SQLite/MySQL)
- 经济系统配置 (Vault/PlayerPoints)
- GUI界面配置
- 拍卖时长配置
- 上架限制配置
- 广播系统配置
- Web服务配置

## 📊 统计功能

### 📈 bStats集成 (Metrics.java)
- 数据库类型统计
- Web服务启用状态
- 全息显示启用状态
- PlayerPoints可用性统计

## 🚀 快速开始

### 1️⃣ 基础使用
```java
// 获取插件实例
GlobalShop plugin = GlobalShop.getInstance();

// 为玩家打开拍卖行
plugin.getGuiManager().openMainMenu(player);
```

### 2️⃣ 创建拍卖
```java
// 创建新拍卖
DatabaseManager db = plugin.getDatabaseManager();
int auctionId = db.createAuction(
    player.getUniqueId(),
    player.getName(), 
    itemStack,
    startPrice,
    buyNowPrice,
    "VAULT",
    duration
);
```

### 3️⃣ 处理经济交易
```java
EconomyManager economy = plugin.getEconomyManager();

// 检查余额
if (economy.hasEnough(player, price, "VAULT")) {
    // 执行扣款
    economy.withdraw(player, price, "VAULT");
}
```

---
## 🎯 核心功能特性

### 🏪 拍卖行系统
- **📋 物品上架**: 支持起拍价和一口价设置
- **💰 双币种支持**: Vault经济系统 + PlayerPoints点券系统
- **⏰ 自定义时长**: 可设置拍卖持续时间
- **🔍 智能搜索**: 支持物品名称、卖家名称搜索
- **📊 多种排序**: 时间、价格、名称等多维度排序
- **📱 分页浏览**: 支持大量物品的分页显示

### 🌟 全息显示系统
- **🎨 3D物品展示**: 真实的3D物品旋转显示
- **📝 动态文本**: 实时更新的拍卖信息文本
- **⚡ 自动更新**: 定时刷新全息内容
- **🎯 多点位支持**: 可创建多个全息拍卖行
- **🔧 管理命令**: 完整的创建、删除、管理功能

### 📢 广播系统
- **🎪 多位置显示**: 聊天框、Boss栏、标题、副标题、动作栏
- **🎨 悬停详情**: 聊天框物品悬停显示完整信息
- **🌈 渐变色支持**: 完美保留物品的所有颜色效果
- **⚙️ 完全自定义**: 所有广播消息可通过配置文件自定义
- **🎯 事件触发**: 上架、竞拍、购买等事件自动广播

### 🌐 Web界面系统
- **📱 响应式设计**: 支持PC和移动端访问
- **🔍 在线搜索**: Web端物品搜索功能
- **📊 数据展示**: 实时拍卖数据展示
- **⚙️ 可配置端口**: 自定义Web服务端口

### 🌍 多语言系统
- **🗣️ 8种语言**: 中文、英语、西班牙语、德语、法语、日语、俄语、越南语
- **🔄 动态切换**: 支持运行时语言切换
- **📝 完整翻译**: 所有界面文本和消息的完整翻译
- **🎯 物品名称**: 支持中英文物品名称转换

## 🛡️ 安全与性能

### 🔒 安全特性
- **💰 交易安全**: 完整的经济交易验证
- **🛡️ 权限控制**: 细粒度的权限管理
- **🔍 数据验证**: 严格的输入数据验证
- **📊 审计日志**: 完整的操作日志记录

### ⚡ 性能优化
- **🗄️ 数据库优化**: 高效的SQL查询和索引
- **💾 内存管理**: 智能的缓存和内存使用
- **⏰ 异步处理**: 非阻塞的数据库操作
- **🔄 定时任务**: 优化的定时任务调度

## 🔧 开发者工具

### 🐛 调试功能
```java
// 启用调试模式
ConfigManager config = plugin.getConfigManager();
boolean debugEnabled = config.isDebug();

// 获取调试消息
DebugMessageManager debugManager = plugin.getDebugMessageManager();
String debugMessage = debugManager.getDebugMessage("key");
```

### 📊 统计数据
```java
// 获取统计信息
DatabaseManager db = plugin.getDatabaseManager();
int totalItems = db.getTotalActiveItems();
int playerListings = db.countPlayerActiveAuctions(playerUuid);
```

### 🔄 配置热重载
```java
// 重载所有配置
plugin.getConfigManager().loadConfig();
plugin.getMessageManager().reloadMessages();
plugin.getBroadcastManager().loadConfig();
```

## 📋 最佳实践

### 🎯 插件集成
```java
// 检查插件依赖
if (plugin.isPlayerPointsAvailable()) {
    // PlayerPoints可用时的逻辑
}

// 安全的经济操作
EconomyManager economy = plugin.getEconomyManager();
if (economy.hasEnough(player, amount, currencyType)) {
    boolean success = economy.withdraw(player, amount, currencyType);
    if (success) {
        // 交易成功后的处理
    }
}
```

### 🛡️ 错误处理
```java
try {
    // 数据库操作
    List<AuctionItem> items = databaseManager.getActiveAuctionItems(page, limit, orderBy);
} catch (Exception e) {
    plugin.getLogger().severe("数据库操作失败: " + e.getMessage());
    // 错误处理逻辑
}
```

### 📱 GUI开发
```java
// 创建自定义GUI
public void openCustomMenu(Player player) {
    Inventory inventory = Bukkit.createInventory(null, 54, "自定义菜单");

    // 设置物品
    ItemStack item = new ItemStack(Material.DIAMOND);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName("§b钻石");
    item.setItemMeta(meta);
    inventory.setItem(22, item);

    player.openInventory(inventory);
}
```

## 🔗 扩展开发

### 📦 自定义模块
```java
// 创建自定义管理器
public class CustomManager {
    private final GlobalShop plugin;

    public CustomManager(GlobalShop plugin) {
        this.plugin = plugin;
    }

    public void customFunction() {
        // 自定义功能实现
    }
}
```

### 🎨 自定义事件
```java
// 监听拍卖事件
@EventHandler
public void onAuctionCreate(AuctionCreateEvent event) {
    AuctionItem item = event.getAuctionItem();
    Player seller = event.getSeller();
    // 自定义处理逻辑
}
```

---

## 📞 技术支持

- **作者**: Saga
- **QQ**: 642751482
- **版本**: 1.4.0.12
- **兼容性**: Bukkit 1.20.1 - 1.21.5
- **GitHub**: https://github.com/linghun91/GlobalShop

---

*本文档基于GlobalShop v1.4.0.12版本编写，如有更新请参考最新版本。*
