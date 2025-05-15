# GlobalShop 插件消息键(Keys)总结

通过分析`MessageManager.java`文件，我整理出了GlobalShop插件中所有的消息键(keys)，这些键用于从配置文件中加载对应的文本消息。以下是按功能模块分类的所有消息键：

## GUI 界面按钮文本

### 通用按钮
- `gui.buttons.previous_page` - 上一页按钮文本
- `gui.buttons.previous_page_desc` - 上一页按钮描述
- `gui.buttons.next_page` - 下一页按钮文本
- `gui.buttons.next_page_desc` - 下一页按钮描述
- `gui.buttons.return_main_menu` - 返回主菜单按钮文本
- `gui.buttons.return_main_menu_desc` - 返回主菜单按钮描述
- `gui.buttons.return` - 返回按钮文本
- `gui.buttons.cancel` - 取消按钮文本
- `gui.buttons.confirm` - 确认按钮文本
- `gui.buttons.search_items` - 搜索物品按钮文本
- `gui.buttons.sell_item` - 上架物品按钮文本
- `gui.buttons.my_auctions` - 我的拍卖按钮文本

### 购买相关按钮
- `gui.buttons.confirm_buy` - 确认购买按钮文本
- `gui.buttons.cancel_buy` - 取消购买按钮文本
- `gui.buttons.confirm_buy_tip` - 确认购买提示文本
- `gui.buttons.confirm_buy_item` - 确认购买物品名称格式
- `gui.buttons.confirm_buy_price` - 确认购买价格格式
- `gui.buttons.cancel_buy_tip` - 取消购买提示文本

### 竞价相关按钮
- `gui.buttons.current_bid_amount` - 当前竞价金额文本
- `gui.buttons.increase_bid` - 加价按钮文本
- `gui.buttons.confirm_bid` - 确认竞价按钮文本
- `gui.buttons.cancel_bid` - 取消竞价按钮文本
- `gui.buttons.cancel_bid_tip` - 取消竞价提示文本
- `gui.buttons.cancel_bid_return` - 取消竞价返回提示文本
- `gui.buttons.bid_increase` - 预先抬价按钮文本

### 物品邮箱相关按钮
- `gui.buttons.sold_items` - 已售出按钮文本
- `gui.buttons.purchased_items` - 已购买按钮文本
- `gui.buttons.mailbox` - 物品邮箱按钮文本
- `gui.buttons.mailbox_description_line1` - 物品邮箱描述第一行
- `gui.buttons.mailbox_description_line2` - 物品邮箱描述第二行
- `gui.buttons.mailbox_page_info` - 邮箱页面信息
- `gui.buttons.mailbox_items_count` - 邮箱物品数量
- `gui.buttons.mailbox_storage_header` - 邮箱存储标题
- `gui.buttons.mailbox_storage_expired` - 邮箱存储过期物品说明
- `gui.buttons.mailbox_storage_won` - 邮箱存储竞拍物品说明
- `gui.buttons.mailbox_storage_full` - 邮箱存储背包已满物品说明
- `gui.buttons.mailbox_item_status_pending` - 邮箱物品状态-待领取
- `gui.buttons.mailbox_item_status_expired` - 邮箱物品状态-过期
- `gui.buttons.mailbox_item_status_other` - 邮箱物品状态-其他
- `gui.buttons.mailbox_item_add_time` - 邮箱物品添加时间
- `gui.buttons.mailbox_item_expire_time` - 邮箱物品过期时间
- `gui.buttons.mailbox_item_collect_tip` - 邮箱物品领取提示

### 上架相关按钮
- `gui.buttons.place_item_here` - 放置物品提示文本
- `gui.buttons.confirm_sell` - 确认上架按钮文本
- `gui.buttons.confirm_sell_description` - 确认上架按钮描述
- `gui.buttons.confirm_sell_description_color` - 确认上架按钮描述颜色

### 搜索相关按钮
- `gui.buttons.enter_item_name` - 输入物品名称提示文本
- `gui.buttons.enter_item_name_description` - 输入物品名称描述
- `gui.buttons.search_history` - 搜索历史文本
- `gui.buttons.clear_search_history` - 清除搜索历史文本
- `gui.buttons.new_search` - 新搜索按钮文本
- `gui.buttons.new_search_desc` - 新搜索按钮描述文本
- `gui.buttons.cancel_search` - 取消搜索文本
- `gui.buttons.search_prefix` - 搜索前缀文本
- `gui.buttons.search_history_item_description` - 搜索历史条目描述文本
- `gui.buttons.clear_search_history_description` - 清除搜索历史描述文本
- `gui.buttons.search_history_return` - 搜索历史返回按钮描述文本
- `gui.buttons.seller_search` - 搜索卖家按钮文本
- `gui.buttons.seller_search_desc` - 搜索卖家按钮描述文本

### 统计信息按钮
- `gui.buttons.page_info_prefix` - 页面信息前缀
- `gui.buttons.sold_items_count_prefix` - 已售出物品数量前缀
- `gui.buttons.total_sold_items_count_prefix` - 总售出物品数量前缀
- `gui.buttons.total_coins_earned_prefix` - 总计赚取金币前缀
- `gui.buttons.total_points_earned_prefix` - 总计赚取点券前缀
- `gui.buttons.purchased_items_count_prefix` - 已购买物品数量前缀
- `gui.buttons.total_purchased_items_count_prefix` - 总计购买物品数量前缀
- `gui.buttons.total_coins_spent_prefix` - 总计花费金币前缀
- `gui.buttons.total_points_spent_prefix` - 总计花费点券前缀
- `gui.buttons.my_sold_auctions_title` - 我的已售出拍卖标题
- `gui.buttons.my_purchased_auctions_title` - 我的已购买拍卖标题

## GUI 界面标题

- `gui.titles.sell_menu` - 上架物品界面标题
- `gui.titles.search_menu` - 搜索物品界面标题
- `gui.titles.confirm_buy_menu` - 确认购买界面标题
- `gui.titles.bid_menu` - 竞价购买界面标题
- `gui.titles.my_auctions_menu` - 我的拍卖界面标题
- `gui.titles.my_sold_auctions_menu` - 我的已售出拍卖界面标题
- `gui.titles.mailbox_menu` - 物品邮箱界面标题
- `gui.titles.my_mailbox_menu_prefix` - 我的物品邮箱界面标题前缀
- `gui.titles.expired_auctions_menu` - 过期拍卖界面标题
- `gui.titles.search_result_prefix` - 搜索结果标题前缀
- `gui.titles.search_history_menu` - 搜索历史界面标题

## 拍卖物品显示格式

### 基本信息
- `auction_item.divider` - 拍卖物品分隔线
- `auction_item.info_header` - 拍卖信息标题
- `auction_item.item_id` - 物品ID格式
- `auction_item.currency_type` - 货币类型格式
- `auction_item.start_price` - 起拍价格式
- `auction_item.current_price` - 当前价格格式
- `auction_item.deal_price` - 成交价格格式
- `auction_item.buy_now_price` - 一口价格式
- `auction_item.list_time` - 上架时间格式
- `auction_item.remaining_time` - 剩余时间格式
- `auction_item.seller` - 卖家格式
- `auction_item.current_bidder` - 当前出价者格式

### 物品所有者提示
- `auction_item.owner_tips.header` - 物品所有者提示标题
- `auction_item.owner_tips.cancel` - 物品所有者取消提示

### 买家提示
- `auction_item.buyer_tips.bid` - 买家竞价提示
- `auction_item.buyer_tips.buy` - 买家购买提示

### 管理员提示
- `auction_item.admin_tips.header` - 管理员操作标题
- `auction_item.admin_tips.force_cancel` - 管理员强制下架提示

### 我的拍卖物品格式
- `auction_item.my_auction_start_price` - 我的拍卖起拍价格式
- `auction_item.my_auction_current_price` - 我的拍卖当前价格式
- `auction_item.my_auction_buy_now_price` - 我的拍卖一口价格式
- `auction_item.my_auction_remaining_time` - 我的拍卖剩余时间格式
- `auction_item.my_auction_current_bidder` - 我的拍卖当前出价者格式
- `auction_item.my_auction_has_bidder_warning1` - 已有人出价警告1文本
- `auction_item.my_auction_has_bidder_warning2` - 已有人出价警告2文本
- `auction_item.my_auction_cancel_tip` - 取消拍卖提示文本

### 已售出物品格式
- `auction_item.sold_item_deal_price` - 已售出物品成交价格格式
- `auction_item.sold_item_buyer` - 已售出物品买家格式
- `auction_item.sold_item_seller` - 已售出物品卖家格式
- `auction_item.sold_item_sold_time` - 已售出物品售出时间格式
- `auction_item.sold_item_notice1` - 已售出物品提示1文本
- `auction_item.sold_item_notice2` - 已售出物品提示2文本

## 上架菜单相关

- `sell_menu.command_tips.header` - 命令提示标题
- `sell_menu.command_tips.usage` - 命令用法提示
- `sell_menu.command_tips.currency_types` - 货币类型提示

### 上架时间设置
- `sell_menu.duration_button.title` - 自定义上架时间按钮标题
- `sell_menu.duration_button.current_setting` - 当前设置时间格式
- `sell_menu.duration_button.divider` - 上架时间按钮分隔线
- `sell_menu.duration_button.left_click` - 左键点击提示
- `sell_menu.duration_button.right_click` - 右键点击提示
- `sell_menu.duration_button.shift_left_click` - Shift+左键点击提示
- `sell_menu.duration_button.shift_right_click` - Shift+右键点击提示
- `sell_menu.duration_button.middle_click` - 中键点击提示
- `sell_menu.duration_button.min_time` - 最短时间提示格式
- `sell_menu.duration_button.max_time` - 最长时间提示格式
- `sell_menu.duration_button.note1` - 时间设置注意事项1
- `sell_menu.duration_button.note2` - 时间设置注意事项2

## 竞价菜单相关

- `bid_menu.item_text` - 竞价物品文本
- `bid_menu.original_price_text` - 原始价格文本
- `bid_menu.current_price_text` - 当前竞价文本
- `bid_menu.rate_text` - 加价幅度文本
- `bid_menu.increase_tip` - 点击增加竞价提示文本
- `bid_menu.min_increase_text` - 最小加价文本
- `bid_menu.confirm_tip` - 确认竞价提示文本
- `bid_menu.confirm_notice1` - 确认竞价警告文本1
- `bid_menu.confirm_notice2` - 确认竞价提示2文本
- `bid_menu.confirm_pre_amount_text` - 确认竞价预加价金额文本
- `bid_menu.pre_amount_text` - 预加价金额文本

## 我的拍卖页面相关

- `my_auction.page_info` - 我的拍卖页面信息格式
- `my_auction.sold_count` - 我的拍卖已售出数量格式
- `my_auction.expired_count` - 我的拍卖已过期数量格式
- `my_auction.listings_count` - 我的拍卖上架数量格式
- `my_auction.limit_warning` - 我的拍卖上架数量接近上限警告文本

## 广播系统相关

- `broadcast.details_button` - 详细信息按钮文本
- `broadcast.no_details_available` - 无详细信息提示

## 物品邮箱相关

- `mailbox.mailbox_filter_tags` - 邮箱物品过滤标签列表
- `mailbox.expired_auction_tags` - 邮箱过期物品标签列表
- `mailbox_details.collect_success` - 物品成功领取消息
- `mailbox_details.inventory_full` - 背包已满无法领取物品消息
- `mailbox_details.auction_has_bidder` - 物品已有人出价无法取消拍卖消息
- `mailbox_details.filter_keywords` - 邮箱物品过滤关键词列表

## 物品详情查看相关

- `item_details.header` - 物品详情标题
- `item_details.item_name` - 物品名称格式
- `item_details.start_price` - 起拍价格格式(详情查看)
- `item_details.current_price` - 当前价格格式(详情查看)
- `item_details.buy_now_price` - 一口价格式(详情查看)
- `item_details.current_bidder` - 当前出价者格式(详情查看)
- `item_details.remaining_time` - 剩余时间格式(详情查看)

## 管理员信息查询相关

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

## 历史记录相关

- `history_item.info_header` - 历史记录标题
- `history_item.sold_time` - 售出时间格式
- `history_item.buyer` - 买家格式

## 时间格式化相关

- `auction_item_format.time.expired` - 已过期文本
- `auction_item_format.time.day` - 时间单位-天
- `auction_item_format.time.hour` - 时间单位-小时
- `auction_item_format.time.minute` - 时间单位-分钟
- `auction_item_format.time.second` - 时间单位-秒
- `auction_item_format.time.not_sold` - 未售出文本
- `auction_item_format.display.unknown_item` - 未知物品文本

## 命令系统相关

### 帮助命令
- `auction_commands.help.header` - 帮助菜单标题
- `auction_commands.help.help` - 帮助命令描述
- `auction_commands.help.open` - 打开命令描述
- `auction_commands.help.sell` - 出售命令描述
- `auction_commands.help.search` - 搜索命令描述
- `auction_commands.help.my` - 我的拍卖命令描述
- `auction_commands.help.collect` - 领取命令描述
- `auction_commands.help.admin_header` - 管理员命令标题
- `auction_commands.help.reload` - 重载命令描述
- `auction_commands.help.close` - 关闭命令描述
- `auction_commands.help.checkexpired` - 检查过期命令描述
- `auction_commands.help.info` - info命令帮助消息

### 命令TAB补全提示
- `auction_commands.tab_completion.sell_price` - sell命令起拍价提示
- `auction_commands.tab_completion.sell_buynow` - sell命令一口价提示
- `auction_commands.tab_completion.sell_currency` - sell命令货币类型提示
- `auction_commands.tab_completion.search_keyword` - search命令关键词提示

### 物品收集命令
- `auction_commands.collect.success` - 物品收集命令成功消息

### 出售命令
- `auction_commands.sell.usage` - 出售命令用法消息
- `auction_commands.sell.currency_types` - 货币类型描述消息
- `auction_commands.sell.points_unavailable` - 点券不可用消息
- `auction_commands.sell.points_unavailable_solution` - 点券不可用解决方案消息
- `auction_commands.sell.invalid_currency` - 无效货币类型消息
- `auction_commands.sell.max_listings_reached` - 已达到最大上架数量消息
- `auction_commands.sell.wait_for_items_sold` - 等待物品售出消息
- `auction_commands.sell.start_price_zero` - 起拍价必须大于0消息
- `auction_commands.sell.buy_now_less_than_start` - 一口价必须大于起拍价消息
- `auction_commands.sell.invalid_price` - 无效价格消息

### 搜索命令
- `auction_commands.search.usage` - 搜索命令用法消息
- `auction_commands.search.min_length` - 搜索关键词最小长度消息
- `auction_commands.search.searching` - 正在搜索消息

### 关闭命令
- `auction_commands.close.starting` - 正在关闭拍卖消息
- `auction_commands.close.async_notice` - 异步执行通知消息
- `auction_commands.close.force_close` - 强制关闭消息
- `auction_commands.close.success` - 关闭成功消息

### 检查过期命令
- `auction_commands.checkexpired.player_only` - 检查过期仅玩家消息
- `auction_commands.checkexpired.checking` - 正在检查过期消息
- `auction_commands.checkexpired.success` - 检查过期成功消息

### 重载命令
- `auction_commands.reload.success` - 重载成功消息
- `auction_commands.reload.debug_info` - 重载调试信息消息

### 通用命令消息
- `auction_commands.player_only` - 仅玩家可用消息
- `auction_commands.unknown_command` - 未知命令消息
- `auction_commands.no_permission` - 无权限消息

### 信息查询命令
- `messages.command.info.usage` - 信息查询命令用法
- `messages.command.player_not_found` - 玩家未找到消息
- `messages.command.check_expired.none` - 检查过期无物品消息

## 系统消息

### 通用消息
- `messages.cancel_bid` - 取消竞价消息
- `messages.cancel_search` - 已取消搜索消息
- `messages.enter_search_keyword` - 请输入搜索关键词消息
- `messages.search_history_cleared` - 搜索历史已清空消息
- `messages.seller_search_prefix` - 卖家前缀文本
- `messages.enter_seller_search` - 输入卖家搜索关键词消息

### 上架相关消息
- `messages.place_item_first` - 请先放入要上架的物品消息
- `messages.drag_item_to_center` - 请直接拖动物品到中央格子消息
- `messages.cant_drag_to_inventory` - 不能向界面拖拽物品消息
- `messages.listing_failed_no_price` - 上架失败：缺少价格信息消息
- `messages.listing_failed_incomplete_price` - 上架失败：价格信息不完整消息
- `messages.listing_failed_price_format` - 上架失败：价格格式错误消息
- `messages.price_exceeds_max_limit` - 价格超出最大限制消息
- `messages.start_price_greater_than_zero` - 起拍价必须大于0消息
- `messages.buy_now_greater_than_start` - 一口价必须大于起拍价消息
- `messages.not_enough_money_for_fee` - 没有足够资金支付手续费消息
- `messages.item_listed_success` - 物品上架成功消息
- `messages.item_listed_success_with_fee` - 物品上架成功并收取手续费消息
- `messages.item_listed_fail` - 物品上架失败消息
- `messages.listing_error` - 上架过程中发生错误消息
- `messages.item_returned` - 物品已返还到背包的消息
- `messages.inventory_full_drop_item` - 背包已满物品掉落的消息
- `messages.max_listings_reached` - 达到最大上架数量限制消息
- `messages.wait_for_items_to_sell` - 等待已上架物品售出或过期消息

### 竞价相关消息
- `messages.bid_usage` - 竞价命令用法
- `messages.bid_id_must_be_number` - 物品ID必须是数字
- `messages.bid_invalid_item` - 找不到有效的拍卖物品
- `messages.bid_no_permission` - 没有权限参与竞价
- `messages.bid_own_item` - 不能竞拍自己的物品
- `messages.bid_already_highest` - 已经是当前最高出价者
- `messages.bid_opening_menu` - 正在打开竞价界面
- `messages.bid_error` - 竞价过程中发生错误
- `messages.bid_below_minimum` - 竞价低于最低要求
- `messages.bid_below_current_price` - 竞价低于当前价格
- `messages.bid_invalid_amount` - 无效竞价金额
- `messages.bid_info_expired` - 竞价信息已过期消息

### 购买相关消息
- `messages.buy_now_not_available` - 该物品不支持一口价购买
- `messages.buy_now_points_unavailable` - 点券支付功能不可用
- `messages.buy_now_not_enough_money` - 没有足够的货币来购买
- `messages.buy_now_inventory_full` - 背包已满无法购买
- `messages.buy_now_success` - 购买成功
- `messages.buy_now_item` - 购买物品信息
- `messages.buy_now_price` - 购买价格信息
- `messages.buy_now_balance` - 购买后余额信息
- `messages.buy_now_inventory_full_mailbox` - 背包已满物品已放入邮箱
- `messages.buy_now_mailbox_instructions` - 邮箱领取指南
- `messages.buy_now_seller_notification` - 卖家物品售出通知
- `messages.buy_now_seller_income` - 卖家收入信息
- `messages.buy_now_seller_balance` - 卖家余额信息
- `messages.buy_now_buyer` - 买家信息
- `messages.failed_transaction` - 交易失败消息

### 错误消息
- `error_messages.item_not_exists` - 物品不存在或已被购买的错误消息

### GUI监听器消息
- `gui_listener.cancel_bid_operation` - 已取消竞价操作消息
- `gui_listener.reset_duration` - 重置上架时间提示消息
- `gui_listener.max_duration_limit` - 最大上架时间限制消息
- `gui_listener.update_duration` - 更新上架时间消息
- `gui_listener.custom_duration_below_min` - 自定义时间低于最小限制消息
- `gui_listener.custom_duration_above_max` - 自定义时间超过最大限制消息
- `gui_listener.custom_duration_set` - 设置自定义上架时间消息
- `gui_listener.expired_item` - 物品已过期或已售出消息
- `gui_listener.owner_bid` - 不能对自己物品出价消息
- `gui_listener.no_permission` - 无权限消息
- `gui_listener.already_highest_bidder` - 已是最高出价者消息
- `gui_listener.owner_buy` - 不能购买自己物品消息
- `gui_listener.no_buy_now_price` - 物品没有一口价消息
- `gui_listener.invalid_item` - 物品不存在或已被购买消息
- `gui_listener.not_enough_money` - 没有足够资金消息
- `gui_listener.inventory_full` - 背包已满消息
- `gui_listener.bid_sold` - 物品已被竞价购买消息
- `gui_listener.balance` - 余额消息
- `gui_listener.partial_item_saved` - 物品已保存到待领取列表消息
- `gui_listener.purchase_success` - 购买成功消息
- `gui_listener.purchase_item` - 购买物品消息
- `gui_listener.purchase_price` - 购买价格消息
- `gui_listener.sold_notification` - 物品售出通知消息
- `gui_listener.seller_fee` - 卖家手续费消息
- `gui_listener.seller_income` - 卖家实际收入消息
- `gui_listener.seller_balance` - 卖家余额消息
- `gui_listener.buyer` - 买家信息消息
- `gui_listener.cancel_purchase` - 取消购买消息
- `gui_listener.expired_bid_info` - 竞价信息已过期消息
- `gui_listener.pre_bid` - 预加价消息
- `gui_listener.bid_amount` - 竞价金额消息
- `gui_listener.incomplete_bid_info` - 竞价信息不完整消息
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