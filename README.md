# 🏪 GlobalShop - 全局拍卖行插件

<div align="center">

![Version](https://img.shields.io/badge/version-1.4.0.15-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.20.1--1.21.5-green.svg)
![API](https://img.shields.io/badge/api-Paper-orange.svg)
![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)

**一个功能强大的Minecraft拍卖行插件，提供类似魔兽世界拍卖行的完整功能体验**

</div>

---

## 📋 项目概述

GlobalShop是一个基于**Paper 1.20.1**开发，兼容**1.21.5**版本的专业级Minecraft服务器拍卖行插件。插件提供类似魔兽世界拍卖行的完整功能体验，支持双货币系统、全息显示、Web界面、多语言等高级功能。

### ✨ 核心特性
- 🏪 **完整拍卖系统** - 起拍价、一口价、竞价功能
- 💰 **双货币支持** - Vault经济系统 + PlayerPoints点券系统
- 🌟 **全息显示** - 3D物品展示 + 动态拍卖历史
- 🌐 **Web界面** - 响应式设计，支持PC和移动端
- 🌍 **多语言支持** - 8种语言完整翻译
- 📢 **智能广播** - 多位置显示，支持物品悬停详情
- 🔍 **强大搜索** - 物品名称、卖家搜索，搜索历史
- 📊 **数据统计** - bStats集成，完整的交易统计

## 🚀 项目状态

✅ **已完成功能**
- 完整的拍卖系统架构
- 双数据库支持（SQLite/MySQL）
- 双货币系统集成
- 全息显示系统
- Web服务系统
- 多语言支持系统
- 广播系统优化
- 物品邮箱系统

🔄 **当前进展**
- 界面美观度优化
- 性能提升优化
- 更多搜索筛选选项
- 用户体验改进

---

## 🏗️ 项目架构

### 🎯 核心模块

#### 🏠 主插件类
- **`GlobalShop.java`** - 插件主类，负责初始化和管理其他组件
  - 🔧 初始化各个管理器（配置、数据库、经济、GUI等）
  - 📝 注册命令和事件监听器
  - ⏰ 启动定时任务（拍卖检查、全息更新）
  - 🔗 提供访问各个管理器的统一接口
  - 📊 集成bStats统计功能

#### ⚙️ 配置管理模块
- **`ConfigManager.java`** - 主配置管理器
  - 📄 加载config.yml配置文件
  - 🎛️ 管理数据库、经济、GUI等核心配置
  - 🔄 支持配置热重载功能
  - 📏 提供上架限制、价格限制等配置访问

- **`MessageManager.java`** - 消息管理器
  - 🌍 支持多语言消息配置（8种语言）
  - 📝 管理GUI界面文本和提示消息
  - 🔄 提供动态占位符替换功能
  - 🎨 支持颜色代码和格式化

- **`DebugMessageManager.java`** - 调试消息管理器
  - 🐛 管理调试消息配置
  - 🔍 支持开发调试和错误追踪
  - 🎛️ 支持动态开关调试输出
  - 📊 提供详细的调试信息

#### 🗄️ 数据库模块
- **`DatabaseManager.java`** - 数据库管理器
  - 💾 **双数据库支持**: SQLite（默认）+ MySQL
  - 🏷️ **表前缀支持**: MySQL自定义表前缀避免冲突
  - 📦 **拍卖物品管理**:
    - 创建、更新、删除拍卖物品
    - 获取活跃、已过期、已售出拍卖物品
    - 高效的分页查询和搜索功能
    - 统计玩家当前活跃拍卖数量
  - 📬 **待领取物品管理**:
    - 安全的物品邮箱系统
    - 过期物品自动存储
    - 竞拍失败物品退还

#### 💰 经济系统模块
- **`EconomyManager.java`** - 经济管理器
  - 💎 **双货币支持**: Vault金币 + PlayerPoints点券
  - 🔍 **余额管理**: 检查余额、扣款、充值操作
  - 💱 **格式化显示**: 智能货币格式化和显示
  - 💸 **手续费系统**:
    - 可配置的买家手续费率
    - 可配置的卖家手续费率
    - 最低手续费限制
  - 📈 **竞价计算**:
    - 最低加价金额计算
    - 最低加价比例计算
    - 价格位数限制检查

#### 🖥️ GUI界面模块
- **`GuiManager.java`** - GUI管理器
  - 🏠 **主菜单界面**: 拍卖行主入口，物品浏览和排序
  - 🔍 **搜索系统**:
    - 搜索界面 (openSearchMenu)
    - 搜索结果界面 (openSearchResultMenu)
    - 搜索历史管理
  - 💰 **交易界面**:
    - 物品出售界面 (openSellMenu)
    - 确认购买界面 (openConfirmBuyMenu)
    - 竞价界面 (openBidMenu)
    - 竞价金额输入界面 (openBidAmountMenu)
  - 📊 **个人中心**:
    - 我的拍卖界面 (openMyAuctionsMenu)
    - 已售出拍卖界面 (openMySoldAuctionsMenu)
    - 物品邮箱界面 (openMyMailboxMenu)
  - 🎛️ **界面数据管理**:
    - 玩家页码跟踪系统
    - 玩家搜索查询跟踪
    - 排序状态管理

#### 🌟 全息显示模块
- **`HologramDisplayManager.java`** - 全息显示管理器
  - 🎨 创建和管理3D全息显示
  - 🔄 处理全息显示实时更新
  - 📍 支持多点位全息拍卖行
  - 🎯 动态显示内容管理

- **`ItemDisplayManager.java`** - 物品显示管理器
  - 🎮 管理3D物品全息显示
  - 🌀 支持物品旋转展示效果
  - 💎 高质量物品渲染
  - 🎨 自定义显示样式

- **`TextDisplayManager.java`** - 文本显示管理器
  - 📝 管理动态文本全息显示
  - 🎨 支持颜色和格式化
  - 🔄 实时文本内容更新
  - 📊 拍卖信息动态展示

- **`AuctionHistoryManager.java`** - 拍卖历史管理器
  - 📈 管理拍卖历史记录
  - 🔍 支持历史记录查询
  - 📊 历史数据统计分析
  - 🎯 热门物品追踪

- **`HologramConfigManager.java`** - 全息配置管理器
  - ⚙️ 加载全息显示配置
  - 🎛️ 管理全息显示设置
  - 🔄 支持配置热重载
  - 📍 位置和样式配置

- **`HologramCommandManager.java`** - 全息命令管理器
  - 🎯 处理全息相关命令
  - 📍 管理全息显示位置
  - 🔧 全息显示配置管理
  - 👑 管理员权限控制

- **`HologramUpdateTask.java`** - 全息更新任务
  - ⏰ 定期更新全息显示
  - 🔄 处理全息显示刷新
  - ⚡ 优化显示性能
  - 📊 实时数据同步

#### 👂 事件监听模块
- **`GuiListener.java`** - GUI事件监听器
  - 🖱️ **核心事件监听**:
    - `onInventoryClick` - 处理所有界面点击事件
    - `onInventoryDrag` - 处理物品拖拽防护
    - `onInventoryClose` - 处理界面关闭（保存上架物品）
    - `onPlayerChat` - 处理聊天输入（竞价金额、搜索关键词）
  - 🎯 **界面交互处理**:
    - 主菜单点击处理 (handleMainMenuClick)
    - 搜索界面交互 (handleSearchMenuClick)
    - 搜索结果处理 (handleSearchResultClick)
    - 物品出售界面 (handleSellMenuClick)
    - 拍卖物品交互 (handleAuctionItemClick)
    - 确认购买处理 (handleConfirmBuyClick)
    - 竞价系统处理 (handleBidMenuClick)
    - 个人拍卖管理 (handleMyAuctionsMenuClick)
    - 取消拍卖操作 (handleCancelAuction)

#### 📝 命令系统模块
- **`AuctionCommand.java`** - 拍卖命令处理器
  - 🎯 **主命令处理**: 统一的命令入口和分发
  - 📋 **用户命令**:
    - `help` - 显示帮助信息
    - `sell` - 上架物品命令
    - `search` - 搜索物品命令
    - `my` - 查看我的拍卖
    - `collect` - 领取待领取物品
  - 👑 **管理员命令**:
    - `reload` - 重载配置文件
    - `close` - 强制关闭所有拍卖
    - `checkexpired` - 手动检查过期物品
    - `info` - 查询玩家信息
  - 🌟 **全息命令**:
    - `hud create` - 创建全息拍卖行
    - `hud remove` - 移除全息拍卖行
    - `hud list` - 列出所有全息拍卖行
    - `hud reload` - 重载全息配置
  - 🔧 **Tab补全**: 完整的命令自动补全支持

#### 📦 数据模型模块
- **`AuctionItem.java`** - 拍卖物品模型类
  - 📋 **基本信息**: ID、卖家UUID、卖家名称、物品数据
  - 💰 **价格信息**: 起拍价、一口价、当前价格、货币类型
  - ⏰ **时间信息**: 开始时间、结束时间、剩余时间
  - 📊 **状态信息**: 活跃、已售出、已过期、已取消
  - 🎯 **竞价信息**: 当前出价者UUID、竞价历史
  - 🔧 **实用方法**:
    - 状态判断 (isActive, isExpired, canBuyNow, canBid)
    - 时间格式化 (getFormattedRemainingTime)
    - 价格计算和验证

#### 🛠️ 工具类模块
- **`BroadcastManager.java`** - 广播管理器
  - 📢 **多位置广播**: 聊天框、Boss栏、标题、副标题、动作栏
  - 🎯 **事件广播**: 物品上架、竞拍成功、一口价购买、竞价确认
  - 🎨 **物品悬停**: 完整的物品信息悬停显示
  - 🔄 **动态配置**: 支持实时配置重载
  - 🌈 **渐变色支持**: 完整保留物品渐变色效果

- **`ChatUtils.java`** - 聊天和文本工具类
  - 📝 **物品名称**: 智能物品名称获取和格式化
  - ⏰ **时间格式化**: 友好的时间显示格式
  - 🎨 **消息格式化**: 颜色代码和格式处理
  - 🔤 **文本处理**: 字符串处理和验证

- **`MinecraftLanguageManager.java`** - 语言管理器
  - 🌍 **双向映射**: 英文ID ↔ 中文名称
  - 📚 **翻译文件**: minecraft_lang.yml加载管理
  - 🔍 **智能搜索**: 根据中文关键词查找物品ID
  - 🎯 **多语言支持**: 支持多种语言物品名称转换
  - 🔧 **模糊匹配**: 支持部分匹配和相似度搜索

- **`SearchHistoryManager.java`** - 搜索历史管理器
  - 📝 **历史存储**: 每个玩家的搜索历史记录
  - 🔄 **历史管理**: 添加、获取、清除搜索记录
  - 📏 **限制控制**: 可配置的历史记录数量限制
  - 🧹 **自动清理**: 定期清理过期历史记录

- **`SortManager.java`** - 排序管理器
  - 📊 **多维排序**: 时间、价格、名称等多种排序方式
  - 🔄 **排序状态**: 升序、降序状态管理
  - 🎯 **智能排序**: 根据用户偏好自动排序
  - ⚡ **性能优化**: 高效的排序算法实现

#### ⏰ 定时任务模块
- **`AuctionTask.java`** - 拍卖定时任务
  - 🔍 **过期检查**: 定期检查过期拍卖
  - 🎯 **结果处理**: 处理成功竞拍、未售出等情况
  - 📬 **物品存储**: 自动存储待领取物品到邮箱
  - 📢 **通知发送**: 发送拍卖结果通知给相关玩家
  - 💰 **资金处理**: 自动转账和退款

- **`CheckAllAuctionsTask.java`** - 拍卖状态检查任务
  - 🔍 **状态检查**: 检查所有拍卖状态
  - 🛠️ **异常处理**: 处理异常拍卖和数据修复
  - 🔄 **信息更新**: 更新拍卖信息和状态
  - 📊 **数据同步**: 确保数据一致性

- **`CloseAllAuctionsTask.java`** - 强制关闭任务
  - 🚫 **强制关闭**: 管理员强制关闭所有拍卖
  - 💰 **退款处理**: 退还所有竞价金额
  - 📦 **物品返还**: 返还所有物品给卖家
  - 📢 **通知发送**: 通知所有相关玩家

#### 🌐 Web服务模块
- **`WebServer.java`** - Web服务器
  - 🌐 **HTTP服务**: 内置轻量级HTTP服务器
  - 📱 **响应式设计**: 支持PC和移动端访问
  - 🔒 **安全控制**: 访问权限和安全防护
  - ⚡ **高性能**: 异步处理和缓存优化

- **`WebController.java`** - Web控制器
  - 🎯 **路由处理**: RESTful API路由管理
  - 📊 **数据接口**: 提供拍卖数据API
  - 🔄 **实时更新**: 支持实时数据推送
  - 🎨 **模板渲染**: 动态页面生成

- **`WebDataProvider.java`** - Web数据提供者
  - 📊 **数据转换**: 数据库数据转换为Web格式
  - 🔍 **查询优化**: 高效的数据查询和缓存
  - 📈 **统计数据**: 提供拍卖统计和分析数据
  - 🎯 **实时同步**: 与游戏内数据实时同步

- **`WebConfig.java`** - Web配置管理
  - ⚙️ **配置管理**: Web服务相关配置
  - 🌐 **端口设置**: 可配置的服务端口
  - 🎨 **界面配置**: Web界面样式和布局配置
  - 🔄 **热重载**: 支持配置热重载

---

## 📁 项目结构树

```
📦 GlobalShop/
├── 📂 src/main/java/cn/i7mc/globalshop/
│   ├── 🏠 GlobalShop.java                    # 插件主类
│   ├── 📝 commands/
│   │   └── AuctionCommand.java               # 拍卖命令处理器
│   ├── ⚙️ config/
│   │   ├── ConfigManager.java                # 主配置管理器
│   │   ├── MessageManager.java               # 消息管理器
│   │   └── DebugMessageManager.java          # 调试消息管理器
│   ├── 🗄️ database/
│   │   └── DatabaseManager.java              # 数据库管理器
│   ├── 💰 economy/
│   │   └── EconomyManager.java               # 经济管理器
│   ├── 📋 enums/
│   │   └── SortType.java                     # 排序类型枚举
│   ├── 🖥️ gui/
│   │   └── GuiManager.java                   # GUI管理器
│   ├── 🌟 hologram/
│   │   ├── HologramDisplayManager.java       # 全息显示管理器
│   │   ├── ItemDisplayManager.java           # 物品显示管理器
│   │   ├── TextDisplayManager.java           # 文本显示管理器
│   │   ├── AuctionHistoryManager.java        # 拍卖历史管理器
│   │   ├── HologramConfigManager.java        # 全息配置管理器
│   │   ├── HologramCommandManager.java       # 全息命令管理器
│   │   └── HologramUpdateTask.java           # 全息更新任务
│   ├── 👂 listeners/
│   │   └── GuiListener.java                  # GUI事件监听器
│   ├── 📊 metrics/
│   │   └── Metrics.java                      # bStats统计
│   ├── 📦 models/
│   │   └── AuctionItem.java                  # 拍卖物品模型
│   ├── ⏰ tasks/
│   │   ├── AuctionTask.java                  # 拍卖定时任务
│   │   ├── CheckAllAuctionsTask.java         # 拍卖状态检查任务
│   │   └── CloseAllAuctionsTask.java         # 强制关闭任务
│   ├── 🛠️ utils/
│   │   ├── BroadcastManager.java             # 广播管理器
│   │   ├── ChatUtils.java                    # 聊天工具类
│   │   ├── MinecraftLanguageManager.java     # 语言管理器
│   │   ├── SearchHistoryManager.java         # 搜索历史管理器
│   │   └── SortManager.java                  # 排序管理器
│   └── 🌐 web/
│       ├── WebServer.java                    # Web服务器
│       ├── WebController.java                # Web控制器
│       ├── WebDataProvider.java              # Web数据提供者
│       └── WebConfig.java                    # Web配置管理
│
├── 📂 src/main/resources/
│   ├── 📄 plugin.yml                         # 插件描述文件
│   ├── ⚙️ config.yml                         # 主配置文件
│   ├── 💬 message.yml                        # 中文消息配置
│   ├── 🐛 debugmessage.yml                   # 调试消息配置
│   ├── 🌟 hd_gui.yml                         # 全息GUI配置
│   ├── 🌍 minecraft_lang.yml                 # 物品名称翻译
│   ├── 🌐 lang/                              # 多语言目录
│   │   ├── message_en.yml                    # 英语
│   │   ├── message_es.yml                    # 西班牙语
│   │   ├── message_de.yml                    # 德语
│   │   ├── message_fr.yml                    # 法语
│   │   ├── message_ja.yml                    # 日语
│   │   ├── message_ru.yml                    # 俄语
│   │   └── message_vi.yml                    # 越南语
│   └── 🌐 web/                               # Web界面资源
│       ├── index.html                        # Web主页
│       ├── script.js                         # JavaScript脚本
│       └── style.css                         # CSS样式表
│
└── 📂 其他文件/
    ├── build.gradle                          # Gradle构建文件
    ├── settings.gradle                        # Gradle设置
    ├── version.properties                     # 版本配置
    └── gradle.properties                      # Gradle属性
```

## 📊 项目统计

### 📈 代码统计
| 模块 | 文件数 | 主要功能 |
|------|--------|----------|
| 🏠 核心类 | 1 | 插件主类和初始化 |
| 📝 命令系统 | 1 | 命令处理和Tab补全 |
| ⚙️ 配置管理 | 3 | 配置文件管理和多语言 |
| 🗄️ 数据库 | 1 | 双数据库支持和数据管理 |
| 💰 经济系统 | 1 | 双货币系统和手续费 |
| 📋 枚举类型 | 1 | 排序类型定义 |
| 🖥️ GUI界面 | 1 | 完整的图形界面系统 |
| 🌟 全息显示 | 7 | 3D全息显示和管理 |
| 👂 事件监听 | 1 | GUI交互事件处理 |
| 📊 统计模块 | 1 | bStats数据统计 |
| 📦 数据模型 | 1 | 拍卖物品数据模型 |
| ⏰ 定时任务 | 3 | 拍卖检查和管理任务 |
| 🛠️ 工具类 | 5 | 广播、搜索、语言等工具 |
| 🌐 Web服务 | 4 | Web界面和API服务 |

### 📋 文件统计
- **Java类文件**: 30个
- **配置文件**: 12个 (含多语言)
- **Web文件**: 3个
- **构建文件**: 4个
- **总计文件**: 49个

### 🌍 多语言支持
- 🇨🇳 中文 (默认)
- 🇺🇸 英语
- 🇪🇸 西班牙语
- 🇩🇪 德语
- 🇫🇷 法语
- 🇯🇵 日语
- 🇷🇺 俄语
- 🇻🇳 越南语

---

## 📝 命令系统

### 👤 用户命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction help` | 📋 显示帮助信息 | `globalshop.use` |
| `/auction open` | 🏪 打开拍卖行主界面 | `globalshop.use` |
| `/auction sell <起拍价> [一口价] [货币类型]` | 💰 上架手中物品 (1=金币, 2=点券) | `globalshop.sell` |
| `/auction search <关键词>` | 🔍 搜索物品 | `globalshop.use` |
| `/auction my` | 📊 查看我的拍卖 | `globalshop.use` |
| `/auction collect` | 📬 领取待领取物品 | `globalshop.use` |

### 👑 管理员命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction reload` | 🔄 重新加载所有配置文件 | `globalshop.admin` |
| `/auction close` | 🚫 强制关闭所有拍卖 | `globalshop.admin` |
| `/auction checkexpired` | ⏰ 手动检查过期物品 | `globalshop.admin` |
| `/auction info <玩家>` | 📊 查询玩家拍卖信息 | `globalshop.admin` |

### 🌟 全息显示命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/auction hud create <名称>` | ✨ 在当前位置创建全息拍卖行 | `globalshop.admin` |
| `/auction hud remove <名称>` | 🗑️ 移除指定名称的全息拍卖行 | `globalshop.admin` |
| `/auction hud list` | 📋 列出所有全息拍卖行 | `globalshop.admin` |
| `/auction hud reload` | 🔄 重新加载全息拍卖行配置 | `globalshop.admin` |

### 💡 命令示例
```bash
# 上架物品示例
/auction sell 100          # 起拍价100金币
/auction sell 100 500      # 起拍价100，一口价500金币
/auction sell 100 500 2    # 起拍价100，一口价500点券

# 搜索示例
/auction search 钻石        # 搜索包含"钻石"的物品
/auction search 剑          # 搜索包含"剑"的物品

# 全息显示示例
/auction hud create 主城拍卖行    # 创建名为"主城拍卖行"的全息显示
/auction hud remove 主城拍卖行    # 移除该全息显示
```

---

## 🔐 权限系统

### 📋 权限节点
| 权限节点 | 描述 | 默认 |
|----------|------|------|
| `globalshop.use` | 🏪 基础使用权限 (打开界面、搜索等) | `true` |
| `globalshop.sell` | 💰 上架物品权限 | `true` |
| `globalshop.buy` | 🛒 购买物品权限 | `true` |
| `globalshop.admin` | 👑 管理员权限 (重载、强制关闭等) | `op` |
| `globalshop.hologram` | 🌟 全息显示管理权限 | `op` |

### 🎯 权限组建议
```yaml
# 普通玩家组
default:
  permissions:
    - globalshop.use
    - globalshop.sell
    - globalshop.buy

# VIP玩家组 (可能有更高上架限制)
vip:
  permissions:
    - globalshop.use
    - globalshop.sell
    - globalshop.buy

# 管理员组
admin:
  permissions:
    - globalshop.*
```

---

## ⚙️ 配置系统

### 🗄️ 数据库配置
```yaml
database:
  type: "sqlite"              # 数据库类型: sqlite/mysql
  mysql:
    host: "localhost"         # MySQL主机地址
    port: 3306               # MySQL端口
    database: "globalshop"    # 数据库名称
    username: "root"          # 用户名
    password: "password"      # 密码
    table_prefix: "gs_"       # 表前缀，避免冲突
```

### 💰 经济系统配置
```yaml
economy:
  vault:
    currency_name: "金币"     # 货币名称
    currency_symbol: "¥"      # 货币符号
    buyer_fee_rate: 0.05      # 买家手续费率 (5%)
    seller_fee_rate: 0.03     # 卖家手续费率 (3%)
    min_fee: 1.0             # 最低手续费

  playerpoints:
    currency_name: "点券"     # 点券名称
    currency_symbol: "P"      # 点券符号
    buyer_fee_rate: 0.02      # 买家手续费率 (2%)
    seller_fee_rate: 0.01     # 卖家手续费率 (1%)
    min_fee: 1               # 最低手续费

  max_price_digits: 10       # 价格最大位数限制
```

### 🏪 拍卖系统配置
```yaml
auction:
  default_duration: 86400    # 默认拍卖时长 (秒)
  min_duration: 3600         # 最短拍卖时长 (1小时)
  max_duration: 604800       # 最长拍卖时长 (7天)
  max_auctions_per_player: 10 # 每个玩家最大上架数量
  check_interval: 60         # 拍卖检查间隔 (秒)

  bidding:
    min_increment_rate: 0.05  # 最低加价比例 (5%)
    min_increment_amount: 1   # 最低加价金额
```

### 🖥️ GUI界面配置
```yaml
gui:
  main_menu:
    title: "§6§l全球拍卖行"    # 主界面标题
    size: 54                  # 界面大小
    items_per_page: 45        # 每页显示物品数量

  search_menu:
    title: "§e§l搜索物品"     # 搜索界面标题
    history_limit: 10         # 搜索历史限制

  sort:
    default_type: "TIME_DESC" # 默认排序: TIME_ASC/TIME_DESC/PRICE_ASC/PRICE_DESC/NAME_ASC/NAME_DESC
```

### 📢 广播系统配置
```yaml
broadcast:
  enabled: true               # 广播总开关

  events:
    item_listed: true         # 物品上架广播
    item_sold: true           # 物品售出广播
    bid_placed: true          # 竞价广播
    bid_confirmed: true       # 竞价确认广播

  locations:
    chat: true                # 聊天框广播
    bossbar: false            # Boss栏广播
    title: false              # 标题广播
    subtitle: false           # 副标题广播
    actionbar: false          # 动作栏广播

  bossbar:
    color: "YELLOW"           # Boss栏颜色
    style: "SOLID"            # Boss栏样式
    duration: 5               # 显示时长 (秒)

  title:
    fade_in: 10               # 淡入时间 (tick)
    stay: 40                  # 停留时间 (tick)
    fade_out: 10              # 淡出时间 (tick)
```

### 🌟 全息显示配置
```yaml
hologram:
  enabled: true               # 全息显示总开关
  update_interval: 20         # 更新间隔 (tick)
  max_display_items: 5        # 最大显示物品数量

  item_display:
    rotation_speed: 2.0       # 物品旋转速度
    bounce_height: 0.3        # 物品弹跳高度
    scale: 1.0               # 物品缩放比例

  text_display:
    line_height: 0.3          # 文本行高
    background: true          # 文本背景
    shadow: true              # 文本阴影
```

### 🌐 Web服务配置
```yaml
web:
  enabled: true               # Web服务开关
  port: 8080                 # 服务端口
  refresh_interval: 30       # 刷新间隔 (秒)
  max_items_display: 50      # 最大显示物品数量

  security:
    allowed_ips: []           # 允许访问的IP (空=全部允许)
    rate_limit: 100           # 请求频率限制 (每分钟)
```

### 🌍 多语言配置
```yaml
language:
  default: "zh_CN"            # 默认语言
  auto_detect: true           # 自动检测客户端语言

  supported:                  # 支持的语言列表
    - "zh_CN"                 # 中文
    - "en_US"                 # 英语
    - "es_ES"                 # 西班牙语
    - "de_DE"                 # 德语
    - "fr_FR"                 # 法语
    - "ja_JP"                 # 日语
    - "ru_RU"                 # 俄语
    - "vi_VN"                 # 越南语
```

### 🐛 调试配置
```yaml
debug:
  enabled: false              # 调试模式开关
  log_level: "INFO"          # 日志级别: DEBUG/INFO/WARN/ERROR
  log_database: false        # 记录数据库操作
  log_economy: false         # 记录经济操作
  log_gui: false             # 记录GUI操作
```

---

## 📈 更新日志

### 🎯 核心功能完善

#### 🌍 多语言支持系统 `v1.4.0.12`
- ✅ **完整的8种语言支持**: 中文、英语、西班牙语、德语、法语、日语、俄语、越南语
- ✅ **智能语言检测**: 自动检测客户端语言并切换对应界面
- ✅ **消息国际化**: 所有GUI文本、提示消息、错误信息完整翻译
- ✅ **拍卖任务消息**: auction_task部分完整多语言支持
- ✅ **动态切换**: 支持运行时语言切换，无需重启

#### 📢 广播系统优化 `v1.4.0.12`
- ✅ **物品悬停信息**: 完整保留渐变色效果的物品信息显示
- ✅ **多位置广播**: 聊天框、Boss栏、标题、副标题、动作栏
- ✅ **消息自定义**: 所有广播消息支持完全自定义配置
- ✅ **数量显示修复**: 修复物品数量重复显示问题
- ✅ **渐变色兼容**: 完美支持带有渐变色代码的物品

#### 💰 经济系统增强 `v1.4.0.12`
- ✅ **价格限制系统**: 可配置的价格最大位数限制
- ✅ **PlayerPoints软依赖**: 提高插件兼容性
- ✅ **手续费优化**: 完善的买家卖家手续费计算
- ✅ **双货币支持**: Vault金币 + PlayerPoints点券

#### 🖥️ GUI界面优化 `v1.4.0.12`
- ✅ **自定义上架时间**: 钟表按钮支持灵活时间设置
- ✅ **翻页系统优化**: 修复翻页按钮无响应问题
- ✅ **竞价系统修复**: 解决竞价界面各种异常问题
- ✅ **已售出界面**: 完善的销售统计和收益显示

#### 👑 管理员功能 `v1.4.0.11`
- ✅ **强制下架功能**: 管理员可强制下架任何物品
- ✅ **配置重载优化**: 完善的配置热重载功能
- ✅ **权限系统**: 细粒度的权限控制
- ✅ **调试系统**: 完整的调试信息和错误追踪

### 🌟 高级功能

#### 🌟 全息显示系统 `v1.4.0.10`
- ✅ **3D物品展示**: 真实的3D物品旋转显示
- ✅ **动态文本**: 实时更新的拍卖信息
- ✅ **历史记录**: 拍卖历史记录显示
- ✅ **多点位支持**: 可创建多个全息拍卖行

#### 🌐 Web界面系统 `v1.4.0.9`
- ✅ **响应式设计**: 支持PC和移动端
- ✅ **实时数据**: 显示当前拍卖物品
- ✅ **安全控制**: 访问权限和安全防护
- ✅ **高性能**: 异步处理和缓存优化

#### 🗄️ 数据库系统 `v1.4.0.8`
- ✅ **双数据库支持**: SQLite（默认）+ MySQL
- ✅ **表前缀支持**: MySQL自定义表前缀避免冲突
- ✅ **数据完整性**: 完善的数据验证和错误处理
- ✅ **性能优化**: 异步数据库操作和连接管理

### 🔄 当前开发进度

#### ✅ 已完成功能
- 🏪 完整拍卖系统 (起拍价、一口价、竞价)
- 💰 双货币系统 (Vault + PlayerPoints)
- 🌟 全息显示系统
- 🌐 Web界面系统
- 🌍 多语言支持 (8种语言)
- 📢 智能广播系统
- 🔍 强大搜索功能
- 📊 数据统计系统
- 👑 管理员工具
- 📬 物品邮箱系统

#### 🔄 正在开发
- 🎨 界面美观度优化
- ⚡ 性能提升优化
- 🔍 更多搜索筛选选项
- 📊 高级数据分析

#### 📋 计划功能
- 🔍 按价格区间搜索
- 📈 按时间/价格排序优化
- 🎯 个性化推荐系统
- 📱 移动端专用界面
- 🔔 拍卖提醒系统
- 📊 详细交易统计

---

## 🚀 安装与使用

### 📋 系统要求
- **Minecraft版本**: 1.20.1 - 1.21.5
- **服务端**: Paper (推荐) / Spigot / Bukkit
- **Java版本**: Java 17+
- **依赖插件**: Vault (必需), PlayerPoints (可选)

### 📥 安装步骤
1. 下载最新版本的GlobalShop插件
2. 将插件文件放入服务器的`plugins`目录
3. 安装Vault插件和经济插件 (如EssentialsX)
4. 可选安装PlayerPoints插件以支持点券系统
5. 重启服务器
6. 根据需要修改配置文件

### ⚙️ 快速配置
1. 编辑`plugins/GlobalShop/config.yml`配置数据库和经济设置
2. 编辑`plugins/GlobalShop/message.yml`自定义界面文本
3. 使用`/auction reload`重载配置
4. 使用`/auction open`测试拍卖行功能

### 🎯 使用指南
1. **上架物品**: 手持物品使用`/auction sell <价格>`
2. **搜索物品**: 使用`/auction search <关键词>`
3. **查看拍卖**: 使用`/auction open`打开主界面
4. **管理拍卖**: 使用`/auction my`查看个人拍卖
5. **领取物品**: 使用`/auction collect`领取待领取物品

---

## 🤝 支持与反馈

### 📞 联系方式
- **开发者**: Saga (linghun91)
- **GitHub**: [GlobalShop Repository](https://github.com/linghun91/GlobalShop)
- **邮箱**: linghun91@163.com

### 🐛 问题反馈
如果您在使用过程中遇到任何问题，请通过以下方式反馈：
1. 在GitHub上提交Issue
2. 发送邮件详细描述问题
3. 提供服务器版本、插件版本等信息

### 💡 功能建议
欢迎提出新功能建议和改进意见：
- 通过GitHub Issues提交功能请求
- 详细描述期望的功能和使用场景
- 我们会认真考虑每一个建议

### 🌟 贡献代码
欢迎开发者参与项目贡献：
1. Fork项目到您的GitHub
2. 创建功能分支进行开发
3. 提交Pull Request
4. 等待代码审查和合并

---

## 📄 许可证

本项目采用 **MIT License** 开源许可证。

```
MIT License

Copyright (c) 2024 Saga (linghun91)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

**🎉 感谢使用 GlobalShop！**

*如果这个项目对您有帮助，请给我们一个 ⭐ Star！*

[![GitHub stars](https://img.shields.io/github/stars/linghun91/GlobalShop.svg?style=social&label=Star)](https://github.com/linghun91/GlobalShop)
[![GitHub forks](https://img.shields.io/github/forks/linghun91/GlobalShop.svg?style=social&label=Fork)](https://github.com/linghun91/GlobalShop)

</div>
