package cn.i7mc.globalshop.tasks;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 关闭所有拍卖的任务类
 * 用于管理员强制关闭所有活跃拍卖，测试过期物品处理机制
 */
public class CloseAllAuctionsTask implements Runnable {
    private final GlobalShop plugin;
    private final Player admin; // 执行命令的管理员

    public CloseAllAuctionsTask(GlobalShop plugin, Player admin) {
        this.plugin = plugin;
        this.admin = admin;
    }

    @Override
    public void run() {
        // 获取所有活跃的拍卖物品
        List<AuctionItem> activeItems = plugin.getDatabaseManager().getAllActiveAuctionItems();
        
        if (activeItems.isEmpty()) {
            admin.sendMessage(ChatColor.YELLOW + "当前没有活跃的拍卖物品需要关闭。");
            return;
        }
        
        admin.sendMessage(ChatColor.YELLOW + "正在处理 " + activeItems.size() + " 个活跃拍卖物品...");
        
        int successCount = 0;
        int failCount = 0;
        
        // 创建AuctionTask实例，用于处理拍卖结束逻辑
        AuctionTask auctionTask = new AuctionTask(plugin);
        
        // 处理每个活跃的拍卖物品
        for (AuctionItem item : activeItems) {
            try {
                // 如果物品已经有出价者，则保持原状态，让定时任务正常处理
                // 如果物品没有出价者，则将状态设置为EXPIRED，让物品返回给卖家
                if (item.getCurrentBidder() == null) {
                    item.setStatus("EXPIRED");
                    boolean updated = plugin.getDatabaseManager().updateAuctionItem(item);
                    
                    if (updated) {
                        // 如果状态更新成功，处理物品返还
                        handleItemReturn(item);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } else {
                }
            } catch (Exception e) {
                failCount++;
                e.printStackTrace();
            }
        }
        
        // 发送处理结果给管理员
        admin.sendMessage(ChatColor.GREEN + "已处理 " + activeItems.size() + " 个拍卖物品:");
        admin.sendMessage(ChatColor.GREEN + "- 成功: " + successCount);
        if (failCount > 0) {
            admin.sendMessage(ChatColor.RED + "- 失败: " + failCount + " (详情请查看控制台日志)");
        }
        
        // 将刷新玩家界面的操作放在主线程中执行
        final int finalSuccessCount = successCount;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            // 刷新在线玩家的拍卖界面
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory() != null && 
                    player.getOpenInventory().getTitle() != null && 
                    player.getOpenInventory().getTitle().contains("拍卖")) {
                    // 如果玩家正在查看拍卖相关界面，刷新
                    player.closeInventory();
                    player.sendMessage(ChatColor.YELLOW + "拍卖行物品状态已更新，界面已关闭。");
                }
            }
            
            // 在主线程中再次通知管理员操作完成
            if (finalSuccessCount > 0) {
                admin.sendMessage(ChatColor.GREEN + "所有界面已刷新完成！");
            }
        });
    }
    
    /**
     * 处理物品返还给卖家逻辑
     */
    private void handleItemReturn(AuctionItem item) {
        Player seller = Bukkit.getPlayer(item.getSellerUuid());
        if (seller != null && seller.isOnline()) {
            // 卖家在线，直接通知
            seller.sendMessage(ChatColor.YELLOW + "你的拍卖物品已被系统强制下架，可在已过期界面查看。");
        }
        
        // 记录日志
        String logMessage = "物品 ID " + item.getId() + " 已设置为过期状态，卖家: " + 
                  item.getSellerName() + " (UUID: " + item.getSellerUuid() + ")";
        plugin.getLogger().info(logMessage);
    }
} 