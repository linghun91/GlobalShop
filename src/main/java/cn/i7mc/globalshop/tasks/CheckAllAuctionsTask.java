package cn.i7mc.globalshop.tasks;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查所有拍卖物品过期状态的任务类
 * 用于管理员手动触发检查过期物品，强制处理可能被系统遗漏的过期物品
 */
public class CheckAllAuctionsTask implements Runnable {
    private final GlobalShop plugin;
    private final Player admin; // 执行命令的管理员

    public CheckAllAuctionsTask(GlobalShop plugin, Player admin) {
        this.plugin = plugin;
        this.admin = admin;
    }

    @Override
    public void run() {
        // 获取所有活跃的拍卖物品
        List<AuctionItem> activeItems = plugin.getDatabaseManager().getAllActiveAuctionItems();
        
        if (activeItems.isEmpty()) {
            admin.sendMessage(ChatColor.YELLOW + "当前没有活跃的拍卖物品需要检查。");
            return;
        }
        
        // 筛选出已过期的物品，确保只处理状态为ACTIVE的物品
        List<AuctionItem> expiredItems = activeItems.stream()
                .filter(item -> "ACTIVE".equals(item.getStatus()) && item.isExpired())
                .collect(Collectors.toList());
        
        if (expiredItems.isEmpty()) {
            admin.sendMessage(ChatColor.GREEN + "检查完成，没有发现过期但未处理的物品。");
            return;
        }
        admin.sendMessage(ChatColor.YELLOW + "发现 " + expiredItems.size() + " 个过期但未处理的物品，正在处理...");
        
        // 创建AuctionTask实例并手动处理每个过期物品
        AuctionTask auctionTask = new AuctionTask(plugin);
        int processedCount = 0;
        
        for (AuctionItem item : expiredItems) {
            try {
                // 再次检查物品状态，防止并发处理
                if (!"ACTIVE".equals(item.getStatus())) {
                    continue;
                }
                
                // 使用反射调用私有方法
                java.lang.reflect.Method method = AuctionTask.class.getDeclaredMethod("handleExpiredAuction", AuctionItem.class);
                method.setAccessible(true);
                method.invoke(auctionTask, item);
                processedCount++;
            } catch (Exception e) {
                e.printStackTrace();
                admin.sendMessage(ChatColor.RED + "处理物品ID: " + item.getId() + " 时出错，请查看控制台日志。");
            }
        }
        
        admin.sendMessage(ChatColor.GREEN + "过期物品处理完成，共处理 " + processedCount + " 个物品。");
    }
} 