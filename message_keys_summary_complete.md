# GlobalShop 插件消息键(Keys)完整总结

## GUI监听器消息（之前缺失的部分）

- `gui_listener.points_unavailable` - 点券系统不可用消息
- `gui_listener.expired_bid` - 物品已过期无法出价消息
- `gui_listener.bid_outbid` - 竞价已被超越消息
- `gui_listener.previous_bid` - 前一次竞价消息
- `gui_listener.new_bid` - 新竞价消息
- `gui_listener.bid_accepted` - 竞价已接受消息
- `gui_listener.mailbox_full` - 物品已放入邮箱消息
- `gui_listener.bid_success` - 竞价成功消息
- `gui_listener.bid_item` - 竞价物品ID消息
- `gui_listener.bid_price` - 竞价价格消息
- `gui_listener.refund` - 退款消息
- `gui_listener.removed_item` - 物品已被移除消息
- `gui_listener.cancel_auction_success` - 取消拍卖成功消息
- `gui_listener.cancel_auction_success_inventory_full` - 取消拍卖成功但背包已满消息
- `gui_listener.cancel_auction_fail` - 取消拍卖失败消息

## 新增管理员查询功能消息键

- `messages.command.info.usage` - info命令用法
- `messages.command.player_not_found` - 玩家未找到消息
- `admin_info_gui.title` - 玩家信息界面标题
- `admin_info_gui.item_name` - 玩家信息物品名称
- `admin_info_gui.sold_count` - 玩家售出数量格式
- `admin_info_gui.purchased_count` - 玩家购买数量格式
- `admin_info_gui.earnings_header` - 玩家收入标题
- `admin_info_gui.earnings_vault` - 玩家金币收入格式
- `admin_info_gui.earnings_points` - 玩家点券收入格式
- `admin_info_gui.spending_header` - 玩家支出标题
- `admin_info_gui.spending_vault` - 玩家金币支出格式
- `admin_info_gui.spending_points` - 玩家点券支出格式
- `admin_info_gui.sales_history_button` - 查看销售记录按钮
- `admin_info_gui.purchase_history_button` - 查看购买记录按钮
- `admin_info_gui.sales_history_title` - 销售历史界面标题
- `admin_info_gui.purchase_history_title` - 购买历史界面标题
- `admin_info_gui.back_button_to_player_info` - 返回玩家信息按钮
- `admin_info_gui.active_auctions` - 玩家活跃拍卖数量文本

## AuctionItem格式化相关消息键

- `auction_item_format.time.expired` - 已过期文本
- `auction_item_format.time.day` - 时间单位-天
- `auction_item_format.time.hour` - 时间单位-小时
- `auction_item_format.time.minute` - 时间单位-分钟
- `auction_item_format.time.second` - 时间单位-秒
- `auction_item_format.time.not_sold` - 未售出文本
- `auction_item_format.display.unknown_item` - 未知物品文本

## 其他补充消息键

- `messages.bid_invalid_amount` - 无效竞价金额消息
- `messages.command.check_expired.none` - 检查过期无物品消息
- `auction_commands.close.force_close` - 关闭命令强制关闭消息
- `auction_commands.close.success` - 关闭命令成功消息
- `auction_commands.checkexpired.success` - 检查过期成功消息

这个完整版本包含了之前缺失的所有消息键，特别是GUI监听器部分和管理员查询功能部分。所有消息键都已按功能模块分类，便于查找和使用。