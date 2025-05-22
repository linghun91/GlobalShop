package cn.i7mc.globalshop.utils;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.Material;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minecraft语言管理类，负责处理原版物品中英文翻译
 */
public class MinecraftLanguageManager {
    private final GlobalShop plugin;
    private Map<String, String> enToZhMap = new HashMap<>();
    private Map<String, String> zhToEnMap = new HashMap<>();
    
    /**
     * 构造一个Minecraft语言管理器
     * 
     * @param plugin 插件实例
     */
    public MinecraftLanguageManager(GlobalShop plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }
    
    /**
     * 加载语言文件
     */
    private void loadLanguageFile() {
        // 检查文件是否存在，不存在则创建
        File langFile = new File(plugin.getDataFolder(), "minecraft_lang.yml");
        if (!langFile.exists()) {
            try (InputStream in = plugin.getResource("minecraft_lang.yml")) {
                if (in != null) {
                    Files.copy(in, langFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                return;
            }
        }
        
        // 使用正则表达式解析文件
        Pattern pattern = Pattern.compile("\"((?:item|block)\\.minecraft\\.[^\"]+)\":\\s*\"([^\"]+)\"");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(langFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String key = matcher.group(1);
                    String zhValue = matcher.group(2);
                    
                    // 提取物品id，无论是item还是block前缀
                    String itemId = key.replaceAll("(item|block)\\.minecraft\\.", "");
                    
                    // 将英文到中文的映射存储在map中
                    enToZhMap.put(itemId, zhValue);
                    // 同时创建中文到英文的映射，用于反向查找
                    zhToEnMap.put(zhValue, itemId);
                }
            }
        } catch (IOException e) {
        }
    }
    
    /**
     * 根据物品ID获取中文名称
     * 
     * @param itemId 物品ID，例如 "diamond"
     * @return 对应的中文名称，不存在则返回原始ID
     */
    public String getChineseName(String itemId) {
        return enToZhMap.getOrDefault(itemId.toLowerCase(), itemId);
    }
    
    /**
     * 根据物品Material获取中文名称
     * 
     * @param material 物品Material
     * @return 对应的中文名称，不存在则返回Material名称
     */
    public String getChineseName(Material material) {
        if (material == null) return "";
        
        String materialName = material.name();
        
        // 1. 尝试直接匹配完整名称 (如REDSTONE_BLOCK -> redstone_block)
        String fullItemId = materialName.toLowerCase();
        String chineseName = enToZhMap.get(fullItemId);
        if (chineseName != null) {
            return chineseName;
        }
        
        // 2. 尝试分解名称匹配
        // 例如对于REDSTONE_BLOCK，我们需要检查是否存在"redstone block"的翻译
        String nameWithSpaces = materialName.toLowerCase().replace('_', ' ');
        chineseName = enToZhMap.get(nameWithSpaces);
        if (chineseName != null) {
            return chineseName;
        }
        
        // 3. 尝试找到基础名称 (例如REDSTONE_BLOCK -> redstone)
        // 许多复合物品的名称在中文里可能是"XX块"这样的形式
        for (String key : enToZhMap.keySet()) {
            if (materialName.toLowerCase().contains(key.toLowerCase())) {
                String baseName = enToZhMap.get(key);
                // 对于像REDSTONE_BLOCK这样的物品，我们可能想返回"红石块"而不只是"红石"
                if (materialName.toLowerCase().equals(key.toLowerCase() + "_block")) {
                    return baseName + "块";
                }
                if (materialName.toLowerCase().contains(key.toLowerCase())) {
                    // 这里只返回基础名称，可能不够准确
                    return baseName;
                }
            }
        }
        
        // 4. 如果前面都没匹配到，尝试将Material名称直接格式化为更友好的显示
        String formatted = formatMaterialName(material);
        return formatted;
    }
    
    /**
     * 检查一个中文关键字是否能匹配物品
     * 
     * @param chineseKeyword 中文搜索关键字
     * @return 如果能匹配到任何物品，返回true
     */
    public boolean canMatchChineseKeyword(String chineseKeyword) {
        if (chineseKeyword == null || chineseKeyword.isEmpty()) {
            return false;
        }
        
        String keyword = chineseKeyword.toLowerCase();
        
        // 检查是否有任何中文名称包含该关键字
        for (String zhName : zhToEnMap.keySet()) {
            if (zhName.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 根据中文关键字查找可能的物品ID集合
     * 
     * @param chineseKeyword 中文搜索关键字
     * @return 可能匹配的物品ID列表
     */
    public Map<String, String> findPossibleItemIds(String chineseKeyword) {
        Map<String, String> result = new HashMap<>();
        
        if (chineseKeyword == null || chineseKeyword.isEmpty()) {
            return result;
        }
        
        String keyword = chineseKeyword.toLowerCase();
        
        // 查找所有中文名称包含关键词的物品
        for (Map.Entry<String, String> entry : enToZhMap.entrySet()) {
            String enId = entry.getKey();
            String zhName = entry.getValue();
            
            // 如果中文名称包含搜索关键词
            if (zhName.toLowerCase().contains(keyword)) {
                result.put(enId, zhName);
            }
        }
        
        return result;
    }
    
    /**
     * 格式化Material名称为更友好的显示形式
     * 
     * @param material 物品Material
     * @return 格式化后的名称
     */
    private String formatMaterialName(Material material) {
        if (material == null) return "";
        
        String name = material.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
} 