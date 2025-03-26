package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
    
    private final GlobalShop plugin;
    private final HologramDisplayManager displayManager;
    private final ItemDisplayManager itemManager;
    private final TextDisplayManager textManager;
    private final AuctionHistoryManager historyManager;
    private final HologramConfigManager configManager;
    
    private final Map<UUID, Location> hologramLocations;
    private boolean forceUpdate = false;
    
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
    }
    
    /**
     * 添加全息显示位置
     * @param hologramId 全息ID
     * @param location 位置
     */
    public void addHologramLocation(UUID hologramId, Location location) {
        hologramLocations.put(hologramId, location.clone());
    }
    
    /**
     * 移除全息显示位置
     * @param hologramId 全息ID
     */
    public void removeHologramLocation(UUID hologramId) {
        hologramLocations.remove(hologramId);
        displayManager.removeHologram(hologramId);
    }
    
    /**
     * 强制更新全息显示
     */
    public void forceUpdate() {
        this.forceUpdate = true;
    }
    
    /**
     * 更新任务配置
     * 从配置管理器重新加载配置值
     */
    public void updateTaskConfig() {
        // 获取新的更新间隔
        int newInterval = configManager.getUpdateInterval();
        
        // 重新排程本任务（如果间隔有变化）
        if (this.getTaskId() > 0) {
            // 只取消当前任务，不要尝试重新调度相同的BukkitRunnable实例
            this.cancel();
            
            // 通知主类需要重新调度任务
            plugin.rescheduleHologramTask(newInterval);
        }
    }
    
    @Override
    public void run() {
        // 首先清理非法TextDisplay实体
        cleanupIllegalTextDisplays();
        
        // 首先从HologramCommandManager同步位置信息
        for (Map.Entry<String, UUID> entry : plugin.getHologramCommandManager().getHologramIds().entrySet()) {
            UUID hologramId = entry.getValue();
            Location location = plugin.getHologramCommandManager().getHologramLocations().get(entry.getKey());
            if (location != null && !hologramLocations.containsKey(hologramId)) {
                hologramLocations.put(hologramId, location.clone());
            }
        }
        
        // 如果没有全息显示，跳过更新
        if (hologramLocations.isEmpty()) {
            return;
        }
        
        // 获取最新历史记录
        List<AuctionHistoryManager.AuctionHistoryEvent> recentEvents = 
                historyManager.getRecentEvents(configManager.getDisplayRows());
        
        // 如果没有历史记录且不强制更新，跳过更新
        if (recentEvents.isEmpty() && !forceUpdate) {
            return;
        }
        
        // 输出调试信息
        
        // 重置强制更新标志
        forceUpdate = false;
        
        // 更新每一个全息显示
        for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
            UUID hologramId = entry.getKey();
            Location baseLocation = entry.getValue().clone();
            
            try {
                // 检查位置有效性
                if (baseLocation.getWorld() == null) {
                    continue;
                }
                

                // 清除现有全息实体
                displayManager.clearHologram(hologramId);
                
                // 创建标题
                Location titleLoc = baseLocation.clone().add(0, 0.3, 0);
                textManager.createTextDisplay(titleLoc, configManager.getMessage("title"), hologramId, 1.2f, null, null, "title");
                
                // 如果没有历史记录，显示提示信息
                if (recentEvents.isEmpty()) {
                    Location textLoc = baseLocation.clone().add(0, -0.5, 0);
                    textManager.createTextDisplay(textLoc, configManager.getMessage("no-history"), hologramId, 1.0f, "#FFFFFF", "#33000000", "content");
                    continue;
                }
                
                // 获取显示位置配置
                float lineSpacing = configManager.getLineSpacing();
                
                // 添加表格列标题行
                Location headerLoc = baseLocation.clone().add(0, -0.5, 0);
                String headerText = configManager.getMessage("header");
                TextDisplay header = textManager.createTextDisplay(headerLoc, headerText, hologramId, 0.9f, null, null, "content");
                
                // 创建历史记录显示
                for (int i = 0; i < recentEvents.size(); i++) {
                    AuctionHistoryManager.AuctionHistoryEvent event = recentEvents.get(i);
                    
                    // 计算行位置（从表头下方开始）
                    Location rowLocation = baseLocation.clone();
                    rowLocation.add(0, -((i + 1) * lineSpacing + 0.5), 0); // 从表头下方开始
                    
                    // 获取格式化文本
                    String text = formatEventText(event);
                    TextDisplay textDisplay = textManager.createTextDisplay(rowLocation, text, hologramId, 0.9f, null, null, "content");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                
                // 异常情况下，仍然尝试显示错误信息
                try {
                    displayManager.clearHologram(hologramId);
                    Location errorLoc = baseLocation.clone();
                    TextDisplay errorDisplay = textManager.createTextDisplay(errorLoc, configManager.getMessage("load-error"), hologramId, 1.0f, null, null, "content");
                } catch (Exception ex) {
                }
            }
        }
        
        // 检查全息显示是否有附近玩家，如果没有，暂时移除实体以节省资源
        optimizeHolograms();
    }
    
    /**
     * 格式化事件文本显示
     */
    private String formatEventText(AuctionHistoryManager.AuctionHistoryEvent event) {
        StringBuilder text = new StringBuilder();
        
        // 物品名称，最多显示10个字符
        String itemName = event.getItem().getDisplayName();
        if (itemName == null || itemName.isEmpty()) {
            itemName = event.getItem().getItem().getType().toString();
        }
        if (itemName.length() > 10) {
            itemName = itemName.substring(0, 9) + "…";
        }
        
        // 物品数量（如果大于1）
        int amount = event.getItem().getItem().getAmount();
        if (amount > 1) {
            itemName += " §fx" + amount;
        }
        text.append("§e").append(itemName);
        
        // 卖家信息
        String sellerName = event.getSellerName();
        text.append(" §7| §a").append(sellerName);
        
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
        
        // 事件时间信息
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(configManager.getDateFormat());
        String timeStr = sdf.format(new java.util.Date(event.getTimestamp()));
        text.append(" §7| §8").append(timeStr);
        
        return text.toString();
    }
    
    /**
     * 优化全息显示
     * 当附近没有玩家时，移除全息实体以节省资源
     */
    private void optimizeHolograms() {
        for (Map.Entry<UUID, Location> entry : hologramLocations.entrySet()) {
            UUID hologramId = entry.getKey();
            Location location = entry.getValue();
            World world = location.getWorld();
            
            if (world == null) continue;
            
            // 从配置获取视距
            float viewRange = configManager.getDisplaySettings("content").getViewRange();
            
            // 检查附近玩家
            boolean hasNearbyPlayer = false;
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            for (Player player : players) {
                if (player.getWorld().equals(world) && player.getLocation().distance(location) <= viewRange) {
                    hasNearbyPlayer = true;
                    break;
                }
            }
            
            // 如果没有附近玩家，隐藏全息显示
            List<Entity> entities = displayManager.getHologramEntities(hologramId);
            for (Entity entity : entities) {
                if (entity instanceof ItemDisplay || entity instanceof TextDisplay) {
                    // 如果没有附近玩家，设为不可见；否则设为可见
                    entity.setGlowing(!hasNearbyPlayer);
                }
            }
        }
    }
    
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
                }
            }
        }
    }
} 