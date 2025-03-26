package cn.i7mc.globalshop;

import cn.i7mc.globalshop.commands.AuctionCommand;
import cn.i7mc.globalshop.config.ConfigManager;
import cn.i7mc.globalshop.database.DatabaseManager;
import cn.i7mc.globalshop.economy.EconomyManager;
import cn.i7mc.globalshop.gui.GuiManager;
import cn.i7mc.globalshop.hologram.AuctionHistoryManager;
import cn.i7mc.globalshop.hologram.HologramCommandManager;
import cn.i7mc.globalshop.hologram.HologramConfigManager;
import cn.i7mc.globalshop.hologram.HologramDisplayManager;
import cn.i7mc.globalshop.hologram.HologramUpdateTask;
import cn.i7mc.globalshop.hologram.ItemDisplayManager;
import cn.i7mc.globalshop.hologram.TextDisplayManager;
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
    
    // 全息相关组件
    private HologramDisplayManager hologramDisplayManager;
    private ItemDisplayManager itemDisplayManager;
    private TextDisplayManager textDisplayManager;
    private AuctionHistoryManager auctionHistoryManager;
    private HologramConfigManager hologramConfigManager;
    private HologramCommandManager hologramCommandManager;
    private HologramUpdateTask hologramUpdateTask;
    
    // 用于存储拍卖检查任务的引用，以便能够取消
    private BukkitTask auctionTask;

    @Override
    public void onEnable() {
        instance = this;
        
        // 加载配置文件
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        
        // 安装所有语言文件
        messageManager.installLanguageFiles();
        
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
        
        // 初始化全息相关组件
        initHologramComponents();
        
        // 注册命令和监听器
        AuctionCommand auctionCommand = new AuctionCommand(this);
        getCommand("auction").setExecutor(auctionCommand);
        getCommand("auction").setTabCompleter(auctionCommand);
        
        // 添加调试信息，确保TabCompleter正确注册
        if (getConfigManager().isDebug()) {
            getLogger().info("已注册auction命令的TabCompleter");
        }
        
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        
        // 启动定时任务，使用配置中设置的间隔时间来检查过期拍卖
        startAuctionTasks();
        
        // 在插件启动时强制清空并初始化所有全息显示
        if (hologramCommandManager != null) {
            getLogger().info("§b[GlobalShop] 正在清空并初始化所有全息显示...");
            hologramCommandManager.forceUpdateAll();
        }
        
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.AQUA + "如有建议或BUG可联系作者QQ642751482反馈");
        console.sendMessage(ChatColor.DARK_AQUA + "[GlobalShop] " + ChatColor.AQUA + "插件已成功启动!");
    }

    @Override
    public void onDisable() {
        // 取消所有任务
        cancelAuctionTasks();
        
        // 关闭全息系统
        if (hologramUpdateTask != null) {
            hologramUpdateTask.cancel();
        }
        
        // 移除所有全息实体
        if (hologramDisplayManager != null) {
            hologramDisplayManager.removeAllHolograms();
        }
        
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

    /**
     * 初始化全息相关组件
     */
    private void initHologramComponents() {
        getLogger().info("§a[GlobalShop] 开始初始化全息组件...");
        try {
            // 初始化全息显示管理器
            this.hologramDisplayManager = new HologramDisplayManager(this);
            getLogger().info("§a[GlobalShop] 全息显示管理器初始化成功");
            
            this.itemDisplayManager = new ItemDisplayManager(this, hologramDisplayManager);
            getLogger().info("§a[GlobalShop] 物品显示管理器初始化成功");
            
            this.textDisplayManager = new TextDisplayManager(this, hologramDisplayManager);
            getLogger().info("§a[GlobalShop] 文本显示管理器初始化成功");
            
            // 初始化拍卖历史记录管理器
            this.auctionHistoryManager = new AuctionHistoryManager(this, 50);
            getLogger().info("§a[GlobalShop] 拍卖历史记录管理器初始化成功");
            
            // 初始化全息配置管理器
            this.hologramConfigManager = new HologramConfigManager(this);
            getLogger().info("§a[GlobalShop] 全息配置管理器初始化成功");
            
            // 初始化全息命令管理器
            this.hologramCommandManager = new HologramCommandManager(this, hologramDisplayManager, hologramConfigManager);
            getLogger().info("§a[GlobalShop] 全息命令管理器初始化成功");
            
            // 创建并启动全息更新任务
            int updateInterval = hologramConfigManager.getUpdateInterval();
            this.hologramUpdateTask = new HologramUpdateTask(this, hologramDisplayManager, itemDisplayManager, textDisplayManager, auctionHistoryManager, hologramConfigManager);
            // 使用同步任务而非异步任务
            hologramUpdateTask.runTaskTimer(this, 20L, updateInterval * 20L);
            getLogger().info("§a[GlobalShop] 全息更新任务已启动，更新间隔：" + updateInterval + "秒");
            
            if (getConfigManager().isDebug()) {
                getLogger().info("全息拍卖行组件已初始化，更新间隔：" + updateInterval + "秒");
            }
        } catch (Exception e) {
            getLogger().severe("§c[GlobalShop] 初始化全息组件时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
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
     * 获取全息显示管理器
     * @return 全息显示管理器
     */
    public HologramDisplayManager getHologramDisplayManager() {
        return hologramDisplayManager;
    }
    
    /**
     * 获取物品显示管理器
     * @return 物品显示管理器
     */
    public ItemDisplayManager getItemDisplayManager() {
        return itemDisplayManager;
    }
    
    /**
     * 获取文本显示管理器
     * @return 文本显示管理器
     */
    public TextDisplayManager getTextDisplayManager() {
        return textDisplayManager;
    }
    
    /**
     * 获取拍卖历史记录管理器
     * @return 拍卖历史记录管理器
     */
    public AuctionHistoryManager getAuctionHistoryManager() {
        return auctionHistoryManager;
    }
    
    /**
     * 获取全息配置管理器
     * @return 全息配置管理器
     */
    public HologramConfigManager getHologramConfigManager() {
        return hologramConfigManager;
    }
    
    /**
     * 获取全息命令管理器
     * @return 全息命令管理器
     */
    public HologramCommandManager getHologramCommandManager() {
        return hologramCommandManager;
    }
    
    /**
     * 获取全息更新任务
     * @return 全息更新任务
     */
    public HologramUpdateTask getHologramUpdateTask() {
        return hologramUpdateTask;
    }

    /**
     * 取消所有拍卖相关任务
     */
    public void cancelAuctionTasks() {
        if (auctionTask != null) {
            auctionTask.cancel();
            auctionTask = null;
        }
    }
    
    /**
     * 启动拍卖相关任务
     */
    public void startAuctionTasks() {
        // 取消现有任务，如果有的话
        cancelAuctionTasks();
        
        // 从配置中获取检查间隔（秒）
        int checkIntervalSeconds = configManager.getConfig().getInt("auction.check-interval", 60);
        
        // 确保间隔时间至少为30秒，避免过于频繁的检查
        checkIntervalSeconds = Math.max(checkIntervalSeconds, 30);
        
        // 将秒转换为tick（1秒 = 20 tick）
        long checkIntervalTicks = checkIntervalSeconds * 20L;
        
        // 添加调试信息
        if (getConfigManager().isDebug()) {
            getLogger().info("启动拍卖检查任务，间隔：" + checkIntervalSeconds + "秒");
        }
        
        // 创建并启动拍卖检查任务
        auctionTask = new AuctionTask(this).runTaskTimer(this, checkIntervalTicks, checkIntervalTicks);
    }

    /**
     * 检查PlayerPoints插件是否可用
     * @return PlayerPoints是否可用
     */
    public boolean isPlayerPointsAvailable() {
        return playerPoints != null;
    }

    /**
     * 重新调度全息更新任务
     * @param intervalSeconds 更新间隔（秒）
     */
    public void rescheduleHologramTask(int intervalSeconds) {
        try {
            // 创建新的全息更新任务实例
            this.hologramUpdateTask = new HologramUpdateTask(this, hologramDisplayManager, 
                itemDisplayManager, textDisplayManager, auctionHistoryManager, hologramConfigManager);
            
            // 以新的间隔启动任务（使用同步任务，而非异步）
            hologramUpdateTask.runTaskTimer(this, 20L, intervalSeconds * 20L);
            
            getLogger().info("§a[GlobalShop] 已重新调度全息更新任务，新间隔：" + intervalSeconds + "秒");
        } catch (Exception e) {
            getLogger().severe("§c[GlobalShop] 重新调度全息更新任务时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}