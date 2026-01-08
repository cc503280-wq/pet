$(function () {
	const adminRole = sessionStorage.getItem('role');
	const adminName = sessionStorage.getItem('adminName');
    // 權限攔截：沒登入就踢回登入頁
    // 取得當前檔名，避免在 Login.html 執行導致死循環
    const currentPage = window.location.pathname.split("/").pop();
    if (!adminRole && currentPage !== 'Login.html') {
        window.location.href = 'Login.html';
    }

    //  統一處理登出點擊事件
    // 使用「委託綁定」，這樣就算 HTML 是動態產生的也能生效
    $(document).on('click', '#logoutBtn', function (e) {
        e.preventDefault();
        
        if (confirm('確定要登出嗎？')) {
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