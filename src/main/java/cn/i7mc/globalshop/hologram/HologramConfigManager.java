package cn.i7mc.globalshop.hologram;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全息显示配置管理器
 * 负责管理全息显示配置文件
 */
public class HologramConfigManager {
    
    private final GlobalShop plugin;
    private FileConfiguration config;
    private File configFile;
    
    private int displayRows;
    private int updateInterval;
    private float lineSpacing;
    private float itemTextSpacing;
    private float itemScale;
    
    private Map<String, TextStyle> textStyles;
    private Map<String, String> eventTexts;
    private Map<String, String> messages;
    private Map<String, String> currencyNames;
    private String dateFormat;
    
    private Map<String, TextDisplaySettings> displaySettings;
    
    /**
     * 构造函数
     * @param plugin 插件实例
     */
    public HologramConfigManager(GlobalShop plugin) {
        this.plugin = plugin;
        this.textStyles = new HashMap<>();
        this.eventTexts = new HashMap<>();
        this.messages = new HashMap<>();
        this.currencyNames = new HashMap<>();
        this.displaySettings = new HashMap<>();
        this.configFile = new File(plugin.getDataFolder(), "hd_gui.yml");
        
        // 默认配置
        this.displayRows = 10;
        this.updateInterval = 30;
        this.lineSpacing = 0.3f;
        this.itemTextSpacing = 0.6f;
        this.itemScale = 0.5f;
        this.dateFormat = "MM-dd HH:mm";
        
        // 加载配置
        loadConfig();
    }
    
    /**
     * 加载配置文件
     */
    public void loadConfig() {
        try {
            initConfig();
            
            // 重新加载配置
            config = YamlConfiguration.loadConfiguration(configFile);
            
            // 加载设置
            loadSettings();
            
            // 加载文本样式
            loadTextStyles();
            
            // 加载事件文本
            loadEventTexts();
            
            // 加载消息
            loadMessages();
            
            // 加载价格格式
            loadPriceFormat();
            
            // 加载TextDisplay设置
            loadTextDisplaySettings();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 加载基础设置
     */
    private void loadSettings() {
        displayRows = config.getInt("hologram.settings.display-rows", 10);
        updateInterval = config.getInt("hologram.settings.update-interval", 30);
        lineSpacing = (float) config.getDouble("hologram.settings.line-spacing", 0.3);
        itemTextSpacing = (float) config.getDouble("hologram.settings.item-text-spacing", 0.6);
        itemScale = (float) config.getDouble("hologram.settings.item-scale", 0.5);
    }
    
    /**
     * 加载文本样式
     */
    private void loadTextStyles() {
        textStyles.clear();
        
        // 加载物品名称样式
        float itemNameScale = (float) config.getDouble("hologram.text.item-name.scale", 0.9);
        String itemNameColor = config.getString("hologram.text.item-name.color", "#FFAA00");
        textStyles.put("item-name", new TextStyle(itemNameScale, itemNameColor, ""));
        
        // 加载卖家名称样式
        float sellerNameScale = (float) config.getDouble("hologram.text.seller-name.scale", 0.9);
        String sellerNameColor = config.getString("hologram.text.seller-name.color", "#55FF55");
        textStyles.put("seller-name", new TextStyle(sellerNameScale, sellerNameColor, ""));
        
        // 加载事件类型样式
        float eventTypeScale = (float) config.getDouble("hologram.text.event-type.scale", 0.9);
        String eventTypeColor = config.getString("hologram.text.event-type.color", "#FFFFFF");
        textStyles.put("event-type", new TextStyle(eventTypeScale, eventTypeColor, ""));
        
        // 加载价格样式
        float priceScale = (float) config.getDouble("hologram.text.price.scale", 0.9);
        String priceColor = config.getString("hologram.text.price.color", "#FFFF55");
        textStyles.put("price", new TextStyle(priceScale, priceColor, ""));
        
        // 加载买家名称样式
        float buyerNameScale = (float) config.getDouble("hologram.text.buyer-name.scale", 0.9);
        String buyerNameColor = config.getString("hologram.text.buyer-name.color", "#55FFFF");
        textStyles.put("buyer-name", new TextStyle(buyerNameScale, buyerNameColor, ""));
    }
    
    /**
     * 加载事件文本
     */
    private void loadEventTexts() {
        eventTexts.clear();
        
        // 加载事件文本
        eventTexts.put("list", config.getString("hologram.events.list", "上架"));
        eventTexts.put("bid", config.getString("hologram.events.bid", "竞价更新"));
        eventTexts.put("buy", config.getString("hologram.events.buy", "被购买"));
        eventTexts.put("expired", config.getString("hologram.events.expired", "已到期"));
        eventTexts.put("cancelled", config.getString("hologram.events.cancelled", "已取消"));
    }
    
    /**
     * 加载消息配置
     */
    private void loadMessages() {
        messages.clear();
        
        // 加载消息
        messages.put("title", config.getString("hologram.messages.title", "§6§l拍卖行历史记录"));
        messages.put("no-history", config.getString("hologram.messages.no-history", "§f暂无拍卖历史记录"));
        messages.put("load-error", config.getString("hologram.messages.load-error", "§c拍卖行历史记录加载失败"));
        messages.put("header", config.getString("hologram.messages.header", "§f物品名称 §7| §f拍卖主人 §7| §f事件类型 §7| §f价格 §7| §f事件触发者 §7| §f日期"));
    }
    
    /**
     * 加载价格格式配置
     */
    private void loadPriceFormat() {
        currencyNames.clear();
        
        // 加载货币名称
        currencyNames.put("vault", config.getString("hologram.price-format.currency-names.vault", "金币"));
        currencyNames.put("points", config.getString("hologram.price-format.currency-names.points", "点券"));
        
        // 加载日期格式
        dateFormat = config.getString("hologram.price-format.date-format", "MM-dd HH:mm");
    }
    
    /**
     * 加载TextDisplay设置
     */
    private void loadTextDisplaySettings() {
        displaySettings.clear();
        
        // 加载标题显示设置
        TextDisplaySettings titleSettings = new TextDisplaySettings();
        loadDisplaySettings(titleSettings, "hologram.display.title");
        displaySettings.put("title", titleSettings);
        
        // 加载内容显示设置
        TextDisplaySettings contentSettings = new TextDisplaySettings();
        loadDisplaySettings(contentSettings, "hologram.display.content");
        displaySettings.put("content", contentSettings);
    }
    
    /**
     * 加载特定类型的TextDisplay设置
     */
    private void loadDisplaySettings(TextDisplaySettings settings, String path) {
        // 加载文本对齐方式
        String alignmentStr = config.getString(path + ".alignment", "LEFT");
        try {
            settings.setAlignment(TextDisplay.TextAlignment.valueOf(alignmentStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            settings.setAlignment(TextDisplay.TextAlignment.LEFT);
        }
        
        // 加载行宽度
        settings.setLineWidth(config.getInt(path + ".line-width", 500));
        
        // 加载是否启用阴影
        settings.setShadowed(config.getBoolean(path + ".shadowed", false));
        
        // 加载是否透视
        settings.setSeeThrough(config.getBoolean(path + ".see-through", true));
        
        // 加载文本不透明度
        settings.setTextOpacity(config.getInt(path + ".text-opacity", 255));
        
        // 加载背景颜色
        String bgColor = config.getString(path + ".background-color", "");
        if (bgColor != null && !bgColor.isEmpty()) {
            try {
                if (bgColor.startsWith("#")) {
                    // 处理ARGB格式
                    int alpha = Integer.parseInt(bgColor.substring(1, 3), 16);
                    int red = Integer.parseInt(bgColor.substring(3, 5), 16);
                    int green = Integer.parseInt(bgColor.substring(5, 7), 16);
                    int blue = Integer.parseInt(bgColor.substring(7, 9), 16);
                    settings.setBackgroundColor(Color.fromARGB(alpha, red, green, blue));
                }
            } catch (Exception e) {
            }
        }
        
        // 加载是否使用默认背景
        settings.setDefaultBackground(config.getBoolean(path + ".default-background", false));
        
        // 加载广告牌样式
        String billboardStr = config.getString(path + ".billboard", "CENTER");
        try {
            settings.setBillboard(Display.Billboard.valueOf(billboardStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            settings.setBillboard(Display.Billboard.CENTER);
        }
        
        // 加载亮度设置
        boolean brightnessEnabled = config.getBoolean(path + ".brightness.enabled", false);
        if (brightnessEnabled) {
            int blockLight = config.getInt(path + ".brightness.block-light", 15);
            int skyLight = config.getInt(path + ".brightness.sky-light", 15);
            settings.setBrightness(new Display.Brightness(blockLight, skyLight));
        }
        
        // 加载可见距离
        settings.setViewRange((float) config.getDouble(path + ".view-range", 48.0));
        
        // 加载阴影半径
        settings.setShadowRadius((float) config.getDouble(path + ".shadow-radius", 0.0));
        
        // 加载阴影强度
        settings.setShadowStrength((float) config.getDouble(path + ".shadow-strength", 0.0));
    }
    
    /**
     * 初始化配置文件
     */
    private void initConfig() {
        configFile = new File(plugin.getDataFolder(), "hd_gui.yml");
        if (!configFile.exists()) {
            plugin.saveResource("hd_gui.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 如果配置文件刚创建，设置默认值
        if (!config.contains("hologram")) {
            config.set("hologram.settings.display-rows", 10);
            config.set("hologram.settings.update-interval", 30);
            config.set("hologram.settings.line-spacing", 0.3);
            config.set("hologram.settings.item-text-spacing", 0.6);
            config.set("hologram.settings.item-scale", 0.5);
            
            config.set("hologram.text.item-name.scale", 0.9);
            config.set("hologram.text.item-name.color", "#FFAA00");
            config.set("hologram.text.seller-name.scale", 0.9);
            config.set("hologram.text.seller-name.color", "#55FF55");
            config.set("hologram.text.event-type.scale", 0.9);
            config.set("hologram.text.event-type.color", "#FFFFFF");
            config.set("hologram.text.price.scale", 0.9);
            config.set("hologram.text.price.color", "#FFFF55");
            config.set("hologram.text.buyer-name.scale", 0.9);
            config.set("hologram.text.buyer-name.color", "#55FFFF");
            
            config.set("hologram.events.list", "上架");
            config.set("hologram.events.bid", "竞价更新");
            config.set("hologram.events.buy", "被购买");
            config.set("hologram.events.expired", "已到期");
            config.set("hologram.events.cancelled", "已取消");
            
            config.set("hologram.messages.title", "§6§l拍卖行历史记录");
            config.set("hologram.messages.no-history", "§f暂无拍卖历史记录");
            config.set("hologram.messages.load-error", "§c拍卖行历史记录加载失败");
            config.set("hologram.messages.header", "§f物品名称 §7| §f拍卖主人 §7| §f事件类型 §7| §f价格 §7| §f事件触发者 §7| §f日期");
            
            config.set("hologram.price-format.currency-names.vault", "金币");
            config.set("hologram.price-format.currency-names.points", "点券");
            config.set("hologram.price-format.date-format", "MM-dd HH:mm");
            
            // 默认TextDisplay设置
            config.set("hologram.display.title.alignment", "CENTER");
            config.set("hologram.display.title.line-width", 500);
            config.set("hologram.display.title.shadowed", true);
            config.set("hologram.display.title.see-through", true);
            config.set("hologram.display.title.text-opacity", 255);
            config.set("hologram.display.title.background-color", "#33000000");
            config.set("hologram.display.title.default-background", false);
            config.set("hologram.display.title.billboard", "CENTER");
            config.set("hologram.display.title.brightness.enabled", false);
            config.set("hologram.display.title.brightness.block-light", 15);
            config.set("hologram.display.title.brightness.sky-light", 15);
            config.set("hologram.display.title.view-range", 48.0);
            config.set("hologram.display.title.shadow-radius", 0.0);
            config.set("hologram.display.title.shadow-strength", 0.0);
            
            config.set("hologram.display.content.alignment", "LEFT");
            config.set("hologram.display.content.line-width", 500);
            config.set("hologram.display.content.shadowed", false);
            config.set("hologram.display.content.see-through", true);
            config.set("hologram.display.content.text-opacity", 255);
            config.set("hologram.display.content.background-color", "");
            config.set("hologram.display.content.default-background", false);
            config.set("hologram.display.content.billboard", "CENTER");
            config.set("hologram.display.content.brightness.enabled", false);
            config.set("hologram.display.content.brightness.block-light", 15);
            config.set("hologram.display.content.brightness.sky-light", 15);
            config.set("hologram.display.content.view-range", 48.0);
            config.set("hologram.display.content.shadow-radius", 0.0);
            config.set("hologram.display.content.shadow-strength", 0.0);
            
            try {
                config.save(configFile);
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        // 重新加载配置
        loadConfig();
    }
    
    /**
     * 保存配置
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
        }
    }
    
    public int getDisplayRows() {
        return displayRows;
    }
    
    public int getUpdateInterval() {
        return updateInterval;
    }
    
    public float getLineSpacing() {
        return lineSpacing;
    }
    
    public float getItemTextSpacing() {
        return itemTextSpacing;
    }
    
    public float getItemScale() {
        return itemScale;
    }
    
    /**
     * 获取文本样式
     * @param styleKey 样式键
     * @return 文本样式
     */
    public TextStyle getTextStyle(String styleKey) {
        return textStyles.getOrDefault(styleKey, new TextStyle(0.9f, "#FFFFFF", ""));
    }
    
    /**
     * 获取事件文本
     * @param eventKey 事件键
     * @return 事件文本
     */
    public String getEventText(String eventKey) {
        return eventTexts.getOrDefault(eventKey, "未知事件");
    }
    
    /**
     * 获取消息
     * @param messageKey 消息键
     * @return 消息文本
     */
    public String getMessage(String messageKey) {
        return messages.getOrDefault(messageKey, "");
    }
    
    /**
     * 获取货币名称
     * @param currencyKey 货币键(vault/points)
     * @return 货币名称
     */
    public String getCurrencyName(String currencyKey) {
        return currencyNames.getOrDefault(currencyKey.toLowerCase(), "金币");
    }
    
    /**
     * 获取日期格式
     * @return 日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }
    
    /**
     * 获取TextDisplay设置
     * @param type 设置类型 (title/content)
     * @return TextDisplay设置
     */
    public TextDisplaySettings getDisplaySettings(String type) {
        return displaySettings.getOrDefault(type, new TextDisplaySettings());
    }
    
    /**
     * 获取事件类型文本（带颜色）
     * @param eventKey 事件键
     * @return 带颜色的事件文本
     */
    public String getEventTypeText(String eventKey) {
        String text = getEventText(eventKey);
        String color = "";
        
        // 根据事件类型设置不同颜色
        switch (eventKey) {
            case "list":
                color = "§a"; // 绿色
                break;
            case "bid":
                color = "§b"; // 天蓝色
                break;
            case "buy":
                color = "§6"; // 金色
                break;
            case "expired":
                color = "§c"; // 红色
                break;
            case "cancelled":
                color = "§7"; // 灰色
                break;
            default:
                color = "§f"; // 白色
        }
        
        return color + text;
    }
    
    /**
     * 获取带颜色的事件文本
     * @param eventKey 事件键
     * @param color 颜色
     * @return 带颜色的事件文本
     */
    public String getColoredEventText(String eventKey, ChatColor color) {
        return color + getEventText(eventKey);
    }
    
    /**
     * 文本样式类
     */
    public static class TextStyle {
        private final float scale;
        private final String color;
        private final String backgroundColor;
        
        public TextStyle(float scale, String color, String backgroundColor) {
            this.scale = scale;
            this.color = color;
            this.backgroundColor = backgroundColor;
        }
        
        public float getScale() {
            return scale;
        }
        
        public String getColor() {
            return color;
        }
        
        public String getBackgroundColor() {
            return backgroundColor;
        }
    }
    
    /**
     * TextDisplay设置类
     */
    public static class TextDisplaySettings {
        private TextDisplay.TextAlignment alignment;
        private int lineWidth;
        private boolean shadowed;
        private boolean seeThrough;
        private int textOpacity;
        private Color backgroundColor;
        private boolean defaultBackground;
        private Display.Billboard billboard;
        private Display.Brightness brightness;
        private float viewRange;
        private float shadowRadius;
        private float shadowStrength;
        
        public TextDisplaySettings() {
            this.alignment = TextDisplay.TextAlignment.LEFT;
            this.lineWidth = 500;
            this.shadowed = false;
            this.seeThrough = true;
            this.textOpacity = 255;
            this.backgroundColor = null;
            this.defaultBackground = false;
            this.billboard = Display.Billboard.CENTER;
            this.brightness = null;
            this.viewRange = 48.0f;
            this.shadowRadius = 0.0f;
            this.shadowStrength = 0.0f;
        }
        
        public TextDisplay.TextAlignment getAlignment() {
            return alignment;
        }
        
        public void setAlignment(TextDisplay.TextAlignment alignment) {
            this.alignment = alignment;
        }
        
        public int getLineWidth() {
            return lineWidth;
        }
        
        public void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }
        
        public boolean isShadowed() {
            return shadowed;
        }
        
        public void setShadowed(boolean shadowed) {
            this.shadowed = shadowed;
        }
        
        public boolean isSeeThrough() {
            return seeThrough;
        }
        
        public void setSeeThrough(boolean seeThrough) {
            this.seeThrough = seeThrough;
        }
        
        public int getTextOpacity() {
            return textOpacity;
        }
        
        public void setTextOpacity(int textOpacity) {
            this.textOpacity = textOpacity;
        }
        
        public Color getBackgroundColor() {
            return backgroundColor;
        }
        
        public void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
        
        public boolean isDefaultBackground() {
            return defaultBackground;
        }
        
        public void setDefaultBackground(boolean defaultBackground) {
            this.defaultBackground = defaultBackground;
        }
        
        public Display.Billboard getBillboard() {
            return billboard;
        }
        
        public void setBillboard(Display.Billboard billboard) {
            this.billboard = billboard;
        }
        
        public Display.Brightness getBrightness() {
            return brightness;
        }
        
        public void setBrightness(Display.Brightness brightness) {
            this.brightness = brightness;
        }
        
        public float getViewRange() {
            return viewRange;
        }
        
        public void setViewRange(float viewRange) {
            this.viewRange = viewRange;
        }
        
        public float getShadowRadius() {
            return shadowRadius;
        }
        
        public void setShadowRadius(float shadowRadius) {
            this.shadowRadius = shadowRadius;
        }
        
        public float getShadowStrength() {
            return shadowStrength;
        }
        
        public void setShadowStrength(float shadowStrength) {
            this.shadowStrength = shadowStrength;
        }
        
        /**
         * 应用设置到TextDisplay实体
         * @param textDisplay TextDisplay实体
         */
        public void applyToTextDisplay(TextDisplay textDisplay) {
            // 设置文本对齐方式
            textDisplay.setAlignment(alignment);
            
            // 设置行宽度
            textDisplay.setLineWidth(lineWidth);
            
            // 设置是否启用阴影
            textDisplay.setShadowed(shadowed);
            
            // 设置是否透视
            textDisplay.setSeeThrough(seeThrough);
            
            // 设置文本不透明度
            textDisplay.setTextOpacity((byte) textOpacity);
            
            // 设置背景颜色
            if (backgroundColor != null) {
                textDisplay.setBackgroundColor(backgroundColor);
            }
            
            // 设置是否使用默认背景
            textDisplay.setDefaultBackground(defaultBackground);
            
            // 设置广告牌样式
            textDisplay.setBillboard(billboard);
            
            // 设置亮度
            if (brightness != null) {
                textDisplay.setBrightness(brightness);
            }
            
            // 设置可见距离
            textDisplay.setViewRange(viewRange);
            
            // 设置阴影半径
            textDisplay.setShadowRadius(shadowRadius);
            
            // 设置阴影强度
            textDisplay.setShadowStrength(shadowStrength);
        }
    }
} 