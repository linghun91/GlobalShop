package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 全息显示管理器
 * 负责创建和管理全息展示系统
 */
public class HologramDisplayManager {
    
    private final GlobalShop plugin;
    private final Map<UUID, List<Entity>> hologramEntities;
    
    /**
     * 构造函数
     * @param plugin 插件实例
     */
    public HologramDisplayManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.hologramEntities = new HashMap<>();
    }
    
    /**
     * 创建新的全息展示组
     * @param location 展示位置
     * @return 全息展示组UUID
     */
    public UUID createHologram(Location location) {
        UUID hologramId = UUID.randomUUID();
        hologramEntities.put(hologramId, new ArrayList<>());
        
        // 创建一个初始的文本显示实体，显示加载中提示
        try {
            if (location.getWorld() != null) {
                // 使用官方API创建文本显示实体
                TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(
                    location, EntityType.TEXT_DISPLAY);
                
                // 设置文本内容
                textDisplay.setText("§a正在加载拍卖行历史记录...");
                
                // 设置显示属性
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.setViewRange(48.0f);
                textDisplay.setSeeThrough(true);
                textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
                
                // 添加到实体列表
                hologramEntities.get(hologramId).add(textDisplay);
                
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return hologramId;
    }
    
    /**
     * 移除全息展示组
     * @param hologramId 展示组UUID
     */
    public void removeHologram(UUID hologramId) {
        List<Entity> entities = hologramEntities.get(hologramId);
        if (entities != null) {
            entities.forEach(Entity::remove);
            hologramEntities.remove(hologramId);
        }
    }
    
    /**
     * 获取展示组中的实体列表
     * @param hologramId 展示组UUID
     * @return 实体列表
     */
    public List<Entity> getHologramEntities(UUID hologramId) {
        return hologramEntities.getOrDefault(hologramId, new ArrayList<>());
    }
    
    /**
     * 添加实体到展示组
     * @param hologramId 展示组UUID
     * @param entity 要添加的实体
     */
    public void addEntityToHologram(UUID hologramId, Entity entity) {
        List<Entity> entities = hologramEntities.get(hologramId);
        if (entities != null) {
            entities.add(entity);
        }
    }
    
    /**
     * 移除展示组中的所有实体
     * @param hologramId 展示组UUID
     */
    public void clearHologram(UUID hologramId) {
        List<Entity> entities = hologramEntities.get(hologramId);
        if (entities != null) {
            entities.forEach(Entity::remove);
            entities.clear();
        }
    }
    
    /**
     * 设置实体的基本显示属性
     * @param display 显示实体
     * @param viewRange 可视范围
     */
    public void setDisplayDefaults(Display display, float viewRange) {
        display.setBillboard(Display.Billboard.CENTER);
        display.setViewRange(viewRange);
        display.setShadowRadius(0);
        display.setShadowStrength(0);
    }
    
    /**
     * 移除所有全息展示
     */
    public void removeAllHolograms() {
        hologramEntities.values().forEach(entities -> 
            entities.forEach(Entity::remove)
        );
        hologramEntities.clear();
    }
} 