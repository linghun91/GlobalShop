# GlobalShop - 全局拍卖行插件

## 项目概述
GlobalShop是一个基于Spigot 1.21.4开发的Minecraft服务器拍卖行插件，提供类似魔兽世界拍卖行的功能。该插件支持自定义物品的拍卖、购买和搜索功能。

## 项目状态
当前项目已实现基础架构和主要功能，包括完整的拍卖系统、搜索功能、多语言支持和广播系统。最近完成了广播系统物品悬停信息的显示优化，现在可完整保留所有渐变色效果。下一步计划优化界面美观度、提升性能并添加更多搜索和筛选选项。

## 最近更新
1. 竞价确认广播事件多语言支持优化
   - 修复了竞价确认广播事件中物品名称未经过语言判断的问题
   - 统一了所有广播事件的物品名称获取逻辑，确保一致的用户体验
   - 完善了中文语言环境下原版物品名称的显示（使用MinecraftLanguageManager）
   - 优化了物品数量显示格式，与其他广播事件保持一致
   - 增强了广播消息的多语言兼容性，确保在所有语言环境下正确显示物品名称

2. 管理员强制下架权限优化
   - 修复了管理员无法强制下架自己物品的问题
   - 移除了强制下架操作中对物品所有者的检查限制
   - 现在管理员可以使用Shift+左键强制下架任何物品，包括自己上架的物品
   - 优化了管理员操作权限的一致性，保证管理员可以执行所有操作

3. 语言文件系统完善
   - 添加了完整的多语言支持系统，新增语言文件
   - 已完成的语言支持：英语(en)、西班牙语(es)
   - 所有语言文件均位于`src/main/resources/lang/`目录下
   - 每个语言文件包含所有GUI文本、消息提示、错误信息等内容
   - 支持中文、英文和西班牙语的完整消息配置
   - 确保了auction_task部分的多语言支持，移除了硬编码信息

4. AuctionTask拍卖任务硬编码消息重构
   - 将AuctionTask.java中的所有硬编码消息迁移至message.yml配置文件
   - 在message.yml中新增auction_task部分，包含买家通知、卖家通知和拍卖过期通知等消息
   - 在MessageManager中添加对应的消息获取方法，支持动态占位符替换
   - 重构通知买家和卖家的消息发送代码，使用MessageManager替代硬编码字符串
   - 遵循项目统一规范，避免硬编码字符串，提高代码可维护性和灵活性

5. 自定义上架时间功能
   - 新增上架界面中的钟表按钮，允许玩家自定义拍卖持续时间
   - 支持多种操作方式：左键+1分钟，右键+1小时，Shift+左键+10分钟，Shift+右键+10小时
   - 中键点击可快速重置为最小上架时间
   - 自动限制时间范围在config.yml设置的最小和最大限制内
   - 上架成功后显示确认信息，提示已设置的持续时间
   - 优化UI，按钮提示明确且详细，使用户操作直观简单

6. 价格输入限制系统
   - 添加了价格最大位数限制功能，防止玩家上架超高价格物品
   - 在config.yml中添加`economy.max_price_digits`配置项，可自定义价格最大位数
   - 使用可靠的数学方法正确计算整数位数，而非字符串分析
   - 添加详细的调试输出，方便服务器管理员排查问题
   - 超出限制时提供友好错误提示，并支持自定义错误信息

7. 已售出拍卖界面优化
   - 将"我的已售出拍卖"界面的金块按钮更改为金锭按钮，更符合直觉
   - 添加总计售出物品数量统计显示，提供更全面的销售信息
   - 改进总收益计算逻辑，统计所有页面的售出物品收益
   - 完善LORE显示格式，同时展示总计售出数量、总计金币和总计点券收益
   - 将所有界面文本移至message.yml配置文件，支持完全自定义

8. 广播系统悬停详细信息优化
   - 修复了聊天框中"[详细信息]"显示内容不完整的问题
   - 确保聊天框中的物品信息与拍卖行主界面完全一致
   - 修复了物品ID不一致的问题（详细信息显示的ID与实际ID不匹配）
   - 确保显示完整的物品名称、原版LORE和所有拍卖信息
   - 添加了错误处理机制，确保即使生成悬停文本失败也能显示基础信息
   - 优化了代码复用，避免重复造轮子

9. 配置重载功能完善
   - 全面优化了`/auction reload`命令处理逻辑
   - 修复了广播配置变更后不立即生效的问题
   - 添加对BroadcastManager和GuiManager的配置重载支持
   - 增加调试输出，帮助管理员确认所有配置项是否正确重载
   - 确保check_interval等关键配置项修改后立即应用

10. 已售出历史物品界面优化
    - 修复了一口价确认购买时已售出历史物品显示起拍价而非实际成交价的问题
    - 修复了已售出历史物品显示硬编码结束时间而非实际售出时间的问题
    - 确保所有购买途径（直接一口价、确认界面一口价、竞价达到一口价）都正确设置成交价格
    - 优化了售出时间显示逻辑，不再回退使用拍卖结束时间
    - 提高了交易记录的准确性和用户体验

11. PlayerPoints软依赖支持
    - 将PlayerPoints从硬依赖改为软依赖，提高插件兼容性
    - 在PlayerPoints不可用时自动禁用点券功能
    - 完善命令和GUI界面，在PlayerPoints不可用时只显示金币选项
    - 对所有点券相关操作添加可用性检查，防止出错
    - 优化用户提示信息，清晰告知用户点券功能不可用的情况

12. 广播系统消息自定义优化
    - 将广播系统的硬编码消息转移到message.yml中，支持完全自定义
    - 支持5种不同位置的广播消息格式配置：聊天框、Boss栏、标题、副标题、动作栏
    - 每个广播事件（物品上架、竞拍成功、一口价购买）可独立配置消息格式
    - 消息配置支持动态reload，无需重启服务器即可生效
    - 优化占位符系统，增强广播消息的自定义灵活性

13. GUI竞价系统优化
    - 修复了竞价界面预加价按钮导致显示"已取消竞价"的错误提示问题
    - 修复了竞价界面点击空位置时出现的空指针异常问题
    - 优化了竞价过程中的元数据处理逻辑
    - 提高了竞价界面操作的稳定性和用户体验

14. GUI翻页系统优化
    - 修复了主拍卖界面下一页按钮无响应问题
    - 优化了翻页逻辑，正确保存和传递页码元数据
    - 调整每页显示物品数量从42个增加到45个
    - 完善了当物品填满时的翻页功能，确保可以无限翻页

15. 管理员强制下架功能修复
    - 修复了有人竞价的物品被管理员强制下架后不返回到卖家邮箱的问题
    - 改进了物品强制下架的状态处理逻辑，使用CANCELLED状态替代EXPIRED状态
    - 优化了竞价退款流程，确保正确退还竞价金额给竞价者
    - 添加了更详细的通知信息，通知卖家和竞价者物品已被管理员下架

16. 多语言支持系统
    - [x] 基础语言文件系统
      - [x] 英语(en)语言文件
      - [x] 西班牙语(es)语言文件
    - [x] 消息管理系统
      - [x] 从语言文件加载消息
      - [x] 支持切换语言
      - [x] 不同语言的完整UI支持
      - [x] 不同语言的完整消息提示支持
    - [x] 拍卖任务消息国际化
      - [x] auction_task部分的多语言支持
      - [x] 买家通知消息的多语言支持
      - [x] 卖家通知消息的多语言支持
      - [x] 拍卖过期通知的多语言支持

17. 广播系统物品数量显示修复
    - 修复了广播消息中物品数量重复显示的问题（如显示为"x6464"而不是"x64"）
    - 优化了BroadcastManager中的物品数量处理逻辑，防止重复添加数量信息
    - 新增独立的`%amount%`占位符，可在message.yml中自定义数量显示格式
    - 支持更灵活的数量显示样式，可自由设置颜色和位置
    - 保留对旧配置的兼容性，确保更新后不影响现有服务器配置
    - 通过配置文件完全控制物品数量显示，杜绝任何硬编码显示方式

18. 广播系统渐变色代码兼容优化
    - 修复了广播系统中"[详细信息]"悬停窗无法正确识别带有渐变色代码的物品颜色问题
    - 检测到物品LORE中包含"§x"渐变色代码时，自动将文本转换为白色显示
    - 确保所有物品LORE在悬停显示时都有正确颜色
    - 使用简洁的处理方式，避免复杂逻辑
    - 提高了带有渐变色物品在广播系统中的展示效果
    - 增强了插件对各类自定义物品的兼容性

19. 广播系统物品悬停信息完全优化
    - 彻底重构悬停显示功能，优化为使用原生Minecraft的HoverEvent机制
    - 修复之前临时方案中的渐变色物品被转成白色显示的问题，现可完整保留所有渐变色效果
    - 创建新的createItemInfoText方法，确保所有物品信息（名称、数量、LORE）格式完全保留
    - 简化broadcastMessage逻辑，更直接地创建悬浮组件，移除不必要的颜色转换代码
    - 保留原createHoverTextForEvent方法作为备用方案，确保向下兼容
    - 玩家现在可以在聊天框中看到与拍卖行主界面完全一致的物品信息，包括所有渐变色效果

## 当前项目进度
   - [ ] 按价格区间搜索
   - [ ] 按时间排序
   - [ ] 按价格排序

## 项目结构

### 核心类
- `GlobalShop.java` - 插件主类，负责初始化和管理其他组件
  - 初始化各个管理器（配置、数据库、经济、GUI等）
  - 注册命令和事件监听器
  - 启动定时任务（拍卖检查）
  - 提供访问各个管理器的方法

- `ConfigManager.java` - 配置管理器，负责加载和管理配置文件
  - 加载config.yml配置文件
  - 提供访问配置项的方法
  - 管理GUI标题、拍卖时长等设置
  - 支持配置热重载功能
  - 提供上架限制配置访问方法

- `MessageManager.java` - 消息管理类
  - 负责读取和保存message.yml，提供消息文本访问方法
  - 包含GUI按钮文本配置项，支持自定义界面文本

- `DebugMessageManager.java` - 调试消息管理类
  - 负责读取和保存debugmessage.yml，提供调试消息访问方法
  - 包含调试消息配置项，支持开发调试和错误追踪

- `DatabaseManager.java` - 数据库管理器，负责与数据库进行交互
  - 管理拍卖物品数据库
    - 创建和更新拍卖物品
    - 获取活跃、已过期、已售出拍卖物品
    - 查询和搜索拍卖物品
    - 统计玩家当前活跃拍卖数量
  - 管理待领取物品数据库
    - 存储待领取物品
    - 获取玩家待领取物品
    - 删除待领取物品

### 经济系统
- `EconomyManager.java` - 经济管理器，封装经济相关操作
  - 支持Vault和PlayerPoints双币种系统
    - 检查余额、扣款、充值
    - 格式化货币显示
  - 处理交易手续费计算
    - 计算买家手续费
    - 计算卖家手续费
  - 计算最低竞价金额
    - 根据当前价格计算最低加价

### GUI系统
- `GuiManager.java` - GUI管理器，负责创建和管理各种界面
  - 主菜单界面 (openMainMenu)
  - 搜索界面 (openSearchMenu)
  - 搜索结果界面 (openSearchResultMenu)
  - 物品出售界面 (openSellMenu)
  - 确认购买界面 (openConfirmBuyMenu)
  - 竞价界面 (openBidMenu)
  - 竞价金额输入界面 (openBidAmountMenu)
  - 我的拍卖界面 (openMyAuctionsMenu)
  - 已售出拍卖界面 (openMySoldAuctionsMenu)
  - 已过期拍卖界面 (openMyMailboxMenu)
  - 界面数据管理
    - 玩家页码跟踪 (playerPages)
    - 玩家搜索查询跟踪 (playerSearchQueries)

### 监听器
- `GuiListener.java` - GUI事件监听器，处理界面交互
  - 主要事件监听
    - onInventoryClick - 处理所有界面点击事件
    - onInventoryDrag - 处理物品拖拽
    - onInventoryClose - 处理界面关闭（保存上架物品）
    - onPlayerChat - 处理聊天输入（竞价金额、搜索关键词）
  - 界面交互处理
    - handleMainMenuClick - 处理主菜单点击
    - handleSearchMenuClick - 处理搜索界面点击
    - handleSearchResultClick - 处理搜索结果点击
    - handleSellMenuClick - 处理物品出售界面点击
    - handleSellMenuClose - 处理出售界面关闭
    - handleAuctionItemClick - 处理拍卖物品点击
    - handleConfirmBuyClick - 处理确认购买界面点击
    - handleBidMenuClick - 处理竞价界面点击
    - handleBidAmountMenuClick - 处理竞价金额输入界面点击
    - handleMyAuctionsMenuClick - 处理我的拍卖界面点击
    - handleCancelAuction - 处理取消拍卖操作

### 命令系统
- `AuctionCommand.java` - 拍卖命令处理类
  - 主命令处理 (onCommand)
  - 子命令处理
    - sendHelp - 显示帮助信息
    - handleSellCommand - 处理上架命令
    - handleBuyCommand - 处理购买命令
    - handleSearchCommand - 处理搜索命令
    - handleMyCommand - 处理我的拍卖命令
    - handleCollectCommand - 处理领取物品命令
    - handleReloadCommand - 处理重载配置命令
    - handleCloseCommand - 处理关闭所有拍卖命令
    - handleCheckExpiredCommand - 处理检查过期物品命令

### 模型类
- `AuctionItem.java` - 拍卖物品模型类
  - 属性
    - 基本信息（ID、卖家UUID、卖家名称、物品等）
    - 价格信息（起拍价、一口价、当前价格）
    - 时间信息（开始时间、结束时间）
    - 状态信息（活跃、已售出、已过期）
    - 竞价信息（当前出价者UUID）
  - 方法
    - 获取和设置属性的方法
    - 状态判断方法（isActive, isExpired, canBuyNow, canBid等）
    - 时间格式化方法（getFormattedRemainingTime）

### 工具类
- `ChatUtils.java` - 聊天和文本工具类
  - 提供物品名称获取方法
  - 提供时间格式化方法
- `SearchHistoryManager.java` - 搜索历史管理类
  - 存储每个玩家的搜索历史
  - 管理搜索历史的添加、获取和清除
- `MinecraftLanguageManager.java` - Minecraft语言管理类
  - 英文ID到中文名称的映射
  - 中文名称到英文ID的映射
  - 加载minecraft_lang.yml翻译文件
  - 根据中文关键词查找可能的物品ID
- `BroadcastManager.java` - 广播管理器
  - 管理不同事件类型的广播配置
  - 处理物品上架、竞拍成功、一口价购买等广播
  - 支持聊天框、Boss栏、标题、副标题、动作栏等显示位置

### 任务类
- `AuctionTask.java` - 拍卖定时任务类
  - 定期检查过期拍卖
  - 处理过期拍卖（成功竞拍、未售出等）
  - 存储待领取物品
- `CheckAllAuctionsTask.java` - 检查所有拍卖任务类
- `CloseAllAuctionsTask.java` - 关闭所有拍卖任务类

### 项目结构树

```
src/
└─main/
    ├─java/
    │  └─cn/
    │      └─i7mc/
    │          └─globalshop/                            # 插件主包
    │              │  GlobalShop.java                   # 插件主类
    │              │
    │              ├─commands/                          # 命令处理模块
    │              │      AuctionCommand.java           # 拍卖命令处理类
    │              │
    │              ├─config/                            # 配置管理模块
    │              │      ConfigManager.java            # 配置管理器
    │              │      DebugMessageManager.java      # 调试消息管理类
    │              │      MessageManager.java           # 消息管理类
    │              │
    │              ├─database/                          # 数据库模块
    │              │      DatabaseManager.java          # 数据库管理器
    │              │
    │              ├─economy/                           # 经济系统模块
    │              │      EconomyManager.java           # 经济管理器
    │              │
    │              ├─gui/                               # 界面系统模块
    │              │      GuiManager.java               # GUI管理器
    │              │
    │              ├─hologram/                          # 全息显示模块
    │              │      AuctionHistoryManager.java    # 拍卖历史管理器
    │              │      HologramCommandManager.java   # 全息命令管理器
    │              │      HologramConfigManager.java    # 全息配置管理器
    │              │      HologramDisplayManager.java   # 全息显示管理器
    │              │      HologramUpdateTask.java       # 全息更新任务
    │              │      ItemDisplayManager.java       # 物品显示管理器
    │              │      TextDisplayManager.java       # 文本显示管理器
    │              │
    │              ├─listeners/                         # 事件监听模块
    │              │      GuiListener.java              # GUI事件监听器
    │              │
    │              ├─models/                            # 数据模型模块
    │              │      AuctionItem.java              # 拍卖物品模型类
    │              │
    │              ├─tasks/                             # 定时任务模块
    │              │      AuctionTask.java              # 拍卖定时任务类
    │              │      CheckAllAuctionsTask.java     # 检查所有拍卖任务类
    │              │      CloseAllAuctionsTask.java     # 关闭所有拍卖任务类
    │              │
    │              └─utils/                             # 工具类模块
    │                     BroadcastManager.java         # 广播管理器
    │                     ChatUtils.java                # 聊天和文本工具类
    │                     MinecraftLanguageManager.java # Minecraft语言管理类
    │                     SearchHistoryManager.java     # 搜索历史管理类
    │
    └─resources/                                        # 资源文件目录
        │  config.yml                                   # 插件配置文件
        │  debugmessage.yml                             # 调试信息配置文件
        │  message.yml                                  # 中文界面文本和提示消息
        │  minecraft_lang.yml                           # 中英文物品名称对照表
        │  plugin.yml                                   # 插件描述文件
        │
        └─lang/                                         # 多语言文件目录
               message_en.yml                           # 英语语言文件
               message_es.yml                           # 西班牙语语言文件
```

## 核心功能需求

### 1. 物品系统
- 支持所有Minecraft物品类型
- 完全兼容自定义物品（自定义Display和Lore）
- 不进行任何硬编码的物品映射
- 保留物品的所有NBT数据

### 2. 拍卖功能
#### 2.1 上架物品
- 玩家可以通过命令上架物品
- 支持设置起拍价
- 支持设置一口价
- 支持设置拍卖时长（默认24小时）
- 上架时自动记录物品的所有属性
- 每个玩家有最大上架数量限制

#### 2.2 购买功能
- 支持一口价直接购买
- 支持竞价购买
- 竞价时自动更新当前最高价
- 拍卖结束自动结算

### 3. 搜索系统
- 支持通过物品Display名称搜索
- 搜索采用模糊匹配（包含关系）
- 示例：搜索"圣剑"可匹配"传说圣剑"、"神圣之剑"等
- 支持分页显示搜索结果
- 支持中文原版物品名称搜索
  - 可通过中文名称搜索原版物品（如"红石"搜索红石相关物品）
  - 使用minecraft_lang.yml翻译表进行中英文物品名称映射
  - 无需复杂配置即可支持大多数原版物品

### 4. 界面系统
- 使用GUI界面展示拍卖物品
  - 主界面设计
    - 54格大箱子界面（6行）
    - 顶部信息栏（显示当前页面、总页数）
    - 底部操作栏（上一页、下一页、搜索、上架按钮）
    - 物品展示区（6x7=42格）
  - 物品展示设计
    - 使用新的物品组件系统
      - 使用`ItemMeta.components`存储拍卖信息
      - 使用`ItemMeta.tags`存储自定义数据
      - 使用`ItemMeta.displayName`和`ItemMeta.lore`展示信息
    - 使用新的库存视图构建器
      - 使用`InventoryView.builder`创建界面
      - 支持链式调用设置界面属性
    - 使用新的物品元数据系统
      - 支持自定义物品属性
      - 支持物品状态管理
    - 界面数据管理
      - 使用`Map<Integer, AuctionItem>`存储槽位数据
      - 使用`InventoryView.getTopInventory()`管理界面
  - 交互设计
    - 使用新的事件系统
      - `InventoryClickEvent`处理点击
      - `InventoryDragEvent`处理拖拽
      - `InventoryCloseEvent`处理关闭
    - 左键点击：查看物品详情
    - 右键点击：快速购买
    - Shift+左键：查看卖家信息
    - 支持拖拽物品到上架区
  - 界面优化
    - 使用异步加载物品数据
    - 实现界面缓存机制
    - 支持界面动画效果
    - 适配不同分辨率
- 支持物品预览功能
  - 独立预览界面
  - 3D物品展示
  - 支持物品旋转查看
- 显示物品详细信息
  - 价格信息（当前价格、一口价）
  - 剩余时间
  - 卖家信息
  - 物品属性
- 支持快速购买按钮
  - 一口价快速购买
  - 竞价购买
  - 确认购买对话框

### 5. 经济系统
- 支持双货币系统
  - 经济系统（通过Vault.jar）
    - 支持所有Vault兼容的经济插件
    - 支持自定义货币名称
    - 支持货币符号显示
  - 点券系统（通过PlayerPoints.jar）
    - 支持点券余额查询
    - 支持点券交易
    - 支持点券符号显示
- 拍卖功能支持
  - 支持选择使用经济或点券
  - 支持设置货币类型
  - 支持货币转换（可选）
- 手续费系统
  - 支持两种货币独立手续费
  - 支持自定义手续费比例
  - 支持最低手续费设置

## 技术实现要点

### 1. 数据存储
- 使用SQLite数据库存储拍卖数据
  - 数据库文件位于plugins/GlobalShop/data.db
  - 自动创建和维护数据库结构
  - 支持数据库文件备份
- 物品数据序列化存储
  - 使用`ItemStack.serialize()`序列化物品
  - 使用`ItemStack.deserialize()`反序列化物品
  - 使用`ItemMeta.serialize()`序列化物品元数据
  - 使用`ItemMeta.deserialize()`反序列化物品元数据
  - 支持完整物品属性保存
- 支持数据备份和恢复
  - 提供数据库文件备份命令
  - 支持手动备份和恢复

### 2. 性能优化
- 使用缓存机制优化搜索性能
- 异步处理数据库操作
- 定期清理过期数据

### 3. 安全性
- 防止物品复制
- 交易验证机制
- 防作弊系统
- 上架数量限制

## 命令系统
```
/auction help - 显示帮助信息
/auction open - 打开拍卖行界面
/auction sell <起拍价> [一口价] [货币类型] - 上架物品 (1=金币, 2=点券)
/auction buy <物品ID> - 购买物品
/auction search <关键词> - 搜索物品
/auction my - 查看我的拍卖
/auction collect - 领取待领取物品
/auction reload - 重新加载配置文件（需要管理员权限）
/auction close - 强制关闭所有拍卖（仅管理员可用，测试用）
/auction checkexpired - 手动检查过期物品（仅管理员可用）
```

## 权限节点
```
globalshop.use - 基础使用权限
globalshop.sell - 上架权限
globalshop.buy - 购买权限
globalshop.admin - 管理员权限
```

## 配置项
- SQLite数据库配置（可选）
- 经济系统配置
  - Vault货币名称
  - Vault货币符号
  - Vault手续费设置
- 点券系统配置
  - 点券货币名称
  - 点券货币符号
  - 点券手续费设置
- 默认拍卖时长
- 每个玩家的最大上架数量限制
- GUI界面配置
- 消息提示配置
- 广播系统配置
  - 广播总开关
  - 事件广播设置（上架/竞拍/一口价）
  - 广播位置设置（聊天框/Boss栏/标题/副标题/动作栏）
  - Boss栏样式和持续时间
  - 标题显示时间
