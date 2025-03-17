package cn.i7mc.globalshop;

import cn.i7mc.globalshop.commands.AuctionCommand;
import cn.i7mc.globalshop.config.ConfigManager;
import cn.i7mc.globalshop.database.DatabaseManager;
import cn.i7mc.globalshop.economy.EconomyManager;
import cn.i7mc.globalshop.gui.GuiManager;
import cn.i7mc.globalshop.listeners.GuiListener;
import cn.i7mc.globalshop.tasks.AuctionTask;
import cn.i7mc.globalshop.utils.MinecraftLanguageManager;
import cn.i7mc.globalshop.utils.SearchHistoryManager;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalShop extends JavaPlugin {
    private static GlobalShop instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private GuiManager guiManager;
    private SearchHistoryManager searchHistoryManager;
    private MinecraftLanguageManager languageManager;
    private Economy vaultEconomy;
    private PlayerPoints playerPoints;

    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化配置
        this.configManager = new ConfigManager(this);
        
        // 初始化数据库
        this.databaseManager = new DatabaseManager(this);
        
        // 初始化经济系统
        if (!setupEconomy()) {
            getLogger().severe("未找到Vault插件或经济系统，插件将无法正常工作！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 初始化点券系统
        if (!setupPlayerPoints()) {
            getLogger().warning("未找到PlayerPoints插件，点券功能将不可用！");
        }
        
        // 初始化经济管理器
        this.economyManager = new EconomyManager(this, vaultEconomy, playerPoints);
        
        // 初始化Minecraft语言管理器
        this.languageManager = new MinecraftLanguageManager(this);
        
        // 初始化搜索历史管理器，最多保存10条历史记录
        this.searchHistoryManager = new SearchHistoryManager(10);
        
        // 初始化GUI管理器
        this.guiManager = new GuiManager(this);
        
        // 注册命令和监听器
        AuctionCommand auctionCommand = new AuctionCommand(this);
        getCommand("auction").setExecutor(auctionCommand);
        getCommand("auction").setTabCompleter(auctionCommand);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        
        // 启动定时任务，使用配置中设置的间隔时间来检查过期拍卖
        int checkIntervalSeconds = configManager.getCheckInterval();
        long checkIntervalTicks = checkIntervalSeconds * 20L; // 转换为tick (1秒=20tick)
        new AuctionTask(this).runTaskTimer(this, checkIntervalTicks, checkIntervalTicks);
        
        getLogger().info("GlobalShop插件已成功启动!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("GlobalShop插件已关闭!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomy = rsp.getProvider();
        return vaultEconomy != null;
    }

    private boolean setupPlayerPoints() {
        playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
        return playerPoints != null;
    }

    public static GlobalShop getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
    
    public SearchHistoryManager getSearchHistoryManager() {
        return searchHistoryManager;
    }

    public Economy getVaultEconomy() {
        return vaultEconomy;
    }

    public PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    /**
     * 获取Minecraft语言管理器
     * 
     * @return Minecraft语言管理器
     */
    public MinecraftLanguageManager getLanguageManager() {
        return languageManager;
    }
}