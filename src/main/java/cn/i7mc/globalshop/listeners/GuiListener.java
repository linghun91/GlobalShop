package cn.i7mc.globalshop.listeners;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import cn.i7mc.globalshop.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class GuiListener implements Listener {
    private final GlobalShop plugin;
    // 添加一个集合来跟踪已处理的物品上架事件
    private final Set<UUID> processingPlayers = new HashSet<>();

    public GuiListener(GlobalShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        String title = event.getView().getTitle();
        
        // 阻止在上架物品界面使用shift+右键点击物品
        if (title.equals("上架物品") && event.isShiftClick()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "请直接拖动物品到中央格子，不要使用Shift+点击");
            return;
        }
        
        // 主界面，禁止所有操作
        if (title.equals(plugin.getConfigManager().getGuiTitle())) {
            event.setCancelled(true);
            handleMainMenuClick(event, player);
        } else if (title.equals("搜索物品")) {
            event.setCancelled(true);
            handleSearchMenuClick(event, player);
        } else if (title.startsWith("搜索结果:")) {
            event.setCancelled(true);
            handleSearchResultClick(event, player);
        } else if (title.equals("上架物品")) {
            // 对于上架界面，先取消所有事件，然后在handleSellMenuClick中根据需要有选择地允许某些操作
            event.setCancelled(true);
            handleSellMenuClick(event, player);
        } else if (title.equals("确认购买")) {
            event.setCancelled(true);
            handleConfirmBuyClick(event, player);
        } else if (title.equals("竞价购买")) {
            event.setCancelled(true);
            handleBidMenuClick(event, player);
        } else if (title.equals("我的拍卖") || 
                  title.equals("我的已售出拍卖")) {
            event.setCancelled(true);
            handleMyAuctionsMenuClick(event, player);
        } else if (title.equals("§8物品邮箱") || title.startsWith("§e我的物品邮箱")) {
            // 对于物品邮箱界面，需要特殊处理，阻止放入物品
            event.setCancelled(true);
            
            // 检查是否试图从玩家物品栏向物品邮箱界面移动物品
            if (event.getAction() == org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                (event.getAction() == org.bukkit.event.inventory.InventoryAction.PLACE_ALL && event.getRawSlot() < 54)) {
                player.sendMessage(ChatColor.RED + "不能在物品邮箱界面放入物品！");
                return;
            }
            
            // 正常处理物品邮箱的取出等操作
            handleMyAuctionsMenuClick(event, player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        // 主界面和搜索界面禁止拖拽
        if (title.equals(plugin.getConfigManager().getGuiTitle()) ||
            title.equals("搜索物品") ||
            title.startsWith("搜索结果:") ||
            title.equals("确认购买") ||
            title.equals("竞价购买") ||
            title.equals("我的拍卖") ||
            title.equals("我的已售出拍卖") ||
            title.equals("§8物品邮箱") ||
            title.startsWith("§e我的物品邮箱")) {
            
            // 特殊处理已过期拍卖界面，不仅禁止拖拽，还显示提示信息
            if (title.equals("我的过期拍卖（无人出价）") || title.startsWith("§e我的物品邮箱")) {
                // 检查是否尝试拖拽到上半部分界面（前54个槽位）
                boolean draggingToTop = false;
                for (int slot : event.getRawSlots()) {
                    if (slot < 54) {
                        draggingToTop = true;
                        break;
                    }
                }
                
                if (draggingToTop) {
                    if (title.startsWith("§e我的物品邮箱")) {
                        player.sendMessage(ChatColor.RED + "不能在物品邮箱界面放入物品！请右键点击邮箱中的物品取出。");
                    } else {
                        player.sendMessage(ChatColor.RED + "不能在过期拍卖界面放入物品！");
                    }
                }
            }
            
            event.setCancelled(true);
            return;
        }
        
        // 对于上架界面，需要检查拖拽的范围
        if (title.equals("上架物品")) {
            // 获取所有被拖拽影响的槽位
            Set<Integer> slots = event.getRawSlots();
            
            // 检查是否影响了非上架区的槽位（上架区只有22号槽位）
            for (Integer slot : slots) {
                // 如果拖拽影响了非上架区，则取消
                if (slot != 22 && slot < event.getView().getTopInventory().getSize()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "请将物品放在中央的格子内");
                    return;
                }
                
                // 检查22号槽位是否包含玻璃板
                if (slot == 22) {
                    ItemStack item = event.getView().getItem(slot);
                    if (item != null && item.getType() == Material.GRAY_STAINED_GLASS_PANE &&
                        item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                        item.getItemMeta().getDisplayName().equals("请将物品放在这里")) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (event.getView().getTitle().equals("上架物品")) {
            handleSellMenuClose(event, player);
        } 
        else if (event.getView().getTitle().equals("竞价购买")) {
            // 检查是否是加价操作中或通过确认按钮关闭的
            if (player.hasMetadata("auction_bid_confirmed") || player.hasMetadata("auction_bid_in_progress")) {
                // 处理确认按钮或加价操作关闭，清除确认标记
                if (player.hasMetadata("auction_bid_confirmed")) {
                    player.removeMetadata("auction_bid_confirmed", plugin);
                }
                if (player.hasMetadata("auction_bid_in_progress")) {
                    player.removeMetadata("auction_bid_in_progress", plugin);
                }
                
                // 获取当前竞价物品
                if (player.hasMetadata("auction_bid_id")) {
                    int itemId = player.getMetadata("auction_bid_id").get(0).asInt();
                    AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
                }
            } 
            // 如果不是确认竞价或加价操作，说明是取消操作
            else {
                player.sendMessage("§c已取消竞价操作");
                
                // 清除所有竞价相关元数据
                if (player.hasMetadata("auction_bid_id")) {
                    player.removeMetadata("auction_bid_id", plugin);
                }
                if (player.hasMetadata("auction_bid_amount")) {
                    player.removeMetadata("auction_bid_amount", plugin);
                }
            }
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int slot = event.getRawSlot();
        Material itemType = clickedItem.getType();

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
        } else if (slot == 51) {
            // 我的拍卖按钮
            plugin.getGuiManager().openMyAuctionsMenu(player, 1);
        }
    }

    private void handleSearchMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        
        // 点击搜索框，打开聊天栏输入搜索关键词
        if (slot == 13) {
            player.closeInventory();
            player.sendMessage("§a请在聊天栏输入要搜索的物品名称，或者输入 §e取消 §a来取消搜索");
            player.setMetadata("auction_search_input", new FixedMetadataValue(plugin, true));
            return;
        }
        
        // 点击历史记录按钮（历史条目）
        if (slot >= 39 && slot <= 43) {
            List<String> history = plugin.getSearchHistoryManager().getSearchHistory(player);
            int index = slot - 39;
            if (index < history.size()) {
                String keyword = history.get(index);
                plugin.getGuiManager().openSearchResultMenu(player, keyword, 1);
            }
            return;
        }
        
        // 点击清除历史
        if (slot == 48) {
            plugin.getSearchHistoryManager().clearSearchHistory(player);
            plugin.getGuiManager().openSearchMenu(player);
            player.sendMessage("§a搜索历史已清空");
            return;
        }
        
        // 点击返回按钮
        if (slot == 49) {
            plugin.getGuiManager().openMainMenu(player);
        }
    }
    
    private void handleSearchResultClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        
        // 获取当前搜索信息
        String keyword = player.getMetadata("auction_search_keyword").get(0).asString();
        int currentPage = player.getMetadata("auction_search_page").get(0).asInt();
        
        // 点击物品（从槽位9到44是物品展示区）
        if (slot >= 9 && slot <= 44) {
            // 计算物品在列表中的索引
            int itemIndex = slot - 9;
            
            // 每页显示36个物品
            int itemsPerPage = 36;
            List<AuctionItem> items = plugin.getDatabaseManager().searchAuctionItems(keyword, currentPage, itemsPerPage);
            
            if (itemIndex < items.size()) {
                AuctionItem item = items.get(itemIndex);
                handleAuctionItemClick(event, player, item);
            }
            return;
        }
        
        // 点击上一页
        if (slot == 45 && currentPage > 1) {
            plugin.getGuiManager().openSearchResultMenu(player, keyword, currentPage - 1);
            return;
        }
        
        // 点击下一页
        if (slot == 53) {
            int totalItems = plugin.getDatabaseManager().getSearchResultCount(keyword);
            int totalPages = (int) Math.ceil((double) totalItems / 36);
            if (currentPage < totalPages) {
                plugin.getGuiManager().openSearchResultMenu(player, keyword, currentPage + 1);
            }
            return;
        }
        
        // 点击新搜索
        if (slot == 48) {
            plugin.getGuiManager().openSearchMenu(player);
            return;
        }
        
        // 点击返回主菜单
        if (slot == 49) {
            plugin.getGuiManager().openMainMenu(player);
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
                player.sendMessage(ChatColor.RED + "请直接拖动物品到中央格子，不要使用Shift+点击");
                return;
            }
        }
        
        // 如果点击了返回按钮
        if (slot == 49 && !isBottomInventory) {
            if (currentItem != null && currentItem.getType() == Material.BARRIER &&
                currentItem.hasItemMeta() && currentItem.getItemMeta().getDisplayName().equals("返回主菜单")) {
                player.closeInventory();
                plugin.getGuiManager().openMainMenu(player);
                return; // 已处理，保持取消状态
            }
        }
        
        // 处理上架区域（只有22号槽位）的点击
        if (slot == 22 && !isBottomInventory) {
            // 处理点击玻璃板的情况
            if (currentItem != null && currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE && 
                currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName() && 
                currentItem.getItemMeta().getDisplayName().equals("请将物品放在这里")) {
                
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
                        // 对于PLACE_ALL和SWAP_WITH_CURSOR，直接放置所有并清空光标
                        player.setItemOnCursor(null);
                    }
                    
                    // 设置物品到槽位
                    event.getView().getTopInventory().setItem(slot, itemToPlace);
                    
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
                        slotItem.getItemMeta().getDisplayName().equals("请将物品放在这里"));
                
                // 如果没有找到合适的槽位，取消操作
                if (!foundSlot) {
                    event.setCancelled(true);
                }
            }
        }
        // 其他GUI区域的点击（如功能按钮区域）默认已经取消
    }

    private void handleSellMenuClose(InventoryCloseEvent event, Player player) {
        Inventory inventory = event.getInventory();
        ItemStack itemToSell = null;
        
        // 只检查22号槽位是否有物品
        ItemStack item = inventory.getItem(22);
        if (item != null && item.getType() != Material.AIR) {
            // 判断是否是占位玻璃板，如果是则忽略
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE && 
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() && 
                item.getItemMeta().getDisplayName().equals("请将物品放在这里")) {
                return;
            }
            
            itemToSell = item;
        }
        
        // 如果没有物品，直接返回
        if (itemToSell == null) {
            return;
        }
        
        // 保存物品的副本，用于后续可能的返还
        ItemStack itemCopy = itemToSell.clone();
        boolean itemReturned = false;
        
        try {
            // 检查玩家是否有上架命令元数据
            if (!player.hasMetadata("auction_sell_command")) {
                player.sendMessage("§c上架失败：缺少价格信息");
                returnItem(player, itemCopy);
                itemReturned = true;
                return;
            }
            
            // 获取玩家之前设置的起拍价和一口价
            String command = player.getMetadata("auction_sell_command").get(0).asString();
            String[] args = command.split(" ");
            
            // 验证命令格式
            if (args.length < 2) {
                player.sendMessage("§c上架失败：价格信息不完整");
                returnItem(player, itemCopy);
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
                player.sendMessage("§c上架失败：价格格式错误");
                returnItem(player, itemCopy);
                itemReturned = true;
                return;
            }
            
            // 验证价格
            if (startPrice <= 0) {
                player.sendMessage("§c起拍价必须大于0!");
                returnItem(player, itemCopy);
                itemReturned = true;
                return;
            }
            if (buyNowPrice > 0 && buyNowPrice <= startPrice) {
                player.sendMessage("§c一口价必须大于起拍价!");
                returnItem(player, itemCopy);
                itemReturned = true;
                return;
            }
            
            // 获取货币类型
            String currencyType = "VAULT"; // 默认为金币
            if (player.hasMetadata("auction_currency_type")) {
                currencyType = player.getMetadata("auction_currency_type").get(0).asString();
            }
            
            // 初始化手续费
            double fee = 0;
            
            // 只有金币类型才收取手续费
            if ("VAULT".equals(currencyType)) {
                // 计算手续费
                fee = plugin.getEconomyManager().calculateFee(startPrice, currencyType);
                
                // 检查玩家是否有足够的钱支付手续费
                if (!plugin.getEconomyManager().hasEnough(player, fee, currencyType)) {
                    player.sendMessage("§c你没有足够的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "支付手续费!");
                    returnItem(player, itemCopy);
                    itemReturned = true;
                    return;
                }
            }
            // 点券类型不收取手续费
            
            // 检查玩家当前上架的物品数量是否已达到上限
            int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
            int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
            
            if (currentListings >= maxListings) {
                player.sendMessage(ChatColor.RED + "你已达到最大上架数量限制 (" + maxListings + " 个物品)!");
                player.sendMessage(ChatColor.RED + "请等待已上架物品售出或过期后再尝试上架新物品。");
                player.closeInventory();
                return;
            }
            
            // 创建拍卖物品
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
                System.currentTimeMillis() + plugin.getConfigManager().getDefaultDuration() * 1000,
                "ACTIVE"
            );
            
            // 保存到数据库
            int id = plugin.getDatabaseManager().createAuctionItem(auctionItem);
            if (id > 0) {
                // 上架成功
                // 扣除手续费（只对金币类型收取）
                if ("VAULT".equals(currencyType) && fee > 0) {
                    plugin.getEconomyManager().takeMoney(player, fee, currencyType);
                }
                
                // 发送成功消息
                player.sendMessage("§a物品上架成功!");
                player.sendMessage("§e物品ID: " + id);
                player.sendMessage("§e起拍价: " + plugin.getEconomyManager().formatAmount(startPrice, currencyType));
                if (buyNowPrice > 0) {
                    player.sendMessage("§e一口价: " + plugin.getEconomyManager().formatAmount(buyNowPrice, currencyType));
                }
                // 只有收取了手续费才显示
                if (fee > 0) {
                    player.sendMessage("§e手续费: " + plugin.getEconomyManager().formatAmount(fee, currencyType));
                }
                player.sendMessage("§e货币类型: " + plugin.getEconomyManager().getCurrencyName(currencyType));
                
                // 添加当前余额显示
                double balance = plugin.getEconomyManager().getBalance(player, currencyType);
                player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "余额: §f" + plugin.getEconomyManager().formatAmount(balance, currencyType));
                
                // 标记物品已处理（上架成功）
                itemReturned = true;
                
                // 使用异步任务延迟打开主界面，避免无限递归
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGuiManager().openMainMenu(player);
                }, 2L); // 延迟2个tick
            } else {
                // 上架失败
                player.sendMessage("§c物品上架失败，请重试!");
                returnItem(player, itemCopy);
                itemReturned = true;
            }
        } catch (Exception e) {
            // 捕获任何异常，确保物品不会丢失
            e.printStackTrace();
            player.sendMessage("§c上架物品时发生错误，物品已返还!");
            returnItem(player, itemCopy);
            itemReturned = true;
        } finally {
            // 清除玩家的命令元数据
            if (player.hasMetadata("auction_sell_command")) {
                player.removeMetadata("auction_sell_command", plugin);
            }
            if (player.hasMetadata("auction_currency_type")) {
                player.removeMetadata("auction_currency_type", plugin);
            }
            
            // 最终安全检查：如果物品尚未返还且上架未成功，则返还物品
            if (!itemReturned) {
                // 再次检查物品是否有效以避免重复返还
                if (itemCopy != null && itemCopy.getType() != Material.AIR) {
                    returnItem(player, itemCopy);
                }
            }
        }
    }
    
    // 帮助方法：返还物品给玩家
    private void returnItem(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        
        // 尝试将物品添加到玩家背包
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            player.sendMessage("§e物品已返还到你的背包");
        } else {
            // 背包已满，掉落物品
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage("§e背包已满，物品已掉落在你的脚下");
        }
    }
    
    // 帮助方法：返还上架界面中的所有物品
    private void returnAllItems(Player player, Inventory inventory) {
        // 现在只有22号槽位需要检查
        ItemStack item = inventory.getItem(22);
        if (item != null && item.getType() != Material.AIR) {
            // 跳过占位玻璃板
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE && 
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() && 
                item.getItemMeta().getDisplayName().equals("请将物品放在这里")) {
                return;
            }
            
            // 返还物品
            returnItem(player, item);
            
            // 从界面中移除物品
            inventory.setItem(22, null);
        }
    }

    private void handleAuctionItemClick(InventoryClickEvent event, Player player, AuctionItem item) {
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage("§c该物品已结束拍卖!");
            return;
        }

        // 判断是否是物品主人
        boolean isOwner = item.getSellerUuid().equals(player.getUniqueId());
        // 判断是否是当前竞价者
        boolean isCurrentBidder = item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId());

        // Shift+右键点击下架（仅物品主人可操作）
        if (event.isShiftClick() && event.isRightClick() && isOwner) {
            // 物品主人Shift+右键快速下架
            handleCancelAuction(player, item);
            return;
        }
        
        // 左键点击参与竞价
        if (event.isLeftClick()) {
            // 如果是物品主人，提示不能竞拍自己的物品
            if (isOwner) {
                player.sendMessage("§c你不能竞拍自己的物品!");
                return;
            }
            
            // 检查权限
            if (!player.hasPermission("globalshop.bid")) {
                player.sendMessage("§c你没有权限参与竞价!");
                return;
            }
            
            // 如果玩家已经是当前最高出价者，则提示并打开竞价界面
            if (isCurrentBidder) {
                player.sendMessage("§e你已经是当前最高出价者，可以继续加价!");
            }
            
            // 打开竞价界面
            plugin.getGuiManager().openBidMenu(player, item);
        }
        // 右键点击直接购买
        else if (event.isRightClick() && !event.isShiftClick()) {
            // 如果是物品主人，提示不能购买自己的物品
            if (isOwner) {
                player.sendMessage("§c你不能购买自己的物品!");
                return;
            }
            
            // 检查是否有一口价
            if (!item.hasBuyNowPrice()) {
                player.sendMessage("§c该物品没有设置一口价，不能直接购买!");
                return;
            }
            
            // 检查权限
            if (!player.hasPermission("globalshop.buy")) {
                player.sendMessage("§c你没有权限购买!");
                return;
            }
            
            // 打开确认购买界面
            plugin.getGuiManager().openConfirmBuyMenu(player, item);
        }
    }

    // 处理确认购买界面点击
    private void handleConfirmBuyClick(InventoryClickEvent event, Player player) {
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
                player.sendMessage("§c物品不存在或已被购买!");
                player.closeInventory();
                return;
            }
            
            // 检查物品状态
            if (!item.isActive() || item.isExpired()) {
                player.sendMessage("§c物品已过期或已被购买!");
                player.closeInventory();
                return;
            }
            
            // 检查物品是否可以一口价购买
            if (!item.hasBuyNowPrice()) {
                player.sendMessage("§c该物品不支持一口价购买!");
                player.closeInventory();
                return;
            }
            
            // 检查是否为卖家本人
            if (item.getSellerUuid().equals(player.getUniqueId())) {
                player.sendMessage("§c你不能购买自己的物品!");
                player.closeInventory();
                return;
            }
            
            // 买家不需要支付税费，直接使用物品价格
            double price = item.getBuyNowPrice();
            
            // 检查玩家是否有足够的钱
            if (!plugin.getEconomyManager().hasEnough(player, price, item.getCurrencyType())) {
                player.sendMessage("§c你没有足够的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "购买此物品!");
                player.closeInventory();
                return;
            }
            
            // 检查玩家背包是否有足够空间
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§c你的背包已满，请先清理背包再购买物品!");
                player.closeInventory();
                return;
            }
            
            // 扣除玩家的钱
            if (!plugin.getEconomyManager().takeMoney(player, price, item.getCurrencyType())) {
                player.sendMessage("§c扣款失败，请重试!");
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
                    previousBidder.sendMessage("§e物品ID: " + item.getId() + " 已被一口价购买");
                    previousBidder.sendMessage("§e你的竞价 " + plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()) + " 已退还");
                    // 显示余额信息
                    double bidderBalance = plugin.getEconomyManager().getBalance(previousBidder, item.getCurrencyType());
                    previousBidder.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                                              plugin.getEconomyManager().formatAmount(bidderBalance, item.getCurrencyType()));
                }
                
            }
            
            // 更新物品状态
            item.setStatus("SOLD");
            // 设置买家UUID和名称
            item.setCurrentBidder(player.getUniqueId());
            item.setCurrentBidderName(player.getName());
            
            // 设置售出时间为当前时间
            long soldTime = System.currentTimeMillis();
            item.setSoldTime(soldTime);
            
            // 更新物品到数据库
            plugin.getDatabaseManager().updateAuctionItem(item);
            

            
            // 将物品给予买家
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item.getItem());
            if (!leftover.isEmpty()) {
                // 如果物品无法完全放入背包，保存到待领取列表
                for (ItemStack stack : leftover.values()) {
                    plugin.getDatabaseManager().storePendingItem(player.getUniqueId(), stack, "物品购买，但背包已满");
                }
                player.sendMessage("§e你的背包已满，部分物品已保存到待领取列表，使用 /ah collect 领取");
            }
            
            // 给卖家钱（减去手续费）
            double fee = plugin.getEconomyManager().calculateFee(item.getBuyNowPrice(), item.getCurrencyType());
            double sellerReceives = item.getBuyNowPrice() - fee;
            plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerReceives, item.getCurrencyType());
            
            // 发送购买成功消息
            player.sendMessage("§a购买成功!");
            player.sendMessage("§e物品ID: §f" + item.getId());
            player.sendMessage("§e价格: §f" + plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
            // 显示余额信息
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                             plugin.getEconomyManager().formatAmount(balance, item.getCurrencyType()));
            
            // 卖家在线则发送通知
            Player seller = Bukkit.getPlayer(item.getSellerUuid());
            if (seller != null && seller.isOnline()) {
                seller.sendMessage("§a你的物品已被购买!");
                seller.sendMessage("§e物品ID: §f" + item.getId());
                seller.sendMessage("§e出售价格: §f" + plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                seller.sendMessage("§e手续费: §f" + plugin.getEconomyManager().formatAmount(fee, item.getCurrencyType()));
                seller.sendMessage("§e实际获得: §f" + plugin.getEconomyManager().formatAmount(sellerReceives, item.getCurrencyType()));
                // 显示卖家余额信息
                double sellerBalance = plugin.getEconomyManager().getBalance(seller, item.getCurrencyType());
                seller.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                                  plugin.getEconomyManager().formatAmount(sellerBalance, item.getCurrencyType()));
                seller.sendMessage("§e买家: §f" + player.getName());
            }
            
            // 关闭界面
            player.closeInventory();
        } else if (event.getRawSlot() > 13) { // 取消按钮 (所有右侧红色按钮)
            // 关闭界面
            player.closeInventory();
            player.sendMessage("§c已取消购买");
        }
    }

    // 处理竞价界面点击
    private void handleBidMenuClick(InventoryClickEvent event, Player player) {
        // 获取点击的物品
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        
        // 获取拍卖物品ID
        if (!player.hasMetadata("auction_bid_id")) {
            player.closeInventory();
            return;
        }
        
        int itemId = player.getMetadata("auction_bid_id").get(0).asInt();
        
        // 获取拍卖物品
        AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
        if (item == null) {
            player.sendMessage("§c物品不存在或已被购买!");
            player.closeInventory();
            return;
        }
        
        // 检查物品状态
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage("§c物品已过期或已被购买!");
            player.closeInventory();
            return;
        }
        
        // 检查是否为卖家本人
        if (item.getSellerUuid().equals(player.getUniqueId())) {
            player.sendMessage("§c你不能对自己的物品出价!");
            player.closeInventory();
            return;
        }
        
        // 获取货币类型和最低加价金额
        String currencyType = item.getCurrencyType();
        double minBidIncrease = plugin.getEconomyManager().calculateMinBid(item.getCurrentPrice(), currencyType);
        
        // 根据点击的按钮处理不同的加价
        if (event.getRawSlot() >= 10 && event.getRawSlot() <= 16) {
            // 确认竞价按钮特殊处理
            if (event.getRawSlot() == 15) {
                // 确认当前竞价并关闭界面
                if (player.hasMetadata("auction_bid_amount")) {
                    double currentBid = player.getMetadata("auction_bid_amount").get(0).asDouble();
                    String currType = item.getCurrencyType();
                    double balance = plugin.getEconomyManager().getBalance(player, currType);
                    
                    player.sendMessage("§a已确认当前竞价金额: §f" + plugin.getEconomyManager().formatAmount(currentBid, currType));
                    player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(currType) + "余额: §f" + 
                                      plugin.getEconomyManager().formatAmount(balance, currType));
                    
                    // 标记为确认操作，防止关闭界面时被认为是取消操作
                    player.setMetadata("auction_bid_confirmed", new FixedMetadataValue(plugin, true));
                    player.closeInventory();
                    return;
                } else {
                    player.sendMessage("§c确认失败: 找不到当前竞价金额");
                    return;
                }
            }
            
            // 计算新的出价金额
            double newBid = item.getCurrentPrice();
            
            switch (event.getRawSlot()) {
                case 10 -> newBid += minBidIncrease; // 最小加价
                case 11 -> newBid += minBidIncrease * 2; // 2倍最小加价
                case 12 -> newBid += minBidIncrease * 5; // 5倍最小加价
                case 13 -> newBid += minBidIncrease * 10; // 10倍最小加价
                case 14 -> newBid = item.getCurrentPrice() * 1.5; // 1.5倍当前价
                case 15 -> newBid = item.getCurrentPrice() * 2.0; // 2倍当前价
                case 16 -> { // 一口价
                    if (item.hasBuyNowPrice()) {
                        newBid = item.getBuyNowPrice();
                    } else {
                        player.sendMessage("§c该物品没有设置一口价!");
                        return;
                    }
                }
            }
            
            // 检查新出价是否大于最低加价要求
            if (newBid < item.getCurrentPrice() + minBidIncrease) {
                player.sendMessage("§c出价必须至少比当前价格高 " + plugin.getEconomyManager().formatAmount(minBidIncrease, currencyType) + "!");
                return;
            }
            
            // 检查是否达到一口价
            if (item.hasBuyNowPrice() && newBid >= item.getBuyNowPrice()) {
                newBid = item.getBuyNowPrice();
            }
            
            // 买家不需要支付税费，直接使用竞价金额
            double bidPrice = newBid;
            
            // 检查玩家是否有足够的钱
            if (!plugin.getEconomyManager().hasEnough(player, bidPrice, currencyType)) {
                player.sendMessage("§c你没有足够的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "进行竞价!");
                return;
            }
            
            // 如果玩家是当前的最高出价者，只需要支付差价
            double amountToDeduct = bidPrice;
            if (item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId())) {
                double previousBid = item.getCurrentPrice();
                amountToDeduct = bidPrice - previousBid;
            }
            
            // 扣除玩家的钱
            if (!plugin.getEconomyManager().takeMoney(player, amountToDeduct, currencyType)) {
                player.sendMessage("§c扣款失败，请重试!");
                return;
            }
            
            // 如果之前有其他出价者，退还他们的钱
            if (item.getCurrentBidder() != null && !item.getCurrentBidder().equals(player.getUniqueId())) {
                double previousBid = item.getCurrentPrice();
                plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getCurrentBidder()), previousBid, currencyType);
                
                // 如果之前的出价者在线，发送通知
                Player previousBidder = Bukkit.getPlayer(item.getCurrentBidder());
                if (previousBidder != null && previousBidder.isOnline()) {
                    previousBidder.sendMessage("§e你的出价已被超越! 物品ID: " + item.getId());
                    previousBidder.sendMessage("§e新的出价: " + plugin.getEconomyManager().formatAmount(newBid, currencyType) + " 由 " + player.getName() + " 提供");
                    previousBidder.sendMessage("§e你的出价 " + plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), currencyType) + " 已退还");
                    // 显示余额信息
                    double bidderBalance = plugin.getEconomyManager().getBalance(previousBidder, currencyType);
                    previousBidder.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "余额: §f" + 
                                              plugin.getEconomyManager().formatAmount(bidderBalance, currencyType));
                }
            }
            
            // 更新物品信息
            item.setCurrentPrice(newBid);
            item.setCurrentBidder(player.getUniqueId());
            
            // 如果达到一口价，直接完成拍卖
            if (item.hasBuyNowPrice() && newBid >= item.getBuyNowPrice()) {
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
                    player.sendMessage("§a购买成功! 物品已放入你的背包。");
                } else {
                    // 如果背包已满，将物品添加到邮箱
                    addToMailbox(item, player.getUniqueId(), "BUY_NOW");
                    player.sendMessage("§a购买成功! §e但你的背包已满，物品已存入物品邮箱，请使用 /ah my 查看。");
                }
                
                // 给卖家钱（减去手续费）
                double fee = plugin.getEconomyManager().calculateFee(newBid, currencyType);
                double sellerReceives = newBid - fee;
                plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerReceives, currencyType);
                
                // 发送购买成功消息
                player.sendMessage("§e物品ID: §f" + item.getId());
                player.sendMessage("§e价格: §f" + plugin.getEconomyManager().formatAmount(newBid, currencyType));
                // 买家不需要支付税费，移除税费相关信息
                
                // 卖家在线则发送通知
                Player seller = Bukkit.getPlayer(item.getSellerUuid());
                if (seller != null && seller.isOnline()) {
                    seller.sendMessage("§a你的物品已被购买!");
                    seller.sendMessage("§e物品ID: §f" + item.getId());
                    seller.sendMessage("§e出售价格: §f" + plugin.getEconomyManager().formatAmount(newBid, currencyType));
                    seller.sendMessage("§e手续费: §f" + plugin.getEconomyManager().formatAmount(fee, currencyType));
                    seller.sendMessage("§e实际获得: §f" + plugin.getEconomyManager().formatAmount(sellerReceives, currencyType));
                    seller.sendMessage("§e买家: §f" + player.getName());
                    // 显示卖家余额信息
                    double sellerBalance = plugin.getEconomyManager().getBalance(seller, currencyType);
                    seller.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "余额: §f" + 
                                      plugin.getEconomyManager().formatAmount(sellerBalance, currencyType));
                }
                
                // 关闭界面
                player.closeInventory();
            } else {
                // 竞价成功但未达到一口价，更新数据库并更新界面
                plugin.getDatabaseManager().updateAuctionItem(item);
                
                // 更新玩家元数据中的竞价金额和标记为加价操作进行中
                player.setMetadata("auction_bid_amount", new FixedMetadataValue(plugin, newBid));
                player.setMetadata("auction_bid_in_progress", new FixedMetadataValue(plugin, true));
                player.setMetadata("auction_bid_confirmed", new FixedMetadataValue(plugin, true));
                
                // 发送竞价成功消息
                player.sendMessage("§a竞价成功!");
                player.sendMessage("§e物品ID: §f" + item.getId());
                player.sendMessage("§e你的出价: §f" + plugin.getEconomyManager().formatAmount(newBid, currencyType));
                // 显示余额信息
                double balance = plugin.getEconomyManager().getBalance(player, currencyType);
                player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(currencyType) + "余额: §f" + 
                                  plugin.getEconomyManager().formatAmount(balance, currencyType));
                
                // 保存需要在Lambda中使用的变量的副本，确保它们是effectively final
                final double finalNewBid = newBid;
                final String finalCurrencyType = currencyType;
                final AuctionItem finalItem = item;
                
                // 使用调度器在下一个游戏刻更新界面，确保元数据已被设置
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // 直接更新竞价界面上的信息，而不关闭界面
                    Inventory inv = event.getInventory();
                    
                    // 更新当前竞价金额显示
                    ItemStack bidAmount = inv.getItem(4);
                    if (bidAmount != null && bidAmount.getItemMeta() != null) {
                        ItemMeta bidAmountMeta = bidAmount.getItemMeta();
                        List<String> bidAmountLore = new ArrayList<>();
                        bidAmountLore.add("§7物品: §f" + ChatUtils.getItemName(finalItem.getItem()));
                        bidAmountLore.add("§7原始价格: §f" + plugin.getEconomyManager().formatAmount(finalItem.getStartPrice(), finalCurrencyType));
                        bidAmountLore.add("§7当前竞价: §f" + plugin.getEconomyManager().formatAmount(finalNewBid, finalCurrencyType));
                        bidAmountLore.add("§7加价幅度: §f+5%");
                        bidAmountMeta.setLore(bidAmountLore);
                        bidAmount.setItemMeta(bidAmountMeta);
                        inv.setItem(4, bidAmount);
                    }
                    
                    // 更新确认按钮的Lore
                    ItemStack confirmButton = inv.getItem(15);
                    if (confirmButton != null && confirmButton.getItemMeta() != null) {
                        ItemMeta confirmMeta = confirmButton.getItemMeta();
                        List<String> confirmLore = new ArrayList<>();
                        confirmLore.add("§7点击确认当前竞价金额");
                        confirmLore.add("§7当前竞价: §f" + plugin.getEconomyManager().formatAmount(finalNewBid, finalCurrencyType));
                        confirmMeta.setLore(confirmLore);
                        confirmButton.setItemMeta(confirmMeta);
                        inv.setItem(15, confirmButton);
                    }
                });
            }
        } else if (event.getRawSlot() == 31) { // 关闭按钮
            player.closeInventory();
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
                            // 移除拍卖相关的LORE（包括邮箱标记）
                            lore.removeIf(line -> 
                                line.contains("起拍价") || 
                                line.contains("时间") || 
                                line.contains("拍卖期间") || 
                                line.contains("系统自动下架") ||
                                line.contains("✉") ||
                                line.contains("§8-----------------"));
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
                                player.sendMessage(ChatColor.GREEN + "已成功领取物品，物品已放入你的背包。");
                            }
                        } else {
                            // 背包已满，直接提示
                            player.sendMessage(ChatColor.YELLOW + "背包已满，无法领取物品。请清理背包后再试。");
                        }
                        
                        // 刷新界面
                        plugin.getGuiManager().openMyMailboxMenu(player, currentPage);
                        return; // 添加return语句，防止继续执行下面的代码
                    }
                    
                    // 检查是否是活跃物品且已经有出价者
                    if ("active".equals(filter) && item.getCurrentBidder() != null) {
                        // 如果物品已有人出价，无法取消
                        player.sendMessage(ChatColor.RED + "该物品已有人出价，无法取消拍卖！");
                        return;
                    }
                    handleCancelAuction(player, item);
                } else if ("active".equals(filter)) { // 只在活跃商品界面显示详细信息
                    // 左键点击，查看详细信息
                    player.sendMessage(ChatColor.GOLD + "===== 拍卖物品详情 =====");
                    player.sendMessage(ChatColor.YELLOW + "物品: " + ChatColor.WHITE + ChatUtils.getItemName(item.getItem()));
                    player.sendMessage(ChatColor.YELLOW + "起拍价: " + ChatColor.WHITE + 
                            plugin.getEconomyManager().formatAmount(item.getStartPrice(), item.getCurrencyType()));
                    player.sendMessage(ChatColor.YELLOW + "当前价格: " + ChatColor.WHITE + 
                            plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
                            
                    if (item.hasBuyNowPrice()) {
                        player.sendMessage(ChatColor.YELLOW + "一口价: " + ChatColor.WHITE + 
                                plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
                    }
                            
                    if (item.getCurrentBidder() != null) {
                        player.sendMessage(ChatColor.YELLOW + "当前出价者: " + ChatColor.WHITE + 
                                Bukkit.getOfflinePlayer(item.getCurrentBidder()).getName());
                    }
                            
                    player.sendMessage(ChatColor.YELLOW + "剩余时间: " + ChatColor.WHITE + item.getFormattedRemainingTime());
                }
            }
            return;
        }
        
        // 处理上一页按钮
        if (slot == 45 && currentPage > 1) {
            if ("active".equals(filter)) {
                plugin.getGuiManager().openMyAuctionsMenu(player, currentPage - 1);
            } else if ("sold".equals(filter)) {
                plugin.getGuiManager().openMySoldAuctionsMenu(player, currentPage - 1);
            } else if ("mailbox".equals(filter)) {
                plugin.getGuiManager().openMyMailboxMenu(player, currentPage - 1);
            }
            return;
        }
        
        // 处理筛选按钮
        if (slot == 47) {
            plugin.getGuiManager().openMySoldAuctionsMenu(player, 1);
            return;
        }
        
        // 处理已过期按钮
        if (slot == 51) {
            plugin.getGuiManager().openMyMailboxMenu(player, 1);
            return;
        }
        
        // 处理返回主菜单按钮
        if (slot == 49) {
            plugin.getGuiManager().openMainMenu(player);
            return;
        }
        
        // 处理下一页按钮
        if (slot == 53) {
            // 获取玩家拍卖物品
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
            } else if ("mailbox".equals(filter)) {
                filteredItems = items.stream()
                        .filter(item -> "EXPIRED".equals(item.getStatus()) && item.getCurrentBidder() == null)
                        .toList();
            } else {
                filteredItems = items;
            }
            
            // 计算总页数
            int itemsPerPage = 36; // 每页36个物品
            int totalPages = (int) Math.ceil((double) filteredItems.size() / itemsPerPage);
            
            if (currentPage < totalPages) {
                if ("active".equals(filter)) {
                    plugin.getGuiManager().openMyAuctionsMenu(player, currentPage + 1);
                } else if ("sold".equals(filter)) {
                    plugin.getGuiManager().openMySoldAuctionsMenu(player, currentPage + 1);
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
                player.sendMessage(ChatColor.RED + "该物品已有人出价，无法取消拍卖！");
                return;
            }
            
            // 更改物品状态为已取消
            item.setStatus("CANCELLED");
            boolean updated = plugin.getDatabaseManager().updateAuctionItem(item);
            
            if (updated) {
                // 物品返还给玩家
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item.getItem());
                
                if (leftover.isEmpty()) {
                    player.sendMessage(ChatColor.GREEN + "已成功取消拍卖，物品已返还到你的背包。");
                } else {
                    // 物品无法完全添加到背包，存入待领取物品列表
                    plugin.getDatabaseManager().storePendingItem(player.getUniqueId(), item.getItem(), "拍卖取消，背包已满");
                    player.sendMessage(ChatColor.YELLOW + "已成功取消拍卖，但你的背包已满。物品已存入待领取列表，使用 /auction collect 领取。");
                }
                
                // 刷新界面
                plugin.getGuiManager().openMyAuctionsMenu(player, 1);
            } else {
                player.sendMessage(ChatColor.RED + "取消拍卖失败，请稍后再试。");
            }
        }
    }

    // 处理聊天消息（用于接收竞价金额和搜索关键词）
    @EventHandler
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // 处理搜索输入
        if (player.hasMetadata("auction_search_input")) {
            event.setCancelled(true);
            
            // 获取搜索关键词
            String keyword = event.getMessage();
            
            // 如果输入"取消"，则返回主菜单
            if (keyword.equals("取消")) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.removeMetadata("auction_search_input", plugin);
                    plugin.getGuiManager().openMainMenu(player);
                    player.sendMessage("§a已取消搜索");
                });
                return;
            }
            
            // 打开搜索结果界面
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.removeMetadata("auction_search_input", plugin);
                plugin.getGuiManager().openSearchResultMenu(player, keyword, 1);
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
                    player.sendMessage("§c使用方法: /bid <物品ID> 或 /竞价 <物品ID>");
                    return;
                }
                
                int itemId;
                try {
                    itemId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c物品ID必须是一个数字");
                    return;
                }
                
                // 获取拍卖物品
                AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
                if (item == null || !item.isActive() || item.isExpired()) {
                    player.sendMessage("§c找不到有效的拍卖物品: " + itemId);
                    return;
                }
                
                // 检查权限
                if (!player.hasPermission("globalshop.bid")) {
                    player.sendMessage("§c你没有权限参与竞价!");
                    return;
                }
                
                // 检查是否是自己的物品
                if (item.getSellerUuid().equals(player.getUniqueId())) {
                    player.sendMessage("§c你不能竞价自己的物品!");
                    return;
                }
                
                // 检查是否已是当前最高出价者
                if (item.getCurrentBidder() != null && item.getCurrentBidder().equals(player.getUniqueId())) {
                    player.sendMessage("§c你已经是当前最高出价者!");
                    return;
                }
                
                // 打开竞价界面
                player.sendMessage("§a正在打开竞价界面...");
                plugin.getGuiManager().openBidMenu(player, item);
                
            } catch (Exception e) {
                player.sendMessage("§c竞价命令处理出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleBuyNow(Player player, AuctionItem item) {
        // 检查是否是物品主人
        if (item.getSellerUuid().equals(player.getUniqueId())) {
            player.sendMessage("§c你不能购买自己的物品！");
            return;
        }
        
        // 检查物品是否已售出或过期
        if (!item.isActive() || item.isExpired()) {
            player.sendMessage("§c该物品已不可购买！");
            return;
        }
        
        // 买家不需要支付税费，直接使用物品价格
        double price = item.getBuyNowPrice();
        
        // 检查玩家是否有足够的钱
        if (!plugin.getEconomyManager().hasEnough(player, price, item.getCurrencyType())) {
            player.sendMessage("§c你没有足够的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "！");
            player.sendMessage("§c需要: " + plugin.getEconomyManager().formatAmount(price, item.getCurrencyType()));
            return;
        }
        
        // 检查玩家背包是否有足够空间
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§c你的背包已满，请先清理背包再购买物品!");
            return;
        }
        
        // 扣除玩家的钱
        plugin.getEconomyManager().takeMoney(player, price, item.getCurrencyType());
        
        // 给卖家钱（扣除手续费）
        double sellerAmount = price - plugin.getEconomyManager().calculateFee(price, item.getCurrencyType());
        plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(item.getSellerUuid()), sellerAmount, item.getCurrencyType());
        
        // 更新物品状态
        item.setStatus("SOLD");
        item.setCurrentBidder(player.getUniqueId());
        item.setCurrentBidderName(player.getName());
        item.setCurrentPrice(price);
        item.setSoldTime(System.currentTimeMillis());
        plugin.getDatabaseManager().updateAuctionItem(item);
        
        // 尝试直接放入背包，如果背包满了则放入邮箱
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item.getItem());
            player.sendMessage("§a你已成功购买物品！");
            player.sendMessage("§e物品: " + ChatUtils.getItemName(item.getItem()));
            player.sendMessage("§e价格: " + plugin.getEconomyManager().formatAmount(price, item.getCurrencyType()));
            // 显示玩家余额
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                             plugin.getEconomyManager().formatAmount(balance, item.getCurrencyType()));
        } else {
            // 背包已满，放入邮箱
            addToMailbox(item, player.getUniqueId(), "BUY_NOW");
            player.sendMessage("§c你的背包已满，物品已放入物品邮箱！");
            player.sendMessage("§e物品: " + ChatUtils.getItemName(item.getItem()));
            player.sendMessage("§e价格: " + plugin.getEconomyManager().formatAmount(price, item.getCurrencyType()));
            // 显示玩家余额
            double balance = plugin.getEconomyManager().getBalance(player, item.getCurrencyType());
            player.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                             plugin.getEconomyManager().formatAmount(balance, item.getCurrencyType()));
            player.sendMessage("§e使用 /auction my 进入拍卖系统，点击\"物品邮箱\"领取");
        }
        
        // 通知卖家
        Player seller = Bukkit.getPlayer(item.getSellerUuid());
        if (seller != null && seller.isOnline()) {
            seller.sendMessage("§a你的物品已被购买！");
            seller.sendMessage("§e物品: " + ChatUtils.getItemName(item.getItem()));
            seller.sendMessage("§e价格: " + plugin.getEconomyManager().formatAmount(price, item.getCurrencyType()));
            seller.sendMessage("§e收入: " + plugin.getEconomyManager().formatAmount(sellerAmount, item.getCurrencyType()));
            // 显示卖家余额
            double sellerBalance = plugin.getEconomyManager().getBalance(seller, item.getCurrencyType());
            seller.sendMessage("§a你的" + plugin.getEconomyManager().getCurrencyName(item.getCurrencyType()) + "余额: §f" + 
                             plugin.getEconomyManager().formatAmount(sellerBalance, item.getCurrencyType()));
            seller.sendMessage("§e买家: " + player.getName());
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
        lore.add("§8-----------------");
        if ("AUCTION_WON".equals(reason)) {
            lore.add("§6✉ 竞拍获得的物品");
            lore.add("§7成交价: " + plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
            lore.add("§7获得时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("AUCTION_EXPIRED".equals(reason)) {
            lore.add("§6✉ 过期未售出的物品");
            lore.add("§7下架时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("BUY_NOW".equals(reason)) {
            lore.add("§6✉ 一口价购买的物品");
            lore.add("§7成交价: " + plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType()));
            lore.add("§7购买时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        }
        
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        
        // 存储到数据库
        plugin.getDatabaseManager().storePendingItem(ownerUuid, itemStack);
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
} 