package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

/**
 * 物品显示管理器
 * 负责创建和管理物品全息显示
 */
public class ItemDisplayManager {
<<<<<<< HEAD

    private final GlobalShop plugin;
    private final HologramDisplayManager displayManager;

=======
    
    private final GlobalShop plugin;
    private final HologramDisplayManager displayManager;
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 构造函数
     * @param plugin 插件实例
     * @param displayManager 全息显示管理器
     */
    public ItemDisplayManager(GlobalShop plugin, HologramDisplayManager displayManager) {
        this.plugin = plugin;
        this.displayManager = displayManager;
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 创建物品全息显示
     * @param location 位置
     * @param itemStack 显示的物品
     * @param hologramId 全息组ID
     * @param scale 物品缩放比例
     * @return 创建的ItemDisplay实体
     */
    public ItemDisplay createItemDisplay(Location location, ItemStack itemStack, java.util.UUID hologramId, float scale) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
<<<<<<< HEAD

        // 创建物品显示实体
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);

        // 设置物品及变换模式
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);

=======
        
        // 创建物品显示实体
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);
        
        // 设置物品及变换模式
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 设置缩放
        Transformation transformation = itemDisplay.getTransformation();
        Vector3f scaleVector = new Vector3f(scale, scale, scale);
        transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                scaleVector,
                transformation.getRightRotation()
        );
        itemDisplay.setTransformation(transformation);
<<<<<<< HEAD

        // 设置基本属性
        displayManager.setDisplayDefaults(itemDisplay, 48.0f);

        // 设置持久性为false，确保服务器关闭后不会有残留实体
        itemDisplay.setPersistent(false);

        // 设置自定义名称，便于识别
        itemDisplay.setCustomName("GlobalShop_Item_" + itemStack.getType().name());
        itemDisplay.setCustomNameVisible(false);

        // 添加到全息组
        displayManager.addEntityToHologram(hologramId, itemDisplay);

        return itemDisplay;
    }

=======
        
        // 设置基本属性
        displayManager.setDisplayDefaults(itemDisplay, 48.0f);
        
        // 添加到全息组
        displayManager.addEntityToHologram(hologramId, itemDisplay);
        
        return itemDisplay;
    }
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 更新物品显示
     * @param itemDisplay 物品显示实体
     * @param itemStack 新的物品
     */
    public void updateItemDisplay(ItemDisplay itemDisplay, ItemStack itemStack) {
        itemDisplay.setItemStack(itemStack);
    }
<<<<<<< HEAD

=======
    
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    /**
     * 调整物品显示大小
     * @param itemDisplay 物品显示实体
     * @param scale 缩放比例
     */
    public void setItemScale(ItemDisplay itemDisplay, float scale) {
        Transformation transformation = itemDisplay.getTransformation();
        Vector3f scaleVector = new Vector3f(scale, scale, scale);
        transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                scaleVector,
                transformation.getRightRotation()
        );
        itemDisplay.setTransformation(transformation);
    }
<<<<<<< HEAD
}
=======
} 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
