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
        return config.getInt("auction.max_listings_per_player", 20);
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
        return 0; // 买家不需要支付税费
    }

    // 获取拍卖检查间隔时间（秒）
    public int getCheckInterval() {
        return config.getInt("auction.check_interval", 30);
    }
} 