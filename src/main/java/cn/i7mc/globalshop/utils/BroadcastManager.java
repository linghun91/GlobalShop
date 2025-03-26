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
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.List;
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
    
    // 添加成员变量用于存储最后广播的物品信息
    private AuctionItem lastBroadcastItem;
    
    /**
     * 广播事件类型
     */
    public enum BroadcastEvent {
        ITEM_LISTED,    // 物品上架
        AUCTION_WON,    // 竞拍成功
        BUY_NOW,        // 一口价购买
        BID_CONFIRMED   // 竞价确认（新增）
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
            // 竞价确认广播（新增）
            loadEventConfig(eventsConfig, "bid_confirmed", BroadcastEvent.BID_CONFIRMED);
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
        if (eventConfig == null) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 未找到广播事件配置: " + configKey);
            }
            return;
        }
        
        boolean eventEnabled = eventConfig.getBoolean("enabled", false);
        BroadcastConfig broadcastConfig = new BroadcastConfig(eventEnabled);
        
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("调试: 加载广播事件配置: " + configKey + ", 启用状态: " + eventEnabled);
        }
        
        ConfigurationSection locationsConfig = eventConfig.getConfigurationSection("locations");
        if (locationsConfig != null) {
            boolean chatEnabled = locationsConfig.getBoolean("chat", false);
            boolean bossbarEnabled = locationsConfig.getBoolean("bossbar", false);
            boolean titleEnabled = locationsConfig.getBoolean("title", false);
            boolean subtitleEnabled = locationsConfig.getBoolean("subtitle", false);
            boolean actionbarEnabled = locationsConfig.getBoolean("actionbar", false);
            
            broadcastConfig.setLocationEnabled(BroadcastLocation.CHAT, chatEnabled);
            broadcastConfig.setLocationEnabled(BroadcastLocation.BOSSBAR, bossbarEnabled);
            broadcastConfig.setLocationEnabled(BroadcastLocation.TITLE, titleEnabled);
            broadcastConfig.setLocationEnabled(BroadcastLocation.SUBTITLE, subtitleEnabled);
            broadcastConfig.setLocationEnabled(BroadcastLocation.ACTIONBAR, actionbarEnabled);
            
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 广播位置配置 - " + configKey + ": chat=" + chatEnabled 
                    + ", bossbar=" + bossbarEnabled + ", title=" + titleEnabled 
                    + ", subtitle=" + subtitleEnabled + ", actionbar=" + actionbarEnabled);
            }
        }
        
        this.eventConfigs.put(event, broadcastConfig);
    }
    
    /**
     * 广播消息到配置的所有位置
     * @param event 广播事件类型
     * @param chatMessage 聊天框消息
     * @param bossbarMessage Boss栏消息
     * @param titleMessage 标题消息
     * @param subtitleMessage 副标题消息
     * @param actionbarMessage 动作栏消息
     */
    private void broadcastMessage(BroadcastEvent event, String chatMessage, String bossbarMessage, 
                                 String titleMessage, String subtitleMessage, String actionbarMessage) {
        if (!this.enabled) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 广播系统总开关已关闭，不发送任何消息");
            }
            return;
        }
        
        BroadcastConfig config = this.eventConfigs.get(event);
        if (config == null || !config.isEnabled()) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 广播事件 " + event.name() + " 的配置不存在或已禁用");
            }
            return;
        }
        
        // 记录开始广播的调试信息
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("调试: 开始广播事件 " + event.name() + " 的消息");
        }
        
        // 聊天框广播
        if (config.isLocationEnabled(BroadcastLocation.CHAT) && chatMessage != null && !chatMessage.isEmpty()) {
            // 创建带有详细信息的交互式组件
            TextComponent mainComponent = new TextComponent(chatMessage);
            // 从配置文件获取详细信息按钮文本
            TextComponent detailsComponent = new TextComponent(
                plugin.getMessageManager().getDetailsButtonText());
            
            // 使用原生Minecraft的HoverEvent展示物品信息
            if (lastBroadcastItem != null) {
                try {
                    // 使用SHOW_ITEM类型直接展示物品，保留所有原始格式包括渐变色
                    ItemStack itemDisplay = plugin.getGuiManager().createAuctionItemDisplay(lastBroadcastItem, null);
                    
                    // 创建物品的悬浮事件
                    net.md_5.bungee.api.chat.BaseComponent[] hoverEventComponents = 
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(createItemInfoText(itemDisplay));
                    
                    detailsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents));
                } catch (Exception e) {
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().warning("创建悬停文本时发生错误: " + e.getMessage());
                    }
                    // 使用备用文本
                    detailsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(plugin.getMessageManager().getNoDetailsAvailableText()).create()));
                }
            } else {
                // 使用无详细信息提示
                detailsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    new ComponentBuilder(plugin.getMessageManager().getNoDetailsAvailableText()).create()));
            }
            
            // 合并组件
            mainComponent.addExtra(detailsComponent);
            
            // 广播给所有玩家
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(mainComponent);
            }
        }
        
        // 其他广播位置需要遍历在线玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Boss栏广播
            if (config.isLocationEnabled(BroadcastLocation.BOSSBAR) && bossbarMessage != null && !bossbarMessage.isEmpty()) {
                showBossBar(player, bossbarMessage, this.bossBarDuration);
            }
            
            // 标题广播
            if ((config.isLocationEnabled(BroadcastLocation.TITLE) && titleMessage != null && !titleMessage.isEmpty()) || 
                (config.isLocationEnabled(BroadcastLocation.SUBTITLE) && subtitleMessage != null && !subtitleMessage.isEmpty())) {
                
                String title = config.isLocationEnabled(BroadcastLocation.TITLE) ? titleMessage : "";
                String subtitle = config.isLocationEnabled(BroadcastLocation.SUBTITLE) ? subtitleMessage : "";
                
                player.sendTitle(title, subtitle, this.titleFadeIn, this.titleStay, this.titleFadeOut);
            }
            
            // 动作栏广播
            if (config.isLocationEnabled(BroadcastLocation.ACTIONBAR) && actionbarMessage != null && !actionbarMessage.isEmpty()) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionbarMessage));
            }
        }
    }
    
    /**
     * 创建物品信息文本，保留所有原始格式包括渐变色
     * @param itemStack 物品实例
     * @return 格式化的物品信息文本
     */
    private String createItemInfoText(ItemStack itemStack) {
        StringBuilder sb = new StringBuilder();
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            // 添加物品名称
            if (meta.hasDisplayName()) {
                sb.append(meta.getDisplayName());
                
                // 添加物品数量显示
                if (itemStack.getAmount() > 1) {
                    sb.append(" x").append(itemStack.getAmount());
                }
                
                sb.append("\n");
            }
            
            // 添加所有Lore内容，保留原始格式
            if (meta.hasLore() && meta.getLore() != null) {
                List<String> lore = meta.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    sb.append(lore.get(i));
                    // 最后一行不添加换行符
                    if (i < lore.size() - 1) {
                        sb.append("\n");
                    }
                }
            }
        } else {
            // 如果没有元数据，至少显示物品类型
            sb.append(ChatUtils.getItemName(itemStack));
        }
        
        return sb.toString();
    }
    
    /**
     * 根据事件类型创建悬停文本，保留为后备方案
     * @param event 广播事件类型
     * @return 悬停文本
     */
    private String createHoverTextForEvent(BroadcastEvent event) {
        if (lastBroadcastItem == null) {
            // 使用配置文件中的无详细信息提示
            return plugin.getMessageManager().getNoDetailsAvailableText();
        }
        
        try {
            // 使用完整的createAuctionItemDisplay方法，传递null作为Player参数
            // 这样可以获取与拍卖行主界面完全一致的物品信息
            ItemStack itemDisplay = plugin.getGuiManager().createAuctionItemDisplay(lastBroadcastItem, null);
            return createItemInfoText(itemDisplay);
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("创建悬停文本时发生错误: " + e.getMessage());
            }
        }
        
        // 如果获取Lore失败，使用备用方案
        switch (event) {
            case ITEM_LISTED:
                String itemName = ChatUtils.getItemName(lastBroadcastItem.getItem());
                String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(lastBroadcastItem.getCurrencyType());
                
                StringBuilder sb = new StringBuilder();
                sb.append("§6物品: §f").append(itemName).append("\n");
                sb.append("§6物品ID: §f").append(lastBroadcastItem.getId()).append("\n");
                sb.append("§6起拍价: §f").append(currencySymbol).append(String.format("%.2f", lastBroadcastItem.getStartPrice())).append("\n");
                
                if (lastBroadcastItem.hasBuyNowPrice()) {
                    sb.append("§6一口价: §f").append(currencySymbol).append(String.format("%.2f", lastBroadcastItem.getBuyNowPrice()));
                } else {
                    sb.append("§6一口价: §f无");
                }
                
                return sb.toString();
                
            case AUCTION_WON:
            case BUY_NOW:
                String itemName2 = ChatUtils.getItemName(lastBroadcastItem.getItem());
                String currencySymbol2 = plugin.getEconomyManager().getCurrencySymbol(lastBroadcastItem.getCurrencyType());
                double finalPrice = lastBroadcastItem.getCurrentPrice();
                
                StringBuilder sb2 = new StringBuilder();
                sb2.append("§6物品: §f").append(itemName2).append("\n");
                sb2.append("§6物品ID: §f").append(lastBroadcastItem.getId()).append("\n");
                sb2.append("§6成交价格: §f").append(currencySymbol2).append(String.format("%.2f", finalPrice));
                
                return sb2.toString();
                
            default:
                return "§c无详细信息";
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
        if (!this.enabled) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 广播系统总开关已关闭，事件类型: " + event.name());
            }
            return false;
        }
        
        BroadcastConfig config = this.eventConfigs.get(event);
        boolean isEnabled = config != null && config.isEnabled();
        
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("调试: 广播事件 " + event.name() + " 启用状态: " + isEnabled);
        }
        
        return isEnabled;
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
    
    /**
     * 从message.yml获取广播消息模板
     * @param eventKey 事件键名，对应message.yml中的broadcast下的子键
     * @param locationType 广播位置类型
     * @return 广播消息模板
     */
    private String getBroadcastMessage(String eventKey, String locationType) {
        String path = "broadcast." + eventKey + "." + locationType;
        
        // 使用一个通用的简单默认消息，确保能从message.yml加载正确的配置
        String defaultMessage = "";
        
        // 通过MessageManager获取消息，确保从message.yml中加载
        String message = plugin.getMessageManager().getMessages().getString(path, defaultMessage);
        
        // 如果配置中没有设置，使用简单的默认消息（不含任何格式，只用于调试）
        if (message == null || message.isEmpty()) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("警告: 未找到广播消息配置: " + path);
            }
            // 返回一个非常简单的默认消息，确保有显示
            return "§7[广播] " + eventKey + " - " + locationType;
        }
        
        return message;
    }
    
    /**
     * 替换消息中的占位符
     * @param message 原始消息
     * @param placeholders 占位符映射表
     * @return 替换后的消息
     */
    private String replacePlaceholders(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * 广播物品上架事件
     * @param player 上架玩家
     * @param item 上架物品
     */
    public void broadcastItemListed(Player player, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.ITEM_LISTED)) return;
        
        // 保存当前物品信息用于详细信息显示
        this.lastBroadcastItem = item;
        
        // 使用与GUI中相同的方法获取物品名称，保持一致性
        ItemStack original = item.getItem();
        String itemName;
        
        // 尝试获取物品的自定义显示名称
        ItemMeta meta = original.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // 移除颜色代码，让message.yml中的颜色设置生效
            itemName = ChatColor.stripColor(meta.getDisplayName());
        } else {
            // 只有在设置为中文语言时才进行原版物品名称的中文翻译
            if (plugin.getConfigManager().isChineseLanguage()) {
                // 使用与GuiManager相同的逻辑获取中文名称，但不添加颜色代码
                String chineseName = plugin.getLanguageManager().getChineseName(original.getType());
                itemName = (chineseName != null && !chineseName.isEmpty()) ? 
                        chineseName : 
                        ChatColor.stripColor(ChatUtils.getItemName(original));
            } else {
                // 非中文语言环境下使用原版物品名称
                itemName = ChatColor.stripColor(ChatUtils.getItemName(original));
            }
        }
        
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        
        // 从message.yml获取消息格式，填充占位符
        String chatMessage = getBroadcastMessage("item_listed", "chat");
        String bossbarMessage = getBroadcastMessage("item_listed", "bossbar");
        String titleMessage = getBroadcastMessage("item_listed", "title"); 
        String subtitleMessage = getBroadcastMessage("item_listed", "subtitle");
        String actionbarMessage = getBroadcastMessage("item_listed", "actionbar");
        
        // 替换占位符
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", player.getName());
        placeholders.put("%item_name%", itemName);
        placeholders.put("%amount%", String.valueOf(original.getAmount()));
        placeholders.put("%start_price%", currencySymbol + String.format("%.2f", item.getStartPrice()));
        
        if (item.hasBuyNowPrice()) {
            placeholders.put("%buy_now_price%", currencySymbol + String.format("%.2f", item.getBuyNowPrice()));
        } else {
            placeholders.put("%buy_now_price%", "无");
        }
        
        chatMessage = replacePlaceholders(chatMessage, placeholders);
        bossbarMessage = replacePlaceholders(bossbarMessage, placeholders);
        titleMessage = replacePlaceholders(titleMessage, placeholders);
        subtitleMessage = replacePlaceholders(subtitleMessage, placeholders);
        actionbarMessage = replacePlaceholders(actionbarMessage, placeholders);
        
        broadcastMessage(BroadcastEvent.ITEM_LISTED, chatMessage, bossbarMessage, titleMessage, subtitleMessage, actionbarMessage);
    }
    
    /**
     * 广播竞拍成功事件
     * @param buyer 买家
     * @param seller 卖家
     * @param item 拍卖物品
     */
    public void broadcastAuctionWon(String buyer, String seller, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.AUCTION_WON)) return;
        
        // 保存当前物品信息用于详细信息显示
        this.lastBroadcastItem = item;
        
        // 使用与GUI中相同的方法获取物品名称，保持一致性
        ItemStack original = item.getItem();
        String itemName;
        
        // 尝试获取物品的自定义显示名称
        ItemMeta meta = original.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // 移除颜色代码，让message.yml中的颜色设置生效
            itemName = ChatColor.stripColor(meta.getDisplayName());
        } else {
            // 只有在设置为中文语言时才进行原版物品名称的中文翻译
            if (plugin.getConfigManager().isChineseLanguage()) {
                // 使用与GuiManager相同的逻辑获取中文名称，但不添加颜色代码
                String chineseName = plugin.getLanguageManager().getChineseName(original.getType());
                itemName = (chineseName != null && !chineseName.isEmpty()) ? 
                        chineseName : 
                        ChatColor.stripColor(ChatUtils.getItemName(original));
            } else {
                // 非中文语言环境下使用原版物品名称
                itemName = ChatColor.stripColor(ChatUtils.getItemName(original));
            }
        }
        
        // 添加物品数量显示
        if (original.getAmount() > 1) {
            itemName += " x" + original.getAmount();
        }
        
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        
        // 从message.yml获取消息格式，填充占位符
        String chatMessage = getBroadcastMessage("auction_won", "chat");
        String bossbarMessage = getBroadcastMessage("auction_won", "bossbar");
        String titleMessage = getBroadcastMessage("auction_won", "title"); 
        String subtitleMessage = getBroadcastMessage("auction_won", "subtitle");
        String actionbarMessage = getBroadcastMessage("auction_won", "actionbar");
        
        // 替换占位符
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%buyer%", buyer);
        placeholders.put("%seller%", seller);
        placeholders.put("%item_name%", itemName);
        placeholders.put("%amount%", String.valueOf(item.getItem().getAmount()));
        placeholders.put("%price%", currencySymbol + String.format("%.2f", item.getCurrentPrice()));
        
        chatMessage = replacePlaceholders(chatMessage, placeholders);
        bossbarMessage = replacePlaceholders(bossbarMessage, placeholders);
        titleMessage = replacePlaceholders(titleMessage, placeholders);
        subtitleMessage = replacePlaceholders(subtitleMessage, placeholders);
        actionbarMessage = replacePlaceholders(actionbarMessage, placeholders);
        
        broadcastMessage(BroadcastEvent.AUCTION_WON, chatMessage, bossbarMessage, titleMessage, subtitleMessage, actionbarMessage);
    }
    
    /**
     * 广播一口价购买事件
     * @param buyer 买家
     * @param seller 卖家
     * @param item 拍卖物品
     */
    public void broadcastBuyNow(String buyer, String seller, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.BUY_NOW)) return;
        
        // 保存当前物品信息用于详细信息显示
        this.lastBroadcastItem = item;
        
        // 使用与GUI中相同的方法获取物品名称，保持一致性
        ItemStack original = item.getItem();
        String itemName;
        
        // 尝试获取物品的自定义显示名称
        ItemMeta meta = original.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // 移除颜色代码，让message.yml中的颜色设置生效
            itemName = ChatColor.stripColor(meta.getDisplayName());
        } else {
            // 只有在设置为中文语言时才进行原版物品名称的中文翻译
            if (plugin.getConfigManager().isChineseLanguage()) {
                // 使用与GuiManager相同的逻辑获取中文名称，但不添加颜色代码
                String chineseName = plugin.getLanguageManager().getChineseName(original.getType());
                itemName = (chineseName != null && !chineseName.isEmpty()) ? 
                        chineseName : 
                        ChatColor.stripColor(ChatUtils.getItemName(original));
            } else {
                // 非中文语言环境下使用原版物品名称
                itemName = ChatColor.stripColor(ChatUtils.getItemName(original));
            }
        }
        
        // 添加物品数量显示
        if (original.getAmount() > 1) {
            itemName += " x" + original.getAmount();
        }
        
        String currencySymbol = plugin.getEconomyManager().getCurrencySymbol(item.getCurrencyType());
        
        // 从message.yml获取消息格式，填充占位符
        String chatMessage = getBroadcastMessage("buy_now", "chat");
        String bossbarMessage = getBroadcastMessage("buy_now", "bossbar");
        String titleMessage = getBroadcastMessage("buy_now", "title"); 
        String subtitleMessage = getBroadcastMessage("buy_now", "subtitle");
        String actionbarMessage = getBroadcastMessage("buy_now", "actionbar");
        
        // 替换占位符
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%buyer%", buyer);
        placeholders.put("%seller%", seller);
        placeholders.put("%item_name%", itemName);
        placeholders.put("%amount%", String.valueOf(item.getItem().getAmount()));
        placeholders.put("%price%", currencySymbol + String.format("%.2f", item.getBuyNowPrice()));
        
        chatMessage = replacePlaceholders(chatMessage, placeholders);
        bossbarMessage = replacePlaceholders(bossbarMessage, placeholders);
        titleMessage = replacePlaceholders(titleMessage, placeholders);
        subtitleMessage = replacePlaceholders(subtitleMessage, placeholders);
        actionbarMessage = replacePlaceholders(actionbarMessage, placeholders);
        
        broadcastMessage(BroadcastEvent.BUY_NOW, chatMessage, bossbarMessage, titleMessage, subtitleMessage, actionbarMessage);
    }
    
    /**
     * 广播竞价确认事件
     * @param bidder 出价者名称
     * @param item 拍卖物品
     */
    public void broadcastBidConfirmed(String bidder, AuctionItem item) {
        if (!isEventEnabled(BroadcastEvent.BID_CONFIRMED)) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("调试: 竞价确认广播已禁用或未配置");
            }
            return;
        }
        
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("调试: 正在广播竞价确认消息，玩家: " + bidder + ", 物品ID: " + item.getId());
        }
        
        // 保存要广播的物品信息，用于悬停显示
        this.lastBroadcastItem = item;
        
        // 获取物品名称
        String itemName;
        ItemStack original = item.getItem();
        ItemMeta meta = original.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // 移除颜色代码，让message.yml中的颜色设置生效
            itemName = ChatColor.stripColor(meta.getDisplayName());
        } else {
            // 只有在设置为中文语言时才进行原版物品名称的中文翻译
            if (plugin.getConfigManager().isChineseLanguage()) {
                // 使用与GuiManager相同的逻辑获取中文名称，但不添加颜色代码
                String chineseName = plugin.getLanguageManager().getChineseName(original.getType());
                itemName = (chineseName != null && !chineseName.isEmpty()) ? 
                        chineseName : 
                        ChatColor.stripColor(ChatUtils.getItemName(original));
            } else {
                // 非中文语言环境下使用原版物品名称
                itemName = ChatColor.stripColor(ChatUtils.getItemName(original));
            }
        }
        
        // 添加物品数量显示
        if (original.getAmount() > 1) {
            itemName += " x" + original.getAmount();
        }
        
        // 替换占位符的映射
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%bidder%", bidder);
        placeholders.put("%item_name%", itemName);
        placeholders.put("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType()));
        placeholders.put("%amount%", String.valueOf(item.getItem().getAmount()));
        
        // 获取消息
        String chatMessage = replacePlaceholders(getBroadcastMessage("bid_confirmed", "chat"), placeholders);
        String bossbarMessage = replacePlaceholders(getBroadcastMessage("bid_confirmed", "bossbar"), placeholders);
        String titleMessage = replacePlaceholders(getBroadcastMessage("bid_confirmed", "title"), placeholders);
        String subtitleMessage = replacePlaceholders(getBroadcastMessage("bid_confirmed", "subtitle"), placeholders);
        String actionbarMessage = replacePlaceholders(getBroadcastMessage("bid_confirmed", "actionbar"), placeholders);
        
        // 广播消息
        broadcastMessage(BroadcastEvent.BID_CONFIRMED, chatMessage, bossbarMessage, titleMessage, subtitleMessage, actionbarMessage);
    }
} 