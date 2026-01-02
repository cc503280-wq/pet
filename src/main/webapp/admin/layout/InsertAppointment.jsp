<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ page import="java.util.List"%>
<!-- InsertAppointment.jsp -->
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>新增預約訂單</title>

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

<body class="hold-transition sidebar-mini sidebar-collapse">
	<!-- Site wrapper -->
	<div class="wrapper">
		<!-- Navbar -->
            <nav class="main-header navbar navbar-expand navbar-white navbar-light">
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
              <a href="../../AppointmentServlet.do" class="nav-link">
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
							<a href="AppointmentServlet.do"
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
										<h3 class="card-title">新增預約訂單</h3>
									</div>
									<!-- /.card-header -->
									<!-- form start -->

									<form class="form-horizontal"
										action="AppointmentServlet.do" method="post">
									<input type="hidden" name="action" value="insert">
										<div class="card-body">

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">會員姓名:</label>
												<div class="col-sm-10">


													<select class="form-control" name="memberId"
														id="memberName" onchange="reloadPageWithMember()">
														<option value="">--選擇預約會員--</option>
														<c:forEach var="member" items="${memberList}">
															<option value="${member.memberId}"
																${member.memberId == currentMemberId ? 'selected' : ''}>
																${member.name}</option>
														</c:forEach>
													</select>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">寵物:</label>
												<div class="col-sm-10">
													<select class="form-control" name="petId" id="petId">
														<c:choose>
															<c:when test="${empty petList}">
																<option value="">-- 請先選擇上方會員 --</option>
															</c:when>
															<c:otherwise>
																<option value="">--選擇預約寵物--</option>
																<c:forEach var="pet" items="${petList}">
																	<option value="${pet.petId}">${pet.petName}(${pet.petType})</option>
																</c:forEach>
															</c:otherwise>
														</c:choose>
													</select>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">服務項目:</label>
												<div class="col-sm-10">
													<select class="form-control" name="serviceId"
														id="serviceId" onchange="updateServiceInfo()">
														<option value="">--選擇服務項目--</option>
														<c:forEach var="service"
															items="${requestScope.serviceList}">
															<option value="${service.serviceId}"
																data-price="${service.price}"
																data-duration="${service.durationMinutes}">${service.serviceName}</option>
														</c:forEach>
													</select>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">服務時長(min) :</label>
												<div class="col-sm-10">
													<input class="form-control" type="text"
														name="durationMinutes" id="durationMinutes"
														value="${dto.durationMinutes}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">總價 :</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" name="totalPrice"
														id="totalPrice" value="${dto.price}" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">備註 :</label>
												<div class="col-sm-10">
													<textarea class="form-control" name="notes" rows="3"
														cols="33">${dto.notes}</textarea>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">選擇美容師:</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" id="employeeName"
														name="employeeName" readonly placeholder="請點擊下方按鈕選擇">
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">選擇時段:</label>
												<div class="col-sm-10">
													<input class="form-control" type="text" id="slot_display"
														readonly placeholder="請點擊下方按鈕選擇">
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label"></label>
												<div class="col-sm-10">
													<button class="form-control" id="btn-choose-employee"
														type="button" onclick="openSchedulePopup()">選擇美容師和時段</button>
												</div>
											</div>


											<input type="hidden" name="hiddenEmployeeId"
												id="hidden_emp_id"><input type="hidden" name="memberName" id="hiddenMemberName"> <input type="hidden"
												name="appointmentDate" id="hidden_date"> <input
												type="hidden" name="hidden_slot_id" id="hidden_slot_id">
											<input type="hidden" name="appointmentstartTime"
												id="hidden_starttime"> <input type="hidden"
												name="appointmentendTime" id="hidden_endtime"> <input
												type="hidden" name="hiddenPetName" id="hiddenPetName">
											<input type="hidden" name="hiddenServiceName"
												id="hiddenServiceName">

											<!-- /.card-body -->
											<div class="card-footer">
												<button type="submit" class="btn btn-info">送出</button>
												<button type="reset" class="btn btn-default float-right">
													清除</button>
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
		function reloadPageWithMember() {
			// 取得選中的 memberId
			let memberSelect = document.getElementById("memberName");
			let selectedId = memberSelect.value;
			if (selectedId) {
				window.location.href = "${pageContext.request.contextPath}/AppointmentServlet.do?action=toInsert&selectedMemberId=" + selectedId;
			}
		}

		function updateServiceInfo() {
	
			let select = document.getElementById("serviceId");
			let selectedOption = select.options[select.selectedIndex];


			let price = selectedOption.getAttribute("data-price");
			let duration = selectedOption.getAttribute("data-duration");

		
			if (price)
				document.getElementById("totalPrice").value = parseInt(price);
			if (duration)
				document.getElementById("durationMinutes").value = duration;
		}

		
		function openSchedulePopup() {
			
			let width = 1100;
			let height = 800;
			let left = (screen.width - width) / 2;
			let top = (screen.height - height) / 2;

			
			let url = "${pageContext.request.contextPath}/GetScheduleServlet.do"; 

			window.open(url, "SelectScheduleWindow", "width=" + width
					+ ",height=" + height + ",top=" + top + ",left=" + left
					+ ",scrollbars=yes");
		}

		
		window.receiveScheduleData = function(empId, empName, date, startTime,
				endTime, slotId) {
			console.log("父視窗收到資料:", empId, empName, date, startTime, endTime,
					slotId); 

			

			
			let enameInput = document.getElementById("employeeName");
			if (enameInput) {
				enameInput.value = empName;
			} else {
				console.error("找不到 ID 為 'ename' 的欄位");
			}

			
			let slotInput = document.getElementById("slot_display");
			if (slotInput) {
				slotInput.value = date + " " + startTime + " ~ " + endTime;
			} else {
				console.error("找不到 ID 為 'slot_display' 的欄位");
			}

			document.getElementById("hidden_emp_id").value = empId;
			document.getElementById("hidden_date").value = date;
			document.getElementById("hidden_starttime").value = startTime;
			document.getElementById("hidden_endtime").value = endTime;
			document.getElementById("hidden_slot_id").value = slotId;

		};

		document
				.querySelector('form')
				.addEventListener(
						'submit',
						function(e) {
							const memberSelect = document.getElementById('memberName');
							const petSelect = document.getElementById('petId');
							const serviceId = document
									.getElementById('serviceId');
							const empId = document
									.getElementById('employeeName');
							const slotId = document
									.getElementById('slot_display');

							let errorMessage = "";

							if (!petSelect.value) {
								errorMessage += "❌ 請選擇寵物\n";
							}

							if (!serviceId.value) {
								errorMessage += "❌ 請選擇服務項目\n";
							}

							if (!empId.value || !slotId.value) {
								errorMessage += "❌ 請點擊「選擇美容師和時段」按鈕並完成選擇\n";
							}

							if (errorMessage !== "") {

								e.preventDefault();
								alert(errorMessage);
								return;
							}

							if (memberSelect && memberSelect.selectedIndex !== -1) {						       
						        const text = memberSelect.options[memberSelect.selectedIndex].text;
						        document.getElementById('hiddenMemberName').value = text;						      
						    }

							if (petSelect.selectedIndex !== -1) {
								const text = petSelect.options[petSelect.selectedIndex].text;
								document.getElementById('hiddenPetName').value = text;
								console
										.log("hiddenPetName: "
												+ document
														.getElementById('hiddenPetName').value)
							}

							if (serviceId.selectedIndex !== -1) {
								const text = serviceId.options[serviceId.selectedIndex].text;
								document.getElementById('hiddenServiceName').value = text
										.trim();
								console
										.log("ServiceName: "
												+ document
														.getElementById('hiddenServiceName').value)
							}

						});
	</script>
</body>
</html>