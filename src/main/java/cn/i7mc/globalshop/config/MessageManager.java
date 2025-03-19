package cn.i7mc.globalshop.config;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;

/**
 * 消息管理器，负责加载和管理message.yml配置文件
 */
public class MessageManager {
    private final GlobalShop plugin;
    private FileConfiguration messages;
    private File messagesFile;

    /**
     * 构造消息管理器
     * @param plugin 插件实例
     */
    public MessageManager(GlobalShop plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * 加载消息配置文件
     */
    public void loadMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        messagesFile = new File(plugin.getDataFolder(), "message.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("message.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * 保存消息配置文件
     */
    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存message.yml: " + e.getMessage());
        }
    }

    /**
     * 重新加载消息配置文件
     */
    public void reloadMessages() {
        loadMessages();
    }

    /**
     * 获取消息配置
     * @return 消息配置
     */
    public FileConfiguration getMessages() {
        return messages;
    }

    /**
     * 获取GUI按钮文本
     * @param key 按钮键
     * @param defaultText 默认文本
     * @return 按钮文本
     */
    public String getButtonText(String key, String defaultText) {
        return messages.getString("gui.buttons." + key, defaultText);
    }

    /**
     * 获取上一页按钮文本
     * @return 上一页按钮文本
     */
    public String getPreviousPageText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("buttons.previous_page", "&e上一页"));
    }
    
    /**
     * 获取下一页按钮文本
     * @return 下一页按钮文本
     */
    public String getNextPageText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("buttons.next_page", "&e下一页"));
    }
    
    /**
     * 获取返回主菜单按钮文本
     * @return 返回主菜单按钮文本
     */
    public String getReturnMainMenuText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("buttons.return_main_menu", "&c返回主菜单"));
    }

    /**
     * 快捷方法获取返回按钮文本
     * @return 返回按钮文本
     */
    public String getReturnText() {
        return getButtonText("return", "§e返回");
    }

    /**
     * 快捷方法获取取消按钮文本
     * @return 取消按钮文本
     */
    public String getCancelText() {
        return getButtonText("cancel", "§c取消");
    }

    /**
     * 快捷方法获取确认按钮文本
     * @return 确认按钮文本
     */
    public String getConfirmText() {
        return getButtonText("confirm", "§a确认");
    }

    /**
     * 快捷方法获取搜索物品按钮文本
     * @return 搜索物品按钮文本
     */
    public String getSearchItemsText() {
        return getButtonText("search_items", "搜索物品");
    }

    /**
     * 快捷方法获取上架物品按钮文本
     * @return 上架物品按钮文本
     */
    public String getSellItemText() {
        return getButtonText("sell_item", "上架物品");
    }

    /**
     * 快捷方法获取我的拍卖按钮文本
     * @return 我的拍卖按钮文本
     */
    public String getMyAuctionsText() {
        return getButtonText("my_auctions", "我的拍卖");
    }

    /**
     * 快捷方法获取确认购买按钮文本
     * @return 确认购买按钮文本
     */
    public String getConfirmBuyText() {
        return getButtonText("confirm_buy", "§a确认购买");
    }

    /**
     * 快捷方法获取取消购买按钮文本
     * @return 取消购买按钮文本
     */
    public String getCancelBuyText() {
        return getButtonText("cancel_buy", "§c取消");
    }

    /**
     * 快捷方法获取当前竞价金额文本
     * @return 当前竞价金额文本
     */
    public String getCurrentBidAmountText() {
        return getButtonText("current_bid_amount", "§e当前竞价金额");
    }

    /**
     * 快捷方法获取加价按钮文本
     * @return 加价按钮文本
     */
    public String getIncreaseBidText() {
        return getButtonText("increase_bid", "§a加价 +5%");
    }

    /**
     * 快捷方法获取确认竞价按钮文本
     * @return 确认竞价按钮文本
     */
    public String getConfirmBidText() {
        return getButtonText("confirm_bid", "§2确认竞价");
    }

    /**
     * 快捷方法获取取消竞价按钮文本
     * @return 取消竞价按钮文本
     */
    public String getCancelBidText() {
        return getButtonText("cancel_bid", "§c取消竞价");
    }

    /**
     * 快捷方法获取已售出按钮文本
     * @return 已售出按钮文本
     */
    public String getSoldItemsText() {
        return getButtonText("sold_items", "§6已售出");
    }

    /**
     * 快捷方法获取物品邮箱按钮文本
     * @return 物品邮箱按钮文本
     */
    public String getMailboxText() {
        return getButtonText("mailbox", "§e物品邮箱");
    }

    /**
     * 快捷方法获取放置物品提示文本
     * @return 放置物品提示文本
     */
    public String getPlaceItemHereText() {
        return getButtonText("place_item_here", "请将物品放在这里");
    }

    /**
     * 快捷方法获取确认上架按钮文本
     * @return 确认上架按钮文本
     */
    public String getConfirmSellText() {
        return getButtonText("confirm_sell", "§a确认上架");
    }

    /**
     * 快捷方法获取输入物品名称提示文本
     * @return 输入物品名称提示文本
     */
    public String getEnterItemNameText() {
        return getButtonText("enter_item_name", "输入物品名称");
    }
    
    /**
     * 快捷方法获取搜索历史文本
     * @return 搜索历史文本
     */
    public String getSearchHistoryText() {
        return getButtonText("search_history", "§a搜索历史");
    }
    
    /**
     * 快捷方法获取清除搜索历史文本
     * @return 清除搜索历史文本
     */
    public String getClearSearchHistoryText() {
        return getButtonText("clear_search_history", "§c清除历史");
    }
    
    /**
     * 快捷方法获取新搜索按钮文本
     * @return 新搜索按钮文本
     */
    public String getNewSearchText() {
        return getButtonText("new_search", "§a新的搜索");
    }
    
    /**
     * 获取取消竞价消息
     * @return 取消竞价消息
     */
    public String getCancelBidMessage() {
        return messages.getString("messages.cancel_bid", "已取消竞价");
    }
    
    /**
     * 获取预先抬价按钮文本
     * @return 预先抬价按钮文本
     */
    public String getBidIncreaseText() {
        return getButtonText("bid_increase", "§b预先抬价");
    }
    
    /**
     * 获取上架物品界面标题
     * @return 上架物品界面标题
     */
    public String getSellMenuTitle() {
        return messages.getString("gui.sell_menu", "上架物品");
    }

    /**
     * 获取搜索物品界面标题
     * @return 搜索物品界面标题
     */
    public String getSearchMenuTitle() {
        return messages.getString("gui.search_menu", "搜索物品");
    }

    /**
     * 获取确认购买界面标题
     * @return 确认购买界面标题
     */
    public String getConfirmBuyMenuTitle() {
        return messages.getString("gui.confirm_buy_menu", "确认购买");
    }

    /**
     * 获取竞价购买界面标题
     * @return 竞价购买界面标题
     */
    public String getBidMenuTitle() {
        return messages.getString("gui.bid_menu", "竞价购买");
    }

    /**
     * 获取我的拍卖界面标题
     * @return 我的拍卖界面标题
     */
    public String getMyAuctionsMenuTitle() {
        return messages.getString("gui.my_auctions_menu", "我的拍卖");
    }

    /**
     * 获取我的已售出拍卖界面标题
     * @return 我的已售出拍卖界面标题
     */
    public String getMySoldAuctionsMenuTitle() {
        return messages.getString("gui.my_sold_auctions_menu", "我的已售出拍卖");
    }

    /**
     * 获取物品邮箱界面标题
     * @return 物品邮箱界面标题
     */
    public String getMailboxMenuTitle() {
        return messages.getString("gui.mailbox_menu", "§8物品邮箱");
    }

    /**
     * 获取我的物品邮箱界面标题前缀
     * @return 我的物品邮箱界面标题前缀
     */
    public String getMyMailboxMenuTitlePrefix() {
        return messages.getString("gui.my_mailbox_menu_prefix", "§e我的物品邮箱");
    }

    /**
     * 获取过期拍卖界面标题
     * @return 过期拍卖界面标题
     */
    public String getExpiredAuctionsMenuTitle() {
        return messages.getString("gui.expired_auctions_menu", "我的过期拍卖（无人出价）");
    }

    /**
     * 获取取消搜索文本
     * @return 取消搜索文本
     */
    public String getCancelSearchText() {
        return messages.getString("gui.cancel_search", "取消");
    }

    /**
     * 获取已取消搜索消息
     * @return 已取消搜索消息
     */
    public String getCancelSearchMessage() {
        return messages.getString("messages.cancel_search", "§a已取消搜索");
    }
    
    /**
     * 获取请输入搜索关键词消息
     * @return 请输入搜索关键词消息
     */
    public String getEnterSearchKeywordMessage() {
        return messages.getString("messages.enter_search_keyword", "§a请在聊天栏输入要搜索的物品名称，或者输入 §e取消 §a来取消搜索");
    }
    
    /**
     * 获取搜索历史已清空消息
     * @return 搜索历史已清空消息
     */
    public String getSearchHistoryClearedMessage() {
        return messages.getString("messages.search_history_cleared", "§a搜索历史已清空");
    }
    
    /**
     * 获取搜索结果标题前缀
     * @return 搜索结果标题前缀
     */
    public String getSearchResultTitlePrefix() {
        return messages.getString("gui.titles.search_result_prefix", "搜索结果:");
    }
    
    /**
     * 获取"请先放入要上架的物品"消息
     * @return 请先放入要上架的物品消息
     */
    public String getPlaceItemFirstMessage() {
        return messages.getString("messages.place_item_first", "§c请先放入要上架的物品!");
    }
    
    /**
     * 获取"请直接拖动物品到中央格子"消息
     * @return 请直接拖动物品到中央格子消息
     */
    public String getDragItemToCenterMessage() {
        return messages.getString("messages.drag_item_to_center", "§c请直接拖动物品到中央格子，不要使用Shift+点击");
    }
    
    /**
     * 获取不能向界面拖拽物品消息
     * @return 不能向界面拖拽物品消息
     */
    public String getCantDragToInventoryMessage() {
        return messages.getString("messages.cant_drag_to_inventory", "§c不能向过期物品界面拖拽物品！");
    }
    
    /**
     * 获取上架失败：缺少价格信息消息
     * @return 上架失败：缺少价格信息消息
     */
    public String getListingFailedNoPriceMessage() {
        return messages.getString("messages.listing_failed_no_price", "§c上架失败：缺少价格信息");
    }
    
    /**
     * 获取上架失败：价格信息不完整消息
     * @return 上架失败：价格信息不完整消息
     */
    public String getListingFailedIncompletePriceMessage() {
        return messages.getString("messages.listing_failed_incomplete_price", "§c上架失败：价格信息不完整");
    }
    
    /**
     * 获取上架失败：价格格式错误消息
     * @return 上架失败：价格格式错误消息
     */
    public String getListingFailedPriceFormatMessage() {
        return messages.getString("messages.listing_failed_price_format", "§c上架失败：价格格式错误");
    }
    
    /**
     * 获取起拍价必须大于0消息
     * @return 起拍价必须大于0消息
     */
    public String getStartPriceGreaterThanZeroMessage() {
        return messages.getString("messages.start_price_greater_than_zero", "§c起拍价必须大于0!");
    }
    
    /**
     * 获取一口价必须大于起拍价消息
     * @return 一口价必须大于起拍价消息
     */
    public String getBuyNowGreaterThanStartMessage() {
        return messages.getString("messages.buy_now_greater_than_start", "§c一口价必须大于起拍价!");
    }
    
    /**
     * 获取没有足够资金支付手续费消息
     * @return 没有足够资金支付手续费消息
     */
    public String getNotEnoughMoneyForFeeMessage() {
        return messages.getString("messages.not_enough_money_for_fee", "§c你没有足够的%currency%支付手续费!");
    }
    
    /**
     * 获取物品上架成功消息
     * @return 物品上架成功消息
     */
    public String getItemListedSuccessMessage() {
        return messages.getString("messages.item_listed_success", "§a物品上架成功!");
    }
    
    /**
     * 获取物品上架成功并收取手续费消息
     * @return 物品上架成功并收取手续费消息
     */
    public String getItemListedSuccessWithFeeMessage() {
        return messages.getString("messages.item_listed_success_with_fee", "§a物品上架成功! §e已收取手续费: §f%fee%");
    }
    
    /**
     * 获取物品上架失败消息
     * @return 物品上架失败消息
     */
    public String getItemListedFailMessage() {
        return messages.getString("messages.item_listed_fail", "§c物品上架失败!");
    }
    
    /**
     * 获取上架过程中发生错误消息
     * @return 上架过程中发生错误消息
     */
    public String getListingErrorMessage() {
        return messages.getString("messages.listing_error", "§c上架过程中发生错误");
    }
    
    /**
     * 获取物品已返还到背包的消息
     * @return 物品返还消息
     */
    public String getItemReturnedMessage() {
        return messages.getString("messages.item_returned", "§e物品已返还到你的背包");
    }
    
    /**
     * 获取背包已满物品掉落的消息
     * @return 背包已满物品掉落消息
     */
    public String getInventoryFullDropItemMessage() {
        return messages.getString("messages.inventory_full_drop_item", "§e背包已满，物品已掉落在你的脚下");
    }
    
    /**
     * 获取达到最大上架数量限制消息
     * @return 达到最大上架数量限制消息
     */
    public String getMaxListingsReachedMessage() {
        return messages.getString("messages.max_listings_reached", "§c你已达到最大上架数量限制 (%max_listings% 个物品)!");
    }
    
    /**
     * 获取等待已上架物品售出或过期消息
     * @return 等待已上架物品售出或过期消息
     */
    public String getWaitForItemsToSellMessage() {
        return messages.getString("messages.wait_for_items_to_sell", "§c请等待已上架物品售出或过期后再尝试上架新物品。");
    }
    
    /**
     * 获取已取消竞价操作消息
     * @return 已取消竞价操作消息
     */
    public String getCancelBidOperationMessage() {
        return messages.getString("messages.cancel_bid_operation", "§c已取消竞价操作");
    }
    
    /**
     * 获取竞价信息已过期消息
     * @return 竞价信息已过期消息
     */
    public String getBidInfoExpiredMessage() {
        return messages.getString("messages.bid_info_expired", "§c竞价信息已过期，请重新选择物品");
    }

    /**
     * 获取确认上架按钮描述
     * @return 确认上架按钮描述
     */
    public String getConfirmSellButtonDescription() {
        return messages.getString("gui.buttons.confirm_sell_description", "点击确认上架物品");
    }
    
    /**
     * 获取输入物品名称描述
     * @return 输入物品名称描述
     */
    public String getEnterItemNameDescription() {
        return messages.getString("gui.buttons.enter_item_name_description", "点击此处输入搜索关键词");
    }

    /**
     * 获取搜索前缀文本
     * @return 搜索前缀文本
     */
    public String getSearchPrefixText() {
        return messages.getString("gui.buttons.search_prefix", "搜索: ");
    }
    
    /**
     * 获取搜索历史项描述
     * @return 搜索历史项描述
     */
    public String getSearchHistoryItemDescription() {
        return messages.getString("gui.buttons.search_history_item_description", "点击以使用该关键词搜索");
    }
    
    /**
     * 获取清除搜索历史描述
     * @return 清除搜索历史描述
     */
    public String getClearSearchHistoryDescription() {
        return messages.getString("gui.buttons.clear_search_history_description", "点击清除所有搜索历史");
    }
} 