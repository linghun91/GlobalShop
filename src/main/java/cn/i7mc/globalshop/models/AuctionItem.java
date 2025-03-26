package cn.i7mc.globalshop.models;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionItem {
    private final int id;
    private final UUID sellerUuid;
    private final String sellerName;
    private final ItemStack item;
    private final double startPrice;
    private final double buyNowPrice;
    private double currentPrice;
    private final String currencyType;
    private final long listTime;    // 上架时间
    private final long startTime;
    private final long endTime;
    private long soldTime;          // 实际售出时间
    private String status;
    private UUID currentBidder;     // 当前出价者UUID
    private String currentBidderName; // 当前出价者名字

    public AuctionItem(int id, UUID sellerUuid, String sellerName, ItemStack item,
                      double startPrice, double buyNowPrice, double currentPrice,
                      String currencyType, long startTime, long endTime, String status) {
        this.id = id;
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item;
        this.startPrice = startPrice;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.currencyType = currencyType;
        this.listTime = System.currentTimeMillis(); // 默认为当前时间
        this.startTime = startTime;
        this.endTime = endTime;
        this.soldTime = 0; // 初始为0，表示未售出
        this.status = status;
        this.currentBidderName = null;
    }
    
    // 完整构造函数，包含所有字段
    public AuctionItem(int id, UUID sellerUuid, String sellerName, ItemStack item,
                      double startPrice, double buyNowPrice, double currentPrice,
                      String currencyType, long listTime, long startTime, long endTime, 
                      long soldTime, String status) {
        this.id = id;
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item;
        this.startPrice = startPrice;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.currencyType = currencyType;
        this.listTime = listTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.soldTime = soldTime;
        this.status = status;
        this.currentBidderName = null;
    }

    public int getId() {
        return id;
    }

    public UUID getSellerUuid() {
        return sellerUuid;
    }

    public String getSellerName() {
        return sellerName;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public double getBuyNowPrice() {
        return buyNowPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCurrencyType() {
        return currencyType;
    }
    
    public long getListTime() {
        return listTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
    
    public long getSoldTime() {
        return soldTime;
    }
    
    public void setSoldTime(long soldTime) {
        this.soldTime = soldTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }

    public boolean hasBuyNowPrice() {
        return buyNowPrice > 0;
    }

    public boolean canBuyNow() {
        return hasBuyNowPrice() && (currentBidder == null || currentPrice < buyNowPrice);
    }

    public boolean canBid() {
        return isActive() && !isExpired();
    }

    public String getFormattedRemainingTime() {
        long remainingMillis = endTime - System.currentTimeMillis();
        if (remainingMillis <= 0) return "已过期";
        
        long seconds = remainingMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("天");
        if (hours > 0) sb.append(hours).append("小时");
        if (minutes > 0) sb.append(minutes).append("分钟");
        if (seconds > 0) sb.append(seconds).append("秒");
        
        return sb.toString();
    }

    // 获取当前出价者UUID
    public UUID getCurrentBidder() {
        return currentBidder;
    }

    // 设置当前出价者UUID
    public void setCurrentBidder(UUID currentBidder) {
        this.currentBidder = currentBidder;
    }
    
    // 获取当前出价者名字
    public String getCurrentBidderName() {
        return currentBidderName;
    }
    
    // 设置当前出价者名字
    public void setCurrentBidderName(String currentBidderName) {
        this.currentBidderName = currentBidderName;
    }
    
    // 获取格式化的列表时间
    public String getFormattedListTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(listTime));
    }
    
    // 获取格式化的售出时间
    public String getFormattedSoldTime() {
        if (soldTime <= 0) return "未售出";
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(soldTime));
    }
    
    /**
     * 获取物品显示名称
     * @return 物品显示名称，如果没有则返回物品类型名称
     */
    public String getDisplayName() {
        if (item == null) {
            return "未知物品";
        }
        
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        } else {
            // 返回物品类型名称
            return item.getType().toString().toLowerCase().replace("_", " ");
        }
    }
} 