package cn.i7mc.globalshop.tasks;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import cn.i7mc.globalshop.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionTask extends BukkitRunnable {
    private final GlobalShop plugin;

    public AuctionTask(GlobalShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // 获取所有过期的拍卖
        List<AuctionItem> expiredItems = plugin.getDatabaseManager().getExpiredAuctionItems();
        if (expiredItems.isEmpty()) return;
        for (AuctionItem item : expiredItems) {
            handleExpiredAuction(item);
        }
    }

    private void handleExpiredAuction(AuctionItem item) {
        // 拍卖已结束，更新状态
        if (item.isActive()) {
            if (item.getCurrentBidder() != null) {
                // 有出价者，拍卖成功
                handleSuccessfulAuction(item);
            } else {
                // 没有出价者，拍卖失败
                handleFailedAuction(item);
            }
        }
    }

    private void handleSuccessfulAuction(AuctionItem item) {
        // 获取买家和卖家
        UUID buyerUUID = item.getCurrentBidder();
        UUID sellerUUID = item.getSellerUuid();
        
        // 更新拍卖状态为已售出
        item.setStatus("SOLD");
        
        // 记录当前时间作为实际售出时间
        long soldTime = System.currentTimeMillis();
        item.setSoldTime(soldTime);
        
        // 记录买家名称
        if (buyerUUID != null) {
            String buyerName = Bukkit.getOfflinePlayer(buyerUUID).getName();
            if (buyerName != null && !buyerName.isEmpty()) {
                item.setCurrentBidderName(buyerName);
            } else {
                // 如果无法获取名称，至少使用UUID的前缀
                String uuidPrefix = buyerUUID.toString().substring(0, 8) + "...";
                item.setCurrentBidderName(uuidPrefix);
            }
        }
        
        // 更新数据库
        boolean updated = plugin.getDatabaseManager().updateAuctionItem(item);
        if (!updated) {
        }
        
        // 记录拍卖历史购买事件
        Player buyer = Bukkit.getPlayer(buyerUUID);
        if (buyer != null && buyer.isOnline()) {
            plugin.getAuctionHistoryManager().addBuyEvent(buyer, item);
        }
        
        // 给卖家货币（扣除手续费）
        double sellerAmount = item.getCurrentPrice() - 
                plugin.getEconomyManager().calculateFee(item.getCurrentPrice(), item.getCurrencyType());
        plugin.getEconomyManager().giveMoney(Bukkit.getOfflinePlayer(sellerUUID), sellerAmount, item.getCurrencyType());
        
        // 广播竞拍成功消息
        String buyerName = item.getCurrentBidderName() != null ? item.getCurrentBidderName() : 
                          (Bukkit.getOfflinePlayer(buyerUUID).getName() != null ? 
                           Bukkit.getOfflinePlayer(buyerUUID).getName() : buyerUUID.toString().substring(0, 8));
        plugin.getBroadcastManager().broadcastAuctionWon(buyerName, item.getSellerName(), item);
        
        // 所有竞拍成功的物品都直接放入邮箱，不管玩家是否在线
        addToMailbox(item, buyerUUID, "AUCTION_WON");
        
        // 通知买家
        Player buyerPlayer = Bukkit.getPlayer(buyerUUID);
        if (buyerPlayer != null && buyerPlayer.isOnline()) {
            // 使用MessageManager获取消息
            buyerPlayer.sendMessage(plugin.getMessageManager().getBuyerWinSuccessMessage());
            buyerPlayer.sendMessage(plugin.getMessageManager().getBuyerWinItemMessage(ChatUtils.getItemName(item.getItem())));
            buyerPlayer.sendMessage(plugin.getMessageManager().getBuyerWinPriceMessage(
                    plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType())));
            buyerPlayer.sendMessage(plugin.getMessageManager().getBuyerWinCollectGuideMessage());
        }
        
        // 通知卖家
        Player sellerPlayer = Bukkit.getPlayer(sellerUUID);
        if (sellerPlayer != null && sellerPlayer.isOnline()) {
            // 使用MessageManager获取消息
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerWinSuccessMessage());
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerWinItemMessage(ChatUtils.getItemName(item.getItem())));
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerWinPriceMessage(
                    plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType())));
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerWinIncomeMessage(
                    plugin.getEconomyManager().formatAmount(sellerAmount, item.getCurrencyType())));
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerWinBuyerMessage(item.getCurrentBidderName()));
        }
    }

    /**
     * 更新拍卖物品的售出时间和状态
     * @param itemId 物品ID
     * @param soldTime 售出时间
     * @return 是否成功更新
     */
    private boolean updateAuctionItemSoldTime(int itemId, long soldTime) {
        // 查看当前物品的信息
        AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
        if (item == null) {
            return false;
        }
        // 使用更精确的SQL查询，同时设置状态和结束时间
        String sql = "UPDATE auction_items SET status = 'SOLD', end_time = ? WHERE id = ?";
        
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            if (conn == null) {
                return false;
            }
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            try {
                pstmt.setLong(1, soldTime);
                pstmt.setInt(2, itemId);
                
                int affectedRows = pstmt.executeUpdate();
                boolean success = affectedRows > 0;
                
                if (success) {
                } else {
                }
                
                return success;
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private void handleFailedAuction(AuctionItem item) {
        // 获取卖家
        UUID sellerUUID = item.getSellerUuid();
        
        // 检查物品是否确实没有出价者
        if (item.getCurrentBidder() != null) {
            // 应该使用handleSuccessfulAuction处理这个物品
            handleSuccessfulAuction(item);
            return;
        }
        
        // 安全检查：确保物品状态为ACTIVE，防止重复处理
        if (!"ACTIVE".equals(item.getStatus())) {
            return;
        }
        
        // 更新拍卖状态
        item.setStatus("EXPIRED");
        boolean updated = plugin.getDatabaseManager().updateAuctionItem(item);
        
        if (!updated) {
            return;
        }
        
        // 记录拍卖历史过期事件
        plugin.getAuctionHistoryManager().addExpiredEvent(item);
        
        // 将过期物品放入邮箱（使用统一方法处理）
        directAddToMailbox(item, sellerUUID);
        
        // 通知卖家
        Player sellerPlayer = Bukkit.getPlayer(sellerUUID);
        if (sellerPlayer != null && sellerPlayer.isOnline()) {
            // 使用MessageManager获取消息
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerExpiredNoticeMessage());
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerExpiredItemMessage(ChatUtils.getItemName(item.getItem())));
            sellerPlayer.sendMessage(plugin.getMessageManager().getSellerExpiredCollectGuideMessage());
        }
    }
    
    // 新增方法：直接设置物品状态为邮箱物品，而不是添加到待领取列表
    private void directAddToMailbox(AuctionItem item, UUID ownerUuid) {
        String itemName = item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName() ? 
                item.getItem().getItemMeta().getDisplayName() : item.getItem().getType().toString();
    }

    // 添加邮箱物品的统一处理方法
    private void addToMailbox(AuctionItem item, UUID ownerUuid, String reason) {
        // 增强日志记录，追踪物品流向
        String rawItemName = item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName() ? 
                item.getItem().getItemMeta().getDisplayName() : item.getItem().getType().toString();
        ItemStack itemStack = item.getItem().clone();
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        
        // 添加邮箱标记和来源信息
        lore.add(plugin.getMessageManager().getAuctionItemDivider());
        
        if ("AUCTION_WON".equals(reason)) {
            // 使用MessageManager中的国际化文本
            lore.add(plugin.getMessageManager().getMailboxStorageWon());
            lore.add(plugin.getMessageManager().getAuctionCurrentPriceFormat()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getCurrentPrice(), item.getCurrencyType())));
            lore.add(plugin.getMessageManager().getMailboxItemAddTime() 
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("AUCTION_EXPIRED".equals(reason)) {
            // 使用MessageManager中的国际化文本
            lore.add(plugin.getMessageManager().getMailboxStorageExpired());
            lore.add(plugin.getMessageManager().getMailboxItemExpireTime()
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        } else if ("BUY_NOW".equals(reason)) {
            // 使用MessageManager中的国际化文本
            lore.add(plugin.getMessageManager().getMailboxStorageFull());
            lore.add(plugin.getMessageManager().getAuctionDealPriceFormat()
                    .replace("%price%", plugin.getEconomyManager().formatAmount(item.getBuyNowPrice(), item.getCurrencyType())));
            lore.add(plugin.getMessageManager().getMailboxItemAddTime()
                    + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        }
        
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        
        // 存储到数据库
        boolean success = plugin.getDatabaseManager().storePendingItem(ownerUuid, itemStack, reason);
        
        // 记录物品添加到邮箱的最终结果
        if (success) {
            // 日志记录
        } else {
            // 日志记录
        }
    }
} 