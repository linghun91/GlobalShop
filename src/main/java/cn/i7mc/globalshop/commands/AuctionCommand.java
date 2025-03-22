package cn.i7mc.globalshop.commands;

import cn.i7mc.globalshop.GlobalShop;
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
        "help", "open", "sell", "search", "my", "collect", "reload", "close", "checkexpired"
    );
    private final List<String> currencyTypes = Arrays.asList("1", "2");

    public AuctionCommand(GlobalShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
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
                player.sendMessage(plugin.getMessageManager().getCommandUnknownCommandMessage());
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // 提供子命令补全
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            Collections.sort(completions);
            return completions;
        }
        
        // sell命令的参数补全
        if (args.length >= 2 && args[0].equalsIgnoreCase("sell")) {
            // 如果输入了上架价格，提示补全一口价
            if (args.length == 2) {
                // 这里不提供具体补全，但可以添加一个占位提示
                List<String> hints = new ArrayList<>();
                hints.add(plugin.getMessageManager().getTabCompletionSellPriceMessage());
                return hints;
            }
            
            // 如果输入了上架价格，提示补全一口价
            if (args.length == 3) {
                List<String> hints = new ArrayList<>();
                hints.add(plugin.getMessageManager().getTabCompletionSellBuyNowMessage());
                return hints;
            }
            
            // 如果输入了上架价格和一口价，补全货币类型
            if (args.length == 4) {
                List<String> availableCurrencyTypes = new ArrayList<>();
                availableCurrencyTypes.add("1"); // Vault经济始终可用
                
                // 只有在PlayerPoints可用时才添加点券选项
                if (plugin.isPlayerPointsAvailable()) {
                    availableCurrencyTypes.add("2"); // 点券
                }
                
                StringUtil.copyPartialMatches(args[3], availableCurrencyTypes, completions);
                if (completions.isEmpty()) {
                    // 如果没有匹配项，显示提示
                    completions.add(plugin.getMessageManager().getTabCompletionSellCurrencyMessage());
                }
                return completions;
            }
        }
        
        // search命令的补全
        if (args.length == 2 && args[0].equalsIgnoreCase("search")) {
            List<String> hints = new ArrayList<>();
            hints.add(plugin.getMessageManager().getTabCompletionSearchKeywordMessage());
            return hints;
        }
        
        return completions;
    }

    private boolean handleHelpCommand(Player player) {
        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.getMessageManager().getCommandHelpHeaderMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpHelpMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpOpenMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpSellMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpSearchMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpMyMessage());
        player.sendMessage(plugin.getMessageManager().getCommandHelpCollectMessage());
        
        // 只向管理员显示管理员命令
        if (player.hasPermission("globalshop.admin")) {
            player.sendMessage(plugin.getMessageManager().getCommandHelpAdminHeaderMessage());
            player.sendMessage(plugin.getMessageManager().getCommandHelpReloadMessage());
            player.sendMessage(plugin.getMessageManager().getCommandHelpCloseMessage());
            player.sendMessage(plugin.getMessageManager().getCommandHelpCheckExpiredMessage());
        }
    }

    private boolean handleCollectCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.use")) {
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
        
        // 打开物品邮箱界面
        plugin.getGuiManager().openMyMailboxMenu(player, 1);
        player.sendMessage(plugin.getMessageManager().getCommandCollectSuccessMessage());
        return true;
    }

    private boolean handleSellCommand(Player player, String[] args) {
        // 检查玩家是否有权限
        if (!player.hasPermission("globalshop.sell")) {
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
        
        // 命令格式检查
        if (args.length < 3) {
            player.sendMessage(plugin.getMessageManager().getCommandSellUsageMessage());
            player.sendMessage(plugin.getMessageManager().getCommandSellCurrencyTypesMessage());
            return true;
        }
        
        // 根据参数数量判断货币类型
        String currencyType = "VAULT"; // 默认为金币
        if (args.length >= 4) {
            if (args[3].equals("1")) {
                currencyType = "VAULT";
            } else if (args[3].equals("2")) {
                // 检查PlayerPoints是否可用
                if (!plugin.isPlayerPointsAvailable()) {
                    player.sendMessage(plugin.getMessageManager().getCommandSellPointsUnavailableMessage());
                    player.sendMessage(plugin.getMessageManager().getCommandSellPointsUnavailableSolutionMessage());
                    return true;
                }
                currencyType = "POINTS";
            } else {
                player.sendMessage(plugin.getMessageManager().getCommandSellInvalidCurrencyMessage());
                return true;
            }
        }
        
        // 检查玩家当前上架的物品数量是否已达到上限
        int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        
        if (currentListings >= maxListings) {
            player.sendMessage(plugin.getMessageManager().getCommandSellMaxListingsReachedMessage(maxListings));
            player.sendMessage(plugin.getMessageManager().getCommandSellWaitForItemsSoldMessage());
            return true;
        }
        
        try {
            double startPrice = Double.parseDouble(args[1]);
            double buyNowPrice = Double.parseDouble(args[2]);
            
            if (startPrice <= 0) {
                player.sendMessage(plugin.getMessageManager().getCommandSellStartPriceZeroMessage());
                return true;
            }
            
            if (buyNowPrice > 0 && buyNowPrice <= startPrice) {
                player.sendMessage(plugin.getMessageManager().getCommandSellBuyNowLessThanStartMessage());
                return true;
            }
            
            // 检查价格是否超过最大位数限制
            int maxPriceDigits = plugin.getConfigManager().getMaxPriceDigits();
            int startPriceDigits = countDigits(startPrice);
            int buyNowPriceDigits = countDigits(buyNowPrice);

            if (startPriceDigits > maxPriceDigits || buyNowPriceDigits > maxPriceDigits) {
                String message = plugin.getMessageManager().getPriceExceedsMaxLimitMessage()
                        .replace("%max_digits%", String.valueOf(maxPriceDigits));
                player.sendMessage(message);
                return true;
            }
            
            // 保存命令信息
            String command = String.join(" ", args);
            player.setMetadata("auction_sell_command", new org.bukkit.metadata.FixedMetadataValue(plugin, command));
            // 保存货币类型信息
            player.setMetadata("auction_currency_type", new org.bukkit.metadata.FixedMetadataValue(plugin, currencyType));
            plugin.getGuiManager().openSellMenu(player);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessageManager().getCommandSellInvalidPriceMessage());
        }
        return true;
    }

    /**
     * 计算数字的位数
     * @param number 需要计算位数的数字
     * @return 数字的位数
     */
    private int countDigits(double number) {
        // 使用更可靠的数学方法计算整数部分位数
        long integerPart = (long) Math.abs(number);
        
        // 对于0特殊处理
        if (integerPart == 0) {
            return 1;
        }
        
        // 使用对数计算位数 (log10(n) + 1)
        return (int) (Math.log10(integerPart) + 1);
    }

    private boolean handleSearchCommand(Player player, String[] args) {
        // 检查参数数量
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getCommandSearchUsageMessage());
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
            player.sendMessage(plugin.getMessageManager().getCommandSearchMinLengthMessage());
            return true;
        }
        
        // 打开搜索结果界面
        player.sendMessage(plugin.getMessageManager().getCommandSearchingMessage(keyword));
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
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
        
        // 打开我的拍卖界面
        plugin.getGuiManager().openMyAuctionsMenu(player, 1);
        return true;
    }

    private boolean handleCloseCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.admin")) {
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
        
        // 创建并执行关闭所有拍卖的任务
        player.sendMessage(plugin.getMessageManager().getCommandCloseStartingMessage());
        player.sendMessage(plugin.getMessageManager().getCommandCloseAsyncNoticeMessage());
        
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
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
        
        // 只有玩家可以执行此命令
        if (!(player instanceof Player)) {
            player.sendMessage(plugin.getMessageManager().getCommandCheckExpiredPlayerOnlyMessage());
            return true;
        }
        
        player.sendMessage(plugin.getMessageManager().getCommandCheckExpiredCheckingMessage());
        
        // 异步执行检查任务
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new cn.i7mc.globalshop.tasks.CheckAllAuctionsTask(plugin, player));
        
        return true;
    }

    private boolean handleReloadCommand(Player player) {
        if (player.hasPermission("globalshop.admin")) {
            // 先取消现有任务 (如果主类GlobalShop中有提供方法的话)
            plugin.cancelAuctionTasks();
            
            // 重载配置
            plugin.getConfigManager().loadConfig();
            plugin.getMessageManager().reloadMessages();
            plugin.getDebugMessageManager().reloadMessages();
            
            // 重新加载广播管理器配置
            plugin.getBroadcastManager().loadConfig();
            
            // 重新加载界面管理器配置
            plugin.getGuiManager().reloadConfig();
            
            // 使用新配置重新启动任务
            plugin.startAuctionTasks();
            
            // 输出调试信息，帮助确认配置已正确加载
            if (plugin.getConfigManager().isDebug()) {
                player.sendMessage(plugin.getMessageManager().getCommandReloadDebugInfoMessage(
                    plugin.getConfigManager().getCheckInterval()));
            }
            
            player.sendMessage(plugin.getMessageManager().getCommandReloadSuccessMessage());
            return true;
        } else {
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
    }
} 