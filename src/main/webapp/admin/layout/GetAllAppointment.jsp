<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*,com.pet.model.appointment.GetAllAppointmentDTO"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%-- GetAllAppointment.jsp --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>查詢所有預約訂單</title>

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

/* 新增以下樣式針對表格調整 */
#example2 th, #example2 td {
	white-space: nowrap;
	vertical-align: middle;
	font-size: 14px;
	padding: 0.5rem;
}

/* 針對操作按鈕欄位，確保按鈕排成一行 */
#example2 .btn-group, #example2 td button, #example2 td a.btn {
	margin: 0 2px;
}
</style>
</head>
<body class="hold-transition sidebar-mini sidebar-collapse">
	<!-- Site wrapper -->
	<div class="wrapper">
		<!-- Navbar -->
		<nav
			class="main-header navbar navbar-expand navbar-white navbar-light">
			<!-- Left navbar links -->
			<ul class="navbar-nav ml-auto">
			        <li class="nav-item">
			            <a href="${pageContext.request.contextPath}/admin/layout/Login.html" class="btn btn-secondary btn-sm">
			                <i class="fas fa-sign-out-alt"></i> 登出
			            </a>
			        </li>
			    </ul>


		</nav>
		<!-- /.navbar -->

		<!-- Main Sidebar Container -->
		<aside class="main-sidebar sidebar-dark-primary elevation-4">
			<!-- Brand Logo -->
			<a href="../../index3.html" class="brand-link"> <i
				class="fas fa-paw" style="color: #FFD43B; font-size: 1.5rem;"></i> <span
				class="brand-text font-weight-light"></span>
			</a>

			<!-- Sidebar -->
			<div class="sidebar">
				<div class="user-panel mt-3 pb-3 mb-3 d-flex"></div>



				<!-- Sidebar Menu -->
				<nav class="mt-2">
					<ul class="nav nav-pills nav-sidebar flex-column"
						data-widget="treeview" role="menu" data-accordion="false">
						<!-- Add icons to the links using the .nav-icon class
               with font-awesome or any other icon font library -->
						<li class="nav-item"><a href="#" class="nav-link"> <i
								class="nav-icon fas fa-edit"></i>
								<p>
									會員管理 <i class="right fas fa-angle-left"></i>
								</p>
						</a>
							<ul class="nav nav-treeview">
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/AdminQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>管理員資料</p>
                  </a>
                </li>
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/MemberQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>會員基本資料</p>
                  </a>
                </li>
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/MemberPetQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>會員寵物資料</p>
                  </a>
                </li>
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/CouponQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>優惠券總覽</p>
                  </a>
                </li>
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/CouponUsersQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>會員持有優惠券</p>
                  </a>
                </li>
                <li class="nav-item">
                  <a href="${pageContext.request.contextPath}/admin/layout/FavoritesQueryAll.html" class="nav-link">
                    <i class="far fa-circle nav-icon"></i>
                    <p>會員收藏清單</p>
                  </a>
                </li>
              </ul></li>
						<li class="nav-item">
			              <a href="${pageContext.request.contextPath}/AllProducts" class="nav-link">
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
			              <a href="${pageContext.request.contextPath}/GetAllAppointmentsServlet" class="nav-link">
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
							<a href="#" class="btn btn-secondary btn-sm"> 回列表 </a>
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
								<div class="card-header d-flex align-items-center">

									<h3 class="card-title">預約訂單列表</h3>

									<div class="d-flex align-items-center ml-auto">
										<a href="InsertAppointmentServlet"
											class="btn btn-secondary btn-sm mr-2"> 新增資料 </a> <a
											href="GetAllAppointmentsServlet"
											class="btn btn-secondary btn-sm mr-2"> 查詢全部 </a>

										<form action="GetAllAppointmentsServlet" method="get"
											class="mr-2" style="display: flex;">
											<div class="input-group input-group-sm" style="width: 150px;">
												<input id="idSearchInput" type="text" class="form-control"
													name="searchById" placeholder="依 ID 查詢"
													value="${searchById}">
												<div class="input-group-append">
													<button type="submit" class="btn btn-secondary">
														<i class="fas fa-search"></i>
													</button>
												</div>
											</div>
										</form>

										<form action="GetAllAppointmentsServlet" method="get"
											style="display: flex;" class="mr-2">
											<div class="input-group input-group-sm" style="width: 150px;">
												<input id="fuzzySearchInput" type="text"
													class="form-control" name="fuzzybyname" placeholder="模糊查詢"
													value="${fuzzybyname}">

												<div class="input-group-append">
													<button type="submit" class="btn btn-secondary">
														<i class="fas fa-search"></i>
													</button>
												</div>
											</div>
										</form>

									</div>
								</div>
								<!-- /.card-header -->
								<div class="card-body">
									<table id="example2"
										class="table table-bordered table-hover table-sm">
										<thead>
											<tr>
												<th>訂單編號
												<th>會員姓名
												<th>寵物姓名
												<th>預約服務
												<th>員工姓名
												<th>預約日期
												<th>預約開始時段
												<th>預約結束時段
												<th>備註
												<th>價格
												<th>預約訂單狀態
												<th>付款狀態
												<th>評分
												<th>評價
												<th>回覆
												<th class="text-center">變更</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="app" items="${apps}">
												<tr>
													<td>${app.appointmentId}
													<td><a href="#${app.memberId}">${app.memberName}</a>
													<td><a href="#${app.petId}">${app.petName}</a>
													<td>${app.serviceName}
													<td><ahref="#">${app.employeeName}</a></td>
													<td>${app.appointmentDate}
													<td>${app.startTime}
													<td>${app.endTime}
													<td>${app.notes}
													<td>${app.price}
													<td>${app.appointmentStatus}
													<td>${app.payStatus}
													<td>${app.rating}
													<td>${app.comment}
													<td>${app.reply}
													<td class="text-center">
														<a href="UpdateAppointmentServlet?appointmentId=${app.appointmentId}&fuzzybyname=${fuzzybyname}&searchById=${searchById}&from=GetAllAppointment"
       														class="btn btn-secondary btn-sm mr-1">修改</a>
														<a href="DeleteAppointmentServlet?appointmentId=${app.appointmentId}&type=soft&fuzzybyname=${fuzzybyname}&searchById=${searchById}"
      														class="btn btn-secondary btn-sm mr-1">停用</a>
														<a href="DeleteAppointmentServlet?appointmentId=${app.appointmentId}&type=hard&fuzzybyname=${fuzzybyname}&searchById=${searchById}"
       														class="btn btn-secondary btn-sm mr-1">刪除</a></td>
											</c:forEach>
										</tbody>

									</table>

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
		$(function() {

			$('#example2')
					.DataTable(
							{
								"paging" : true,
								"lengthChange" : false, // 關閉每頁筆數選項
								"searching" : false, // 關閉搜索框
								"ordering" : true,
								"info" : true, // 顯示資訊文字
								"autoWidth" : true,
								"responsive" : true,
								"pageLength" : 20,

								// **【關鍵修正】使用 dom 屬性強制分頁 (p) 靠右 (col-md-7)**
								// 結構: 表格本體(t) / 新的一行: [資訊(i) - 左 5 欄] [分頁(p) - 右 7 欄]
								"dom" : '<"row"<"col-sm-12"t>><"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7 d-flex justify-content-end"p>>'
							});
		});
	</script>
</body>

</html>