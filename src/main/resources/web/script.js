// 全局变量
let refreshTimer = null;
let defaultRefreshInterval = 30000; // 默认30秒刷新一次

// 页面加载时获取数据
document.addEventListener('DOMContentLoaded', function() {
    fetchItems();
    fetchStatus();
    
    // 初始化定时刷新
    refreshTimer = setInterval(function() {
        fetchItems();
        fetchStatus();
    }, defaultRefreshInterval);
});

// 获取拍卖物品数据
function fetchItems() {
    fetch('/api/items')
        .then(response => {
            if (!response.ok) {
                throw new Error('网络响应不正常');
            }
            return response.json();
        })
        .then(data => {
            renderItems(data);
        })
        .catch(error => {
            console.error('获取物品数据失败:', error);
            document.getElementById('items-container').innerHTML = 
                '<div class="no-items">获取数据失败，请稍后再试</div>';
        });
}

// 获取服务器状态
function fetchStatus() {
    fetch('/api/status')
        .then(response => {
            if (!response.ok) {
                throw new Error('网络响应不正常');
            }
            return response.json();
        })
        .then(data => {
            updateStatusInfo(data);
            updateRefreshInterval(data);
        })
        .catch(error => {
            console.error('获取状态数据失败:', error);
        });
}

// 更新状态信息
function updateStatusInfo(data) {
    document.getElementById('last-update').textContent = '数据刷新时间: ' + data.lastUpdate;
    document.getElementById('next-update').textContent = '下次刷新: ' + data.nextUpdate;
}

// 更新刷新间隔
function updateRefreshInterval(data) {
    if (data.refreshInterval) {
        const intervalMs = data.refreshInterval * 1000;
        
        // 只有当间隔发生变化时才重新设置定时器
        if (intervalMs !== defaultRefreshInterval) {
            defaultRefreshInterval = intervalMs;
            
            // 清除现有的定时器
            if (refreshTimer) {
                clearInterval(refreshTimer);
            }
            
            // 设置新的定时器
            refreshTimer = setInterval(function() {
                fetchItems();
                fetchStatus();
            }, intervalMs);
            
            console.log('刷新间隔已更新为: ' + data.refreshInterval + '秒');
        }
    }
}

// 渲染物品列表
function renderItems(items) {
    const container = document.getElementById('items-container');
    
    if (!items || items.length === 0) {
        container.innerHTML = '<div class="no-items">当前没有物品在拍卖中</div>';
        return;
    }
    
    container.innerHTML = '<div class="items-grid"></div>';
    const grid = container.querySelector('.items-grid');
    
    // 按剩余时间排序
    items.sort((a, b) => a.remainingTimeMs - b.remainingTimeMs);
    
    items.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        
        let buyNowText = '';
        if (item.buyNowPrice > 0) {
            buyNowText = `<div class="item-info item-buynow">一口价: ${item.currencySymbol}${item.buyNowPrice}</div>`;
        }
        
        let bidderText = '';
        if (item.hasBidder) {
            bidderText = `<div class="item-info item-bidder">当前出价者: ${item.currentBidder}</div>`;
        }
        
        card.innerHTML = `
            <div class="item-header">
                <div class="item-name">${escapeHtml(item.itemName)}</div>
                <div class="item-amount">${item.itemAmount}</div>
            </div>
            <div class="item-info item-seller">卖家: ${escapeHtml(item.sellerName)}</div>
            <div class="item-info item-price">当前价格: ${item.currencySymbol}${item.currentPrice}</div>
            ${buyNowText}
            <div class="item-info item-time">剩余时间: ${item.remainingTimeFormatted}</div>
            ${bidderText}
        `;
        
        grid.appendChild(card);
    });
}

// HTML转义函数，防止XSS攻击
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 格式化时间函数
function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 格式化剩余时间函数
function formatRemainingTime(milliseconds) {
    if (milliseconds <= 0) {
        return "已过期";
    }
    
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    const remainingHours = hours % 24;
    const remainingMinutes = minutes % 60;
    const remainingSeconds = seconds % 60;
    
    let result = "";
    
    if (days > 0) {
        result += days + "天 ";
    }
    
    if (remainingHours > 0 || days > 0) {
        result += remainingHours + "小时 ";
    }
    
    if (remainingMinutes > 0 || remainingHours > 0 || days > 0) {
        result += remainingMinutes + "分钟 ";
    }
    
    result += remainingSeconds + "秒";
    
    return result;
}
