package cn.i7mc.globalshop.enums;

/**
 * 拍卖物品排序类型枚举
 * 定义了所有可用的排序方式
 */
public enum SortType {
    /**
     * 按时间升序排序 - 最近上架
     */
    TIME_ASC("time_asc", "最近上架"),
    
    /**
     * 按时间降序排序 - 即将结束
     */
    TIME_DESC("time_desc", "即将结束"),
    
    /**
     * 按价格降序排序 - 最高价格
     */
    PRICE_HIGH("price_high", "最高价格"),
    
    /**
     * 按价格升序排序 - 最低价格
     */
    PRICE_LOW("price_low", "最低价格"),
    
    /**
     * 按物品名称排序
     */
    NAME("name", "物品名称");
    
    private final String key;
    private final String displayName;
    
    SortType(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }
    
    /**
     * 获取排序类型的键值
     * @return 键值
     */
    public String getKey() {
        return key;
    }
    
    /**
     * 获取排序类型的显示名称
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取下一个排序类型（循环）
     * @return 下一个排序类型
     */
    public SortType getNext() {
        SortType[] values = values();
        int currentIndex = this.ordinal();
        int nextIndex = (currentIndex + 1) % values.length;
        return values[nextIndex];
    }
    
    /**
     * 根据键值获取排序类型
     * @param key 键值
     * @return 对应的排序类型，如果未找到则返回TIME_ASC
     */
    public static SortType fromKey(String key) {
        for (SortType type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return TIME_ASC; // 默认返回最近上架
    }
}
