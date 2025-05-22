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
<<<<<<< HEAD

    private final GlobalShop plugin;
    private final Map<UUID, List<Entity>> hologramEntities;

=======
    
    private final GlobalShop plugin;
    private final Map<UUID, List<Entity>> hologramEntities;
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 构造函数
     * @param plugin 插件实例
     */
    public HologramDisplayManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.hologramEntities = new HashMap<>();
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 创建新的全息展示组
     * @param location 展示位置
     * @return 全息展示组UUID
     */
    public UUID createHologram(Location location) {
        UUID hologramId = UUID.randomUUID();
        hologramEntities.put(hologramId, new ArrayList<>());
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 创建一个初始的文本显示实体，显示加载中提示
        try {
            if (location.getWorld() != null) {
                // 使用官方API创建文本显示实体
                TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(
                    location, EntityType.TEXT_DISPLAY);
<<<<<<< HEAD

                // 设置文本内容
                textDisplay.setText("§a正在加载拍卖行历史记录...");

=======
                
                // 设置文本内容
                textDisplay.setText("§a正在加载拍卖行历史记录...");
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 设置显示属性
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.setViewRange(48.0f);
                textDisplay.setSeeThrough(true);
                textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
<<<<<<< HEAD

                // 设置持久性为false，确保服务器关闭后不会有残留实体
                textDisplay.setPersistent(false);

                // 设置自定义名称，便于识别
                textDisplay.setCustomName("GlobalShop_Text_" + plugin.getName());
                textDisplay.setCustomNameVisible(false);

                // 添加到实体列表
                hologramEntities.get(hologramId).add(textDisplay);

=======
                
                // 添加到实体列表
                hologramEntities.get(hologramId).add(textDisplay);
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
<<<<<<< HEAD

        return hologramId;
    }

=======
        
        return hologramId;
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 获取展示组中的实体列表
     * @param hologramId 展示组UUID
     * @return 实体列表
     */
    public List<Entity> getHologramEntities(UUID hologramId) {
        return hologramEntities.getOrDefault(hologramId, new ArrayList<>());
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 设置实体的基本显示属性
     * @param display 显示实体
     * @param viewRange 可视范围
     */
    public void setDisplayDefaults(Display display, float viewRange) {
        display.setViewRange(viewRange);
        display.setShadowRadius(0);
        display.setShadowStrength(0);
<<<<<<< HEAD

        // 确保实体不是持久化的
        display.setPersistent(false);
    }

=======
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 移除所有全息展示
     */
    public void removeAllHolograms() {
<<<<<<< HEAD
        hologramEntities.values().forEach(entities ->
=======
        hologramEntities.values().forEach(entities -> 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            entities.forEach(Entity::remove)
        );
        hologramEntities.clear();
    }
<<<<<<< HEAD

    /**
     * 验证全息实体的有效性
     * 检查并移除无效的实体引用
     */
    public void validateHolograms() {
        // 遍历所有全息组
        for (Map.Entry<UUID, List<Entity>> entry : new HashMap<>(hologramEntities).entrySet()) {
            UUID hologramId = entry.getKey();
            List<Entity> entities = entry.getValue();
            List<Entity> validEntities = new ArrayList<>();

            // 检查每个实体是否有效
            for (Entity entity : entities) {
                if (entity != null && !entity.isDead() && entity.isValid()) {
                    validEntities.add(entity);
                }
            }

            // 如果有无效实体，更新列表
            if (validEntities.size() != entities.size()) {
                if (validEntities.isEmpty()) {
                    // 如果没有有效实体，移除整个全息组
                    hologramEntities.remove(hologramId);
                } else {
                    // 更新为有效实体列表
                    hologramEntities.put(hologramId, validEntities);
                }
            }
        }
    }
}
=======
} 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
