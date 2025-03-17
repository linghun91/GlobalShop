# GlobalShop - 全局拍卖行插件

## 项目概述
GlobalShop是一个基于Spigot 1.21.4开发的Minecraft服务器拍卖行插件，提供类似魔兽世界拍卖行的功能。该插件支持自定义物品的拍卖、购买和搜索功能。

## 最近更新
1. GUI翻页系统优化（2023.11）
   - 修复了主拍卖界面下一页按钮无响应问题
   - 优化了翻页逻辑，正确保存和传递页码元数据
   - 调整每页显示物品数量从42个增加到45个
   - 完善了当物品填满时的翻页功能，确保可以无限翻页

## 当前项目进度

### 已完成功能
1. 基础框架搭建
   - [x] 插件主类 (GlobalShop.java)
   - [x] 配置管理 (ConfigManager.java)
   - [x] 数据库管理 (DatabaseManager.java)
   - [x] 经济系统集成 (EconomyManager.java)
   - [x] GUI框架 (GuiManager.java)
   - [x] 命令系统 (AuctionCommand.java)
   - [x] 事件监听 (GuiListener.java)
   - [x] 物品模型 (AuctionItem.java)

2. 数据库系统
   - [x] SQLite数据库连接
   - [x] 表结构设计
   - [x] 物品序列化/反序列化
   - [x] CRUD操作实现
   - [x] 扩展字段支持
     - [x] 添加买家名称字段
     - [x] 添加上架时间字段
     - [x] 添加售出时间字段
   - [x] 自动检测和添加新字段

3. 经济系统
   - [x] Vault经济集成
   - [x] PlayerPoints点券集成
   - [x] 双货币支持
     - [x] 上架时选择货币类型
     - [x] 物品展示中显示货币类型
     - [x] 交易过程保持货币类型一致
   - [x] 手续费计算
   - [x] 最低竞价计算

4. GUI系统
   - [x] 主界面框架
   - [x] 搜索界面框架
   - [x] 上架界面框架
   - [x] 物品显示系统
     - [x] 显示上架时间
     - [x] 显示物品详细信息
     - [x] 优化主界面布局
   - [x] 确认购买界面
   - [x] 竞价界面
   - [x] 竞价金额输入界面
   - [x] 分页系统完善
     - [x] 优化翻页逻辑，修复下一页按钮无响应问题
     - [x] 调整每页显示物品数量从42个增加到45个
     - [x] 优化页面元数据保存机制，确保准确传递页码
     - [x] 支持物品填满时继续翻页功能
   - [x] 我的拍卖界面
     - [x] 优化UI布局，移除冗余的"活跃拍卖"按钮
     - [x] 调整"已售出"和"已过期"按钮位置
     - [x] 显示玩家当前上架数量和最大上架限制
     - [x] 接近上架上限时提供警告提示
   - [x] 已售出拍卖界面
     - [x] 显示买家名称
     - [x] 显示实际售出时间
   - [x] 已过期拍卖界面
   - [x] 物品操作简化
     - [x] 简化物品描述，移除冗余操作提示
     - [x] 左键直接参与竞价，右键直接快速购买

5. 拍卖功能
   - [x] 上架物品逻辑
     - [x] 上架数量限制功能
     - [x] 基于配置的最大上架数量控制
     - [x] 超出限制时友好提示
   - [x] 快速购买逻辑
   - [x] 竞价系统
     - [x] 竞价界面加价功能优化
     - [x] 竞价金额直接在当前界面更新，避免关闭/重开界面
   - [x] 拍卖结束处理
     - [x] 定时检查过期拍卖
     - [x] 处理成功竞拍（给予买家物品，给予卖家钱）
     - [x] 处理未售出物品（返还给卖家）
     - [x] 处理离线或背包已满玩家的物品
   - [x] 物品主人功能
     - [x] 识别玩家是否为物品主人
     - [x] 禁止玩家购买或竞价自己的物品
     - [x] 物品主人可通过Shift+右键快速下架物品
   - [x] 交易记录优化
     - [x] 记录买家名称
     - [x] 记录准确的售出时间
     - [x] 交易日志完善

6. 搜索系统
   - [x] 物品名称搜索（模糊匹配）
     - [x] 支持带颜色代码（&3等）的物品名称搜索
     - [x] 采用内存过滤代替SQL查询，提高搜索准确性
   - [x] 搜索结果分页
     - [x] 优化界面布局，顶部信息栏+中部物品展示+底部导航栏
     - [x] 每页显示36个物品，布局更加合理
   - [x] 搜索历史记录
   - [x] 聊天框搜索输入修复
     - [x] 正确处理聊天框输入的搜索关键词
     - [x] 支持取消搜索操作
   - [x] 中文原版物品名称搜索
     - [x] 支持使用中文名称搜索原版物品（如"红石"、"钻石"等）
     - [x] 通过minecraft_lang.yml翻译表实现中英文物品名称映射
     - [x] 简单模糊匹配算法，提高搜索准确性
     - [x] 适配大部分原版物品，无需手动配置即可使用

7. 我的拍卖
   - [x] 查看我的拍卖列表
   - [x] 管理我的拍卖
     - [x] 查看我的当前拍卖
     - [x] 查看我的已售出拍卖
     - [x] 查看我的已过期拍卖
     - [x] 取消未收到竞价的拍卖
   - [x] 上架限制
     - [x] 每个玩家的最大上架数量控制
     - [x] 超出上架限制时的友好提示
     - [x] 在我的拍卖界面显示当前上架数量和上限

8. 物品领取系统
   - [x] 离线和背包已满处理
   - [x] 领取待领取物品
   - [x] 待领取物品存储
   - [x] 待领取物品管理

9. 命令系统优化
   - [x] 命令结构重组
     - [x] `/auction`显示帮助信息
     - [x] `/auction help`显示帮助信息
     - [x] `/auction open`打开拍卖行界面
   - [x] 命令参数自动补全
     - [x] 子命令自动补全
     - [x] 参数类型提示
     - [x] 货币类型选项补全
   - [x] 参数验证与提示优化
     - [x] 上架时必须指定货币类型的提示
     - [x] 参数格式和有效性验证
   - [x] 配置热重载
     - [x] `/auction reload`命令支持
     - [x] 无需重启即可应用配置变更
     - [x] 权限控制（仅管理员可用）
   - [x] 管理员命令系统
     - [x] `/auction close`命令支持
     - [x] 强制关闭所有拍卖
     - [x] `/auction checkexpired`命令支持
     - [x] 手动检查过期物品
     - [x] 权限控制（仅管理员可用）
     - [x] 完整指令帮助文档

10. 系统安全和限制
    - [x] 物品邮箱系统
      - [x] 将过期物品放入物品邮箱
      - [x] 实时检查背包空间防止物品丢失
      - [x] 竞拍成功/购买成功但背包已满时自动放入邮箱
    - [x] 玩家上架限制
      - [x] 基于配置的最大上架数量限制
      - [x] 上架前检查玩家当前上架数量
      - [x] 超出限制时的友好提示和操作阻止
      - [x] GUI和命令双重限制保障

### 待实现功能
1. 优化和完善
   - [ ] 界面美化
   - [ ] 性能优化
   - [ ] 数据备份
   - [ ] 错误处理

2. 更多搜索和筛选选项
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
    - 显示当前上架数量和最大上限
    - 接近上限时提供警告提示
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
    - handleSellMenuClick - 处理物品出售界面点击（包含上架限制检查）
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
    - handleSellCommand - 处理上架命令（包含上架限制检查）
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
  - 数据结构
    - Map<UUID, List<String>> searchHistory - 存储每个玩家的搜索历史
  - 方法
    - addSearchHistory - 添加搜索历史
    - getSearchHistory - 获取玩家的搜索历史
    - clearSearchHistory - 清除玩家的搜索历史
    - clearAllSearchHistory - 清除所有搜索历史
- `MinecraftLanguageManager.java` - Minecraft语言管理类
  - 数据结构
    - Map<String, String> enToZhMap - 英文ID到中文名称的映射
    - Map<String, String> zhToEnMap - 中文名称到英文ID的映射
  - 方法
    - loadLanguageFile - 加载minecraft_lang.yml翻译文件
    - getChineseName - 获取物品的中文名称
    - findPossibleItemIds - 根据中文关键词查找可能的物品ID
    - canMatchChineseKeyword - 检查中文关键词是否能匹配到任何物品

### 任务类
- `AuctionTask.java` - 拍卖定时任务类
  - 继承自BukkitRunnable
  - 方法
    - run - 定期检查过期拍卖
    - handleExpiredAuctions - 处理过期拍卖
    - handleSuccessfulAuction - 处理成功竞拍
    - handleFailedAuction - 处理未售出物品
    - storePendingItem - 存储待领取物品
- `CheckAllAuctionsTask.java` - 检查所有拍卖任务类
  - 用于管理员手动检查所有拍卖物品
  - 查找并处理过期物品

### 项目类结构树

```
cn.i7mc.globalshop/
├── GlobalShop.java (主插件类，初始化所有组件)
├── commands/
│   └── AuctionCommand.java (命令处理器，处理所有拍卖相关命令)
├── config/
│   └── ConfigManager.java (配置管理器，处理配置文件加载和访问)
├── database/
│   └── DatabaseManager.java (数据库管理器，处理数据存储和读取)
├── economy/
│   └── EconomyManager.java (经济系统管理器，处理货币和交易)
├── gui/
│   └── GuiManager.java (GUI管理器，创建和显示各种界面)
├── listeners/
│   └── GuiListener.java (事件监听器，处理GUI交互事件)
├── models/
│   └── AuctionItem.java (拍卖物品模型，存储物品和拍卖信息)
├── tasks/
│   ├── AuctionTask.java (拍卖检查任务，定期处理过期物品)
│   ├── CheckAllAuctionsTask.java (手动检查过期物品任务)
│   └── CloseAllAuctionsTask.java (关闭所有拍卖任务，管理员使用)
└── utils/
    ├── ChatUtils.java (聊天工具类，处理消息和文本格式化)
    ├── MinecraftLanguageManager.java (语言管理器，处理中英文物品名称映射)
    └── SearchHistoryManager.java (搜索历史管理器，存储和获取搜索记录)
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
  - 使用minecraft_lang.yml翻译表进行中英文转换
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

## 开发计划
1. 第一阶段：基础框架搭建
   - 插件基础结构
   - 数据库设计
   - GUI框架

2. 第二阶段：核心功能开发
   - 物品系统
   - 拍卖功能
   - 搜索系统
   - 上架限制实现

3. 第三阶段：优化和完善
   - 性能优化
   - 界面美化
   - 功能测试
   - 安全性加强

4. 第四阶段：发布和维护
   - 文档编写
   - 问题修复
   - 功能更新

## 注意事项
1. 所有物品数据必须完整保存，包括NBT数据
2. 搜索功能仅支持Display名称的模糊匹配
3. 不进行任何硬编码的物品映射
4. 确保与自定义物品插件的兼容性
5. 保证数据安全性和交易可靠性
6. 防止玩家通过上架大量物品影响服务器性能

## 后续扩展计划
1. 支持批量上架
2. 支持拍卖历史记录
3. 支持物品分类系统
4. 支持拍卖推荐系统
5. 支持拍卖数据统计
6. 动态调整上架限制（基于玩家等级、权限组等）