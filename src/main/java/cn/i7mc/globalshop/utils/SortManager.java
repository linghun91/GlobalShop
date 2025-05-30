package cn.i7mc.globalshop.utils;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.enums.SortType;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排序管理器
 * 负责管理拍卖物品的排序功能
 */
public class SortManager {
    private final GlobalShop plugin;
    private final Map<Player, SortType> playerSortTypes;
    
    public SortManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.playerSortTypes = new ConcurrentHashMap<>();
    }
    
    /**
     * 获取玩家当前的排序类型
     * @param player 玩家
     * @return 排序类型
     */
    public SortType getPlayerSortType(Player player) {
        return playerSortTypes.getOrDefault(player, SortType.TIME_ASC);
    }
    
    /**
     * 设置玩家的排序类型
     * @param player 玩家
     * @param sortType 排序类型
     */
    public void setPlayerSortType(Player player, SortType sortType) {
        playerSortTypes.put(player, sortType);
        // 同时设置到玩家元数据中，方便其他地方使用
        player.setMetadata("auction_sort_type", new FixedMetadataValue(plugin, sortType.getKey()));
    }
    
    /**
     * 切换玩家的排序类型到下一个
     * @param player 玩家
     * @return 新的排序类型
     */
    public SortType togglePlayerSortType(Player player) {
        SortType currentType = getPlayerSortType(player);
        SortType nextType = currentType.getNext();
        setPlayerSortType(player, nextType);
        return nextType;
    }
    
    /**
     * 根据排序类型对拍卖物品列表进行排序
     * @param items 拍卖物品列表
     * @param sortType 排序类型
     */
    public void sortItems(List<AuctionItem> items, SortType sortType) {
        Comparator<AuctionItem> comparator = getComparator(sortType);
        items.sort(comparator);
    }
    
    /**
     * 获取排序类型对应的比较器
     * @param sortType 排序类型
     * @return 比较器
     */
    private Comparator<AuctionItem> getComparator(SortType sortType) {
        switch (sortType) {
            case TIME_ASC:
                return Comparator.comparing(AuctionItem::getListTime).reversed(); // 最近上架在前
            case TIME_DESC:
                return Comparator.comparing(AuctionItem::getEndTime); // 即将结束在前
            case PRICE_HIGH:
                return Comparator.comparing(AuctionItem::getCurrentPrice).reversed(); // 价格从高到低
            case PRICE_LOW:
                return Comparator.comparing(AuctionItem::getCurrentPrice); // 价格从低到高
            case NAME:
                return Comparator.comparing(item -> {
                    // 按物品名称排序，优先使用显示名称，否则使用类型名称
                    if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName()) {
                        return item.getItem().getItemMeta().getDisplayName();
                    }
                    return item.getItem().getType().toString();
                });
            default:
                return Comparator.comparing(AuctionItem::getListTime).reversed();
        }
    }
    
    /**
     * 获取排序类型的显示名称（从配置文件获取）
     * @param sortType 排序类型
     * @return 显示名称
     */
    public String getSortDisplayName(SortType sortType) {
        switch (sortType) {
            case TIME_ASC:
                return plugin.getMessageManager().getSortByTimeAscText();
            case TIME_DESC:
                return plugin.getMessageManager().getSortByTimeDescText();
            case PRICE_HIGH:
                return plugin.getMessageManager().getSortByPriceHighText();
            case PRICE_LOW:
                return plugin.getMessageManager().getSortByPriceLowText();
            case NAME:
                return plugin.getMessageManager().getSortByNameText();
            default:
                return plugin.getMessageManager().getSortByTimeAscText();
        }
    }
    
    /**
     * 获取排序的SQL ORDER BY子句
     * @param sortType 排序类型
     * @return SQL ORDER BY子句
     */
    public String getSqlOrderBy(SortType sortType) {
        switch (sortType) {
            case TIME_ASC:
                return "ORDER BY list_time DESC"; // 最近上架在前
            case TIME_DESC:
                return "ORDER BY end_time ASC"; // 即将结束在前
            case PRICE_HIGH:
                return "ORDER BY current_price DESC"; // 价格从高到低
            case PRICE_LOW:
                return "ORDER BY current_price ASC"; // 价格从低到高
            case NAME:
                return "ORDER BY item_data ASC"; // 按物品数据排序（简化处理）
            default:
                return "ORDER BY list_time DESC";
        }
    }
    
    /**
     * 清除玩家的排序数据
     * @param player 玩家
     */
    public void clearPlayerData(Player player) {
        playerSortTypes.remove(player);
        if (player.hasMetadata("auction_sort_type")) {
            player.removeMetadata("auction_sort_type", plugin);
        }
    }
}
