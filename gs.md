<div align="center">
<h1 style="color: #2196F3; font-size: 36px;">🏪 GlobalShop - 全球拍卖行</h1>
<p style="color: #666; font-size: 18px;">一个功能强大、界面美观的 Minecraft 拍卖行插件</p>
</div>

---

<h2 style="color: #E91E63; font-size: 28px;">📚 插件介绍</h2>

GlobalShop 是一个功能丰富的 Minecraft 拍卖行插件，提供类似魔兽世界拍卖行的功能。支持物品上架、竞价、一口价购买，并具有全息展示、邮箱系统等特色功能。

<h3 style="color: #4CAF50; font-size: 24px;">✨ 主要特性</h3>

- 🎯 支持竞价和一口价两种交易模式
- 💰 支持金币(Vault)和点券(PlayerPoints)双货币系统
- 🌐 支持中文物品名称搜索
- 📦 内置邮箱系统，安全保管交易物品
- 🎨 美观的GUI界面，支持分页显示
- 🌟 支持全息展示，在游戏世界中展示拍卖信息
- 🔄 支持MySQL和SQLite数据库
- 🌍 完整的多语言支持

<h2 style="color: #E91E63; font-size: 28px;">⌨️ 命令系统</h2>

<h3 style="color: #4CAF50; font-size: 24px;">📝 基础命令</h3>

| 命令 | 别名 | 描述 | 权限 |
|------|------|------|------|
| `/ah` | `/auc`, `/auction`, `/globalshop` | 打开拍卖行主菜单 | `globalshop.use` |
| `/ah help` | - | 显示帮助信息 | `globalshop.use` |
| `/ah sell <起拍价> <一口价> [货币类型]` | - | 上架手中的物品 | `globalshop.sell` |
| `/ah search <关键词>` | - | 搜索拍卖物品 | `globalshop.search` |
| `/ah my` | - | 查看自己的拍卖物品 | `globalshop.my` |
| `/ah collect` | - | 打开物品邮箱 | `globalshop.collect` |

<h3 style="color: #4CAF50; font-size: 24px;">👑 管理员命令</h3>

| 命令 | 描述 | 权限 |
|------|------|------|
| `/ah reload` | 重新加载配置 | `globalshop.admin` |
| `/ah close` | 强制所有玩家关闭拍卖行界面 | `globalshop.admin` |
| `/ah checkexpired` | 检查并处理过期的拍卖物品 | `globalshop.admin` |
| `/ah info <玩家名>` | 查询玩家的拍卖行信息 | `globalshop.admin` |

<h3 style="color: #4CAF50; font-size: 24px;">🌟 全息显示命令</h3>

| 命令 | 描述 | 权限 |
|------|------|------|
| `/ah hud create <名称>` | 在当前位置创建全息拍卖行 | `globalshop.hud.admin` |
| `/ah hud remove <名称>` | 移除指定名称的全息拍卖行 | `globalshop.hud.admin` |
| `/ah hud list` | 列出所有全息拍卖行 | `globalshop.hud.admin` |
| `/ah hud reload` | 重新加载全息拍卖行配置 | `globalshop.hud.admin` |

<h2 style="color: #E91E63; font-size: 28px;">🔒 权限节点</h2>

<h3 style="color: #4CAF50; font-size: 24px;">👥 玩家权限</h3>

```yaml
globalshop.use: 使用拍卖行的基本权限
globalshop.sell: 允许上架物品
globalshop.search: 允许搜索物品
globalshop.my: 允许查看自己的拍卖物品
globalshop.collect: 允许使用物品邮箱
globalshop.bid: 允许参与竞价
globalshop.buynow: 允许使用一口价购买
```

<h3 style="color: #4CAF50; font-size: 24px;">⚡ 管理员权限</h3>

```yaml
globalshop.admin: 管理员基本权限（包含所有普通权限）
globalshop.hud.admin: 管理全息拍卖行的权限
globalshop.reload: 重载插件配置的权限
globalshop.close: 强制关闭拍卖行界面的权限
globalshop.checkexpired: 检查过期物品的权限
globalshop.info: 查询玩家信息的权限
```

<h2 style="color: #E91E63; font-size: 28px;">🛠️ 功能详解</h2>

<h3 style="color: #4CAF50; font-size: 24px;">💰 拍卖系统</h3>

- **上架物品**
  - 支持设置起拍价和一口价
  - 可选择货币类型（金币/点券）
  - 自定义拍卖持续时间（1小时-7天）
  - 每个玩家最多同时上架20个物品

- **竞价系统**
  - 支持最低加价金额设置
  - 自动计算下一次最低出价
  - 到期自动结算

- **一口价系统**
  - 立即购买功能
  - 自动计算交易手续费
  - 即时完成交易

<h3 style="color: #4CAF50; font-size: 24px;">📬 邮箱系统</h3>

- **自动存储**
  - 已售出物品自动进入买家邮箱
  - 过期物品自动返回卖家邮箱
  - 交易记录完整保存

- **物品管理**
  - 简单的领取界面
  - 物品状态清晰显示
  - 支持批量领取

<h3 style="color: #4CAF50; font-size: 24px;">🔍 搜索系统</h3>

- **多维度搜索**
  - 按物品名称搜索
  - 按卖家名称搜索
  - 支持中文搜索
  - 模糊匹配支持

- **筛选功能**
  - 按物品类型筛选
  - 按价格区间筛选
  - 按上架时间筛选

<h3 style="color: #4CAF50; font-size: 24px;">📊 经济系统</h3>

- **双货币支持**
  - Vault经济（金币）
  - PlayerPoints（点券）
  - 可自定义货币名称和符号

- **手续费系统**
  - 可配置交易手续费率
  - 支持最低手续费设置
  - 手续费自动扣除

<h3 style="color: #4CAF50; font-size: 24px;">🌟 全息显示</h3>

- **创建与管理**
  - 在任意位置创建全息拍卖行
  - 支持多个全息展示点
  - 简单的管理命令

- **显示内容**
  - 最新上架物品展示
  - 热门交易物品展示
  - 实时价格更新
  - 支持自定义显示内容

<h3 style="color: #4CAF50; font-size: 24px;">📢 广播系统</h3>

- **多种广播方式**
  - 聊天框广播
  - Boss栏广播
  - 标题广播
  - ActionBar广播

- **广播事件**
  - 物品上架广播
  - 竞价成功广播
  - 一口价购买广播
  - 可自定义广播内容和格式

<h2 style="color: #E91E63; font-size: 28px;">⚙️ 配置说明</h2>

插件配置文件位于 `plugins/GlobalShop/config.yml`，支持以下主要配置项：

- 数据库设置（MySQL/SQLite）
- 经济系统设置
- 拍卖系统参数
- GUI界面设置
- 全息显示设置
- 广播系统设置
- 语言文件设置

<h2 style="color: #E91E63; font-size: 28px;">📋 依赖插件</h2>

- **必需依赖**
  - Vault - 经济系统支持

- **可选依赖**
  - PlayerPoints - 点券系统支持

<h2 style="color: #E91E63; font-size: 28px;">🔧 技术支持</h2>

如果您在使用过程中遇到任何问题，可以通过以下方式获取帮助：

- 💬 加入QQ群：1041380457

<div align="center">
<p style="color: #666; font-size: 14px;">
GlobalShop - 让您的服务器拥有一个专业的拍卖系统
</p>
</div>