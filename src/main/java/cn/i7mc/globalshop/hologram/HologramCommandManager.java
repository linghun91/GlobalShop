package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.config.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HologramCommandManager {
    private final GlobalShop plugin;
    private final HologramDisplayManager hologramDisplayManager;
    private final MessageManager messageManager;
    private final HologramConfigManager configManager;
    private File hudLocationsFile;
    private FileConfiguration hudLocationsConfig;
    private final Map<String, Location> hologramLocations = new HashMap<>();
    private final Map<String, UUID> hologramIds = new HashMap<>();

    public HologramCommandManager(GlobalShop plugin, HologramDisplayManager hologramDisplayManager,
                                 HologramConfigManager configManager) {
        this.plugin = plugin;
        this.hologramDisplayManager = hologramDisplayManager;
        this.messageManager = plugin.getMessageManager();
        this.configManager = configManager;

        // 初始化位置配置文件
        initLocationsFile();

        // 加载已保存的全息位置
        loadHologramLocations();
    }

    private void initLocationsFile() {
        hudLocationsFile = new File(plugin.getDataFolder(), "hud_locations.yml");
        if (!hudLocationsFile.exists()) {
            try {
                hudLocationsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        hudLocationsConfig = YamlConfiguration.loadConfiguration(hudLocationsFile);
    }

    private void loadHologramLocations() {
        if (hudLocationsConfig.contains("locations")) {
            for (String key : hudLocationsConfig.getConfigurationSection("locations").getKeys(false)) {
                String worldName = hudLocationsConfig.getString("locations." + key + ".world");
                double x = hudLocationsConfig.getDouble("locations." + key + ".x");
                double y = hudLocationsConfig.getDouble("locations." + key + ".y");
                double z = hudLocationsConfig.getDouble("locations." + key + ".z");
                float yaw = (float) hudLocationsConfig.getDouble("locations." + key + ".yaw");
                float pitch = (float) hudLocationsConfig.getDouble("locations." + key + ".pitch");

                if (Bukkit.getWorld(worldName) != null) {
                    Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                    hologramLocations.put(key, location);
                    // 创建全息并保存ID
                    UUID hologramId = hologramDisplayManager.createHologram(location);
                    hologramIds.put(key, hologramId);
                } else {
                }
            }
        }
    }

    private void saveHologramLocations() {
        // 清空现有配置
        hudLocationsConfig.set("locations", null);

        // 保存每个全息位置
        for (Map.Entry<String, Location> entry : hologramLocations.entrySet()) {
            String key = entry.getKey();
            Location loc = entry.getValue();

            hudLocationsConfig.set("locations." + key + ".world", loc.getWorld().getName());
            hudLocationsConfig.set("locations." + key + ".x", loc.getX());
            hudLocationsConfig.set("locations." + key + ".y", loc.getY());
            hudLocationsConfig.set("locations." + key + ".z", loc.getZ());
            hudLocationsConfig.set("locations." + key + ".yaw", loc.getYaw());
            hudLocationsConfig.set("locations." + key + ".pitch", loc.getPitch());
        }

        try {
            hudLocationsConfig.save(hudLocationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理/ah hud命令
     */
    public boolean onCommand(CommandSender sender, String[] args) {

        // 检查是否有权限
        if (!sender.hasPermission("globalshop.admin.hud")) {
            sender.sendMessage("§c你没有权限执行此命令");
            return true;
        }

        if (args.length < 2) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "create":
                return handleCreateCommand(sender, args);
            case "remove":
                return handleRemoveCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            case "reload":
                return handleReloadCommand(sender);
            default:
                sendHelpMessage(sender);
                return true;
        }
    }

    /**
     * 处理创建全息显示的命令
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 3) {
            sender.sendMessage("§c请指定全息名称");
            return true;
        }

        String hudName = args[2];

        // 检查名称是否已存在
        if (hologramLocations.containsKey(hudName)) {
            sender.sendMessage("§c该名称的全息已存在");
            return true;
        }

        // 获取玩家当前位置
        Location location = player.getLocation().clone();

        // 计算最接近的90度角
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        // 四舍五入到最接近的90/180/270/360度
        int roundedYaw = Math.round(yaw / 90) * 90;
        if (roundedYaw == 0) {
            roundedYaw = 360;
        }

        // 设置角度为四舍五入后的角度，pitch设为0
        location.setYaw(roundedYaw);
        location.setPitch(0);

        // 创建全息并保存ID
        UUID hologramId = hologramDisplayManager.createHologram(location);

        // 添加全息位置
        hologramLocations.put(hudName, location);
        hologramIds.put(hudName, hologramId);

        // 将位置添加到更新任务
        HologramUpdateTask updateTask = plugin.getHologramUpdateTask();
        if (updateTask != null) {
            updateTask.addHologramLocation(hologramId, location);
            updateTask.forceUpdate(); // 强制立即更新
        } else {
        }

        // 保存到配置
        saveHologramLocations();

        sender.sendMessage("§a成功创建全息显示: §e" + hudName);
        return true;
    }

    /**
     * 处理移除全息显示的命令
     */
    private boolean handleRemoveCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c请指定全息名称");
            return true;
        }

        String hudName = args[2];

        // 检查名称是否存在
        if (!hologramLocations.containsKey(hudName)) {
            sender.sendMessage("§c未找到该全息显示");
            return true;
        }

        // 移除全息位置
        hologramLocations.remove(hudName);

        // 移除全息实体
        UUID hologramId = hologramIds.get(hudName);
        if (hologramId != null) {
            hologramDisplayManager.removeHologram(hologramId);
            hologramIds.remove(hudName);
        }

        // 保存到配置
        saveHologramLocations();

        sender.sendMessage("§a成功移除全息显示: §e" + hudName);
        return true;
    }

    /**
     * 处理列出全息显示的命令
     */
    private boolean handleListCommand(CommandSender sender) {
        if (hologramLocations.isEmpty()) {
            sender.sendMessage("§c当前没有任何全息显示");
            return true;
        }

        sender.sendMessage("§6---- 全息拍卖行列表 ----");

        for (Map.Entry<String, Location> entry : hologramLocations.entrySet()) {
            Location loc = entry.getValue();
            String locationInfo = String.format("§a%s§f: §e%s §f(%.2f, %.2f, %.2f)",
                    entry.getKey(),
                    loc.getWorld().getName(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ());
            sender.sendMessage(locationInfo);
        }

        return true;
    }

    /**
     * 处理重载配置的命令
     */
    public boolean handleReloadCommand(CommandSender sender) {
        // 重新加载配置
        configManager.reloadConfig();

        // 清除现有全息
        for (UUID hologramId : hologramIds.values()) {
            hologramDisplayManager.removeHologram(hologramId);
        }

        // 清理所有世界中可能残留的全息实体
        cleanupRemainingHolograms();

        // 重新加载位置
        hologramLocations.clear();
        hologramIds.clear();
        hudLocationsConfig = YamlConfiguration.loadConfiguration(hudLocationsFile);
        loadHologramLocations();

        // 强制更新全息显示
        for (UUID hologramId : hologramIds.values()) {
            hologramDisplayManager.clearHologram(hologramId);
        }

        // 强制更新一次
        HologramUpdateTask updateTask = plugin.getHologramUpdateTask();
        if (updateTask != null) {
            // 更新任务配置
            updateTask.updateTaskConfig();
            updateTask.forceUpdate();
        }

        sender.sendMessage("§a全息拍卖行配置已重新加载");
        sender.sendMessage("§a视距设置为: §e" + configManager.getDisplaySettings("content").getViewRange() + " 方块");
        sender.sendMessage("§a更新间隔设置为: §e" + configManager.getUpdateInterval() + " 秒");
        return true;
    }

    /**
     * 清理所有世界中可能残留的全息实体
     */
    private void cleanupRemainingHolograms() {
        int removedCount = 0;

        try {
            for (org.bukkit.World world : plugin.getServer().getWorlds()) {
                for (org.bukkit.entity.Entity entity : world.getEntities()) {
                    if (entity instanceof org.bukkit.entity.Display) {
                        String customName = entity.getCustomName();
                        if (customName != null && (customName.startsWith("GlobalShop_Text_") || customName.startsWith("GlobalShop_Item_"))) {
                            entity.remove();
                            removedCount++;
                        }
                    }
                }
            }

            if (removedCount > 0 && plugin.getConfigManager().isDebug()) {
                plugin.getDebugMessageManager().debug("重载时清理了 " + removedCount + " 个残留的全息实体");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送帮助信息
     */
    private void sendHelpMessage(CommandSender sender) {
        List<String> helpMessages = new ArrayList<>();
        helpMessages.add("§6---- 全息拍卖行命令帮助 ----");
        helpMessages.add("§a/ah hud create <名称> §f- 在当前位置创建一个全息拍卖行");
        helpMessages.add("§a/ah hud remove <名称> §f- 移除指定名称的全息拍卖行");
        helpMessages.add("§a/ah hud list §f- 列出所有全息拍卖行");
        helpMessages.add("§a/ah hud reload §f- 重新加载全息拍卖行配置");

        for (String message : helpMessages) {
            sender.sendMessage(message);
        }
    }

    /**
     * 获取已注册的全息位置
     */
    public Map<String, Location> getHologramLocations() {
        return hologramLocations;
    }

    /**
     * 获取全息ID映射
     */
    public Map<String, UUID> getHologramIds() {
        return hologramIds;
    }

    /**
     * 添加全息位置
     */
    public void addHologramLocation(String name, Location location) {
        // 处理yaw和pitch
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        // 四舍五入到最接近的90/180/270/360度
        int roundedYaw = Math.round(yaw / 90) * 90;
        if (roundedYaw == 0) {
            roundedYaw = 360;
        }

        // 设置角度为四舍五入后的角度，pitch设为0
        location.setYaw(roundedYaw);
        location.setPitch(0);

        // 创建全息并保存ID
        UUID hologramId = hologramDisplayManager.createHologram(location);

        // 添加全息位置
        hologramLocations.put(name, location);
        hologramIds.put(name, hologramId);

        // 保存到配置
        saveHologramLocations();
    }

    /**
     * 移除全息位置
     */
    public void removeHologramLocation(String name) {
        // 检查名称是否存在
        if (!hologramLocations.containsKey(name)) {
            return;
        }

        // 移除全息位置
        hologramLocations.remove(name);

        // 移除全息实体
        UUID hologramId = hologramIds.get(name);
        if (hologramId != null) {
            hologramDisplayManager.removeHologram(hologramId);
            hologramIds.remove(name);
        }

        // 保存到配置
        saveHologramLocations();
    }

    /**
     * 强制更新所有全息显示
     */
    public void forceUpdateAll() {
        // 清空全部全息实体
        for (UUID hologramId : hologramIds.values()) {
            hologramDisplayManager.clearHologram(hologramId);
        }

        // 清理所有世界中可能残留的全息实体
        cleanupRemainingHolograms();

        // 触发强制更新
        HologramUpdateTask updateTask = plugin.getHologramUpdateTask();
        if (updateTask != null) {
            updateTask.forceUpdate();
        }
    }
}