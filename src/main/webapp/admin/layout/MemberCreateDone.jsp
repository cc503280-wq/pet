<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.pet.model.member.Admin"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>MemberCreateDone</title>

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

            .form-control-static {
                padding-top: 7px;
            }
            
            .box2{
            	display:flex;
            	justify-content: center;
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
    <div class="container-fluid d-flex justify-content-center">
        <div class="row w-100 box2">
            <div class="col-md-6 "> <!-- 限制卡片寬度 -->
                <div class="card">
                    <!-- Horizontal Form -->
                    <div class="card card-info">
                        <div class="card-header">
                            <h3 class="card-title">新增資料檢視</h3>
                        </div>
                        
                        <div class="alert alert-success mx-4 mt-3 py-2">
                            <i class="icon fas fa-check-circle"></i>
                            <strong>新增成功!</strong>
                        </div>

                        <%
                        String memberId = request.getParameter("memberId");
                        String name = request.getParameter("name");
                        String email = request.getParameter("email");
                        String phone = request.getParameter("phone");
                        String address = request.getParameter("address");
                        String picture = request.getParameter("picture");
                        String birthday = request.getParameter("birthday"); //時間待處理
                        String gender = request.getParameter("gender");
                        
                        %>

                        <form class="form-horizontal">
                            <div class="card-body">
                                <!-- 大頭貼置中 -->
                                <div class="form-group row justify-content-center mb-4">
                                    <img src="<%= picture %>" alt="大頭貼" class="rounded-circle" 
                                        style="width:120px; height:120px; object-fit:cover; border:2px solid #FFD43B;">
                                </div>

                                <!-- 資料靠左 -->
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">ID:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= memberId %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">姓名:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= name %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">性別:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= gender %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">生日:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= birthday %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">Email:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= email %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">電話:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= phone %></p>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-4 col-form-label">地址:</label>
                                    <div class="col-sm-8">
                                        <p class="form-control-static"><%= address %></p>
                                    </div>
                                </div>
                            </div>
                            <!-- /.card-body -->
                            <div class="card-footer text-right">
                                <a href="MemberCreate.html" class="btn btn-info">
                                    <i class="fas fa-plus"></i> 繼續新增
                                </a>
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
                <strong>Copyright &copy; 2014-2021 <a href="https://adminlte.io">AdminLTE.io</a>.</strong> All rights
                reserved.
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

                
            });
        </script>
    </body>

    </html>