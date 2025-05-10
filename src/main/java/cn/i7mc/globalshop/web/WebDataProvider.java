package cn.i7mc.globalshop.web;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Web数据提供者
 * 负责从数据库获取拍卖数据并转换为Web可用格式
 */
public class WebDataProvider {
    private final GlobalShop plugin;
    private final WebConfig webConfig;
    private final SimpleDateFormat dateFormat;
    private long lastUpdateTime;

    /**
     * 构造函数
     * @param plugin 插件实例
     * @param webConfig Web配置
     */
    public WebDataProvider(GlobalShop plugin, WebConfig webConfig) {
        this.plugin = plugin;
        this.webConfig = webConfig;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 获取所有活跃的拍卖物品数据（JSON格式）
     * @return 拍卖物品数据的JSON字符串
     */
    public String getActiveAuctionItemsJson() {
        List<Map<String, Object>> itemsData = getActiveAuctionItemsData();
        return convertToJson(itemsData);
    }

    /**
     * 获取所有活跃的拍卖物品数据
     * @return 拍卖物品数据列表
     */
    public List<Map<String, Object>> getActiveAuctionItemsData() {
        List<AuctionItem> items = plugin.getDatabaseManager().getAllActiveAuctionItems();
        List<Map<String, Object>> itemsData = new ArrayList<>();

        for (AuctionItem item : items) {
            Map<String, Object> itemData = new HashMap<>();
            ItemStack itemStack = item.getItem();

            // 基本信息
            itemData.put("id", item.getId());
            itemData.put("sellerName", item.getSellerName());

            // 物品信息
            String itemName = getItemName(itemStack);
            itemData.put("itemName", itemName);
            itemData.put("itemType", itemStack.getType().toString());
            itemData.put("itemAmount", itemStack.getAmount());

            // 价格信息
            itemData.put("startPrice", item.getStartPrice());
            itemData.put("currentPrice", item.getCurrentPrice());
            itemData.put("buyNowPrice", item.hasBuyNowPrice() ? item.getBuyNowPrice() : -1);
            itemData.put("currencyType", item.getCurrencyType());
            itemData.put("currencySymbol", getCurrencySymbol(item.getCurrencyType()));

            // 竞价信息
            itemData.put("hasBidder", item.getCurrentBidder() != null);
            itemData.put("currentBidder", item.getCurrentBidderName() != null ? item.getCurrentBidderName() : "");

            // 时间信息
            itemData.put("listTime", dateFormat.format(new Date(item.getListTime())));
            itemData.put("endTime", dateFormat.format(new Date(item.getEndTime())));

            // 剩余时间（毫秒）
            long remainingTime = item.getEndTime() - System.currentTimeMillis();
            itemData.put("remainingTimeMs", Math.max(0, remainingTime));

            // 剩余时间（格式化）
            itemData.put("remainingTimeFormatted", formatRemainingTime(remainingTime));

            // 状态
            itemData.put("status", item.getStatus());
            itemData.put("isExpired", item.isExpired());

            itemsData.add(itemData);
        }

        // 更新最后更新时间
        this.lastUpdateTime = System.currentTimeMillis();

        return itemsData;
    }

    /**
     * 获取物品名称
     * @param itemStack 物品堆
     * @return 物品名称
     */
    private String getItemName(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                // 使用Adventure API获取显示名称（避免使用已弃用的方法）
                if (meta.displayName() != null) {
                    // 将Adventure组件转换为纯文本
                    return extractPlainText(meta.displayName());
                }
            }
        }
        return formatItemType(itemStack.getType().toString());
    }

    /**
     * 从Adventure组件中提取纯文本内容
     * @param component Adventure组件
     * @return 纯文本内容
     */
    private String extractPlainText(net.kyori.adventure.text.Component component) {
        if (component == null) {
            return "";
        }

        // 使用Adventure API的PlainTextComponentSerializer获取纯文本
        try {
            // 导入必要的类
            net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer serializer =
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText();

            // 将组件序列化为纯文本
            String plainText = serializer.serialize(component);

            // 处理颜色代码
            return translateColorCodes(plainText);
        } catch (Exception e) {
            // 如果出现异常，使用备用方法
            return translateColorCodes(component.toString());
        }
    }

    /**
     * 转换Minecraft颜色代码为纯文本
     * @param text 包含颜色代码的文本
     * @return 转换后的文本
     */
    private String translateColorCodes(String text) {
        if (text == null) {
            return "";
        }

        // 移除所有Minecraft颜色代码（§ 和 & 开头的颜色代码）
        // 使用更精确的正则表达式匹配所有可能的颜色代码
        String result = text;
        result = result.replaceAll("§[0-9a-fklmnor]", "");
        result = result.replaceAll("&[0-9a-fklmnor]", "");

        return result;
    }

    /**
     * 格式化物品类型名称
     * @param itemType 物品类型
     * @return 格式化后的物品类型名称
     */
    private String formatItemType(String itemType) {
        String[] parts = itemType.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                      .append(part.substring(1))
                      .append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * 获取货币符号
     * @param currencyType 货币类型
     * @return 货币符号
     */
    private String getCurrencySymbol(String currencyType) {
        if ("POINTS".equals(currencyType)) {
            return translateColorCodes(plugin.getConfigManager().getPointsSymbol());
        } else {
            return translateColorCodes(plugin.getConfigManager().getVaultSymbol());
        }
    }

    /**
     * 格式化剩余时间
     * @param remainingTimeMs 剩余时间（毫秒）
     * @return 格式化后的剩余时间
     */
    private String formatRemainingTime(long remainingTimeMs) {
        if (remainingTimeMs <= 0) {
            return plugin.getMessageManager().getMessages().getString("auction_item_format.time.expired", "已过期");
        }

        long seconds = remainingTimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append(plugin.getMessageManager().getMessages().getString("auction_item_format.time.day", "天")).append(" ");
        }

        if (hours > 0 || days > 0) {
            sb.append(hours).append(plugin.getMessageManager().getMessages().getString("auction_item_format.time.hour", "小时")).append(" ");
        }

        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append(plugin.getMessageManager().getMessages().getString("auction_item_format.time.minute", "分钟")).append(" ");
        }

        sb.append(seconds).append(plugin.getMessageManager().getMessages().getString("auction_item_format.time.second", "秒"));

        return sb.toString();
    }

    /**
     * 将数据转换为JSON字符串
     * @param data 数据
     * @return JSON字符串
     */
    private String convertToJson(Object data) {
        StringBuilder json = new StringBuilder();

        if (data instanceof List) {
            json.append("[");
            List<?> list = (List<?>) data;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                json.append(convertToJson(list.get(i)));
            }
            json.append("]");
        } else if (data instanceof Map) {
            json.append("{");
            Map<?, ?> map = (Map<?, ?>) data;
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                first = false;
                json.append("\"").append(entry.getKey()).append("\":");
                json.append(convertToJson(entry.getValue()));
            }
            json.append("}");
        } else if (data instanceof String) {
            json.append("\"").append(escapeJsonString((String) data)).append("\"");
        } else if (data instanceof Number || data instanceof Boolean) {
            json.append(data);
        } else if (data == null) {
            json.append("null");
        } else {
            json.append("\"").append(escapeJsonString(data.toString())).append("\"");
        }

        return json.toString();
    }

    /**
     * 转义JSON字符串
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * 获取最后更新时间
     * @return 最后更新时间
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 获取下次更新时间
     * @return 下次更新时间
     */
    public long getNextUpdateTime() {
        return lastUpdateTime + (webConfig.getRefreshInterval() * 1000L);
    }
}
