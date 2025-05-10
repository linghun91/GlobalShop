package cn.i7mc.globalshop.economy;

import cn.i7mc.globalshop.GlobalShop;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;

public class EconomyManager {
    private final GlobalShop plugin;
    private final Economy vaultEconomy;
    private final PlayerPoints playerPoints;

    public EconomyManager(GlobalShop plugin, Economy vaultEconomy, PlayerPoints playerPoints) {
        this.plugin = plugin;
        this.vaultEconomy = vaultEconomy;
        this.playerPoints = playerPoints;
    }

    /**
     * 检查点券系统是否可用
     * @return 点券系统是否可用
     */
    public boolean isPlayerPointsAvailable() {
        return playerPoints != null;
    }

    // 检查玩家是否有足够的钱
    public boolean hasEnough(Player player, double amount, String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> vaultEconomy.has(player, amount);
            case "POINTS" -> isPlayerPointsAvailable() && playerPoints.getAPI().look(player.getUniqueId()) >= amount;
            default -> false;
        };
    }

    // 扣除玩家的钱
    public boolean takeMoney(Player player, double amount, String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> vaultEconomy.withdrawPlayer(player, amount).transactionSuccess();
            case "POINTS" -> isPlayerPointsAvailable() && playerPoints.getAPI().take(player.getUniqueId(), (int) amount);
            default -> false;
        };
    }

    // 给玩家钱
    public boolean giveMoney(Player player, double amount, String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> vaultEconomy.depositPlayer(player, amount).transactionSuccess();
            case "POINTS" -> isPlayerPointsAvailable() && playerPoints.getAPI().give(player.getUniqueId(), (int) amount);
            default -> false;
        };
    }

    // 给离线玩家钱
    public boolean giveMoney(org.bukkit.OfflinePlayer player, double amount, String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> vaultEconomy.depositPlayer(player, amount).transactionSuccess();
            case "POINTS" -> isPlayerPointsAvailable() && playerPoints.getAPI().give(player.getUniqueId(), (int) amount);
            default -> false;
        };
    }

    // 获取玩家余额
    public double getBalance(Player player, String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> vaultEconomy.getBalance(player);
            case "POINTS" -> isPlayerPointsAvailable() ? playerPoints.getAPI().look(player.getUniqueId()) : 0;
            default -> 0.0;
        };
    }

    // 获取货币符号
    public String getCurrencySymbol(String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultSymbol();
            case "POINTS" -> plugin.getConfigManager().getPointsSymbol();
            default -> "";
        };
    }

    // 获取货币名称
    public String getCurrencyName(String currencyType) {
        return switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultName();
            case "POINTS" -> plugin.getConfigManager().getPointsName();
            default -> "";
        };
    }

    // 计算手续费
    public double calculateFee(double amount, String currencyType) {
        double feeRate = switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultFee();
            case "POINTS" -> plugin.getConfigManager().getPointsFee();
            default -> 0.0;
        };
        double fee = amount * feeRate;
        double minFee = switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultMinFee();
            case "POINTS" -> plugin.getConfigManager().getPointsMinFee();
            default -> 0.0;
        };
        return Math.max(fee, minFee);
    }

    // 格式化金额显示
    public String formatAmount(double amount, String currencyType) {
        String symbol = getCurrencySymbol(currencyType);
        if (currencyType.equalsIgnoreCase("POINTS")) {
            return String.format("%s%d", symbol, (int) amount);
        } else {
            return String.format("%s%.2f", symbol, amount);
        }
    }

    // 新增：格式化Vault金额
    public String formatVault(double amount) {
        String symbol = plugin.getConfigManager().getVaultSymbol();
        return String.format("%s%.2f", symbol, amount);
    }

    // 新增：格式化PlayerPoints金额
    public String formatPoints(double amount) {
        String symbol = plugin.getConfigManager().getPointsSymbol();
        return String.format("%s%d", symbol, (int) amount);
    }

    // 计算最低加价
    public double calculateMinBid(double currentPrice, String currencyType) {
        double minBidRate = switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultMinBidRate();
            case "POINTS" -> plugin.getConfigManager().getPointsMinBidRate();
            default -> 0.05; // 默认5%
        };
        double minBid = currentPrice * minBidRate;
        double minBidAmount = switch (currencyType.toUpperCase()) {
            case "VAULT" -> plugin.getConfigManager().getVaultMinBidAmount();
            case "POINTS" -> plugin.getConfigManager().getPointsMinBidAmount();
            default -> 1.0;
        };
        return Math.max(minBid, minBidAmount);
    }
} 