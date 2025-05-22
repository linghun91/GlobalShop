package cn.i7mc.globalshop.config;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.MessageFormat;

/**
 * 调试消息管理器，负责加载和管理debugmessage.yml配置文件
 */
public class DebugMessageManager {
    private final GlobalShop plugin;
    private FileConfiguration messages;
    private File messagesFile;
    private boolean debugEnabled;

    /**
     * 构造调试消息管理器
     * @param plugin 插件实例
     */
    public DebugMessageManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.debugEnabled = plugin.getConfigManager().getConfig().getBoolean("debug", false);
        loadMessages();
    }

    /**
     * 加载调试消息配置文件
     */
    public void loadMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        messagesFile = new File(plugin.getDataFolder(), "debugmessage.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("debugmessage.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * 重新加载调试消息配置文件
     */
    public void reloadMessages() {
        loadMessages();
        this.debugEnabled = plugin.getConfigManager().getConfig().getBoolean("debug", false);
    }

    /**
     * 获取调试消息
     * @param key 消息键
     * @param defaultMessage 默认消息
     * @param args 格式化参数
     * @return 格式化后的消息
     */
    public String getMessage(String key, String defaultMessage, Object... args) {
        String message = messages.getString("debug." + key, defaultMessage);
        if (args.length > 0) {
            try {
                return MessageFormat.format(message, args);
            } catch (Exception e) {
                return message;
            }
        }
        return message;
    }

    /**
     * 发送调试消息到控制台
     * @param key 消息键
     * @param args 格式化参数
     */
    public void debug(String key, Object... args) {
        if (debugEnabled) {
            String message = getMessage(key, key, args);
        }
    }

    /**
     * 检查调试模式是否启用
     * @return 调试模式是否启用
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * 设置调试模式状态
     * @param debugEnabled 调试模式状态
     */
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        plugin.getConfigManager().getConfig().set("debug", debugEnabled);
        plugin.getConfigManager().saveConfig();
    }
} 