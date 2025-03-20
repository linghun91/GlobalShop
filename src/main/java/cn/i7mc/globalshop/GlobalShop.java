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
import cn.i7mc.globalshop.utils.BroadcastManager;
import cn.i7mc.globalshop.config.MessageManager;
import cn.i7mc.globalshop.config.DebugMessageManager;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
    private MessageManager messageManager;
    private DebugMessageManager debugMessageManager;
    private BroadcastManager broadcastManager;
    
    // 用于存储拍卖检查任务的引用，以便能够取消
    private BukkitTask auctionTask;

    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化配置
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.debugMessageManager = new DebugMessageManager(this);
        
        // 初始化数据库
        this.databaseManager = new DatabaseManager(this);
        
        // 初始化经济系统
        if (!setupEconomy()) {
            ConsoleCommandSender console = getServer().getConsoleSender();
            console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.DARK_RED + "未找到" + ChatColor.GOLD + "Vault" + ChatColor.DARK_RED + "插件或经济系统，插件将无法正常工作！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 初始化点券系统
        if (!setupPlayerPoints()) {
            ConsoleCommandSender console = getServer().getConsoleSender();
            console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.DARK_RED + "未找到" + ChatColor.GOLD + "PlayerPoints" + ChatColor.DARK_RED + "插件，点券功能将不可用！");
        }
        
        // 初始化经济管理器
        this.economyManager = new EconomyManager(this, vaultEconomy, playerPoints);
        
        // 初始化Minecraft语言管理器
        this.languageManager = new MinecraftLanguageManager(this);
        
        // 初始化搜索历史管理器，最多保存10条历史记录
        this.searchHistoryManager = new SearchHistoryManager(10);
        
        // 初始化GUI管理器
        this.guiManager = new GuiManager(this);
        
        // 初始化广播管理器
        this.broadcastManager = new BroadcastManager(this);
        
        // 注册命令和监听器
        AuctionCommand auctionCommand = new AuctionCommand(this);
        getCommand("auction").setExecutor(auctionCommand);
        getCommand("auction").setTabCompleter(auctionCommand);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        
        // 启动定时任务，使用配置中设置的间隔时间来检查过期拍卖
        startAuctionTasks();
        
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.AQUA + "如有建议或BUG可联系作者QQ642751482反馈");
        console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.AQUA + "插件已成功启动!");
    }

    @Override
    public void onDisable() {
        // 取消所有任务
        cancelAuctionTasks();
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        // 关闭广播管理器
        if (this.broadcastManager != null) {
            this.broadcastManager.shutdown();
        }
        
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(ChatColor.AQUA + "[GlobalShop] 插件已关闭!");
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
        if (getServer().getPluginManager().getPlugin("PlayerPoints") != null) {
            playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
            return playerPoints != null;
        }
        return false;
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

    /**
     * 获取消息管理器
     * @return 消息管理器
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * 获取调试消息管理器
     * @return 调试消息管理器
     */
    public DebugMessageManager getDebugMessageManager() {
        return debugMessageManager;
    }

    /**
     * 获取广播管理器
     * @return 广播管理器
     */
    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    /**
     * 取消当前运行的拍卖检查任务
     */
    public void cancelAuctionTasks() {
        if (auctionTask != null && !auctionTask.isCancelled()) {
            auctionTask.cancel();
            auctionTask = null;
            if (configManager.isDebug()) {
                getLogger().info("已取消拍卖检查任务");
            }
        }
    }
    
    /**
     * 启动拍卖检查任务，使用配置中的check_interval设置
     */
    public void startAuctionTasks() {
        // 先取消现有任务，确保不会有多个任务同时运行
        cancelAuctionTasks();
        
        // 使用配置中设置的间隔时间来检查过期拍卖
        int checkIntervalSeconds = configManager.getCheckInterval();
        long checkIntervalTicks = checkIntervalSeconds * 20L; // 转换为tick (1秒=20tick)
        
        // 保存任务引用，以便后续可以取消
        auctionTask = new AuctionTask(this).runTaskTimer(this, checkIntervalTicks, checkIntervalTicks);
        
        if (configManager.isDebug()) {
            getLogger().info("已启动拍卖检查任务，间隔: " + checkIntervalSeconds + "秒");
        }
    }

    /**
     * 检查点券系统是否可用
     * @return 点券系统是否可用
     */
    public boolean isPlayerPointsAvailable() {
        return playerPoints != null;
    }
}