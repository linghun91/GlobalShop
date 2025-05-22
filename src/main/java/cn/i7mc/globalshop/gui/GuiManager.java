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
<<<<<<< HEAD
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
import java.util.stream.Stream;
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba

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

    /**
     * 重新加载界面配置
     */
    public void reloadConfig() {
        // 这里无需执行特殊操作，因为GuiManager通过plugin.getConfigManager()
        // 来获取配置信息，只要ConfigManager已经重新加载，这里就能获取到最新配置
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 获取总物品数量用于分页
        int totalItems = plugin.getDatabaseManager().getTotalActiveItems();
        int itemsPerPage = 45; // 每页45个物品（5行9列）
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 打开界面
        player.openInventory(inventory);
        playerPages.put(player, page);
    }

    /**
     * 创建拍卖物品显示
     * @param item 拍卖物品
     * @param viewer 查看者（可能为null）
     * @return 展示用的ItemStack
     */
    public ItemStack createAuctionItemDisplay(AuctionItem item, Player viewer) {
        ItemStack original = item.getItem();
        ItemStack displayItem = original.clone();
        ItemMeta meta = displayItem.getItemMeta();
        if (meta == null) return displayItem;
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 只有在中文语言环境时才进行物品中文名称转译
        if (plugin.getConfigManager().isChineseLanguage()) {
            // 尝试获取中文名称
            String itemId = original.getType().name().toLowerCase();
            String chineseName = plugin.getLanguageManager().getChineseName(itemId);
<<<<<<< HEAD

            List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            List<String> auctionLore = new ArrayList<>();
            String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());

=======
            
            List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            List<String> auctionLore = new ArrayList<>();
            String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 记录原版物品的展示名称(如果没有自定义名称，使用中文名称)
            if (!meta.hasDisplayName() && chineseName != null && !chineseName.isEmpty()) {
                meta.setDisplayName(messageManager.getItemNameColor() + chineseName);
            }
        } else {
            // 非中文环境下，如果没有自定义名称，使用原版格式化名称
            List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            List<String> auctionLore = new ArrayList<>();
            String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            if (!meta.hasDisplayName()) {
                // 将物品类型名称转换为更易读的格式
                String typeName = original.getType().toString();
                typeName = typeName.toLowerCase().replace('_', ' ');
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 首字母大写处理
                StringBuilder result = new StringBuilder();
                boolean capitalizeNext = true;
                for (char c : typeName.toCharArray()) {
                    if (c == ' ') {
                        result.append(c);
                        capitalizeNext = true;
                    } else if (capitalizeNext) {
                        result.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else {
                        result.append(c);
                    }
                }
<<<<<<< HEAD

                meta.setDisplayName(messageManager.getItemNameColor() + result.toString());
            }
        }

=======
                
                meta.setDisplayName(messageManager.getItemNameColor() + result.toString());
            }
        }
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 共用部分：获取原始lore和添加拍卖信息
        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        List<String> auctionLore = new ArrayList<>();
        String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加原版物品的lore
        if (originalLore != null && !originalLore.isEmpty()) {
            auctionLore.addAll(originalLore);
            auctionLore.add(""); // 空行分隔
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加拍卖信息
        auctionLore.add(messageManager.getAuctionItemDivider());
        auctionLore.add(messageManager.getAuctionInfoHeader());
        auctionLore.add(messageManager.getAuctionItemIdFormat().replace("%id%", String.valueOf(item.getId())));
        auctionLore.add(messageManager.getAuctionCurrencyTypeFormat().replace("%currency%", currencyName));
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 根据物品状态显示不同的价格信息
        if ("SOLD".equals(item.getStatus())) {
            // 已售出物品显示成交价格
            String dealPrice = messageManager.getAuctionDealPriceFormat()
                .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
            auctionLore.add(dealPrice);
        } else {
            // 活跃物品显示当前价格
            String startPrice = messageManager.getAuctionStartPriceFormat()
                .replace("%price%", plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
            auctionLore.add(startPrice);
<<<<<<< HEAD

            String currentPrice = messageManager.getAuctionCurrentPriceFormat()
                .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
            auctionLore.add(currentPrice);

=======
            
            String currentPrice = messageManager.getAuctionCurrentPriceFormat()
                .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
            auctionLore.add(currentPrice);
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 显示一口价（如果有）
            if (item.getBuyNowPrice() > 0) {
                String buyNowPrice = messageManager.getAuctionBuyNowPriceFormat()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                auctionLore.add(buyNowPrice);
            }
        }
<<<<<<< HEAD

        // 显示时间信息
        auctionLore.add(messageManager.getAuctionListTimeFormat().replace("%time%", item.getFormattedListTime()));

=======
        
        // 显示时间信息
        auctionLore.add(messageManager.getAuctionListTimeFormat().replace("%time%", item.getFormattedListTime()));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        if ("ACTIVE".equals(item.getStatus())) {
            // 活跃物品显示剩余时间
            auctionLore.add(messageManager.getAuctionRemainingTimeFormat().replace("%time%", item.getFormattedRemainingTime()));
        }
<<<<<<< HEAD

        // 显示卖家信息
        auctionLore.add(messageManager.getAuctionSellerFormat().replace("%seller%", item.getSellerName()));

=======
        
        // 显示卖家信息
        auctionLore.add(messageManager.getAuctionSellerFormat().replace("%seller%", item.getSellerName()));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 显示当前出价者（如果有）
        if (item.getCurrentBidder() != null && !item.getCurrentBidder().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            String bidderName = Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName();
            if (bidderName != null) {
                auctionLore.add(messageManager.getAuctionCurrentBidderFormat().replace("%bidder%", bidderName));
            }
        }
<<<<<<< HEAD

        auctionLore.add(messageManager.getAuctionItemDivider());

=======
        
        auctionLore.add(messageManager.getAuctionItemDivider());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加操作提示
        if (viewer != null && "ACTIVE".equals(item.getStatus())) {
            if (viewer.getUniqueId().equals(item.getSellerUuid())) {
                // 物品所有者提示
                auctionLore.add(messageManager.getAuctionOwnerTipsHeader());
                auctionLore.add(messageManager.getAuctionOwnerCancelTip());
            } else {
                // 买家提示
                auctionLore.add(messageManager.getAuctionBuyerBidTip());
                auctionLore.add(messageManager.getAuctionBuyerBuyTip());
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 管理员提示
            if (viewer.hasPermission("globalshop.admin")) {
                auctionLore.add(messageManager.getAuctionItemDivider());
                auctionLore.add(messageManager.getAuctionAdminTipsHeader());
                auctionLore.add(messageManager.getAuctionAdminForceCancelTip());
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        meta.setLore(auctionLore);
        displayItem.setItemMeta(meta);
        return displayItem;
    }

    /**
     * 创建拍卖物品显示（不带玩家判断）
     * @param item 拍卖物品
     * @return 展示用的ItemStack
     */
    public ItemStack createAuctionItemDisplay(AuctionItem item) {
        return createAuctionItemDisplay(item, null);
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置上架按钮
        ItemStack sellButton = new ItemStack(Material.EMERALD);
        ItemMeta sellMeta = sellButton.getItemMeta();
        sellMeta.setDisplayName(messageManager.getSellItemText());
        sellMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加上架指南信息
        List<String> sellLore = new ArrayList<>();
        sellLore.add(messageManager.getSellMenuCommandTipsHeader());
        sellLore.add(messageManager.getSellMenuCommandUsage());
        sellLore.add(messageManager.getSellMenuCurrencyTypes());
        sellMeta.setLore(sellLore);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
            List<String> nextLore = new ArrayList<>();
            nextLore.add(messageManager.getNextPageDescText());
            nextMeta.setLore(nextLore);
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

<<<<<<< HEAD
        // 输入物品名称提示
        ItemStack searchInput = new ItemStack(Material.PAPER);
        ItemMeta inputMeta = searchInput.getItemMeta();
        inputMeta.setDisplayName(messageManager.getEnterItemNameText());
        List<String> inputLore = new ArrayList<>();
        inputLore.add(messageManager.getEnterItemNameDescription());
        inputMeta.setLore(inputLore);
        searchInput.setItemMeta(inputMeta);
        inventory.setItem(22, searchInput); // 放在中间位置

        // 按卖家名搜索按钮 (Player Head)
        ItemStack sellerSearchButton = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sellerMeta = (SkullMeta) sellerSearchButton.getItemMeta();
        sellerMeta.setDisplayName(ChatColor.GREEN + "按卖家名搜索");
        List<String> sellerLore = new ArrayList<>();
        sellerLore.add(ChatColor.YELLOW + "输入玩家名称搜索该玩家的所有上架物品");
        sellerMeta.setLore(sellerLore);
        sellerSearchButton.setItemMeta(sellerMeta);
        inventory.setItem(31, sellerSearchButton); // 放在物品搜索下方

        // 搜索历史按钮 (Book)
        ItemStack historyButton = new ItemStack(Material.BOOK);
        ItemMeta historyMeta = historyButton.getItemMeta();
        historyMeta.setDisplayName(messageManager.getSearchHistoryText());
        List<String> historyLore = new ArrayList<>();
        historyLore.add(ChatColor.YELLOW + "点击查看你的搜索历史"); // 修改提示
        historyMeta.setLore(historyLore);
        historyButton.setItemMeta(historyMeta);
        inventory.setItem(47, historyButton); // 放在右下角

        // 清除历史按钮 (Barrier)
        ItemStack clearButton = new ItemStack(Material.BARRIER);
        ItemMeta clearMeta = clearButton.getItemMeta();
        clearMeta.setDisplayName(messageManager.getClearSearchHistoryText());
        List<String> clearLore = new ArrayList<>();
        clearLore.add(ChatColor.YELLOW + messageManager.getClearSearchHistoryDescription());
        clearMeta.setLore(clearLore);
        clearButton.setItemMeta(clearMeta);
        inventory.setItem(51, clearButton); // 放在历史按钮旁边

        // 返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnText());
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);

        player.openInventory(inventory);
    }

=======
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
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    // 打开搜索结果界面
    public void openSearchResultMenu(Player player, String keyword, int page) {
        // 添加到搜索历史
        plugin.getSearchHistoryManager().addSearchHistory(player, keyword);
<<<<<<< HEAD

        // 计算每页显示物品数量（9-44槽位，共36个物品）
        int itemsPerPage = 36;

        // 获取搜索结果
        List<AuctionItem> items = plugin.getDatabaseManager().searchAuctionItems(keyword, page, itemsPerPage);

        // 获取搜索结果总数
        int totalItems = plugin.getDatabaseManager().getSearchResultCount(keyword);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchResultTitlePrefix() + " " + keyword);

=======
        
        // 计算每页显示物品数量（9-44槽位，共36个物品）
        int itemsPerPage = 36;
        
        // 获取搜索结果
        List<AuctionItem> items = plugin.getDatabaseManager().searchAuctionItems(keyword, page, itemsPerPage);
        
        // 获取搜索结果总数
        int totalItems = plugin.getDatabaseManager().getSearchResultCount(keyword);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchResultTitlePrefix() + " " + keyword);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置标题栏（第一行）
        for (int i = 0; i < 9; i++) {
            ItemStack titleItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = titleItem.getItemMeta();
            meta.setDisplayName(" ");
            titleItem.setItemMeta(meta);
            inventory.setItem(i, titleItem);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置页码信息在标题栏中间
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + messageManager.getSearchResultTitlePrefix() + " " + keyword);
        List<String> infoLore = new ArrayList<>();
        String pageInfoText = messageManager.getPageInfoPrefix() + page + "/" + (totalPages > 0 ? totalPages : 1);
        infoLore.add(pageInfoText);
<<<<<<< HEAD

        String totalItemsText = messageManager.getSoldItemsCountPrefix() + totalItems;
        infoLore.add(totalItemsText);

        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);

=======
        
        String totalItemsText = messageManager.getSoldItemsCountPrefix() + totalItems;
        infoLore.add(totalItemsText);
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置搜索结果物品（从第二行开始，9-44槽位）
        for (int i = 0; i < items.size(); i++) {
            AuctionItem item = items.get(i);
            inventory.setItem(i + 9, createAuctionItemDisplay(item, player));
        }
<<<<<<< HEAD

        // 设置导航按钮
        setSearchResultNavigationButtons(inventory, page, totalPages, keyword);

        // 保存当前搜索关键词和页码
        playerSearchQueries.put(player, keyword);
        playerPages.put(player, page);

        // 设置元数据，供监听器使用
        player.setMetadata("auction_search_keyword", new org.bukkit.metadata.FixedMetadataValue(plugin, keyword));
        player.setMetadata("auction_search_page", new org.bukkit.metadata.FixedMetadataValue(plugin, page));

=======
        
        // 设置导航按钮
        setSearchResultNavigationButtons(inventory, page, totalPages, keyword);
        
        // 保存当前搜索关键词和页码
        playerSearchQueries.put(player, keyword);
        playerPages.put(player, page);
        
        // 设置元数据，供监听器使用
        player.setMetadata("auction_search_keyword", new org.bukkit.metadata.FixedMetadataValue(plugin, keyword));
        player.setMetadata("auction_search_page", new org.bukkit.metadata.FixedMetadataValue(plugin, page));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
            prevLore.add(messageManager.getPreviousPageDescText());
            prevMeta.setLore(prevLore);
            prevButton.setItemMeta(prevMeta);
            inventory.setItem(45, prevButton);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置下一页按钮
        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            List<String> nextLore = new ArrayList<>();
            nextLore.add(messageManager.getNextPageDescText());
            nextMeta.setLore(nextLore);
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(53, nextButton);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置新搜索按钮
        ItemStack searchButton = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = searchButton.getItemMeta();
        searchMeta.setDisplayName(messageManager.getNewSearchText());
        List<String> searchLore = new ArrayList<>();
        searchLore.add(messageManager.getNewSearchDescText());
        searchMeta.setLore(searchLore);
        searchMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        searchButton.setItemMeta(searchMeta);
        inventory.setItem(48, searchButton);
<<<<<<< HEAD

        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnText());
=======
        
        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnMainMenuText());
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> backLore = new ArrayList<>();
        backLore.add(messageManager.getReturnMainMenuDescText());
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

        // 自定义上架时间按钮（钟表材质）
        ItemStack durationButton = new ItemStack(Material.CLOCK);
        ItemMeta durationMeta = durationButton.getItemMeta();
        durationMeta.setDisplayName(messageManager.getDurationButtonTitle());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 初始时间设为最小时间
        long initialDuration = configManager.getMinDuration();
        // 将秒转换为小时和分钟格式显示
        long hours = initialDuration / 3600;
        long minutes = (initialDuration % 3600) / 60;
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> durationLore = new ArrayList<>();
        // 使用配置文件中的文本
        durationLore.add(messageManager.getDurationCurrentSettingFormat()
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes)));
        durationLore.add(messageManager.getDurationButtonDivider());
        durationLore.add(messageManager.getDurationLeftClickTip());
        durationLore.add(messageManager.getDurationRightClickTip());
        durationLore.add(messageManager.getDurationShiftLeftClickTip());
        durationLore.add(messageManager.getDurationShiftRightClickTip());
        durationLore.add(messageManager.getDurationMiddleClickTip());
        durationLore.add(messageManager.getDurationButtonDivider());
        durationLore.add(messageManager.getDurationMinTimeFormat()
                .replace("%hours%", String.valueOf(configManager.getMinDuration() / 3600)));
        durationLore.add(messageManager.getDurationMaxTimeFormat()
                .replace("%hours%", String.valueOf(configManager.getMaxDuration() / 3600)));
        durationLore.add(messageManager.getDurationButtonDivider());
        durationLore.add(messageManager.getDurationNote1());
        durationLore.add(messageManager.getDurationNote2());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        durationMeta.setLore(durationLore);
        durationMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        durationButton.setItemMeta(durationMeta);
        inventory.setItem(47, durationButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置确认上架按钮（将原来的返回按钮改为确认上架按钮）
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmSellText());
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(messageManager.getConfirmSellButtonDescriptionWithColor());
        confirmMeta.setLore(confirmLore);
        confirmMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(49, confirmButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置初始上架时间元数据
        player.setMetadata("auction_duration", new FixedMetadataValue(plugin, initialDuration));

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
<<<<<<< HEAD

        // 在中央展示物品
        inv.setItem(13, item.getItem());

=======
        
        // 在中央展示物品
        inv.setItem(13, item.getItem());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 确认按钮（绿色玻璃板）
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmBuyText());
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(messageManager.getConfirmBuyTipText());
        String itemNameText = messageManager.getConfirmBuyItemText().replace("%item_name%", ChatUtils.getItemName(item.getItem()));
        confirmLore.add(itemNameText);
        String priceText = messageManager.getConfirmBuyPriceText().replace("%price%", String.valueOf(item.getBuyNowPrice()));
        confirmLore.add(priceText);
<<<<<<< HEAD

        // 不再添加税费信息，因为买家不需要支付税费

        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);

=======
        
        // 不再添加税费信息，因为买家不需要支付税费
        
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 取消按钮（红色玻璃板）
        ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(messageManager.getCancelBuyText());
        cancelMeta.setLore(Collections.singletonList(messageManager.getCancelBuyTipText()));
        cancelButton.setItemMeta(cancelMeta);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

        // 保存物品ID到玩家元数据，以便在处理点击事件时使用
        player.setMetadata("confirm_buy_id", new FixedMetadataValue(plugin, item.getId()));

=======
        
        // 保存物品ID到玩家元数据，以便在处理点击事件时使用
        player.setMetadata("confirm_buy_id", new FixedMetadataValue(plugin, item.getId()));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果物品不是活跃状态或已过期
        if (!auctionItem.isActive() || auctionItem.isExpired()) {
            player.sendMessage(ChatColor.RED + "该物品已过期或已售出!");
            return;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果是卖家自己
        if (auctionItem.getSellerUuid().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "你不能对自己的物品出价!");
            return;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 清除玩家之前可能存在的竞价元数据（特别是预加价值）
        if (player.hasMetadata("auction_bid_id") && player.getMetadata("auction_bid_id").get(0).asInt() != auctionId) {
            // 如果是不同的物品，清除旧的预加价数据
            if (player.hasMetadata("auction_bid_amount")) {
                player.removeMetadata("auction_bid_amount", plugin);
            }
            if (player.hasMetadata("auction_bid_in_progress")) {
                player.removeMetadata("auction_bid_in_progress", plugin);
            }
        }
<<<<<<< HEAD

        // 创建竞价界面
        Inventory inventory = Bukkit.createInventory(null, 27, messageManager.getBidMenuTitle());

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

=======
        
        // 创建竞价界面
        Inventory inventory = Bukkit.createInventory(null, 27, messageManager.getBidMenuTitle());
        
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
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置当前竞价金额显示
        ItemStack currentBidInfo = new ItemStack(Material.PAPER);
        ItemMeta currentBidMeta = currentBidInfo.getItemMeta();
        currentBidMeta.setDisplayName(messageManager.getCurrentBidAmountText());
<<<<<<< HEAD

        List<String> currentBidLore = new ArrayList<>();
        String itemText = messageManager.getCurrentBidItemText().replace("%item_name%", ChatUtils.getItemName(auctionItem.getItem()));
        currentBidLore.add(itemText);

        String originalPriceText = messageManager.getCurrentBidOriginalPriceText().replace("%price%",
                plugin.getEconomyManager().formatAmount(auctionItem.getStartPrice(), currencyType));
        currentBidLore.add(originalPriceText);

        String currentPriceText = messageManager.getCurrentBidCurrentPriceText().replace("%price%",
                plugin.getEconomyManager().formatAmount(currentPrice, currencyType));
        currentBidLore.add(currentPriceText);

        // 添加预先出价信息（如果提供）
        if (preBidAmount > 0) {
            String preAmountText = messageManager.getCurrentBidPreAmountText().replace("%price%",
                    plugin.getEconomyManager().formatAmount(preBidAmount, currencyType));
            currentBidLore.add(preAmountText);
        }

=======
        
        List<String> currentBidLore = new ArrayList<>();
        String itemText = messageManager.getCurrentBidItemText().replace("%item_name%", ChatUtils.getItemName(auctionItem.getItem()));
        currentBidLore.add(itemText);
        
        String originalPriceText = messageManager.getCurrentBidOriginalPriceText().replace("%price%", 
                plugin.getEconomyManager().formatAmount(auctionItem.getStartPrice(), currencyType));
        currentBidLore.add(originalPriceText);
        
        String currentPriceText = messageManager.getCurrentBidCurrentPriceText().replace("%price%", 
                plugin.getEconomyManager().formatAmount(currentPrice, currencyType));
        currentBidLore.add(currentPriceText);
        
        // 添加预先出价信息（如果提供）
        if (preBidAmount > 0) {
            String preAmountText = messageManager.getCurrentBidPreAmountText().replace("%price%", 
                    plugin.getEconomyManager().formatAmount(preBidAmount, currencyType));
            currentBidLore.add(preAmountText);
        }
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        String rateText = messageManager.getCurrentBidRateText().replace("%rate%", String.valueOf(bidRatePercent));
        currentBidLore.add(rateText);
        currentBidMeta.setLore(currentBidLore);
        currentBidInfo.setItemMeta(currentBidMeta);
        inventory.setItem(4, currentBidInfo);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 抬价按钮 - 放在左边11位置
        ItemStack increaseBid = new ItemStack(Material.LAPIS_BLOCK);
        ItemMeta increaseMeta = increaseBid.getItemMeta();
        increaseMeta.setDisplayName(messageManager.getIncreaseBidText(bidRatePercent));
<<<<<<< HEAD

        List<String> increaseLore = new ArrayList<>();
        increaseLore.add(messageManager.getIncreaseBidTipText());
        String minBidText = messageManager.getIncreaseBidMinText().replace("%price%",
=======
        
        List<String> increaseLore = new ArrayList<>();
        increaseLore.add(messageManager.getIncreaseBidTipText());
        String minBidText = messageManager.getIncreaseBidMinText().replace("%price%", 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                plugin.getEconomyManager().formatAmount(minBidIncrease, currencyType));
        increaseLore.add(minBidText);
        increaseMeta.setLore(increaseLore);
        increaseBid.setItemMeta(increaseMeta);
        inventory.setItem(11, increaseBid);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 确认按钮 - 放在右边15位置
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(messageManager.getConfirmBidText());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> confirmLore = new ArrayList<>();
        // 如果已有预出价，显示确认信息；否则提示需要先预出价
        if (preBidAmount > 0) {
            confirmLore.add(messageManager.getConfirmBidTipText());
<<<<<<< HEAD
            String preAmountText = messageManager.getConfirmBidPreAmountText().replace("%price%",
=======
            String preAmountText = messageManager.getConfirmBidPreAmountText().replace("%price%", 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                    plugin.getEconomyManager().formatAmount(preBidAmount, currencyType));
            confirmLore.add(preAmountText);
        } else {
            confirmLore.add(messageManager.getConfirmBidNotice1Text());
            confirmLore.add(messageManager.getConfirmBidNotice2Text());
        }
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(15, confirmButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 取消按钮 - 放在底部中间22位置
        ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(messageManager.getCancelBidText());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(messageManager.getCancelBidTipText());
        cancelLore.add(messageManager.getCancelBidReturnText());
        cancelMeta.setLore(cancelLore);
        cancelButton.setItemMeta(cancelMeta);
        inventory.setItem(22, cancelButton);
<<<<<<< HEAD

        // 添加玩家元数据，用于后续操作
        player.setMetadata("auction_bid_id", new FixedMetadataValue(plugin, auctionId));
        player.setMetadata("auction_item_price", new FixedMetadataValue(plugin, currentPrice));

=======
        
        // 添加玩家元数据，用于后续操作
        player.setMetadata("auction_bid_id", new FixedMetadataValue(plugin, auctionId));
        player.setMetadata("auction_item_price", new FixedMetadataValue(plugin, currentPrice));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果提供了预先出价，设置预出价元数据
        if (preBidAmount > 0) {
            player.setMetadata("auction_bid_amount", new FixedMetadataValue(plugin, preBidAmount));
            player.setMetadata("auction_bid_in_progress", new FixedMetadataValue(plugin, true));
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 筛选活跃拍卖
        List<AuctionItem> activeItems = items.stream()
                .filter(item -> item.isActive() && !item.isExpired())
                .toList();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 获取已售出和已过期的数量，用于显示统计信息
        long completedItems = items.stream()
                .filter(item -> "SOLD".equals(item.getStatus()))
                .count();
<<<<<<< HEAD

        long expiredItems = items.stream()
                .filter(item -> !item.isActive() && !"SOLD".equals(item.getStatus()))
                .count();

        // 获取玩家当前上架数量和最大上架限制
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        int maxListings = configManager.getMaxListingsPerPlayer();

=======
        
        long expiredItems = items.stream()
                .filter(item -> !item.isActive() && !"SOLD".equals(item.getStatus()))
                .count();
        
        // 获取玩家当前上架数量和最大上架限制
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        int maxListings = configManager.getMaxListingsPerPlayer();
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = activeItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
<<<<<<< HEAD

        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyAuctionsMenuTitle());

=======
        
        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyAuctionsMenuTitle());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加物品 - 从槽位9开始（第二行第一列）
        if (!activeItems.isEmpty()) {
            List<AuctionItem> pageItems = activeItems.subList(startIndex, endIndex);
            for (int i = 0; i < pageItems.size(); i++) {
                AuctionItem item = pageItems.get(i);
                ItemStack displayItem = item.getItem().clone();
                ItemMeta meta = displayItem.getItemMeta();
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 添加拍卖信息
                List<String> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore.addAll(meta.getLore());
                    lore.add("");
                }
<<<<<<< HEAD

                String startPriceText = messageManager.getMyAuctionStartPriceText().replace("%price%",
                        plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
                lore.add(startPriceText);

                String currentPriceText = messageManager.getMyAuctionCurrentPriceText().replace("%price%",
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                lore.add(currentPriceText);

                if (item.hasBuyNowPrice()) {
                    String buyNowPriceText = messageManager.getMyAuctionBuyNowPriceText().replace("%price%",
                            plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                    lore.add(buyNowPriceText);
                }

                String remainingTimeText = messageManager.getMyAuctionRemainingTimeText().replace("%time%", item.getFormattedRemainingTime());
                lore.add(remainingTimeText);

                if (item.getCurrentBidder() != null) {
                    String bidderText = messageManager.getMyAuctionCurrentBidderText().replace("%bidder%",
=======
                
                String startPriceText = messageManager.getMyAuctionStartPriceText().replace("%price%", 
                        plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
                lore.add(startPriceText);
                
                String currentPriceText = messageManager.getMyAuctionCurrentPriceText().replace("%price%", 
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                lore.add(currentPriceText);
                
                if (item.hasBuyNowPrice()) {
                    String buyNowPriceText = messageManager.getMyAuctionBuyNowPriceText().replace("%price%", 
                            plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                    lore.add(buyNowPriceText);
                }
                
                String remainingTimeText = messageManager.getMyAuctionRemainingTimeText().replace("%time%", item.getFormattedRemainingTime());
                lore.add(remainingTimeText);
                
                if (item.getCurrentBidder() != null) {
                    String bidderText = messageManager.getMyAuctionCurrentBidderText().replace("%bidder%", 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                            Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName());
                    lore.add(bidderText);
                    lore.add("");
                    lore.add(messageManager.getMyAuctionHasBidderWarning1Text());
                    lore.add(messageManager.getMyAuctionHasBidderWarning2Text());
                } else {
                    lore.add("");
                    lore.add(messageManager.getMyAuctionCancelTipText());
                }
<<<<<<< HEAD

                meta.setLore(lore);
                displayItem.setItemMeta(meta);

=======
                
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 使用槽位9-44而不是0-35
                inventory.setItem(i + 9, displayItem);
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + messageManager.getMyAuctionsText());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> infoLore = new ArrayList<>();
        String pageInfoText = messageManager.getMyAuctionPageInfoText()
                .replace("%page%", String.valueOf(page))
                .replace("%total_pages%", String.valueOf(totalPages));
        infoLore.add(pageInfoText);
<<<<<<< HEAD

        String soldCountText = messageManager.getMyAuctionSoldCountText()
                .replace("%count%", String.valueOf(completedItems));
        infoLore.add(soldCountText);

        String expiredCountText = messageManager.getMyAuctionExpiredCountText()
                .replace("%count%", String.valueOf(expiredItems));
        infoLore.add(expiredCountText);

=======
        
        String soldCountText = messageManager.getMyAuctionSoldCountText()
                .replace("%count%", String.valueOf(completedItems));
        infoLore.add(soldCountText);
        
        String expiredCountText = messageManager.getMyAuctionExpiredCountText()
                .replace("%count%", String.valueOf(expiredItems));
        infoLore.add(expiredCountText);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        String listingsCountText = messageManager.getMyAuctionListingsCountText()
                .replace("%current%", String.valueOf(currentListings))
                .replace("%max%", String.valueOf(maxListings));
        infoLore.add(listingsCountText);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果接近上限，添加警告信息
        if (currentListings >= maxListings * 0.8) {
            infoLore.add(messageManager.getMyAuctionLimitWarningText());
        }
<<<<<<< HEAD

        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);

        // 设置导航按钮
        setMyAuctionsNavigationButtons(inventory, page, totalPages);

=======
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
        // 设置导航按钮
        setMyAuctionsNavigationButtons(inventory, page, totalPages);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 移除活跃拍卖按钮，保留已售出和已过期按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
<<<<<<< HEAD
        inventory.setItem(46, soldButton); // 调整位置到46

        // 添加已购买按钮
        ItemStack purchasedButton = new ItemStack(Material.DIAMOND);
        ItemMeta purchasedMeta = purchasedButton.getItemMeta();
        purchasedMeta.setDisplayName(messageManager.getPurchasedItemsText());
        purchasedButton.setItemMeta(purchasedMeta);
        inventory.setItem(47, purchasedButton); // 位置到47

=======
        inventory.setItem(47, soldButton); // 调整位置到原活跃拍卖按钮的位置
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(messageManager.getMailboxDescriptionLine1());
        mailboxLore.add(messageManager.getMailboxDescriptionLine2());
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton); // 调整位置到51
<<<<<<< HEAD

        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);

        // 保存当前页码和筛选条件到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "active"));

=======
        
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
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
            prevLore.add(messageManager.getPreviousPageDescText());
            prevMeta.setLore(prevLore);
            prevButton.setItemMeta(prevMeta);
            inventory.setItem(48, prevButton);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置下一页按钮
        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            List<String> nextLore = new ArrayList<>();
            nextLore.add(messageManager.getNextPageDescText());
            nextMeta.setLore(nextLore);
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(50, nextButton);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnMainMenuText());
        List<String> backLore = new ArrayList<>();
        backLore.add(messageManager.getReturnMainMenuDescText());
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 筛选已售出的物品
        List<AuctionItem> soldItems = items.stream()
                .filter(item -> "SOLD".equals(item.getStatus()))
                .toList();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = soldItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
<<<<<<< HEAD

        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMySoldAuctionsMenuTitle());

=======
        
        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMySoldAuctionsMenuTitle());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加物品 - 从槽位9开始（第二行第一列）
        if (!soldItems.isEmpty() && startIndex < soldItems.size()) {
            List<AuctionItem> pageItems = soldItems.subList(startIndex, endIndex);
            for (int i = 0; i < pageItems.size(); i++) {
                AuctionItem item = pageItems.get(i);
                ItemStack displayItem = item.getItem().clone();
                ItemMeta meta = displayItem.getItemMeta();
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 添加拍卖信息
                List<String> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore.addAll(meta.getLore());
                    lore.add("");
                }
<<<<<<< HEAD

                String dealPriceText = messageManager.getSoldItemDealPriceText().replace("%price%",
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                lore.add(dealPriceText);

=======
                
                String dealPriceText = messageManager.getSoldItemDealPriceText().replace("%price%", 
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                lore.add(dealPriceText);
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
                }
                String buyerText = messageManager.getSoldItemBuyerText().replace("%buyer%", buyerName);
                lore.add(buyerText);
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 显示售出时间，使用专门的售出时间字段，不再回退到结束时间
                long displayTime = item.getSoldTime();
                String timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(displayTime));
                String soldTimeText = messageManager.getSoldItemSoldTimeText().replace("%time%", timeFormat);
                lore.add(soldTimeText);
<<<<<<< HEAD

                lore.add("");
                lore.add(messageManager.getSoldItemNotice1Text());
                lore.add(messageManager.getSoldItemNotice2Text());

                meta.setLore(lore);
                displayItem.setItemMeta(meta);

=======
                
                lore.add("");
                lore.add(messageManager.getSoldItemNotice1Text());
                lore.add(messageManager.getSoldItemNotice2Text());
                
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 使用槽位9-44而不是0-35
                inventory.setItem(i + 9, displayItem);
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(messageManager.getMySoldAuctionsTitle());
<<<<<<< HEAD

        List<String> infoLore = new ArrayList<>();
        infoLore.add(messageManager.getPageInfoPrefix() + page + "/" + totalPages);
        infoLore.add(messageManager.getSoldItemsCountPrefix() + soldItems.size());

        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);

=======
        
        List<String> infoLore = new ArrayList<>();
        infoLore.add(messageManager.getPageInfoPrefix() + page + "/" + totalPages);
        infoLore.add(messageManager.getSoldItemsCountPrefix() + soldItems.size());
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置导航和筛选按钮
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
<<<<<<< HEAD

        // 计算总收益（金币和点券分开计算）
        double totalCoins = 0;
        double totalPoints = 0;

=======
        
        // 计算总收益（金币和点券分开计算）
        double totalCoins = 0;
        double totalPoints = 0;
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        for (AuctionItem item : soldItems) {
            String currencyType = item.getCurrencyType().toUpperCase();
            if ("MONEY".equals(currencyType) || "VAULT".equals(currencyType)) {
                totalCoins += item.getCurrentPrice();
            } else if ("POINTS".equals(currencyType)) {
                totalPoints += item.getCurrentPrice();
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置筛选按钮，将金块改为金锭，并添加总收益信息
        ItemStack soldButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
<<<<<<< HEAD

        // 添加总收益信息到LORE
        List<String> soldLore = new ArrayList<>();
        soldLore.add(messageManager.getTotalSoldItemsCountPrefix() + soldItems.size());
        soldLore.add(messageManager.getTotalCoinsEarnedPrefix() +
                plugin.getEconomyManager().formatAmount(totalCoins, "VAULT"));
        soldLore.add(messageManager.getTotalPointsEarnedPrefix() +
                plugin.getEconomyManager().formatAmount(totalPoints, "POINTS"));

        soldMeta.setLore(soldLore);
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);

=======
        
        // 添加总收益信息到LORE
        List<String> soldLore = new ArrayList<>();
        soldLore.add(messageManager.getTotalSoldItemsCountPrefix() + soldItems.size());
        soldLore.add(messageManager.getTotalCoinsEarnedPrefix() + 
                plugin.getEconomyManager().formatAmount(totalCoins, "VAULT"));
        soldLore.add(messageManager.getTotalPointsEarnedPrefix() + 
                plugin.getEconomyManager().formatAmount(totalPoints, "POINTS"));
        
        soldMeta.setLore(soldLore);
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(messageManager.getMailboxDescriptionLine1());
        mailboxLore.add(messageManager.getMailboxDescriptionLine2());
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);
<<<<<<< HEAD

        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "sold"));

=======
        
        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "sold"));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_sold_auctions"));
    }

    /**
<<<<<<< HEAD
     * 打开玩家的"已购买拍卖"界面
     * @param player 玩家
     * @param page 页码
     */
    public void openMyPurchasedAuctionsMenu(Player player, int page) {
        // 获取玩家的已购买物品
        List<AuctionItem> purchasedItems = plugin.getDatabaseManager().getPlayerPurchasedItems(player.getUniqueId(), page, 36);

        // 计算分页 - 界面第二行到第五行（槽位9-44）可用于显示物品，共36个槽位
        int itemsPerPage = 36;
        int totalItems = plugin.getDatabaseManager().countPlayerPurchasedItems(player.getUniqueId());
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 如果请求的页码超出范围，调整为有效值
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;

        // 创建界面
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyPurchasedAuctionsMenuTitle());

        // 添加物品 - 从槽位9开始（第二行第一列）
        if (!purchasedItems.isEmpty()) {
            for (int i = 0; i < purchasedItems.size(); i++) {
                AuctionItem item = purchasedItems.get(i);
                ItemStack displayItem = item.getItem().clone();
                ItemMeta meta = displayItem.getItemMeta();

                // 添加拍卖信息
                List<String> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore.addAll(meta.getLore());
                    lore.add("");
                }

                String dealPriceText = messageManager.getSoldItemDealPriceText().replace("%price%",
                        plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                lore.add(dealPriceText);

                // 显示卖家信息
                String sellerName = item.getSellerName();
                if (sellerName == null || sellerName.isEmpty()) {
                    // 如果没有存储卖家名称，则尝试通过UUID获取
                    sellerName = Bukkit.getOfflinePlayer(item.getSellerUuid()).getName();
                    if (sellerName == null || sellerName.isEmpty()) {
                        // 如果名称仍然为空，使用UUID前8位
                        sellerName = item.getSellerUuid().toString().substring(0, 8) + "...";
                    }
                }
                String sellerText = messageManager.getSoldItemSellerText().replace("%seller%", sellerName);
                lore.add(sellerText);

                // 显示售出时间
                long displayTime = item.getSoldTime();
                String timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(displayTime));
                String soldTimeText = messageManager.getSoldItemSoldTimeText().replace("%time%", timeFormat);
                lore.add(soldTimeText);

                lore.add("");
                lore.add(messageManager.getSoldItemNotice1Text());
                lore.add(messageManager.getSoldItemNotice2Text());

                meta.setLore(lore);
                displayItem.setItemMeta(meta);

                // 使用槽位9-44而不是0-35
                inventory.setItem(i + 9, displayItem);
            }
        }

        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(messageManager.getMyPurchasedAuctionsMenuTitle());

        List<String> infoLore = new ArrayList<>();
        infoLore.add(messageManager.getPageInfoPrefix() + page + "/" + totalPages);
        infoLore.add(messageManager.getPurchasedItemsCountPrefix() + totalItems);

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

        // 计算总支出（金币和点券分开计算）
        Map<String, Double> spending = plugin.getDatabaseManager().getPlayerTotalSpending(player.getUniqueId());
        double totalCoins = spending.getOrDefault("VAULT", 0.0);
        double totalPoints = spending.getOrDefault("POINTS", 0.0);

        // 设置已购买按钮，使用钻石块并添加总支出信息
        ItemStack purchasedButton = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta purchasedMeta = purchasedButton.getItemMeta();
        purchasedMeta.setDisplayName(messageManager.getPurchasedItemsText());

        // 添加总支出信息到LORE
        List<String> purchasedLore = new ArrayList<>();
        purchasedLore.add(messageManager.getTotalPurchasedItemsCountPrefix() + totalItems);
        purchasedLore.add(messageManager.getTotalCoinsSpentPrefix() +
                plugin.getEconomyManager().formatAmount(totalCoins, "VAULT"));
        purchasedLore.add(messageManager.getTotalPointsSpentPrefix() +
                plugin.getEconomyManager().formatAmount(totalPoints, "POINTS"));

        purchasedMeta.setLore(purchasedLore);
        purchasedButton.setItemMeta(purchasedMeta);
        inventory.setItem(47, purchasedButton);

        // 设置已售出按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(46, soldButton);

        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);

        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "purchased"));

        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_purchased_auctions"));
    }

    /**
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
     * 打开我的物品邮箱界面
     * @param player 玩家
     * @param page 页码
     */
    public void openMyMailboxMenu(Player player, int page) {
        // 获取玩家的所有拍卖物品
        List<AuctionItem> myItems = plugin.getDatabaseManager().getPlayerAuctionItems(player.getUniqueId());
        List<AuctionItem> yourMailboxItemsFromWonBids = plugin.getDatabaseManager().getAllMailboxItems(player.getUniqueId());
<<<<<<< HEAD

        // 结合所有邮箱物品
        List<AuctionItem> combinedMailboxItems = new ArrayList<>();

=======
        
        // 结合所有邮箱物品
        List<AuctionItem> combinedMailboxItems = new ArrayList<>();
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加自己发布但过期未售出的物品
        for (AuctionItem item : myItems) {
            if ("EXPIRED".equals(item.getStatus()) && item.getCurrentBidder() == null) {
                combinedMailboxItems.add(item);
            }
        }
<<<<<<< HEAD

        // 添加竞拍成功或背包已满时购买的物品
        combinedMailboxItems.addAll(yourMailboxItemsFromWonBids);

        // 按结束时间降序排序
        combinedMailboxItems.sort((item1, item2) ->
            Long.compare(item2.getEndTime(), item1.getEndTime()));

=======
        
        // 添加竞拍成功或背包已满时购买的物品
        combinedMailboxItems.addAll(yourMailboxItemsFromWonBids);
        
        // 按结束时间降序排序
        combinedMailboxItems.sort((item1, item2) -> 
            Long.compare(item2.getEndTime(), item1.getEndTime()));
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 计算总页数
        int totalItems = combinedMailboxItems.size();
        int itemsPerPage = 36; // 每页显示36个物品
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        if (page < 1) {
            page = 1;
        } else if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }
<<<<<<< HEAD

        // 创建界面，使用配置文件中的页码分隔符格式
        String pageSeparator = messageManager.getPageSeparatorFormat().replace("%page%", String.valueOf(page));
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyMailboxMenuTitlePrefix() + pageSeparator);

        // 计算当前页显示的物品范围
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

=======
        
        // 创建界面，使用配置文件中的页码分隔符格式
        String pageSeparator = messageManager.getPageSeparatorFormat().replace("%page%", String.valueOf(page));
        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getMyMailboxMenuTitlePrefix() + pageSeparator);
        
        // 计算当前页显示的物品范围
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加物品到界面
        for (int i = startIndex; i < endIndex; i++) {
            AuctionItem item = combinedMailboxItems.get(i);
            ItemStack displayItem = createMailboxItemDisplay(item);
            inventory.setItem(9 + (i - startIndex), displayItem);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置页码信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + messageManager.getMailboxText());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        List<String> infoLore = new ArrayList<>();
        infoLore.add(messageManager.getMailboxPageInfo() + page + "/" + totalPages);
        infoLore.add(messageManager.getMailboxItemsCount() + combinedMailboxItems.size());
        infoLore.add("");
        infoLore.add(messageManager.getMailboxStorageHeader());
        infoLore.add(messageManager.getMailboxStorageExpired());
        infoLore.add(messageManager.getMailboxStorageWon());
        infoLore.add(messageManager.getMailboxStorageFull());
<<<<<<< HEAD

        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);

=======
        
        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置导航和筛选按钮
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置筛选按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(messageManager.getSoldItemsText());
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(messageManager.getMailboxText());
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(messageManager.getMailboxDescriptionLine1());
        mailboxLore.add(messageManager.getMailboxDescriptionLine2());
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 返回按钮
        ItemStack returnButton = new ItemStack(Material.BARRIER);
        ItemMeta returnMeta = returnButton.getItemMeta();
        returnMeta.setDisplayName(messageManager.getReturnMainMenuText());
        returnMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        returnButton.setItemMeta(returnMeta);
        inventory.setItem(49, returnButton);
<<<<<<< HEAD

        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "mailbox"));

        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_mailbox"));
    }

=======
        
        // 保存当前页码到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "mailbox"));
        
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_mailbox"));
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 为邮箱中的物品创建显示ItemStack
     * @param item 拍卖物品
     * @return 用于显示的ItemStack
     */
    private ItemStack createMailboxItemDisplay(AuctionItem item) {
        ItemStack displayItem = item.getItem().clone();
        ItemMeta meta = displayItem.getItemMeta();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 添加物品信息
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) {
            lore.addAll(meta.getLore());
<<<<<<< HEAD

            // 如果已有Lore，检查是否有我们添加的邮箱标记，如果有则去除游戏内已有的邮箱标记
            // 以避免重复显示邮箱相关信息
            List<String> filterTags = messageManager.getMailboxFilterTags();
            lore.removeIf(line ->
                filterTags.stream().anyMatch(tag -> line.contains(tag)));

=======
            
            // 如果已有Lore，检查是否有我们添加的邮箱标记，如果有则去除游戏内已有的邮箱标记
            // 以避免重复显示邮箱相关信息
            List<String> filterTags = messageManager.getMailboxFilterTags();
            lore.removeIf(line -> 
                filterTags.stream().anyMatch(tag -> line.contains(tag)));
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            if (!lore.isEmpty()) {
                lore.add("");
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 根据物品的状态添加不同的描述
        if ("MAILBOX_PENDING".equals(item.getStatus())) {
            // 来自pending_items表的物品
            lore.add(messageManager.getMailboxItemStatusPending());
            // 使用创建时间而不是结束时间
<<<<<<< HEAD
            lore.add(messageManager.getMailboxItemAddTime() +
=======
            lore.add(messageManager.getMailboxItemAddTime() + 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(item.getStartTime())));
        } else if ("EXPIRED".equals(item.getStatus())) {
            // 来自auction_items表的过期物品
            lore.add(messageManager.getMailboxItemStatusExpired());
            // 使用结束时间，因为这表示物品过期的实际时间
<<<<<<< HEAD
            lore.add(messageManager.getMailboxItemExpireTime() +
=======
            lore.add(messageManager.getMailboxItemExpireTime() + 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(item.getEndTime())));
        } else {
            // 其他状态（通常不会出现）
            lore.add(messageManager.getMailboxItemStatusOther() + item.getStatus());
        }
<<<<<<< HEAD

        // 添加操作提示
        lore.add("");
        lore.add(messageManager.getMailboxItemCollectTip());

        meta.setLore(lore);
        displayItem.setItemMeta(meta);

        return displayItem;
    }

=======
        
        // 添加操作提示
        lore.add("");
        lore.add(messageManager.getMailboxItemCollectTip());
        
        meta.setLore(lore);
        displayItem.setItemMeta(meta);
        
        return displayItem;
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    // 新增方法：判断物品是否来自过期的拍卖物品
    private boolean isFromExpiredAuction(AuctionItem item) {
        // 检查物品是否有"过期未售出的物品"或"AUCTION_EXPIRED"的标记
        if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasLore()) {
            List<String> lore = item.getItem().getItemMeta().getLore();
            List<String> expiredTags = messageManager.getExpiredAuctionTags();
<<<<<<< HEAD
            return lore.stream().anyMatch(line ->
=======
            return lore.stream().anyMatch(line -> 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                expiredTags.stream().anyMatch(tag -> line.contains(tag)));
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
            player.sendMessage(messageManager.getItemNotExistsErrorMessage());
            return;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 调用基础方法
        openBidMenu(player, item.getId(), 0);
    }

    /**
     * 检查玩家是否正在查看拍卖行界面
     * @param player 要检查的玩家
     * @return 如果玩家正在查看拍卖行界面则返回true，否则返回false
     */
    public boolean isViewingGui(Player player) {
        // 通过检查玩家是否有相关元数据或者当前显示的标题来判断
<<<<<<< HEAD
        return player.getOpenInventory() != null &&
=======
        return player.getOpenInventory() != null && 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
               (player.getOpenInventory().getTitle().equals(configManager.getGuiTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getSearchMenuTitle()) ||
                player.getOpenInventory().getTitle().startsWith(messageManager.getSearchResultTitlePrefix()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getSellMenuTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getConfirmBuyMenuTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getBidMenuTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getMyAuctionsMenuTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getMySoldAuctionsMenuTitle()) ||
                player.getOpenInventory().getTitle().equals(messageManager.getMailboxMenuTitle()) ||
                player.hasMetadata("currentGui"));
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 关闭玩家当前的GUI界面
     * @param player 要关闭界面的玩家
     */
    public void closeGui(Player player) {
        if (isViewingGui(player)) {
            player.closeInventory();
            // 清除可能的元数据
            if (player.hasMetadata("auction_page")) {
                player.removeMetadata("auction_page", plugin);
            }
            if (player.hasMetadata("auction_search_input")) {
                player.removeMetadata("auction_search_input", plugin);
            }
            if (player.hasMetadata("currentGui")) {
                player.removeMetadata("currentGui", plugin);
            }
            if (player.hasMetadata("auction_my_page")) {
                player.removeMetadata("auction_my_page", plugin);
            }
            if (player.hasMetadata("auction_my_filter")) {
                player.removeMetadata("auction_my_filter", plugin);
            }
            // 清除玩家页码缓存
            playerPages.remove(player);
            playerSearchQueries.remove(player);
        }
    }
<<<<<<< HEAD

    // --- 新增管理员查询功能所需的 InventoryHolder --- START ---

    /**
     * 用于标识玩家信息主界面的 InventoryHolder
     */
    public interface PlayerInfoHolder extends InventoryHolder {
        OfflinePlayer getTargetPlayer();
    }

    /**
     * 用于标识玩家销售历史界面的 InventoryHolder
     */
    public interface PlayerSalesHistoryHolder extends InventoryHolder {
        OfflinePlayer getTargetPlayer();
        int getCurrentPage();
    }

    /**
     * 用于标识玩家购买历史界面的 InventoryHolder
     */
    public interface PlayerPurchaseHistoryHolder extends InventoryHolder {
        OfflinePlayer getTargetPlayer();
        int getCurrentPage();
    }

    // --- 新增管理员查询功能所需的 InventoryHolder --- END ---

    // --- 新增管理员查询功能的GUI方法 --- START ---

    /**
     * 打开玩家信息主界面 (管理员用)
     * @param admin 查看该界面的管理员
     * @param targetPlayer 被查询的玩家
     */
    public void openPlayerInfoMenu(Player admin, OfflinePlayer targetPlayer) {
        // 创建自定义 InventoryHolder
        PlayerInfoHolder holder = new PlayerInfoHolder() {
            @Override
            public OfflinePlayer getTargetPlayer() {
                return targetPlayer;
            }
            @Override
            public Inventory getInventory() {
                return null; // 在创建 Inventory 时设置
            }
        };

        String title = messageManager.getPlayerInfoGuiTitle(targetPlayer.getName()); // 新消息
        Inventory inventory = Bukkit.createInventory(holder, 54, title);

        // 获取统计数据
        int soldCount = plugin.getDatabaseManager().countPlayerSoldItems(targetPlayer.getUniqueId());
        int purchasedCount = plugin.getDatabaseManager().countPlayerPurchasedItems(targetPlayer.getUniqueId());
        Map<String, Double> earnings = plugin.getDatabaseManager().getPlayerTotalEarnings(targetPlayer.getUniqueId());
        Map<String, Double> spending = plugin.getDatabaseManager().getPlayerTotalSpending(targetPlayer.getUniqueId());
        int activeAuctions = plugin.getDatabaseManager().countPlayerActiveAuctions(targetPlayer.getUniqueId()); // 再次尝试添加此行: 获取活跃拍卖数量

        // 创建信息显示物品 (使用玩家头颅)
        ItemStack infoItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta infoMeta = (SkullMeta) infoItem.getItemMeta();
        infoMeta.setOwningPlayer(targetPlayer); // 设置为玩家自身的皮肤
        infoMeta.setDisplayName(messageManager.getPlayerInfoItemName(targetPlayer.getName())); // 新消息
        List<String> infoLore = new ArrayList<>();
        infoLore.add(messageManager.getPlayerInfoActiveAuctions().replace("%count%", String.valueOf(activeAuctions))); // 再次尝试添加此行: 显示活跃拍卖数量
        infoLore.add(messageManager.getPlayerInfoSoldCountFormat(soldCount)); // 新消息
        infoLore.add(messageManager.getPlayerInfoPurchasedCountFormat(purchasedCount)); // 新消息
        infoLore.add(""); // 分隔线

        // 显示收入 (区分货币类型)
        infoLore.add(messageManager.getPlayerInfoEarningsHeader()); // 新消息
        double vaultEarnings = earnings.getOrDefault("VAULT", 0.0);
        infoLore.add(messageManager.getPlayerInfoEarningsVaultFormat(plugin.getEconomyManager().formatVault(vaultEarnings))); // 新消息
        if (plugin.isPlayerPointsAvailable()) {
            double pointsEarnings = earnings.getOrDefault("POINTS", 0.0);
            infoLore.add(messageManager.getPlayerInfoEarningsPointsFormat(plugin.getEconomyManager().formatPoints(pointsEarnings))); // 新消息
        }

        infoLore.add(""); // 分隔线

        // 显示支出 (区分货币类型)
        infoLore.add(messageManager.getPlayerInfoSpendingHeader()); // 新消息
        double vaultSpending = spending.getOrDefault("VAULT", 0.0);
        infoLore.add(messageManager.getPlayerInfoSpendingVaultFormat(plugin.getEconomyManager().formatVault(vaultSpending))); // 新消息
        if (plugin.isPlayerPointsAvailable()) {
            double pointsSpending = spending.getOrDefault("POINTS", 0.0);
            infoLore.add(messageManager.getPlayerInfoSpendingPointsFormat(plugin.getEconomyManager().formatPoints(pointsSpending))); // 新消息
        }

        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(13, infoItem); // 放置在中间位置

        // 创建销售记录按钮
        ItemStack salesButton = new ItemStack(Material.GOLD_INGOT); // 使用金锭代表销售
        ItemMeta salesMeta = salesButton.getItemMeta();
        salesMeta.setDisplayName(messageManager.getPlayerInfoSalesHistoryButton()); // 新消息
        salesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        salesButton.setItemMeta(salesMeta);
        inventory.setItem(30, salesButton); // 左侧按钮

        // 创建购买记录按钮
        ItemStack purchaseButton = new ItemStack(Material.DIAMOND); // 使用钻石代表购买
        ItemMeta purchaseMeta = purchaseButton.getItemMeta();
        purchaseMeta.setDisplayName(messageManager.getPlayerInfoPurchaseHistoryButton()); // 新消息
        purchaseMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        purchaseButton.setItemMeta(purchaseMeta);
        inventory.setItem(32, purchaseButton); // 右侧按钮

        // 创建返回按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnText()); // 使用正确的 getReturnText()
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton); // 中间下方

        admin.openInventory(inventory);
    }

    /**
     * 打开玩家销售历史记录界面 (管理员用)
     * @param admin 查看该界面的管理员
     * @param targetPlayer 被查询的玩家
     * @param page 页码
     */
    public void openPlayerSalesHistoryMenu(Player admin, OfflinePlayer targetPlayer, int page) {
        // 获取数据 (先计算分页和最终页码)
        int itemsPerPage = 45;
        int totalItems = plugin.getDatabaseManager().countPlayerSoldItems(targetPlayer.getUniqueId());
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (totalPages == 0) totalPages = 1; // 至少有1页
        if (page > totalPages) page = totalPages; // 防止页码超出
        final int finalPage = page; // 在匿名类之前确定 final 值

        // 创建自定义 InventoryHolder (使用 finalPage)
        PlayerSalesHistoryHolder holder = new PlayerSalesHistoryHolder() {
            @Override
            public OfflinePlayer getTargetPlayer() { return targetPlayer; }
            @Override
            public int getCurrentPage() { return finalPage; } // 使用 finalPage
            @Override
            public Inventory getInventory() { return null; }
        };

        String title = messageManager.getPlayerSalesHistoryGuiTitle(targetPlayer.getName(), finalPage); // 使用 finalPage
        Inventory inventory = Bukkit.createInventory(holder, 54, title);

        // 获取当前页的数据
        List<AuctionItem> soldItems = plugin.getDatabaseManager().getPlayerSoldItems(targetPlayer.getUniqueId(), finalPage, itemsPerPage);

        // 填充物品
        for (int i = 0; i < soldItems.size(); i++) {
            AuctionItem item = soldItems.get(i);
            // 使用现有的 createAuctionItemDisplay，但可能需要调整以适应历史记录（比如移除操作提示）
            inventory.setItem(i, createHistoryItemDisplay(item)); // 需要一个新的 createHistoryItemDisplay 方法
        }

        // 设置分页按钮 (复用现有逻辑，但目标 holder 不同)
        setHistoryPaginationButtons(inventory, finalPage, totalPages, holder); // 使用 finalPage

        // 添加返回按钮 -> 返回玩家信息主界面
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getBackButtonToPlayerInfoText()); // 新消息
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton); // 中间下方

        admin.openInventory(inventory);
    }

    /**
     * 打开玩家购买历史记录界面 (管理员用)
     * @param admin 查看该界面的管理员
     * @param targetPlayer 被查询的玩家
     * @param page 页码
     */
    public void openPlayerPurchaseHistoryMenu(Player admin, OfflinePlayer targetPlayer, int page) {
        // 获取数据 (先计算分页和最终页码)
        int itemsPerPage = 45;
        int totalItems = plugin.getDatabaseManager().countPlayerPurchasedItems(targetPlayer.getUniqueId());
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (totalPages == 0) totalPages = 1; // 至少有1页
        if (page > totalPages) page = totalPages; // 防止页码超出
        final int finalPage = page; // 在匿名类之前确定 final 值

        // 创建自定义 InventoryHolder (使用 finalPage)
        PlayerPurchaseHistoryHolder holder = new PlayerPurchaseHistoryHolder() {
            @Override
            public OfflinePlayer getTargetPlayer() { return targetPlayer; }
            @Override
            public int getCurrentPage() { return finalPage; } // 使用 finalPage
            @Override
            public Inventory getInventory() { return null; }
        };

        String title = messageManager.getPlayerPurchaseHistoryGuiTitle(targetPlayer.getName(), finalPage); // 使用 finalPage
        Inventory inventory = Bukkit.createInventory(holder, 54, title);

        // 获取当前页的数据
        List<AuctionItem> purchasedItems = plugin.getDatabaseManager().getPlayerPurchasedItems(targetPlayer.getUniqueId(), finalPage, itemsPerPage);

        // 填充物品
        for (int i = 0; i < purchasedItems.size(); i++) {
            AuctionItem item = purchasedItems.get(i);
            inventory.setItem(i, createHistoryItemDisplay(item)); // 复用 createHistoryItemDisplay
        }

        // 设置分页按钮 (复用现有逻辑，但目标 holder 不同)
        setHistoryPaginationButtons(inventory, finalPage, totalPages, holder); // 使用 finalPage

        // 添加返回按钮 -> 返回玩家信息主界面
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getBackButtonToPlayerInfoText()); // 复用消息
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton); // 中间下方

        admin.openInventory(inventory);
    }

    /**
     * 创建历史记录物品的显示 (移除了操作提示)
     * @param item 拍卖物品 (已售出状态)
     * @return 展示用的ItemStack
     */
    private ItemStack createHistoryItemDisplay(AuctionItem item) {
        ItemStack original = item.getItem();
        ItemStack displayItem = original.clone();
        ItemMeta meta = displayItem.getItemMeta();
        if (meta == null) return displayItem;

        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        List<String> auctionLore = new ArrayList<>();
        String currencyName = plugin.getEconomyManager().getCurrencyName(item.getCurrencyType());

        // 尝试设置中文名 (如果配置启用)
        if (plugin.getConfigManager().isChineseLanguage()) {
            String itemId = original.getType().name().toLowerCase();
            String chineseName = plugin.getLanguageManager().getChineseName(itemId);
            if (!meta.hasDisplayName() && chineseName != null && !chineseName.isEmpty()) {
                meta.setDisplayName(messageManager.getItemNameColor() + chineseName);
            }
        } else if (!meta.hasDisplayName()) {
            // 非中文环境格式化名称
             String typeName = original.getType().toString().toLowerCase().replace('_', ' ');
             StringBuilder result = new StringBuilder();
             boolean capitalizeNext = true;
             for (char c : typeName.toCharArray()) {
                 if (c == ' ') {
                     result.append(c);
                     capitalizeNext = true;
                 } else if (capitalizeNext) {
                     result.append(Character.toUpperCase(c));
                     capitalizeNext = false;
                 } else {
                     result.append(c);
                 }
             }
             meta.setDisplayName(messageManager.getItemNameColor() + result.toString());
        }

        // 添加原版物品的lore
        if (originalLore != null && !originalLore.isEmpty()) {
            auctionLore.addAll(originalLore);
            auctionLore.add(""); // 空行分隔
        }

        // 添加拍卖信息
        auctionLore.add(messageManager.getAuctionItemDivider());
        auctionLore.add(messageManager.getHistoryInfoHeader()); // 新消息: 历史记录信息头
        auctionLore.add(messageManager.getAuctionItemIdFormat().replace("%id%", String.valueOf(item.getId())));
        auctionLore.add(messageManager.getAuctionCurrencyTypeFormat().replace("%currency%", currencyName));

        // 显示成交价格
        String dealPrice = messageManager.getAuctionDealPriceFormat()
            .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
        auctionLore.add(dealPrice);

        // 显示时间信息 (使用售出时间)
        auctionLore.add(messageManager.getHistorySoldTimeFormat().replace("%time%", item.getFormattedSoldTime())); // 新消息

        // 显示卖家信息
        auctionLore.add(messageManager.getAuctionSellerFormat().replace("%seller%", item.getSellerName()));

        // 显示买家信息 (即当前出价者)
        if (item.getCurrentBidder() != null && !item.getCurrentBidder().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            String bidderName = item.getCurrentBidderName() != null ? item.getCurrentBidderName() : Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName();
            if (bidderName != null) {
                 auctionLore.add(messageManager.getHistoryBuyerFormat().replace("%buyer%", bidderName)); // 新消息
            }
        }

        auctionLore.add(messageManager.getAuctionItemDivider());

        meta.setLore(auctionLore);
        displayItem.setItemMeta(meta);
        return displayItem;
    }

    /**
     * 设置历史记录界面的分页按钮
     * @param inv 物品栏
     * @param page 当前页
     * @param totalPages 总页数
     * @param holder 当前界面的 InventoryHolder (用于区分销售/购买历史)
     */
    private void setHistoryPaginationButtons(Inventory inv, int page, int totalPages, InventoryHolder holder) {
        // 上一页按钮
        if (page > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            prevButton.setItemMeta(prevMeta);
            inv.setItem(48, prevButton); // 左下角
        }

        // 下一页按钮
        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(messageManager.getNextPageText());
            nextButton.setItemMeta(nextMeta);
            inv.setItem(50, nextButton); // 右下角
        }

        // 当前页码显示 (使用新的方法)
        ItemStack pageIndicator = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageIndicator.getItemMeta();
        // 使用新的带参数的方法
        pageMeta.setDisplayName(messageManager.getCurrentPageFormat(page, totalPages));
        pageIndicator.setItemMeta(pageMeta);
        // inventory.setItem(53, pageIndicator); // 放在右下角或其他位置
        // 注意：在标准54格GUI中，49是中间，48左，50右。可以把页码放在最右下角53?
        // 暂时不放置页码指示器以保持简洁，因为标题已有页码
    }

    // --- 新增管理员查询功能的GUI方法 --- END ---

    // 打开卖家搜索结果界面
    public void openSellerSearchResultMenu(Player player, String sellerName, int page) {
        // 添加到搜索历史
        plugin.getSearchHistoryManager().addSearchHistory(player, "卖家:" + sellerName);

        // 计算每页显示物品数量（9-44槽位，共36个物品）
        int itemsPerPage = 36;

        // 获取搜索结果
        List<AuctionItem> items = plugin.getDatabaseManager().searchItemsBySeller(sellerName, page, itemsPerPage);

        // 获取搜索结果总数
        int totalItems = plugin.getDatabaseManager().getSellerSearchResultCount(sellerName);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        Inventory inventory = Bukkit.createInventory(null, 54, messageManager.getSearchResultTitlePrefix() + " 卖家:" + sellerName);

        // 设置标题栏（第一行）
        for (int i = 0; i < 9; i++) {
            ItemStack titleItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = titleItem.getItemMeta();
            meta.setDisplayName(" ");
            titleItem.setItemMeta(meta);
            inventory.setItem(i, titleItem);
        }

        // 设置页码信息在标题栏中间
        ItemStack pageInfo = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta infoMeta = (SkullMeta) pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "卖家: " + sellerName + " 的物品");
        List<String> infoLore = new ArrayList<>();
        String pageInfoText = messageManager.getPageInfoPrefix() + page + "/" + (totalPages > 0 ? totalPages : 1);
        infoLore.add(pageInfoText);

        String totalItemsText = messageManager.getSoldItemsCountPrefix() + totalItems;
        infoLore.add(totalItemsText);

        infoMeta.setLore(infoLore);
        pageInfo.setItemMeta(infoMeta);
        inventory.setItem(4, pageInfo);

        // 设置搜索结果物品（从第二行开始，9-44槽位）
        for (int i = 0; i < items.size(); i++) {
            AuctionItem item = items.get(i);
            inventory.setItem(i + 9, createAuctionItemDisplay(item, player));
        }

        // 设置导航按钮，重用现有方法但传递自定义前缀
        setSellerSearchResultNavigationButtons(inventory, page, totalPages, sellerName);

        // 保存当前搜索关键词和页码
        playerSearchQueries.put(player, "seller:" + sellerName);
        playerPages.put(player, page);

        // 设置元数据，供监听器使用
        player.setMetadata("auction_search_seller", new org.bukkit.metadata.FixedMetadataValue(plugin, sellerName));
        player.setMetadata("auction_search_page", new org.bukkit.metadata.FixedMetadataValue(plugin, page));

        player.openInventory(inventory);
    }

    // 设置卖家搜索结果的导航按钮
    private void setSellerSearchResultNavigationButtons(Inventory inventory, int page, int totalPages, String sellerName) {
        // 设置上一页按钮
        if (page > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(messageManager.getPreviousPageText());
            List<String> prevLore = new ArrayList<>();
            prevLore.add(messageManager.getPreviousPageDescText());
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
            nextLore.add(messageManager.getNextPageDescText());
            nextMeta.setLore(nextLore);
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(53, nextButton);
        }

        // 设置新搜索按钮
        ItemStack searchButton = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = searchButton.getItemMeta();
        searchMeta.setDisplayName(messageManager.getNewSearchText());
        List<String> searchLore = new ArrayList<>();
        searchLore.add(messageManager.getNewSearchDescText());
        searchMeta.setLore(searchLore);
        searchMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        searchButton.setItemMeta(searchMeta);
        inventory.setItem(48, searchButton);

        // 设置返回主菜单按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(messageManager.getReturnText());
        List<String> backLore = new ArrayList<>();
        backLore.add(messageManager.getReturnMainMenuDescText());
        backMeta.setLore(backLore);
        backMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }
}
=======
} 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
