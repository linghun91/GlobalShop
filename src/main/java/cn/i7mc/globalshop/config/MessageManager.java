package cn.i7mc.globalshop.config;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

        // 从config.yml获取语言设置
        String locale = plugin.getConfig().getString("language.locale", "zh_CN");

        // 根据语言设置选择对应的消息文件
        // zh_CN使用message.yml，其他语言使用message_xx.yml
        String messageFileName = "message.yml";
        if (!"zh_CN".equals(locale)) {
            messageFileName = "message_" + locale + ".yml";
            // 检查lang目录是否存在
            File langDir = new File(plugin.getDataFolder(), "lang");
            if (langDir.exists() && langDir.isDirectory()) {
                // 尝试从lang目录加载语言文件
                File localeFile = new File(langDir, messageFileName);
                if (localeFile.exists()) {
                    messagesFile = localeFile;
                    messages = YamlConfiguration.loadConfiguration(messagesFile);
                    return;
                }
            }
        }

        // 如果是中文或找不到特定语言文件，则使用默认的message.yml
        messagesFile = new File(plugin.getDataFolder(), messageFileName);
        if (!messagesFile.exists()) {
            if ("zh_CN".equals(locale)) {
                // 对于默认的中文，使用插件内置的message.yml
                plugin.saveResource("message.yml", false);
            } else {
                // 对于其他未找到的语言，使用message.yml作为后备，并记录警告
                messagesFile = new File(plugin.getDataFolder(), "message.yml");
                if (!messagesFile.exists()) {
                    plugin.saveResource("message.yml", false);
                }
            }
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
        }
    }

    /**
     * 重新加载消息配置文件
     */
    public void reloadMessages() {
        // 重新从插件获取最新的配置信息
        plugin.reloadConfig();

        // 重新加载消息配置
        loadMessages();

        // 记录日志，表明消息已重新加载
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("已重新加载消息配置文件");
        }
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
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons." + key, defaultText));
    }

    /**
     * 获取上一页按钮文本
     * @return 上一页按钮文本
     */
    public String getPreviousPageText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.previous_page", "&e上一页"));
    }

    /**
     * 获取上一页按钮描述文本
     * @return 上一页按钮描述文本
     */
    public String getPreviousPageDescText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.previous_page_desc", "§7点击查看上一页"));
    }

    /**
     * 获取下一页按钮文本
     * @return 下一页按钮文本
     */
    public String getNextPageText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.next_page", "&e下一页"));
    }

    /**
     * 获取下一页按钮描述文本
     * @return 下一页按钮描述文本
     */
    public String getNextPageDescText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.next_page_desc", "§7点击查看下一页"));
    }

    /**
     * 获取返回主菜单按钮文本
     * @return 返回主菜单按钮文本
     */
    public String getReturnMainMenuText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.return_main_menu", "&c返回主菜单"));
    }

    /**
     * 获取返回主菜单按钮描述文本
     * @return 返回主菜单按钮描述文本
     */
    public String getReturnMainMenuDescText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.return_main_menu_desc", "§7点击返回主菜单"));
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
     * 快捷方法获取刷新页面按钮文本
     * @return 刷新页面按钮文本
     */
    public String getRefreshPageText() {
        return getButtonText("refresh_page", "§e刷新页面");
    }

    /**
     * 快捷方法获取刷新页面按钮描述文本
     * @return 刷新页面按钮描述文本
     */
    public String getRefreshPageDescText() {
        return getButtonText("refresh_page_desc", "§7点击刷新当前页面数据");
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
     * 获取加价按钮文本，根据传入的加价率替换%rate%占位符
     * @param rate 加价率百分比，例如5.0表示5%
     * @return 替换了占位符的加价按钮文本
     */
    public String getIncreaseBidText(double rate) {
        String text = getButtonText("increase_bid", "§a加价 +%rate%%");
        return text.replace("%rate%", String.format("%.0f", rate));
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
     * 快捷方法获取已购买按钮文本
     * @return 已购买按钮文本
     */
    public String getPurchasedItemsText() {
        return getButtonText("purchased_items", "§b已购买");
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
        return getButtonText("place_item_here", "§a请将物品放在这里");
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
        return getButtonText("enter_item_name", "§a输入物品名称");
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
     * 获取新搜索按钮文本
     * @return 新搜索按钮文本
     */
    public String getNewSearchText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.new_search", "§a新搜索"));
    }

    /**
     * 获取新搜索按钮描述文本
     * @return 新搜索按钮描述文本
     */
    public String getNewSearchDescText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.new_search_desc", "§7点击执行新搜索"));
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
        return messages.getString("gui.titles.sell_menu", "上架物品");
    }

    /**
     * 获取搜索物品界面标题
     * @return 搜索物品界面标题
     */
    public String getSearchMenuTitle() {
        return messages.getString("gui.titles.search_menu", "搜索物品");
    }

    /**
     * 获取确认购买界面标题
     * @return 确认购买界面标题
     */
    public String getConfirmBuyMenuTitle() {
        return messages.getString("gui.titles.confirm_buy_menu", "确认购买");
    }

    /**
     * 获取竞价购买界面标题
     * @return 竞价购买界面标题
     */
    public String getBidMenuTitle() {
        return messages.getString("gui.titles.bid_menu", "竞价购买");
    }

    /**
     * 获取我的拍卖界面标题
     * @return 我的拍卖界面标题
     */
    public String getMyAuctionsMenuTitle() {
        return messages.getString("gui.titles.my_auctions_menu", "我的拍卖");
    }

    /**
     * 获取我的已售出拍卖界面标题
     * @return 我的已售出拍卖界面标题
     */
    public String getMySoldAuctionsMenuTitle() {
        return messages.getString("gui.titles.my_sold_auctions_menu", "我的已售出拍卖");
    }

    /**
     * 获取我的已购买拍卖界面标题
     * @return 我的已购买拍卖界面标题
     */
    public String getMyPurchasedAuctionsMenuTitle() {
        return messages.getString("gui.buttons.my_purchased_auctions_title", "§b我的已购买拍卖");
    }

    /**
     * 获取物品邮箱界面标题
     * @return 物品邮箱界面标题
     */
    public String getMailboxMenuTitle() {
        return messages.getString("gui.titles.mailbox_menu", "§8物品邮箱");
    }

    /**
     * 获取我的物品邮箱界面标题前缀
     * @return 我的物品邮箱界面标题前缀
     */
    public String getMyMailboxMenuTitlePrefix() {
        return messages.getString("gui.titles.my_mailbox_menu_prefix", "§e我的物品邮箱");
    }

    /**
     * 获取过期拍卖界面标题
     * @return 过期拍卖界面标题
     */
    public String getExpiredAuctionsMenuTitle() {
        return messages.getString("gui.titles.expired_auctions_menu", "我的过期拍卖（无人出价）");
    }

    /**
     * 获取取消搜索文本
     * @return 取消搜索文本
     */
    public String getCancelSearchText() {
        return messages.getString("gui.buttons.cancel_search", "取消");
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
     * 获取价格超出最大限制消息
     * @return 价格超出最大限制消息
     */
    public String getPriceExceedsMaxLimitMessage() {
        return messages.getString("messages.price_exceeds_max_limit", "§c上架失败：价格超出最大限制(%max_digits%位数)!");
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
        return messages.getString("messages.buy_now_greater_than_start", "§c一口价不能小于起拍价!");
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
        return messages.getString("gui_listener.cancel_bid_operation", "§c已取消竞价操作");
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
     * 获取带颜色的确认上架按钮描述
     * @return 带颜色的确认上架按钮描述
     */
    public String getConfirmSellButtonDescriptionWithColor() {
        String color = messages.getString("gui.buttons.confirm_sell_description_color", "§7");
        return color + getConfirmSellButtonDescription();
    }

    /**
     * 获取输入物品名称描述
     * @return 输入物品名称描述
     */
    public String getEnterItemNameDescription() {
        return messages.getString("gui.buttons.enter_item_name_description", "§e点击此处输入搜索关键词");
    }

    /**
     * 获取搜索前缀文本
     * @return 搜索前缀文本
     */
    public String getSearchPrefixText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.search_prefix", "搜索: "));
    }

    /**
     * 获取搜索历史条目描述文本
     * @return 搜索历史条目描述文本
     */
    public String getSearchHistoryItemDescription() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.search_history_item_description", "点击以使用该关键词搜索"));
    }

    /**
     * 获取清除搜索历史描述文本
     * @return 清除搜索历史描述文本
     */
    public String getClearSearchHistoryDescription() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.clear_search_history_description", "点击清除所有搜索历史"));
    }

    /**
     * 获取搜索历史界面标题
     * @return 搜索历史界面标题
     */
    public String getSearchHistoryMenuTitle() {
        return messages.getString("gui.titles.search_history_menu", "搜索历史记录");
    }

    /**
     * 获取搜索历史返回按钮描述文本
     * @return 搜索历史返回按钮描述文本
     */
    public String getSearchHistoryReturnText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.search_history_return", "§7点击返回搜索界面"));
    }

    /**
     * 获取搜索卖家按钮文本
     * @return 搜索卖家按钮文本
     */
    public String getSellerSearchText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.seller_search", "§a搜索卖家"));
    }

    /**
     * 获取搜索卖家按钮描述文本
     * @return 搜索卖家按钮描述文本
     */
    public String getSellerSearchDescText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.seller_search_desc", "§7点击搜索特定卖家的物品"));
    }

    /**
     * 获取输入卖家搜索关键词消息
     * @return 输入卖家搜索关键词消息
     */
    public String getEnterSellerSearchMessage() {
        return messages.getString("messages.enter_seller_search", "§a请在聊天栏输入要搜索的卖家名称，或者输入 §e取消 §a来取消搜索");
    }

    /**
     * 获取卖家前缀文本
     * @return 卖家前缀文本
     */
    public String getSellerSearchPrefixText() {
        return messages.getString("messages.seller_search_prefix", "卖家:");
    }

    /**
     * 获取卖家物品后缀文本
     * @return 卖家物品后缀文本
     */
    public String getSellerItemsSuffixText() {
        return messages.getString("gui.buttons.seller_items_suffix", "的物品");
    }

    /**
     * 获取我的已售出拍卖标题
     * @return 我的已售出拍卖标题
     */
    public String getMySoldAuctionsTitle() {
        return messages.getString("gui.buttons.my_sold_auctions_title", "§6我的已售出拍卖");
    }

    /**
     * 获取页面信息前缀
     * @return 页面信息前缀
     */
    public String getPageInfoPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.page_info_prefix", "§e当前页: §f"));
    }

    /**
     * 获取已售出物品数量前缀
     * @return 已售出物品数量前缀
     */
    public String getSoldItemsCountPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.sold_items_count_prefix", "§e已售出: §f"));
    }

    /**
     * 获取总售出物品数量前缀
     * @return 总售出物品数量前缀
     */
    public String getTotalSoldItemsCountPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_sold_items_count_prefix", "§e总计售出物品: §f"));
    }

    /**
     * 获取总计赚取金币前缀
     * @return 总计赚取金币前缀
     */
    public String getTotalCoinsEarnedPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_coins_earned_prefix", "§e总计赚取金币: §f"));
    }

    /**
     * 获取总计赚取点券前缀
     * @return 总计赚取点券前缀
     */
    public String getTotalPointsEarnedPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_points_earned_prefix", "§e总计赚取点券: §f"));
    }

    /**
     * 获取已购买物品数量前缀
     * @return 已购买物品数量前缀
     */
    public String getPurchasedItemsCountPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.purchased_items_count_prefix", "§e已购买: §f"));
    }

    /**
     * 获取总计购买物品数量前缀
     * @return 总计购买物品数量前缀
     */
    public String getTotalPurchasedItemsCountPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_purchased_items_count_prefix", "§e总计购买物品: §f"));
    }

    /**
     * 获取总计花费金币前缀
     * @return 总计花费金币前缀
     */
    public String getTotalCoinsSpentPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_coins_spent_prefix", "§e总计花费金币: §f"));
    }

    /**
     * 获取总计花费点券前缀
     * @return 总计花费点券前缀
     */
    public String getTotalPointsSpentPrefix() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.total_points_spent_prefix", "§e总计花费点券: §f"));
    }

    /**
     * 获取物品邮箱描述第一行文本
     * @return 物品邮箱描述第一行文本
     */
    public String getMailboxDescriptionLine1() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_description_line1", "§7查看你的物品邮箱"));
    }

    /**
     * 获取物品邮箱描述第二行文本
     * @return 物品邮箱描述第二行文本
     */
    public String getMailboxDescriptionLine2() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_description_line2", "§7包含过期、竞拍和背包已满的物品"));
    }

    /**
     * 获取拍卖物品分隔线
     * @return 拍卖物品分隔线
     */
    public String getAuctionItemDivider() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_item.divider", "§8§m--------------------"));
    }

    /**
     * 获取拍卖信息标题
     * @return 拍卖信息标题
     */
    public String getAuctionInfoHeader() {
        return messages.getString("auction_item.info_header", "§6§l拍卖信息:");
    }

    /**
     * 获取物品ID格式
     * @return 物品ID格式
     */
    public String getAuctionItemIdFormat() {
        return messages.getString("auction_item.item_id", "§e物品ID: §f%id%");
    }

    /**
     * 获取货币类型格式
     * @return 货币类型格式
     */
    public String getAuctionCurrencyTypeFormat() {
        return messages.getString("auction_item.currency_type", "§e货币类型: §f%currency%");
    }

    /**
     * 获取起拍价格式
     * @return 起拍价格式
     */
    public String getAuctionStartPriceFormat() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_item.start_price", "§e起拍价: §f%price%"));
    }

    /**
     * 获取当前价格格式
     * @return 当前价格格式
     */
    public String getAuctionCurrentPriceFormat() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_item.current_price", "§e当前价格: §f%price%"));
    }

    /**
     * 获取成交价格格式
     * @return 成交价格格式
     */
    public String getAuctionDealPriceFormat() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_item.deal_price", "§e成交价格: §f%price%"));
    }

    /**
     * 获取一口价格式
     * @return 一口价格式
     */
    public String getAuctionBuyNowPriceFormat() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_item.buy_now_price", "§e一口价: §f%price%"));
    }

    /**
     * 获取上架时间格式
     * @return 上架时间格式
     */
    public String getAuctionListTimeFormat() {
        return messages.getString("auction_item.list_time", "§e上架时间: §f%time%");
    }

    /**
     * 获取剩余时间格式
     * @return 剩余时间格式
     */
    public String getAuctionRemainingTimeFormat() {
        return messages.getString("auction_item.remaining_time", "§e剩余时间: §f%time%");
    }

    /**
     * 获取卖家格式
     * @return 卖家格式
     */
    public String getAuctionSellerFormat() {
        return messages.getString("auction_item.seller", "§e卖家: §f%seller%");
    }

    /**
     * 获取当前出价者格式
     * @return 当前出价者格式
     */
    public String getAuctionCurrentBidderFormat() {
        return messages.getString("auction_item.current_bidder", "§e当前出价者: §f%bidder%");
    }

    /**
     * 获取物品所有者提示标题
     * @return 物品所有者提示标题
     */
    public String getAuctionOwnerTipsHeader() {
        return messages.getString("auction_item.owner_tips.header", "§7这是你的拍卖物品");
    }

    /**
     * 获取物品所有者取消提示
     * @return 物品所有者取消提示
     */
    public String getAuctionOwnerCancelTip() {
        return messages.getString("auction_item.owner_tips.cancel", "§7Shift+右键点击: §f快速下架");
    }

    /**
     * 获取买家竞价提示
     * @return 买家竞价提示
     */
    public String getAuctionBuyerBidTip() {
        return messages.getString("auction_item.buyer_tips.bid", "§7左键点击: §f参与竞价");
    }

    /**
     * 获取买家购买提示
     * @return 买家购买提示
     */
    public String getAuctionBuyerBuyTip() {
        return messages.getString("auction_item.buyer_tips.buy", "§7右键点击: §f快速购买");
    }

    /**
     * 获取管理员操作标题
     * @return 管理员操作标题
     */
    public String getAuctionAdminTipsHeader() {
        return messages.getString("auction_item.admin_tips.header", "§c§l管理员操作:");
    }

    /**
     * 获取管理员强制下架提示
     * @return 管理员强制下架提示
     */
    public String getAuctionAdminForceCancelTip() {
        return messages.getString("auction_item.admin_tips.force_cancel", "§cShift+左键点击: §f强制下架该物品");
    }

    /**
     * 获取命令提示标题
     * @return 命令提示标题
     */
    public String getSellMenuCommandTipsHeader() {
        return messages.getString("sell_menu.command_tips.header", "§7手持要上架的物品输入:");
    }

    /**
     * 获取命令用法提示
     * @return 命令用法提示
     */
    public String getSellMenuCommandUsage() {
        return messages.getString("sell_menu.command_tips.usage", "§7/auction sell <起拍价> [一口价] [货币类型]");
    }

    /**
     * 获取货币类型提示
     * @return 货币类型提示
     */
    public String getSellMenuCurrencyTypes() {
        return messages.getString("sell_menu.command_tips.currency_types", "§7货币类型: §f1=金币, 2=点券");
    }

    /**
     * 获取自定义上架时间按钮标题
     * @return 自定义上架时间按钮标题
     */
    public String getDurationButtonTitle() {
        return messages.getString("sell_menu.duration_button.title", "§a自定义上架时间");
    }

    /**
     * 获取当前设置时间格式
     * @return 当前设置时间格式
     */
    public String getDurationCurrentSettingFormat() {
        return messages.getString("sell_menu.duration_button.current_setting", "§7当前设置: §f%hours%小时 %minutes%分钟");
    }

    /**
     * 获取上架时间按钮分隔线
     * @return 上架时间按钮分隔线
     */
    public String getDurationButtonDivider() {
        return messages.getString("sell_menu.duration_button.divider", "§8§m--------------------");
    }

    /**
     * 获取左键点击提示
     * @return 左键点击提示
     */
    public String getDurationLeftClickTip() {
        return messages.getString("sell_menu.duration_button.left_click", "§7左键点击: §f+1分钟");
    }

    /**
     * 获取右键点击提示
     * @return 右键点击提示
     */
    public String getDurationRightClickTip() {
        return messages.getString("sell_menu.duration_button.right_click", "§7右键点击: §f+1小时");
    }

    /**
     * 获取Shift+左键点击提示
     * @return Shift+左键点击提示
     */
    public String getDurationShiftLeftClickTip() {
        return messages.getString("sell_menu.duration_button.shift_left_click", "§7Shift+左键: §f+10分钟");
    }

    /**
     * 获取Shift+右键点击提示
     * @return Shift+右键点击提示
     */
    public String getDurationShiftRightClickTip() {
        return messages.getString("sell_menu.duration_button.shift_right_click", "§7Shift+右键: §f+10小时");
    }

    /**
     * 获取中键点击提示
     * @return 中键点击提示
     */
    public String getDurationMiddleClickTip() {
        return messages.getString("sell_menu.duration_button.middle_click", "§7中键或Q键: §f重置为最小时间");
    }

    /**
     * 获取最短时间提示格式
     * @return 最短时间提示格式
     */
    public String getDurationMinTimeFormat() {
        return messages.getString("sell_menu.duration_button.min_time", "§7最短时间: §f%hours%小时");
    }

    /**
     * 获取最长时间提示格式
     * @return 最长时间提示格式
     */
    public String getDurationMaxTimeFormat() {
        return messages.getString("sell_menu.duration_button.max_time", "§7最长时间: §f%hours%小时");
    }

    /**
     * 获取时间设置注意事项1
     * @return 时间设置注意事项1
     */
    public String getDurationNote1() {
        return messages.getString("sell_menu.duration_button.note1", "§e注意: §f时间设置仅在确认上架后生效");
    }

    /**
     * 获取时间设置注意事项2
     * @return 时间设置注意事项2
     */
    public String getDurationNote2() {
        return messages.getString("sell_menu.duration_button.note2", "§e       §f使用命令上架时无效");
    }

    /**
     * 获取详细信息按钮文本
     * @return 详细信息按钮文本
     */
    public String getDetailsButtonText() {
        return messages.getString("broadcast.details_button", " §6[§e详细信息§6]");
    }

    /**
     * 获取无详细信息提示
     * @return 无详细信息提示
     */
    public String getNoDetailsAvailableText() {
        return messages.getString("broadcast.no_details_available", "§c无详细信息");
    }

    /**
     * 获取页码分隔符格式
     * @return 页码分隔符格式
     */
    public String getPageSeparatorFormat() {
        return messages.getString("format.page_separator", " §7- 第 %page% 页");
    }

    /**
     * 获取物品名称颜色代码
     * @return 物品名称颜色代码，默认为白色
     */
    public String getItemNameColor() {
        return messages.getString("item-name-color", "§f");
    }

    /**
     * 安装语言文件 - 将所有可用的语言文件从JAR复制到插件lang目录
     */
    public void installLanguageFiles() {
        // 创建语言文件目录
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // 支持的所有语言列表
        String[] languages = {
            "en", "es", "de", "ru", "pt", "ja", "ar", "hi", "it", "vi"
        };

        // 复制每个语言文件
        for (String lang : languages) {
            String fileName = "message_" + lang + ".yml";
            File langFile = new File(langDir, fileName);

            // 只有在文件不存在时才复制，避免覆盖用户修改的配置
            if (!langFile.exists()) {
                try {
                    // 检查resources/lang目录中是否存在该语言文件
                    if (plugin.getResource("lang/" + fileName) != null) {
                        // 从JAR复制语言文件
                        plugin.saveResource("lang/" + fileName, false);
                        plugin.getLogger().info("已安装语言文件: " + fileName);
                    } else {
                        // 从插件目录中复制语言文件
                        File sourceFile = new File(plugin.getDataFolder(), "lang/" + fileName);
                        if (sourceFile.exists()) {
                            // 如果本地已有该文件，则记录信息
                            plugin.getLogger().info("使用本地语言文件: " + fileName);
                        } else {
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        // 创建语言说明文件
        File readmeFile = new File(langDir, "LANG.md");
        if (!readmeFile.exists()) {
            try {
                if (plugin.getResource("lang/LANG.md") != null) {
                    plugin.saveResource("lang/LANG.md", false);
                    plugin.getLogger().info("已安装语言说明文件: LANG.md");
                }
            } catch (Exception e) {
            }
        }
    }

    // 新增邮箱页面信息文本方法
    public String getMailboxPageInfo() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_page_info", "§e当前页: §f"));
    }

    public String getMailboxItemsCount() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_items_count", "§e物品数量: §f"));
    }

    public String getMailboxStorageHeader() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_storage_header", "§7这里存储:"));
    }

    public String getMailboxStorageExpired() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_storage_expired", "§7- 过期未售出的物品"));
    }

    public String getMailboxStorageWon() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_storage_won", "§7- 竞拍成功的物品"));
    }

    public String getMailboxStorageFull() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_storage_full", "§7- 背包已满时购买的物品"));
    }

    public String getMailboxItemStatusPending() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_status_pending", "§e状态: §f待领取物品"));
    }

    public String getMailboxItemStatusExpired() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_status_expired", "§e状态: §f过期未售出"));
    }

    public String getMailboxItemStatusOther() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_status_other", "§e状态: §f"));
    }

    public String getMailboxItemAddTime() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_add_time", "§e添加时间: §f"));
    }

    public String getMailboxItemExpireTime() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_expire_time", "§e过期时间: §f"));
    }

    public String getMailboxItemCollectTip() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("gui.buttons.mailbox_item_collect_tip", "§7右键点击领取物品"));
    }

    // 竞价相关命令消息
    public String getBidUsage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_usage", "§c用法: /bid <物品ID>"));
    }

    public String getBidIdMustBeNumber() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_id_must_be_number", "§c物品ID必须是一个数字!"));
    }

    public String getBidInvalidItem() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_invalid_item", "§c找不到有效的拍卖物品，ID: "));
    }

    public String getBidNoPermission() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_no_permission", "§c你没有权限参与竞价!"));
    }

    public String getBidOwnItem() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_own_item", "§c你不能竞拍自己的物品!"));
    }

    public String getBidAlreadyHighest() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_already_highest", "§c你已经是当前最高出价者!"));
    }

    public String getBidOpeningMenu() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_opening_menu", "§a正在打开竞价界面..."));
    }

    public String getBidError() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_error", "§c竞价过程中发生错误: "));
    }

    // 一口价购买相关消息
    public String getBuyNowNotAvailable() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_not_available", "§c该物品不支持一口价购买!"));
    }

    public String getBuyNowPointsUnavailable() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_points_unavailable", "§c点券支付功能不可用!"));
    }

    public String getBuyNowNotEnoughMoney() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_not_enough_money", "§c你没有足够的%currency%来购买该物品! 需要: %amount%"));
    }

    public String getBuyNowInventoryFull() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_inventory_full", "§c你的背包已满，无法购买物品!"));
    }

    public String getBuyNowSuccess() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_success", "§a购买成功!"));
    }

    public String getBuyNowItem() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_item", "§e物品: %item%"));
    }

    public String getBuyNowPrice() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_price", "§e价格: %price%"));
    }

    public String getBuyNowBalance() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_balance", "§e余额: %amount% %currency%"));
    }

    public String getBuyNowInventoryFullMailbox() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_inventory_full_mailbox", "§a你的背包已满，物品已放入邮箱!"));
    }

    public String getBuyNowMailboxInstructions() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_mailbox_instructions", "§e使用 /auction my 进入拍卖系统，点击\"物品邮箱\"领取"));
    }

    public String getBuyNowSellerNotification() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_seller_notification", "§a你的物品已被一口价购买!"));
    }

    public String getBuyNowSellerIncome() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_seller_income", "§e收入: %amount%"));
    }

    public String getBuyNowSellerBalance() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_seller_balance", "§e余额: %amount% %currency%"));
    }

    public String getBuyNowBuyer() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.buy_now_buyer", "§e购买者: %player%"));
    }

    /**
     * 获取确认购买提示文本
     * @return 确认购买提示文本
     */
    public String getConfirmBuyTipText() {
        return messages.getString("gui.buttons.confirm_buy_tip", "§e点击确认购买此物品");
    }

    /**
     * 获取物品名称格式文本
     * @return 物品名称格式文本
     */
    public String getConfirmBuyItemText() {
        return messages.getString("gui.buttons.confirm_buy_item", "§e物品名称: §f%item_name%");
    }

    /**
     * 获取购买价格格式文本
     * @return 购买价格格式文本
     */
    public String getConfirmBuyPriceText() {
        return messages.getString("gui.buttons.confirm_buy_price", "§e购买价格: §f%price% (一口价)");
    }

    /**
     * 获取取消购买提示文本
     * @return 取消购买提示文本
     */
    public String getCancelBuyTipText() {
        return messages.getString("gui.buttons.cancel_buy_tip", "§e点击取消购买");
    }

    /**
     * 获取竞价物品文本
     * @return 竞价物品文本
     */
    public String getCurrentBidItemText() {
        return messages.getString("bid_menu.item_text", "§7物品: §f%item_name%");
    }

    /**
     * 获取原始价格文本
     * @return 原始价格文本
     */
    public String getCurrentBidOriginalPriceText() {
        return messages.getString("bid_menu.original_price_text", "§7原始价格: §f%price%");
    }

    /**
     * 获取当前竞价文本
     * @return 当前竞价文本
     */
    public String getCurrentBidCurrentPriceText() {
        return messages.getString("bid_menu.current_price_text", "§7当前竞价: §f%price%");
    }

    /**
     * 获取加价幅度文本
     * @return 加价幅度文本
     */
    public String getCurrentBidRateText() {
        return messages.getString("bid_menu.rate_text", "§7加价幅度: §f+%rate%%");
    }

    /**
     * 获取点击增加竞价提示文本
     * @return 点击增加竞价提示文本
     */
    public String getIncreaseBidTipText() {
        return messages.getString("bid_menu.increase_tip", "§7点击增加你的竞价金额");
    }

    /**
     * 获取最小加价文本
     * @return 最小加价文本
     */
    public String getIncreaseBidMinText() {
        return messages.getString("bid_menu.min_increase_text", "§7最小加价: §f%price%");
    }

    /**
     * 获取确认竞价提示文本
     * @return 确认竞价提示文本
     */
    public String getConfirmBidTipText() {
        return messages.getString("bid_menu.confirm_tip", "§7点击确认当前竞价金额");
    }

    /**
     * 获取取消竞价提示文本
     * @return 取消竞价提示文本
     */
    public String getCancelBidTipText() {
        return messages.getString("gui.buttons.cancel_bid_tip", "§7点击取消竞价");
    }

    /**
     * 获取取消竞价返回提示文本
     * @return 取消竞价返回提示文本
     */
    public String getCancelBidReturnText() {
        return messages.getString("gui.buttons.cancel_bid_return", "§7并返回主菜单");
    }

    /**
     * 获取我的拍卖起拍价格式
     * @return 我的拍卖起拍价格式
     */
    public String getMyAuctionStartPriceText() {
        return messages.getString("auction_item.my_auction_start_price", "§e起拍价: §f%price%");
    }

    /**
     * 获取我的拍卖当前价格式
     * @return 我的拍卖当前价格式
     */
    public String getMyAuctionCurrentPriceText() {
        return messages.getString("auction_item.my_auction_current_price", "§e当前价: §f%price%");
    }

    /**
     * 获取我的拍卖一口价格式
     * @return 我的拍卖一口价格式
     */
    public String getMyAuctionBuyNowPriceText() {
        return messages.getString("auction_item.my_auction_buy_now_price", "§e一口价: §f%price%");
    }

    /**
     * 获取我的拍卖剩余时间格式
     * @return 我的拍卖剩余时间格式
     */
    public String getMyAuctionRemainingTimeText() {
        return messages.getString("auction_item.my_auction_remaining_time", "§e剩余时间: §f%time%");
    }

    /**
     * 获取我的拍卖当前出价者格式
     * @return 我的拍卖当前出价者格式
     */
    public String getMyAuctionCurrentBidderText() {
        return messages.getString("auction_item.my_auction_current_bidder", "§e当前出价者: §f%bidder%");
    }

    /**
     * 获取已有人出价警告1文本
     * @return 已有人出价警告1文本
     */
    public String getMyAuctionHasBidderWarning1Text() {
        return messages.getString("auction_item.my_auction_has_bidder_warning1", "§c⚠ 已有人出价，无法取消拍卖 ⚠");
    }

    /**
     * 获取已有人出价警告2文本
     * @return 已有人出价警告2文本
     */
    public String getMyAuctionHasBidderWarning2Text() {
        return messages.getString("auction_item.my_auction_has_bidder_warning2", "§c请等待拍卖结束");
    }

    /**
     * 获取取消拍卖提示文本
     * @return 取消拍卖提示文本
     */
    public String getMyAuctionCancelTipText() {
        return messages.getString("auction_item.my_auction_cancel_tip", "§e右键点击取消拍卖");
    }

    /**
     * 获取已售出物品成交价格格式
     * @return 已售出物品成交价格格式
     */
    public String getSoldItemDealPriceText() {
        return messages.getString("auction_item.sold_item_deal_price", "§e成交价格: §f%price%");
    }

    /**
     * 获取已售出物品买家格式
     * @return 已售出物品买家格式
     */
    public String getSoldItemBuyerText() {
        return messages.getString("auction_item.sold_item_buyer", "§e购买者: §f%buyer%");
    }

    /**
     * 获取已售出物品卖家格式
     * @return 已售出物品卖家格式
     */
    public String getSoldItemSellerText() {
        return messages.getString("auction_item.sold_item_seller", "§e卖家: §f%seller%");
    }

    /**
     * 获取已售出物品售出时间格式
     * @return 已售出物品售出时间格式
     */
    public String getSoldItemSoldTimeText() {
        return messages.getString("auction_item.sold_item_sold_time", "§e售出时间: §f%time%");
    }

    /**
     * 获取已售出物品提示1文本
     * @return 已售出物品提示1文本
     */
    public String getSoldItemNotice1Text() {
        return messages.getString("auction_item.sold_item_notice1", "§c此物品已售出，仅供查看");
    }

    /**
     * 获取已售出物品提示2文本
     * @return 已售出物品提示2文本
     */
    public String getSoldItemNotice2Text() {
        return messages.getString("auction_item.sold_item_notice2", "§c无法取回或再次出售");
    }

    /**
     * 获取我的拍卖页面信息格式
     * @return 我的拍卖页面信息格式
     */
    public String getMyAuctionPageInfoText() {
        return messages.getString("my_auction.page_info", "§e当前页: §f%page%/%total_pages%");
    }

    /**
     * 获取我的拍卖已售出数量格式
     * @return 我的拍卖已售出数量格式
     */
    public String getMyAuctionSoldCountText() {
        return messages.getString("my_auction.sold_count", "§e已售出: §f%count%");
    }

    /**
     * 获取我的拍卖已过期数量格式
     * @return 我的拍卖已过期数量格式
     */
    public String getMyAuctionExpiredCountText() {
        return messages.getString("my_auction.expired_count", "§e已过期: §f%count%");
    }

    /**
     * 获取我的拍卖上架数量格式
     * @return 我的拍卖上架数量格式
     */
    public String getMyAuctionListingsCountText() {
        return messages.getString("my_auction.listings_count", "§e上架数量: §f%current%/%max%");
    }

    /**
     * 获取我的拍卖上架数量接近上限警告文本
     * @return 我的拍卖上架数量接近上限警告文本
     */
    public String getMyAuctionLimitWarningText() {
        return messages.getString("my_auction.limit_warning", "§c⚠ 你的上架数量即将达到上限!");
    }

    /**
     * 获取邮箱物品过滤标签列表
     * @return 邮箱物品过滤标签列表
     */
    public List<String> getMailboxFilterTags() {
        return messages.getStringList("mailbox.mailbox_filter_tags");
    }

    /**
     * 获取邮箱过期物品标签列表
     * @return 邮箱过期物品标签列表
     */
    public List<String> getExpiredAuctionTags() {
        return messages.getStringList("mailbox.expired_auction_tags");
    }

    /**
     * 获取物品不存在或已被购买的错误消息
     * @return 物品不存在或已被购买的错误消息
     */
    public String getItemNotExistsErrorMessage() {
        return messages.getString("error_messages.item_not_exists", "§c物品不存在或已被购买!");
    }

    /**
     * 获取重置上架时间提示消息
     * @return 重置上架时间提示消息
     */
    public String getResetDurationMessage() {
        return messages.getString("gui_listener.reset_duration", "§e已重置上架时间为最小值！");
    }

    /**
     * 获取最大上架时间限制消息
     * @return 最大上架时间限制消息
     */
    public String getMaxDurationLimitMessage() {
        return messages.getString("gui_listener.max_duration_limit", "§c已达到最大上架时间限制！");
    }

    /**
     * 获取更新上架时间消息
     * @param hours 小时
     * @param minutes 分钟
     * @return 更新上架时间消息
     */
    public String getUpdateDurationMessage(long hours, long minutes) {
        return messages.getString("gui_listener.update_duration", "§a已更新上架时间为: §f%hours%小时 %minutes%分钟")
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes));
    }

    /**
     * 获取自定义时间低于最小限制消息
     * @return 自定义时间低于最小限制消息
     */
    public String getCustomDurationBelowMinLimitMessage() {
        return messages.getString("gui_listener.custom_duration_below_min", "§e自定义时间低于最小限制，已自动调整为最小限制值。");
    }

    /**
     * 获取自定义时间超过最大限制消息
     * @return 自定义时间超过最大限制消息
     */
    public String getCustomDurationAboveMaxLimitMessage() {
        return messages.getString("gui_listener.custom_duration_above_max", "§e自定义时间超过最大限制，已自动调整为最大限制值。");
    }

    /**
     * 获取设置自定义上架时间消息
     * @param hours 小时
     * @param minutes 分钟
     * @return 设置自定义上架时间消息
     */
    public String getCustomDurationSetMessage(long hours, long minutes) {
        return messages.getString("gui_listener.custom_duration_set", "§a物品已上架，上架时间设为: §f%hours%小时 %minutes%分钟")
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes));
    }

    /**
     * 获取物品已过期或已售出消息
     * @return 物品已过期或已售出消息
     */
    public String getExpiredItemMessage() {
        return messages.getString("gui_listener.expired_item", "§c该物品已结束拍卖!");
    }

    /**
     * 获取不能对自己物品出价消息
     * @return 不能对自己物品出价消息
     */
    public String getOwnerBidMessage() {
        return messages.getString("gui_listener.owner_bid", "§c你不能竞拍自己的物品!");
    }

    /**
     * 获取无权限消息
     * @return 无权限消息
     */
    public String getNoPermissionMessage() {
        return messages.getString("gui_listener.no_permission", "§c你没有权限执行此操作!");
    }

    /**
     * 获取已是最高出价者消息
     * @return 已是最高出价者消息
     */
    public String getAlreadyHighestBidderMessage() {
        return messages.getString("gui_listener.already_highest_bidder", "§e你已经是当前最高出价者，可以继续加价!");
    }

    /**
     * 获取不能购买自己物品消息
     * @return 不能购买自己物品消息
     */
    public String getOwnerBuyMessage() {
        return messages.getString("gui_listener.owner_buy", "§c你不能购买自己的物品!");
    }

    /**
     * 获取物品没有一口价消息
     * @return 物品没有一口价消息
     */
    public String getNoBuyNowPriceMessage() {
        return messages.getString("gui_listener.no_buy_now_price", "§c该物品没有设置一口价，不能直接购买!");
    }

    /**
     * 获取物品不存在或已被购买消息
     * @return 物品不存在或已被购买消息
     */
    public String getInvalidItemMessage() {
        return messages.getString("gui_listener.invalid_item", "§c物品不存在或已被购买!");
    }

    /**
     * 获取没有足够资金消息
     * @param currency 货币类型
     * @return 没有足够资金消息
     */
    public String getNotEnoughMoneyMessage(String currency) {
        return messages.getString("gui_listener.not_enough_money", "§c你没有足够的%currency%进行此操作!")
                .replace("%currency%", currency);
    }

    /**
     * 获取背包已满消息
     * @return 背包已满消息
     */
    public String getInventoryFullMessage() {
        return messages.getString("gui_listener.inventory_full", "§c你的背包已满，请先清理背包再购买物品!");
    }

    /**
     * 获取交易失败消息
     * @return 交易失败消息
     */
    public String getFailedTransactionMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.failed_transaction", "§c交易失败，请检查您的余额!"));
    }

    /**
     * 获取物品已被竞价购买消息
     * @param itemId 物品ID
     * @param price 价格
     * @return 物品已被竞价购买消息
     */
    public String getBidSoldMessage(int itemId, String price) {
        return messages.getString("gui_listener.bid_sold", "§e物品ID: %id% 已被一口价购买\n§e你的竞价 %price% 已退还")
                .replace("%id%", String.valueOf(itemId))
                .replace("%price%", price);
    }

    /**
     * 获取余额消息
     * @param currency 货币类型
     * @param balance 余额
     * @return 余额消息
     */
    public String getBalanceMessage(String currency, double balance) {
        return messages.getString("gui_listener.balance", "§a你的%currency%余额: §f%balance%")
                .replace("%currency%", currency)
                .replace("%balance%", formatPrice(balance));
    }

    /**
     * 获取物品已保存到待领取列表消息
     * @return 物品已保存到待领取列表消息
     */
    public String getPartialItemSavedMessage() {
        return messages.getString("gui_listener.partial_item_saved", "§e你的背包已满，部分物品已保存到待领取列表，使用 /ah collect 领取");
    }

    /**
     * 获取购买成功消息
     * @return 购买成功消息
     */
    public String getPurchaseSuccessMessage() {
        return messages.getString("gui_listener.purchase_success", "§a购买成功!");
    }

    /**
     * 获取购买物品消息
     * @param itemName 物品名称
     * @return 购买物品消息
     */
    public String getPurchaseItemMessage(String itemName) {
        return messages.getString("gui_listener.purchase_item", "§e物品: §f%item%")
                .replace("%item%", itemName);
    }

    /**
     * 获取购买价格消息
     * @param price 价格
     * @return 购买价格消息
     */
    public String getPurchasePriceMessage(String price) {
        return messages.getString("gui_listener.purchase_price", "§e价格: §f%price%")
                .replace("%price%", price);
    }

    /**
     * 获取物品售出通知消息
     * @param itemId 物品ID
     * @param price 价格
     * @return 物品售出通知消息
     */
    public String getSoldNotification(int itemId, String price) {
        return messages.getString("gui_listener.sold_notification", "§a你的物品已被购买!\n§e物品ID: §f%id%\n§e出售价格: §f%price%")
                .replace("%id%", String.valueOf(itemId))
                .replace("%price%", price);
    }

    /**
     * 获取卖家手续费消息
     * @param fee 手续费
     * @return 卖家手续费消息
     */
    public String getSellerFeeMessage(String fee) {
        return messages.getString("gui_listener.seller_fee", "§e手续费: §f%fee%")
                .replace("%fee%", fee);
    }

    /**
     * 获取卖家实际收入消息
     * @param income 实际收入
     * @return 卖家实际收入消息
     */
    public String getSellerIncomeMessage(String income) {
        return messages.getString("gui_listener.seller_income", "§e实际获得: §f%income%")
                .replace("%income%", income);
    }

    /**
     * 获取卖家余额消息
     * @param currency 货币类型
     * @param balance 余额
     * @return 卖家余额消息
     */
    public String getSellerBalanceMessage(String currency, double balance) {
        return getBalanceMessage(currency, balance);
    }

    /**
     * 获取买家信息消息
     * @param buyer 买家名称
     * @return 买家信息消息
     */
    public String getBuyerMessage(String buyer) {
        return messages.getString("gui_listener.buyer", "§e买家: §f%buyer%")
                .replace("%buyer%", buyer);
    }

    /**
     * 获取取消购买消息
     * @return 取消购买消息
     */
    public String getCancelPurchaseMessage() {
        return messages.getString("gui_listener.cancel_purchase", "§c已取消购买");
    }

    /**
     * 获取竞价信息已过期消息
     * @return 竞价信息已过期消息
     */
    public String getExpiredBidInfoMessage() {
        return messages.getString("gui_listener.expired_bid_info", "§c竞价信息已过期，请重新选择物品");
    }

    /**
     * 获取竞价低于最低要求消息
     * @param minBid 最低加价
     * @return 竞价低于最低要求消息
     */
    public String getBidBelowMinimumMessage(String minBid) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_below_minimum", "§c最低加价不能低于 %min_amount%!").replace("%min_amount%", minBid));
    }

    /**
     * 获取预加价消息
     * @param price 价格
     * @return 预加价消息
     */
    public String getPreBidMessage(String price) {
        return messages.getString("gui_listener.pre_bid", "§a已预加价! §e点击'确认竞价'按钮完成竞价，或点击'取消竞价'取消。");
    }

    /**
     * 获取竞价金额消息
     * @param amount 金额
     * @return 竞价金额消息
     */
    public String getBidAmountMessage(String amount) {
        return messages.getString("gui_listener.bid_amount", "§e预计竞价金额: §f%amount%")
                .replace("%amount%", amount);
    }

    /**
     * 获取竞价信息不完整消息
     * @return 竞价信息不完整消息
     */
    public String getIncompleteBidInfoMessage() {
        return messages.getString("gui_listener.incomplete_bid_info", "§c竞价信息不完整，请重新出价。");
    }

    /**
     * 获取点券系统不可用消息
     * @return 点券系统不可用消息
     */
    public String getPointsUnavailableMessage() {
        return messages.getString("gui_listener.points_unavailable", "§c点券系统不可用，无法对使用点券上架的物品出价。");
    }

    /**
     * 获取物品已过期无法出价消息
     * @return 物品已过期无法出价消息
     */
    public String getExpiredBidMessage() {
        return messages.getString("gui_listener.expired_bid", "§c该物品已过期，无法出价。");
    }

    /**
     * 获取竞价已被超越消息
     * @param itemId 物品ID
     * @param newBid 新的竞价
     * @return 竞价已被超越消息
     */
    public String getBidOutbidMessage(int itemId, String newBid) {
        return messages.getString("gui_listener.bid_outbid", "§e你的出价已被超越! 物品ID: %id%")
                .replace("%id%", String.valueOf(itemId));
    }

    /**
     * 获取前一次竞价消息
     * @param previousBid 前一次竞价
     * @return 前一次竞价消息
     */
    public String getPreviousBidMessage(String previousBid) {
        return messages.getString("gui_listener.previous_bid", "§e你的出价 %amount% 已退还")
                .replace("%amount%", previousBid);
    }

    /**
     * 获取新竞价消息
     * @param newBid 新竞价
     * @return 新竞价消息
     */
    public String getNewBidMessage(String newBid) {
        return messages.getString("gui_listener.new_bid", "§e新的出价: %amount%")
                .replace("%amount%", newBid);
    }

    /**
     * 获取竞价已接受消息
     * @param amount 金额
     * @return 竞价已接受消息
     */
    public String getBidAcceptedMessage(String amount) {
        return messages.getString("gui_listener.bid_accepted", "§a已确认当前竞价金额: §f%amount%")
                .replace("%amount%", amount);
    }

    /**
     * 获取物品已放入邮箱消息
     * @return 物品已放入邮箱消息
     */
    public String getMailboxFullMessage() {
        return messages.getString("gui_listener.mailbox_full", "§a购买成功! §e但你的背包已满，物品已存入物品邮箱，请使用 /ah my 查看。");
    }

    /**
     * 获取竞价成功消息
     * @param amount 金额
     * @return 竞价成功消息
     */
    public String getBidSuccessMessage(String amount) {
        return messages.getString("gui_listener.bid_success", "§a竞价成功!");
    }

    /**
     * 获取竞价物品ID消息
     * @param itemId 物品ID
     * @return 竞价物品ID消息
     */
    public String getBidItemMessage(String itemId) {
        return messages.getString("gui_listener.bid_item", "§e物品ID: §f%id%")
                .replace("%id%", itemId);
    }

    /**
     * 获取竞价价格消息
     * @param price 价格
     * @return 竞价价格消息
     */
    public String getBidPriceMessage(String price) {
        return messages.getString("gui_listener.bid_price", "§e你的出价: §f%price%")
                .replace("%price%", price);
    }

    /**
     * 获取退款消息
     * @param amount 金额
     * @return 退款消息
     */
    public String getRefundMessage(String amount) {
        return messages.getString("gui_listener.refund", "§e你在一个物品上的竞价已被退还，因为该物品被管理员强制下架。\n§e已退还: §f%amount%")
                .replace("%amount%", amount);
    }

    /**
     * 获取物品已被移除消息
     * @param itemId 物品ID
     * @param seller 卖家
     * @return 物品已被移除消息
     */
    public String getRemovedItemMessage(int itemId, String seller) {
        return messages.getString("gui_listener.removed_item", "§a已强制下架物品 #%id% (卖家: %seller%)")
                .replace("%id%", String.valueOf(itemId))
                .replace("%seller%", seller);
    }

    /**
     * 格式化价格显示
     * @param price 价格
     * @return 格式化后的价格
     */
    private String formatPrice(double price) {
        return String.format("%.2f", price);
    }

    // 物品邮箱相关消息
    /**
     * 获取物品成功领取消息
     * @return 物品成功领取消息
     */
    public String getCollectSuccessMessage() {
        return messages.getString("mailbox_details.collect_success", "§a已成功领取物品，物品已放入你的背包。");
    }

    /**
     * 获取背包已满无法领取物品消息
     * @return 背包已满无法领取物品消息
     */
    public String getInventoryFullCollectMessage() {
        return messages.getString("mailbox_details.inventory_full", "§e背包已满，无法领取物品。请清理背包后再试。");
    }

    /**
     * 获取物品已有人出价无法取消拍卖消息
     * @return 物品已有人出价无法取消拍卖消息
     */
    public String getAuctionHasBidderMessage() {
        return messages.getString("mailbox_details.auction_has_bidder", "§c该物品已有人出价，无法取消拍卖！");
    }

    /**
     * 获取邮箱物品过滤关键词列表
     * @return 邮箱物品过滤关键词列表
     */
    public List<String> getMailboxFilterKeywords() {
        return messages.getStringList("mailbox_details.filter_keywords");
    }

    // 物品详情查看相关消息
    /**
     * 获取物品详情标题
     * @return 物品详情标题
     */
    public String getItemDetailsHeader() {
        return messages.getString("item_details.header", "§6===== 拍卖物品详情 =====");
    }

    /**
     * 获取物品名称格式
     * @param itemName 物品名称
     * @return 格式化的物品名称
     */
    public String getItemDetailsNameFormat(String itemName) {
        return messages.getString("item_details.item_name", "§e物品: §f%item%").replace("%item%", itemName);
    }

    /**
     * 获取起拍价格格式(详情查看)
     * @param price 价格
     * @return 格式化的起拍价格
     */
    public String getItemDetailsStartPriceFormat(String price) {
        return messages.getString("item_details.start_price", "§e起拍价: §f%price%").replace("%price%", price);
    }

    /**
     * 获取当前价格格式(详情查看)
     * @param price 价格
     * @return 格式化的当前价格
     */
    public String getItemDetailsCurrentPriceFormat(String price) {
        return messages.getString("item_details.current_price", "§e当前价格: §f%price%").replace("%price%", price);
    }

    /**
     * 获取一口价格式(详情查看)
     * @param price 价格
     * @return 格式化的一口价
     */
    public String getItemDetailsBuyNowPriceFormat(String price) {
        return messages.getString("item_details.buy_now_price", "§e一口价: §f%price%").replace("%price%", price);
    }

    /**
     * 获取当前出价者格式(详情查看)
     * @param bidder 出价者名称
     * @return 格式化的当前出价者
     */
    public String getItemDetailsCurrentBidderFormat(String bidder) {
        return messages.getString("item_details.current_bidder", "§e当前出价者: §f%bidder%").replace("%bidder%", bidder);
    }

    /**
     * 获取剩余时间格式(详情查看)
     * @param time 剩余时间
     * @return 格式化的剩余时间
     */
    public String getItemDetailsRemainingTimeFormat(String time) {
        return messages.getString("item_details.remaining_time", "§e剩余时间: §f%time%").replace("%time%", time);
    }

    /**
     * 获取取消拍卖成功消息
     * @return 取消拍卖成功消息
     */
    public String getCancelAuctionSuccessMessage() {
        return messages.getString("gui_listener.cancel_auction_success", "§a已成功取消拍卖，物品已返还到你的背包。");
    }

    /**
     * 获取取消拍卖成功但背包已满消息
     * @return 取消拍卖成功但背包已满消息
     */
    public String getCancelAuctionSuccessInventoryFullMessage() {
        return messages.getString("gui_listener.cancel_auction_success_inventory_full", "§e已成功取消拍卖，但你的背包已满。物品已存入待领取列表，使用 /auction collect 领取。");
    }

    /**
     * 获取取消拍卖失败消息
     * @return 取消拍卖失败消息
     */
    public String getCancelAuctionFailMessage() {
        return messages.getString("gui_listener.cancel_auction_fail", "§c取消拍卖失败，请稍后再试。");
    }

    /**
     * 获取确认竞价警告文本1
     * @return 确认竞价警告文本1
     */
    public String getConfirmBidNotice1Text() {
        return messages.getString("bid_menu.confirm_notice1", "§7请先点击预先抬价按钮");
    }

    /**
     * 获取确认竞价提示2文本
     * @return 确认竞价提示2文本
     */
    public String getConfirmBidNotice2Text() {
        return messages.getString("bid_menu.confirm_notice2", "§7然后再确认竞价");
    }

    /**
     * 获取确认竞价预加价金额文本
     * @return 确认竞价预加价金额文本
     */
    public String getConfirmBidPreAmountText() {
        return messages.getString("bid_menu.confirm_pre_amount_text", "§7预加价金额: §f%price%");
    }

    /**
     * 获取预加价金额文本
     * @return 预加价金额文本
     */
    public String getCurrentBidPreAmountText() {
        return messages.getString("bid_menu.pre_amount_text", "§7预加价金额: §f%price%");
    }

    public String getCommandHelpAdminHeaderMessage() {
        return messages.getString("auction_commands.help.admin_header", "§c===== 管理员命令 =====");
    }

    /**
     * 获取重载命令描述
     * @return 重载命令描述
     */
    public String getCommandHelpReloadMessage() {
        return messages.getString("auction_commands.help.reload", "§e/auction reload - 重新加载配置文件");
    }

    /**
     * 获取关闭命令描述
     * @return 关闭命令描述
     */
    public String getCommandHelpCloseMessage() {
        return messages.getString("auction_commands.help.close", "§e/auction close - §c[测试] §e强制关闭所有拍卖物品并标记为过期");
    }

    /**
     * 获取检查过期命令描述
     * @return 检查过期命令描述
     */
    public String getCommandHelpCheckExpiredMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_commands.help.checkexpired", "§e/auction checkexpired - 检查所有过期但未处理的物品"));
    }

    /**
     * 获取info命令帮助消息
     * @return info命令帮助消息
     */
    public String getCommandHelpInfoMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("auction_commands.help.info", "§e/auction info <玩家名> - 查询玩家的拍卖行信息"));
    }

    /**
     * 获取物品收集命令成功消息
     * @return 物品收集命令成功消息
     */
    public String getCommandCollectSuccessMessage() {
        return messages.getString("auction_commands.collect.success", "§a已打开物品邮箱，你可以在这里领取所有待领取的物品。");
    }

    /**
     * 获取出售命令用法消息
     * @return 出售命令用法消息
     */
    public String getCommandSellUsageMessage() {
        return messages.getString("auction_commands.sell.usage", "§c用法: /auction sell <起拍价> <一口价> [货币类型]");
    }

    /**
     * 获取货币类型描述消息
     * @return 货币类型描述消息
     */
    public String getCommandSellCurrencyTypesMessage() {
        return messages.getString("auction_commands.sell.currency_types", "§c货币类型: 1 = 金币, 2 = 点券 (默认为金币)");
    }

    /**
     * 获取点券不可用消息
     * @return 点券不可用消息
     */
    public String getCommandSellPointsUnavailableMessage() {
        return messages.getString("auction_commands.sell.points_unavailable", "§c点券系统不可用，服务器未安装PlayerPoints插件。");
    }

    /**
     * 获取点券不可用解决方案消息
     * @return 点券不可用解决方案消息
     */
    public String getCommandSellPointsUnavailableSolutionMessage() {
        return messages.getString("auction_commands.sell.points_unavailable_solution", "§c请使用金币上架物品，或联系服务器管理员获取帮助。");
    }

    /**
     * 获取无效货币类型消息
     * @return 无效货币类型消息
     */
    public String getCommandSellInvalidCurrencyMessage() {
        return messages.getString("auction_commands.sell.invalid_currency", "§c无效的货币类型! 使用: 1 = 金币, 2 = 点券");
    }

    /**
     * 获取已达到最大上架数量消息
     * @param maxListings 最大上架数量
     * @return 已达到最大上架数量消息
     */
    public String getCommandSellMaxListingsReachedMessage(int maxListings) {
        return messages.getString("auction_commands.sell.max_listings_reached", "§c你已达到最大上架数量限制 (%max_listings% 个物品)!")
                .replace("%max_listings%", String.valueOf(maxListings));
    }

    /**
     * 获取等待物品售出消息
     * @return 等待物品售出消息
     */
    public String getCommandSellWaitForItemsSoldMessage() {
        return messages.getString("auction_commands.sell.wait_for_items_sold", "§c请等待已上架物品售出或过期后再尝试上架新物品。");
    }

    /**
     * 获取起拍价必须大于0消息
     * @return 起拍价必须大于0消息
     */
    public String getCommandSellStartPriceZeroMessage() {
        return messages.getString("auction_commands.sell.start_price_zero", "§c起拍价必须大于0!");
    }

    /**
     * 获取一口价必须大于起拍价消息
     * @return 一口价必须大于起拍价消息
     */
    public String getCommandSellBuyNowLessThanStartMessage() {
        return messages.getString("auction_commands.sell.buy_now_less_than_start", "§c一口价不能小于起拍价!");
    }

    /**
     * 获取无效价格消息
     * @return 无效价格消息
     */
    public String getCommandSellInvalidPriceMessage() {
        return messages.getString("auction_commands.sell.invalid_price", "§c请输入有效的价格!");
    }

    /**
     * 获取搜索命令用法消息
     * @return 搜索命令用法消息
     */
    public String getCommandSearchUsageMessage() {
        return messages.getString("auction_commands.search.usage", "§c用法: /auction search <关键词>");
    }

    /**
     * 获取搜索关键词最小长度消息
     * @return 搜索关键词最小长度消息
     */
    public String getCommandSearchMinLengthMessage() {
        return messages.getString("auction_commands.search.min_length", "§c搜索关键词至少需要2个字符!");
    }

    /**
     * 获取正在搜索消息
     * @param keyword 搜索关键词
     * @return 正在搜索消息
     */
    public String getCommandSearchingMessage(String keyword) {
        return messages.getString("auction_commands.search.searching", "§a正在搜索: §e%keyword%")
                .replace("%keyword%", keyword);
    }

    /**
     * 获取正在关闭拍卖消息
     * @return 正在关闭拍卖消息
     */
    public String getCommandCloseStartingMessage() {
        return messages.getString("auction_commands.close.starting", "§e正在强制关闭所有拍卖物品...");
    }

    /**
     * 获取异步执行通知消息
     * @return 异步执行通知消息
     */
    public String getCommandCloseAsyncNoticeMessage() {
        return messages.getString("auction_commands.close.async_notice", "§e此操作将在后台异步执行，以避免服务器卡顿，请耐心等待。");
    }

    /**
     * 获取检查过期仅玩家消息
     * @return 检查过期仅玩家消息
     */
    public String getCommandCheckExpiredPlayerOnlyMessage() {
        return messages.getString("auction_commands.checkexpired.player_only", "§c只有玩家可以执行此命令！");
    }

    /**
     * 获取正在检查过期消息
     * @return 正在检查过期消息
     */
    public String getCommandCheckExpiredCheckingMessage() {
        return messages.getString("auction_commands.checkexpired.checking", "§a正在检查所有拍卖物品，查找过期但未处理的物品...");
    }

    /**
     * 获取重载成功消息
     * @return 重载成功消息
     */
    public String getCommandReloadSuccessMessage() {
        return messages.getString("auction_commands.reload.success", "§a GlobalShop配置已重载!");
    }

    /**
     * 获取重载调试信息消息
     * @param interval 检查间隔
     * @return 重载调试信息消息
     */
    public String getCommandReloadDebugInfoMessage(int interval) {
        return messages.getString("auction_commands.reload.debug_info", "§e调试信息: 检查间隔已设置为 %interval% 秒")
                .replace("%interval%", String.valueOf(interval));
    }

    /**
     * 获取命令相关消息 - 仅玩家可用消息
     * @return 仅玩家可用消息
     */
    public String getCommandPlayerOnlyMessage() {
        return messages.getString("auction_commands.player_only", "§c此命令只能由玩家使用!");
    }

    /**
     * 获取命令相关消息 - 未知命令消息
     * @return 未知命令消息
     */
    public String getCommandUnknownCommandMessage() {
        return messages.getString("auction_commands.unknown_command", "§c未知命令! 使用 /auction help查看帮助");
    }

    /**
     * 获取命令相关消息 - 无权限消息
     * @return 无权限消息
     */
    public String getCommandNoPermissionMessage() {
        return messages.getString("auction_commands.no_permission", "§c你没有权限使用该命令!");
    }

    /**
     * 获取帮助菜单标题
     * @return 帮助菜单标题
     */
    public String getCommandHelpHeaderMessage() {
        return messages.getString("auction_commands.help.header", "§6===== 拍卖行帮助 =====");
    }

    /**
     * 获取帮助命令描述
     * @return 帮助命令描述
     */
    public String getCommandHelpHelpMessage() {
        return messages.getString("auction_commands.help.help", "§e/auction help - 显示此帮助信息");
    }

    /**
     * 获取打开命令描述
     * @return 打开命令描述
     */
    public String getCommandHelpOpenMessage() {
        return messages.getString("auction_commands.help.open", "§e/auction open - 打开拍卖行");
    }

    /**
     * 获取出售命令描述
     * @return 出售命令描述
     */
    public String getCommandHelpSellMessage() {
        return messages.getString("auction_commands.help.sell", "§e/auction sell <起拍价> [一口价] [货币类型] - 上架物品 (1=金币, 2=点券)");
    }

    /**
     * 获取搜索命令描述
     * @return 搜索命令描述
     */
    public String getCommandHelpSearchMessage() {
        return messages.getString("auction_commands.help.search", "§e/auction search <关键词> - 搜索物品");
    }

    /**
     * 获取我的拍卖命令描述
     * @return 我的拍卖命令描述
     */
    public String getCommandHelpMyMessage() {
        return messages.getString("auction_commands.help.my", "§e/auction my - 查看我的拍卖和物品邮箱");
    }

    /**
     * 获取领取命令描述
     * @return 领取命令描述
     */
    public String getCommandHelpCollectMessage() {
        return messages.getString("auction_commands.help.collect", "§e/auction collect - 领取待领取物品");
    }

    // AuctionTask相关消息获取方法

    /**
     * 获取竞拍成功买家通知 - 成功消息
     * @return 竞拍成功买家通知成功消息
     */
    public String getBuyerWinSuccessMessage() {
        return messages.getString("auction_task.buyer_win.success", "§a你竞拍的物品已送达至物品邮箱！");
    }

    /**
     * 获取竞拍成功买家通知 - 物品信息
     * @param itemName 物品名称
     * @return 物品信息消息
     */
    public String getBuyerWinItemMessage(String itemName) {
        return messages.getString("auction_task.buyer_win.item", "§e物品: %item_name%")
                .replace("%item_name%", itemName);
    }

    /**
     * 获取竞拍成功买家通知 - 价格信息
     * @param price 价格
     * @return 价格信息消息
     */
    public String getBuyerWinPriceMessage(String price) {
        return messages.getString("auction_task.buyer_win.price", "§e价格: %price%")
                .replace("%price%", price);
    }

    /**
     * 获取竞拍成功买家通知 - 领取指南
     * @return 领取指南消息
     */
    public String getBuyerWinCollectGuideMessage() {
        return messages.getString("auction_task.buyer_win.collect_guide",
                "§e使用 /auction my 进入拍卖系统，点击\"物品邮箱\"领取");
    }

    /**
     * 获取竞拍成功卖家通知 - 成功消息
     * @return 竞拍成功卖家通知成功消息
     */
    public String getSellerWinSuccessMessage() {
        return messages.getString("auction_task.seller_win.success", "§a你的物品已被拍卖成功！");
    }

    /**
     * 获取竞拍成功卖家通知 - 物品信息
     * @param itemName 物品名称
     * @return 物品信息消息
     */
    public String getSellerWinItemMessage(String itemName) {
        return messages.getString("auction_task.seller_win.item", "§e物品: %item_name%")
                .replace("%item_name%", itemName);
    }

    /**
     * 获取竞拍成功卖家通知 - 价格信息
     * @param price 价格
     * @return 价格信息消息
     */
    public String getSellerWinPriceMessage(String price) {
        return messages.getString("auction_task.seller_win.price", "§e价格: %price%")
                .replace("%price%", price);
    }

    /**
     * 获取竞拍成功卖家通知 - 收入信息
     * @param amount 收入金额
     * @return 收入信息消息
     */
    public String getSellerWinIncomeMessage(String amount) {
        return messages.getString("auction_task.seller_win.income", "§e收入: %amount%")
                .replace("%amount%", amount);
    }

    /**
     * 获取竞拍成功卖家通知 - 买家信息
     * @param buyer 买家名称
     * @return 买家信息消息
     */
    public String getSellerWinBuyerMessage(String buyer) {
        return messages.getString("auction_task.seller_win.buyer", "§e买家: %buyer%")
                .replace("%buyer%", buyer != null ? buyer : getUnknownBuyerText());
    }

    /**
     * 获取未知买家文本
     * @return 未知买家文本
     */
    public String getUnknownBuyerText() {
        return messages.getString("auction_task.seller_win.unknown_buyer", "未知");
    }

    /**
     * 获取拍卖过期卖家通知 - 提示信息
     * @return 拍卖过期提示信息
     */
    public String getSellerExpiredNoticeMessage() {
        return messages.getString("auction_task.seller_expired.notice",
                "§a你的拍卖物品已过期，物品已放入物品邮箱！");
    }

    /**
     * 获取拍卖过期卖家通知 - 物品信息
     * @param itemName 物品名称
     * @return 物品信息消息
     */
    public String getSellerExpiredItemMessage(String itemName) {
        return messages.getString("auction_task.seller_expired.item", "§e物品: %item_name%")
                .replace("%item_name%", itemName);
    }

    /**
     * 获取拍卖过期卖家通知 - 领取指南
     * @return 领取指南消息
     */
    public String getSellerExpiredCollectGuideMessage() {
        return messages.getString("auction_task.seller_expired.collect_guide",
                "§e使用 /auction my 进入拍卖系统，点击\"物品邮箱\"领取");
    }

    /**
     * 获取sell命令起拍价的TAB补全提示
     * @return 起拍价提示文本
     */
    public String getTabCompletionSellPriceMessage() {
        return messages.getString("auction_commands.tab_completion.sell_price", "<起拍价>");
    }

    /**
     * 获取sell命令一口价的TAB补全提示
     * @return 一口价提示文本
     */
    public String getTabCompletionSellBuyNowMessage() {
        return messages.getString("auction_commands.tab_completion.sell_buynow", "<一口价>");
    }

    /**
     * 获取sell命令货币类型的TAB补全提示
     * @return 货币类型提示文本
     */
    public String getTabCompletionSellCurrencyMessage() {
        return messages.getString("auction_commands.tab_completion.sell_currency", "<货币类型: 1=金币, 2=点券>");
    }

    /**
     * 获取search命令关键词的TAB补全提示
     * @return 搜索关键词提示文本
     */
    public String getTabCompletionSearchKeywordMessage() {
        return messages.getString("auction_commands.tab_completion.search_keyword", "<关键词>");
    }

    public String getBidBelowCurrentPriceMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.bid_below_current_price", "§c价格已更新，您的竞价低于当前最新价格，请重新出价!"));
    }

    /**
     * 获取关闭命令强制关闭消息
     * @return 强制关闭消息
     */
    public String getCommandCloseForceCloseMessage() {
        return messages.getString("auction_commands.close.force_close", "§c拍卖行已被管理员强制关闭!");
    }

    /**
     * 获取关闭命令成功消息
     * @return 关闭成功消息
     */
    public String getCommandCloseSuccessMessage() {
        return messages.getString("auction_commands.close.success", "§a所有玩家的拍卖行已被强制关闭!");
    }

    /**
     * 获取检查过期成功消息
     * @param count 处理的过期物品数量
     * @return 检查过期成功消息
     */
    public String getCommandCheckExpiredSuccessMessage(int count) {
        return messages.getString("auction_commands.checkexpired.success", "§a成功处理了 %count% 个过期物品!")
                .replace("%count%", String.valueOf(count));
    }

    /**
     * 获取检查过期无物品消息
     * @return 检查过期无物品消息
     */
    public String getCommandCheckExpiredNoneMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.command.check_expired.none", "§a没有找到需要处理的过期物品。"));
    }

    // --- 新增管理员查询功能的消息获取方法 --- START ---

    public String getCommandInfoUsageMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.command.info.usage", "§c用法: /gs info <玩家名>"));
    }

    public String getPlayerNotFoundMessage(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("messages.command.player_not_found", "§c未找到玩家: %player_name%").replace("%player_name%", playerName));
    }

    public String getPlayerInfoGuiTitle(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.title", "玩家信息: %player_name%").replace("%player_name%", playerName));
    }

    public String getPlayerInfoItemName(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.item_name", "§e玩家: §f%player_name%").replace("%player_name%", playerName));
    }

    public String getPlayerInfoSoldCountFormat(int count) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.sold_count", "§e总计售出物品: §f%count%").replace("%count%", String.valueOf(count)));
    }

    public String getPlayerInfoPurchasedCountFormat(int count) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.purchased_count", "§e总计购买物品: §f%count%").replace("%count%", String.valueOf(count)));
    }

    public String getPlayerInfoEarningsHeader() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.earnings_header", "§6总计收入:"));
    }

    public String getPlayerInfoEarningsVaultFormat(String amount) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.earnings_vault", "§7 - 金币: §f%amount%").replace("%amount%", amount));
    }

    public String getPlayerInfoEarningsPointsFormat(String amount) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.earnings_points", "§7 - 点券: §f%amount%").replace("%amount%", amount));
    }

    public String getPlayerInfoSpendingHeader() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.spending_header", "§c总计支出:"));
    }

    public String getPlayerInfoSpendingVaultFormat(String amount) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.spending_vault", "§7 - 金币: §f%amount%").replace("%amount%", amount));
    }

    public String getPlayerInfoSpendingPointsFormat(String amount) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.spending_points", "§7 - 点券: §f%amount%").replace("%amount%", amount));
    }

    public String getPlayerInfoSalesHistoryButton() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.sales_history_button", "§6查看销售记录"));
    }

    public String getPlayerInfoPurchaseHistoryButton() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.purchase_history_button", "§b查看购买记录"));
    }

    public String getPlayerSalesHistoryGuiTitle(String playerName, int page) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.sales_history_title", "§6销售历史: %player_name% - 页 %page%")
                .replace("%player_name%", playerName)
                .replace("%page%", String.valueOf(page)));
    }

    public String getPlayerPurchaseHistoryGuiTitle(String playerName, int page) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.purchase_history_title", "§b购买历史: %player_name% - 页 %page%")
                .replace("%player_name%", playerName)
                .replace("%page%", String.valueOf(page)));
    }

    public String getBackButtonToPlayerInfoText() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.back_button_to_player_info", "§e返回玩家信息"));
    }

    public String getHistoryInfoHeader() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("history_item.info_header", "§6§l交易记录:"));
    }

    public String getHistorySoldTimeFormat() {
        return getMessages().getString("history_item.sold_time", "§e成交时间: §f%time%");
    }

    public String getHistoryBuyerFormat() {
        return getMessages().getString("history_item.buyer", "§e买家: §f%buyer%");
    }

    public String getCurrentPageFormat(int page, int totalPages) {
        String format = getMessages().getString("my_auction.page_info", "§e当前页: §f%page%/%total_pages%");
        return format.replace("%page%", String.valueOf(page)).replace("%total_pages%", String.valueOf(totalPages));
    }
    /**
     * 获取玩家活跃拍卖数量文本格式
     * @return 玩家活跃拍卖数量文本格式
     */
    public String getPlayerInfoActiveAuctions() {
        return ChatColor.translateAlternateColorCodes('&', messages.getString("admin_info_gui.active_auctions", "&7活跃拍卖: &e%count%"));
    }

    /**
     * 获取无效竞价金额消息
     * @return 无效竞价金额消息
     */
    public String getBidInvalidAmountMessage() {
        return messages.getString("messages.bid_invalid_amount", "§c无效的金额，请输入一个数字。");
    }

    // --- AuctionItem格式化相关方法 ---

    /**
     * 获取已过期文本
     * @return 已过期文本
     */
    public String getTimeExpiredText() {
        return messages.getString("auction_item_format.time.expired", "已过期");
    }

    /**
     * 获取时间单位-天文本
     * @return 天文本
     */
    public String getTimeDayText() {
        return messages.getString("auction_item_format.time.day", "天");
    }

    /**
     * 获取时间单位-小时文本
     * @return 小时文本
     */
    public String getTimeHourText() {
        return messages.getString("auction_item_format.time.hour", "小时");
    }

    /**
     * 获取时间单位-分钟文本
     * @return 分钟文本
     */
    public String getTimeMinuteText() {
        return messages.getString("auction_item_format.time.minute", "分钟");
    }

    /**
     * 获取时间单位-秒文本
     * @return 秒文本
     */
    public String getTimeSecondText() {
        return messages.getString("auction_item_format.time.second", "秒");
    }

    /**
     * 获取未售出文本
     * @return 未售出文本
     */
    public String getTimeNotSoldText() {
        return messages.getString("auction_item_format.time.not_sold", "未售出");
    }

    /**
     * 获取未知物品文本
     * @return 未知物品文本
     */
    public String getUnknownItemText() {
        return messages.getString("auction_item_format.display.unknown_item", "未知物品");
    }

    // 排序相关方法

    /**
     * 获取排序按钮文本
     * @return 排序按钮文本
     */
    public String getSortButtonText() {
        return getButtonText("sort_button", "§e排序方式");
    }

    /**
     * 获取按时间升序排序文本
     * @return 按时间升序排序文本
     */
    public String getSortByTimeAscText() {
        return getButtonText("sort_by_time_asc", "§7最近上架");
    }

    /**
     * 获取按时间降序排序文本
     * @return 按时间降序排序文本
     */
    public String getSortByTimeDescText() {
        return getButtonText("sort_by_time_desc", "§7即将结束");
    }

    /**
     * 获取按价格降序排序文本
     * @return 按价格降序排序文本
     */
    public String getSortByPriceHighText() {
        return getButtonText("sort_by_price_high", "§7最高价格");
    }

    /**
     * 获取按价格升序排序文本
     * @return 按价格升序排序文本
     */
    public String getSortByPriceLowText() {
        return getButtonText("sort_by_price_low", "§7最低价格");
    }

    /**
     * 获取按名称排序文本
     * @return 按名称排序文本
     */
    public String getSortByNameText() {
        return getButtonText("sort_by_name", "§7物品名称");
    }

    /**
     * 获取当前排序文本
     * @param sortType 排序类型
     * @return 当前排序文本
     */
    public String getCurrentSortText(String sortType) {
        return messages.getString("sort.current_sort", "§e当前排序: §f%sort_type%")
                .replace("%sort_type%", sortType);
    }

    /**
     * 获取点击切换排序提示文本
     * @return 点击切换排序提示文本
     */
    public String getClickToChangeSortText() {
        return messages.getString("sort.click_to_change", "§7点击切换排序方式");
    }

    /**
     * 获取排序已切换消息
     * @param sortType 排序类型
     * @return 排序已切换消息
     */
    public String getSortChangedMessage(String sortType) {
        return messages.getString("sort.sort_changed", "§a排序方式已切换为: §f%sort_type%")
                .replace("%sort_type%", sortType);
    }

} // 确保类有正确的结束括号