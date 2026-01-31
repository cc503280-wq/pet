/* dist/js/admin-layout.js */

// ==========================================
// 1. 全域設定與主題
// ==========================================
const themeColors = { primary: '#be8754', secondary: '#ffc20f', dark: '#6a2614', light: '#fbf7ea', text: '#5a4530' };

// ==========================================
// 2. HTML 模板 (Navbar, Sidebar, Chat)
// ==========================================

// Navbar HTML (注意：登出按鈕 ID 是 logoutBtn)
const navbarHTML = `
<nav class="main-header navbar navbar-expand navbar-white navbar-light">
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link text-white" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
        </li>
    </ul>
    <ul class="navbar-nav ml-auto align-items-center">
        <li class="nav-item">
            <a href="#" class="btn btn-theme-action btn-sm shadow-sm mr-2 position-relative" onclick="toggleAdminChat(); return false;">
                <i class="far fa-comments"></i> 客服中心
                <span class="badge badge-danger position-absolute" id="globalUnreadBadge"
                    style="display: none; top: -5px !important; right: -5px !important; border-radius: 50%; padding: 3px 6px !important; font-size: 11px !important; line-height: 1 !important; z-index: 10; border: 1.5px solid white;">0</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="javascript:void(0)" id="logoutBtn" class="btn btn-theme-action btn-sm shadow-sm">
                <i class="fas fa-sign-out-alt"></i> 登出
            </a>
        </li>
    </ul>
</nav>
`;

// Sidebar HTML
const sidebarHTML = `
<aside class="main-sidebar">
    <a href="/admin/layout/Home.html" class="brand-link">
        <i class="fas fa-paw" style="color: #ffc20f; font-size: 1.8rem;"></i>
    </a>

    <div class="sidebar">
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">
                <li class="nav-item">
                    <a href="#" class="nav-link"><i class="nav-icon fas fa-edit"></i><p>會員管理 <i class="right fas fa-angle-left"></i></p></a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item"><a href="/admin/layout/AdminQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>管理員資料</p></a></li>
                        <li class="nav-item"><a href="/admin/layout/MemberQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>會員基本資料</p></a></li>
                        <li class="nav-item"><a href="/admin/layout/MemberPetQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>會員寵物資料</p></a></li>
                        <li class="nav-item"><a href="/admin/layout/CouponQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>優惠券總覽</p></a></li>
                        <li class="nav-item"><a href="/admin/layout/CouponUsersQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>會員持有優惠券</p></a></li>
                        <li class="nav-item"><a href="/admin/layout/FavoritesQueryAll.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>會員收藏清單</p></a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link"><i class="nav-icon fas fa-edit"></i><p>商品管理 <i class="right fas fa-angle-left"></i></p></a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item"><a href="/admin/products" class="nav-link"><i class="far fa-circle nav-icon"></i><p>商品管理</p></a></li>
                        <li class="nav-item"><a href="/admin/categories" class="nav-link"><i class="far fa-circle nav-icon"></i><p>類別管理</p></a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link"><i class="nav-icon fas fa-edit"></i><p>訂單管理 <i class="right fas fa-angle-left"></i></p></a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item"><a href="/orders/list" class="nav-link"><i class="far fa-circle nav-icon"></i><p>查詢全部訂單</p></a></li>
                        <li class="nav-item"><a href="/shipment/list" class="nav-link"><i class="far fa-circle nav-icon"></i><p>物流查詢</p></a></li>
                        <li class="nav-item"><a href="/ordersItem/list" class="nav-link"><i class="far fa-circle nav-icon"></i><p>查詢全部訂單明細</p></a></li>
                        <li class="nav-item"><a href="/Cart/shopping" class="nav-link"><i class="far fa-circle nav-icon"></i><p>新增訂單</p></a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link"><i class="nav-icon fas fa-edit"></i><p>預約管理 <i class="right fas fa-angle-left"></i></p></a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item"><a href="GetAllAppointments.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>預約訂單列表</p></a></li>
                        <li class="nav-item"><a href="GetAllAppointmentDetails.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>預約明細</p></a></li>
                        <li class="nav-item"><a href="GetAllServiceItems.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>服務項目</p></a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link"><i class="nav-icon fas fa-edit"></i><p>人員管理 <i class="right fas fa-angle-left"></i></p></a>
                    <ul class="nav nav-treeview">
					    <li class="nav-item"><a href="AuditDashboard.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>系統監控儀表板</p></a></li>
                        <li class="nav-item"><a href="GetAllGroomers.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>美容師列表</p></a></li>
                        <li class="nav-item"><a href="GetAllLeaveRecords.html" class="nav-link"><i class="far fa-circle nav-icon"></i><p>休假審核</p></a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>
</aside>
`;

// Chat Widget HTML
const chatWidgetHTML = `
<div id="adminChatWidget">
    <div class="chat-header">
        <div id="chatHeaderLeft" class="d-flex align-items-center">
            <i class="bi bi-chat-dots-fill mr-2" style="font-size: 1.5rem;"></i>
            <span class="font-weight-bold">客服中心</span>
        </div>
        <div class="d-flex align-items-center">
            <button type="button" class="btn btn-sm text-white mr-2" id="backToListBtn" style="display: none; font-size: 1.2rem;"
                onclick="showUserList()" title="返回列表">
                <i class="fas fa-arrow-left"></i>
            </button>
            <button type="button" class="btn btn-sm text-white" onclick="toggleAdminChat()" title="關閉" style="font-size: 1.2rem;">
                <i class="fas fa-times"></i>
            </button>
        </div>
    </div>
    <div id="view-user-list" class="user-list-body">
        <div id="contactListBody"></div>
    </div>
    <div id="view-chat-room" class="chat-body" style="display: none;">
        <div id="adminMsgBox" style="display: flex; flex-direction: column;"></div>
    </div>
    <div id="quickReplyArea" class="px-2 pb-1 border-top pt-2 bg-light" style="display: none;">
        <button class="btn btn-outline-secondary btn-sm rounded-pill mb-1 mr-1" onclick="sendQuickReply('您好，請問有什麼可以協助您的嗎?')" style="font-size: 0.85rem;">
            您好，請問有什麼可以協助您的嗎?
        </button>
        <button class="btn btn-outline-secondary btn-sm rounded-pill mb-1 mr-1" onclick="sendQuickReply('正在替您確認中，請稍後哦')" style="font-size: 0.85rem;">
            正在替您確認中，請稍後哦
        </button>
    </div>
    <div id="chatInputArea" class="chat-footer" style="display: none;">
        <div class="input-group">
            <input type="text" id="adminMsgInput" placeholder="請輸入訊息..." class="form-control border-0 chat-input">
            <span class="input-group-append ml-2">
                <button type="button" class="btn btn-link text-primary" onclick="sendAdminMessage()">
                    <i class="bi bi-send-fill" style="font-size: 1.5rem;"></i>
                </button>
            </span>
        </div>
    </div>
</div>
`;

// Footer HTML
const footerHTML = `
<footer class="main-footer">
    <strong>Copyright &copy; 2026 MaoMaoLand.</strong>
</footer>
`;

// ==========================================
// 3. 變數宣告 (Chat)
// ==========================================
let stompClientAdmin = null;
let currentUser = null;
let userList = [];
let isWidgetOpen = false;
let md;

// ==========================================
// 4. 主程式入口 (Document Ready)
// ==========================================
$(function () {

    // --- [A] 插入版型結構 (Layout) ---
    const $wrapper = $('.wrapper');
    if ($wrapper.length > 0) {
        $wrapper.prepend(navbarHTML);
        $wrapper.children('.navbar').after(sidebarHTML);
        $wrapper.append(footerHTML);

        // 修正：動態插入 HTML 後，需手動觸發 AdminLTE 的 Treeview 與 PushMenu 初始化
        // 因為 AdminLTE 可能在我們插入這些元素之前就已經跑完初始化了 (Race Condition)
        if ($.fn.Layout) {
            $('[data-widget="pushmenu"]').PushMenu();
            $('[data-widget="treeview"]').Treeview('init');
        }
    }
    $('body').append(chatWidgetHTML);

    // --- [B] 權限驗證 (Auth) - 這裡就是你的 adminAuth 邏輯 ---
    const adminRole = sessionStorage.getItem('role');
    const adminName = sessionStorage.getItem('adminName');

    // 取得當前檔名
    const currentPage = window.location.pathname.split("/").pop();

    // 權限攔截：沒登入就踢回登入頁
    // 邏輯：如果沒有 role，而且當前頁面不是 Login.html，就踢走
    if ((!adminRole || adminRole === 'undefined') && currentPage !== 'Login.html') {
        // 我先註解掉，以免開發時一直被踢，你確認要開啟時把下面這行打開即可
        // window.location.href = '/admin/layout/Login.html';
    }

    // --- [C] 登出邏輯 (Logout) - 整合你的 AJAX 與 SweetAlert ---
    // 使用 document.on 是因為 #logoutBtn 是動態生成的
    $(document).on('click', '#logoutBtn', function (e) {
        e.preventDefault();

        // 使用 SweetAlert2 替代原生的 confirm
        Swal.fire({
            title: '確定要登出嗎？',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: themeColors.primary,
            cancelButtonColor: '#d33',
            confirmButtonText: '登出',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                // 這是你原本的 AJAX 邏輯
                $.ajax({
                    url: '/auth/logout',
                    method: 'POST',
                    success: function () {
                        sessionStorage.clear();
                        window.location.href = '/admin/layout/Login.html';
                    },
                    error: function () {
                        // 即使後端 Session 過期，前端也要清空並跳轉
                        sessionStorage.clear();
                        window.location.href = '/admin/layout/Login.html';
                    }
                });
            }
        });
    });

    // --- [D] 其他初始化 ---
    if (window.markdownit) {
        md = window.markdownit({ breaks: true, linkify: true });
    }
    highlightActiveMenu();

    // Chat 初始化
    $('#adminMsgInput').keypress(function (e) { if (e.which == 13) sendAdminMessage(); });
    initAdminWebSocket();
    loadUserList();
});

// ==========================================
// 5. 輔助函式 (Helper Functions)
// ==========================================

// 自動標記當前選單
function highlightActiveMenu() {
    const path = window.location.pathname;
    const page = path.split("/").pop();

    $('.nav-sidebar a.nav-link').each(function () {
        const href = $(this).attr('href');
        // 修正比對邏輯，支援相對路徑
        if (href === page || href.endsWith('/' + page) || (page === '' && href === 'Home.html')) {
            $(this).addClass('active');
            $(this).parents('.nav-item').addClass('menu-open');
            $(this).parents('.nav-item').children('a.nav-link').addClass('active');
        }
    });
}

// ==========================================
// 6. 客服聊天室邏輯 (Chat Logic)
// ==========================================
// 輔助：安全解析時間格式
function safeParseTime(createdAt) {
    if (!createdAt) return '';
    // 如果是 ISO 字串 (2026-01-31T16:15:20)
    if (typeof createdAt === 'string' && createdAt.length >= 16) {
        return createdAt.substring(11, 16);
    }
    // 如果是陣列 [2026, 1, 31, 16, 15, 30]
    if (Array.isArray(createdAt) && createdAt.length >= 5) {
        const hh = createdAt[3].toString().padStart(2, '0');
        const mm = createdAt[4].toString().padStart(2, '0');
        return `${hh}:${mm}`;
    }
    return '';
}

function toggleAdminChat() {
    isWidgetOpen = !isWidgetOpen;
    if (isWidgetOpen) {
        $('#adminChatWidget').css('display', 'flex').hide().fadeIn();
        loadUserList();
    } else {
        $('#adminChatWidget').fadeOut();
        currentUser = null;
    }
}

function showUserList() {
    currentUser = null;
    $('#chatHeaderLeft').html('<i class="bi bi-chat-dots-fill mr-2" style="font-size: 1.5rem;"></i><span class="font-weight-bold">客服中心</span>');
    $('#backToListBtn').hide();
    $('#view-chat-room').hide(); $('#chatInputArea').hide(); $('#quickReplyArea').hide();
    $('#view-user-list').fadeIn();
    loadUserList();
}

function enterChatRoom(id, name, pic) {
    currentUser = { id: id, name: name, pic: pic };
    $('#chatHeaderLeft').html(`
        <div class="d-flex align-items-center">
            <img src="${pic}" style="width: 45px; height: 45px; border-radius: 50%; border: 2px solid #fff; margin-right: 12px; object-fit: cover;">
            <div>
                <div class="font-weight-bold" style="line-height:1.2; font-size: 1.3rem;">${name}</div>
                <small style="opacity: 0.9; font-size: 0.95rem;">ID: ${id}</small>
            </div>
        </div>
    `);
    $('#view-user-list').hide();
    $('#backToListBtn').show();
    $('#view-chat-room').css('display', 'flex');
    $('#quickReplyArea').show(); // 顯示快速回覆區域
    $('#chatInputArea').show();
    $.post('/admin/chat/read?memberId=' + id);

    const index = userList.findIndex(x => x.member.memberId === id);
    if (index !== -1) {
        userList[index].unreadCount = 0;
    }

    calcTotalUnread();
    loadAdminHistory(id);
}

function initAdminWebSocket() {
    const socket = new SockJS('http://localhost:8081/shop/ws-chat');
    stompClientAdmin = Stomp.over(socket);
    stompClientAdmin.debug = null;
    stompClientAdmin.connect({}, function () {
        stompClientAdmin.subscribe('/topic/admin', function (message) {
            handleAdminNewMsg(JSON.parse(message.body));
        });
    }, function (err) { console.error(err); });
}

function loadUserList() {
    $.get('/admin/chat/recent').done(function (data) {
        userList = (Array.isArray(data)) ? data : (data.data || []);
        renderUserListUI();
    }).fail(function () { renderUserListUI(); });
}

function renderUserListUI() {
    const listEl = $('#contactListBody'); listEl.empty();
    if (!userList || userList.length === 0) {
        listEl.html('<div class="h-100 d-flex flex-column align-items-center justify-content-center text-muted"><i class="bi bi-chat-square-dots" style="font-size: 4rem; opacity:0.3;"></i><p class="mt-2" style="font-size: 1.2rem;">目前尚無訊息</p></div>');
        calcTotalUnread(); return;
    }
    userList.forEach(item => {
        const m = item.member;
        const pic = m.picture || '../../dist/img/default-150x150.png';
        const count = parseInt(item.unreadCount) || 0;
        const unreadHtml = count > 0 ? `<span class="badge badge-danger rounded-pill ml-auto" style="font-size: 0.9rem; padding: 5px 8px;">${count}</span>` : '';

        listEl.append(`
            <div class="contact-item" onclick="enterChatRoom(${m.memberId}, '${m.name}', '${pic}')">
                <img src="${pic}" style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover; margin-right: 15px;">
                <div style="flex-grow: 1; overflow: hidden;">
                    <div class="d-flex align-items-center">
                        <span class="contact-name text-dark mr-2">${m.name}</span>
                        <small class="contact-id">ID: ${m.memberId}</small>
                    </div>
                    <div class="text-truncate text-muted" style="font-size: 1rem;">點擊查看對話...</div>
                </div>
                ${unreadHtml}
            </div>
        `);
    });
    calcTotalUnread();
}

function loadAdminHistory(id) {
    const box = $('#adminMsgBox'); box.html('<div class="text-center text-muted mt-4" style="font-size:1.2rem;">載入中...</div>');
    $.get('/admin/chat/history?memberId=' + id).done(msgs => {
        box.empty();
        if (!msgs || msgs.length === 0) { box.html('<div class="text-center text-muted mt-5 opacity-50"><small style="font-size:1.1rem;">— 尚無歷史訊息 —</small></div>'); }
        else { msgs.forEach(msg => appendAdminMsgUI(msg)); }
        scrollToBottom();
    });
}

function appendAdminMsgUI(msg) {
    const box = $('#adminMsgBox');

    // 特殊處理：系統訊息 (SYSTEM)
    if (msg.sender === 'SYSTEM') {
        const timeStr = safeParseTime(msg.createdAt);
        box.append(`
            <div class="d-flex justify-content-center my-3">
                <span class="badge badge-secondary px-3 py-2 shadow-sm" style="font-size: 0.9rem; opacity: 0.9; border-radius: 20px;">
                    <i class="fas fa-info-circle mr-1"></i> ${msg.content} <small class="ml-1 opacity-75">${timeStr}</small>
                </span>
            </div>
        `);
        return;
    }

    const isSystem = (msg.sender === 'ADMIN' || msg.sender === 'AI');
    const wrapperClass = isSystem ? 'admin-wrapper' : 'member-wrapper';
    let contentHtml = md ? md.render(msg.content) : msg.content;
    const timeStr = safeParseTime(msg.createdAt);

    box.append(`
        <div class="message-wrapper ${wrapperClass}">
            <div class="message-container">
                <div class="message-bubble shadow-sm">
                    <div class="markdown-content">${contentHtml}</div>
                </div>
                <div class="message-time align-self-end mx-2">${timeStr}</div>
            </div>
        </div>
    `);
}

function scrollToBottom() { const box = document.getElementById('view-chat-room'); if (box) box.scrollTop = box.scrollHeight; }

function sendAdminMessage() {
    const input = $('#adminMsgInput'); const content = input.val().trim();
    if (!content || !currentUser) return;
    const payload = { memberId: currentUser.id, sender: 'ADMIN', content: content };
    if (stompClientAdmin && stompClientAdmin.connected) {
        stompClientAdmin.send("/app/sendMessage", {}, JSON.stringify(payload));
        appendAdminMsgUI({ sender: 'ADMIN', content: content, createdAt: new Date().toISOString() });
        input.val(''); scrollToBottom();
    } else {
        Swal.fire({
            icon: 'error',
            title: '連線中斷',
            text: '無法發送訊息，請檢查網路連線',
            confirmButtonColor: themeColors.primary
        });
    }
}

function sendQuickReply(msg) {
    if (!msg) msg = "您好，請問有什麼可以協助您的嗎?";
    $('#adminMsgInput').val(msg);
    sendAdminMessage();
}

function handleAdminNewMsg(msg) {
    if (isWidgetOpen && currentUser && currentUser.id == msg.memberId) {
        appendAdminMsgUI(msg);
        scrollToBottom();
        $.post('/admin/chat/read?memberId=' + currentUser.id);

        // 如果是 SYSTEM 訊息，除了顯示在聊天室，也跳個 Toast 提醒
        if (msg.sender === 'SYSTEM') {
            Swal.fire({
                toast: true,
                position: 'top-end',
                icon: 'info',
                title: msg.content,
                showConfirmButton: false,
                timer: 3000
            });
        }
        return;
    }

    // 如果不在聊天室，如果是 SYSTEM 訊息，也跳個 Toast
    if (msg.sender === 'SYSTEM') {
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'info',
            title: `會員 ${msg.memberId}: ${msg.content}`,
            showConfirmButton: false,
            timer: 3000
        });
    }

    let target = userList.find(u => u.member.memberId === msg.memberId);
    if (!target) { loadUserList(); return; }

    target.unreadCount = (parseInt(target.unreadCount) || 0) + 1;
    userList = userList.filter(u => u !== target); userList.unshift(target);

    if (isWidgetOpen && !currentUser) { renderUserListUI(); } else { calcTotalUnread(); }
}

function calcTotalUnread() {
    let total = 0;
    if (userList && Array.isArray(userList)) {
        userList.forEach(u => { total += (parseInt(u.unreadCount) || 0); });
    }
    const badge = $('#globalUnreadBadge');
    if (total > 0) {
        badge.text(total).fadeIn();
        badge.addClass('animate__animated animate__bounceIn');
    } else {
        badge.fadeOut();
    }
}