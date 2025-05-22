package cn.i7mc.globalshop.web;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Web服务配置管理类
 * 负责管理Web服务的配置项
 */
public class WebConfig {
    private final GlobalShop plugin;
    private boolean enabled;
    private int port;
    private int refreshInterval;
    private int itemsPerPage;
    private boolean showExpired;
    private boolean showSold;
    private boolean allowCors;

    /**
     * 构造函数
     * @param plugin 插件实例
     */
    public WebConfig(GlobalShop plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * 从配置文件加载Web服务配置
     */
    public void loadConfig() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        this.enabled = config.getBoolean("web.enabled", true);
        this.port = config.getInt("web.port", 20088);
        this.refreshInterval = config.getInt("web.refresh_interval", 30);
        this.itemsPerPage = config.getInt("web.items_per_page", 20);
        this.showExpired = config.getBoolean("web.show_expired", false);
        this.showSold = config.getBoolean("web.show_sold", true);
        this.allowCors = config.getBoolean("web.allow_cors", false);
    }

    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        loadConfig();
    }

    /**
     * 检查Web服务是否启用
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 获取Web服务端口
     * @return 端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取数据刷新间隔（秒）
     * @return 刷新间隔
     */
    public int getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 获取每页显示的物品数量
     * @return 每页物品数量
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * 检查是否显示已过期物品
     * @return 是否显示已过期物品
     */
    public boolean isShowExpired() {
        return showExpired;
    }

    /**
     * 检查是否显示已售出物品
     * @return 是否显示已售出物品
     */
    public boolean isShowSold() {
        return showSold;
    }

    /**
     * 检查是否允许跨域请求
     * @return 是否允许跨域请求
     */
    public boolean isAllowCors() {
        return allowCors;
    }
}
