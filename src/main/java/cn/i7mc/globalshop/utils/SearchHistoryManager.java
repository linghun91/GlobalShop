package cn.i7mc.globalshop.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索历史管理类，负责存储和管理玩家的搜索历史
 */
public class SearchHistoryManager {
    private final Map<UUID, List<String>> searchHistory;
    private final int maxHistorySize;

    /**
     * 构造一个搜索历史管理器
     * 
     * @param maxHistorySize 每个玩家最大搜索历史记录数
     */
    public SearchHistoryManager(int maxHistorySize) {
        this.searchHistory = new ConcurrentHashMap<>();
        this.maxHistorySize = maxHistorySize > 0 ? maxHistorySize : 10;
    }

    /**
     * 添加搜索历史
     * 
     * @param player 玩家
     * @param query 搜索关键词
     */
    public void addSearchHistory(Player player, String query) {
        if (player == null || query == null || query.trim().isEmpty()) {
            return;
        }
        
        UUID playerUuid = player.getUniqueId();
        List<String> history = searchHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>());
        
        // 如果历史记录中已有相同查询，先移除
        history.remove(query);
        
        // 添加到历史记录开头
        history.add(0, query);
        
        // 如果超出最大记录数，移除最旧的
        if (history.size() > maxHistorySize) {
            history.remove(history.size() - 1);
        }
    }

    /**
     * 获取玩家的搜索历史
     * 
     * @param player 玩家
     * @return 搜索历史列表
     */
    public List<String> getSearchHistory(Player player) {
        if (player == null) {
            return new ArrayList<>();
        }
        
        return searchHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    /**
     * 清除玩家的搜索历史
     * 
     * @param player 玩家
     */
    public void clearSearchHistory(Player player) {
        if (player != null) {
            searchHistory.remove(player.getUniqueId());
        }
    }

    /**
     * 清除所有玩家的搜索历史
     */
    public void clearAllSearchHistory() {
        searchHistory.clear();
    }
} 