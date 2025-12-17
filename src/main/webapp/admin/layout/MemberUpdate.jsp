<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@page import="com.pet.model.member.Admin" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>MemberUpdate.jsp</title>

            <!-- Google Font: Source Sans Pro -->
            <link rel="stylesheet"
                href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
            <!-- Font Awesome -->
            <link rel="stylesheet" href="../../plugins/fontawesome-free/css/all.min.css">
            <!-- Theme style -->
            <link rel="stylesheet" href="../../dist/css/adminlte.min.css">
            <link rel="stylesheet" href="../../plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
            <link rel="stylesheet" href="../../plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
            <link rel="stylesheet" href="../../plugins/select2/css/select2.min.css">
            <link rel="stylesheet" href="../../plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css">
            <link rel="stylesheet" href="../../plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css">
            <style type="text/css">
                /* 調整 sidebar logo 排版 */
                .brand-link {
                    display: flex;
                    align-items: center;
                    justify-content: flex-start;
                    /* 展開時靠左 */
                    padding: 0.5rem 1rem;
                }

                /* 收起 sidebar 時，icon 置中 */
                .sidebar-collapse .brand-link {
                    justify-content: center;
                }

                /* 調整 icon 與文字間距 */
                .brand-link i {
                    width: 1.5rem;
                    /* 固定寬度讓文字距離固定 */
                    text-align: center;
                }

                /* 收起 sidebar 時文字隱藏 */
                .sidebar-collapse .brand-text {
                    display: none;
                }

                .box1 {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
            </style>
        </head>

        <body class="hold-transition sidebar-mini sidebar-collapse">
            <!-- Site wrapper -->
            <div class="wrapper">
                <!-- Navbar -->
                <nav class="main-header navbar navbar-expand navbar-white navbar-light">
                    <!-- Left navbar links -->
                    <ul class="navbar-nav">
                        <li class="nav-item">
                            <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i
                                    class="fas fa-bars"></i></a>
                        </li>
                    </ul>
				<!-- 右側 Navbar 按鈕 -->
			    <ul class="navbar-nav ml-auto">
			        <li class="nav-item">
			            <a href="Login.html" class="btn btn-secondary btn-sm">
			                <i class="fas fa-sign-out-alt"></i> 登出
			            </a>
			        </li>
			    </ul>

                </nav>
                <!-- /.navbar -->

                <!-- Main Sidebar Container -->
                <aside class="main-sidebar sidebar-dark-primary elevation-4">
                    <!-- Brand Logo -->
                    <a href="../../index3.html" class="brand-link">
                        <i class="fas fa-paw" style="color: #FFD43B; font-size: 1.5rem;"></i>
                        <span class="brand-text font-weight-light"></span>
                    </a>

                    <!-- Sidebar -->
                    <div class="sidebar">
                        <div class="user-panel mt-3 pb-3 mb-3 d-flex">

                        </div>



                        <!-- Sidebar Menu -->
                        <nav class="mt-2">
                            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                                data-accordion="false">
                                <!-- Add icons to the links using the .nav-icon class
               with font-awesome or any other icon font library -->
                                <li class="nav-item">
                                    <a href="#" class="nav-link">
                                        <i class="nav-icon fas fa-edit"></i>
                                        <p>
                                            會員管理
                                            <i class="right fas fa-angle-left"></i>
                                        </p>
                                    </a>
                                    <ul class="nav nav-treeview">
                                <li class="nav-item">
                                    <a href="AdminQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>管理員資料</p>
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a href="MemberQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>會員基本資料</p>
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a href="MemberPetQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>會員寵物資料</p>
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a href="CouponQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>優惠券總覽</p>
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a href="CouponUsersQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>會員持有優惠券</p>
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a href="FavoritesQueryAll.html" class="nav-link">
                                        <i class="far fa-circle nav-icon"></i>
                                        <p>會員收藏清單</p>
                                    </a>
                                </li>
                            </ul>
                                <li class="nav-item">
              <a href="../../AllProducts" class="nav-link">
                <i class="nav-icon fas fa-edit"></i>
                <p>
                  商品管理
                  <i class="right fas fa-angle-left"></i>
                </p>
              </a>
            </li>
                                <li class="nav-item">
            <a href="#" class="nav-link"> 
            <i class="nav-icon fas fa-edit"></i>
				<p>
					訂單管理 <i class="right fas fa-angle-left"></i>
				</p>
			</a>
			<ul class="nav nav-treeview">
				<li class="nav-item"><a
					href="../../orderList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>查詢全部訂單</p>
				</a></li>
				<li class="nav-item"><a
					href="../../shipmentsList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>物流查詢</p>
				</a></li>
				<li class="nav-item"><a
					href="../../orderItemsList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>查詢全部訂單明細</p>
				</a></li>
				<li class="nav-item"><a
					href="../order/shopping.jsp"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>新增訂單</p>
				</a></li>
			</ul></li>
                                <li class="nav-item">
              <a href="../../GetAllAppointmentsServlet" class="nav-link">
                <i class="nav-icon fas fa-edit"></i>
                <p>
                  預約管理
                  <i class="right fas fa-angle-left"></i>
                </p>
              </a>
            </li>

                            </ul>
                        </nav>
                        <!-- /.sidebar-menu -->
                    </div>
                    <!-- /.sidebar -->
                </aside>

                <!-- Content Wrapper. Contains page content -->
                <div class="content-wrapper">
                    <!-- Content Header (Page header) -->
                    <section class="content-header">
                        <div class="container-fluid">
                            <div class="row mb-2">
                                <div class="col-sm-12 box1">
                                    <h1>會員管理</h1>
                                    <a href="MemberQueryAll.html" class="btn btn-secondary btn-sm">
                                        回列表
                                    </a>
                                </div>
                                <div class="col-sm-6">

                                </div>
                            </div>
                        </div><!-- /.container-fluid -->
                    </section>

                    <!-- Main content -->
                    <section class="content">
                        <div class="container-fluid">
                            <div class="row">
                                <div class="col-12">
                                    <div class="card">
                                        <!-- /.card-header -->
                                        <!-- Horizontal Form -->
                                        <div class="card card-info">
                                            <div class="card-header">
                                                <h3 class="card-title">修改會員資料</h3>
                                            </div>
                                            <!-- /.card-header -->
                                            <!-- form start -->
                                            <form class="form-horizontal" id="updateForm" enctype="multipart/form-data">
                                                <div class="card-body">
                                                    <div class="form-group row">
                                                        <label for="inputEmail3"
                                                            class="col-sm-2 col-form-label">ID:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="memberId"
                                                                autocomplete="off" readonly>
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label for="inputEmail3"
                                                            class="col-sm-2 col-form-label">姓名:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="name"
                                                                placeholder="姓名:" autocomplete="off">
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label for="inputPassword3"
                                                            class="col-sm-2 col-form-label">Email:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="email"
                                                                placeholder="Email:" autocomplete="off">
                                                        </div>
                                                    </div>
                                                    
                                                    <div class="form-group row">
                                                        <label class="col-sm-2 col-form-label">性別:</label>
                                                        <div class="col-sm-10">
                                                            <select class="form-control select2" style="width: 100%;"
                                                                id="gender">
                                                                <option value="M">男</option>
                                                                <option value="F" selected>女</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label class="col-sm-2 col-form-label">生日:</label>
                                                        <div class="col-sm-10">
                                                            <div class="input-group date" id="reservationdate"
                                                                data-target-input="nearest">
                                                                <input type="text"
                                                                    class="form-control datetimepicker-input"
                                                                    id="birthday" data-target="#reservationdate" />
                                                                <div class="input-group-append"
                                                                    data-target="#reservationdate"
                                                                    data-toggle="datetimepicker">
                                                                    <div class="input-group-text"><i
                                                                            class="fa fa-calendar"></i></div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label for="inputPassword3"
                                                            class="col-sm-2 col-form-label">電話:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="phone"
                                                                placeholder="電話:" autocomplete="off">
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label for="inputPassword3"
                                                            class="col-sm-2 col-form-label">地址:</label>
                                                        <div class="col-sm-10">
                                                            <input type="text" class="form-control" id="address"
                                                                placeholder="地址:" autocomplete="off">
                                                        </div>
                                                    </div>
                                                    <div class="form-group row">
                                                        <label for="inputPassword3"
                                                            class="col-sm-2 col-form-label">大頭貼上傳:</label>
                                                        <div class="col-sm-10">
                                                            <div class="input-group">
                                                                <div class="custom-file">
                                                                    <input type="file"
                                                                        class="form-control custom-file-input"
                                                                        id="picture" name="picture">
                                                                    <label class="custom-file-label"
                                                                        for="picture">Choose file</label>
                                                                </div>
                                                                <div class="input-group-append">
                                                                    <span class="input-group-text">Upload</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <!-- /.card-body -->
                                                <div class="card-footer">
                                                    <button type="submit" class="btn btn-info">送出</button>
                                                    <button type="reset" class="btn btn-default float-right">清除</button>
                                                </div>
                                                <!-- /.card-footer -->
                                            </form>
                                        </div>
                                        <!-- /.card-body -->
                                    </div>
                                    <!-- /.card -->
                                </div>
                            </div>
                        </div>
                    </section>
                    <!-- /.content -->
                </div>
                <!-- /.content-wrapper -->

                <footer class="main-footer">
                    <div class="float-right d-none d-sm-block">
                        <b>Version</b> 3.2.0
                    </div>
                    <strong>Copyright &copy; 2014-2021 <a href="https://adminlte.io">AdminLTE.io</a>.</strong> All
                    rights reserved.
                </footer>

                <!-- Control Sidebar -->
                <aside class="control-sidebar control-sidebar-dark">
                    <!-- Control sidebar content goes here -->
                </aside>
                <!-- /.control-sidebar -->
            </div>
            <!-- ./wrapper -->

            <!-- jQuery -->
            <script src="../../plugins/jquery/jquery.min.js"></script>
            <!-- Bootstrap 4 -->
            <script src="../../plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
            <!-- DataTables  & Plugins -->
            <script src="../../plugins/datatables/jquery.dataTables.min.js"></script>
            <script src="../../plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
            <script src="../../plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
            <script src="../../plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>
            <script src="../../plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
            <script src="../../plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
            <script src="../../plugins/jszip/jszip.min.js"></script>
            <script src="../../plugins/pdfmake/pdfmake.min.js"></script>
            <script src="../../plugins/pdfmake/vfs_fonts.js"></script>
            <script src="../../plugins/datatables-buttons/js/buttons.html5.min.js"></script>
            <script src="../../plugins/datatables-buttons/js/buttons.print.min.js"></script>
            <script src="../../plugins/datatables-buttons/js/buttons.colVis.min.js"></script>
            <script src="../../plugins/moment/moment.min.js"></script>
            <script src="../../plugins/tempusdominus-bootstrap-4/js/tempusdominus-bootstrap-4.min.js"></script>
            <script src="../../plugins/select2/js/select2.full.min.js"></script>
            <!-- AdminLTE App -->
            <script src="../../dist/js/adminlte.min.js"></script>
            <!-- AdminLTE for demo purposes
            <script src="../../dist/js/demo.js"></script> -->
            <!-- Page specific script -->
            <script>
                $(function () {

                    $('#example2').DataTable({
                        "paging": true,
                        "lengthChange": false, // 關閉每頁筆數選項
                        "searching": false,    // 關閉搜索框
                        "ordering": true,
                        "info": true,          // 顯示資訊文字
                        "autoWidth": true,
                        "responsive": true,

                        // **【關鍵修正】使用 dom 屬性強制分頁 (p) 靠右 (col-md-7)**
                        // 結構: 表格本體(t) / 新的一行: [資訊(i) - 左 5 欄] [分頁(p) - 右 7 欄]
                        "dom": '<"row"<"col-sm-12"t>><"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7 d-flex justify-content-end"p>>'
                    });
                 // 顯示選擇的檔案名稱
                    $('.custom-file-input').on('change', function(e){
                        const fileName = e.target.files[0].name;
                        $(this).next('.custom-file-label').html(fileName);
                    });
                    
                 // 啟用 DatePicker (日期時間選擇器)
                    $('#reservationdate').datetimepicker({
        			    format: 'YYYY/MM/DD',    // 只顯示日期
        			    useCurrent: false,       // 預設不選時間
        			    // 這裡沒有 timepicker，所以不會送時間
        			    minDate: moment('1900-01-01'),
    					maxDate: moment(), // 今天
        			});

                    // 從URL拿memberId
                    const urlParams = new URLSearchParams(window.location.search);
                    const memberId = urlParams.get('memberId');
                    $('#memberId').val(memberId);

                    // 載入管理員資料
                    $.ajax({
                        url: '/pet/MemberServlet',
                        method: 'GET',
                        data: { action: 'queryById', memberId: memberId },
                        dataType: 'json',
                        success: function (member) {
                            $('#email').val(member.email);
                            $('#name').val(member.name);
                            $('#gender').val(member.gender).trigger('change');
                            $('#birthday').val(member.birthday);
                            $('#phone').val(member.phone);
                            $('#address').val(member.address);
                        },
                        error: function () {
                            alert('載入資料失敗');
                        }
                    });


                    // 送出更新
                    $('#updateForm').submit(function (e) {
                        e.preventDefault();
                    	
                        const email = $('#email').val().trim();
                     // ----------- Email 驗證函式 -----------
                        function isValidEmail(email) {
                            const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                            return regex.test(email);
                        }

                        
                        if (email === '') {
                            e.preventDefault();
                            alert('Email不得為空白');
                            return;
                        }

                        // Email 格式不正確
                        if (!isValidEmail(email)) {
                            e.preventDefault();
                            alert('Email 格式不正確');
                            return;
                        }

                        const formData = new FormData();

                        formData.append("memberId", $("#memberId").val());
                        formData.append("name", $("#name").val());
                        formData.append("email", $("#email").val());
                        formData.append("phone", $("#phone").val());
                        formData.append("address", $("#address").val());
                        formData.append("gender", $("#gender").val());
                        formData.append("birthday", $("#birthday").val());

                        //  圖片
                        const file = $("#picture")[0].files[0];
                        if (file) {
                            formData.append("picture", file); 
                        }

                        $.ajax({
                            url: "/pet/MemberServlet?action=update",
                            method: "POST",
                            data: formData,
                            processData: false,
                            contentType: false,
                            success: function (response) {
                                console.log("更新成功");
                                console.log(response);
                                if (response && response.memberId) {
                                    // 更新成功，帶參數跳轉到完成頁面
                                    var params = new URLSearchParams();
                                    params.append('memberId', response.memberId);
                                    params.append('name', response.name);
                                    params.append('email', response.email);
                                    params.append('phone', response.phone);
                                    params.append('address', response.address);
                                    params.append('gender', response.gender);
                                    params.append('birthday', response.birthday);
                                    params.append('picture', response.picture);
                                    window.location.href = 'MemberUpdateDone.jsp?' + params.toString();
                                } else {
                                    alert('更新失敗，伺服器回傳空值');
                                }
                            },
                            error: function (xhr) {
                                console.log("錯誤");
                                console.log(xhr.responseText);
                                alert("更新失敗");
                            }
                        });
                    });
                });

            </script>
        </body>

        </html>