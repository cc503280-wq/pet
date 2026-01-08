<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>CouponUpdate.jsp</title>

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
            <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
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
			            <a href="../../AdminLogoutServlet" class="btn btn-secondary btn-sm">
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
                                    <h1>會員管理</h1>
                                    <a href="CouponQueryAll.html" class="btn btn-secondary btn-sm">
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
                                                <h3 class="card-title">修改優惠券資料</h3>
                                            </div>
                                            <!-- /.card-header -->
                                            <!-- form start -->
                                    <form class="form-horizontal" id="updateForm" enctype="multipart/form-data">
                                    	<input type="hidden" id="couponId" name="couponId"> 
                                        <div class="card-body">
                                            <div class="form-group row">
                                                <label for="code" class="col-sm-2 col-form-label">折扣碼:</label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="code" autocomplete="off">
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label class="col-sm-2 col-form-label">折扣類型:</label>
                                                <div class="col-sm-10">
                                                    <select class="form-control select2" style="width: 100%;" id="discountType">
                                                        <option value="amount" >amount</option>
                                                        <option value="percent" selected>percent</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label for="discountValue" class="col-sm-2 col-form-label">折扣金額/百分比:</label>
                                                <div class="col-sm-10">
                                                    <input type="number" step="0.05" min="0" max="1" class="form-control" id="discountValue"
                                                         placeholder="請輸入 0.0 ~ 1.0（例如六折=0.6）" autocomplete="off">
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label class="col-sm-2 col-form-label">是否限量:</label>
                                                <div class="col-sm-10">
                                                    <select class="form-control select2" style="width: 100%;" id="isLimited">
                                                        <option value="0" selected>0:不限量</option>
                                                        <option value="1">1:限量</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label for="totalAmount" class="col-sm-2 col-form-label">總發放量:</label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="totalAmount"
                                                         autocomplete="off">
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label for="totalAmount" class="col-sm-2 col-form-label">已發放數量:</label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="issuedAmount"
                                                         autocomplete="off">
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <label for="minPurchase" class="col-sm-2 col-form-label">使用門檻金額:</label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="minPurchase"
                                                         autocomplete="off">
                                                </div>
                                            </div>
                                            <!-- 發放區間 -->
											<div class="form-group row">
											    <label class="col-sm-2 col-form-label">發放區間:</label>
											    <div class="col-sm-10">
											        <input type="text" class="form-control" id="issueRange" placeholder="選擇發放區間" autocomplete="off">
											        <input type="hidden" id="issueStartAt" name="issueStartAt">
											        <input type="hidden" id="issueEndAt" name="issueEndAt">
											    </div>
											</div>
											
											<!-- 使用區間 -->
											<div class="form-group row">
											    <label class="col-sm-2 col-form-label">使用區間:</label>
											    <div class="col-sm-10">
											        <input type="text" class="form-control" id="useRange" placeholder="選擇使用區間" autocomplete="off">
											        <input type="hidden" id="useStartAt" name="useStartAt">
											        <input type="hidden" id="useEndAt" name="useEndAt">
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
            <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/moment@2.29.4/moment.min.js"></script>
			<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
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
                    
                  //限量控管
                    function toggleQuantityInputs() {
                        if ($('#isLimited').val() === "0") {
                            $('#totalAmount').val('').prop('disabled', true);
                            $('#issuedAmount').val('').prop('disabled', true);
                        } else {
                            $('#totalAmount').prop('disabled', false);
                            $('#issuedAmount').prop('disabled', false);
                        }
                    }

                    toggleQuantityInputs();
                    $('#isLimited').change(toggleQuantityInputs);
                    
                 // 發放區間
                    $('#issueRange').daterangepicker({
                        autoUpdateInput: false,
                        locale: {
                            format: 'YYYY/MM/DD',
                            cancelLabel: '清除'
                        }
                    });

                    $('#issueRange').on('apply.daterangepicker', function(ev, picker) {
                        $(this).val(picker.startDate.format('YYYY/MM/DD') + ' - ' + picker.endDate.format('YYYY/MM/DD'));
                        $('#issueStartAt').val(picker.startDate.format('YYYY/MM/DD'));
                        $('#issueEndAt').val(picker.endDate.format('YYYY/MM/DD'));
                    });

                    $('#issueRange').on('cancel.daterangepicker', function(ev, picker) {
                        $(this).val('');
                        $('#issueStartAt').val('');
                        $('#issueEndAt').val('');
                    });

                    // 使用區間
                    $('#useRange').daterangepicker({
                        autoUpdateInput: false,
                        locale: {
                            format: 'YYYY/MM/DD',
                            cancelLabel: '清除'
                        }
                    });

                    $('#useRange').on('apply.daterangepicker', function(ev, picker) {
                        $(this).val(picker.startDate.format('YYYY/MM/DD') + ' - ' + picker.endDate.format('YYYY/MM/DD'));
                        $('#useStartAt').val(picker.startDate.format('YYYY/MM/DD'));
                        $('#useEndAt').val(picker.endDate.format('YYYY/MM/DD'));
                    });

                    $('#useRange').on('cancel.daterangepicker', function(ev, picker) {
                        $(this).val('');
                        $('#useStartAt').val('');
                        $('#useEndAt').val('');
                    });
                 	
                    $('#discountType').change(function () {
                        const type = $(this).val();
                        const input = $('#discountValue');

                        if (type === 'percent') {
                            // 百分比折扣：使用 0.0~1.0
                            input.attr({
                                'type': 'number',
                                'min': '0',
                                'max': '1',
                                'step': '0.05',
                                'placeholder': '請輸入 0.0 ~ 1.0（例如六折=0.6）'
                            });
                            input.val(''); // 清空，避免舊值不符合
                        } else {
                            // 金額折扣：不可 <0，無 max
                            input.attr({
                                'type': 'number',
                                'min': '0',
                                'max': null,
                                'step': '10',
                                'placeholder': '請輸入折抵金額（例如 100）'
                            });
                            input.val('');
                        }
                    });
                    
                    function validateCouponFront(discountType, discountValue, minPurchase, issueStartAt, useStartAt) {

                        // ---- 折扣金額 vs 最低消費 ----
                        if (discountType === 'amount') {
						    const discountNum = parseFloat(discountValue);
						    const minPurchaseNum = parseFloat(minPurchase);
						    if (!isNaN(minPurchaseNum) && discountNum > minPurchaseNum) {
						        return "折抵金額不可大於最低消費金額";
						    }
						}

                        // ---- 日期驗證：使用日不能早於發放日 ----
                        if (issueStartAt && useStartAt) {
                            const issueStart = new Date(issueStartAt);
                            const useStart = new Date(useStartAt);

                            if (useStart < issueStart) {
                                return "使用開始日期不能早於發放開始日期";
                            }
                        }

                        return null; // 驗證通過
                    }

                    // 從URL拿couponId
                    const urlParams = new URLSearchParams(window.location.search);
                    const couponId = urlParams.get('couponId');
                    $('#couponId').val(couponId);
                    console.log(couponId);
                    console.log($('#couponId').val());

                    // 載入優惠券資料
                    $.ajax({
                        url: '/pet/CouponServlet',
                        method: 'GET',
                        data: { action: 'queryById', couponId: couponId },
                        dataType: 'json',
                        success: function (coupon) {
                            $('#code').val(coupon.code);
                            $('#discountType').val(coupon.discountType).trigger('change');
                            $('#discountValue').val(coupon.discountValue);
                            $('#isLimited').val(coupon.isLimited).trigger('change');
                            $('#totalAmount').val(coupon.totalAmount);
                            $('#issuedAmount').val(coupon.issuedAmount);
                            $('#minPurchase').val(coupon.minPurchase);
                            $('#issueStartAt').val(coupon.issueStartAt);
                            $('#issueEndAt').val(coupon.issueEndAt);
                            $('#issueRange').val(coupon.issueStartAt + ' - ' + coupon.issueEndAt);
                            $('#useStartAt').val(coupon.useStartAt);
                            $('#useEndAt').val(coupon.useEndAt);
                            $('#useRange').val(coupon.useStartAt + ' - ' + coupon.useEndAt);
                        },
                        error: function () {
                            alert('載入資料失敗');
                        }
                    });


                    // 送出更新
                    $('#updateForm').submit(function (e) {
                        e.preventDefault();
                    	
                     // 取得優惠券表單欄位值
                        const code = $('#code').val().trim();
                        const discountType = $('#discountType').val().trim();
                        const discountValue = $('#discountValue').val().trim();
                        const isLimited = $('#isLimited').val();
                        const totalAmount = $('#totalAmount').val().trim();
                        const issuedAmount = $('#issuedAmount').val().trim();
                        const minPurchase = $('#minPurchase').val().trim();

                        // 取得發放/使用區間拆分後的日期
                        const issueStartAt = $('#issueStartAt').val();
                        const issueEndAt = $('#issueEndAt').val();
                        const useStartAt = $('#useStartAt').val();
                        const useEndAt = $('#useEndAt').val();
        				
                        //呼叫驗證
                        const err = validateCouponFront(discountType, discountValue, minPurchase, issueStartAt, useStartAt);

                        if (err) {
                            alert(err);
                            return; // 中止送出
                        }

                        $.ajax({
                            url: "/pet/CouponServlet?action=update",
                            method: "POST",
                            data: {
                                couponId: $('#couponId').val(),
                                code: code,
                                discountType: discountType,
                                discountValue: discountValue,
                                isLimited: isLimited,
                                totalAmount: totalAmount,
                                issuedAmount: issuedAmount,
                                minPurchase: minPurchase,
                                issueStartAt: issueStartAt,
                                issueEndAt: issueEndAt,
                                useStartAt: useStartAt,
                                useEndAt: useEndAt
                            },
                            success: function (response) {
                                console.log("更新成功");
                                console.log(response);
                                if (response && response.couponId) {
                                    // 導向完成頁面
                                    var params = new URLSearchParams(response).toString();
                                    window.location.href = 'CouponUpdateDone.jsp?' + params;
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