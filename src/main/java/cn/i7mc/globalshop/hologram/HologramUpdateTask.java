package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.World;
<<<<<<< HEAD
import org.bukkit.entity.Display;
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全息更新任务
 * 负责定时更新全息展示内容
 */
public class HologramUpdateTask extends BukkitRunnable {
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    private final GlobalShop plugin;
    private final HologramDisplayManager displayManager;
    private final ItemDisplayManager itemManager;
    private final TextDisplayManager textManager;
    private final AuctionHistoryManager historyManager;
    private final HologramConfigManager configManager;
<<<<<<< HEAD

    private final Map<UUID, Location> hologramLocations;
    private boolean forceUpdate = false;

=======
    
    private final Map<UUID, Location> hologramLocations;
    private boolean forceUpdate = false;
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 构造函数
     * @param plugin 插件实例
     * @param displayManager 显示管理器
     * @param itemManager 物品显示管理器
     * @param textManager 文本显示管理器
     * @param historyManager 历史记录管理器
     * @param configManager 配置管理器
     */
    public HologramUpdateTask(GlobalShop plugin, HologramDisplayManager displayManager,
                            ItemDisplayManager itemManager, TextDisplayManager textManager,
                            AuctionHistoryManager historyManager, HologramConfigManager configManager) {
        this.plugin = plugin;
        this.displayManager = displayManager;
        this.itemManager = itemManager;
        this.textManager = textManager;
        this.historyManager = historyManager;
        this.configManager = configManager;
        this.hologramLocations = new ConcurrentHashMap<>();
<<<<<<< HEAD

        // 在构造函数中设置强制更新标志，确保第一次运行时立即更新
        this.forceUpdate = true;
    }

=======
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 添加全息显示位置
     * @param hologramId 全息ID
     * @param location 位置
     */
    public void addHologramLocation(UUID hologramId, Location location) {
        hologramLocations.put(hologramId, location.clone());
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 移除全息显示位置
     * @param hologramId 全息ID
     */
    public void removeHologramLocation(UUID hologramId) {
        hologramLocations.remove(hologramId);
        displayManager.removeHologram(hologramId);
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 强制更新全息显示
     */
    public void forceUpdate() {
        this.forceUpdate = true;
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 更新任务配置
     * 从配置管理器重新加载配置值
     */
    public void updateTaskConfig() {
        // 获取新的更新间隔
        int newInterval = configManager.getUpdateInterval();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 重新排程本任务（如果间隔有变化）
        if (this.getTaskId() > 0) {
            // 只取消当前任务，不要尝试重新调度相同的BukkitRunnable实例
            this.cancel();
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 通知主类需要重新调度任务
            plugin.rescheduleHologramTask(newInterval);
        }
    }
<<<<<<< HEAD

    @Override
    public void run() {
        // 验证全息实体的有效性
        displayManager.validateHolograms();

        // 清理非法Display实体
        cleanupIllegalDisplayEntities();

=======
    
    @Override
    public void run() {
        // 首先清理非法TextDisplay实体
        cleanupIllegalTextDisplays();
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 首先从HologramCommandManager同步位置信息
        for (Map.Entry<String, UUID> entry : plugin.getHologramCommandManager().getHologramIds().entrySet()) {
            UUID hologramId = entry.getValue();
            Location location = plugin.getHologramCommandManager().getHologramLocations().get(entry.getKey());
            if (location != null && !hologramLocations.containsKey(hologramId)) {
                hologramLocations.put(hologramId, location.clone());
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果没有全息显示，跳过更新
        if (hologramLocations.isEmpty()) {
            return;
        }
<<<<<<< HEAD

        // 获取最新历史记录
        List<AuctionHistoryManager.AuctionHistoryEvent> recentEvents =
                historyManager.getRecentEvents(configManager.getDisplayRows());

=======
        
        // 获取最新历史记录
        List<AuctionHistoryManager.AuctionHistoryEvent> recentEvents = 
                historyManager.getRecentEvents(configManager.getDisplayRows());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果没有历史记录且不强制更新，跳过更新
        if (recentEvents.isEmpty() && !forceUpdate) {
            return;
        }
<<<<<<< HEAD

        // 输出调试信息
        if (plugin.getConfigManager().isDebug()) {
            plugin.getDebugMessageManager().debug("正在更新全息显示，位置数量: " + hologramLocations.size() +
                                                ", 历史记录数量: " + recentEvents.size() +
                                                ", 强制更新: " + forceUpdate);
        }

        // 重置强制更新标志
        forceUpdate = false;

=======
        
        // 输出调试信息
        
        // 重置强制更新标志
        forceUpdate = false;
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 更新每一个全息显示
        for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
            UUID hologramId = entry.getKey();
            Location baseLocation = entry.getValue().clone();
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            try {
                // 检查位置有效性
                if (baseLocation.getWorld() == null) {
                    continue;
                }
<<<<<<< HEAD


                // 清除现有全息实体
                displayManager.clearHologram(hologramId);

                // 创建标题
                Location titleLoc = baseLocation.clone().add(0, 0.3, 0);
                textManager.createTextDisplay(titleLoc, configManager.getMessage("title"), hologramId, 1.2f, null, null, "title");

=======
                

                // 清除现有全息实体
                displayManager.clearHologram(hologramId);
                
                // 创建标题
                Location titleLoc = baseLocation.clone().add(0, 0.3, 0);
                textManager.createTextDisplay(titleLoc, configManager.getMessage("title"), hologramId, 1.2f, null, null, "title");
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 如果没有历史记录，显示提示信息
                if (recentEvents.isEmpty()) {
                    Location textLoc = baseLocation.clone().add(0, -0.5, 0);
                    textManager.createTextDisplay(textLoc, configManager.getMessage("no-history"), hologramId, 1.0f, "#FFFFFF", "#33000000", "content");
                    continue;
                }
<<<<<<< HEAD

                // 获取显示位置配置
                float lineSpacing = configManager.getLineSpacing();

=======
                
                // 获取显示位置配置
                float lineSpacing = configManager.getLineSpacing();
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 添加表格列标题行
                Location headerLoc = baseLocation.clone().add(0, -0.5, 0);
                String headerText = configManager.getMessage("header");
                TextDisplay header = textManager.createTextDisplay(headerLoc, headerText, hologramId, 0.9f, null, null, "content");
<<<<<<< HEAD

                // 创建历史记录显示
                for (int i = 0; i < recentEvents.size(); i++) {
                    AuctionHistoryManager.AuctionHistoryEvent event = recentEvents.get(i);

                    // 计算行位置（从表头下方开始）
                    Location rowLocation = baseLocation.clone();
                    rowLocation.add(0, -((i + 1) * lineSpacing + 0.5), 0); // 从表头下方开始

=======
                
                // 创建历史记录显示
                for (int i = 0; i < recentEvents.size(); i++) {
                    AuctionHistoryManager.AuctionHistoryEvent event = recentEvents.get(i);
                    
                    // 计算行位置（从表头下方开始）
                    Location rowLocation = baseLocation.clone();
                    rowLocation.add(0, -((i + 1) * lineSpacing + 0.5), 0); // 从表头下方开始
                    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                    // 获取格式化文本
                    String text = formatEventText(event);
                    TextDisplay textDisplay = textManager.createTextDisplay(rowLocation, text, hologramId, 0.9f, null, null, "content");
                }
<<<<<<< HEAD

            } catch (Exception e) {
                e.printStackTrace();

=======
                
            } catch (Exception e) {
                e.printStackTrace();
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 异常情况下，仍然尝试显示错误信息
                try {
                    displayManager.clearHologram(hologramId);
                    Location errorLoc = baseLocation.clone();
                    TextDisplay errorDisplay = textManager.createTextDisplay(errorLoc, configManager.getMessage("load-error"), hologramId, 1.0f, null, null, "content");
                } catch (Exception ex) {
                }
            }
        }
<<<<<<< HEAD

        // 检查全息显示是否有附近玩家，如果没有，暂时移除实体以节省资源
        optimizeHolograms();
    }

=======
        
        // 检查全息显示是否有附近玩家，如果没有，暂时移除实体以节省资源
        optimizeHolograms();
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 格式化事件文本显示
     */
    private String formatEventText(AuctionHistoryManager.AuctionHistoryEvent event) {
        StringBuilder text = new StringBuilder();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 物品名称，最多显示10个字符
        String itemName;
        ItemStack original = event.getItem().getItem();
        ItemMeta meta = original.getItemMeta();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 尝试获取物品的自定义显示名称
        if (meta != null && meta.hasDisplayName()) {
            // 保留原有的颜色代码和样式
            itemName = meta.getDisplayName();
        } else {
            // 只有在中文语言环境时才进行原版物品名称的中文翻译
            if (plugin.getConfigManager().isChineseLanguage()) {
                // 使用与GuiManager相同的逻辑获取中文名称
                String chineseName = plugin.getLanguageManager().getChineseName(original.getType());
<<<<<<< HEAD
                itemName = (chineseName != null && !chineseName.isEmpty()) ?
=======
                itemName = (chineseName != null && !chineseName.isEmpty()) ? 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                        "§e" + chineseName : // 只有非自定义名称才使用黄色
                        "§e" + ChatUtils.getItemName(original);
            } else {
                // 非中文语言环境下使用原版物品名称
                itemName = "§e" + ChatUtils.getItemName(original);
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 如果名称超过10个字符，截断并添加省略号
        if (ChatColor.stripColor(itemName).length() > 10) {
            // 保留颜色代码，但截断实际文本
            String colorCodes = getColorCodesBeforeIndex(itemName, 10);
            String plainText = ChatColor.stripColor(itemName);
            itemName = colorCodes + plainText.substring(0, 9) + "…";
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 物品数量（如果大于1）
        int amount = original.getAmount();
        if (amount > 1) {
            itemName += " §fx" + amount;
        }
        text.append(itemName);
<<<<<<< HEAD

        // 卖家信息
        String sellerName = event.getSellerName();
        text.append(" §7| §a").append(sellerName);

=======
        
        // 卖家信息
        String sellerName = event.getSellerName();
        text.append(" §7| §a").append(sellerName);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 事件类型
        String eventTypeText = "";
        switch (event.getEventType()) {
            case LIST:
                eventTypeText = configManager.getEventTypeText("list");
                break;
            case BID:
                eventTypeText = configManager.getEventTypeText("bid");
                break;
            case BUY:
                eventTypeText = configManager.getEventTypeText("buy");
                break;
            case EXPIRED:
                eventTypeText = configManager.getEventTypeText("expired");
                break;
            case CANCELLED:
                eventTypeText = configManager.getEventTypeText("cancelled");
                break;
        }
        text.append(" §7| §f").append(eventTypeText);
<<<<<<< HEAD

        // 价格信息
        String priceText = "-";
        if (event.getEventType() == AuctionHistoryManager.AuctionEventType.BID ||
            event.getEventType() == AuctionHistoryManager.AuctionEventType.BUY) {
            // 货币类型
            String currencyType = event.getItem().getCurrencyType();
            String currencyName = currencyType.equals("VAULT") ?
                                 configManager.getCurrencyName("vault") : configManager.getCurrencyName("points");
            String currencySymbol = currencyType.equals("VAULT") ?
                                  plugin.getConfigManager().getVaultSymbol() : plugin.getConfigManager().getPointsSymbol();
            double price = event.getEventType() == AuctionHistoryManager.AuctionEventType.BID ?
                          event.getItem().getCurrentPrice() : event.getItem().getBuyNowPrice();

            priceText = String.format("%.2f %s%s", price, currencySymbol, currencyName);
        } else if (event.getEventType() == AuctionHistoryManager.AuctionEventType.LIST) {
            // 上架事件显示起拍价和一口价（如果有）
            String currencyType = event.getItem().getCurrencyType();
            String currencyName = currencyType.equals("VAULT") ?
                                 configManager.getCurrencyName("vault") : configManager.getCurrencyName("points");
            String currencySymbol = currencyType.equals("VAULT") ?
                                  plugin.getConfigManager().getVaultSymbol() : plugin.getConfigManager().getPointsSymbol();
            double startPrice = event.getItem().getStartPrice();
            double buyNowPrice = event.getItem().getBuyNowPrice();

            if (buyNowPrice > 0) {
                // 有一口价
                priceText = String.format("%.2f/%.2f %s%s", startPrice, buyNowPrice, currencySymbol, currencyName);
            } else {
                // 只有起拍价
                priceText = String.format("%.2f %s%s", startPrice, currencySymbol, currencyName);
            }
        }
        text.append(" §7| §6").append(priceText);

        // 买家/触发者信息
        String buyerName = event.getBuyerName() != null ? event.getBuyerName() : "-";
        text.append(" §7| §b").append(buyerName);

=======
        
        // 价格信息
        String priceText = "-";
        if (event.getEventType() == AuctionHistoryManager.AuctionEventType.BID || 
            event.getEventType() == AuctionHistoryManager.AuctionEventType.BUY) {
            // 货币类型
            String currencyType = event.getItem().getCurrencyType();
            String currencyText = currencyType.equals("VAULT") ? 
                                 configManager.getCurrencyName("vault") : configManager.getCurrencyName("points");
            double price = event.getEventType() == AuctionHistoryManager.AuctionEventType.BID ? 
                          event.getItem().getCurrentPrice() : event.getItem().getBuyNowPrice();
            
            priceText = String.format("%.2f %s", price, currencyText);
        } else if (event.getEventType() == AuctionHistoryManager.AuctionEventType.LIST) {
            // 上架事件显示起拍价和一口价（如果有）
            String currencyType = event.getItem().getCurrencyType();
            String currencyText = currencyType.equals("VAULT") ? 
                                 configManager.getCurrencyName("vault") : configManager.getCurrencyName("points");
            double startPrice = event.getItem().getStartPrice();
            double buyNowPrice = event.getItem().getBuyNowPrice();
            
            if (buyNowPrice > 0) {
                // 有一口价
                priceText = String.format("%.2f/%.2f %s", startPrice, buyNowPrice, currencyText);
            } else {
                // 只有起拍价
                priceText = String.format("%.2f %s", startPrice, currencyText);
            }
        }
        text.append(" §7| §6").append(priceText);
        
        // 买家/触发者信息
        String buyerName = event.getBuyerName() != null ? event.getBuyerName() : "-";
        text.append(" §7| §b").append(buyerName);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 事件时间信息
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(configManager.getDateFormat());
        String timeStr = sdf.format(new java.util.Date(event.getTimestamp()));
        text.append(" §7| §8").append(timeStr);
<<<<<<< HEAD

        return text.toString();
    }

=======
        
        return text.toString();
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 获取指定位置之前的所有颜色代码
     * @param text 包含颜色代码的文本
     * @param index 指定位置
     * @return 颜色代码字符串
     */
    private String getColorCodesBeforeIndex(String text, int index) {
        StringBuilder codes = new StringBuilder();
        char[] chars = text.toCharArray();
        int realCharCount = 0;
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        for (int i = 0; i < chars.length - 1 && realCharCount < index; i++) {
            if (chars[i] == '§') {
                codes.append('§').append(chars[i + 1]);
                i++; // 跳过颜色代码字符
            } else {
                realCharCount++;
            }
        }
<<<<<<< HEAD

        return codes.toString();
    }

=======
        
        return codes.toString();
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 优化全息显示
     * 当附近没有玩家时，移除全息实体以节省资源
     */
    private void optimizeHolograms() {
        for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
            UUID hologramId = entry.getKey();
            Location location = entry.getValue();
            World world = location.getWorld();
<<<<<<< HEAD

            if (world == null) continue;

            // 从配置获取视距
            float viewRange = configManager.getDisplaySettings("content").getViewRange();

=======
            
            if (world == null) continue;
            
            // 从配置获取视距
            float viewRange = configManager.getDisplaySettings("content").getViewRange();
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 检查附近玩家
            boolean hasNearbyPlayer = false;
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            for (Player player : players) {
                if (player.getWorld().equals(world) && player.getLocation().distance(location) <= viewRange) {
                    hasNearbyPlayer = true;
                    break;
                }
            }
<<<<<<< HEAD

            // 如果没有附近玩家，隐藏全息显示
            List<Entity> entities = displayManager.getHologramEntities(hologramId);
            for (Entity entity : entities) {
                if (entity instanceof Display) {
                    Display display = (Display) entity;
                    // 如果没有附近玩家，设置超小的可见范围；否则恢复正常范围
                    if (!hasNearbyPlayer) {
                        // 设置极小的可见范围，实现隐藏效果
                        display.setViewRange(0.1f);
                    } else {
                        // 恢复正常的可见范围
                        float normalViewRange = configManager.getDisplaySettings("content").getViewRange();
                        display.setViewRange(normalViewRange);
                    }
=======
            
            // 如果没有附近玩家，隐藏全息显示
            List<Entity> entities = displayManager.getHologramEntities(hologramId);
            for (Entity entity : entities) {
                if (entity instanceof ItemDisplay || entity instanceof TextDisplay) {
                    // 如果没有附近玩家，设为不可见；否则设为可见
                    entity.setGlowing(!hasNearbyPlayer);
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                }
            }
        }
    }
<<<<<<< HEAD

    /**
     * 清理非法Display实体
     * 检查所有Display实体，如果是由本插件创建但不在当前管理中的实体则移除
     */
    private void cleanupIllegalDisplayEntities() {
        // 获取所有世界
        for (World world : plugin.getServer().getWorlds()) {
            // 获取所有Display实体
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Display) {
                    Display display = (Display) entity;

                    // 检查是否是本插件创建的实体
                    String customName = display.getCustomName();
                    if (customName != null && (customName.startsWith("GlobalShop_Text_") || customName.startsWith("GlobalShop_Item_"))) {
                        // 是本插件创建的实体，检查是否在当前管理中
                        boolean isManaged = false;

                        // 检查是否在当前管理的实体列表中
                        for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
                            UUID hologramId = entry.getKey();
                            List<Entity> managedEntities = displayManager.getHologramEntities(hologramId);
                            if (managedEntities.contains(display)) {
                                isManaged = true;
                                break;
                            }
                        }

                        // 如果不在当前管理中，则移除
                        if (!isManaged) {
                            display.remove();
                        }
                    }
=======
    
    /**
     * 清理非法TextDisplay实体
     * 检查所有TextDisplay实体，如果不在hd_locations.yml中则移除
     */
    private void cleanupIllegalTextDisplays() {
        // 获取所有世界
        for (World world : plugin.getServer().getWorlds()) {
            // 获取所有TextDisplay实体
            for (Entity entity : world.getEntities()) {
                if (entity instanceof TextDisplay) {
                    TextDisplay textDisplay = (TextDisplay) entity;
                    Location location = textDisplay.getLocation();
                    
                    // 检查是否是合法的全息显示
                    boolean isLegal = false;
                    for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
                        Location legalLoc = entry.getValue();
                        // 如果位置在合法位置附近（允许1格误差），则认为是合法的
                        if (legalLoc.getWorld().equals(location.getWorld()) &&
                            Math.abs(legalLoc.getX() - location.getX()) <= 1 &&
                            Math.abs(legalLoc.getY() - location.getY()) <= 1 &&
                            Math.abs(legalLoc.getZ() - location.getZ()) <= 1) {
                            isLegal = true;
                            break;
                        }
                    }
                    
                    // 如果不是合法的，则移除
                    if (!isLegal) {
                        textDisplay.remove();
                    }
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                }
            }
        }
    }
<<<<<<< HEAD
}
=======
} 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
