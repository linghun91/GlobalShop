package cn.i7mc.globalshop.gui;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import cn.i7mc.globalshop.utils.ChatUtils;
import cn.i7mc.globalshop.config.ConfigManager;
import cn.i7mc.globalshop.config.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GuiManager {
    private final GlobalShop plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Map<Player, Integer> playerPages;
    private final Map<Player, String> playerSearchQueries;

    public GuiManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
        this.playerPages = new ConcurrentHashMap<>();
        this.playerSearchQueries = new ConcurrentHashMap<>();
    }

    // 打开主界面
    public void openMainMenu(Player player) {
        int page = playerPages.getOrDefault(player, 1);
        openMainMenu(player, page);
    }

    // 打开主界面(指定页码)
    public void openMainMenu(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, configManager.getGuiSize(),
                configManager.getGuiTitle());

        // 获取当前页的拍卖物品 - 修改为45个物品而不是42个
        List<AuctionItem> items = plugin.getDatabaseManager().getActiveAuctionItems(page, 45);
        
        // 获取总物品数量用于分页
        int totalItems = plugin.getDatabaseManager().getTotalActiveItems();
        int itemsPerPage = 45; // 每页45个物品（5行9列）
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // 设置物品
        for (int i = 0; i < items.size(); i++) {
            AuctionItem item = items.get(i);
            inventory.setItem(i, createAuctionItemDisplay(item, player));
        }

        // 设置导航按钮和分页按钮
        setNavigationButtons(inventory);
        setPaginationButtons(inventory, page, totalPages);

        // 保存当前页码到玩家元数据中
        player.setMetadata("auction_page", new FixedMetadataValue(plugin, page));
        
        // 打开界面
        player.openInventory(inventory);
        playerPages.put(player, page);
    }

    // 创建拍卖物品显示
    private ItemStack createAuctionItemDisplay(AuctionItem item) {
        return createAuctionItemDisplay(item, null);
    }
    
    // 创建拍卖物品显示（带玩家判断）
    private ItemStack createAuctionItemDisplay(AuctionItem item, Player player) {
        ItemStack display = item.getItem().clone();
        ItemMeta meta = display.getItemMeta();
        
        // 获取原有的Lore
        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();
        
        // 创建拍卖信息Lore
        List<String> auctionLore = new ArrayList<>();
        
        // 添加分隔线
        auctionLore.add("§8§m--------------------");
        auctionLore.add("§6§l拍卖信息:");
        
        // 添加物品ID信息
        auctionLore.add("§e物品ID: §f" + item.getId());
        
        // 如果是原版物品没有自定义名称，添加中文翻译
        if (!meta.hasDisplayName()) {
            String materialName = display.getType().name().toLowerCase();
            // 获取中文名称
            String chineseName = plugin.getLanguageManager().getChineseName(materialName);
            if (!chineseName.equals(materialName)) {
                // 设置中文物品名称
                meta.setDisplayName("§f" + chineseName);
            }
        }
        
        // 获取货币类型名称
        String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());
        
        // 添加货币类型和价格信息
        auctionLore.add("§e货币类型: §f" + currencyName);
        auctionLore.add("§e起拍价: §f" + plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
        auctionLore.add("§e当前价: §f" + plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
        if (item.hasBuyNowPrice()) {
            auctionLore.add("§e一口价: §f" + plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
        }

        // 添加时间信息
        auctionLore.add("§e上架时间: §f" + item.getFormattedListTime());
        auctionLore.add("§e剩余时间: §f" + item.getFormattedRemainingTime());

        // 添加卖家信息
        auctionLore.add("§e卖家: §f" + item.getSellerName());

        // 添加当前最高出价者信息
        if (item.getCurrentBidder() != null) {
            auctionLore.add("§e当前出价者: §f" + Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName());
        }

        // 添加操作提示
        auctionLore.add("§8§m--------------------");
        
        // 根据玩家是否为物品主人显示不同操作提示
        boolean isOwner = player != null && item.getSellerUuid().equals(player.getUniqueId());
        
        if (isOwner) {
            // 如果是物品主人
            auctionLore.add("§7这是你的拍卖物品");
            auctionLore.add("§7Shift+右键点击: §f快速下架");
        } else {
            // 如果不是物品主人
            auctionLore.add("§7左键点击: §f参与竞价");
            auctionLore.add("§7右键点击: §f快速购买");
        }
        
        // 合并原有Lore和拍卖信息
        List<String> combinedLore = new ArrayList<>(originalLore);
        combinedLore.addAll(auctionLore);
        
        meta.setLore(combinedLore);
        display.setItemMeta(meta);
        return display;
    }

    /**
     * 设置导航按钮
     * @param inv 物品栏
     */
    private void setNavigationButtons(Inventory inv) {
        // 设置功能按钮
        ItemStack searchButton = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = searchButton.getItemMeta();
        searchMeta.setDisplayName(messageManager.getSearchItemsText());
        searchMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        searchButton.setItemMeta(searchMeta);
        inv.setItem(47, searchButton);
        
        // 设置上架按钮
        ItemStack sellButton = new ItemStack(Material.EMERALD);
        ItemMeta sellMeta = sellButton.getItemMeta();
        sellMeta.setDisplayName(messageManager.getSellItemText());
        sellMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        
        // 添加上架指南信息
        List<String> sellLore = new ArrayList<>();
        sellLore.add("§7手持要上架的物品输入:");
        sellLore.add("§7/auction sell <起拍价> [一口价] [货币类型]");
        sellLore.add("§7货币类型: §f1=金币, 2=点券");
        sellMeta.setLore(sellLore);
        
        sellButton.setItemMeta(sellMeta);
        inv.setItem(49, sellButton);

        // 设置我的拍卖按钮
        ItemStack myAuctionsButton = new ItemStack(Material.CHEST);
        ItemMeta myAuctionsMeta = myAuctionsButton.getItemMeta();
        myAuctionsMeta.setDisplayName(messageManager.getMyAuctionsText());
        myAuctionsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        myAuctionsButton.setItemMeta(myAuctionsMeta);
        inv.setItem(51, myAuctionsButton);
    }

    /**
     * 设置翻页按钮
     * @param inv 物品栏
     * @param page 当前页码
     * @param totalPages 总页数
     */
    private void setPaginationButtons(Inventory inv, int page, int totalPages) {
        if (page > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            prevButton.setItemMeta(prevMeta);
            inv.setItem(45, prevButton);
        }

        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            nextButton.setItemMeta(nextMeta);
            inv.setItem(53, nextButton);
        }
    }

    // 设置功能按钮
    private void setFunctionButtons(Inventory inventory) {
        // 搜索按钮
        ItemStack search = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = search.getItemMeta();
        searchMeta.setDisplayName(messageManager.getSearchItemsText());
        searchMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        search.setItemMeta(searchMeta);
        inventory.setItem(47, search);

        // 上架按钮
        ItemStack sell = new ItemStack(Material.EMERALD);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName(messageManager.getSellItemText());
        sellMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sell.setItemMeta(sellMeta);
        inventory.setItem(49, sell);

        // 我的拍卖按钮
        ItemStack myAuctions = new ItemStack(Material.BOOK);
        ItemMeta myAuctionsMeta = myAuctions.getItemMeta();
        myAuctionsMeta.setDisplayName(messageManager.getMyAuctionsText());
        myAuctionsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        myAuctions.setItemMeta(myAuctionsMeta);
        inventory.setItem(51, myAuctions);
    }

    // 打开搜索界面
    public void openSearchMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchMenuTitle());

        // 设置搜索框
        ItemStack searchBox = new ItemStack(Material.PAPER);
        ItemMeta searchMeta = searchBox.getItemMeta();
        searchMeta.setDisplayName(messageManager.getEnterItemNameText());
        List<String> searchLore = new ArrayList<>();
        searchLore.add(ChatColor.YELLOW + messageManager.getEnterItemNameDescription());
        searchMeta.setLore(searchLore);
        searchBox.setItemMeta(searchMeta);
        inventory.setItem(22, searchBox);

        // 设置搜索历史
        List<String> history = plugin.getSearchHistoryManager().getSearchHistory(player);
        if (history != null && !history.isEmpty()) {
            // 在26号槽位放置历史记录按钮
            ItemStack historyButton = new ItemStack(Material.BOOK);
            ItemMeta historyMeta = historyButton.getItemMeta();
            historyMeta.setDisplayName(messageManager.getSearchHistoryText());
            historyButton.setItemMeta(historyMeta);
            inventory.setItem(26, historyButton);
            
            // 放置最近5条搜索历史（只保留5条）
            int count = Math.min(history.size(), 5);
            for (int i = 0; i < count; i++) {
                ItemStack button = new ItemStack(Material.PAPER);
                ItemMeta buttonMeta = button.getItemMeta();
                buttonMeta.setDisplayName(ChatColor.GREEN + messageManager.getSearchPrefixText() + history.get(i));
                List<String> buttonLore = new ArrayList<>();
                buttonLore.add(ChatColor.YELLOW + messageManager.getSearchHistoryItemDescription());
                buttonMeta.setLore(buttonLore);
                button.setItemMeta(buttonMeta);
                
                // 放置在第四行的位置
                inventory.setItem(29 + i, button);
            }
            
            // 清除历史按钮
            ItemStack clearButton = new ItemStack(Material.BARRIER);
            ItemMeta clearMeta = clearButton.getItemMeta();
            clearMeta.setDisplayName(messageManager.getClearSearchHistoryText());
            List<String> clearLore = new ArrayList<>();
            clearLore.add(ChatColor.YELLOW + messageManager.getClearSearchHistoryDescription());
            clearMeta.setLore(clearLore);
            clearButton.setItemMeta(clearMeta);
            inventory.setItem(35, clearButton);
        }
        
        // 返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnMainMenuText());
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
        
        player.openInventory(inventory);
    }
    
    // 打开搜索结果界面
    public void openSearchResultMenu(Player player, String keyword, int page) {
        // 添加到搜索历史
        plugin.getSearchHistoryManager().addSearchHistory(player, keyword);
        
        // 计算每页显示物品数量（9-44槽位，共36个物品）
        int itemsPerPage = 36;
        
        // 获取搜索结果
        List<AuctionItem> items = plugin.getDatabaseManager().searchAuctionItems(keyword, page, itemsPerPage);
        
        // 获取搜索结果总数
        int totalItems = plugin.getDatabaseManager().getSearchResultCount(keyword);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchResultTitlePrefix() + " " + keyword);
        
        // 设置标题栏（第一行）
        for (int i = 0; i < 9; i++) {
            ItemStack titleItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = titleItem.getItemMeta();
            meta.setDisplayName(" ");
            titleItem.setItemMeta(meta);
            inventory.setItem(i, titleItem);
        }
        
        // 设置页码信息在标题栏中间
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + messageManager.getSearchResultTitlePrefix() + " " + ChatColor.WHITE + keyword);
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.YELLOW + "当前页: " + ChatColor.WHITE + page + "/" + (totalPages > 0 ? totalPages : 1));
        infoLore.add(ChatColor.YELLOW + "总计: " + ChatColor.WHITE + totalItems + " 个物品");
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
        // 设置搜索结果物品（从第二行开始，9-44槽位）
        for (int i = 0; i < items.size(); i++) {
            AuctionItem item = items.get(i);
            inventory.setItem(i + 9, createAuctionItemDisplay(item, player));
        }
        
        // 设置导航按钮
        setSearchResultNavigationButtons(inventory, page, totalPages, keyword);
        
        // 保存当前搜索关键词和页码
        playerSearchQueries.put(player, keyword);
        playerPages.put(player, page);
        
        // 设置元数据，供监听器使用
        player.setMetadata("auction_search_keyword", new org.bukkit.metadata.FixedMetadataValue(plugin, keyword));
        player.setMetadata("auction_search_page", new org.bukkit.metadata.FixedMetadataValue(plugin, page));
        
        player.openInventory(inventory);
    }

    // 设置导航按钮
    private void setSearchResultNavigationButtons(Inventory inventory, int page, int totalPages, String keyword) {
        // 设置上一页按钮
        if (page > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            List<String> prevLore = new ArrayList<>();
            prevLore.add(ChatColor.GRAY + "点击查看上一页");
            prevMeta.setLore(prevLore);
            prevButton.setItemMeta(prevMeta);
            inventory.setItem(45, prevButton);
        }
        
        // 设置下一页按钮
        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            List<String> nextLore = new ArrayList<>();
            nextLore.add(ChatColor.GRAY + "点击查看下一页");
            nextMeta.setLore(nextLore);
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(53, nextButton);
        }
        
        // 设置新搜索按钮
        ItemStack searchButton = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = searchButton.getItemMeta();
        searchMeta.setDisplayName(messageManager.getNewSearchText());
        List<String> searchLore = new ArrayList<>();
        searchLore.add(ChatColor.GRAY + "点击执行新搜索");
        searchMeta.setLore(searchLore);
        searchMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        searchButton.setItemMeta(searchMeta);
        inventory.setItem(48, searchButton);
        
        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnMainMenuText());
        List<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "点击返回主菜单");
        backMeta.setLore(backLore);
        backMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    // 打开上架界面
    public void openSellMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSellMenuTitle());

        // 设置上架区 - 只在22号槽位放置提示玻璃板
        inventory.setItem(22, createPlaceholder());

        // 设置确认上架按钮（将原来的返回按钮改为确认上架按钮）
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmSellText());
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§7" + messageManager.getConfirmSellButtonDescription());
        confirmMeta.setLore(confirmLore);
        confirmMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(49, confirmButton);

        player.openInventory(inventory);
    }

    // 创建占位符
    public ItemStack createPlaceholder() {
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        meta.setDisplayName(messageManager.getPlaceItemHereText());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        placeholder.setItemMeta(meta);
        return placeholder;
    }

    // 获取玩家当前页码
    public int getPlayerPage(Player player) {
        return playerPages.getOrDefault(player, 1);
    }

    // 设置玩家页码
    public void setPlayerPage(Player player, int page) {
        playerPages.put(player, page);
    }

    // 获取玩家搜索关键词
    public String getPlayerSearchQuery(Player player) {
        return playerSearchQueries.getOrDefault(player, "");
    }

    // 设置玩家搜索关键词
    public void setPlayerSearchQuery(Player player, String query) {
        playerSearchQueries.put(player, query);
    }

    // 清除玩家数据
    public void clearPlayerData(Player player) {
        playerPages.remove(player);
        playerSearchQueries.remove(player);
    }

    /**
     * 打开确认购买界面
     *
     * @param player 玩家
     * @param item 拍卖物品
     */
    public void openConfirmBuyMenu(Player player, AuctionItem item) {
        // 创建一个确认购买的界面
        Inventory inv = Bukkit.createInventory(player, 27, messageManager.getConfirmBuyMenuTitle());
        
        // 在中央展示物品
        inv.setItem(13, item.getItem());
        
        // 确认按钮（绿色玻璃板）
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmBuyText());
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(ChatColor.YELLOW + "点击确认购买此物品");
        confirmLore.add(ChatColor.YELLOW + "物品名称: " + ChatColor.WHITE + ChatUtils.getItemName(item.getItem()));
        confirmLore.add(ChatColor.YELLOW + "购买价格: " + ChatColor.WHITE + item.getBuyNowPrice() + " (一口价)");
        
        // 不再添加税费信息，因为买家不需要支付税费
        
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        
        // 取消按钮（红色玻璃板）
        ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(messageManager.getCancelBuyText());
        cancelMeta.setLore(Collections.singletonList(ChatColor.YELLOW + "点击取消购买"));
        cancelButton.setItemMeta(cancelMeta);
        
        // 设置确认和取消按钮
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                if (i < 13) {
                    inv.setItem(i, confirmButton);
                } else {
                    inv.setItem(i, cancelButton);
                }
            }
        }
        
        // 保存物品ID到玩家元数据，以便在处理点击事件时使用
        player.setMetadata("confirm_buy_id", new FixedMetadataValue(plugin, item.getId()));
        
        // 打开界面
        player.openInventory(inv);
    }

    /**
     * 打开竞价界面
     * @param player 玩家
     * @param auctionId 拍卖物品ID
     * @param preBidAmount 预先出价金额
     */
    public void openBidMenu(Player player, int auctionId, double preBidAmount) {
        // 获取拍卖物品信息
        AuctionItem auctionItem = plugin.getDatabaseManager().getAuctionItem(auctionId);
        if (auctionItem == null) {
            player.sendMessage(ChatColor.RED + "该物品不存在或已售出!");
            return;
        }
        
        // 如果物品不是活跃状态或已过期
        if (!auctionItem.isActive() || auctionItem.isExpired()) {
            player.sendMessage(ChatColor.RED + "该物品已过期或已售出!");
            return;
        }
        
        // 如果是卖家自己
        if (auctionItem.getSellerUuid().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "你不能对自己的物品出价!");
            return;
        }
        
        // 创建竞价界面
        Inventory inventory = Bukkit.createInventory(null, 36, messageManager.getBidMenuTitle());
        
        // 获取货币类型
        String currencyType = auctionItem.getCurrencyType();
        
        // 获取当前货币类型的最低加价比例
        double bidRatePercent = currencyType.equalsIgnoreCase("VAULT") ? 
                configManager.getVaultMinBidRate() * 100 : 
                configManager.getPointsMinBidRate() * 100;
        
        // 物品当前价格
        double currentPrice = auctionItem.getCurrentPrice();
        
        // 当前物品的副本，作为展示
        ItemStack displayItem = auctionItem.getItem().clone();
        
        // 计算最小加价金额
        double minBidIncrease = plugin.getEconomyManager().calculateMinBid(currentPrice, currencyType);
        
        // 设置物品显示
        inventory.setItem(13, displayItem);
        
        // 设置当前竞价金额显示
        ItemStack currentBidInfo = new ItemStack(Material.PAPER);
        ItemMeta currentBidMeta = currentBidInfo.getItemMeta();
        currentBidMeta.setDisplayName(messageManager.getCurrentBidAmountText());
        
        List<String> currentBidLore = new ArrayList<>();
        currentBidLore.add(ChatColor.GRAY + "物品: " + ChatColor.WHITE + ChatUtils.getItemName(auctionItem.getItem()));
        currentBidLore.add(ChatColor.GRAY + "原始价格: " + ChatColor.WHITE + plugin.getEconomyManager().formatAmount(auctionItem.getStartPrice(), currencyType));
        currentBidLore.add(ChatColor.GRAY + "当前竞价: " + ChatColor.WHITE + plugin.getEconomyManager().formatAmount(currentPrice, currencyType));
        
        // 添加预先出价信息（如果提供）
        if (preBidAmount > 0) {
            currentBidLore.add(ChatColor.GRAY + "预加价金额: " + ChatColor.WHITE + plugin.getEconomyManager().formatAmount(preBidAmount, currencyType));
        }
        
        currentBidLore.add(ChatColor.GRAY + "加价幅度: " + ChatColor.WHITE + "+" + bidRatePercent + "%");
        currentBidMeta.setLore(currentBidLore);
        currentBidInfo.setItemMeta(currentBidMeta);
        inventory.setItem(4, currentBidInfo);
        
        // 抬价按钮 - 放在左边11位置
        ItemStack increaseBid = new ItemStack(Material.LAPIS_BLOCK);
        ItemMeta increaseMeta = increaseBid.getItemMeta();
        increaseMeta.setDisplayName(messageManager.getBidIncreaseText());
        
        List<String> increaseLore = new ArrayList<>();
        increaseLore.add(ChatColor.GRAY + "点击增加你的竞价金额");
        increaseLore.add(ChatColor.GRAY + "最小加价: " + ChatColor.WHITE + plugin.getEconomyManager().formatAmount(minBidIncrease, currencyType));
        increaseMeta.setLore(increaseLore);
        increaseBid.setItemMeta(increaseMeta);
        inventory.setItem(11, increaseBid);
        
        // 确认按钮 - 放在右边15位置
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmBidText());
        
        List<String> confirmLore = new ArrayList<>();
        // 如果已有预出价，显示确认信息；否则提示需要先预出价
        if (preBidAmount > 0) {
            confirmLore.add(ChatColor.GRAY + "点击确认当前竞价金额");
            confirmLore.add(ChatColor.GRAY + "预加价金额: " + ChatColor.WHITE + plugin.getEconomyManager().formatAmount(preBidAmount, currencyType));
        } else {
            confirmLore.add(ChatColor.GRAY + "请先点击预先抬价按钮");
            confirmLore.add(ChatColor.GRAY + "然后再确认竞价");
        }
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(15, confirmButton);
        
        // 取消按钮 - 放在底部中间22位置
        ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(messageManager.getCancelBidText());
        
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(ChatColor.GRAY + "点击取消竞价");
        cancelLore.add(ChatColor.GRAY + "并返回主菜单");
        cancelMeta.setLore(cancelLore);
        cancelButton.setItemMeta(cancelMeta);
        inventory.setItem(22, cancelButton);
        
        // 添加玩家元数据，用于后续操作
        player.setMetadata("auction_bid_id", new FixedMetadataValue(plugin, auctionId));
        player.setMetadata("auction_item_price", new FixedMetadataValue(plugin, currentPrice));
        
        // 如果提供了预先出价，设置预出价元数据
        if (preBidAmount > 0) {
            player.setMetadata("auction_bid_amount", new FixedMetadataValue(plugin, preBidAmount));
            player.setMetadata("auction_bid_in_progress", new FixedMetadataValue(plugin, true));
        }
        
        // 打开界面
        player.openInventory(inventory);
    }

    /**
     * 打开竞价界面（通过物品ID，不带预先出价）
     * @param player 玩家
     * @param auctionId 拍卖物品ID
     */
    public void openBidMenu(Player player, int auctionId) {
        // 调用带默认预先出价为0的重载方法
        openBidMenu(player, auctionId, 0);
    }

    /**
     * 打开玩家的"我的拍卖"界面
     * @param player 玩家
     * @param page 页码
     */
    public void openMyAuctionsMenu(Player player, int page) {
        // 获取玩家的拍卖物品
        List<AuctionItem> items = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
        
        // 筛选活跃拍卖
        List<AuctionItem> activeItems = items.stream()
                .filter(item -> item.isActive() && !item.isExpired())
                .toList();
        
        // 获取已售出和已过期的数量，用于显示统计信息
        long completedItems = items.stream()
                .filter(item -> "SOLD".equals(item.getStatus()))
                .count();
        
        long expiredItems = items.stream()
                .filter(item -> !item.isActive() && !"SOLD".equals(item.getStatus()))
                .count();
        
        // 获取玩家当前上架数量和最大上架限制
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        int maxListings = configManager.getMaxListingsPerPlayer();
        
        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = activeItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyAuctionsMenuTitle());
        
        // 添加物品 - 从槽位9开始（第二行第一列）
        if (!activeItems.isEmpty()) {
            List<AuctionItem> pageItems = activeItems.subList(startIndex, endIndex);
            for (int i = 0; i < pageItems.size(); i++) {
                AuctionItem item = pageItems.get(i);
                ItemStack displayItem = item.getItem().clone();
                ItemMeta meta = displayItem.getItemMeta();
                
                // 添加拍卖信息
                List<String> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore.addAll(meta.getLore());
                    lore.add("");
                }
                
                lore.add(ChatColor.YELLOW + "起拍价: " + ChatColor.WHITE + 
                        plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
                lore.add(ChatColor.YELLOW + "当前价: " + ChatColor.WHITE + 
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                
                if (item.hasBuyNowPrice()) {
                    lore.add(ChatColor.YELLOW + "一口价: " + ChatColor.WHITE + 
                            plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                }
                
                lore.add(ChatColor.YELLOW + "剩余时间: " + ChatColor.WHITE + item.getFormattedRemainingTime());
                
                if (item.getCurrentBidder() != null) {
                    lore.add(ChatColor.YELLOW + "当前出价者: " + ChatColor.WHITE + 
                            Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName());
                    lore.add("");
                    lore.add(ChatColor.RED + "⚠ 已有人出价，无法取消拍卖 ⚠");
                    lore.add(ChatColor.RED + "请等待拍卖结束");
                } else {
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "右键点击取消拍卖");
                }
                
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
                
                // 使用槽位9-44而不是0-35
                inventory.setItem(i + 9, displayItem);
            }
        }
        
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "我的拍卖");
        
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.YELLOW + "当前页: " + ChatColor.WHITE + page + "/" + totalPages);
        infoLore.add(ChatColor.YELLOW + "已售出: " + ChatColor.WHITE + completedItems);
        infoLore.add(ChatColor.YELLOW + "已过期: " + ChatColor.WHITE + expiredItems);
        infoLore.add(ChatColor.YELLOW + "上架数量: " + ChatColor.WHITE + currentListings + "/" + maxListings);
        
        // 如果接近上限，添加警告信息
        if (currentListings >= maxListings * 0.8) {
            infoLore.add(ChatColor.RED + "⚠ 你的上架数量即将达到上限!");
        }
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
        // 设置导航按钮
        setMyAuctionsNavigationButtons(inventory, page, totalPages);
        
        // 移除活跃拍卖按钮，保留已售出和已过期按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton); // 调整位置到原活跃拍卖按钮的位置
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton); // 调整位置到51
        
        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);
        
        // 保存当前页码和筛选条件到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "active"));
        
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_auctions"));
    }

    // 设置我的拍卖导航按钮
    private void setMyAuctionsNavigationButtons(Inventory inventory, int page, int totalPages) {
        // 设置上一页按钮
        if (page > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            List<String> prevLore = new ArrayList<>();
            prevLore.add(ChatColor.GRAY + "点击查看上一页");
            prevMeta.setLore(prevLore);
            prevButton.setItemMeta(prevMeta);
            inventory.setItem(48, prevButton);
        }
        
        // 设置下一页按钮
        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            List<String> nextLore = new ArrayList<>();
            nextLore.add(ChatColor.GRAY + "点击查看下一页");
            nextMeta.setLore(nextLore);
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(50, nextButton);
        }
        
        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnMainMenuText());
        List<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "点击返回主菜单");
        backMeta.setLore(backLore);
        backMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    /**
     * 打开玩家的"已售出拍卖"界面
     * @param player 玩家
     * @param page 页码
     */
    public void openMySoldAuctionsMenu(Player player, int page) {
        // 获取玩家的拍卖物品
        List<AuctionItem> items = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
        
        // 筛选已售出的物品
        List<AuctionItem> soldItems = items.stream()
                .filter(item -> "SOLD".equals(item.getStatus()))
                .toList();
        
        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = soldItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMySoldAuctionsMenuTitle());
        
        // 添加物品 - 从槽位9开始（第二行第一列）
        if (!soldItems.isEmpty() && startIndex < soldItems.size()) {
            List<AuctionItem> pageItems = soldItems.subList(startIndex, endIndex);
            for (int i = 0; i < pageItems.size(); i++) {
                AuctionItem item = pageItems.get(i);
                ItemStack displayItem = item.getItem().clone();
                ItemMeta meta = displayItem.getItemMeta();
                
                // 添加拍卖信息
                List<String> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore.addAll(meta.getLore());
                    lore.add("");
                }
                
                lore.add(ChatColor.YELLOW + "售出价格: " + ChatColor.WHITE + 
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                
                // 显示买家信息，优先使用存储的买家名称
                String buyerName = "未知";
                if (item.getCurrentBidderName() != null && !item.getCurrentBidderName().isEmpty()) {
                    buyerName = item.getCurrentBidderName();
                } else if (item.getCurrentBidder() != null) {
                    // 如果没有存储买家名称，则尝试通过UUID获取
                    buyerName = Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName();
                    if (buyerName == null || buyerName.isEmpty()) {
                        // 如果名称仍然为空，使用UUID前8位
                        buyerName = item.getCurrentBidder().toString().substring(0, 8) + "...";
                    }
                } else {
                }
                lore.add(ChatColor.YELLOW + "购买者: " + ChatColor.WHITE + buyerName);
                
                // 显示售出时间，优先使用专门的售出时间字段
                long displayTime = item.getSoldTime();
                if (displayTime <= 0) {
                    // 如果售出时间未设置，回退到使用结束时间
                    displayTime = item.getEndTime();
                }
                
                lore.add(ChatColor.YELLOW + "售出时间: " + ChatColor.WHITE + 
                        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(displayTime)));
                
                lore.add("");
                lore.add(ChatColor.RED + "此物品已售出，仅供查看");
                lore.add(ChatColor.RED + "无法取回或再次出售");
                
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
                
                // 使用槽位9-44而不是0-35
                inventory.setItem(i + 9, displayItem);
            }
        }
        
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "我的已售出拍卖");
        
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.YELLOW + "当前页: " + ChatColor.WHITE + page + "/" + totalPages);
        infoLore.add(ChatColor.YELLOW + "已售出: " + ChatColor.WHITE + soldItems.size());
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
        // 设置导航和筛选按钮
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 设置筛选按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
        
        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);
        
        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "sold"));
        
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_sold_auctions"));
    }

    /**
     * 打开我的过期拍卖界面
     * @param player 玩家
     * @param page 页码
     */
    public void openMyMailboxMenu(Player player, int page) {
        List<AuctionItem> combinedMailboxItems = new ArrayList<>();
        
        // 1. 获取玩家的拍卖物品 (auction_items表)
        List<AuctionItem> auctionItems = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
        
        // 筛选状态为EXPIRED且无人出价的物品
        List<AuctionItem> expiredItems = auctionItems.stream()
                .filter(item -> "EXPIRED".equals(item.getStatus()) && item.getCurrentBidder() == null)
                .toList();
        
        // 2. 获取玩家的待领取物品 (pending_items表)，只获取不是从过期物品转移来的物品
        // 这里不再获取所有待领取物品，而是筛选出没有对应过期物品的物品
        List<AuctionItem> pendingItems = plugin.getDatabaseManager().getPendingItemsAsAuctionItems(player.getUniqueId());
        
        // 3. 合并两个来源的物品，确保不出现重复
        combinedMailboxItems.addAll(expiredItems);
        
        // 只添加那些不是由过期物品自动添加的待领取物品
        // 这样可以避免既从auction_items又从pending_items加载同一个物品
        List<AuctionItem> filteredPendingItems = pendingItems.stream()
                .filter(pendingItem -> !isFromExpiredAuction(pendingItem))
                .toList();
        
        combinedMailboxItems.addAll(filteredPendingItems);
        
        // 增强日志记录，记录更详细的邮箱物品信息
        if (!combinedMailboxItems.isEmpty()) {
            // 邮箱中有物品
        }
        
        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = combinedMailboxItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // 确保页码有效
        if (page < 1) {
            page = 1;
        } else if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }
        
        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyMailboxMenuTitlePrefix() + " §7- 第 " + page + " 页");
        
        // 计算当前页显示的物品范围
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // 添加物品到界面
        for (int i = startIndex; i < endIndex; i++) {
            AuctionItem item = combinedMailboxItems.get(i);
            ItemStack displayItem = createMailboxItemDisplay(item);
            inventory.setItem(9 + (i - startIndex), displayItem);
        }
        
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "物品邮箱");
        
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.YELLOW + "当前页: " + ChatColor.WHITE + page + "/" + totalPages);
        infoLore.add(ChatColor.YELLOW + "物品数量: " + ChatColor.WHITE + combinedMailboxItems.size());
        infoLore.add("");
        infoLore.add(ChatColor.GRAY + "这里存储:");
        infoLore.add(ChatColor.GRAY + "- 过期未售出的物品");
        infoLore.add(ChatColor.GRAY + "- 竞拍成功的物品");
        infoLore.add(ChatColor.GRAY + "- 背包已满时购买的物品");
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
        // 设置导航和筛选按钮
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 设置筛选按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
        
        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);
        
        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "mailbox"));
        
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_mailbox"));
    }
    
    /**
     * 为邮箱中的物品创建显示ItemStack
     * @param item 拍卖物品
     * @return 用于显示的ItemStack
     */
    private ItemStack createMailboxItemDisplay(AuctionItem item) {
        ItemStack displayItem = item.getItem().clone();
        ItemMeta meta = displayItem.getItemMeta();
        
        // 添加物品信息
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) {
            lore.addAll(meta.getLore());
            
            // 如果已有Lore，检查是否有我们添加的邮箱标记，如果有则去除游戏内已有的邮箱标记
            // 以避免重复显示邮箱相关信息
            lore.removeIf(line -> 
                line.contains("✉") || 
                line.contains("§8-----------------") ||
                line.contains("成交价") ||
                line.contains("获得时间") ||
                line.contains("下架时间") ||
                line.contains("购买时间"));
            
            if (!lore.isEmpty()) {
                lore.add("");
            }
        }
        
        // 根据物品的状态添加不同的描述
        if ("MAILBOX_PENDING".equals(item.getStatus())) {
            // 来自pending_items表的物品
            lore.add(ChatColor.YELLOW + "状态: " + ChatColor.WHITE + "待领取物品");
            // 使用创建时间而不是结束时间
            lore.add(ChatColor.YELLOW + "添加时间: " + ChatColor.WHITE + 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(item.getStartTime())));
        } else if ("EXPIRED".equals(item.getStatus())) {
            // 来自auction_items表的过期物品
            lore.add(ChatColor.YELLOW + "状态: " + ChatColor.WHITE + "过期未售出");
            // 使用结束时间，因为这表示物品过期的实际时间
            lore.add(ChatColor.YELLOW + "过期时间: " + ChatColor.WHITE + 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(item.getEndTime())));
        } else {
            // 其他状态（通常不会出现）
            lore.add(ChatColor.YELLOW + "状态: " + ChatColor.WHITE + item.getStatus());
            // 对于其他状态，使用开始时间作为添加时间
            lore.add(ChatColor.YELLOW + "添加时间: " + ChatColor.WHITE + 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(item.getStartTime())));
        }
        
        lore.add("");
        lore.add(ChatColor.GRAY + "右键点击领取物品");
        
        meta.setLore(lore);
        displayItem.setItemMeta(meta);
        
        return displayItem;
    }
    
    // 新增方法：判断物品是否来自过期的拍卖物品
    private boolean isFromExpiredAuction(AuctionItem item) {
        // 检查物品是否有"过期未售出的物品"或"AUCTION_EXPIRED"的标记
        if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasLore()) {
            List<String> lore = item.getItem().getItemMeta().getLore();
            return lore.stream().anyMatch(line -> 
                line.contains("过期未售出") || 
                line.contains("AUCTION_EXPIRED"));
        }
        return false;
    }

    /**
     * 打开竞价界面（通过AuctionItem对象）
     * @param player 玩家
     * @param item 拍卖物品
     */
    public void openBidMenu(Player player, AuctionItem item) {
        if (item == null) {
            player.sendMessage(ChatColor.RED + "物品不存在或已被购买!");
            return;
        }
        
        // 调用基础方法
        openBidMenu(player, item.getId(), 0);
    }
} 