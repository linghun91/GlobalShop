package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 拍卖历史记录管理器
 * 负责存储和管理拍卖历史事件
 */
public class AuctionHistoryManager {
    
    private final GlobalShop plugin;
    private final LinkedList<AuctionHistoryEvent> historyEvents;
    private final ReadWriteLock lock;
    private final int maxHistorySize;
    
    /**
     * 构造函数
     * @param plugin 插件实例
     * @param maxHistorySize 最大历史记录数量
     */
    public AuctionHistoryManager(GlobalShop plugin, int maxHistorySize) {
        this.plugin = plugin;
        this.historyEvents = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock();
        this.maxHistorySize = maxHistorySize > 0 ? maxHistorySize : 50;
    }
    
    /**
     * 添加物品上架事件
     * @param player 上架玩家
     * @param item 拍卖物品
     */
    public void addListEvent(Player player, AuctionItem item) {
        AuctionHistoryEvent event = new AuctionHistoryEvent(
                AuctionEventType.LIST,
                player.getName(),
                null,
                item,
                System.currentTimeMillis()
        );
        addEvent(event);
    }
    
    /**
     * 添加竞价更新事件
     * @param bidder 竞价玩家
     * @param item 拍卖物品
     */
    public void addBidEvent(Player bidder, AuctionItem item) {
        AuctionHistoryEvent event = new AuctionHistoryEvent(
                AuctionEventType.BID,
                item.getSellerName(),
                bidder.getName(),
                item,
                System.currentTimeMillis()
        );
        addEvent(event);
    }
    
    /**
     * 添加物品购买事件
     * @param buyer 购买玩家
     * @param item 拍卖物品
     */
    public void addBuyEvent(Player buyer, AuctionItem item) {
        AuctionHistoryEvent event = new AuctionHistoryEvent(
                AuctionEventType.BUY,
                item.getSellerName(),
                buyer.getName(),
                item,
                System.currentTimeMillis()
        );
        addEvent(event);
    }
    
    /**
     * 添加物品过期事件
     * @param item 拍卖物品
     */
    public void addExpiredEvent(AuctionItem item) {
        AuctionHistoryEvent event = new AuctionHistoryEvent(
                AuctionEventType.EXPIRED,
                item.getSellerName(),
                null,
                item,
                System.currentTimeMillis()
        );
        addEvent(event);
    }
    
    /**
     * 添加物品取消事件
     * @param player 取消玩家（可能是管理员）
     * @param item 拍卖物品
     */
    public void addCancelledEvent(Player player, AuctionItem item) {
        AuctionHistoryEvent event = new AuctionHistoryEvent(
                AuctionEventType.CANCELLED,
                item.getSellerName(),
                player.getName(),
                item,
                System.currentTimeMillis()
        );
        addEvent(event);
    }
    
    /**
     * 添加事件到历史记录
     * @param event 事件对象
     */
    private void addEvent(AuctionHistoryEvent event) {
        lock.writeLock().lock();
        try {
            historyEvents.addFirst(event); // 新事件添加到头部
            
            // 如果超过最大记录数，移除最旧的记录
            while (historyEvents.size() > maxHistorySize) {
                historyEvents.removeLast();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取最近的历史记录
     * @param count 获取数量
     * @return 历史记录列表
     */
    public List<AuctionHistoryEvent> getRecentEvents(int count) {
        List<AuctionHistoryEvent> events = new ArrayList<>();
        lock.readLock().lock();
        try {
            int actualCount = Math.min(count, historyEvents.size());
            for (int i = 0; i < actualCount; i++) {
                events.add(historyEvents.get(i));
            }
        } finally {
            lock.readLock().unlock();
        }
        return events;
    }
    
    /**
     * 清空历史记录
     */
    public void clearHistory() {
        lock.writeLock().lock();
        try {
            historyEvents.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取事件类型显示文本
     * @param eventType 事件类型
     * @return 显示文本
     */
    public String getEventTypeText(AuctionEventType eventType) {
        switch (eventType) {
            case LIST:
                return ChatColor.GREEN + "上架";
            case BID:
                return ChatColor.AQUA + "竞价更新";
            case BUY:
                return ChatColor.GOLD + "被购买";
            case EXPIRED:
                return ChatColor.RED + "已到期";
            case CANCELLED:
                return ChatColor.GRAY + "已取消";
            default:
                return ChatColor.WHITE + "未知";
        }
    }
    
    /**
     * 拍卖事件类型枚举
     */
    public enum AuctionEventType {
        LIST,       // 上架
        BID,        // 竞价
        BUY,        // 购买
        EXPIRED,    // 过期
        CANCELLED   // 取消
    }
    
    /**
     * 拍卖历史事件类
     */
    public class AuctionHistoryEvent {
        private final AuctionEventType eventType;
        private final String sellerName;
        private final String buyerName;
        private final AuctionItem item;
        private final long timestamp;
        private final ItemStack displayItem;
        
        /**
         * 构造函数
         * @param eventType 事件类型
         * @param sellerName 卖家名称
         * @param buyerName 买家名称
         * @param item 拍卖物品
         * @param timestamp 时间戳
         */
        public AuctionHistoryEvent(AuctionEventType eventType, String sellerName, 
                                  String buyerName, AuctionItem item, long timestamp) {
            this.eventType = eventType;
            this.sellerName = sellerName;
            this.buyerName = buyerName;
            this.item = item;
            this.timestamp = timestamp;
            this.displayItem = item.getItem().clone();
        }
        
        public AuctionEventType getEventType() {
            return eventType;
        }
        
        public String getSellerName() {
            return sellerName;
        }
        
        public String getBuyerName() {
            return buyerName;
        }
        
        public AuctionItem getItem() {
            return item;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public ItemStack getDisplayItem() {
            return displayItem;
        }
        
        /**
         * 获取格式化的事件文本
         * @return 格式化文本
         */
        public String getFormattedText() {
            StringBuilder sb = new StringBuilder();
            
            // 物品名称 - 尝试从物品中获取显示名称
            String itemName = item.getItem().getItemMeta() != null && item.getItem().getItemMeta().hasDisplayName() 
                    ? item.getItem().getItemMeta().getDisplayName() 
                    : item.getItem().getType().toString();
            sb.append(ChatColor.GOLD).append(itemName);
            
            // 卖家名称
            sb.append(" ").append(ChatColor.GREEN).append(sellerName);
            
            // 事件类型
            sb.append(" ").append(getEventTypeText(eventType));
            
            // 货币类型和价格
            String currencyType = "1".equals(item.getCurrencyType()) ? "金币" : "点券";
            double price = item.getCurrentPrice();
            sb.append(" ").append(ChatColor.YELLOW).append(currencyType).append(" ").append(price);
            
            // 买家名称（如果有）
            if (buyerName != null && !buyerName.isEmpty()) {
                sb.append(" ").append(ChatColor.AQUA).append(buyerName);
            }
            
            return sb.toString();
        }
    }
} 