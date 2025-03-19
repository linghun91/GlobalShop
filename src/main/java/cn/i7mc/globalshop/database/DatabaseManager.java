package cn.i7mc.globalshop.database;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import org.bukkit.ChatColor;
import java.util.HashMap;

public class DatabaseManager {
    private final GlobalShop plugin;
    private Connection connection;

    public DatabaseManager(GlobalShop plugin) {
        this.plugin = plugin;
        connect();
        createTables();
    }

    private void connect() {
        try {
            // 如果已经有连接并且连接有效，则不需要重新连接
            if (connection != null && !connection.isClosed()) {
                return;
            }
            
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + new File(plugin.getDataFolder(), plugin.getConfigManager().getDatabaseFile()).getAbsolutePath();
            connection = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
        }
    }

    /**
     * 检查数据库连接状态，如果连接已关闭则重新连接
     * @return 连接是否有效
     */
    private boolean checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * 获取数据库连接
     * @return 数据库连接对象，可能为null
     */
    public Connection getConnection() {
        if (!checkConnection()) {
            return null;
        }
        return connection;
    }

    private void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS auction_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                seller_uuid TEXT NOT NULL,
                seller_name TEXT NOT NULL,
                item_data TEXT NOT NULL,
                start_price REAL NOT NULL,
                buy_now_price REAL,
                current_price REAL NOT NULL,
                current_bidder TEXT,
                current_bidder_name TEXT,
                currency_type TEXT NOT NULL,
                list_time INTEGER NOT NULL,
                start_time INTEGER NOT NULL,
                end_time INTEGER NOT NULL,
                sold_time INTEGER,
                status TEXT NOT NULL
            )
        """;
        
        String pendingItemsTable = """
            CREATE TABLE IF NOT EXISTS pending_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_uuid TEXT NOT NULL,
                item_data TEXT NOT NULL,
                reason TEXT NOT NULL,
                created_time INTEGER NOT NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            stmt.execute(pendingItemsTable);
            // 检查并添加新字段
            checkAndAddColumns();
        } catch (SQLException e) {
        }
    }
    
    // 检查并添加新字段到现有表
    private void checkAndAddColumns() {
        try {
            // 检查current_bidder_name字段
            if (!columnExists("auction_items", "current_bidder_name")) {
                executeUpdate("ALTER TABLE auction_items ADD COLUMN current_bidder_name TEXT");
            }
            
            // 检查list_time字段
            if (!columnExists("auction_items", "list_time")) {
                executeUpdate("ALTER TABLE auction_items ADD COLUMN list_time INTEGER DEFAULT " + System.currentTimeMillis());
            }
            
            // 检查sold_time字段
            if (!columnExists("auction_items", "sold_time")) {
                executeUpdate("ALTER TABLE auction_items ADD COLUMN sold_time INTEGER");
            }
        } catch (SQLException e) {
        }
    }
    
    // 检查字段是否存在
    private boolean columnExists(String table, String column) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, table, column)) {
            return rs.next();
        }
    }
    
    // 执行更新SQL
    private void executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
        }
    }

    // 物品序列化
    private String serializeItem(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            // 使用serialize方法保存物品的全部属性，包括NBT数据和Lore
            dataOutput.writeObject(item.serialize());
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 物品反序列化
    private ItemStack deserializeItem(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            // 使用ItemStack.deserialize方法还原物品的全部属性
            @SuppressWarnings("unchecked")
            Map<String, Object> serialized = (Map<String, Object>) dataInput.readObject();
            dataInput.close();
            
            ItemStack item = ItemStack.deserialize(serialized);
            
            // 确保元数据正确恢复
            if (item != null && !item.hasItemMeta() && serialized.containsKey("meta")) {
                try {
                    // 尝试手动恢复元数据
                    ItemMeta meta = (ItemMeta) serialized.get("meta");
                    item.setItemMeta(meta);
                } catch (Exception e) {
                }
            }
            
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 创建拍卖物品
    public int createAuctionItem(AuctionItem item) {
        // 检查连接
        if (!checkConnection()) {
            return -1;
        }
        
        String sql = "INSERT INTO auction_items (seller_uuid, seller_name, item_data, " +
                "start_price, buy_now_price, current_price, current_bidder, current_bidder_name, currency_type, " +
                "list_time, start_time, end_time, sold_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            try {
                pstmt.setString(1, item.getSellerUuid().toString());
                pstmt.setString(2, item.getSellerName());
                pstmt.setString(3, serializeItem(item.getItem()));
                pstmt.setDouble(4, item.getStartPrice());
                pstmt.setDouble(5, item.getBuyNowPrice());
                pstmt.setDouble(6, item.getCurrentPrice());
                pstmt.setString(7, item.getCurrentBidder() != null ? item.getCurrentBidder().toString() : null);
                pstmt.setString(8, item.getCurrentBidderName());
                pstmt.setString(9, item.getCurrencyType());
                pstmt.setLong(10, item.getListTime());
                pstmt.setLong(11, item.getStartTime());
                pstmt.setLong(12, item.getEndTime());
                pstmt.setLong(13, item.getSoldTime());
                pstmt.setString(14, item.getStatus());
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                try {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
        }
        return -1;
    }

    // 获取拍卖物品
    public AuctionItem getAuctionItem(int id) {
        // 检查连接
        if (!checkConnection()) {
            return null;
        }
        
        String sql = "SELECT * FROM auction_items WHERE id = ?";
        
        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            try {
                pstmt.setInt(1, id);
                
                ResultSet rs = pstmt.executeQuery();
                try {
                    if (rs.next()) {
                        // 获取新字段的值，如果不存在则使用默认值
                        long listTime = getColumnLong(rs, "list_time", System.currentTimeMillis());
                        long soldTime = getColumnLong(rs, "sold_time", 0);
                        String bidderName = getColumnString(rs, "current_bidder_name", null);
                        
                        AuctionItem item = new AuctionItem(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("seller_uuid")),
                            rs.getString("seller_name"),
                            deserializeItem(rs.getString("item_data")),
                            rs.getDouble("start_price"),
                            rs.getDouble("buy_now_price"),
                            rs.getDouble("current_price"),
                            rs.getString("currency_type"),
                            listTime,
                            rs.getLong("start_time"),
                            rs.getLong("end_time"),
                            soldTime,
                            rs.getString("status")
                        );
                        
                        // 设置当前出价者
                        if (rs.getString("current_bidder") != null && !rs.getString("current_bidder").isEmpty()) {
                            item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                        }
                        
                        return item;
                    } else {
                    }
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // 辅助方法：安全获取Long字段值
    private long getColumnLong(ResultSet rs, String columnName, long defaultValue) {
        try {
            int index = rs.findColumn(columnName);
            long value = rs.getLong(index);
            return rs.wasNull() ? defaultValue : value;
        } catch (SQLException e) {
            return defaultValue;
        }
    }
    
    // 辅助方法：安全获取String字段值
    private String getColumnString(ResultSet rs, String columnName, String defaultValue) {
        try {
            int index = rs.findColumn(columnName);
            String value = rs.getString(index);
            return rs.wasNull() ? defaultValue : value;
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    /**
     * 删除拍卖物品记录
     * @param id 物品ID
     * @return 删除是否成功
     */
    public boolean deleteAuctionItem(int id) {
        // 检查连接
        if (!checkConnection()) {
            return false;
        }
        String sql = "DELETE FROM auction_items WHERE id = ?";
        
        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            try {
                pstmt.setInt(1, id);
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新拍卖物品
    public boolean updateAuctionItem(AuctionItem item) {
        // 检查连接
        if (!checkConnection()) {
            return false;
        }
        
        String sql = "UPDATE auction_items SET current_price = ?, current_bidder = ?, current_bidder_name = ?, status = ?, sold_time = ? WHERE id = ?";
        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            try {
                pstmt.setDouble(1, item.getCurrentPrice());
                pstmt.setString(2, item.getCurrentBidder() != null ? item.getCurrentBidder().toString() : null);
                pstmt.setString(3, item.getCurrentBidderName());
                pstmt.setString(4, item.getStatus());
                pstmt.setLong(5, item.getSoldTime());
                pstmt.setInt(6, item.getId());
                
                int affectedRows = pstmt.executeUpdate();
                boolean success = affectedRows > 0;
                
                if (!success) {
                }
                
                return success;
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取活跃的拍卖物品列表
    public List<AuctionItem> getActiveAuctionItems(int page, int size) {
        // 检查连接
        if (!checkConnection()) {
            return new ArrayList<>();
        }
        
        List<AuctionItem> items = new ArrayList<>();
        String sql = """
            SELECT * FROM auction_items
            WHERE status = 'ACTIVE' AND end_time > ?
            ORDER BY end_time ASC
            LIMIT ? OFFSET ?
        """;

        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            try {
                pstmt.setLong(1, System.currentTimeMillis()); // 添加当前时间作为过滤条件
                pstmt.setInt(2, size);
                pstmt.setInt(3, (page - 1) * size);
                
                ResultSet rs = pstmt.executeQuery();
                try {
                    while (rs.next()) {
                        // 获取新字段的值，如果不存在则使用默认值
                        long listTime = getColumnLong(rs, "list_time", System.currentTimeMillis());
                        long soldTime = getColumnLong(rs, "sold_time", 0);
                        String bidderName = getColumnString(rs, "current_bidder_name", null);
                        
                        AuctionItem item = new AuctionItem(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("seller_uuid")),
                            rs.getString("seller_name"),
                            deserializeItem(rs.getString("item_data")),
                            rs.getDouble("start_price"),
                            rs.getDouble("buy_now_price"),
                            rs.getDouble("current_price"),
                            rs.getString("currency_type"),
                            listTime,
                            rs.getLong("start_time"),
                            rs.getLong("end_time"),
                            soldTime,
                            rs.getString("status")
                        );
                        
                        // 设置当前出价者
                        if (rs.getString("current_bidder") != null && !rs.getString("current_bidder").isEmpty()) {
                            item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                        }
                        
                        items.add(item);
                    }
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        } catch (SQLException e) {
        }
        return items;
    }

    // 获取玩家的拍卖物品
    public List<AuctionItem> getPlayerAuctionItems(UUID playerUuid) {
        List<AuctionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM auction_items WHERE seller_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 获取新字段的值，如果不存在则使用默认值
                    long listTime = getColumnLong(rs, "list_time", System.currentTimeMillis());
                    long soldTime = getColumnLong(rs, "sold_time", 0);
                    String bidderName = getColumnString(rs, "current_bidder_name", null);
                    
                    // 使用完整构造函数创建AuctionItem对象
                    AuctionItem item = new AuctionItem(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("seller_uuid")),
                        rs.getString("seller_name"),
                        deserializeItem(rs.getString("item_data")),
                        rs.getDouble("start_price"),
                        rs.getDouble("buy_now_price"),
                        rs.getDouble("current_price"),
                        rs.getString("currency_type"),
                        listTime,
                        rs.getLong("start_time"),
                        rs.getLong("end_time"),
                        soldTime,
                        rs.getString("status")
                    );
                    
                    // 设置当前出价者UUID和名称
                    if (rs.getString("current_bidder") != null && !rs.getString("current_bidder").isEmpty()) {
                        item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                    }
                    
                    items.add(item);
                }
            }
        } catch (SQLException e) {
        }
        return items;
    }

    // 获取过期的拍卖物品
    public List<AuctionItem> getExpiredAuctionItems() {
        List<AuctionItem> items = new ArrayList<>();
        String sql = """
            SELECT * FROM auction_items
            WHERE status = 'ACTIVE' AND end_time < ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 获取新字段的值，如果不存在则使用默认值
                    long listTime = getColumnLong(rs, "list_time", System.currentTimeMillis());
                    long soldTime = getColumnLong(rs, "sold_time", 0);
                    String bidderName = getColumnString(rs, "current_bidder_name", null);
                    
                    AuctionItem item = new AuctionItem(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("seller_uuid")),
                        rs.getString("seller_name"),
                        deserializeItem(rs.getString("item_data")),
                        rs.getDouble("start_price"),
                        rs.getDouble("buy_now_price"),
                        rs.getDouble("current_price"),
                        rs.getString("currency_type"),
                        listTime,
                        rs.getLong("start_time"),
                        rs.getLong("end_time"),
                        soldTime,
                        rs.getString("status")
                    );
                    
                    // 设置当前出价者
                    if (rs.getString("current_bidder") != null && !rs.getString("current_bidder").isEmpty()) {
                        item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                    }
                    
                    items.add(item);
                }
            }
        } catch (SQLException e) {
        }
        return items;
    }
    
    // 重载的存储待领取物品方法，不需要reason参数
    public boolean storePendingItem(UUID playerUuid, ItemStack item) {
        return storePendingItem(playerUuid, item, "MAILBOX_ITEM");
    }
    
    // 存储待领取物品
    public boolean storePendingItem(UUID playerUuid, ItemStack item, String reason) {
        // 检查物品是否已经存在于pending_items表中
        // 通过比较序列化后的物品数据来防止完全相同的物品被添加多次
        String serializedItem = serializeItem(item);
        String checkSql = "SELECT COUNT(*) FROM pending_items WHERE player_uuid = ? AND item_data = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, playerUuid.toString());
            checkStmt.setString(2, serializedItem);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 物品已存在，记录日志并返回true以避免上层处理逻辑出错
                    String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? 
                            item.getItemMeta().getDisplayName() : item.getType().toString();
                    return true;
                }
            }
        } catch (SQLException e) {
            // 继续处理，不要因为检查出错而阻止添加物品
        }
        
        // 物品不存在，执行插入操作
        String sql = "INSERT INTO pending_items (player_uuid, item_data, reason, created_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, serializedItem);
            pstmt.setString(3, reason);
            pstmt.setLong(4, System.currentTimeMillis());
            
            int result = pstmt.executeUpdate();
            boolean success = result > 0;
            
            // 只记录严重错误
            if (!success) {
            }
            
            return success;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // 获取玩家的待领取物品
    public List<ItemStack> getPendingItems(UUID playerUuid) {
        List<ItemStack> items = new ArrayList<>();
        String sql = "SELECT item_data FROM pending_items WHERE player_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack item = deserializeItem(rs.getString("item_data"));
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
        }
        return items;
    }
    
    // 获取玩家待领取物品的详细信息，并转换为AuctionItem格式方便统一处理
    public List<AuctionItem> getPendingItemsAsAuctionItems(UUID playerUuid) {
        List<AuctionItem> auctionItems = new ArrayList<>();
        String sql = "SELECT id, player_uuid, item_data, reason, created_time FROM pending_items WHERE player_uuid = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String itemData = rs.getString("item_data");
                    String reason = rs.getString("reason");
                    long createdTime = rs.getLong("created_time");
                    
                    ItemStack itemStack = deserializeItem(itemData);
                    if (itemStack != null) {
                        // 创建AuctionItem对象表示邮箱物品
                        // 使用现有构造函数，对于不需要的字段使用默认值
                        AuctionItem item = new AuctionItem(
                            id, // 使用pending_items表的id
                            playerUuid, // 玩家UUID
                            Bukkit.getOfflinePlayer(playerUuid).getName(), // 尝试获取玩家名称
                            itemStack, // 物品本身
                            0.0, // 起始价格(不重要)
                            0.0, // 一口价(不重要)
                            0.0, // 当前价格(不重要)
                            "MONEY", // 货币类型(不重要)
                            createdTime, // 开始时间
                            createdTime + 86400000, // 结束时间，设为创建时间+1天
                            "MAILBOX_PENDING" // 特殊状态标记为待领取物品
                        );
                        
                        // 添加到列表
                        auctionItems.add(item);
                    }
                }
            }
        } catch (SQLException e) {
        }
        
        return auctionItems;
    }
    
    // 根据ID删除待领取物品
    public boolean deletePendingItemById(int id) {
        String sql = "DELETE FROM pending_items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            boolean success = result > 0;
            
            if (success) {
            } else {
            }
            
            return success;
        } catch (SQLException e) {
        }
        return false;
    }
    
    // 删除玩家的所有待领取物品
    public boolean deletePendingItems(UUID playerUuid) {
        String sql = "DELETE FROM pending_items WHERE player_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            int result = pstmt.executeUpdate();
            boolean success = result > 0;
            
            if (success) {
            } else {
            }
            
            return success;
        } catch (SQLException e) {
        }
        return false;
    }

    // 搜索拍卖物品
    public List<AuctionItem> searchAuctionItems(String keyword, int page, int size) {
        List<AuctionItem> items = new ArrayList<>();
        List<AuctionItem> allItems = new ArrayList<>();
        
        // 先获取所有活跃的物品
        String sql = """
            SELECT * FROM auction_items
            WHERE status = 'ACTIVE'
            ORDER BY end_time ASC
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // 获取新字段的值，如果不存在则使用默认值
                long listTime = getColumnLong(rs, "list_time", System.currentTimeMillis());
                long soldTime = getColumnLong(rs, "sold_time", 0);
                String bidderName = getColumnString(rs, "current_bidder_name", null);
                
                AuctionItem item = new AuctionItem(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("seller_uuid")),
                    rs.getString("seller_name"),
                    deserializeItem(rs.getString("item_data")),
                    rs.getDouble("start_price"),
                    rs.getDouble("buy_now_price"),
                    rs.getDouble("current_price"),
                    rs.getString("currency_type"),
                    listTime,
                    rs.getLong("start_time"),
                    rs.getLong("end_time"),
                    soldTime,
                    rs.getString("status")
                );
                
                // 设置当前出价者
                if (rs.getString("current_bidder") != null && !rs.getString("current_bidder").isEmpty()) {
                    item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                }
                
                allItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return items;
        }
        
        // 处理关键词 (去除颜色代码)
        String processedKeyword = keyword.replace("&", "").replace("§", "").toLowerCase();
        
        // 是否是中文搜索
        boolean isChineseSearch = plugin.getLanguageManager().canMatchChineseKeyword(processedKeyword);
        
        // 如果是中文搜索，获取匹配的物品ID
        Map<String, String> chineseMatches = new HashMap<>();
        if (isChineseSearch) {
            chineseMatches = plugin.getLanguageManager().findPossibleItemIds(processedKeyword);
        }
        
        // 遍历所有物品进行匹配
        for (AuctionItem item : allItems) {
            ItemStack itemStack = item.getItem();
            if (itemStack == null) continue;
            
            boolean matched = false;
            
            // 1. 检查自定义显示名称
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName().toLowerCase());
                if (displayName.contains(processedKeyword)) {
                    matched = true;
                }
            }
            
            // 2. 如果是中文搜索，检查Material名称是否匹配任何中文项
            if (!matched && isChineseSearch) {
                String materialName = itemStack.getType().name();
                
                // 简单的检查：将Material名转为小写并替换下划线
                String simpleMaterialName = materialName.toLowerCase().replace("_", "");
                
                // 对每个中文匹配项检查
                for (String enId : chineseMatches.keySet()) {
                    // 将物品ID转为适合与Material比较的格式
                    String simpleEnId = enId.toLowerCase().replace("_", "");
                    
                    // 如果Material名包含物品ID，则匹配成功
                    if (simpleMaterialName.contains(simpleEnId)) {
                        matched = true;
                        break;
                    }
                }
            }
            
            // 如果匹配成功，添加到结果列表
            if (matched) {
                items.add(item);
            }
        }
        
        // 排序结果
        items.sort(Comparator.comparing(AuctionItem::getEndTime));
        
        // 分页处理
        int totalItems = items.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        
        List<AuctionItem> pagedItems = new ArrayList<>();
        if (startIndex < totalItems) {
            pagedItems = items.subList(startIndex, endIndex);
        }
        
        return pagedItems;
    }
    
    // 获取搜索结果总数
    public int getSearchResultCount(String keyword) {
        List<AuctionItem> allItems = new ArrayList<>();
        
        // 先获取所有活跃的物品
        String sql = "SELECT * FROM auction_items WHERE status = 'ACTIVE'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AuctionItem item = new AuctionItem(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("seller_uuid")),
                    rs.getString("seller_name"),
                    deserializeItem(rs.getString("item_data")),
                    rs.getDouble("start_price"),
                    rs.getDouble("buy_now_price"),
                    rs.getDouble("current_price"),
                    rs.getString("currency_type"),
                    rs.getLong("start_time"),
                    rs.getLong("end_time"),
                    rs.getString("status")
                );
                
                allItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        
        // 处理关键词 (去除颜色代码)
        String processedKeyword = keyword.replace("&", "").replace("§", "").toLowerCase();
        
        // 是否是中文搜索
        boolean isChineseSearch = plugin.getLanguageManager().canMatchChineseKeyword(processedKeyword);
        
        // 如果是中文搜索，获取匹配的物品ID
        Map<String, String> chineseMatches = new HashMap<>();
        if (isChineseSearch) {
            chineseMatches = plugin.getLanguageManager().findPossibleItemIds(processedKeyword);
        }
        
        // 计数变量
        int count = 0;
        
        // 遍历所有物品进行匹配
        for (AuctionItem item : allItems) {
            ItemStack itemStack = item.getItem();
            if (itemStack == null) continue;
            
            boolean matched = false;
            
            // 1. 检查自定义显示名称
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName().toLowerCase());
                if (displayName.contains(processedKeyword)) {
                    matched = true;
                }
            }
            
            // 2. 如果是中文搜索，检查Material名称是否匹配任何中文项
            if (!matched && isChineseSearch) {
                String materialName = itemStack.getType().name();
                
                // 简单的检查：将Material名转为小写并替换下划线
                String simpleMaterialName = materialName.toLowerCase().replace("_", "");
                
                // 对每个中文匹配项检查
                for (String enId : chineseMatches.keySet()) {
                    // 将物品ID转为适合与Material比较的格式
                    String simpleEnId = enId.toLowerCase().replace("_", "");
                    
                    // 如果Material名包含物品ID，则匹配成功
                    if (simpleMaterialName.contains(simpleEnId)) {
                        matched = true;
                        break;
                    }
                }
            }
            
            // 如果匹配成功，增加计数
            if (matched) {
                count++;
            }
        }
        
        return count;
    }

    // 获取活跃拍卖物品总数
    public int getTotalActiveItems() {
        String sql = "SELECT COUNT(*) FROM auction_items WHERE status = 'ACTIVE'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
        }
        return 0;
    }

    // 搜索物品根据名称
    public List<AuctionItem> searchItemsByName(String keyword, int page, int pageSize) {
        List<AuctionItem> items = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM auction_items WHERE status = 'ACTIVE' AND item_name LIKE ? ORDER BY created_time DESC LIMIT ? OFFSET ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setInt(2, pageSize);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    UUID sellerUuid = UUID.fromString(rs.getString("seller_uuid"));
                    String sellerName = rs.getString("seller_name");
                    double price = rs.getDouble("price");
                    String itemName = rs.getString("item_name");
                    String status = rs.getString("status");
                    String currencyType = rs.getString("currency_type");
                    long createdTime = rs.getLong("created_time");
                    long endTime = rs.getLong("end_time");
                    
                    // 反序列化物品数据
                    ItemStack itemStack = null;
                    byte[] itemBytes = rs.getBytes("item_data");
                    if (itemBytes != null) {
                        try {
                            // 使用BukkitObjectInputStream进行反序列化
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(itemBytes);
                            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                            itemStack = (ItemStack) dataInput.readObject();
                            dataInput.close();
                        } catch (Exception e) {
                        }
                    }
                    
                    if (itemStack != null) {
                        // 创建AuctionItem对象
                        AuctionItem item = new AuctionItem(
                            id, sellerUuid, sellerName, itemStack,
                            rs.getDouble("start_price"), rs.getDouble("buy_now_price"), price,
                            currencyType, createdTime, endTime, status
                        );
                        
                        // 处理竞价相关信息
                        String bidderUuid = rs.getString("current_bidder_uuid");
                        if (bidderUuid != null && !bidderUuid.isEmpty()) {
                            item.setCurrentBidder(UUID.fromString(bidderUuid));
                        }
                        
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
        }
        
        return items;
    }

    // 获取全部活跃的拍卖物品列表（无分页、排序）
    public List<AuctionItem> getAllActiveAuctionItems() {
        // 检查连接
        if (!checkConnection()) {
            return new ArrayList<>();
        }
        
        List<AuctionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM auction_items WHERE status = 'ACTIVE'";

        try {
            // 确保数据库连接没有关闭
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    AuctionItem item = new AuctionItem(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("seller_uuid")),
                        rs.getString("seller_name"),
                        deserializeItem(rs.getString("item_data")),
                        rs.getDouble("start_price"),
                        rs.getDouble("buy_now_price"),
                        rs.getDouble("current_price"),
                        rs.getString("currency_type"),
                        rs.getLong("start_time"),
                        rs.getLong("end_time"),
                        rs.getString("status")
                    );
                    
                    // 设置当前出价者
                    if (rs.getString("current_bidder") != null) {
                        item.setCurrentBidder(UUID.fromString(rs.getString("current_bidder")));
                    }
                    
                    items.add(item);
                }
            }
        } catch (SQLException e) {
        }
        return items;
    }

    // 统计玩家当前有效的拍卖物品数量
    public int countPlayerActiveAuctions(UUID playerUuid) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM auction_items WHERE seller_uuid = ? AND status = 'ACTIVE'";
        
        try {
            if (!checkConnection()) {
                return count;
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerUuid.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
        }
        
        return count;
    }
} 