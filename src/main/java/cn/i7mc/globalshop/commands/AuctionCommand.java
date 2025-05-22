package cn.i7mc.globalshop.commands;

import cn.i7mc.globalshop.GlobalShop;
import org.bukkit.Bukkit;
<<<<<<< HEAD
=======
import org.bukkit.ChatColor;
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
<<<<<<< HEAD
import org.bukkit.OfflinePlayer;
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuctionCommand implements CommandExecutor, TabCompleter {
    private final GlobalShop plugin;
    private final List<String> subCommands = Arrays.asList(
<<<<<<< HEAD
        "help", "open", "sell", "search", "my", "collect", "reload", "close", "checkexpired", "hud", "info"
=======
        "help", "open", "sell", "search", "my", "collect", "reload", "close", "checkexpired", "hud"
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
    );
    private final List<String> hudSubCommands = Arrays.asList(
        "create", "remove", "list", "reload"
    );
    private final List<String> currencyTypes = Arrays.asList("1", "2");

    public AuctionCommand(GlobalShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                // 默认打开主菜单
                plugin.getGuiManager().openMainMenu((Player) sender);
            } else {
                sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                return handleHelpCommand(sender);
            case "open":
                if (sender instanceof Player) {
                    plugin.getGuiManager().openMainMenu((Player) sender);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
            case "sell":
                if (sender instanceof Player) {
                    return handleSellCommand((Player) sender, args);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
            case "search":
                if (sender instanceof Player) {
                    return handleSearchCommand((Player) sender, args);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
            case "my":
                if (sender instanceof Player) {
                    return handleMyCommand((Player) sender);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
            case "collect":
                if (sender instanceof Player) {
                    return handleCollectCommand((Player) sender);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
            case "reload":
                return handleReloadCommand(sender);
            case "close":
                return handleCloseCommand(sender);
            case "checkexpired":
                return handleCheckExpiredCommand(sender);
            case "hud":
                return handleHudCommand(sender, args);
<<<<<<< HEAD
            case "info":
                if (sender instanceof Player) {
                    return handleInfoCommand((Player) sender, args);
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandPlayerOnlyMessage());
                }
                return true;
=======
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            default:
                sender.sendMessage(plugin.getMessageManager().getCommandUnknownCommandMessage());
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 提供子命令补全
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            Collections.sort(completions);
            return completions;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // hud命令的参数补全
        if (args.length >= 2 && args[0].equalsIgnoreCase("hud")) {
            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], hudSubCommands, completions);
                Collections.sort(completions);
                return completions;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // hud remove命令的参数补全
            if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
                List<String> hudNames = new ArrayList<>(plugin.getHologramCommandManager().getHologramLocations().keySet());
                StringUtil.copyPartialMatches(args[2], hudNames, completions);
                Collections.sort(completions);
                return completions;
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // sell命令的参数补全
        if (args.length >= 2 && args[0].equalsIgnoreCase("sell")) {
            // 如果输入了上架价格，提示补全一口价
            if (args.length == 2) {
                // 这里不提供具体补全，但可以添加一个占位提示
                List<String> hints = new ArrayList<>();
                hints.add(plugin.getMessageManager().getTabCompletionSellPriceMessage());
                return hints;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 如果输入了上架价格，提示补全一口价
            if (args.length == 3) {
                List<String> hints = new ArrayList<>();
                hints.add(plugin.getMessageManager().getTabCompletionSellBuyNowMessage());
                return hints;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 如果输入了上架价格和一口价，补全货币类型
            if (args.length == 4) {
                List<String> availableCurrencyTypes = new ArrayList<>();
                availableCurrencyTypes.add("1"); // Vault经济始终可用
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 只有在PlayerPoints可用时才添加点券选项
                if (plugin.isPlayerPointsAvailable()) {
                    availableCurrencyTypes.add("2"); // 点券
                }
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                StringUtil.copyPartialMatches(args[3], availableCurrencyTypes, completions);
                if (completions.isEmpty()) {
                    // 如果没有匹配项，显示提示
                    completions.add(plugin.getMessageManager().getTabCompletionSellCurrencyMessage());
                }
                return completions;
            }
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // search命令的补全
        if (args.length == 2 && args[0].equalsIgnoreCase("search")) {
            List<String> hints = new ArrayList<>();
            hints.add(plugin.getMessageManager().getTabCompletionSearchKeywordMessage());
            return hints;
        }
<<<<<<< HEAD

        // info命令的补全 (补全在线玩家名)
        if (args.length == 2 && args[0].equalsIgnoreCase("info") && sender.hasPermission("globalshop.admin.info")) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            StringUtil.copyPartialMatches(args[1], playerNames, completions);
            Collections.sort(completions);
            return completions;
        }

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        return completions;
    }

    private boolean handleHelpCommand(CommandSender sender) {
        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessageManager().getCommandHelpHeaderMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpHelpMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpOpenMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpSellMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpSearchMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpMyMessage());
        sender.sendMessage(plugin.getMessageManager().getCommandHelpCollectMessage());
<<<<<<< HEAD

        // 显示Web服务信息
        String webInfo = plugin.getMessageManager().getMessages().getString("auction_commands.help.web", "§e拍卖行Web服务: §f访问 §bhttp://服务器IP:%port% §f查看拍卖物品");
        webInfo = webInfo.replace("%port%", String.valueOf(plugin.getWebServer().getWebConfig().getPort()));
        sender.sendMessage(webInfo);

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 只向管理员显示管理员命令
        if (sender.hasPermission("globalshop.admin")) {
            sender.sendMessage(plugin.getMessageManager().getCommandHelpAdminHeaderMessage());
            sender.sendMessage(plugin.getMessageManager().getCommandHelpReloadMessage());
            sender.sendMessage(plugin.getMessageManager().getCommandHelpCloseMessage());
            sender.sendMessage(plugin.getMessageManager().getCommandHelpCheckExpiredMessage());
<<<<<<< HEAD

            // 直接显示info命令帮助
            sender.sendMessage("§e/ah info <玩家名> §7- 查询玩家的拍卖行信息");

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 显示全息相关命令
            sender.sendMessage("§6===== 全息拍卖行命令 =====");
            sender.sendMessage("§e/ah hud create <名称> §7- 在当前位置创建全息拍卖行");
            sender.sendMessage("§e/ah hud remove <名称> §7- 移除指定名称的全息拍卖行");
            sender.sendMessage("§e/ah hud list §7- 列出所有全息拍卖行");
            sender.sendMessage("§e/ah hud reload §7- 重新加载全息拍卖行配置");
        }
    }

    private boolean handleCollectCommand(Player player) {
        // 检查权限
        if (!player.hasPermission("globalshop.use")) {
            player.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 命令格式检查
        if (args.length < 3) {
            player.sendMessage(plugin.getMessageManager().getCommandSellUsageMessage());
            player.sendMessage(plugin.getMessageManager().getCommandSellCurrencyTypesMessage());
            return true;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

        // 检查玩家当前上架的物品数量是否已达到上限
        int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());

=======
        
        // 检查玩家当前上架的物品数量是否已达到上限
        int maxListings = plugin.getConfigManager().getMaxListingsPerPlayer();
        int currentListings = plugin.getDatabaseManager().countPlayerActiveAuctions(player.getUniqueId());
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        if (currentListings >= maxListings) {
            player.sendMessage(plugin.getMessageManager().getCommandSellMaxListingsReachedMessage(maxListings));
            player.sendMessage(plugin.getMessageManager().getCommandSellWaitForItemsSoldMessage());
            return true;
        }
<<<<<<< HEAD

        try {
            double startPrice = Double.parseDouble(args[1]);
            double buyNowPrice = Double.parseDouble(args[2]);

=======
        
        try {
            double startPrice = Double.parseDouble(args[1]);
            double buyNowPrice = Double.parseDouble(args[2]);
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            if (startPrice <= 0) {
                player.sendMessage(plugin.getMessageManager().getCommandSellStartPriceZeroMessage());
                return true;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            if (buyNowPrice > 0 && buyNowPrice <= startPrice) {
                player.sendMessage(plugin.getMessageManager().getCommandSellBuyNowLessThanStartMessage());
                return true;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 对于0特殊处理
        if (integerPart == 0) {
            return 1;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 使用对数计算位数 (log10(n) + 1)
        return (int) (Math.log10(integerPart) + 1);
    }

    private boolean handleSearchCommand(Player player, String[] args) {
        // 检查参数数量
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getCommandSearchUsageMessage());
            return true;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 获取搜索关键词
        StringBuilder keywordBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) keywordBuilder.append(" ");
            keywordBuilder.append(args[i]);
        }
        String keyword = keywordBuilder.toString();
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 检查关键词长度
        if (keyword.length() < 2) {
            player.sendMessage(plugin.getMessageManager().getCommandSearchMinLengthMessage());
            return true;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
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
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 打开我的拍卖界面
        plugin.getGuiManager().openMyAuctionsMenu(player, 1);
        return true;
    }

    /**
     * 处理close命令
     * @param sender 命令发送者
     * @return 命令执行结果
     */
    private boolean handleCloseCommand(CommandSender sender) {
        if (sender.hasPermission("globalshop.admin")) {
            // 强制所有在线玩家关闭拍卖行界面
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (plugin.getGuiManager().isViewingGui(onlinePlayer)) {
                    plugin.getGuiManager().closeGui(onlinePlayer);
                    onlinePlayer.sendMessage(plugin.getMessageManager().getCommandCloseForceCloseMessage());
                }
            }
            sender.sendMessage(plugin.getMessageManager().getCommandCloseSuccessMessage());
            return true;
        } else {
            sender.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
    }

    /**
     * 处理checkexpired命令
     * @param sender 命令发送者
     * @return 命令执行结果
     */
    private boolean handleCheckExpiredCommand(CommandSender sender) {
        if (sender.hasPermission("globalshop.admin")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(plugin.getMessageManager().getCommandCheckExpiredCheckingMessage());
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 异步执行检查任务
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new cn.i7mc.globalshop.tasks.CheckAllAuctionsTask(plugin, player));
            } else {
                int expiredItems = plugin.getDatabaseManager().processExpiredAuctions();
<<<<<<< HEAD

=======
                
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
                // 发送过期物品处理结果消息
                if (expiredItems > 0) {
                    sender.sendMessage(plugin.getMessageManager().getCommandCheckExpiredSuccessMessage(expiredItems));
                } else {
                    sender.sendMessage(plugin.getMessageManager().getCommandCheckExpiredNoneMessage());
                }
            }
            return true;
        } else {
            sender.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (sender.hasPermission("globalshop.admin")) {
            // 先取消现有任务 (如果主类GlobalShop中有提供方法的话)
            plugin.cancelAuctionTasks();
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 重载配置
            plugin.getConfigManager().loadConfig();
            plugin.getMessageManager().reloadMessages();
            plugin.getDebugMessageManager().reloadMessages();
<<<<<<< HEAD

            // 重新加载广播管理器配置
            plugin.getBroadcastManager().loadConfig();

            // 重新加载界面管理器配置
            plugin.getGuiManager().reloadConfig();

=======
            
            // 重新加载广播管理器配置
            plugin.getBroadcastManager().loadConfig();
            
            // 重新加载界面管理器配置
            plugin.getGuiManager().reloadConfig();
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 重新加载全息显示配置
            if (plugin.getHologramConfigManager() != null) {
                plugin.getHologramConfigManager().reloadConfig();
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 重新加载全息显示位置
            if (plugin.getHologramCommandManager() != null) {
                // 调用HologramCommandManager的reload方法
                plugin.getHologramCommandManager().handleReloadCommand(sender);
            }
<<<<<<< HEAD

            // 重启Web服务
            if (plugin.getWebServer() != null) {
                plugin.getWebServer().restart();
            }

            // 使用新配置重新启动任务
            plugin.startAuctionTasks();

=======
            
            // 使用新配置重新启动任务
            plugin.startAuctionTasks();
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            // 输出调试信息，帮助确认配置已正确加载
            if (plugin.getConfigManager().isDebug()) {
                sender.sendMessage(plugin.getMessageManager().getCommandReloadDebugInfoMessage(
                    plugin.getConfigManager().getCheckInterval()));
            }
<<<<<<< HEAD

=======
            
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
            sender.sendMessage(plugin.getMessageManager().getCommandReloadSuccessMessage());
            return true;
        } else {
            sender.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }
    }

    /**
     * 处理全息拍卖行命令
     * @param sender 命令发送者
     * @param args 命令参数
     * @return 命令执行结果
     */
    private boolean handleHudCommand(CommandSender sender, String[] args) {
        // 添加调试信息
        if (plugin.getConfigManager().isDebug()) {
        }
<<<<<<< HEAD

=======
        
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
        // 检查HologramCommandManager是否可用
        if (plugin.getHologramCommandManager() == null) {
            sender.sendMessage("§c全息拍卖行功能未启用");
            return true;
        }
<<<<<<< HEAD

        // 调用HologramCommandManager处理命令
        return plugin.getHologramCommandManager().onCommand(sender, args);
    }

    // 处理 /gs info <玩家名> 的方法
    private boolean handleInfoCommand(Player admin, String[] args) {
        // 权限检查
        if (!admin.hasPermission("globalshop.admin.info")) {
            admin.sendMessage(plugin.getMessageManager().getCommandNoPermissionMessage());
            return true;
        }

        // 参数检查
        if (args.length != 2) {
            admin.sendMessage(plugin.getMessageManager().getCommandInfoUsageMessage());
            return true;
        }

        String targetPlayerName = args[1];

        // 首先尝试从在线玩家中查找（精确匹配）
        Player onlinePlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (onlinePlayer != null) {
            // 玩家在线，直接使用
            plugin.getGuiManager().openPlayerInfoMenu(admin, onlinePlayer);
            return true;
        }

        // 如果玩家不在线，尝试从数据库中查找
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        // 检查玩家是否存在于数据库中
        if (!targetPlayer.hasPlayedBefore()) {
            admin.sendMessage(plugin.getMessageManager().getPlayerNotFoundMessage(targetPlayerName));
            return true;
        }

        // 调用 GuiManager 打开玩家信息界面
        plugin.getGuiManager().openPlayerInfoMenu(admin, targetPlayer);
        return true;
    }
}
=======
        
        // 调用HologramCommandManager处理命令
        return plugin.getHologramCommandManager().onCommand(sender, args);
    }
} 
>>>>>>> 15f107e82b75f924ce81fb7e47d6dc0a10e3c8ba
