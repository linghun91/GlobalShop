package cn.i7mc.globalshop.web;

import cn.i7mc.globalshop.GlobalShop;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;

/**
 * Web服务器
 * 负责启动和管理内嵌的HTTP服务器
 */
public class WebServer {
    private final GlobalShop plugin;
    private final WebConfig webConfig;
    private final WebDataProvider dataProvider;
    private HttpServer server;
    private boolean isRunning;

    /**
     * 构造函数
     * @param plugin 插件实例
     */
    public WebServer(GlobalShop plugin) {
        this.plugin = plugin;
        this.webConfig = new WebConfig(plugin);
        this.dataProvider = new WebDataProvider(plugin, webConfig);
        this.isRunning = false;

        // 确保web目录存在
        ensureWebDirectoryExists();
    }

    /**
     * 确保web目录存在，并复制默认资源
     */
    private void ensureWebDirectoryExists() {
        File webDir = new File(plugin.getDataFolder(), "web");
        if (!webDir.exists()) {
            webDir.mkdirs();
        }

        // 复制默认资源
        copyDefaultResource("web/index.html", new File(webDir, "index.html"));
        copyDefaultResource("web/style.css", new File(webDir, "style.css"));
        copyDefaultResource("web/script.js", new File(webDir, "script.js"));
    }

    /**
     * 复制默认资源
     * @param resourcePath 资源路径
     * @param targetFile 目标文件
     */
    private void copyDefaultResource(String resourcePath, File targetFile) {
        if (!targetFile.exists()) {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in != null) {
                    Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if (plugin.getConfigManager().isDebug()) {
                        plugin.getLogger().info("已复制默认资源: " + resourcePath + " 到 " + targetFile.getPath());
                    }
                } else {
                    plugin.getLogger().warning("找不到资源: " + resourcePath);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("复制资源时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 启动Web服务器
     * @return 是否成功启动
     */
    public boolean start() {
        if (isRunning) {
            plugin.getLogger().info(plugin.getMessageManager().getMessages().getString("web.server.already_running", "拍卖行Web服务已在运行中，端口: %port%")
                    .replace("%port%", String.valueOf(webConfig.getPort())));
            return true;
        }

        if (!webConfig.isEnabled()) {
            return false;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(webConfig.getPort()), 0);
            server.createContext("/", new WebController(plugin, webConfig, dataProvider));
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            isRunning = true;

            plugin.getLogger().info(plugin.getMessageManager().getMessages().getString("web.server.started", "拍卖行Web服务已启动，端口: %port%")
                    .replace("%port%", String.valueOf(webConfig.getPort())));
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe(plugin.getMessageManager().getMessages().getString("web.server.error", "拍卖行Web服务启动失败: %error%")
                    .replace("%error%", e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 停止Web服务器
     */
    public void stop() {
        if (server != null && isRunning) {
            server.stop(0);
            isRunning = false;
            plugin.getLogger().info(plugin.getMessageManager().getMessages().getString("web.server.stopped", "拍卖行Web服务已停止"));
        }
    }

    /**
     * 重启Web服务器
     * @return 是否成功重启
     */
    public boolean restart() {
        stop();
        webConfig.reloadConfig();
        return start();
    }

    /**
     * 检查Web服务器是否正在运行
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 获取Web配置
     * @return Web配置
     */
    public WebConfig getWebConfig() {
        return webConfig;
    }

    /**
     * 获取数据提供者
     * @return 数据提供者
     */
    public WebDataProvider getDataProvider() {
        return dataProvider;
    }
}
