package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * 文本显示管理器
 * 负责创建和管理文本全息显示
 */
public class TextDisplayManager {
    
    private final GlobalShop plugin;
    private final HologramDisplayManager displayManager;
    
    /**
     * 构造函数
     * @param plugin 插件实例
     * @param displayManager 全息显示管理器
     */
    public TextDisplayManager(GlobalShop plugin, HologramDisplayManager displayManager) {
        this.plugin = plugin;
        this.displayManager = displayManager;
    }
    
    /**
     * 创建文本全息显示
     * @param location 位置
     * @param text 显示文本
     * @param hologramId 全息组ID
     * @param scale 文本缩放比例
     * @param textColor 文本颜色
     * @param backgroundColor 背景颜色
     * @return 创建的TextDisplay实体
     */
    public TextDisplay createTextDisplay(Location location, String text, UUID hologramId, 
                                      float scale, String textColor, String backgroundColor) {
        return createTextDisplay(location, text, hologramId, scale, textColor, backgroundColor, "content");
    }
    
    /**
     * 创建文本全息显示（带类型）
     * @param location 位置
     * @param text 显示文本
     * @param hologramId 全息组ID
     * @param scale 文本缩放比例
     * @param textColor 文本颜色
     * @param backgroundColor 背景颜色
     * @param type 文本类型（title/content）
     * @return 创建的TextDisplay实体
     */
    public TextDisplay createTextDisplay(Location location, String text, UUID hologramId, 
                                      float scale, String textColor, String backgroundColor, String type) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        
        // 创建文本显示实体
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
        
        // 设置文本内容（带颜色）
        if (textColor != null && !textColor.isEmpty()) {
            if (textColor.startsWith("#")) {
                // 尝试使用HEX颜色
                try {
                    java.awt.Color color = java.awt.Color.decode(textColor);
                    text = net.md_5.bungee.api.ChatColor.of(color) + text;
                } catch (Exception e) {
                    // 如果HEX颜色解析失败，使用默认白色
                    text = ChatColor.WHITE + text;
                }
            } else {
                // 尝试使用命名颜色
                try {
                    ChatColor color = ChatColor.valueOf(textColor.toUpperCase());
                    text = color + text;
                } catch (Exception e) {
                    // 如果命名颜色解析失败，使用默认白色
                    text = ChatColor.WHITE + text;
                }
            }
        }
        
        // 设置文本
        textDisplay.setText(text);
        
        // 从配置文件获取TextDisplay设置
        HologramConfigManager.TextDisplaySettings settings = 
                plugin.getHologramConfigManager().getDisplaySettings(type);
        
        // 应用设置到TextDisplay实体
        settings.applyToTextDisplay(textDisplay);
        
        // 如果有自定义的背景颜色参数，则覆盖配置中的设置
        if (backgroundColor != null && !backgroundColor.isEmpty() && backgroundColor.startsWith("#")) {
            try {
                int alpha = Integer.parseInt(backgroundColor.substring(1, 3), 16);
                int red = Integer.parseInt(backgroundColor.substring(3, 5), 16);
                int green = Integer.parseInt(backgroundColor.substring(5, 7), 16);
                int blue = Integer.parseInt(backgroundColor.substring(7, 9), 16);
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(alpha, red, green, blue));
            } catch (Exception e) {
                // 忽略格式错误
            }
        }
        
        // 设置缩放
        Transformation transformation = textDisplay.getTransformation();
        Vector3f scaleVector = new Vector3f(scale, scale, scale);
        transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                scaleVector,
                transformation.getRightRotation()
        );
        textDisplay.setTransformation(transformation);
        
        // 设置基本属性
        displayManager.setDisplayDefaults(textDisplay, settings.getViewRange());
        
        // 添加到全息组
        displayManager.addEntityToHologram(hologramId, textDisplay);
        
        return textDisplay;
    }
    
    /**
     * 更新文本显示内容
     * @param textDisplay 文本显示实体
     * @param text 新的文本内容
     * @param textColor 文本颜色
     */
    public void updateTextDisplay(TextDisplay textDisplay, String text, String textColor) {
        // 设置文本内容（带颜色）
        if (textColor != null && !textColor.isEmpty()) {
            if (textColor.startsWith("#")) {
                // 尝试使用HEX颜色
                try {
                    java.awt.Color color = java.awt.Color.decode(textColor);
                    text = net.md_5.bungee.api.ChatColor.of(color) + text;
                } catch (Exception e) {
                    // 如果HEX颜色解析失败，使用默认白色
                    text = ChatColor.WHITE + text;
                }
            } else {
                // 尝试使用命名颜色
                try {
                    ChatColor color = ChatColor.valueOf(textColor.toUpperCase());
                    text = color + text;
                } catch (Exception e) {
                    // 如果命名颜色解析失败，使用默认白色
                    text = ChatColor.WHITE + text;
                }
            }
        }
        
        // 设置文本
        textDisplay.setText(text);
    }
    
    /**
     * 设置背景颜色
     * @param textDisplay 文本显示实体
     * @param backgroundColor 背景颜色
     */
    public void setBackgroundColor(TextDisplay textDisplay, String backgroundColor) {
        if (backgroundColor != null && !backgroundColor.isEmpty() && backgroundColor.startsWith("#")) {
            try {
                int alpha = Integer.parseInt(backgroundColor.substring(1, 3), 16);
                int red = Integer.parseInt(backgroundColor.substring(3, 5), 16);
                int green = Integer.parseInt(backgroundColor.substring(5, 7), 16);
                int blue = Integer.parseInt(backgroundColor.substring(7, 9), 16);
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(alpha, red, green, blue));
            } catch (Exception e) {
                // 忽略格式错误
            }
        }
    }
    
    /**
     * 设置文本缩放比例
     * @param textDisplay 文本显示实体
     * @param scale 缩放比例
     */
    public void setTextScale(TextDisplay textDisplay, float scale) {
        Transformation transformation = textDisplay.getTransformation();
        Vector3f scaleVector = new Vector3f(scale, scale, scale);
        transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                scaleVector,
                transformation.getRightRotation()
        );
        textDisplay.setTransformation(transformation);
    }
} 