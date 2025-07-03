package cn.i7mc.globalshop.config;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final GlobalShop plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(GlobalShop plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 设置默认值
        config.addDefault("database.type", "sqlite");
        config.addDefault("database.file", "data.db");
        
        // MySQL默认配置
        config.addDefault("database.mysql.host", "localhost");
        config.addDefault("database.mysql.port", 3306);
        config.addDefault("database.mysql.database", "globalshop");
        config.addDefault("database.mysql.username", "root");
        config.addDefault("database.mysql.password", "password");
        config.addDefault("database.mysql.useSSL", false);
        config.addDefault("database.mysql.table_prefix", "gs_");
        
        config.addDefault("economy.buyer_tax_rate", 0.02);
        config.addDefault("economy.vault.name", "金币");
        config.addDefault("economy.vault.symbol", "§6$");
        config.addDefault("economy.vault.fee", 0.05);
        config.addDefault("economy.vault.min_fee", 1.0);
        
        config.addDefault("economy.points.name", "点券");
        config.addDefault("economy.points.symbol", "§b★");
        config.addDefault("economy.points.fee", 0.05);
        config.addDefault("economy.points.min_fee", 1);
        
        config.addDefault("auction.default_duration", 86400); // 24小时
        config.addDefault("auction.min_duration", 3600); // 1小时
        config.addDefault("auction.max_duration", 604800); // 7天
        config.addDefault("auction.max_listings_per_player", 3);
        config.addDefault("auction.check_interval", 30); // 默认30秒检查一次
        
        config.addDefault("gui.title", "§8[§6拍卖行§8]");
        config.addDefault("gui.size", 54);
        
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // 数据库配置
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }

    public String getDatabaseFile() {
        return config.getString("database.file", "data.db");
    }
    
    // MySQL数据库配置
    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }
    
    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "globalshop");
    }
    
    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }
    
    public boolean getMySQLUseSSL() {
        return config.getBoolean("database.mysql.useSSL", false);
    }

    /**
     * 获取MySQL数据库表前缀
     * @return 表前缀，默认为"gs_"
     */
    public String getMySQLTablePrefix() {
        return config.getString("database.mysql.table_prefix", "gs_");
    }

    // Vault经济配置
    public String getVaultName() {
        return config.getString("economy.vault.name", "金币");
    }

    public String getVaultSymbol() {
        return config.getString("economy.vault.symbol", "§6$");
    }

    public double getVaultFee() {
        return config.getDouble("economy.vault.fee", 0.05);
    }

    public double getVaultMinFee() {
        return config.getDouble("economy.vault.min_fee", 1.0);
    }

    // 获取Vault最低加价比例
    public double getVaultMinBidRate() {
        return config.getDouble("economy.vault.min_bid_rate", 0.05);
    }

    // 获取点券最低加价比例
    public double getPointsMinBidRate() {
        return config.getDouble("economy.points.min_bid_rate", 0.05);
    }

    // 获取Vault最低加价金额
    public double getVaultMinBidAmount() {
        return config.getDouble("economy.vault.min_bid_amount", 1.0);
    }

    // 获取点券最低加价金额
    public double getPointsMinBidAmount() {
        return config.getDouble("economy.points.min_bid_amount", 1.0);
    }

    // 点券配置
    public String getPointsName() {
        return config.getString("economy.points.name", "点券");
    }

    public String getPointsSymbol() {
        return config.getString("economy.points.symbol", "§b★");
    }

    public double getPointsFee() {
        return config.getDouble("economy.points.fee", 0.05);
    }

    public int getPointsMinFee() {
        return config.getInt("economy.points.min_fee", 1);
    }

    // 拍卖配置
    public long getDefaultDuration() {
        return config.getLong("auction.default_duration", 86400);
    }

    public long getMinDuration() {
        return config.getLong("auction.min_duration", 3600);
    }

    public long getMaxDuration() {
        return config.getLong("auction.max_duration", 604800);
    }

    public int getMaxListingsPerPlayer() {
        return config.getInt("auction.max_listings_per_player", 3);
    }

    /**
     * 获取玩家的最大上架数量限制
     * 优先检查玩家的 globalshop.maxsell.X 权限，如果没有则使用全局配置
     *
     * @param player 要检查的玩家
     * @return 玩家允许的最大上架数量
     */
    public int getMaxListingsForPlayer(org.bukkit.entity.Player player) {
        // 检查玩家是否有自定义上架数量权限
        int maxFromPermission = getMaxListingsFromPermission(player);

        // 如果有权限设置，使用权限中的数量；否则使用全局配置
        if (maxFromPermission > 0) {
            return maxFromPermission;
        }

        // 使用全局配置的默认值
        return getMaxListingsPerPlayer();
    }

    /**
     * 从玩家权限中解析最大上架数量
     * 检查所有 globalshop.maxsell.X 权限，返回最大的数值
     *
     * @param player 要检查的玩家
     * @return 权限中设置的最大上架数量，如果没有相关权限则返回0
     */
    private int getMaxListingsFromPermission(org.bukkit.entity.Player player) {
        int maxListings = 0;

        // 遍历玩家的所有权限
        for (org.bukkit.permissions.PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String permission = permInfo.getPermission();

            // 检查是否是 globalshop.maxsell.X 格式的权限
            if (permission.startsWith("globalshop.maxsell.") && permInfo.getValue()) {
                try {
                    // 提取权限中的数字部分
                    String numberPart = permission.substring("globalshop.maxsell.".length());
                    int permissionValue = Integer.parseInt(numberPart);

                    // 取最大值
                    if (permissionValue > maxListings) {
                        maxListings = permissionValue;
                    }
                } catch (NumberFormatException e) {
                    // 如果权限格式不正确，忽略该权限
                    // 这里不输出错误信息，避免日志污染
                }
            }
        }

        return maxListings;
    }

    // GUI配置
    public String getGuiTitle() {
        return config.getString("gui.title", "§8[§6拍卖行§8]");
    }

    public int getGuiSize() {
        return config.getInt("gui.size", 54);
    }
    
    /**
     * 获取买家税率（购买时需要支付的额外税费）
     * @return 买家税率，范围0-1
     */
    public double getBuyerTaxRate() {
        return config.getDouble("economy.buyer_tax_rate", 0.0);
    }

    // 获取拍卖检查间隔时间（秒）
    public int getCheckInterval() {
        return config.getInt("auction.check_interval", 30);
    }
    
    /**
     * 检查调试模式是否开启
     * @return 调试模式是否开启
     */
    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    // 获取价格最大位数限制
    public int getMaxPriceDigits() {
        return config.getInt("economy.max_price_digits", 9);
    }
    
    /**
     * 获取语言设置
     * @return 设置的语言代码(如zh_CN, en等)
     */
    public String getLanguageLocale() {
        return config.getString("language.locale", "zh_CN");
    }
    
    /**
     * 检查是否使用中文
     * @return 是否使用中文
     */
    public boolean isChineseLanguage() {
        String locale = getLanguageLocale();
        return "zh_CN".equals(locale) || "zh".equals(locale);
    }
} 