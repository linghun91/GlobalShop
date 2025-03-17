package cn.i7mc.globalshop.commands;

import cn.i7mc.globalshop.GlobalShop;
import cn.i7mc.globalshop.models.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuctionCommand implements CommandExecutor, TabCompleter {
    private final GlobalShop plugin;
    private final List<String> subCommands = Arrays.asList(
        "help", "open", "sell", "buy", "search", "my", "collect", "reload", "close", "checkexpired"
    );
    private final List<String> currencyTypes = Arrays.asList("1", "2");

    public AuctionCommand(GlobalShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家使用!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // 默认打开主菜单
            plugin.getGuiManager().openMainMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                return handleHelpCommand(player);
            case "open":
                plugin.getGuiManager().openMainMenu(player);
                return true;
            case "sell":
                return handleSellCommand(player, args);
            case "buy":
                return handleBuyCommand(player, args);
            case "search":
                return handleSearchCommand(player, args);
            case "my":
                return handleMyCommand(player);
            case "collect":
                return handleCollectCommand(player);
            case "reload":
                return handleReloadCommand(player);
            case "close":
                return handleCloseCommand(player);
            case "checkexpired":
                return handleCheckExpiredCommand(player);
            default:
                player.sendMessage(ChatColor.RED + "未知命令! 使用 /auction help查看帮助");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // 第一个参数 - 子命令
        if (args.length == 1) {
            List<String> commands = new ArrayList<>(subCommands);
            
            // 如果没有管理员权限，移除管理员命令
            if (sender instanceof Player && !sender.hasPermission("globalshop.admin")) {
                commands.remove("reload");
                commands.remove("close");
            }
            
            return StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        // 后续参数根据不同子命令提供不同的补全
        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "sell":
                    if (args.length == 2) {
                        // 第二个参数 - 起拍价
                        completions.add("<起拍价:数值>");
                    } else if (args.length == 3) {
                        // 第三个参数 - 一口价
                        completions.add("<一口价:数值>");
                    } else if (args.length == 4) {
                        // 第四个参数 - 货币类型
                        return StringUtil.copyPartialMatches(args[3], currencyTypes, completions);
                    }
                    break;
                
                case "buy":
                    if (args.length == 2) {
                        // 第二个参数 - 物品ID
                        completions.add("<物品ID:数值>");
                    }
                    break;
                
                case "search":
                    if (args.length == 2) {
                        // 第二个参数 - 搜索关键词
                        completions.add("<关键词:文本>");
                    }
                    break;
            }
        }

        // 按字母排序
        Collections.sort(completions);
        return completions;
    }

    private boolean handleHelpCommand(Player player) {
        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "===== 拍卖行帮助 =====");
        player.sendMessage(ChatColor.YELLOW + "/auction help - 显示此帮助信息");
        player.sendMessage(ChatColor.YELLOW + "/auction open - 打开拍卖行");
        player.sendMessage(ChatColor.YELLOW + "/auction sell <起拍价> [一口价] [货币类型] - 上架物品 (1=金币, 2=点券)");
        player.sendMessage(ChatColor.YELLOW + "/auction buy <物品ID> - 购买物品");
        player.sendMessage(ChatColor.YELLOW + "/auction search <关键词> - 搜索物品");
        player.sendMessage(ChatColor.YELLOW + "/auction my - 查看我的拍卖和物品邮箱");
        player.sendMessage(ChatColor.YELLOW + "/auction collect - 领取待领取物品");
        
        // 只向管理员显示管理员命令
        if (player.hasPermission("globalshop.admin")) {
            player.sendMessage(ChatColor.RED + "===== 管理员命令 =====");
            player.sendMessage(ChatColor.YELLOW + "/auction reload - 重新加载配置文件");
            player.sendMessage(ChatColor.YELLOW + "/auction close - " + ChatColor.RED + "[测试] " + 
                             ChatColor.YELLOW + "强制关闭所有拍卖物品并标记为过期");
        }
    }

    private boolean handleCollectCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.use")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用该命令!");
            return true;
        }
        
        // 打开物品邮箱界面
        plugin.getGuiManager().openMyMailboxMenu(player, 1);
        player.sendMessage(ChatColor.GREEN + "已打开物品邮箱，你可以在这里领取所有待领取的物品。");
        return true;
    }

    private boolean handleBuyCommand(Player player, String[] args) {
        // 检查参数数量
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "用法: /auction buy <物品ID>");
            return true;
        }
        
        try {
            // 解析物品ID
            int itemId = Integer.parseInt(args[1]);
            
            // 获取拍卖物品
            AuctionItem item = plugin.getDatabaseManager().getAuctionItem(itemId);
            
            if (item == null) {
                player.sendMessage(ChatColor.RED + "物品不存在或已被购买!");
                return true;
            }
            
            if (!item.isActive() || item.isExpired()) {
                player.sendMessage(ChatColor.RED + "物品已经过期或已被购买!");
                return true;
            }
            
            // 打开确认购买界面
            plugin.getGuiManager().openConfirmBuyMenu(player, item);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "物品ID必须是一个数字!");
        }
        return true;
    }

    private boolean handleSellCommand(Player player, String[] args) {
        if (!player.hasPermission("globalshop.sell")) {
            player.sendMessage(ChatColor.RED + "你没有权限上架物品!");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "用法: /auction sell <起拍价> [一口价] [货币类型]");
            player.sendMessage(ChatColor.RED + "货币类型: 1=金币, 2=点券，默认为金币");
            return true;
        }
        
        // 检查玩家当前上架的物品数量是否已达到上限
        int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        
        if (currentListings >= maxListings) {
            player.sendMessage(ChatColor.RED + "你已达到最大上架数量限制 (" + maxListings + " 个物品)!");
            player.sendMessage(ChatColor.RED + "请等待已上架物品售出或过期后再尝试上架新物品。");
            return true;
        }
        
        try {
            double startPrice = Double.parseDouble(args[1]);
            double buyNowPrice = args.length > 2 ? Double.parseDouble(args[2]) : 0;
            
            // 检查参数数量，如果有一口价但没有货币类型，则提示必须指定货币类型
            if (args.length == 3 && buyNowPrice > 0) {
                player.sendMessage(ChatColor.RED + "必须在命令结尾指定货币类型!");
                player.sendMessage(ChatColor.RED + "用法: /auction sell <起拍价> <一口价> <货币类型>");
                player.sendMessage(ChatColor.RED + "货币类型: 1=金币, 2=点券");
                return true;
            }
            
            // 解析货币类型
            String currencyType = "VAULT"; // 默认为金币
            if (args.length > 3) {
                int currencyTypeId = Integer.parseInt(args[3]);
                if (currencyTypeId == 2) {
                    currencyType = "POINTS"; // 点券
                }
            }
            
            if (startPrice <= 0) {
                player.sendMessage(ChatColor.RED + "起拍价必须大于0!");
                return true;
            }
            if (buyNowPrice > 0 && buyNowPrice <= startPrice) {
                player.sendMessage(ChatColor.RED + "一口价必须大于起拍价!");
                return true;
            }
            // 保存命令信息
            String command = String.join(" ", args);
            player.setMetadata("auction_sell_command", new org.bukkit.metadata.FixedMetadataValue(plugin, command));
            // 保存货币类型信息
            player.setMetadata("auction_currency_type", new org.bukkit.metadata.FixedMetadataValue(plugin, currencyType));
            plugin.getGuiManager().openSellMenu(player);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "请输入有效的价格!");
        }
        return true;
    }

    private boolean handleSearchCommand(Player player, String[] args) {
        // 检查参数数量
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "用法: /auction search <关键词>");
            return true;
        }
        
        // 获取搜索关键词
        StringBuilder keywordBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) keywordBuilder.append(" ");
            keywordBuilder.append(args[i]);
        }
        String keyword = keywordBuilder.toString();
        
        // 检查关键词长度
        if (keyword.length() < 2) {
            player.sendMessage(ChatColor.RED + "搜索关键词至少需要2个字符!");
            return true;
        }
        
        // 打开搜索结果界面
        player.sendMessage(ChatColor.GREEN + "正在搜索: " + ChatColor.YELLOW + keyword);
        plugin.getGuiManager().openSearchResultMenu(player, keyword, 1);
        return true;
    }

    /**
     * 处理我的拍卖命令
     * @param player 玩家
     */
    private boolean handleMyCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.use")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用该命令!");
            return true;
        }
        
        // 打开我的拍卖界面
        plugin.getGuiManager().openMyAuctionsMenu(player, 1);
        return true;
    }

    private boolean handleCloseCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.admin")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用该命令!");
            return true;
        }
        
        // 创建并执行关闭所有拍卖的任务
        player.sendMessage(ChatColor.YELLOW + "正在强制关闭所有拍卖物品...");
        player.sendMessage(ChatColor.YELLOW + "此操作将在后台异步执行，以避免服务器卡顿，请耐心等待。");
        
        // 创建并运行任务（异步执行以避免卡服）
        cn.i7mc.globalshop.tasks.CloseAllAuctionsTask task = new cn.i7mc.globalshop.tasks.CloseAllAuctionsTask(plugin, player);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        return true;
    }

    /**
     * 处理检查过期物品命令
     * @param player 命令发送者
     * @return 命令执行结果
     */
    private boolean handleCheckExpiredCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.admin")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用该命令!");
            return true;
        }
        
        // 只有玩家可以执行此命令
        if (!(player instanceof Player)) {
            player.sendMessage("§c只有玩家可以执行此命令！");
            return true;
        }
        
        player.sendMessage("§a正在检查所有拍卖物品，查找过期但未处理的物品...");
        
        // 异步执行检查任务
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new cn.i7mc.globalshop.tasks.CheckAllAuctionsTask(plugin, player));
        
        return true;
    }

    private boolean handleReloadCommand(Player player) {
        if (player.hasPermission("globalshop.admin")) {
            plugin.getConfigManager().loadConfig();
            player.sendMessage(ChatColor.GREEN + "GlobalShop配置已重载!");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "你没有权限重载配置!");
            return true;
        }
    }
} 