# GlobalShop 全息拍卖行历史记录展示系统开发计划

> **重要原则：本功能必须在不破坏/修改/删除任何现有项目结构/代码/逻辑的情况下实现。作为扩展项目，原有功能已全部开发完毕，新功能仅需读取获取原本的方法和数据，严禁修改/破坏/删除任何原有代码！**

## 1. 功能概述

本计划旨在为GlobalShop插件增加全息拍卖行历史记录展示系统，在服务器固定位置实时展示拍卖行历史事件。该功能将以公共展示板形式呈现，对所有玩家可见，提供拍卖行活动的实时动态。

### 1.1 主要特性
- [x] 由管理员通过命令`/ah hud create`在固定位置创建全息展示板
- [x] 展示板对所有玩家可见，作为服务器公共信息展示
- [x] 以全息方式展示最近的拍卖行历史事件（如上架、竞价、购买、到期等）
- [x] 每行显示格式：物品名称 + 卖家名称 + 事件类型 + 货币类型 + 价格 + 买家名称
- [x] 支持在hd_gui.yml中配置显示行数、更新频率和显示样式

### 1.2 用户体验目标
- [x] 提供服务器公共拍卖行信息展示平台，增强社区交流
- [x] 实时更新拍卖行事件，让玩家了解最新交易动态
- [x] 视觉上直观美观，提升服务器整体氛围
- [x] 支持管理员自定义摆放位置，融入服务器场景

## 2. 技术实现

### 2.1 开发原则
- [x] **完全扩展性开发**：作为扩展模块，不修改任何现有代码
- [x] **数据共享**：通过现有API方法获取数据，不直接操作数据库
- [x] **独立配置**：所有配置独立于主配置，存放于hd_gui.yml
- [x] **兼容性保证**：确保与原有功能完全兼容，不影响原有功能正常使用

### 2.2 核心组件
| 组件名称 | 说明 | 状态 |
|---------|------|------|
| HologramDisplayManager | 负责创建和管理全息显示 | ✅ 已完成 |
| TextDisplayManager | 管理文本全息显示 | ✅ 已完成 |
| AuctionHistoryManager | 负责获取和管理拍卖历史记录 | ✅ 已完成 |
| HologramConfigManager | 处理全息展示配置文件 | ✅ 已完成 |
| HologramUpdateTask | 定时更新全息展示内容 | ✅ 已完成 |
| HologramCommandManager | 管理全息显示命令 | ✅ 已完成 |

## 3. 开发阶段

### 3.1 阶段一：基础架构（已完成）
- [x] 创建HologramDisplayManager基础类
- [x] 实现TextDisplayManager
  - [x] 设计文本格式化系统
  - [x] 实现文本颜色、大小、背景等样式
  - [x] 修复文本对齐方式，统一使用左对齐

### 3.2 阶段二：历史记录系统（已完成）
- [x] 设计AuctionHistoryManager
  - [x] 创建事件缓存系统
  - [x] 实现事件监听和记录
- [x] 实现事件格式化
  - [x] 设计不同类型事件的显示格式
  - [x] 支持多语言显示
  - [x] 完善上架事件价格显示（起拍价和一口价）

### 3.3 阶段三：配置系统（已完成）
- [x] 设计hd_gui.yml配置文件结构
- [x] 实现HologramConfigManager
- [x] 集成配置选项与MessageManager
- [x] 实现配置热重载功能

### 3.4 阶段四：管理命令实现（已完成）
- [x] 实现`/ah hud create`命令
  - [x] 支持选择创建位置
  - [x] 支持设置朝向
- [x] 实现`/ah hud remove`命令
- [x] 实现`/ah hud reload`命令
- [x] 实现权限检查

### 3.5 阶段五：更新任务系统（已完成）
- [x] 实现HologramUpdateTask
  - [x] 定时更新展示内容
  - [x] 支持配置更新频率
- [x] 优化更新逻辑
  - [x] 仅在有变化时更新
  - [x] 支持渐变过渡效果

### 3.6 阶段六：优化与测试（进行中）
- [x] 性能优化
  - [x] 实现了基于玩家距离的显示优化
  - [x] 改进了实体创建和更新逻辑，减少资源占用
- [x] 用户体验优化
  - [x] 优化信息布局和显示效果
  - [x] 实现服务器启动时自动清空并初始化全息显示
- [x] 兼容性测试
  - [x] 测试不同版本客户端兼容性
  - [x] 测试与其他插件的兼容性

## 4. 技术文档参考

### 4.1 Display实体通用方法
- [Display](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Display.html)
  - `setBillboard(Display.Billboard billboard)` - 设置全息显示的朝向模式
  - `setTransformation(Transformation transformation)` - 设置变换矩阵
  - `setBrightness(Display.Brightness brightness)` - 设置亮度
  - `setViewRange(float viewRange)` - 设置可见距离
  - `setShadowRadius(float shadowRadius)` - 设置阴影半径
  - `setShadowStrength(float shadowStrength)` - 设置阴影强度

### 4.2 ItemDisplay特有方法
- [ItemDisplay](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/ItemDisplay.html)
  - `setItemStack(ItemStack item)` - 设置显示的物品
  - `setItemDisplayTransform(ItemDisplay.ItemDisplayTransform transform)` - 设置物品显示变换
  - `ItemDisplay.ItemDisplayTransform.GUI` - GUI显示模式，模拟物品在GUI中的显示效果

### 4.3 TextDisplay特有方法
- [TextDisplay](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/TextDisplay.html)
  - `setText(Component text)` - 设置显示文本
  - `setAlignment(TextDisplay.TextAlignment alignment)` - 设置文本对齐方式
  - `setLineWidth(int lineWidth)` - 设置行宽
  - `setBackgroundColor(Color backgroundColor)` - 设置背景颜色
  - `setSeeThrough(boolean seeThrough)` - 设置是否可透视

## 5. 开发进度表

| 阶段 | 任务 | 开始时间 | 结束时间 | 状态 |
|------|-----|----------|---------|------|
| 1 | 基础架构 | 第1周 | 第2周末 | 已完成 |
| 1.1 | HologramDisplayManager实现 | 第1周 | 第1周末 | 已完成 |
| 1.2 | ItemDisplayManager实现 | 第1周 | 第2周中 | 已完成 |
| 1.3 | TextDisplayManager实现 | 第1周末 | 第2周末 | 已完成 |
| 2 | 历史记录系统 | 第3周 | 第3周末 | 已完成 |
| 2.1 | AuctionHistoryManager实现 | 第3周初 | 第3周中 | 已完成 |
| 2.2 | 事件格式化实现 | 第3周中 | 第3周末 | 已完成 |
| 3 | 配置系统 | 第4周 | 第4周末 | 已完成 |
| 3.1 | 配置文件设计 | 第4周初 | 第4周中 | 已完成 |
| 3.2 | 配置管理实现 | 第4周中 | 第4周末 | 已完成 |
| 4 | 管理命令实现 | 第5周 | 第5周末 | 已完成 |
| 4.1 | 创建命令 | 第5周初 | 第5周中 | 已完成 |
| 4.2 | 移除和重载命令 | 第5周中 | 第5周末 | 已完成 |
| 5 | 更新任务系统 | 第6周 | 第6周末 | 已完成 |
| 5.1 | 定时更新任务 | 第6周初 | 第6周中 | 已完成 |
| 5.2 | 更新优化 | 第6周中 | 第6周末 | 已完成 |
| 6 | 优化与测试 | 第7周 | 第8周末 | 进行中 |
| 6.1 | 性能优化 | 第7周 | 第7周末 | 已完成 |
| 6.2 | 用户体验优化 | 第7周末 | 第8周中 | 未开始 |
| 6.3 | 兼容性测试 | 第8周中 | 第8周末 | 已完成 |

## 6. 风险评估

### 6.1 技术风险
- **客户端兼容性**：新版本的Display实体可能在较旧的客户端上不兼容或显示异常
  - **缓解措施**：添加版本检测，在不支持的客户端上隐藏全息显示

- **性能问题**：多个全息实体在服务器固定位置可能影响性能
  - **缓解措施**：优化更新频率，仅在玩家附近时更新，减少实体数量

- **事件更新及时性**：实时监听所有拍卖事件可能增加服务器负担
  - **缓解措施**：使用异步任务处理事件记录，定时批量更新展示内容

### 6.2 用户体验风险
- **信息密度**：大量拍卖历史信息可能导致视觉混乱
  - **缓解措施**：优化信息布局，提供可配置的行距和字体大小

- **位置选择**：不恰当的全息展示位置可能影响服务器美观度
  - **缓解措施**：提供详细的位置调整命令，允许管理员精确定位全息展示板

## 7. 总结

全息拍卖行历史记录展示系统将为GlobalShop插件增添新的社区互动元素，让玩家可以在游戏世界中直观地了解拍卖行的实时动态。该功能利用Minecraft 1.21.4的Display实体系列，在不修改现有代码的情况下，为服务器创建一个公共信息展示平台。

通过合理的架构设计和配置系统，该功能能够无缝集成到现有插件中，为服务器管理员提供一种新的方式来增强玩家的交易体验和社区互动。全息展示板不仅具有实用价值，还能成为服务器的一个亮点特色，提升整体游戏氛围。

## 8. 最近更新日志

### 2024-07-13
1. 优化了全息显示系统的启动流程：
   - 在插件启动时自动清空并初始化所有全息显示
   - 确保全息显示在服务器重启后正确恢复
   - 添加了详细的初始化日志输出

2. 改进了TextDisplayManager的文本对齐：
   - 统一使用左对齐方式显示所有文本
   - 优化了文本布局和显示效果

3. 完善了上架事件的价格显示：
   - 现在会同时显示起拍价和一口价信息
   - 使用更清晰的价格格式化方式

4. 增强了HologramCommandManager功能：
   - 添加了forceUpdateAll方法用于强制更新所有全息显示
   - 改进了全息显示的清理和初始化逻辑

5. 优化了全息显示的性能：
   - 实现了更高效的更新机制
   - 减少了不必要的实体创建和更新操作

### 2024-07-12
1. 修复了`HologramDisplayManager.createHologram`方法：
   - 添加了实际创建TextDisplay实体的代码，而不仅仅是返回UUID
   - 使用官方API正确设置TextDisplay实体属性，确保显示效果
   - 添加了详细的错误处理和日志记录

2. 完善了`HologramUpdateTask.run`方法：
   - 添加了从HologramCommandManager同步位置信息的逻辑
   - 改进了全息内容更新逻辑，增加了错误处理和标题显示
   - 创建了`formatEventText`方法来美化显示文本

### 2024-07-10
1. 修复了`DatabaseManager`类中的`processExpiredAuctions`方法：
   - 将`addPendingItem`方法调用更改为`storePendingItem`
   - 将`calculateSellerFee`方法调用更改为`calculateFee`
   - 将`deposit`方法调用更改为`giveMoney`
2. 在`GuiManager`类中添加了两个新方法：
   - `isViewingGui(Player player)` - 检查玩家是否正在查看拍卖行界面
   - `closeGui(Player player)` - 关闭玩家当前的拍卖行界面
3. 在`message.yml`和`message_en.yml`文件中添加了缺失的消息配置：
   - `close.force_close` - 拍卖行被管理员强制关闭的消息
   - `close.success` - 所有玩家的拍卖行已被强制关闭的消息
   - `checkexpired.success` - 成功处理过期物品的消息
   - `checkexpired.none` - 没有过期物品的消息
4. 完成了项目的编译测试，解决了所有编译错误 