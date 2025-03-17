package cn.i7mc.globalshop.gui;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import cn.i7mc.globalshop.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiManager {
    private final GlobalShop plugin;
    private final Map<Player, Integer> playerPages;
    private final Map<Player, String> playerSearchQueries;

    public GuiManager(GlobalShop plugin) {
        this.plugin = plugin;
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
        Inventory inventory = Bukkit.createInventory(null, plugin.getConfigManager().getGuiSize(),
                plugin.getConfigManager().getGuiTitle());

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

        // 设置导航按钮（传入总页数以决定是否显示下一页按钮）
        setNavigationButtons(inventory, page, totalPages);

        // 设置功能按钮
        setFunctionButtons(inventory);

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

    // 设置导航按钮
    private void setNavigationButtons(Inventory inventory, int currentPage, int totalPages) {
        // 上一页按钮
        if (currentPage > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("上一页");
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }

        // 下一页按钮 - 只有当有下一页时才显示
        if (currentPage < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("下一页");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
    }

    // 设置功能按钮
    private void setFunctionButtons(Inventory inventory) {
        // 搜索按钮
        ItemStack search = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = search.getItemMeta();
        searchMeta.setDisplayName("搜索物品");
        search.setItemMeta(searchMeta);
        inventory.setItem(47, search);

        // 上架按钮
        ItemStack sell = new ItemStack(Material.EMERALD);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("上架物品");
        sell.setItemMeta(sellMeta);
        inventory.setItem(49, sell);

        // 我的拍卖按钮
        ItemStack myAuctions = new ItemStack(Material.BOOK);
        ItemMeta myAuctionsMeta = myAuctions.getItemMeta();
        myAuctionsMeta.setDisplayName("我的拍卖");
        myAuctions.setItemMeta(myAuctionsMeta);
        inventory.setItem(51, myAuctions);
    }

    // 打开搜索界面
    public void openSearchMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "搜索物品");

        // 设置搜索框
        ItemStack searchBox = new ItemStack(Material.PAPER);
        ItemMeta searchMeta = searchBox.getItemMeta();
        searchMeta.setDisplayName("输入物品名称");
        List<String> searchLore = new ArrayList<>();
        searchLore.add(ChatColor.YELLOW + "点击此处输入搜索关键词");
        searchLore.add(ChatColor.GRAY + "支持模糊搜索，例如：\"钻石\"、\"剑\"等");
        searchLore.add(ChatColor.GRAY + "支持中文搜索原版物品，例如：\"钻石\" 可匹配钻石块、钻石剑等");
        searchMeta.setLore(searchLore);
        searchBox.setItemMeta(searchMeta);
        inventory.setItem(13, searchBox);
        
        // 设置搜索历史
        List<String> history = plugin.getSearchHistoryManager().getSearchHistory(player);
        if (!history.isEmpty()) {
            ItemStack historyItem = new ItemStack(Material.BOOK);
            ItemMeta historyMeta = historyItem.getItemMeta();
            historyMeta.setDisplayName(ChatColor.GOLD + "搜索历史");
            List<String> historyLore = new ArrayList<>();
            // 最多显示5条历史记录
            for (int i = 0; i < Math.min(5, history.size()); i++) {
                historyLore.add(ChatColor.YELLOW + "➤ " + ChatColor.WHITE + history.get(i));
            }
            historyLore.add("");
            historyLore.add(ChatColor.GRAY + "点击历史记录进行搜索");
            historyMeta.setLore(historyLore);
            historyItem.setItemMeta(historyMeta);
            inventory.setItem(31, historyItem);
            
            // 设置历史按钮
            for (int i = 0; i < Math.min(5, history.size()); i++) {
                ItemStack historyButton = new ItemStack(Material.PAPER);
                ItemMeta buttonMeta = historyButton.getItemMeta();
                buttonMeta.setDisplayName(ChatColor.GREEN + "搜索: " + history.get(i));
                List<String> buttonLore = new ArrayList<>();
                buttonLore.add(ChatColor.YELLOW + "点击使用此关键词搜索");
                buttonMeta.setLore(buttonLore);
                historyButton.setItemMeta(buttonMeta);
                inventory.setItem(39 + i, historyButton);
            }
            
            // 设置清除历史按钮
            ItemStack clearButton = new ItemStack(Material.BARRIER);
            ItemMeta clearMeta = clearButton.getItemMeta();
            clearMeta.setDisplayName(ChatColor.RED + "清除搜索历史");
            clearMeta.setLore(Collections.singletonList(ChatColor.GRAY + "点击清除所有搜索历史"));
            clearButton.setItemMeta(clearMeta);
            inventory.setItem(48, clearButton);
        }

        // 设置返回按钮
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("返回主菜单");
        back.setItemMeta(backMeta);
        inventory.setItem(49, back);

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
        
        Inventory inventory = Bukkit.createInventory(null, 54, "搜索结果: " + keyword);
        
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
        infoMeta.setDisplayName(ChatColor.GOLD + "搜索结果: " + ChatColor.WHITE + keyword);
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
        
        // 设置导航按钮 (上一页、下一页)
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "上一页");
            List<String> prevLore = new ArrayList<>();
            prevLore.add(ChatColor.GRAY + "点击查看上一页");
            prevMeta.setLore(prevLore);
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ChatColor.YELLOW + "下一页");
            List<String> nextLore = new ArrayList<>();
            nextLore.add(ChatColor.GRAY + "点击查看下一页");
            nextMeta.setLore(nextLore);
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 设置搜索按钮
        ItemStack search = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = search.getItemMeta();
        searchMeta.setDisplayName(ChatColor.GREEN + "新搜索");
        List<String> searchLore = new ArrayList<>();
        searchLore.add(ChatColor.GRAY + "点击进行新的搜索");
        searchMeta.setLore(searchLore);
        search.setItemMeta(searchMeta);
        inventory.setItem(48, search);
        
        // 设置返回按钮
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回主菜单");
        List<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "点击返回主菜单");
        backMeta.setLore(backLore);
        back.setItemMeta(backMeta);
        inventory.setItem(49, back);
        
        // 保存当前搜索关键词和页码到玩家元数据中
        player.setMetadata("auction_search_keyword", new FixedMetadataValue(plugin, keyword));
        player.setMetadata("auction_search_page", new FixedMetadataValue(plugin, page));
        
        player.openInventory(inventory);
    }

    // 打开上架界面
    public void openSellMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "上架物品");

        // 设置上架区 - 只在22号槽位放置提示玻璃板
        inventory.setItem(22, createPlaceholder());

        // 设置返回按钮
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("返回主菜单");
        back.setItemMeta(backMeta);
        inventory.setItem(49, back);

        player.openInventory(inventory);
    }

    // 创建占位符
    public ItemStack createPlaceholder() {
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        meta.setDisplayName("请将物品放在这里");
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
        Inventory inv = Bukkit.createInventory(player, 27, "确认购买");
        
        // 在中央展示物品
        inv.setItem(13, item.getItem());
        
        // 确认按钮（绿色玻璃板）
        ItemStack confirmButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "确认购买");
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(ChatColor.YELLOW + "点击确认购买此物品");
        confirmLore.add(ChatColor.YELLOW + "物品名称: " + ChatColor.WHITE + ChatUtils.getItemName(item.getItem()));
        confirmLore.add(ChatColor.YELLOW + "购买价格: " + ChatColor.WHITE + item.getBuyNowPrice() + " (一口价)");
        
        // 不再添加税费信息，因为买家不需要支付税费
        
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        
        // 取消按钮（红色玻璃板）
        ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "取消");
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

    // 打开竞价界面
    public void openBidMenu(Player player, AuctionItem item) {
        Inventory inventory = Bukkit.createInventory(null, 27, "竞价购买");

        // 设置物品显示
        inventory.setItem(13, item.getItem());
        
        // 计算初始竞价金额（当前价格 + 5%）
        double currentPrice = item.getCurrentPrice();
        double bidIncrement = currentPrice * 0.05;
        double initialBidAmount = currentPrice + bidIncrement;
        
        // 设置当前竞价金额显示（顶部）
        ItemStack bidAmount = new ItemStack(Material.GOLD_INGOT);
        ItemMeta bidAmountMeta = bidAmount.getItemMeta();
        bidAmountMeta.setDisplayName("§e当前竞价金额");
        List<String> bidAmountLore = new ArrayList<>();
        bidAmountLore.add("§7物品: §f" + ChatUtils.getItemName(item.getItem()));
        bidAmountLore.add("§7原始价格: §f" + plugin.getEconomyManager().formatAmount(currentPrice, item.getCurrencyType()));
        bidAmountLore.add("§7当前竞价: §f" + plugin.getEconomyManager().formatAmount(initialBidAmount, item.getCurrencyType()));
        bidAmountLore.add("§7加价幅度: §f+5%");
        bidAmountMeta.setLore(bidAmountLore);
        bidAmount.setItemMeta(bidAmountMeta);
        inventory.setItem(4, bidAmount);
        
        // 设置加价按钮（左侧）
        ItemStack increaseButton = new ItemStack(Material.LIME_WOOL);
        ItemMeta increaseMeta = increaseButton.getItemMeta();
        increaseMeta.setDisplayName("§a加价 +5%");
        List<String> increaseLore = new ArrayList<>();
        increaseLore.add("§7点击增加竞价金额");
        increaseLore.add("§7每次增加当前价格的5%");
        increaseMeta.setLore(increaseLore);
        increaseButton.setItemMeta(increaseMeta);
        inventory.setItem(11, increaseButton);
        
        // 设置确认按钮（右侧）
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§2确认竞价");
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§7点击确认当前竞价金额");
        confirmLore.add("§7当前竞价: §f" + plugin.getEconomyManager().formatAmount(initialBidAmount, item.getCurrencyType()));
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(15, confirmButton);
        
        // 设置取消按钮（底部）
        ItemStack cancelButton = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("§c取消竞价");
        cancelButton.setItemMeta(cancelMeta);
        inventory.setItem(22, cancelButton);
        
        // 保存物品ID和初始竞价金额到元数据
        player.setMetadata("auction_bid_id", new org.bukkit.metadata.FixedMetadataValue(plugin, item.getId()));
        player.setMetadata("auction_bid_amount", new org.bukkit.metadata.FixedMetadataValue(plugin, initialBidAmount));
        
        player.openInventory(inventory);
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
        int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
        
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
        Inventory inventory = Bukkit.createInventory(null, 54, "我的拍卖");
        
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
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("上一页");
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("下一页");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 移除活跃拍卖按钮，保留已售出和已过期按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(ChatColor.GOLD + "已售出");
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton); // 调整位置到原活跃拍卖按钮的位置
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(ChatColor.YELLOW + "物品邮箱");
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton); // 调整位置到51
        
        // 返回按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回主菜单");
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
        
        // 保存当前页码和筛选条件到玩家数据
        player.setMetadata("auction_my_page", new FixedMetadataValue(plugin, page));
        player.setMetadata("auction_my_filter", new FixedMetadataValue(plugin, "active"));
        
        player.openInventory(inventory);
        player.setMetadata("currentGui", new FixedMetadataValue(plugin, "my_auctions"));
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
        Inventory inventory = Bukkit.createInventory(null, 54, "我的已售出拍卖");
        
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
            prevMeta.setDisplayName("上一页");
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("下一页");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 设置筛选按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(ChatColor.GOLD + "已售出");
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(ChatColor.YELLOW + "物品邮箱");
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
        
        // 返回按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回主菜单");
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
        
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
        Inventory inventory = Bukkit.createInventory(null, 54, "§e我的物品邮箱 §7- 第 " + page + " 页");
        
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
            prevMeta.setDisplayName("上一页");
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(45, prevPage);
        }
        
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("下一页");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }
        
        // 设置筛选按钮
        ItemStack soldButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta soldMeta = soldButton.getItemMeta();
        soldMeta.setDisplayName(ChatColor.GOLD + "已售出");
        soldButton.setItemMeta(soldMeta);
        inventory.setItem(47, soldButton);
        
        ItemStack mailboxButton = new ItemStack(Material.CHEST);
        ItemMeta mailboxMeta = mailboxButton.getItemMeta();
        mailboxMeta.setDisplayName(ChatColor.YELLOW + "物品邮箱");
        List<String> mailboxLore = new ArrayList<>();
        mailboxLore.add(ChatColor.GRAY + "查看你的物品邮箱");
        mailboxLore.add(ChatColor.GRAY + "包含过期、竞拍和背包已满的物品");
        mailboxMeta.setLore(mailboxLore);
        mailboxButton.setItemMeta(mailboxMeta);
        inventory.setItem(51, mailboxButton);
        
        // 返回按钮
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回主菜单");
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
        
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
} 