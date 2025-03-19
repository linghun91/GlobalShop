package cn.i7mc.globalshop.utils;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 广播管理器类，负责处理拍卖行事件的广播
 * 支持多种广播位置：聊天框、Boss栏、标题、副标题、动作栏
 */
public class BroadcastManager {
    private final GlobalShop plugin;
    private boolean enabled;
    
    // 事件广播配置
    private Map<BroadcastEvent, BroadcastConfig> eventConfigs;
    
    // 活跃的Boss栏列表
    private Map<UUID, BossBar> activeBossBars;
    private Map<UUID, BukkitTask> bossBarTasks;
    
    // Boss栏样式配置
    private BarColor bossBarColor;
    private BarStyle bossBarStyle;
    private int bossBarDuration;
    
    // 标题样式配置
    private int titleFadeIn;
    private int titleStay;
    private int titleFadeOut;
    
    /**
     * 广播事件类型
     */
    public enum BroadcastEvent {
        ITEM_LISTED,    // 物品上架
        AUCTION_WON,    // 竞拍成功
        BUY_NOW         // 一口价购买
    }
    
    /**
     * 广播位置类型
     */
    public enum BroadcastLocation {
        CHAT,           // 聊天框
        BOSSBAR,        // Boss栏
        TITLE,          // 标题
        SUBTITLE,       // 副标题
        ACTIONBAR       // 动作栏
    }
    
    /**
     * 单个事件的广播配置
     */
    private static class BroadcastConfig {
        private boolean enabled;
        private Map<BroadcastLocation, Boolean> locations;
        
        public BroadcastConfig(boolean enabled) {
            this.enabled = enabled;
            this.locations = new HashMap<>();
            // 默认都不启用
            for (BroadcastLocation location : BroadcastLocation.values()) {
                this.locations.put(location, false);
            }
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public boolean isLocationEnabled(BroadcastLocation location) {
            return locations.getOrDefault(location, false);
        }
        
        public void setLocationEnabled(BroadcastLocation location, boolean enabled) {
            locations.put(location, enabled);
        }
    }
    
    /**
     * 构造函数，初始化广播管理器
     * @param plugin 插件实例
     */
    public BroadcastManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.eventConfigs = new HashMap<>();
        this.activeBossBars = new HashMap<>();
        this.bossBarTasks = new HashMap<>();
        
        // 默认所有事件都不启用广播
        for (BroadcastEvent event : BroadcastEvent.values()) {
            this.eventConfigs.put(event, new BroadcastConfig(false));
        }
        
        // 加载配置
        loadConfig();
    }
    
    /**
     * 从配置文件加载广播设置
     */
    public void loadConfig() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("broadcast");
        if (config == null) {
            this.enabled = false;
            return;
        }
        
        this.enabled = config.getBoolean("enabled", false);
        if (!this.enabled) return;
        
        // 加载事件配置
        ConfigurationSection eventsConfig = config.getConfigurationSection("events");
        if (eventsConfig != null) {
            // 物品上架广播
            loadEventConfig(eventsConfig, "item_listed", BroadcastEvent.ITEM_LISTED);
            // 竞拍成功广播
            loadEventConfig(eventsConfig, "auction_won", BroadcastEvent.AUCTION_WON);
            // 一口价购买广播
            loadEventConfig(eventsConfig, "buy_now", BroadcastEvent.BUY_NOW);
        }
        
        // 加载样式配置
        ConfigurationSection stylesConfig = config.getConfigurationSection("styles");
        if (stylesConfig != null) {
            // Boss栏样式
            ConfigurationSection bossBarConfig = stylesConfig.getConfigurationSection("bossbar");
            if (bossBarConfig != null) {
                try {
                    this.bossBarColor = BarColor.valueOf(bossBarConfig.getString("color", "PURPLE"));
                } catch (IllegalArgumentException e) {
                    this.bossBarColor = BarColor.PURPLE;
                }
                
                try {
                    this.bossBarStyle = BarStyle.valueOf(bossBarConfig.getString("style", "SOLID"));
                } catch (IllegalArgumentException e) {
                    this.bossBarStyle = BarStyle.SOLID;
                }
                
                this.bossBarDuration = bossBarConfig.getInt("duration", 5);
            }
            
            // 标题样式
            ConfigurationSection titleConfig = stylesConfig.getConfigurationSection("title");
            if (titleConfig != null) {
                this.titleFadeIn = titleConfig.getInt("fade_in", 10);
                this.titleStay = titleConfig.getInt("stay", 40);
                this.titleFadeOut = titleConfig.getInt("fade_out", 10);
            }
        }
    }
    
    /**
     * 加载单个事件的广播配置
     */
    private void loadEventConfig(ConfigurationSection eventsConfig, String configKey, BroadcastEvent event) {
        ConfigurationSection eventConfig = eventsConfig.getConfigurationSection(configKey);
        if (eventConfig == null) return;
        
        boolean eventEnabled = eventConfig.getBoolean("enabled", false);
        BroadcastConfig broadcastConfig = new BroadcastConfig(eventEnabled);
        
        ConfigurationSection locationsConfig = eventConfig.getConfigurationSection("locations");
        if (locationsConfig != null) {
            broadcastConfig.setLocationEnabled(BroadcastLocation.CHAT, locationsConfig.getBoolean("chat", false));
            broadcastConfig.setLocationEnabled(BroadcastLocation.BOSSBAR, locationsConfig.getBoolean("bossbar", false));
            broadcastConfig.setLocationEnabled(BroadcastLocation.TITLE, locationsConfig.getBoolean("title", false));
            broadcastConfig.setLocationEnabled(BroadcastLocation.SUBTITLE, locationsConfig.getBoolean("subtitle", false));
            broadcastConfig.setLocationEnabled(BroadcastLocation.ACTIONBAR, locationsConfig.getBoolean("actionbar", false));
        }
        
        this.eventConfigs.put(event, broadcastConfig);
    }
    
    /**
     * 广播物品上架事件
     * @param player 上架玩家
     * @param item 上架物品
     */
    public void broadcastItemListed(Player player, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.ITEM_LISTED)) return;
        
        String itemName = ChatUtils.getItemName(item.getItem());
        String message = String.format("§6玩家 §e%s §6上架了物品 §e%s§6！", player.getName(), itemName);
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        String priceInfo = "";
        
        if (item.hasBuyNowPrice()) {
            priceInfo = String.format("§6起拍价: %s§e%.2f §6一口价: %s§e%.2f", 
                    currencySymbol, item.getStartPrice(), 
                    currencySymbol, item.getBuyNowPrice());
        } else {
            priceInfo = String.format("§6起拍价: %s§e%.2f", 
                    currencySymbol, item.getStartPrice());
        }
        
        broadcastMessage(BroadcastEvent.ITEM_LISTED, message, priceInfo);
    }
    
    /**
     * 广播竞拍成功事件
     * @param buyer 买家
     * @param seller 卖家
     * @param item 拍卖物品
     */
    public void broadcastAuctionWon(String buyer, String seller, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.AUCTION_WON)) return;
        
        String itemName = ChatUtils.getItemName(item.getItem());
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        
        String message = String.format("§6玩家 §e%s §6赢得了 §e%s §6的拍卖物品 §e%s§6！", 
                buyer, seller, itemName);
        String priceInfo = String.format("§6成交价: %s§e%.2f", 
                currencySymbol, item.getCurrentPrice());
        
        broadcastMessage(BroadcastEvent.AUCTION_WON, message, priceInfo);
    }
    
    /**
     * 广播一口价购买事件
     * @param buyer 买家
     * @param seller 卖家
     * @param item 拍卖物品
     */
    public void broadcastBuyNow(String buyer, String seller, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.BUY_NOW)) return;
        
        String itemName = ChatUtils.getItemName(item.getItem());
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        
        String message = String.format("§6玩家 §e%s §6立即购买了 §e%s §6的物品 §e%s§6！", 
                buyer, seller, itemName);
        String priceInfo = String.format("§6成交价: %s§e%.2f", 
                currencySymbol, item.getBuyNowPrice());
        
        broadcastMessage(BroadcastEvent.BUY_NOW, message, priceInfo);
    }
    
    /**
     * 广播消息到配置的所有位置
     * @param event 广播事件类型
     * @param mainMessage 主要消息
     * @param subMessage 次要消息（可用于副标题或额外信息）
     */
    private void broadcastMessage(BroadcastEvent event, String mainMessage, String subMessage) {
        if (!this.enabled) return;
        
        BroadcastConfig config = this.eventConfigs.get(event);
        if (config == null || !config.isEnabled()) return;
        
        // 聊天框广播
        if (config.isLocationEnabled(BroadcastLocation.CHAT)) {
            Bukkit.broadcastMessage(mainMessage);
            if (subMessage != null && !subMessage.isEmpty()) {
                Bukkit.broadcastMessage(subMessage);
            }
        }
        
        // 其他广播位置需要遍历在线玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Boss栏广播
            if (config.isLocationEnabled(BroadcastLocation.BOSSBAR)) {
                showBossBar(player, mainMessage, this.bossBarDuration);
            }
            
            // 标题广播
            if (config.isLocationEnabled(BroadcastLocation.TITLE) || 
                config.isLocationEnabled(BroadcastLocation.SUBTITLE)) {
                
                String title = config.isLocationEnabled(BroadcastLocation.TITLE) ? mainMessage : "";
                String subtitle = config.isLocationEnabled(BroadcastLocation.SUBTITLE) ? 
                        (subMessage != null ? subMessage : "") : "";
                
                player.sendTitle(title, subtitle, this.titleFadeIn, this.titleStay, this.titleFadeOut);
            }
            
            // 动作栏广播
            if (config.isLocationEnabled(BroadcastLocation.ACTIONBAR)) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(mainMessage));
            }
        }
    }
    
    /**
     * 向玩家显示Boss栏消息
     * @param player 目标玩家
     * @param message 消息内容
     * @param duration 持续时间（秒）
     */
    private void showBossBar(Player player, String message, int duration) {
        // 先移除该玩家现有的Boss栏
        removeBossBar(player.getUniqueId());
        
        // 创建新的Boss栏
        BossBar bossBar = Bukkit.createBossBar(message, this.bossBarColor, this.bossBarStyle);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);
        
        // 保存Boss栏引用
        this.activeBossBars.put(player.getUniqueId(), bossBar);
        
        // 设置定时任务，在指定时间后移除Boss栏
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                removeBossBar(player.getUniqueId());
            }
        }.runTaskLater(plugin, duration * 20L);
        
        this.bossBarTasks.put(player.getUniqueId(), task);
    }
    
    /**
     * 移除玩家的Boss栏
     * @param playerId 玩家UUID
     */
    private void removeBossBar(UUID playerId) {
        BossBar bossBar = this.activeBossBars.remove(playerId);
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
        
        BukkitTask task = this.bossBarTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * 检查事件是否启用
     * @param event 广播事件类型
     * @return 是否启用
     */
    private boolean isEventEnabled(BroadcastEvent event) {
        if (!this.enabled) return false;
        
        BroadcastConfig config = this.eventConfigs.get(event);
        return config != null && config.isEnabled();
    }
    
    /**
     * 清理所有活跃的Boss栏
     */
    public void clearAllBossBars() {
        for (UUID playerId : this.activeBossBars.keySet()) {
            removeBossBar(playerId);
        }
        this.activeBossBars.clear();
        this.bossBarTasks.clear();
    }
    
    /**
     * 关闭广播管理器，清理资源
     */
    public void shutdown() {
        clearAllBossBars();
    }
} 