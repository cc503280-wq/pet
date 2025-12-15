<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="bean.orderItemBean"%>
<!--bean需要更換-->
<%@ page import="javax.sql.*"%>
<%@ page import="javax.naming.Context"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>insert.html</title>

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
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/select2/css/select2.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css">
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
							<h1>xx管理</h1>
							<a href="link" class="btn btn-secondary btn-sm"> 回列表 </a>
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
										<h3 class="card-title">新增訂單</h3>
									</div>
									<!-- /.card-header -->
									<!-- form start -->
									<form class="form-horizontal" method="post" action="${pageContext.request.contextPath}/insertOrder">
										<div class="card-body">
											<%
											double totalPrice = 0;
											List<orderItemBean> items = (List<orderItemBean>) request.getAttribute("items");
											if (items != null) {
												for (int i = 0; i < items.size(); i++) {
													orderItemBean item = items.get(i);
												
													totalPrice += item.getSubtotal();
													
											%>
											<!-- 商品編號 -->
											<div class="form-group row">
												<label class="col-sm-2 col-form-label">商品編號</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" name="productId[]"
														value="<%=item.getProductId()%>" readonly>
												</div>
											</div>

											<!-- 商品名稱 -->
											<div class="form-group row">
												<label class="col-sm-2 col-form-label">商品名稱</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														name="productName[]" value="<%=item.getProductName()%>"
														readonly>
												</div>
											</div>

											<!-- 數量 -->
											<div class="form-group row">
												<label class="col-sm-2 col-form-label">數量</label>
												<div class="col-sm-10">
													<input type="number" class="form-control" name="quantity[]"
														value="<%=item.getQuantity()%>" min="1" readonly>
												</div>
											</div>
											<div class="form-group row">
												<label class="col-sm-2 col-form-label">商品單價</label>
												<div class="col-sm-10">
													<input type="number" class="form-control" name="price[]"
														value="<%=item.getUnitPrice()%>" readonly>
												</div>
											</div>
											
											<hr>
											<%
											}
											}
											%>
											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">目前總金額</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="total_price" name="total_price"
														value="<%=String.format("%.2f", totalPrice)%>" readonly>

												</div>
											</div>

										</div>

										<div class="card-body">
											<div class="form-group row">
												<label class="col-sm-2 col-form-label">會員:</label>
												<div class="col-sm-10">
													<select class="form-control" id="member_id"
														name="member_id" style="width: 100%;">
														<option value="">請選擇會員</option>
													</select>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">會員編號</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="id" readonly>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">優惠券:</label>
												<div class="col-sm-10">
													<select class="form-control select2" id="couponSelect"
														style="width: 100%;" name="coupon_id">
														<option value="0" data-type="" data-value="0" >目前無優惠券</option>
													</select>
												</div>
											</div>

											<div class="form-group row">
												<label class="col-sm-2 col-form-label">優惠價格:</label>
												<div class="col-sm-10">
													<input type="number" class="form-control"
														name="discountPrice" value="" id="discountPrice" readonly>
												</div>
											</div>
											<hr>





											<div class="form-group row">
												<label class="col-sm-2 col-form-label">物流方式:</label>
												<div class="col-sm-10">
													<select class="form-control select2" id="method"
														style="width: 100%;" name="method">
														<option value="0" selected="selected">請選擇物流方式</option>
														<option value="7-11">7-11領貨</option>
														<option value="family">全家領貨</option>
														<option value="blackCat">黑貓宅配</option>
													</select>
												</div>
											</div>
											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">運費</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="fee" value=""
														readonly name="fee">
												</div>
											</div>
											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">最後總金額</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="finalAmount" value=""
														readonly name="finalAmount">
												</div>
											</div>



											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">收貨人姓名</label>
												<div class="col-sm-10">
													<input type="text" class="form-control" id="recipinet_name"
														placeholder="收貨人姓名" name="recipientName">
												</div>
											</div>
											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">收貨人電話</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														id="recipinet_phone" placeholder="收貨人電話" name="recipientPhone">
												</div>
											</div>
											<div class="form-group row">
												<label for="inputPassword3" class="col-sm-2 col-form-label">收貨人地址(超商地址)</label>
												<div class="col-sm-10">
													<input type="text" class="form-control"
														id="recipinet_address" placeholder="地址" name="shippingAddress">
												</div>
											</div>





										</div>
										<!-- /.card-body -->
										<div class="card-footer">
											<button type="submit" class="btn btn-info">送出</button>
											<button type="remove" class="btn btn-default float-right">清除</button>
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
	<!-- DataTables & Plugins -->
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
	<script
		src="${pageContext.request.contextPath}/plugins/moment/moment.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/tempusdominus-bootstrap-4/js/tempusdominus-bootstrap-4.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/plugins/select2/js/select2.full.min.js"></script>
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

								// **【關鍵修正】使用 dom 屬性強制分頁 (p) 靠右 (col-md-7)**
								// 結構: 表格本體(t) / 新的一行: [資訊(i) - 左 5 欄] [分頁(p) - 右 7 欄]
								"dom" : '<"row"<"col-sm-12"t>><"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7 d-flex justify-content-end"p>>'
							});

			// 啟用 DatePicker (日期時間選擇器)
			$('#reservationdate').datetimepicker({
				format : 'L' // 這裡使用 'L' 代表當地短日期格式，例如 2025/11/29
			});
			
			function updateFee() {
				let method = $('#method').val();
				let fee = 0;
				
				if (method === '7-11')
					fee = 45;
				else if (method === 'family')
					fee = 50;
				else if (method === 'blackCat')
					fee = 60;
				$('#fee').val(fee);
				finalAmount()
				
				
				
				
				
			}
			updateFee();
			$('#method').on('change', function() {
				updateFee();
			});
			
			function finalAmount(){
				let discountPrice=parseFloat($("#discountPrice").val());
				let fee=parseFloat($("#fee").val());
				$('#finalAmount').val(fee+discountPrice);
			}
			$("#discountPrice").val($("#total_price").val());
			finalAmount()
			
			memberList = [];

			// AJAX 取得會員
			$.ajax({
			    url: "${pageContext.request.contextPath}/memberServletOrder",
			    type: "GET",
			    dataType: "json",
			    success: function(res) {
			    	memberList = res; // 存下來
			       	let $member = $("#member_id");
			        $member.empty(); // 先清空下拉選單
			        $member.append('<option value="">請選擇會員</option>');

			        res.forEach(function(member){
			        	$member.append(
			            		'<option value="' + member.memberId + '">' + member.name + '</option>'
			            );
			            $member.trigger('change');
			            
			        });
			    },
			    error: function() {
			        alert("載入會員失敗");
			    }
			});

			// 選會員自動帶入會員編號
			$("#member_id").change(function() {
			    const selectedId = $(this).val();
			    const member = memberList.find(m => m.memberId == selectedId);
			    $("#id").val(member ? member.memberId : "");
			});
			
			//選會員自動帶入優惠券
			$("#member_id").change(function() {
			    let memberId = $(this).val();
			    if(!memberId){
			        $("#couponSelect").html('<option value="0">目前無優惠券</option>');
			        return;
			    }

			    $.ajax({
			        url: "${pageContext.request.contextPath}/couponServletOrder",
			        type: "GET",
			        data: { memberId: memberId },
			        dataType: "json",
			        success: function(data){
			            let $coupon = $("#couponSelect");
			            $coupon.empty();
			            $coupon.append('<option value="0" data-type="" data-value="0" name="">目前無優惠券</option>');

			            data.forEach(item => {
			                let $option = $('<option></option>')
			                    .val(item.couponId)
			                    .attr('data-type', item.discountType)
			                    .attr('data-value', item.discountValue)
			                    .attr('data-min', item.minPurchase);

			                if (item.discountType == 'amount') {
			                    $option.text(item.discountValue + '元折扣');
			                } else {
			                    let percentOff = Math.round((1 - item.discountValue) * 100);
			                    $option.text(percentOff + '%折扣');
			                }

			                $coupon.append($option);
			            });
			        }
			    });
			});
			
			$("#couponSelect").on("change", function() {
				let totalPrice = parseFloat($("#total_price").val())
				let $selected = $(this).find("option:selected");
				let type = $selected.data("type");
				let value = parseFloat($selected.data("value"));
				let minPurchase = parseFloat($selected.data("min"));
				console.log(totalPrice);
				console.log($selected);
				console.log(type);
				console.log(value);
				console.log(minPurchase);
				if(!type|| totalPrice < minPurchase){
					$("#discountPrice").val(totalPrice);
					finalAmount()
			        return;
				}
				let discountedPrice = totalPrice;

			    if (type == "amount") {
			        discountedPrice = totalPrice - value;
			    } else if (type == "percent") {
			        discountedPrice = totalPrice * value; // value 例如 0.9
			    }

			    // 不允許負數
			    if (discountedPrice < 0) discountedPrice = 0;

			    $("#discountPrice").val(discountedPrice.toFixed(0)); // 四捨五入到整數
			    finalAmount()
		
		    });
			
			
			
			

		});
	</script>
</body>

</html>