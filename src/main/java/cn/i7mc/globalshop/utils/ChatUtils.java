package cn.i7mc.globalshop.utils;

<<<<<<< HEAD
import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.config.MessageManager;
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 聊天和文本工具类
 */
public class ChatUtils {
    
    /**
     * 获取物品的显示名称，如果没有则返回物品类型名称
     * 
     * @param item 物品堆栈
     * @return 物品名称
     */
    public static String getItemName(ItemStack item) {
<<<<<<< HEAD
        MessageManager messageManager = GlobalShop.getInstance().getMessageManager();
        
        if (item == null) {
            return messageManager.getUnknownItemText();
=======
        if (item == null) {
            return "未知物品";
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        
        // 将物品类型转换为更易读的格式
        String typeName = item.getType().toString();
        typeName = typeName.toLowerCase().replace('_', ' ');
        
        // 首字母大写处理
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : typeName.toCharArray()) {
            if (c == ' ') {
                result.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 格式化时间（秒）为易读的格式
     * 
     * @param seconds 秒数
     * @return 格式化后的时间字符串
     */
    public static String formatTime(long seconds) {
<<<<<<< HEAD
        MessageManager messageManager = GlobalShop.getInstance().getMessageManager();
        
        if (seconds < 60) {
            return seconds + messageManager.getTimeSecondText();
        } else if (seconds < 3600) {
            return (seconds / 60) + messageManager.getTimeMinuteText() + 
                   (seconds % 60 > 0 ? " " + (seconds % 60) + messageManager.getTimeSecondText() : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + messageManager.getTimeHourText() + 
                   (minutes > 0 ? " " + minutes + messageManager.getTimeMinuteText() : "");
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + messageManager.getTimeDayText() + 
                   (hours > 0 ? " " + hours + messageManager.getTimeHourText() : "");
=======
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分钟" + (seconds % 60 > 0 ? " " + (seconds % 60) + "秒" : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "小时" + (minutes > 0 ? " " + minutes + "分钟" : "");
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "天" + (hours > 0 ? " " + hours + "小时" : "");
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        }
    }

    /**
     * 记录拍卖物品调试信息
     * @param plugin 插件实例
     * @param item 拍卖物品
     * @param action 操作描述
     */
    public static void logAuctionItemDebug(JavaPlugin plugin, AuctionItem item, String action) {
        // 调试日志已被移除
        // 如果需要详细日志，请在配置中启用调试模式
    }
} 