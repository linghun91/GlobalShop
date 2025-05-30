package cn.i7mc.globalshop.listeners;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import cn.i7mc.globalshop.utils.ChatUtils;
import cn.i7mc.globalshop.config.ConfigManager;
import cn.i7mc.globalshop.config.MessageManager;
import cn.i7mc.globalshop.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.metadata.FixedMetadataValue;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.OfflinePlayer; // 确保导入
import cn.i7mc.globalshop.gui.GuiManager.PlayerInfoHolder; // 导入新的 Holder
import cn.i7mc.globalshop.gui.GuiManager.PlayerSalesHistoryHolder; // 导入新的 Holder
import cn.i7mc.globalshop.gui.GuiManager.PlayerPurchaseHistoryHolder; // 导入新的 Holder
import net.md_5.bungee.api.ChatColor;

public class GuiListener implements Listener {
    private final GlobalShop plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final GuiManager guiManager;
    // 添加一个集合来跟踪已处理的物品上架事件
    private final Set<UUID> processingPlayers = new HashSet<>();

    public GuiListener(GlobalShop plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
        this.guiManager = plugin.getGuiManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 确保是玩家点击
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // 记录点击类型，确保所有点击类型都能被捕获
        ClickType clickType = event.getClick();



        if (event.getClickedInventory() == null) {
            return; // 无效的点击，可能是在屏幕边缘
        }

        String title = event.getView().getTitle();

        // 处理主界面点击（包括带排序信息的标题）
        if (title.equals(configManager.getGuiTitle()) || title.startsWith(configManager.getGuiTitle() + " §7- ")) {
            event.setCancelled(true);
            handleMainMenuClick(event, player);
            return;
        }

        // 处理搜索界面点击
        if (title.equals(messageManager.getSearchMenuTitle())) {
            handleSearchMenuClick(event, player);
            return;
        }

        // 处理搜索结果界面点击
        if (title.startsWith(messageManager.getSearchResultTitlePrefix())) {
            event.setCancelled(true);
            handleSearchResultClick(event, player);
            return;
        }

        // 处理确认购买界面点击
        if (title.equals(messageManager.getConfirmBuyMenuTitle())) {
            event.setCancelled(true);
            handleConfirmBuyClick(event, player);
            return;
        }

        // 处理竞价界面点击
        if (title.equals(messageManager.getBidMenuTitle())) {
            event.setCancelled(true);
            handleBidMenuClick(event, player, event.getCurrentItem());
            return;
        }

        // 处理我的拍卖界面点击
        if (title.equals(messageManager.getMyAuctionsMenuTitle())) {
            event.setCancelled(true);
            handleMyAuctionsClick(event, player);
            return;
        }

        // 处理我的已售出拍卖界面点击
        if (title.equals(messageManager.getMySoldAuctionsMenuTitle())) {
            event.setCancelled(true);
            handleMySoldAuctionsClick(event, player);
            return;
        }

        // 处理我的已购买拍卖界面点击
        if (title.equals(messageManager.getMyPurchasedAuctionsMenuTitle())) {
            event.setCancelled(true);
            handleMyPurchasedAuctionsClick(event, player);
            return;
        }

        // 处理物品邮箱界面点击
        if (title.equals(messageManager.getMailboxMenuTitle())) {
            event.setCancelled(true);
            handleMailboxClick(event, player);
            return;
        }

        // 处理我的物品邮箱界面点击
        if (title.startsWith(messageManager.getMyMailboxMenuTitlePrefix())) {
            event.setCancelled(true);
            handleMyMailboxClick(event, player);
            return;
        }

        // 处理过期拍卖界面点击
        if (title.equals(messageManager.getExpiredAuctionsMenuTitle())) {
            event.setCancelled(true);
            handleExpiredAuctionsClick(event, player);
            return;
        }

        // 处理上架物品界面点击
        if (title.equals(messageManager.getSellMenuTitle())) {
            handleSellMenuClick(event, player);
            return;
        }

        // --- 新增：处理管理员查询界面点击 --- START ---
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.getHolder() instanceof PlayerInfoHolder infoHolder) {
            event.setCancelled(true);
            handlePlayerInfoClick(event, player, infoHolder);
            return;
        }
        if (topInventory.getHolder() instanceof PlayerSalesHistoryHolder salesHolder) {
            event.setCancelled(true);
            handlePlayerSalesHistoryClick(event, player, salesHolder);
            return;
        }
        if (topInventory.getHolder() instanceof PlayerPurchaseHistoryHolder purchaseHolder) {
            event.setCancelled(true);
            handlePlayerPurchaseHistoryClick(event, player, purchaseHolder);
            return;
        }
        // 处理搜索历史界面点击事件
        if (player.hasMetadata("currentGui") && player.getMetadata("currentGui").get(0).asString().equals("search_history")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();

            // 处理搜索历史界面中的返回按钮点击
            if (slot == 49) {
                player.removeMetadata("currentGui", plugin);
                plugin.getGuiManager().openSearchMenu(player);
            }
            // 处理历史记录条目点击
            else if (slot >= 0 && slot < 36) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.PAPER && clickedItem.hasItemMeta()) {
                    String displayName = clickedItem.getItemMeta().getDisplayName();
                    String keyword = ChatColor.stripColor(displayName).substring(messageManager.getSearchPrefixText().length());
                    player.removeMetadata("currentGui", plugin);

                    // 检查是否是卖家搜索记录
                    if (keyword.startsWith(messageManager.getSellerSearchPrefixText())) {
                        String sellerName = keyword.substring(messageManager.getSellerSearchPrefixText().length()); // 去掉卖家前缀
                        // 使用调度器确保在主线程执行GUI操作
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.getGuiManager().openSellerSearchResultMenu(player, sellerName, 1);
                        });
                    } else {
                        // 普通搜索
                        plugin.getGuiManager().openSearchResultMenu(player, keyword, 1);
                    }
                }
            }
            return;
        }
        // --- 新增：处理管理员查询界面点击 --- END ---
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        // 主界面和搜索界面禁止拖拽
        if (title.equals(configManager.getGuiTitle()) ||
            title.equals(messageManager.getSearchMenuTitle()) ||
            title.startsWith(messageManager.getSearchResultTitlePrefix()) ||
            title.equals(messageManager.getConfirmBuyMenuTitle()) ||
            title.equals(messageManager.getBidMenuTitle()) ||
            title.equals(messageManager.getMyAuctionsMenuTitle()) ||
            title.equals(messageManager.getMySoldAuctionsMenuTitle()) ||
            title.equals(messageManager.getMailboxMenuTitle()) ||
            title.startsWith(messageManager.getMyMailboxMenuTitlePrefix())) {

            // 特殊处理已过期拍卖界面，不仅禁止拖拽，还显示提示信息
            if (title.equals(messageManager.getExpiredAuctionsMenuTitle()) || title.startsWith(messageManager.getMyMailboxMenuTitlePrefix())) {
                // 检查是否尝试拖拽到上半部分界面（前54个槽位）
                boolean draggingToTop = false;
                for (int slot : event.getRawSlots()) {
                    if (slot < 54) {
                        draggingToTop = true;
                        break;
                    }
                }

                if (draggingToTop) {
                    event.setCancelled(true);
                    player.sendMessage(messageManager.getDragItemToCenterMessage());
                    return;
                }
            }

            event.setCancelled(true);
            return;
        }

        // 上架物品界面特殊处理，允许拖拽到22号位置（物品栏），但禁止拖拽到其他位置
        if (title.equals(messageManager.getSellMenuTitle())) {
            // 检查是否拖拽到了上半部分（0-44号槽位）
            boolean draggingToTop = false;
            for (int slot : event.getRawSlots()) {
                if (slot < 45 && slot != 22) { // 除22号位置外的上半部分槽位
                    draggingToTop = true;
                    break;
                }
            }

            if (draggingToTop) {
                event.setCancelled(true);
                return;
            }

            // 检查22号槽位是否包含玻璃板
            ItemStack item = event.getView().getTopInventory().getItem(22);
            if (item != null && item.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (event.getView().getTitle().equals(messageManager.getSellMenuTitle())) {
            handleSellMenuClose(event, player);
        }
        else if (event.getView().getTitle().equals(messageManager.getBidMenuTitle())) {
            // 检查是否是通过确认按钮关闭的
            if (player.hasMetadata("auction_bid_confirmed")) {
                // 处理确认按钮关闭，清除确认标记
                player.removeMetadata("auction_bid_confirmed", plugin);

                // 获取当前竞价物品
                if (player.hasMetadata("auction_bid_id")) {
                    int itemId = player.getMetadata("auction_bid_id").get(0).asInt();
                    AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
                }
            }
            // 如果不是确认竞价，也不是预加价操作，则清理所有元数据
            else if (!player.hasMetadata("auction_bid_increasing")) {
                // 显示已取消竞价操作的消息
                player.sendMessage(messageManager.getCancelBidOperationMessage());

                // 清除所有竞价相关元数据
                cleanupBidMetadata(player);
            }
            // 如果是预加价操作，则不清理元数据，因为预加价过程中会重新打开界面
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int slot = event.getRawSlot();
        Material itemType = clickedItem.getType();

        // 处理底部导航和功能按钮时取消事件，防止按钮被拿走
        if (slot >= 45) {
            event.setCancelled(true);
        }

        // 处理点击的物品
        if (slot < 45 && itemType != Material.BLACK_STAINED_GLASS_PANE) {
            // 确保玩家有正确的元数据
            if (!player.hasMetadata("auction_page")) {
                player.setMetadata("auction_page", new FixedMetadataValue(plugin, 1));
            }

            // 如果点击了有效的拍卖物品
            int currentPage = player.getMetadata("auction_page").get(0).asInt();
            List<AuctionItem> items = plugin.getDatabaseManager().getActiveAuctionItems(currentPage, 45);

            // 检查items是否为空以及slot是否在有效范围内
            if (items != null && !items.isEmpty() && slot < items.size()) {
                AuctionItem item = items.get(slot);
                handleAuctionItemClick(event, player, item);
            }
        }
        // 处理导航按钮
        else if (slot == 45) {
            // 上一页
            // 确保玩家有正确的元数据
            if (!player.hasMetadata("auction_page")) {
                player.setMetadata("auction_page", new FixedMetadataValue(plugin, 1));
                return;
            }

            int currentPage = player.getMetadata("auction_page").get(0).asInt();
            if (currentPage > 1) {
                int newPage = currentPage - 1;
                player.setMetadata("auction_page", new FixedMetadataValue(plugin, newPage));
                plugin.getGuiManager().openMainMenu(player, newPage);
            }
        } else if (slot == 53) {
            // 下一页
            // 确保玩家有正确的元数据
            if (!player.hasMetadata("auction_page")) {
                player.setMetadata("auction_page", new FixedMetadataValue(plugin, 1));
                return;
            }

            int currentPage = player.getMetadata("auction_page").get(0).asInt();
            int totalItems = plugin.getDatabaseManager().getTotalActiveItems();
            int totalPages = (int) Math.ceil(totalItems / 45.0);

            if (currentPage < totalPages) {
                int newPage = currentPage + 1;
                player.setMetadata("auction_page", new FixedMetadataValue(plugin, newPage));
                plugin.getGuiManager().openMainMenu(player, newPage);
            }
        } else if (slot == 47) {
            // 搜索按钮
            plugin.getGuiManager().openSearchMenu(player);
        } else if (slot == 48) {
            // 排序按钮
            handleSortButtonClick(player);
        } else if (slot == 51) {
            // 我的拍卖按钮
            plugin.getGuiManager().openMyAuctionsMenu(player, 1);
        }
    }

    private void handleSearchMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();

        // 总是取消事件，防止按钮被拿走
        event.setCancelled(true);

        // 点击搜索框，打开聊天栏输入搜索关键词
        if (slot == 22) { // 修改为22，与GUI创建时的位置一致
            player.closeInventory();
            player.sendMessage(messageManager.getEnterSearchKeywordMessage());
            player.setMetadata("auction_search_input", new FixedMetadataValue(plugin, true));
            return;
        }

        // 点击卖家搜索按钮
        if (slot == 31) {
            player.closeInventory();
            player.sendMessage(messageManager.getEnterSellerSearchMessage());
            player.setMetadata("auction_seller_search_input", new FixedMetadataValue(plugin, true));
            return;
        }

        // 点击历史记录按钮（历史条目）
        if (slot >= 39 && slot <= 43) {
            List<String> history = plugin.getSearchHistoryManager().getSearchHistory(player);
            int index = slot - 39;
            if (index < history.size()) {
                String keyword = history.get(index);
                // 检查是否是卖家搜索
                if (keyword.startsWith(messageManager.getSellerSearchPrefixText())) {
                    String sellerName = keyword.substring(messageManager.getSellerSearchPrefixText().length());
                    plugin.getGuiManager().openSellerSearchResultMenu(player, sellerName, 1);
                } else {
                    plugin.getGuiManager().openSearchResultMenu(player, keyword, 1);
                }
            }
            return;
        }

        // 点击清除历史
        if (slot == 48) {
            plugin.getSearchHistoryManager().clearSearchHistory(player);
            plugin.getGuiManager().openSearchMenu(player);
            player.sendMessage(messageManager.getSearchHistoryClearedMessage());
            return;
        }

        // 点击返回按钮
        if (slot == 49) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
                plugin.getGuiManager().openMainMenu(player);
                return;
            }
        }

        // 点击搜索历史按钮 (Book)
        ItemStack clickedItem = event.getCurrentItem(); // 获取点击的物品
        if (slot == 47 && clickedItem != null && clickedItem.getType() == Material.BOOK) {
            openSearchHistoryMenu(player); // 新方法：打开搜索历史界面
            return;
        }

        // 点击清除历史
        if (slot == 51 && clickedItem != null && clickedItem.getType() == Material.BARRIER) { // 确认是清除按钮
            plugin.getSearchHistoryManager().clearSearchHistory(player);
            plugin.getGuiManager().openSearchMenu(player); // 修正：调用 GuiManager 的方法
            player.sendMessage(messageManager.getSearchHistoryClearedMessage()); // 添加清除成功提示
            return;
        }
    }

    /**
     * 打开搜索历史记录界面
     * @param player 玩家
     */
    private void openSearchHistoryMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchHistoryMenuTitle());

        // 获取历史记录
        List<String> history = plugin.getSearchHistoryManager().getSearchHistory(player);

        // 填充历史记录 (最多显示36条)
        if (history != null && !history.isEmpty()) {
            int count = Math.min(history.size(), 36);
            for (int i = 0; i < count; i++) {
                ItemStack historyItem = new ItemStack(Material.PAPER);
                ItemMeta historyMeta = historyItem.getItemMeta();
                historyMeta.setDisplayName(ChatColor.GREEN + messageManager.getSearchPrefixText() + history.get(i));
                List<String> historyLore = new ArrayList<>();
                historyLore.add(ChatColor.YELLOW + messageManager.getSearchHistoryItemDescription());
                historyMeta.setLore(historyLore);
                historyItem.setItemMeta(historyMeta);
                inventory.setItem(i, historyItem); // 从0开始填充
            }
        }

        // 添加返回按钮，返回到主搜索界面
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnText()); // 使用通用的返回文本
        List<String> backLore = new ArrayList<>();
        backLore.add(messageManager.getSearchHistoryReturnText()); // 使用配置的返回描述
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);

        player.openInventory(inventory);
        // 可以设置一个元数据来标识当前是历史记录界面
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "search_history"));
    }

    private void handleSearchResultClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();

        // 总是取消事件，防止按钮被拿走
        event.setCancelled(true);

        // 获取标题信息
        String title = event.getView().getTitle();

        // 检查是按普通关键词搜索还是按卖家搜索
        boolean isSellerSearch = player.hasMetadata("auction_search_seller");

        // 检查当前是否真的在搜索结果页面
        if (!title.startsWith(messageManager.getSearchResultTitlePrefix())) {
            return; // 不是搜索结果页面，直接返回
        }

        String keyword;
        String sellerName = null;
        int currentPage;

        // 检查元数据是否存在
        if (isSellerSearch) {
            // 卖家搜索
            sellerName = player.getMetadata("auction_search_seller").get(0).asString();
            currentPage = player.hasMetadata("auction_search_page") ?
                         player.getMetadata("auction_search_page").get(0).asInt() : 1;
            keyword = messageManager.getSellerSearchPrefixText() + sellerName;
        } else if (player.hasMetadata("auction_search_keyword") && player.hasMetadata("auction_search_page")) {
            // 普通搜索
            keyword = player.getMetadata("auction_search_keyword").get(0).asString();
            currentPage = player.getMetadata("auction_search_page").get(0).asInt();
        } else {
            // 元数据不存在，尝试从标题中提取关键词
            keyword = title.substring(messageManager.getSearchResultTitlePrefix().length()).trim();
            currentPage = 1; // 默认第1页

            // 检查是否是卖家搜索
            if (keyword.startsWith(messageManager.getSellerSearchPrefixText())) {
                sellerName = keyword.substring(messageManager.getSellerSearchPrefixText().length());
                isSellerSearch = true;

                // 设置元数据供后续使用
                player.setMetadata("auction_search_seller", new FixedMetadataValue(plugin, sellerName));
                player.setMetadata("auction_search_page", new FixedMetadataValue(plugin, currentPage));
            } else {
                // 设置元数据供后续使用
                player.setMetadata("auction_search_keyword", new FixedMetadataValue(plugin, keyword));
                player.setMetadata("auction_search_page", new FixedMetadataValue(plugin, currentPage));
            }
        }

        // 点击物品（从槽位9到44是物品展示区）
        if (slot >= 9 && slot <= 44) {
            // 计算物品在列表中的索引
            int itemIndex = slot - 9;

            // 每页显示36个物品
            int itemsPerPage = 36;
            List<AuctionItem> items;

            if (isSellerSearch) {
                items = plugin.getDatabaseManager().searchItemsBySeller(sellerName, currentPage, itemsPerPage);
            } else {
                items = plugin.getDatabaseManager().searchAuctionItems(keyword, currentPage, itemsPerPage);
            }

            if (itemIndex < items.size()) {
                AuctionItem item = items.get(itemIndex);
                handleAuctionItemClick(event, player, item);
            }
            return;
        }

        // 点击上一页
        if (slot == 45 && currentPage > 1) {
            if (isSellerSearch) {
                plugin.getGuiManager().openSellerSearchResultMenu(player, sellerName, currentPage - 1);
            } else {
                plugin.getGuiManager().openSearchResultMenu(player, keyword, currentPage - 1);
            }
            return;
        }

        // 点击下一页
        if (slot == 53) {
            int totalItems;
            int totalPages;

            if (isSellerSearch) {
                totalItems = plugin.getDatabaseManager().getSellerSearchResultCount(sellerName);
            } else {
                totalItems = plugin.getDatabaseManager().getSearchResultCount(keyword);
            }

            totalPages = (int) Math.ceil((double) totalItems / 36);

            if (currentPage < totalPages) {
                if (isSellerSearch) {
                    plugin.getGuiManager().openSellerSearchResultMenu(player, sellerName, currentPage + 1);
                } else {
                    plugin.getGuiManager().openSearchResultMenu(player, keyword, currentPage + 1);
                }
            }
            return;
        }

        // 点击新搜索
        if (slot == 48) {
            player.closeInventory();

            // 根据当前搜索类型设置不同的元数据
            if (isSellerSearch) {
                // 卖家搜索
                player.sendMessage(messageManager.getEnterSellerSearchMessage());
                player.setMetadata("auction_seller_search_input", new FixedMetadataValue(plugin, true));
            } else {
                // 物品搜索
                player.sendMessage(messageManager.getEnterSearchKeywordMessage());
                player.setMetadata("auction_search_input", new FixedMetadataValue(plugin, true));
            }

            // 清除旧的搜索元数据
            cleanupSearchMetadata(player);
            return;
        }

        // 点击返回主菜单
        if (slot == 49) {
            plugin.getGuiManager().openMainMenu(player);
            // 清除搜索元数据
            cleanupSearchMetadata(player);
        }
    }

    // 清除所有搜索相关的元数据
    private void cleanupSearchMetadata(Player player) {
        if (player.hasMetadata("auction_search_keyword")) {
            player.removeMetadata("auction_search_keyword", plugin);
        }
        if (player.hasMetadata("auction_search_page")) {
            player.removeMetadata("auction_search_page", plugin);
        }
        if (player.hasMetadata("auction_search_seller")) {
            player.removeMetadata("auction_search_seller", plugin);
        }
    }

    private void handleSellMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        InventoryAction action = event.getAction();

        // 检查是否点击了底部栏（玩家物品栏）
        boolean isBottomInventory = event.getClickedInventory() != event.getView().getTopInventory();

        // 阻止shift+右键点击操作，防止物品移动到上架GUI
        if (event.isShiftClick()) {
            // 如果是从玩家物品栏shift点击，阻止操作并提示玩家
            if (isBottomInventory) {
                event.setCancelled(true);
                player.sendMessage(messageManager.getDragItemToCenterMessage());
                return;
            }
        }

        // 禁止移动上架界面中的功能按钮和玻璃板
        if (!isBottomInventory && (slot != 22 || (currentItem != null && currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE))) {
            event.setCancelled(true);
        }

        // 处理自定义上架时间按钮点击
        if (slot == 47 && !isBottomInventory && currentItem != null && currentItem.getType() == Material.CLOCK) {
            event.setCancelled(true);
            // 记录当前点击类型，确保正确传递到处理方法
            ClickType clickType = event.getClick();


            handleDurationButtonClick(event, player);
            return;
        }

        // 如果点击了确认上架按钮
        if (slot == 49 && !isBottomInventory) {
            event.setCancelled(true); // 总是取消事件，防止按钮被拿走
            if (currentItem != null && currentItem.getType() == Material.EMERALD_BLOCK &&
                currentItem.hasItemMeta() && currentItem.getItemMeta().getDisplayName().equals(messageManager.getConfirmSellText())) {

                // 获取物品
                ItemStack itemToSell = event.getView().getTopInventory().getItem(22);

                // 检查物品是否存在且不是占位符
                if (itemToSell == null || itemToSell.getType() == Material.AIR ||
                    (itemToSell.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                     itemToSell.hasItemMeta() && itemToSell.getItemMeta().hasDisplayName() &&
                     itemToSell.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText()))) {
                    player.sendMessage(messageManager.getPlaceItemFirstMessage());
                    return;
                }

                // 处理上架逻辑
                processSellItem(player, itemToSell);

                // 清空物品槽位，防止关闭时触发物品返回逻辑
                event.getView().getTopInventory().setItem(22, null);

                // 清除上架时间元数据
                if (player.hasMetadata("auction_duration")) {
                    player.removeMetadata("auction_duration", plugin);
                }

                // 关闭界面并打开主菜单
                player.closeInventory();
                plugin.getGuiManager().openMainMenu(player);
                return;
            }
        }

        // 处理上架区域（只有22号槽位）的点击
        if (slot == 22 && !isBottomInventory) {
            // 处理点击玻璃板的情况
            if (currentItem != null && currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName() &&
                currentItem.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText())) {

                // 如果玩家持有物品准备放置，手动处理放置操作
                if (cursorItem != null && cursorItem.getType() != Material.AIR &&
                    (action == InventoryAction.PLACE_ALL ||
                     action == InventoryAction.PLACE_ONE ||
                     action == InventoryAction.PLACE_SOME ||
                     action == InventoryAction.SWAP_WITH_CURSOR)) {

                    // 取消默认操作
                    event.setCancelled(true);

                    // 手动设置物品
                    ItemStack itemToPlace = cursorItem.clone();

                    // 根据放置类型设置数量
                    if (action == InventoryAction.PLACE_ONE) {
                        itemToPlace.setAmount(1);

                        // 更新光标上的物品数量
                        ItemStack newCursor = cursorItem.clone();
                        newCursor.setAmount(cursorItem.getAmount() - 1);
                        if (newCursor.getAmount() <= 0) {
                            newCursor = null;
                        }
                        player.setItemOnCursor(newCursor);
                    } else {
                        // The original code contained "# 对于PLACE_ALL和SWAP_WITH_CURSOR，直接放置所有并清空光标"
                        // but we changed it to: "# 对于其他放置类型，直接清空光标"
                        // Let's keep the new comment style but fix the code
                        player.setItemOnCursor(null);
                    }

                    // 放置物品到上架区
                    event.getView().getTopInventory().setItem(22, itemToPlace);

                    // 播放放置音效
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);

                    return;
                }

                // 其他所有对玻璃板的操作都保持取消状态
                return;
            }

            // 处理点击已放置物品的情况
            if (currentItem != null && currentItem.getType() != Material.AIR &&
                currentItem.getType() != Material.GRAY_STAINED_GLASS_PANE) {

                // 允许取回已放置的物品
                if (action == InventoryAction.PICKUP_ALL ||
                    action == InventoryAction.PICKUP_HALF ||
                    action == InventoryAction.PICKUP_ONE ||
                    action == InventoryAction.PICKUP_SOME ||
                    action == InventoryAction.SWAP_WITH_CURSOR) {
                    event.setCancelled(false); // 允许取回物品

                    // 这个槽位将变为空，放回玻璃板
                    if (action == InventoryAction.PICKUP_ALL) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            // 检查槽位是否为空
                            if (event.getView().getTopInventory().getItem(slot) == null ||
                                event.getView().getTopInventory().getItem(slot).getType() == Material.AIR) {
                                // 放回玻璃板
                                event.getView().getTopInventory().setItem(slot, plugin.getGuiManager().createPlaceholder());
                            }
                        });
                    }
                    return;
                }
            }

            // 允许放置物品到空槽位
            if ((currentItem == null || currentItem.getType() == Material.AIR) &&
                cursorItem != null && cursorItem.getType() != Material.AIR) {
                event.setCancelled(false); // 允许放置
                return;
            }
        }
        // 处理玩家物品栏的操作
        else if (isBottomInventory) {
            // 允许玩家在自己的物品栏内操作
            event.setCancelled(false);

            // 如果是Shift+点击尝试将物品移动到上架区
            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                // 查找上架区域是否有空槽位或玻璃板
                ItemStack slotItem = event.getView().getTopInventory().getItem(22);
                boolean foundSlot = slotItem == null || slotItem.getType() == Material.AIR ||
                       (slotItem.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                        slotItem.hasItemMeta() && slotItem.getItemMeta().hasDisplayName() &&
                        slotItem.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText()));

                // 如果没有找到合适的槽位，取消操作
                if (!foundSlot) {
                    event.setCancelled(true);
                }
            }
        }
        // 其他GUI区域的点击（如功能按钮区域）默认已经取消
    }

    /**
     * 处理自定义上架时间按钮点击
     * @param event 点击事件
     * @param player 玩家
     */
    private void handleDurationButtonClick(InventoryClickEvent event, Player player) {
        if (!player.hasMetadata("auction_duration")) {
            // 如果没有元数据，设置默认值为最小时间
            player.setMetadata("auction_duration", new FixedMetadataValue(plugin, configManager.getMinDuration()));
        }


        // 获取当前设置的时间（秒）
        long currentDuration = player.getMetadata("auction_duration").get(0).asLong();
        long maxDuration = configManager.getMaxDuration();
        long minDuration = configManager.getMinDuration();
        long newDuration = currentDuration;

        // 获取点击类型的原始值
        ClickType clickType = event.getClick();

        // 检查是否是中键点击或DROP类型(Q键)点击，将DROP作为中键的替代方案
        if (clickType == ClickType.MIDDLE || clickType == ClickType.DROP) {

            // 重置为最小时间
            newDuration = minDuration;
            player.sendMessage(messageManager.getResetDurationMessage());
        }
        // 备用判断方法：如果检测到是鼠标中键按钮，也作为中键点击处理
        else if (clickType.name().contains("MIDDLE")) {

            // 重置为最小时间
            newDuration = minDuration;
            player.sendMessage(messageManager.getResetDurationMessage());
        }
        else {
            // 根据点击方式增加时间
            if (event.isLeftClick() && event.isShiftClick()) {
                // Shift+左键：+10分钟
                newDuration += 600;

            } else if (event.isRightClick() && event.isShiftClick()) {
                // Shift+右键：+10小时
                newDuration += 36000;

            } else if (event.isLeftClick()) {
                // 左键：+1分钟
                newDuration += 60;

            } else if (event.isRightClick()) {
                // 右键：+1小时
                newDuration += 3600;

            } else {

            }
        }

        // 确保不超过最大时间限制
        if (newDuration > maxDuration) {
            newDuration = maxDuration;
            player.sendMessage(messageManager.getMaxDurationLimitMessage());
        }

        // 确保不低于最小时间限制
        if (newDuration < minDuration) {
            newDuration = minDuration;
        }

        // 更新元数据
        player.setMetadata("auction_duration", new FixedMetadataValue(plugin, newDuration));

        // 将时间转换为可读格式
        long hours = newDuration / 3600;
        long minutes = (newDuration % 3600) / 60;

        // 更新按钮显示
        ItemStack durationButton = event.getView().getTopInventory().getItem(47);
        if (durationButton != null && durationButton.getType() == Material.CLOCK) {
            ItemMeta meta = durationButton.getItemMeta();
            List<String> lore = meta.getLore();

            // 更新第一行显示当前设置的时间
            lore.set(0, messageManager.getDurationCurrentSettingFormat().replace("%hours%", String.valueOf(hours)).replace("%minutes%", String.valueOf(minutes)));

            meta.setLore(lore);
            durationButton.setItemMeta(meta);
        }

        // 通知玩家
        player.sendMessage(messageManager.getUpdateDurationMessage(hours, minutes));
    }

    private void handleSellMenuClose(InventoryCloseEvent event, Player player) {
        Inventory inventory = event.getInventory();

        // 只检查22号槽位是否有物品
        ItemStack item = inventory.getItem(22);
        if (item != null && item.getType() != Material.AIR) {
            // 判断是否是占位玻璃板，如果是则忽略
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText())) {
                return;
            }

            // 将物品返还给玩家
            returnItem(player, item.clone(), true);
        }
    }

    private void processSellItem(Player player, ItemStack itemToSell) {
        // 保存物品的副本，用于后续可能的返还
        ItemStack itemCopy = itemToSell.clone();
        boolean itemReturned = false;
        boolean auctionSuccess = false;  // 添加标记，表示拍卖是否成功

        try {
            // 检查玩家是否有上架命令元数据
            if (!player.hasMetadata("auction_sell_command")) {
                player.sendMessage(messageManager.getListingFailedNoPriceMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }

            // 获取玩家之前设置的起拍价和一口价
            String command = player.getMetadata("auction_sell_command").get(0).asString();
            String[] args = command.split(" ");

            // 验证命令格式
            if (args.length < 2) {
                player.sendMessage(messageManager.getListingFailedIncompletePriceMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }

            double startPrice;
            double buyNowPrice = 0;

            // 解析价格，添加异常处理
            try {
                startPrice = Double.parseDouble(args[1]);
                if (args.length > 2) {
                    buyNowPrice = Double.parseDouble(args[2]);
                }
            } catch (NumberFormatException e) {
                // 这是第608行，修改为使用messageManager
                player.sendMessage(messageManager.getListingFailedPriceFormatMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }

            // 验证价格
            if (startPrice <= 0) {
                player.sendMessage(messageManager.getStartPriceGreaterThanZeroMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }
            if (buyNowPrice > 0 && buyNowPrice < startPrice) {
                player.sendMessage(messageManager.getBuyNowGreaterThanStartMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }

            // 获取货币类型
            String currencyType = "VAULT"; // 默认为金币
            if (player.hasMetadata("auction_currency_type")) {
                currencyType = player.getMetadata("auction_currency_type").get(0).asString();

                // 检查如果是点券类型但PlayerPoints不可用，则转换为金币类型
                if ("POINTS".equals(currencyType) && !plugin.isPlayerPointsAvailable()) {
                    currencyType = "VAULT";
                    player.sendMessage(messageManager.getPointsUnavailableMessage());
                }
            }

            // 初始化手续费
            double fee = 0;

            // 只有金币类型才收取手续费
            if ("VAULT".equals(currencyType)) {
                // 计算手续费
                fee = plugin.getEconomyManager().calculateFee(startPrice, currencyType);

                // 检查玩家是否有足够的钱支付手续费
                if (!plugin.getEconomyManager().hasEnough(player, fee, currencyType)) {
                    String message = messageManager.getNotEnoughMoneyForFeeMessage().replace("%currency%", plugin.getEconomyManager().getCurrencyName(currencyType));
                    player.sendMessage(message);
                    returnItem(player, itemCopy, true);
                    itemReturned = true;
                    return;
                }
            }
            // 点券类型不收取手续费

            // 检查玩家当前上架的物品数量是否已达到上限
            int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
            int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());

            if (currentListings >= maxListings) {
                player.sendMessage(messageManager.getCommandSellMaxListingsReachedMessage(maxListings));
                player.sendMessage(messageManager.getCommandSellWaitForItemsSoldMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
                return;
            }

            // 获取拍卖持续时间，优先使用自定义时间
            long durationInSeconds = plugin.getConfigManager().getDefaultDuration(); // 默认持续时间（秒）
            if (player.hasMetadata("auction_duration")) {
                durationInSeconds = player.getMetadata("auction_duration").get(0).asLong();

                // 确保时间在限制范围内
                long minDuration = plugin.getConfigManager().getMinDuration();
                long maxDuration = plugin.getConfigManager().getMaxDuration();

                if (durationInSeconds < minDuration) {
                    durationInSeconds = minDuration;
                    player.sendMessage(messageManager.getCustomDurationBelowMinLimitMessage());
                } else if (durationInSeconds > maxDuration) {
                    durationInSeconds = maxDuration;
                    player.sendMessage(messageManager.getCustomDurationAboveMaxLimitMessage());
                }
            }

            // 创建拍卖物品，使用自定义持续时间
            AuctionItem auctionItem = new AuctionItem(
                0, // ID将由数据库生成
                player.getUniqueId(),
                player.getName(),
                itemToSell,
                startPrice,
                buyNowPrice,
                startPrice, // 当前价格等于起拍价
                currencyType, // 使用选择的货币类型
                System.currentTimeMillis(),
                System.currentTimeMillis() + durationInSeconds * 1000, // 使用自定义持续时间
                "ACTIVE"
            );

            // 保存到数据库
            int id = plugin.getDatabaseManager().createAuctionItem(auctionItem);
            if (id > 0) {
                // 上架成功
                auctionSuccess = true;  // 标记拍卖成功
                itemReturned = true;  // 标记物品已处理，不需要返还

                // 扣除手续费（只对金币类型收取）
                if ("VAULT".equals(currencyType) && fee > 0) {
                    plugin.getEconomyManager().takeMoney(player, fee, currencyType);
                }

                // 广播上架信息
                plugin.getBroadcastManager().broadcastItemListed(player, auctionItem);

                // 记录拍卖历史事件
                plugin.getAuctionHistoryManager().addListEvent(player, auctionItem);

                // 通知玩家
                if (fee > 0) {
                    String message = messageManager.getItemListedSuccessWithFeeMessage().replace("%fee%", plugin.getEconomyManager().formatAmount(fee, currencyType));
                    player.sendMessage(message);
                } else {
                    player.sendMessage(messageManager.getItemListedSuccessMessage());
                }

                // 如果使用了自定义时间，添加额外提示
                if (player.hasMetadata("auction_duration")) {
                    long hours = durationInSeconds / 3600;
                    long minutes = (durationInSeconds % 3600) / 60;
                    player.sendMessage(messageManager.getCustomDurationSetMessage(hours, minutes));
                }
            } else {
                // 上架失败
                player.sendMessage(messageManager.getItemListedFailMessage());
                returnItem(player, itemCopy, true);
                itemReturned = true;
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
            player.sendMessage(messageManager.getListingErrorMessage());
            if (!itemReturned && !auctionSuccess) {  // 只有在未处理物品且拍卖未成功时才返还
                returnItem(player, itemCopy, true);
            }
        } finally {
            // 清除上架相关元数据
            if (player.hasMetadata("auction_duration")) {
                player.removeMetadata("auction_duration", plugin);
            }
        }
    }

    // 帮助方法：返还物品给玩家
    private void returnItem(Player player, ItemStack item, boolean showMessage) {
        if (item == null || item.getType() == Material.AIR) return;

        // 尝试将物品添加到玩家背包
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            if (showMessage) {
                player.sendMessage(messageManager.getItemReturnedMessage());
            }
        } else {
            // 背包已满，掉落物品
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            if (showMessage) {
                player.sendMessage(messageManager.getInventoryFullDropItemMessage());
            }
        }
    }

    // 重载方法，默认显示消息
    private void returnItem(Player player, ItemStack item) {
        returnItem(player, item, true);
    }

    // 帮助方法：返还上架界面中的所有物品
    private void returnAllItems(Player player, Inventory inventory) {
        // 现在只有22号槽位需要检查
        ItemStack item = inventory.getItem(22);
        if (item != null && item.getType() != Material.AIR) {
            // 跳过占位玻璃板
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(messageManager.getPlaceItemHereText())) {
                return;
            }

            // 返还物品
            returnItem(player, item, true);

            // 从界面中移除物品
            inventory.setItem(22, null);
        }
    }

    private void handleAuctionItemClick(InventoryClickEvent event, Player player, AuctionItem item) {
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage(messageManager.getExpiredItemMessage());
            return;
        }

        // 判断是否是物品主人
        boolean isOwner = item.getSellerUuid().equals(player.getUniqueId());
        // 判断是否是当前竞价者
        boolean isCurrentBidder = item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId());
        // 判断是否是OP
        boolean isOp = player.isOp();

        // Shift+右键点击下架（仅物品主人可操作）
        if (event.isShiftClick() && event.isRightClick() && isOwner) {
            // 物品主人Shift+右键快速下架
            handleCancelAuction(player, item);
            return;
        }

        // OP强制下架物品（Shift+左键点击）
        if (event.isShiftClick() && event.isLeftClick() && isOp) {
            // 管理员强制下架
            handleForceRemoveAuction(player, item);
            return;
        }

        // 左键点击参与竞价
        if (event.isLeftClick() && !event.isShiftClick()) {
            // 如果是物品主人，提示不能竞拍自己的物品
            if (isOwner) {
                player.sendMessage(messageManager.getOwnerBidMessage());
                return;
            }

            // 检查权限
            if (!player.hasPermission("globalshop.bid")) {
                player.sendMessage(messageManager.getNoPermissionMessage());
                return;
            }

            // 如果玩家已经是当前最高出价者，则提示并打开竞价界面
            if (isCurrentBidder) {
                player.sendMessage(messageManager.getAlreadyHighestBidderMessage());
            }

            // 打开竞价界面
            plugin.getGuiManager().openBidMenu(player, item);
        }
        // 右键点击直接购买
        else if (event.isRightClick() && !event.isShiftClick()) {
            // 如果是物品主人，提示不能购买自己的物品
            if (isOwner) {
                player.sendMessage(messageManager.getOwnerBuyMessage());
                return;
            }

            // 检查是否有一口价
            if (!item.hasBuyNowPrice()) {
                player.sendMessage(messageManager.getNoBuyNowPriceMessage());
                return;
            }

            // 检查权限
            if (!player.hasPermission("globalshop.buy")) {
                player.sendMessage(messageManager.getNoPermissionMessage());
                return;
            }

            // 打开确认购买界面
            plugin.getGuiManager().openConfirmBuyMenu(player, item);
        }
    }

    // 处理确认购买界面点击
    private void handleConfirmBuyClick(InventoryClickEvent event, Player player) {
        // 总是取消事件，防止按钮被拿走
        event.setCancelled(true);

        // 获取点击的物品
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        // 确认购买界面
        if (event.getRawSlot() < 13 && event.getRawSlot() != 13) { // 确认按钮 (所有左侧绿色按钮)
            // 获取拍卖物品ID
            if (!player.hasMetadata("confirm_buy_id")) {
                player.closeInventory();
                return;
            }

            int itemId = player.getMetadata("confirm_buy_id").get(0).asInt();

            // 获取拍卖物品
            AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
            if (item == null) {
                player.sendMessage(messageManager.getInvalidItemMessage());
                player.closeInventory();
                return;
            }

            // 检查物品状态
            if (!item.isActive() || item.isExpired()) {
                player.sendMessage(messageManager.getExpiredItemMessage());
                player.closeInventory();
                return;
            }

            // 检查物品是否可以一口价购买
            if (!item.hasBuyNowPrice()) {
                player.sendMessage(messageManager.getNoBuyNowPriceMessage());
                player.closeInventory();
                return;
            }

            // 检查是否为卖家本人
            if (item.getSellerUuid().equals(player.getUniqueId())) {
                player.sendMessage(messageManager.getOwnerBuyMessage());
                player.closeInventory();
                return;
            }

            // 买家不需要支付税费，直接使用物品价格
            double price = item.getBuyNowPrice();

            // 检查玩家是否有足够的钱
            if (!plugin.getEconomyManager().hasEnough(player, price, item.getCurrencyType())) {
                player.sendMessage(messageManager.getNotEnoughMoneyMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType())));
                player.closeInventory();
                return;
            }

            // 检查玩家背包是否有足够空间
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(messageManager.getInventoryFullMessage());
                player.closeInventory();
                return;
            }

            // 扣除玩家的钱
            if (!plugin.getEconomyManager().takeMoney(player, price, item.getCurrencyType())) {
                player.sendMessage(messageManager.getFailedTransactionMessage());
                player.closeInventory();
                return;
            }

            // 检查是否有当前竞价者，如果有则退还其竞价资金
            if (item.getCurrentBidder() != null && !item.getCurrentBidder().toString().isEmpty()) {
                // 退还之前的竞价者支付的金额（不含税费）
                double previousBid = item.getCurrentPrice();

                // 退还资金给之前的竞价者
                plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getCurrentBidder()), previousBid, item.getCurrencyType());

                // 如果之前的竞价者在线，发送通知
                Player previousBidder = Bukkit.getPlayer(item.getCurrentBidder());
                if (previousBidder != null && previousBidder.isOnline()) {
                    previousBidder.sendMessage(messageManager.getBidSoldMessage(item.getId(), plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType())));
                    // 显示余额信息
                    double bidderBalance = plugin.getEconomyManager().getBalance(previousBidder, item.getCurrencyType());
                    previousBidder.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), bidderBalance));
                }

            }

            // 更新物品状态
            item.setStatus("SOLD");
            // 设置买家UUID和名称
            item.setCurrentBidder(player.getUniqueId());
            item.setCurrentBidderName(player.getName());
            // 设置当前价格为一口价
            item.setCurrentPrice(item.getBuyNowPrice());
            // 设置售出时间为当前时间
            long soldTime = System.currentTimeMillis();
            item.setSoldTime(soldTime);

            // 更新物品到数据库
            plugin.getDatabaseManager().updateAuctionItem(item);

            // 记录拍卖历史购买事件
            plugin.getAuctionHistoryManager().addBuyEvent(player, item);

            // 广播一口价购买消息
            plugin.getBroadcastManager().broadcastBuyNow(player.getName(), item.getSellerName(), item);

            // 将物品给予买家
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item.getItem());
            if (!leftover.isEmpty()) {
                // 如果物品无法完全放入背包，保存到待领取列表
                for (ItemStack stack : leftover.values()) {
                    plugin.getDatabaseManager().storePendingItem(player.getUniqueId(), stack, "物品购买，但背包已满");
                }
                player.sendMessage(messageManager.getPartialItemSavedMessage());
            }

            // 给卖家钱（减去手续费）
            double fee = plugin.getEconomyManager().calculateFee(item.getBuyNowPrice(), item.getCurrencyType());
            double sellerReceives = item.getBuyNowPrice() - fee;
            plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerReceives, item.getCurrencyType());

            // 发送购买成功消息
            player.sendMessage(messageManager.getPurchaseSuccessMessage());
            player.sendMessage(messageManager.getPurchaseItemMessage(ChatUtils.getItemName(item.getItem())));
            player.sendMessage(messageManager.getPurchasePriceMessage(plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            // 显示余额信息
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), balance));

            // 卖家在线则发送通知
            Player seller = Bukkit.getPlayer(item.getSellerUuid());
            if (seller != null && seller.isOnline()) {
                seller.sendMessage(messageManager.getSoldNotification(item.getId(), plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
                seller.sendMessage(messageManager.getSellerFeeMessage(plugin.getEconomyManager().formatAmount(fee, item.getCurrencyType())));
                seller.sendMessage(messageManager.getSellerIncomeMessage(plugin.getEconomyManager().formatAmount(sellerReceives, item.getCurrencyType())));
                // 显示卖家余额信息
                double sellerBalance = plugin.getEconomyManager().getBalance(seller, item.getCurrencyType());
                seller.sendMessage(messageManager.getSellerBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), sellerBalance));
                seller.sendMessage(messageManager.getBuyerMessage(player.getName()));
            }

            // 关闭界面
            player.closeInventory();
        } else if (event.getRawSlot() > 13) { // 取消按钮 (所有右侧红色按钮)
            // 关闭界面
            player.closeInventory();
            player.sendMessage(messageManager.getCancelPurchaseMessage());
        }
    }

    // 处理竞价界面点击
    private void handleBidMenuClick(InventoryClickEvent event, Player player, ItemStack item) {
        // 总是取消事件，防止按钮被拿走
        event.setCancelled(true);

        // 检查item是否为null，避免空指针异常
        if (item == null) {
            return;
        }

        if (!player.hasMetadata("auction_bid_id") || !player.hasMetadata("auction_item_price")) {
            // 这表明我们不在竞价过程中，可能是切换了物品
            player.closeInventory();
            player.sendMessage(messageManager.getExpiredBidInfoMessage());
            return;
        }

        // 预先抬价按钮（青金石块）- 位置11
        if (item.getType() == Material.LAPIS_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            // 获取当前货币类型的最低加价比例
            int itemId = player.getMetadata("auction_bid_id").get(0).asInt();
            AuctionItem auctionItem = plugin.getDatabaseManager().getAuctionItem(itemId);
            if (auctionItem != null) {
                String currencyType = auctionItem.getCurrencyType();
                double bidRatePercent = currencyType.equalsIgnoreCase("VAULT") ?
                        configManager.getVaultMinBidRate() * 100 :
                        configManager.getPointsMinBidRate() * 100;

                // 使用MessageManager获取预加价按钮的标题，而不是硬编码
                String expectedButtonName = messageManager.getIncreaseBidText(bidRatePercent);

                // 比较按钮名称
                if (item.getItemMeta().getDisplayName().equals(expectedButtonName)) {
                    handleBidIncreaseButton(player);
                    return;
                }
            }
        }

        // 确认竞价按钮（绿宝石块）- 位置15
        if (item.getType() == Material.EMERALD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals(messageManager.getConfirmBidText())) {
            handleConfirmBidButton(event, player);
            return;
        }

        // 取消竞价按钮（红石块）- 位置22
        if (item.getType() == Material.REDSTONE_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals(messageManager.getCancelBidText())) {
            // 清除所有相关元数据
            cleanupBidMetadata(player);

            // 关闭界面，然后打开主菜单
            player.closeInventory();
            player.sendMessage(messageManager.getCancelBidMessage());
            plugin.getGuiManager().openMainMenu(player);
            return;
        }
    }

    // 处理"我的拍卖"界面的点击事件
    private void handleMyAuctionsMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        int currentPage = 1;

        // 获取当前页码
        if (player.hasMetadata("auction_my_page")) {
            currentPage = player.getMetadata("auction_my_page").get(0).asInt();
        }

        // 获取当前筛选条件
        String filter = "active";
        if (player.hasMetadata("auction_my_filter")) {
            filter = player.getMetadata("auction_my_filter").get(0).asString();
        }

        // 处理物品点击 (9-44的槽位是物品展示区 - 第二行到第五行)
        if (slot >= 9 && slot <= 44) {
            // 如果是"已售出"界面，直接忽略物品点击事件，防止聊天框刷屏
            if ("sold".equals(filter)) {
                return;
            }

            List<AuctionItem> combinedItems = new ArrayList<>();

            // 如果是邮箱界面，获取合并的物品列表
            if ("mailbox".equals(filter)) {
                // 获取拍卖物品中的过期物品
                List<AuctionItem> auctionItems = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
                List<AuctionItem> expiredItems = auctionItems.stream()
                        .filter(item -> "EXPIRED".equals(item.getStatus()) && item.getCurrentBidder() == null)
                        .toList();

                // 获取待领取物品
                List<AuctionItem> pendingItems = plugin.getDatabaseManager().getPendingItemsAsAuctionItems(player.getUniqueId());

                // 合并两个来源
                combinedItems.addAll(expiredItems);
                combinedItems.addAll(pendingItems);
            } else {
                // 对于其他筛选条件，只获取拍卖物品
                List<AuctionItem> items = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());

                // 根据筛选条件过滤物品
                if ("active".equals(filter)) {
                    combinedItems.addAll(items.stream()
                            .filter(item -> item.isActive() && !item.isExpired())
                            .toList());
                } else if ("sold".equals(filter)) {
                    combinedItems.addAll(items.stream()
                            .filter(item -> "SOLD".equals(item.getStatus()))
                            .toList());
                } else {
                    combinedItems.addAll(items);
                }
            }

            // 计算物品在当前页面的索引
            int itemsPerPage = 36; // 每页36个物品
            int startIndex = (currentPage - 1) * itemsPerPage;
            int index = slot - 9; // 槽位9-44映射到索引0-35
            int itemIndex = startIndex + index;

            // 检查是否点击到有效物品
            if (itemIndex >= 0 && itemIndex < combinedItems.size()) {
                AuctionItem item = combinedItems.get(itemIndex);

                // 处理右键点击
                if (event.isRightClick()) {
                    // 检查是否是物品邮箱中的物品
                    if ("mailbox".equals(filter)) {
                        // 返回原始物品，不带有任何拍卖信息的LORE
                        ItemStack originalItem = item.getItem().clone();
                        // 清除所有拍卖相关的LORE
                        ItemMeta meta = originalItem.getItemMeta();
                        if (meta != null && meta.hasLore()) {
                            List<String> lore = meta.getLore();
                            // 使用MessageManager获取过滤关键词列表
                            List<String> filterKeywords = plugin.getMessageManager().getMailboxFilterKeywords();

                            // 移除拍卖相关的LORE（包括邮箱标记）
                            lore.removeIf(line -> {
                                for (String keyword : filterKeywords) {
                                    if (line.contains(keyword)) {
                                        return true;
                                    }
                                }
                                return false;
                            });
                            meta.setLore(lore);
                            originalItem.setItemMeta(meta);
                        }

                        // 先检查玩家背包是否有足够空间
                        boolean hasSpace = hasInventorySpace(player, originalItem);

                        if (hasSpace) {
                            boolean deleted = false;

                            // 根据物品来源选择不同的删除方法
                            if ("MAILBOX_PENDING".equals(item.getStatus())) {
                                // 来自pending_items表的物品
                                deleted = plugin.getDatabaseManager().deletePendingItemById(item.getId());
                            } else {
                                // 来自auction_items表的物品
                                deleted = plugin.getDatabaseManager().deleteAuctionItem(item.getId());
                            }

                            if (deleted) {
                                // 将物品添加到玩家背包
                                player.getInventory().addItem(originalItem);
                                player.sendMessage(plugin.getMessageManager().getCollectSuccessMessage());
                            }
                        } else {
                            // 背包已满，直接提示
                            player.sendMessage(plugin.getMessageManager().getInventoryFullCollectMessage());
                        }

                        // 刷新界面
                        plugin.getGuiManager().openMyMailboxMenu(player, currentPage);
                        return; // 添加return语句，防止继续执行下面的代码
                    }

                    // 检查是否是活跃物品且已经有出价者
                    if ("active".equals(filter) && item.getCurrentBidder() != null) {
                        // 如果物品已有人出价，无法取消
                        player.sendMessage(plugin.getMessageManager().getAuctionHasBidderMessage());
                        return;
                    }
                    handleCancelAuction(player, item);
                } else if ("active".equals(filter)) { // 只在活跃商品界面显示详细信息
                    // 左键点击，查看详细信息
                    player.sendMessage(plugin.getMessageManager().getItemDetailsHeader());
                    player.sendMessage(plugin.getMessageManager().getItemDetailsNameFormat(ChatUtils.getItemName(item.getItem())));

                    String startPrice = plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType());
                    player.sendMessage(plugin.getMessageManager().getItemDetailsStartPriceFormat(startPrice));

                    String currentPrice = plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType());
                    player.sendMessage(plugin.getMessageManager().getItemDetailsCurrentPriceFormat(currentPrice));

                    if (item.hasBuyNowPrice()) {
                        String buyNowPrice = plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType());
                        player.sendMessage(plugin.getMessageManager().getItemDetailsBuyNowPriceFormat(buyNowPrice));
                    }

                    if (item.getCurrentBidder() != null) {
                        String bidderName = Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName();
                        if (bidderName != null) {
                            player.sendMessage(plugin.getMessageManager().getItemDetailsCurrentBidderFormat(bidderName));
                        }
                    }

                    player.sendMessage(plugin.getMessageManager().getItemDetailsRemainingTimeFormat(item.getFormattedRemainingTime()));
                }
            }
            return;
        }

        // 处理上一页按钮
        else if (slot == 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
            // 打开上一页
            if (currentPage > 1) {
                currentPage--;
                openFilteredAuctionMenu(player, filter, currentPage);
            }
        }
        // 处理已售出按钮
        else if (slot == 46 && event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.GOLD_BLOCK || event.getCurrentItem().getType() == Material.GOLD_INGOT)) {
            // 切换到已售出筛选
            openFilteredAuctionMenu(player, "sold", 1);
        }
        // 处理已购买按钮
        else if (slot == 47 && event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.DIAMOND || event.getCurrentItem().getType() == Material.DIAMOND_BLOCK)) {
            // 切换到已购买筛选
            openFilteredAuctionMenu(player, "purchased", 1);
        }
        // 处理返回主菜单按钮
        else if (slot == 49 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
            // 返回主菜单
            plugin.getGuiManager().openMainMenu(player);
        }
        // 处理已过期/物品邮箱按钮
        else if (slot == 51 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST) {
            // 切换到物品邮箱
            openFilteredAuctionMenu(player, "mailbox", 1);
        }
        // 处理下一页按钮
        else if (slot == 53 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
            // 打开下一页
            int totalPages = 1;
            List<AuctionItem> items = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
            List<AuctionItem> filteredItems;

            // 根据筛选条件过滤物品
            if ("active".equals(filter)) {
                filteredItems = items.stream()
                        .filter(item -> item.isActive() && !item.isExpired())
                        .toList();
            } else if ("sold".equals(filter)) {
                filteredItems = items.stream()
                        .filter(item -> "SOLD".equals(item.getStatus()))
                        .toList();
            } else if ("purchased".equals(filter)) {
                // 已购买物品需要从数据库中获取
                filteredItems = plugin.getDatabaseManager().getPlayerPurchasedItems(player.getUniqueId(), 1, 1000);
            } else if ("mailbox".equals(filter)) {
                filteredItems = items.stream()
                        .filter(item -> "EXPIRED".equals(item.getStatus()) && item.getCurrentBidder() == null)
                        .toList();
            } else {
                filteredItems = items;
            }

            // 计算总页数
            totalPages = (int) Math.ceil((double) filteredItems.size() / 36);

            if (currentPage < totalPages) {
                if ("active".equals(filter)) {
                    plugin.getGuiManager().openMyAuctionsMenu(player, currentPage + 1);
                } else if ("sold".equals(filter)) {
                    plugin.getGuiManager().openMySoldAuctionsMenu(player, currentPage + 1);
                } else if ("purchased".equals(filter)) {
                    plugin.getGuiManager().openMyPurchasedAuctionsMenu(player, currentPage + 1);
                } else if ("mailbox".equals(filter)) {
                    plugin.getGuiManager().openMyMailboxMenu(player, currentPage + 1);
                }
            }
        }
    }

    // 处理取消拍卖
    private void handleCancelAuction(Player player, AuctionItem item) {
        // 创建同步锁，防止并发操作
        synchronized (plugin.getDatabaseManager()) {
            // 重新从数据库获取最新的物品信息，确保数据是最新的
            AuctionItem freshItem = plugin.getDatabaseManager().getAuctionItem(item.getId());
            if (freshItem != null) {
                item = freshItem;
            }

            // 强制再次检查是否有出价者
            if (item.getCurrentBidder() != null) {
                player.sendMessage(plugin.getMessageManager().getAuctionHasBidderMessage());
                return;
            }

            // 更改物品状态为已取消
            item.setStatus("CANCELLED");
            boolean updated = plugin.getDatabaseManager().updateAuctionItem(item);

            if (updated) {
                // 记录拍卖历史取消事件
                plugin.getAuctionHistoryManager().addCancelledEvent(player, item);

                // 物品返还给玩家
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item.getItem());

                if (leftover.isEmpty()) {
                    player.sendMessage(plugin.getMessageManager().getCancelAuctionSuccessMessage());
                } else {
                    // 物品无法完全添加到背包，存入待领取物品列表
                    plugin.getDatabaseManager().storePendingItem(player.getUniqueId(), item.getItem(), "拍卖取消，背包已满");
                    player.sendMessage(plugin.getMessageManager().getCancelAuctionSuccessInventoryFullMessage());
                }

                // 刷新界面
                plugin.getGuiManager().openMyAuctionsMenu(player, 1);
            } else {
                player.sendMessage(plugin.getMessageManager().getCancelAuctionFailMessage());
            }
        }
    }

    // 处理聊天消息（用于接收竞价金额和搜索关键词）
    @EventHandler
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // 处理搜索输入
        if (player.hasMetadata("auction_search_input")) {
            event.setCancelled(true); // 取消聊天消息
            player.removeMetadata("auction_search_input", plugin);

            final String searchText = event.getMessage().trim();
            if (searchText.isEmpty() || searchText.equals("取消") || searchText.equals("cancel")) {
                player.sendMessage(messageManager.getCancelSearchMessage());
                return;
            }

            // 使用调度器在主线程中运行GUI操作
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getGuiManager().openSearchResultMenu(player, searchText, 1);
            });
            return;
        }

        // 处理卖家搜索输入
        if (player.hasMetadata("auction_seller_search_input")) {
            event.setCancelled(true); // 取消聊天消息
            player.removeMetadata("auction_seller_search_input", plugin);

            final String sellerName = event.getMessage().trim();
            if (sellerName.isEmpty() || sellerName.equals("取消") || sellerName.equals("cancel")) {
                player.sendMessage(messageManager.getCancelSearchMessage());
                return;
            }

            // 使用调度器在主线程中运行GUI操作
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getGuiManager().openSellerSearchResultMenu(player, sellerName, 1);
            });
            return;
        }

        // 处理竞价输入
        if (player.hasMetadata("auction_bid_input")) {
            event.setCancelled(true); // 取消聊天消息
            player.removeMetadata("auction_bid_input", plugin);

            // 检查元数据
            if (!player.hasMetadata("auction_bid_id") || !player.hasMetadata("auction_item_price")) {
                player.sendMessage(messageManager.getIncompleteBidInfoMessage());
                return;
            }

            // 获取输入的金额
            String amountText = event.getMessage().trim();
            if (amountText.isEmpty() || amountText.equals("取消") || amountText.equals("cancel")) {
                player.sendMessage(messageManager.getCancelBidOperationMessage());
                return;
            }

            // 尝试解析为数字
            double bidAmount;
            try {
                bidAmount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                player.sendMessage(messageManager.getBidInvalidAmountMessage());
                return;
            }

            // 获取物品信息
            int itemId = player.getMetadata("auction_bid_id").get(0).asInt();
            double currentPrice = player.getMetadata("auction_item_price").get(0).asDouble();

            // 获取完整的物品信息
            AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
            if (item == null || !item.isActive()) {
                player.sendMessage(messageManager.getExpiredBidInfoMessage());
                cleanupBidMetadata(player);
                return;
            }

            // 检查输入的金额是否足够高
            double minimumBid = plugin.getEconomyManager().calculateMinBid(currentPrice, item.getCurrencyType());
            if (bidAmount < minimumBid) {
                player.sendMessage(messageManager.getBidBelowMinimumMessage(
                        plugin.getEconomyManager().formatAmount(minimumBid, item.getCurrencyType())));
                return;
            }

            // 检查金额是否低于当前价格
            if (bidAmount <= currentPrice) {
                player.sendMessage(messageManager.getBidBelowCurrentPriceMessage());
                return;
            }

            // 存储竞价金额
            player.setMetadata("auction_bid_amount", new FixedMetadataValue(plugin, bidAmount));

            // 更新竞价界面
            updateBidMenuDisplay(player, item, bidAmount);

            player.sendMessage(messageManager.getPreBidMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
            player.sendMessage(messageManager.getBidAmountMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));

            // 打开确认界面，在主线程中执行
            final int finalItemId = itemId;
            final double finalBidAmount = bidAmount;
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getGuiManager().openBidMenu(player, finalItemId, finalBidAmount);
            });
        }
    }

    @EventHandler
    public void onPlayerCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();

        // 检查命令是否是/bid或/竞价
        if (command.startsWith("/bid ") || command.startsWith("/竞价 ")) {
            event.setCancelled(true);

            try {
                // 解析物品ID
                String[] args = command.split(" ");
                if (args.length < 2) {
                    player.sendMessage(plugin.getMessageManager().getBidUsage());
                    return;
                }

                int itemId;
                try {
                    itemId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessageManager().getBidIdMustBeNumber());
                    return;
                }

                // 获取拍卖物品
                AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
                if (item == null || !item.isActive() || item.isExpired()) {
                    player.sendMessage(plugin.getMessageManager().getBidInvalidItem() + itemId);
                    return;
                }

                // 检查权限
                if (!player.hasPermission("globalshop.bid")) {
                    player.sendMessage(plugin.getMessageManager().getBidNoPermission());
                    return;
                }

                // 检查是否是自己的物品
                if (item.getSellerUuid().equals(player.getUniqueId())) {
                    player.sendMessage(plugin.getMessageManager().getBidOwnItem());
                    return;
                }

                // 检查是否已是当前最高出价者
                if (item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId())) {
                    player.sendMessage(plugin.getMessageManager().getBidAlreadyHighest());
                    return;
                }

                // 打开竞价界面
                player.sendMessage(plugin.getMessageManager().getBidOpeningMenu());
                plugin.getGuiManager().openBidMenu(player, item);

            } catch (Exception e) {
                player.sendMessage(plugin.getMessageManager().getBidError() + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleBuyNow(Player player, AuctionItem item) {
        // 检查物品是否可以一口价购买
        if (!item.canBuyNow()) {
            player.sendMessage(plugin.getMessageManager().getBuyNowNotAvailable());
            return;
        }

        // 检查货币类型
        if ("POINTS".equals(item.getCurrencyType()) && !plugin.isPlayerPointsAvailable()) {
            player.sendMessage(plugin.getMessageManager().getBuyNowPointsUnavailable());
            return;
        }

        // 检查玩家是否有足够的钱
        if (!plugin.getEconomyManager().hasEnough(player, item.getBuyNowPrice(), item.getCurrencyType())) {
            player.sendMessage(plugin.getMessageManager().getBuyNowNotEnoughMoney()
                    .replace("%currency%", plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()))
                    .replace("%amount%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            return;
        }

        // 检查玩家背包是否有足够空间
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessageManager().getBuyNowInventoryFull());
            return;
        }

        // 扣除玩家的钱
        plugin.getEconomyManager().takeMoney(player, item.getBuyNowPrice(), item.getCurrencyType());

        // 给卖家钱（扣除手续费）
        double sellerAmount = item.getBuyNowPrice() - plugin.getEconomyManager().calculateFee(item.getBuyNowPrice(), item.getCurrencyType());
        plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerAmount, item.getCurrencyType());

        // 更新物品状态
        item.setStatus("SOLD");
        item.setCurrentBidder(player.getUniqueId());
        item.setCurrentBidderName(player.getName());
        item.setCurrentPrice(item.getBuyNowPrice());
        item.setSoldTime(System.currentTimeMillis());
        plugin.getDatabaseManager().updateAuctionItem(item);

        // 记录拍卖历史购买事件
        plugin.getAuctionHistoryManager().addBuyEvent(player, item);

        // 尝试直接放入背包，如果背包满了则放入邮箱
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item.getItem());
            player.sendMessage(plugin.getMessageManager().getBuyNowSuccess());
            player.sendMessage(plugin.getMessageManager().getBuyNowItem()
                    .replace("%item%", ChatUtils.getItemName(item.getItem())));
            player.sendMessage(plugin.getMessageManager().getBuyNowPrice()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            // 显示玩家余额
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage(plugin.getMessageManager().getBuyNowBalance()
                    .replace("%currency%", plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()))
                    .replace("%amount%", plugin.getEconomyManager().formatAmount(balance, item.getCurrencyType())));
        } else {
            // 背包已满，放入邮箱
            addToMailbox(item, player.getUniqueId(), "BUY_NOW");
            player.sendMessage(plugin.getMessageManager().getBuyNowInventoryFullMailbox());
            player.sendMessage(plugin.getMessageManager().getBuyNowItem()
                    .replace("%item%", ChatUtils.getItemName(item.getItem())));
            player.sendMessage(plugin.getMessageManager().getBuyNowPrice()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            // 显示玩家余额
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage(plugin.getMessageManager().getBuyNowBalance()
                    .replace("%currency%", plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()))
                    .replace("%amount%", plugin.getEconomyManager().formatAmount(balance, item.getCurrencyType())));
            player.sendMessage(plugin.getMessageManager().getBuyNowMailboxInstructions());
        }

        // 通知卖家
        Player seller = Bukkit.getPlayer(item.getSellerUuid());
        if (seller != null && seller.isOnline()) {
            seller.sendMessage(plugin.getMessageManager().getBuyNowSellerNotification());
            seller.sendMessage(plugin.getMessageManager().getBuyNowItem()
                    .replace("%item%", ChatUtils.getItemName(item.getItem())));
            seller.sendMessage(plugin.getMessageManager().getBuyNowPrice()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            seller.sendMessage(plugin.getMessageManager().getBuyNowSellerIncome()
                    .replace("%amount%", plugin.getEconomyManager().formatAmount(sellerAmount, item.getCurrencyType())));
            // 显示卖家余额信息
            double sellerBalance = plugin.getEconomyManager().getBalance(seller, item.getCurrencyType());
            seller.sendMessage(plugin.getMessageManager().getBuyNowSellerBalance()
                    .replace("%currency%", plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()))
                    .replace("%amount%", plugin.getEconomyManager().formatAmount(sellerBalance, item.getCurrencyType())));
            seller.sendMessage(plugin.getMessageManager().getBuyNowBuyer()
                    .replace("%player%", player.getName()));
        }

        // 刷新界面
        plugin.getGuiManager().openMainMenu(player);
    }

    // 添加邮箱物品的统一处理方法
    private void addToMailbox(AuctionItem item, UUID ownerUuid, String reason) {
        ItemStack itemStack = item.getItem().clone();
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        // 添加邮箱标记和来源信息
        lore.add(plugin.getMessageManager().getAuctionItemDivider());

        if ("AUCTION_WON".equals(reason)) {
            lore.add(plugin.getMessageManager().getMailboxStorageWon());
            lore.add(plugin.getMessageManager().getAuctionDealPriceFormat()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType())));
            lore.add(plugin.getMessageManager().getMailboxItemAddTime()
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("AUCTION_EXPIRED".equals(reason)) {
            lore.add(plugin.getMessageManager().getMailboxStorageExpired());
            lore.add(plugin.getMessageManager().getMailboxItemExpireTime()
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("BUY_NOW".equals(reason)) {
            lore.add(plugin.getMessageManager().getMailboxStorageFull());
            lore.add(plugin.getMessageManager().getAuctionDealPriceFormat()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            lore.add(plugin.getMessageManager().getMailboxItemAddTime()
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        // 存储到数据库
        plugin.getDatabaseManager().storePendingItem(ownerUuid, itemStack, reason);
    }

    /**
     * 检查玩家背包是否有足够空间容纳指定物品
     * @param player 玩家
     * @param item 要添加的物品
     * @return 如果有足够空间返回true，否则返回false
     */
    private boolean hasInventorySpace(Player player, ItemStack item) {
        // 创建物品的副本，因为我们只需要检查空间，不需要实际添加
        ItemStack itemCopy = item.clone();

        // 获取玩家背包的临时副本
        Inventory tempInventory = Bukkit.createInventory(null, 36);
        ItemStack[] contents = player.getInventory().getStorageContents().clone();
        tempInventory.setContents(contents);

        // 尝试添加物品到临时背包
        HashMap<Integer, ItemStack> leftover = tempInventory.addItem(itemCopy);

        // 如果没有剩余物品，说明有足够空间
        return leftover.isEmpty();
    }

    // 处理竞价按钮点击
    private void handleBidIncreaseButton(Player player) {
        // 获取拍卖物品ID
        if (!player.hasMetadata("auction_bid_id")) {
            player.closeInventory();
            player.sendMessage(messageManager.getExpiredBidInfoMessage());
            return;
        }

        int itemId = player.getMetadata("auction_bid_id").get(0).asInt();

        // 获取拍卖物品的最新信息
        AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
        if (item == null) {
            player.sendMessage(messageManager.getExpiredItemMessage());
            player.closeInventory();
            return;
        }

        // 检查物品状态
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage(messageManager.getExpiredItemMessage());
            player.closeInventory();
            return;
        }

        // 检查是否为卖家本人
        if (item.getSellerUuid().equals(player.getUniqueId())) {
            player.sendMessage(messageManager.getOwnerBidMessage());
            player.closeInventory();
            return;
        }

        // 始终使用物品的最新当前价格，而不是缓存的价格
        double currentPrice = item.getCurrentPrice();

        // 更新元数据中的当前价格，确保使用最新值
        player.setMetadata("auction_item_price", new FixedMetadataValue(plugin, currentPrice));

        // 获取货币类型和最低加价金额
        String currencyType = item.getCurrencyType();
        double minBidIncrease = plugin.getEconomyManager().calculateMinBid(currentPrice, currencyType);

        // 计算新的出价金额 - 使用当前的预加价金额（如果存在），否则使用物品的当前最新价格
        double currentBidAmount = player.hasMetadata("auction_bid_amount") ?
                                player.getMetadata("auction_bid_amount").get(0).asDouble() :
                                currentPrice;

        double newBid = currentBidAmount + minBidIncrease;

        // 检查新出价是否大于最低加价要求 - 只有首次加价时才需要检查
        if (!player.hasMetadata("auction_bid_amount") && newBid < currentPrice + minBidIncrease) {
            player.sendMessage(messageManager.getBidBelowMinimumMessage(plugin.getEconomyManager().formatAmount(minBidIncrease, currencyType)));
            return;
        }

        // 检查是否达到一口价
        if (item.hasBuyNowPrice() && newBid >= item.getBuyNowPrice()) {
            newBid = item.getBuyNowPrice();
        }

        // 检查玩家是否有足够的钱
        if (!plugin.getEconomyManager().hasEnough(player, newBid, currencyType)) {
            player.sendMessage(messageManager.getNotEnoughMoneyMessage(plugin.getEconomyManager().getCurrencyName(currencyType)));
            return;
        }

        // 更新玩家元数据中的竞价金额和标记为加价操作进行中
        player.setMetadata("auction_bid_amount", new FixedMetadataValue(plugin, newBid));
        player.setMetadata("auction_bid_in_progress", new FixedMetadataValue(plugin, true));

        // 通知玩家已预加价，等待确认
        player.sendMessage(messageManager.getPreBidMessage(plugin.getEconomyManager().formatAmount(newBid, currencyType)));
        player.sendMessage(messageManager.getBidAmountMessage(plugin.getEconomyManager().formatAmount(newBid, currencyType)));

        // 直接更新当前界面的预加价信息
        updateBidMenuDisplay(player, item, newBid);
    }

    /**
     * 更新竞价界面显示，不关闭和重新打开界面
     * @param player 玩家
     * @param item 拍卖物品
     * @param bidAmount 预加价金额
     */
    private void updateBidMenuDisplay(Player player, AuctionItem item, double bidAmount) {
        // 获取当前打开的界面
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof Player)) {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            // 确保是在竞价界面
            if (player.getOpenInventory().getTitle().equals(messageManager.getBidMenuTitle())) {
                String currencyType = item.getCurrencyType();
                double currentPrice = item.getCurrentPrice();

                // 获取当前货币类型的最低加价比例
                double bidRatePercent = currencyType.equalsIgnoreCase("VAULT") ?
                        configManager.getVaultMinBidRate() * 100 :
                        configManager.getPointsMinBidRate() * 100;

                // 更新当前竞价金额信息
                ItemStack currentBidInfo = inventory.getItem(4);
                if (currentBidInfo != null && currentBidInfo.getType() == Material.PAPER) {
                    ItemMeta meta = currentBidInfo.getItemMeta();
                    List<String> lore = new ArrayList<>();

                    // 基本信息
                    lore.add(messageManager.getCurrentBidItemText().replace("%item_name%", ChatUtils.getItemName(item.getItem())));
                    lore.add(messageManager.getCurrentBidOriginalPriceText().replace("%price%", plugin.getEconomyManager().formatAmount(item.getStartPrice(), currencyType)));
                    lore.add(messageManager.getCurrentBidCurrentPriceText().replace("%price%", plugin.getEconomyManager().formatAmount(currentPrice, currencyType)));

                    // 添加预加价信息
                    lore.add(messageManager.getCurrentBidPreAmountText().replace("%price%", plugin.getEconomyManager().formatAmount(bidAmount, currencyType)));
                    lore.add(messageManager.getCurrentBidRateText().replace("%rate%", String.valueOf(bidRatePercent)));

                    meta.setLore(lore);
                    currentBidInfo.setItemMeta(meta);
                }

                // 更新预加价按钮信息
                ItemStack increaseBid = inventory.getItem(11);
                if (increaseBid != null && increaseBid.getType() == Material.LAPIS_BLOCK) {
                    ItemMeta meta = increaseBid.getItemMeta();
                    List<String> lore = new ArrayList<>();

                    lore.add(messageManager.getIncreaseBidTipText());
                    lore.add(messageManager.getIncreaseBidMinText().replace("%price%", plugin.getEconomyManager().formatAmount(plugin.getEconomyManager().calculateMinBid(currentPrice, currencyType), currencyType)));
                    lore.add(messageManager.getCurrentBidPreAmountText().replace("%price%", plugin.getEconomyManager().formatAmount(bidAmount, currencyType)));

                    meta.setLore(lore);
                    increaseBid.setItemMeta(meta);
                }

                // 更新确认按钮信息
                ItemStack confirmButton = inventory.getItem(15);
                if (confirmButton != null && confirmButton.getType() == Material.EMERALD_BLOCK) {
                    ItemMeta meta = confirmButton.getItemMeta();
                    List<String> lore = new ArrayList<>();

                    lore.add(messageManager.getConfirmBidTipText());
                    lore.add(messageManager.getConfirmBidPreAmountText().replace("%price%", plugin.getEconomyManager().formatAmount(bidAmount, currencyType)));

                    meta.setLore(lore);
                    confirmButton.setItemMeta(meta);
                }
            }
        }
    }

    // 处理确认竞价按钮点击
    private void handleConfirmBidButton(InventoryClickEvent event, Player player) {
        // 获取竞价信息
        if (!player.hasMetadata("auction_bid_id") || !player.hasMetadata("auction_bid_amount")) {
            player.sendMessage(messageManager.getIncompleteBidInfoMessage());
            return;
        }

        int auctionId = player.getMetadata("auction_bid_id").get(0).asInt();
        double bidAmount = player.getMetadata("auction_bid_amount").get(0).asDouble();

        // 获取拍卖物品的最新信息
        AuctionItem item = plugin.getDatabaseManager().getAuctionItem(auctionId);
        if (item == null) {
            player.sendMessage(messageManager.getInvalidItemMessage());
            return;
        }

        // 检查货币类型
        if ("POINTS".equals(item.getCurrencyType()) && !plugin.isPlayerPointsAvailable()) {
            player.sendMessage(messageManager.getPointsUnavailableMessage());
            return;
        }

        // 检查物品是否过期
        if (item.isExpired()) {
            player.sendMessage(messageManager.getExpiredBidMessage());
            return;
        }

        // 获取最新的物品当前价格
        double currentPrice = item.getCurrentPrice();

        // 检查是否已达到一口价
        boolean reachedBuyNowPrice = item.hasBuyNowPrice() && bidAmount >= item.getBuyNowPrice();

        // 如果没有达到一口价，检查竞价是否合法
        if (!reachedBuyNowPrice) {
            double minBidIncrease = plugin.getEconomyManager().calculateMinBid(currentPrice, item.getCurrencyType());
            if (bidAmount < currentPrice + minBidIncrease) {
                player.sendMessage(messageManager.getBidBelowCurrentPriceMessage());
                // 更新元数据中的当前价格
                player.setMetadata("auction_item_price", new FixedMetadataValue(plugin, currentPrice));
                // 关闭界面并重新打开竞价界面
                player.closeInventory();
                plugin.getGuiManager().openBidMenu(player, auctionId);
                return;
            }
        }

        // 检查玩家是否有足够的钱
        if (!plugin.getEconomyManager().hasEnough(player, bidAmount, item.getCurrencyType())) {
            player.sendMessage(messageManager.getNotEnoughMoneyMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType())));
            return;
        }

        // 设置确认标记，避免在界面关闭时显示取消消息
        player.setMetadata("auction_bid_confirmed", new FixedMetadataValue(plugin, true));

        // 如果玩家是当前的最高出价者，只需要支付差价
        double amountToDeduct = bidAmount;
        if (item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId())) {
            double previousBid = item.getCurrentPrice();
            amountToDeduct = bidAmount - previousBid;
        }

        // 扣除玩家的钱
        if (!plugin.getEconomyManager().takeMoney(player, amountToDeduct, item.getCurrencyType())) {
            player.sendMessage(messageManager.getFailedTransactionMessage());
            return;
        }

        // 如果之前有其他出价者，退还他们的钱
        if (item.getCurrentBidder() != null && !item.getCurrentBidder().equals(player.getUniqueId())) {
            double previousBid = item.getCurrentPrice();
            plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getCurrentBidder()), previousBid, item.getCurrencyType());

            // 如果之前的出价者在线，发送通知
            Player previousBidder = Bukkit.getPlayer(item.getCurrentBidder());
            if (previousBidder != null && previousBidder.isOnline()) {
                previousBidder.sendMessage(messageManager.getBidOutbidMessage(item.getId(), plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
                previousBidder.sendMessage(messageManager.getPreviousBidMessage(plugin.getEconomyManager().formatAmount(previousBid, item.getCurrencyType())));
                previousBidder.sendMessage(messageManager.getNewBidMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
                // 显示余额信息
                double bidderBalance = plugin.getEconomyManager().getBalance(previousBidder, item.getCurrencyType());
                previousBidder.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), bidderBalance));
            }
        }

        // 更新物品信息
        item.setCurrentPrice(bidAmount);
        item.setCurrentBidder(player.getUniqueId());
        item.setCurrentBidderName(player.getName());
        plugin.getDatabaseManager().updateAuctionItem(item);

        double bidderBalance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());

        player.sendMessage(messageManager.getBidAcceptedMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
        player.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), bidderBalance));

        // 如果达到一口价，直接完成拍卖
        if (reachedBuyNowPrice) {
            // 标记为已售出
            item.setStatus("SOLD");
            item.setCurrentBidderName(player.getName());
            item.setSoldTime(System.currentTimeMillis());
            plugin.getDatabaseManager().updateAuctionItem(item);

            // 检查玩家背包是否有足够空间
            boolean hasSpace = hasInventorySpace(player, item.getItem());

            if (hasSpace) {
                // 将物品给予买家
                player.getInventory().addItem(item.getItem());
                player.sendMessage(messageManager.getPurchaseSuccessMessage());
                player.sendMessage(messageManager.getPurchaseItemMessage(ChatUtils.getItemName(item.getItem())));
                player.sendMessage(messageManager.getPurchasePriceMessage(plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
                // 显示余额信息
                double updatedBalance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
                player.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), updatedBalance));
            } else {
                // 如果背包已满，将物品添加到邮箱
                addToMailbox(item, player.getUniqueId(), "BUY_NOW");
                player.sendMessage(messageManager.getMailboxFullMessage());
                player.sendMessage(messageManager.getPurchaseItemMessage(ChatUtils.getItemName(item.getItem())));
                player.sendMessage(messageManager.getPurchasePriceMessage(plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
                // 显示余额信息
                double updatedBalance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
                player.sendMessage(messageManager.getBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), updatedBalance));
            }

            // 给卖家钱（减去手续费）
            double fee = plugin.getEconomyManager().calculateFee(bidAmount, item.getCurrencyType());
            double sellerReceives = bidAmount - fee;
            plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerReceives, item.getCurrencyType());

            // 发送购买成功消息
            player.sendMessage(messageManager.getPurchaseSuccessMessage());
            player.sendMessage(messageManager.getPurchaseItemMessage(String.valueOf(item.getId())));
            player.sendMessage(messageManager.getPurchasePriceMessage(plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));

            // 卖家在线则发送通知
            Player seller = Bukkit.getPlayer(item.getSellerUuid());
            if (seller != null && seller.isOnline()) {
                seller.sendMessage(messageManager.getSoldNotification(item.getId(), plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
                seller.sendMessage(messageManager.getSellerFeeMessage(plugin.getEconomyManager().formatAmount(fee, item.getCurrencyType())));
                seller.sendMessage(messageManager.getSellerIncomeMessage(plugin.getEconomyManager().formatAmount(sellerReceives, item.getCurrencyType())));
                // 显示卖家余额信息
                double sellerBalance = plugin.getEconomyManager().getBalance(seller, item.getCurrencyType());
                seller.sendMessage(messageManager.getSellerBalanceMessage(plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()), sellerBalance));
                seller.sendMessage(messageManager.getBuyerMessage(player.getName()));
            }

            // 广播购买消息
            plugin.getBroadcastManager().broadcastBuyNow(player.getName(), item.getSellerName(), item);
        } else {
            // 竞价成功但未达到一口价，更新数据库
            plugin.getDatabaseManager().updateAuctionItem(item);

            // 记录拍卖历史竞价事件
            plugin.getAuctionHistoryManager().addBidEvent(player, item);

            // 发送竞价成功消息
            player.sendMessage(messageManager.getBidSuccessMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
            player.sendMessage(messageManager.getBidItemMessage(String.valueOf(item.getId())));
            player.sendMessage(messageManager.getBidPriceMessage(plugin.getEconomyManager().formatAmount(bidAmount, item.getCurrencyType())));
            // 广播竞价确认消息
            plugin.getBroadcastManager().broadcastBidConfirmed(player.getName(), item);
        }

        // 关闭界面时会自动清理元数据，因为我们已经设置了auction_bid_confirmed标记
        player.closeInventory();

        // 界面关闭后打开主菜单
        plugin.getGuiManager().openMainMenu(player);
    }

    // 补充方法名对应
    private void handleSearchResultsClick(InventoryClickEvent event, Player player) {
        handleSearchResultClick(event, player);
    }

    private void handleMyAuctionsClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    private void handleMySoldAuctionsClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    private void handleMyPurchasedAuctionsClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    /**
     * 处理排序按钮点击
     * @param player 玩家
     */
    private void handleSortButtonClick(Player player) {
        // 切换到下一个排序类型
        cn.i7mc.globalshop.enums.SortType newSortType = plugin.getSortManager().togglePlayerSortType(player);

        // 获取新排序类型的显示名称
        String sortDisplayName = plugin.getSortManager().getSortDisplayName(newSortType);

        // 发送切换消息
        player.sendMessage(messageManager.getSortChangedMessage(sortDisplayName));

        // 获取当前页码
        int currentPage = plugin.getGuiManager().getPlayerPage(player);

        // 重新打开主界面以应用新的排序
        plugin.getGuiManager().openMainMenu(player, currentPage);
    }

    private void handleMailboxClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    private void handleMyMailboxClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    private void handleExpiredAuctionsClick(InventoryClickEvent event, Player player) {
        handleMyAuctionsMenuClick(event, player);
    }

    /**
     * 处理管理员强制下架物品
     * @param player 管理员玩家
     * @param item 要下架的物品
     */
    private void handleForceRemoveAuction(Player player, AuctionItem item) {
        // 验证玩家是OP
        if (!player.isOp()) {
            player.sendMessage(messageManager.getNoPermissionMessage());
            return;
        }

        // 验证物品是否活跃
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage(messageManager.getExpiredItemMessage());
            return;
        }

        // 获取物品所有者
        UUID sellerUuid = item.getSellerUuid();
        String sellerName = item.getSellerName();

        // 保存当前价格和货币类型到局部变量，以便后续使用
        double itemCurrentPrice = item.getCurrentPrice();
        String itemCurrencyType = item.getCurrencyType();

        // 如果存在竞价，退还给竞价者
        if (item.getCurrentBidder() != null) {
            UUID bidderUuid = item.getCurrentBidder();

            // 退还竞价金额
            if (itemCurrencyType.equalsIgnoreCase("VAULT")) {
                org.bukkit.OfflinePlayer bidder = Bukkit.getOfflinePlayer(bidderUuid);
                plugin.getEconomyManager().giveMoney(bidder, itemCurrentPrice, itemCurrencyType);
            } else if (itemCurrencyType.equalsIgnoreCase("POINTS")) {
                plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(bidderUuid), itemCurrentPrice, itemCurrencyType);
            }

            // 通知竞价者(确保不是管理员自己)
            Player bidder = Bukkit.getPlayer(bidderUuid);
            if (bidder != null && bidder.isOnline() && !bidder.equals(player)) {
                bidder.sendMessage(messageManager.getRefundMessage(plugin.getEconomyManager().formatAmount(itemCurrentPrice, itemCurrencyType)));
            }
        }

        // 将物品状态设置为CANCELLED而不是EXPIRED
        // 使用CANCELLED状态可避免与自动过期处理逻辑冲突
        item.setStatus("CANCELLED");

        // 更新数据库中的物品状态
        plugin.getDatabaseManager().updateAuctionItem(item);

        // 记录拍卖历史取消事件
        plugin.getAuctionHistoryManager().addCancelledEvent(player, item);

        // 将物品直接添加到卖家邮箱
        // 明确的理由说明这是被管理员强制下架的
        plugin.getDatabaseManager().storePendingItem(sellerUuid, item.getItem(), "管理员强制下架");

        // 判断管理员是否是卖家自己
        boolean isManagerSeller = player.getUniqueId().equals(sellerUuid);

        // 通知管理员
        player.sendMessage(messageManager.getRemovedItemMessage(item.getId(), sellerName));

        // 通知卖家(如果卖家不是管理员自己)
        Player seller = Bukkit.getPlayer(sellerUuid);
        if (seller != null && seller.isOnline() && !isManagerSeller) {
            seller.sendMessage(messageManager.getRemovedItemMessage(item.getId(), sellerName));
            seller.sendMessage(messageManager.getRefundMessage(plugin.getEconomyManager().formatAmount(itemCurrentPrice, itemCurrencyType)));
        }

        // 刷新界面
        plugin.getGuiManager().openMainMenu(player);
    }

    /**
     * 清理玩家所有竞价相关的元数据
     * @param player 要清理元数据的玩家
     */
    private void cleanupBidMetadata(Player player) {
        if (player.hasMetadata("auction_bid_id")) {
            player.removeMetadata("auction_bid_id", plugin);
        }
        if (player.hasMetadata("auction_item_price")) {
            player.removeMetadata("auction_item_price", plugin);
        }
        if (player.hasMetadata("auction_bid_amount")) {
            player.removeMetadata("auction_bid_amount", plugin);
        }
        if (player.hasMetadata("auction_bid_in_progress")) {
            player.removeMetadata("auction_bid_in_progress", plugin);
        }
        if (player.hasMetadata("auction_bid_confirmed")) {
            player.removeMetadata("auction_bid_confirmed", plugin);
        }
    }

    /**
     * 根据筛选条件打开相应的拍卖界面
     * @param player 玩家
     * @param filter 筛选条件 (active, sold, purchased, mailbox)
     * @param page 页码
     */
    private void openFilteredAuctionMenu(Player player, String filter, int page) {
        switch(filter) {
            case "active":
                plugin.getGuiManager().openMyAuctionsMenu(player, page);
                break;
            case "sold":
                plugin.getGuiManager().openMySoldAuctionsMenu(player, page);
                break;
            case "purchased":
                plugin.getGuiManager().openMyPurchasedAuctionsMenu(player, page);
                break;
            case "mailbox":
                plugin.getGuiManager().openMyMailboxMenu(player, page);
                break;
            default:
                plugin.getGuiManager().openMyAuctionsMenu(player, page);
                break;
        }
    }

    // --- 新增：管理员查询界面点击处理方法 --- START ---

    private void handlePlayerInfoClick(InventoryClickEvent event, Player player, PlayerInfoHolder holder) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        int slot = event.getRawSlot();
        OfflinePlayer targetPlayer = holder.getTargetPlayer();

        // 销售记录按钮
        if (slot == 30 && clickedItem.getType() == Material.GOLD_INGOT) {
            guiManager.openPlayerSalesHistoryMenu(player, targetPlayer, 1);
            return;
        }

        // 购买记录按钮
        if (slot == 32 && clickedItem.getType() == Material.DIAMOND) {
            guiManager.openPlayerPurchaseHistoryMenu(player, targetPlayer, 1);
            return;
        }

        // 返回按钮 (关闭当前界面)
        if (slot == 49 && clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }
    }

    private void handlePlayerSalesHistoryClick(InventoryClickEvent event, Player player, PlayerSalesHistoryHolder holder) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        int slot = event.getRawSlot();
        OfflinePlayer targetPlayer = holder.getTargetPlayer();
        int currentPage = holder.getCurrentPage();

        // 上一页按钮
        if (slot == 48 && clickedItem.getType() == Material.ARROW) {
            if (currentPage > 1) {
                guiManager.openPlayerSalesHistoryMenu(player, targetPlayer, currentPage - 1);
            }
            return;
        }

        // 下一页按钮
        if (slot == 50 && clickedItem.getType() == Material.ARROW) {
             // 计算总页数 (这里需要再次查询数据库，或者传递 totalPages)
             int totalItems = plugin.getDatabaseManager().countPlayerSoldItems(targetPlayer.getUniqueId());
             int itemsPerPage = 45;
             int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
             if (totalPages == 0) totalPages = 1;

             if (currentPage < totalPages) {
                guiManager.openPlayerSalesHistoryMenu(player, targetPlayer, currentPage + 1);
             }
            return;
        }

        // 返回按钮 -> 玩家信息主界面
        if (slot == 49 && clickedItem.getType() == Material.ARROW) {
            guiManager.openPlayerInfoMenu(player, targetPlayer);
            return;
        }

        // 点击物品本身不做任何操作
        if (slot < 45) {
            // 未来可以添加复制物品ID等功能
            return;
        }
    }

    private void handlePlayerPurchaseHistoryClick(InventoryClickEvent event, Player player, PlayerPurchaseHistoryHolder holder) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        int slot = event.getRawSlot();
        OfflinePlayer targetPlayer = holder.getTargetPlayer();
        int currentPage = holder.getCurrentPage();

        // 上一页按钮
        if (slot == 48 && clickedItem.getType() == Material.ARROW) {
            if (currentPage > 1) {
                guiManager.openPlayerPurchaseHistoryMenu(player, targetPlayer, currentPage - 1);
            }
            return;
        }

        // 下一页按钮
        if (slot == 50 && clickedItem.getType() == Material.ARROW) {
            // 计算总页数
            int totalItems = plugin.getDatabaseManager().countPlayerPurchasedItems(targetPlayer.getUniqueId());
            int itemsPerPage = 45;
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
            if (totalPages == 0) totalPages = 1;

            if (currentPage < totalPages) {
                 guiManager.openPlayerPurchaseHistoryMenu(player, targetPlayer, currentPage + 1);
            }
            return;
        }

        // 返回按钮 -> 玩家信息主界面
        if (slot == 49 && clickedItem.getType() == Material.ARROW) {
            guiManager.openPlayerInfoMenu(player, targetPlayer);
            return;
        }

        // 点击物品本身不做任何操作
        if (slot < 45) {
            // 未来可以添加复制物品ID等功能
            return;
        }
    }

    // --- 新增：管理员查询界面点击处理方法 --- END ---
}