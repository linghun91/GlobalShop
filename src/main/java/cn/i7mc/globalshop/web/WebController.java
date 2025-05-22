package cn.i7mc.globalshop.web;

import cn.i7mc.globalshop.GlobalShop;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Web控制器
 * 处理HTTP请求和响应
 */
public class WebController implements HttpHandler {
    private final GlobalShop plugin;
    private final WebConfig webConfig;
    private final WebDataProvider dataProvider;
    private final SimpleDateFormat dateFormat;
    private final Map<String, String> mimeTypes;

    /**
     * 构造函数
     * @param plugin 插件实例
     * @param webConfig Web配置
     * @param dataProvider 数据提供者
     */
    public WebController(GlobalShop plugin, WebConfig webConfig, WebDataProvider dataProvider) {
        this.plugin = plugin;
        this.webConfig = webConfig;
        this.dataProvider = dataProvider;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.mimeTypes = initMimeTypes();
    }

    /**
     * 初始化MIME类型映射
     * @return MIME类型映射
     */
    private Map<String, String> initMimeTypes() {
        Map<String, String> types = new HashMap<>();
        types.put("html", "text/html");
        types.put("css", "text/css");
        types.put("js", "application/javascript");
        types.put("json", "application/json");
        types.put("png", "image/png");
        types.put("jpg", "image/jpeg");
        types.put("jpeg", "image/jpeg");
        types.put("gif", "image/gif");
        types.put("ico", "image/x-icon");
        types.put("svg", "image/svg+xml");
        return types;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        
        // 设置跨域头（如果启用）
        if (webConfig.isAllowCors()) {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Methods", "GET, OPTIONS");
            headers.set("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
        }
        
        // 处理API请求
        if (path.startsWith("/api/")) {
            handleApiRequest(exchange, path);
            return;
        }
        
        // 处理静态资源请求
        if (path.equals("/") || path.equals("/index.html")) {
            serveIndexPage(exchange);
        } else {
            serveStaticResource(exchange, path);
        }
    }

    /**
     * 处理API请求
     * @param exchange HTTP交换
     * @param path 请求路径
     * @throws IOException IO异常
     */
    private void handleApiRequest(HttpExchange exchange, String path) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        
        String response;
        
        if (path.equals("/api/items")) {
            response = dataProvider.getActiveAuctionItemsJson();
        } else if (path.equals("/api/status")) {
            response = getStatusJson();
        } else {
            response = "{\"error\":\"Unknown API endpoint\"}";
            exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }
        
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 获取服务器状态JSON
     * @return 状态JSON字符串
     */
    private String getStatusJson() {
        Map<String, Object> status = new HashMap<>();
        status.put("serverTime", dateFormat.format(new Date()));
        status.put("lastUpdate", dateFormat.format(new Date(dataProvider.getLastUpdateTime())));
        status.put("nextUpdate", dateFormat.format(new Date(dataProvider.getNextUpdateTime())));
        status.put("refreshInterval", webConfig.getRefreshInterval());
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"serverTime\":\"").append(status.get("serverTime")).append("\",");
        json.append("\"lastUpdate\":\"").append(status.get("lastUpdate")).append("\",");
        json.append("\"nextUpdate\":\"").append(status.get("nextUpdate")).append("\",");
        json.append("\"refreshInterval\":").append(status.get("refreshInterval"));
        json.append("}");
        
        return json.toString();
    }

    /**
     * 提供首页
     * @param exchange HTTP交换
     * @throws IOException IO异常
     */
    private void serveIndexPage(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/html; charset=UTF-8");
        
        String html = getIndexHtml();
        
        exchange.sendResponseHeaders(200, html.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 获取首页HTML
     * @return 首页HTML
     */
    private String getIndexHtml() {
        // 从插件资源目录加载HTML模板
        try {
            File webDir = new File(plugin.getDataFolder(), "web");
            if (!webDir.exists()) {
                webDir.mkdirs();
            }
            
            File indexFile = new File(webDir, "index.html");
            if (indexFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    return content.toString();
                }
            } else {
                // 如果文件不存在，返回默认HTML
                return getDefaultHtml();
            }
        } catch (IOException e) {
            return getDefaultHtml();
        }
    }

    /**
     * 获取默认HTML
     * @return 默认HTML
     */
    private String getDefaultHtml() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"zh-CN\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>" + plugin.getMessageManager().getMessages().getString("web.page.title", "全球拍卖行 - 物品展示") + "</title>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: Arial, sans-serif;\n" +
               "            margin: 0;\n" +
               "            padding: 20px;\n" +
               "            background-color: #f5f5f5;\n" +
               "        }\n" +
               "        .container {\n" +
               "            max-width: 1200px;\n" +
               "            margin: 0 auto;\n" +
               "            background-color: white;\n" +
               "            padding: 20px;\n" +
               "            border-radius: 5px;\n" +
               "            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n" +
               "        }\n" +
               "        h1 {\n" +
               "            color: #333;\n" +
               "            text-align: center;\n" +
               "        }\n" +
               "        .items-grid {\n" +
               "            display: grid;\n" +
               "            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));\n" +
               "            gap: 20px;\n" +
               "            margin-top: 20px;\n" +
               "        }\n" +
               "        .item-card {\n" +
               "            border: 1px solid #ddd;\n" +
               "            border-radius: 5px;\n" +
               "            padding: 15px;\n" +
               "            background-color: #fff;\n" +
               "            box-shadow: 0 1px 3px rgba(0,0,0,0.1);\n" +
               "        }\n" +
               "        .item-name {\n" +
               "            font-weight: bold;\n" +
               "            font-size: 18px;\n" +
               "            margin-bottom: 10px;\n" +
               "            color: #333;\n" +
               "        }\n" +
               "        .item-info {\n" +
               "            margin-bottom: 5px;\n" +
               "            color: #666;\n" +
               "        }\n" +
               "        .item-price {\n" +
               "            font-weight: bold;\n" +
               "            color: #e74c3c;\n" +
               "        }\n" +
               "        .refresh-info {\n" +
               "            text-align: center;\n" +
               "            margin-top: 20px;\n" +
               "            color: #777;\n" +
               "            font-size: 14px;\n" +
               "        }\n" +
               "        .footer {\n" +
               "            text-align: center;\n" +
               "            margin-top: 30px;\n" +
               "            padding-top: 10px;\n" +
               "            border-top: 1px solid #eee;\n" +
               "            color: #999;\n" +
               "            font-size: 12px;\n" +
               "        }\n" +
               "        .no-items {\n" +
               "            text-align: center;\n" +
               "            padding: 50px 0;\n" +
               "            color: #999;\n" +
               "            font-style: italic;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"container\">\n" +
               "        <h1>" + plugin.getMessageManager().getMessages().getString("web.page.header", "拍卖行物品展示") + "</h1>\n" +
               "        <div id=\"items-container\" class=\"items-grid\">\n" +
               "            <div class=\"no-items\">" + plugin.getMessageManager().getMessages().getString("web.page.no_items", "当前没有物品在拍卖中") + "</div>\n" +
               "        </div>\n" +
               "        <div class=\"refresh-info\">\n" +
               "            <span id=\"last-update\"></span><br>\n" +
               "            <span id=\"next-update\"></span>\n" +
               "        </div>\n" +
               "        <div class=\"footer\">\n" +
               "            " + plugin.getMessageManager().getMessages().getString("web.page.footer", "由GlobalShop插件提供") + "\n" +
               "        </div>\n" +
               "    </div>\n" +
               "\n" +
               "    <script>\n" +
               "        // 页面加载时获取数据\n" +
               "        document.addEventListener('DOMContentLoaded', function() {\n" +
               "            fetchItems();\n" +
               "            fetchStatus();\n" +
               "            \n" +
               "            // 定时刷新数据\n" +
               "            setInterval(function() {\n" +
               "                fetchItems();\n" +
               "                fetchStatus();\n" +
               "            }, " + (webConfig.getRefreshInterval() * 1000) + ");\n" +
               "        });\n" +
               "        \n" +
               "        // 获取拍卖物品数据\n" +
               "        function fetchItems() {\n" +
               "            fetch('/api/items')\n" +
               "                .then(response => response.json())\n" +
               "                .then(data => {\n" +
               "                    renderItems(data);\n" +
               "                })\n" +
               "                .catch(error => {\n" +
               "                    console.error('获取物品数据失败:', error);\n" +
               "                });\n" +
               "        }\n" +
               "        \n" +
               "        // 获取服务器状态\n" +
               "        function fetchStatus() {\n" +
               "            fetch('/api/status')\n" +
               "                .then(response => response.json())\n" +
               "                .then(data => {\n" +
               "                    document.getElementById('last-update').textContent = '" + 
                   plugin.getMessageManager().getMessages().getString("web.page.refresh_time", "数据刷新时间: %time%").replace("%time%", "' + data.lastUpdate + '") + ";\n" +
               "                    document.getElementById('next-update').textContent = '" + 
                   plugin.getMessageManager().getMessages().getString("web.page.next_refresh", "下次刷新: %time%").replace("%time%", "' + data.nextUpdate + '") + ";\n" +
               "                })\n" +
               "                .catch(error => {\n" +
               "                    console.error('获取状态数据失败:', error);\n" +
               "                });\n" +
               "        }\n" +
               "        \n" +
               "        // 渲染物品列表\n" +
               "        function renderItems(items) {\n" +
               "            const container = document.getElementById('items-container');\n" +
               "            \n" +
               "            if (!items || items.length === 0) {\n" +
               "                container.innerHTML = '<div class=\"no-items\">" + plugin.getMessageManager().getMessages().getString("web.page.no_items", "当前没有物品在拍卖中") + "</div>';\n" +
               "                return;\n" +
               "            }\n" +
               "            \n" +
               "            container.innerHTML = '';\n" +
               "            \n" +
               "            items.forEach(item => {\n" +
               "                const card = document.createElement('div');\n" +
               "                card.className = 'item-card';\n" +
               "                \n" +
               "                let buyNowText = '';\n" +
               "                if (item.buyNowPrice > 0) {\n" +
               "                    buyNowText = `<div class=\"item-info item-price\">一口价: ${item.currencySymbol}${item.buyNowPrice}</div>`;\n" +
               "                }\n" +
               "                \n" +
               "                card.innerHTML = `\n" +
               "                    <div class=\"item-name\">${item.itemName} x${item.itemAmount}</div>\n" +
               "                    <div class=\"item-info\">卖家: ${item.sellerName}</div>\n" +
               "                    <div class=\"item-info\">当前价格: ${item.currencySymbol}${item.currentPrice}</div>\n" +
               "                    ${buyNowText}\n" +
               "                    <div class=\"item-info\">剩余时间: ${item.remainingTimeFormatted}</div>\n" +
               "                    ${item.hasBidder ? `<div class=\"item-info\">当前出价者: ${item.currentBidder}</div>` : ''}\n" +
               "                `;\n" +
               "                \n" +
               "                container.appendChild(card);\n" +
               "            });\n" +
               "        }\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }

    /**
     * 提供静态资源
     * @param exchange HTTP交换
     * @param path 请求路径
     * @throws IOException IO异常
     */
    private void serveStaticResource(HttpExchange exchange, String path) throws IOException {
        // 解码URL路径
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        
        // 移除开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = path.substring(lastDotIndex + 1).toLowerCase();
        }
        
        // 设置Content-Type
        String contentType = mimeTypes.getOrDefault(extension, "application/octet-stream");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        
        // 尝试从插件资源目录加载文件
        File webDir = new File(plugin.getDataFolder(), "web");
        File file = new File(webDir, path);
        
        // 检查文件是否存在且在web目录内
        if (file.exists() && file.isFile() && file.getCanonicalPath().startsWith(webDir.getCanonicalPath())) {
            try (FileInputStream fis = new FileInputStream(file)) {
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }
        } else {
            // 文件不存在，返回404
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
