<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ page import="java.util.List"%>
<!-- InsertAppointment.jsp -->
<!DOCTYPE html>
<html lang="en">
<head>

<!-- Google Font: Source Sans Pro -->
  <link rel="stylesheet"
    href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
  <!-- Font Awesome -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/fontawesome-free/css/all.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/dist/css/adminlte.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
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

#btn-choose-employee {
	background-color: #0e71eb; /* 您指定的藍色底 */
	color: #fff; /* 您指定的黃色字 */
	border: 1px solid #0e71eb; /* 邊框同色 */
	font-weight: bold; /* 字體加粗比較好看 */
	transition: all 0.3s ease; /* 平滑過渡效果 */
}

#btn-choose-employee:hover {
	background-color: #0a58ca; /* 變深一點的藍色 */
	color: #ebce0e; /* 懸停時字體變白(比較顯眼)，若想維持黃色可改回 #ebce0e */
	border-color: #0a58ca;
	cursor: pointer;
}
/* ⭐ 新增按鈕樣式 End ⭐ */
</style>
</head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>修改預約訂單成功</title>
<body class="hold-transition sidebar-mini sidebar-collapse">
	<!-- Site wrapper -->
	<div class="wrapper">
		<!-- Navbar -->
            <nav class="main-header navbar navbar-expand navbar-white navbar-light">
                 <!-- 右側 Navbar 按鈕 -->
			    <ul class="navbar-nav ml-auto">
			        <li class="nav-item">
			            <a href="${pageContext.request.contextPath}/AdminLogoutServlet" class="btn btn-secondary btn-sm">
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
                    <!-- Sidebar Menu -->
                    <nav class="mt-2">
                        <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                            data-accordion="false">
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
            <li class="nav-item"><a href="#" class="nav-link"> <i
								class="nav-icon fas fa-edit"></i>
				<p>
					訂單管理 <i class="right fas fa-angle-left"></i>
				</p>
		</a>
			<ul class="nav nav-treeview">
				<li class="nav-item"><a
					href="${pageContext.request.contextPath}/orderList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>查詢全部訂單</p>
				</a></li>
				<li class="nav-item"><a
					href="${pageContext.request.contextPath}/shipmentsList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>物流查詢</p>
				</a></li>
				<li class="nav-item"><a
					href="${pageContext.request.contextPath}/orderItemsList"
					class="nav-link"> <i class="far fa-circle nav-icon"></i>
						<p>查詢全部訂單明細</p>
				</a></li>
				<li class="nav-item"><a
					href="${pageContext.request.contextPath}/admin/order/shopping.jsp"
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
							<h1>預約訂單管理</h1>
							<a href="GetAllAppointmentsServlet"
								class="btn btn-secondary btn-sm"> 回列表 </a>
						</div>
						<div class="col-sm-6"></div>
					</div>
				</div>
				<!-- /.container-fluid -->
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
										<h3 class="card-title">修改約訂單</h3>
									</div>
									<div class="alert alert-success mx-4 mt-3 py-2">
										<i class="icon fas fa-check-circle"></i> <strong>修改成功!</strong>
									</div>
									<!-- /.card-header -->
									<!-- form start -->

									<form class="form-horizontal" action="#" method="post">
										<div class="card-body">

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">訂單編號:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="AppointmentId"
														name="AppointmentId" value="${dto.appointmentId}" readonly />
												</div>
											</div>

											<div class="form-group row">
												<label for="memberName" class="col-sm-2 col-form-label">訂單狀態:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														value="${dto.appointmentStatus}" readonly />
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">付款狀態:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														value="${dto.payStatus}" readonly />
												</div>
											</div>

											<div class="form-group row">
												<label for="memberName" class="col-sm-2 col-form-label">會員姓名:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="memberName"
														name="memberName" value="${dto.memberName}" readonly />
												</div>
											</div>

											<div class="form-group row">
												<label for="petName" class="col-sm-2 col-form-label">寵物:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														value="${dto.petName}" readonly />
												</div>
											</div>

											<div class="form-group row">
												<label for="serviceName" class="col-sm-2 col-form-label">預約服務:</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														value="${dto.serviceName}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label for="durationMinutes" class="col-sm-2 col-form-label">服務時長(min)
													:</label>
												<div class="col-sm-10">
													<input type="text" name="durationMinutes"
														id="durationMinutes" class="form-control"
														value="${dto.durationMinutes}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label for="totalPrice" class="col-sm-2 col-form-label">總價
													:</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" name="totalPrice"
														id="totalPrice" value="${dto.price}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label for="durationMinutes" class="col-sm-2 col-form-label">備註
													:</label>
												<div class="col-sm-10">
													<textarea class="form-control" id="notes" name="notes"
														rows="3" cols="33">${dto.notes}</textarea>
												</div>
											</div>

											<div class="form-group row">
												<label for="display_emp" class="col-sm-2 col-form-label">選擇美容師:</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" name="display_emp"
														id="ename" value="${dto.empName}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label for="slot_display" class="col-sm-2 col-form-label">預約時段:</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" name="slot_display"
														id="slot_display"
														value="${dto.appointmentDateStr} ${dto.appointmentstartTime} ~ ${dto.appointmentendTime}"
														readonly>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label"></label>
												<div class="col-sm-10">
													<button class="form-control" id="btn-choose-employee"
														type="button">選擇美容師和時段</button>
												</div>
											</div>



											<div class="form-group row">
												<label class="col-sm-2 col-form-label">評分:</label>
												<div class="col-sm-10">
													<input class="form-control" type="number" name="rating"
														id="rating" value="${dto.rating}" min="0" max="5"
														style="text-align: left;">
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">會員評論: </label>
												<div class="col-sm-10">
													<textarea class="form-control" name="comment" rows="3"
														placeholder="Null">${dto.comment}</textarea>
												</div>
											</div>


											<div class="form-group row">
												<label class="col-sm-2 col-form-label">服務美容師回覆:</label>
												<div class="col-sm-10">
													<textarea class="form-control" name="reply"
														class="rounded-area" rows="3" placeholder="Null">${dto.reply}</textarea>
												</div>
											</div>


											<input type="hidden" name="fuzzybyname"
												value="${fuzzybyname}"> <input type="hidden"
												name="searchById" value="${searchById}">


											<!-- /.card-body -->
											<div class="card-footer">
												<a
													href="GetAllAppointmentsServlet?fuzzybyname=${fuzzybyname}&searchById=${searchById}"><button
														type="button" class="btn btn-info">完成</button></a>
												<button type="button" class="btn btn-default float-right">
													清除</button>
											</div>
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
			<strong>Copyright &copy; 2014-2021 <a
				href="https://adminlte.io">AdminLTE.io</a>.
			</strong> All rights reserved.
		</footer>

		<!-- Control Sidebar -->
		<aside class="control-sidebar control-sidebar-dark">
			<!-- Control sidebar content goes here -->
		</aside>
		<!-- /.control-sidebar -->
	</div>
	<!-- ./wrapper -->

	<!-- jQuery -->
	<script
		src="${pageContext.request.contextPath}/plugins/jquery/jquery.min.js"></script>
	<!-- Bootstrap 4 -->
	<script
		src="${pageContext.request.contextPath}/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
	<!-- DataTables  & Plugins -->
	<script
		src="${pageContext.request.contextPath}/plugins/datatables/jquery.dataTables.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/jszip/jszip.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/pdfmake/pdfmake.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/pdfmake/vfs_fonts.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-buttons/js/buttons.html5.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-buttons/js/buttons.print.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/datatables-buttons/js/buttons.colVis.min.js"></script>
	<!-- AdminLTE App -->
	<script
		src="${pageContext.request.contextPath}/dist/js/adminlte.min.js"></script>
	<!-- AdminLTE for demo purposes
            <script src="../../dist/js/demo.js"></script> -->
	<!-- Page specific script -->


	<script>
		
	</script>
</body>

</html>