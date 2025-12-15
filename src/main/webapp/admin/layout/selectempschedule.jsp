<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<!-- selectempschedule.jsp -->
<html>
<head>
<meta charset="UTF-8">
<title>選擇美容師與預約時段</title>
<link href="https://unpkg.com/tabulator-tables/dist/css/tabulator.min.css" rel="stylesheet">
<script type="text/javascript" src="https://unpkg.com/tabulator-tables/dist/js/tabulator.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<style>
/* (可選) 點擊選中和狀態樣式，讓 Tabulator 表格更美觀 */
.available {
    background-color: #e8f5e9 !important; /* 淺綠色 */
    color: #1b5e20;
    font-weight: 500;
}

.booked {
    background-color: #ffebee !important; /* 淺紅色 */
    color: #c62828;
    cursor: not-allowed;
}

.unavailable {
    background-color: #f9f9f9 !important; /* 灰色 */
    color: #777;
    cursor: not-allowed;
}

.selected {
    background-color: #007bff !important;
    color: white !important;
    border: 2px solid #0056b3;
    font-weight: bold;
}

.nav-btn {
    padding: 5px 10px;
    font-size: 18px;
    cursor: pointer;
    border: 1px solid #ccc;
    background-color: #f9f9f9;
    border-radius: 4px;
}

.original-slot {
    background-color: #e3f2fd !important; /* 淺藍色 */
    color: #0d47a1;
    font-weight: bold;
    border: 2px dashed #2196f3; /* 虛線邊框 */
    cursor: pointer;
}

.original-slot.selected {
    background-color: #ffeb3b !important; /* 亮黃色背景 */
    color: #000000 !important;          /* 黑色文字 (在黃底上比較清楚) */
    border: 3px solid #fbc02d !important; /* 深黃色實心邊框 */
    font-weight: bold;
    box-shadow: 0 0 5px rgba(255, 193, 7, 0.5); /* 加一點發光效果讓它更明顯 */
}
</style>
</head>
<body>

    <div id="booking-container" style="max-width: 1000px; margin: 30px auto;">
        <h2>選擇美容師與預約時段</h2>

        <div id="employee-selection" style="margin-bottom: 20px;">
            <label for="employeeSelect" style="font-size: 18px;">選擇美容師:</label> 
            <select id="employeeSelect" name="employeeId" style="padding: 5px; margin-right: 10px;">
                <option value="">-- 請選擇 --</option>    
                <!-- GetScheduleServlet doGet傳emps值到selectempschedule.jsp -->          
                <c:forEach var="emp" items="${emps}">
                    <option value="${emp.employeeId}">${emp.ename}</option>
                </c:forEach>
            </select>
        </div>

        <div id="schedule-display">
            <div id="date-navigation" style="display: flex; justify-content: center; align-items: center; margin-bottom: 10px;">
                <button id="prev-week-btn" class="nav-btn">&larr;</button>
                <span id="current-date-range" style="font-weight: bold; margin: 0 15px;">---</span>
                <button id="next-week-btn" class="nav-btn">&rarr;</button>
            </div>

            <div id="schedule-table-tabulator" style="border: 1px solid #ccc;"></div> <!-- tabulator 自動生成 -->

            <div id="confirmation-area" style="margin-top: 20px; text-align: center; border-top: 1px solid #ccc; padding-top: 15px;">
                <p>
                    已選定時段: <span id="selected-slot-info" style="font-weight: bold; color: #007bff;">尚未選擇</span>
                </p>
                <button id="confirm-selection-btn" disabled
                    style="padding: 10px 20px; font-size: 16px; background-color: #4CAF50; color: white; border: none; cursor: pointer; border-radius: 5px;">確認送出資料</button>
            </div>
        </div>
    </div>

    <script>
  const REAL_TODAY_DATE = new Date();
  const REAL_TODAY_STR = formatDate(REAL_TODAY_DATE);

  function formatDate(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return y + "-" + m + "-" + d;
  }

  
  function dateFromString(str) {
    if (!/^\d{4}-\d{2}-\d{2}$/.test(str)) return new Date(NaN);
    const parts = str.split("-").map(Number);
    return new Date(parts[0], parts[1] - 1, parts[2]);
  }

  
  function getTodayDate() {
    return REAL_TODAY_STR; 
  }

 
  function getNewDate(days, baseDateString) {
    const date = dateFromString(baseDateString);
    if (isNaN(date)) return baseDateString;
    date.setDate(date.getDate() + days);
    return formatDate(date);
  }
	
 
  $(document).ready(function () {
    let table = null;
    let currentStartDate = "${AppointDate}"; 
    console.log("網頁載入時時間欄位顯示的第一格日期:", currentStartDate);
    let selectedSlotData = null;
    let selectedCellComponent = null;

    const employeeSelect = document.getElementById("employeeSelect"); 
    const currentDateRangeSpan = document.getElementById("current-date-range");
    const selectedSlotInfo = document.getElementById("selected-slot-info"); 
    const confirmBtn = document.getElementById("confirm-selection-btn"); 
    

    function getURLParameter(name) {
        return new URLSearchParams(window.location.search).get(name);
    }

      const slotFormatter = function (cell) { 
      const value = cell.getValue();
      const cellElement = cell.getElement();
      
      cellElement.classList.remove("selected", "available", "booked", "unavailable"); 
	

      if (value === "可預約") {
        cellElement.classList.add("available");
        cellElement.style.cursor = "pointer";
      } else if (value === "已預約") { 
        cellElement.classList.add("booked");
      } else if (value === "您的預約時段") {  
        cellElement.classList.add("original-slot");
      } else {
        cellElement.classList.add("unavailable");
      }

      if (selectedCellComponent && selectedCellComponent === cell) {
        cellElement.classList.add("selected");
      }
      return value;
    };


    function updateNavButtonsState() {
      if (currentStartDate <= REAL_TODAY_STR) { 
        $("#prev-week-btn").prop("disabled", true).css({"opacity": "0.5", "cursor": "not-allowed"});
      } else {
        $("#prev-week-btn").prop("disabled", false).css({"opacity": "1", "cursor": "pointer"});
      }
    }


    function getColumnDefinitions(startDate) {
    	const columns = [
        { title: "美容師", field: "employeeName", width: 100, frozen: true, hozAlign: "center" },
        { title: "開始時間", field: "startTime", width: 80, frozen: true, hozAlign: "center" },
        { title: "結束時間", field: "endTime", width: 80, frozen: true, hozAlign: "center" },
        { field: "employeeId", visible: false },
      ];


      
      const start = dateFromString(startDate); 
      for (let i = 0; i < 7; i++) {
        const date = new Date(start); 
        date.setDate(start.getDate() + i);
        const fullDate = formatDate(date);  
        const dayNames = ["日", "一", "二", "三", "四", "五", "六"];
        const dayName = dayNames[date.getDay()];

        columns.push({
          title: fullDate + "<br><small>" + dayName + "</small>",
          field: fullDate,
          formatter: slotFormatter, 
          hozAlign: "center",
          headerHozAlign: "center",
          minWidth: 100,
          cellClick: handleCellClick
        });
      }
		
      const endLoopDate = new Date(start);
      endLoopDate.setDate(start.getDate() + 6);
      currentDateRangeSpan.textContent = startDate + " ~ " + formatDate(endLoopDate);
      return columns;
    }

    function initializeTable(employeeId, startDate) {
      updateNavButtonsState();
      selectedSlotData = null;
      
      if (selectedCellComponent) {
         try { selectedCellComponent.getElement().classList.remove("selected"); } catch(e){}
         selectedCellComponent = null;
      }
      selectedSlotInfo.textContent = "尚未選擇";
      confirmBtn.disabled = true;
	
      if (!employeeId) {
        if(table) table.clearData();
        currentDateRangeSpan.textContent = "請選擇美容師";
        return;
      }
      const columns = getColumnDefinitions(startDate); 
	  
      
      if (table === null) { 
        table = new Tabulator("#schedule-table-tabulator", {
          height: "auto",
          layout: "fitColumns",
          columns: columns,
          data: [],
          placeholder: "載入中...",
          headerSort: false,
        });
      } 
      else {
        table.setColumns(columns);
        table.clearData();
      }
      fetchScheduleData(employeeId, startDate);
    }

   
    function handleCellClick(e, cell) { 
      const rowData = cell.getRow().getData();
      const columnField = cell.getColumn().getField(); 
      const status = cell.getValue();
      
      console.log("點擊cell，取得rowData: ", rowData);
      console.log("點擊cell，取得日期資料:"+columnField);
      console.log("點擊cell，取得cell資料:"+status);
      
      if (!columnField.match(/^\d{4}-\d{2}-\d{2}$/g) || (status !== "可預約" && status !== "您的預約時段")) {
          return; 
      }
	  
      if (selectedCellComponent) {
        selectedCellComponent.getElement().classList.remove("selected");
      }
	  
 	  	
	  
      if (selectedCellComponent === cell) {
        selectedCellComponent = null;
        selectedSlotData = null;
        selectedSlotInfo.textContent = "尚未選擇";
        confirmBtn.disabled = true;
      }  
  
      else {
        cell.getElement().classList.add("selected");
        selectedCellComponent = cell;
        
        const selectedEmployeeName = employeeSelect.options[employeeSelect.selectedIndex].text;
        selectedSlotData = {
          employeeId: rowData.employeeId,
          employeeName: selectedEmployeeName,
          slotDate: columnField,
          startTime: rowData.startTime,
          endTime: rowData.endTime,
          realSlotId: rowData.slotId
        };
        
       
        selectedSlotInfo.textContent = selectedEmployeeName + ", " + columnField + " " + rowData.startTime + "~" + rowData.endTime;
        
        confirmBtn.disabled = false;
      }
    }

   
    function fetchScheduleData(employeeId, startDate) {
      if (!employeeId) return;
	 
      const API_URL = "${pageContext.request.contextPath}/GetScheduleServlet"; 

      $.ajax({
        url: API_URL,
        type: "POST",
        data: {
          employeeId: employeeId,
          startDate: startDate
        },
        dataType: "json",
        success: function(response) {
        	console.log("後端原始資料:", response);
        	
        
            const targetEmpId = getURLParameter("AppointEmployee");
            const targetDate = getURLParameter("AppointDate"); 
            const targetStartTime = getURLParameter("AppointStartTime");
            const targetEndTime = getURLParameter("AppointEndTime"); 
            
            
            const dbEmpId = getURLParameter("DbEmp"); 
            const dbDate = getURLParameter("DbDate");
            const dbStartTime = getURLParameter("DbStartTime");
            const dbEndTime = getURLParameter("DbEndTime");

	            response.forEach(function(row) {
	                
	                
	               
	                if (targetEmpId && targetDate && targetStartTime) {
	                    let targetTimeShort = targetStartTime.substring(0, 5); 
	                    let rowTimeShort = row.startTime.substring(0, 5);
	
	                    if (row.employeeId == targetEmpId && rowTimeShort === targetTimeShort) {
	                        if (row[targetDate] === "已預約" || row[targetDate] === "可預約") {
	                            row[targetDate] = "您的預約時段";
	                        }
	                    }
	                }
	            
	             
	               
	                if (dbEmpId && dbDate && dbStartTime) {
	                    let dbTimeShort = dbStartTime.substring(0, 5);
	                    let rowTimeShort = row.startTime.substring(0, 5);
	
	                  
	                    if (row.employeeId == dbEmpId && rowTimeShort === dbTimeShort) {
	                        
	                      
	                        if (row[dbDate] === "已預約") {
	                            row[dbDate] = "可預約";                           
	                        }
	                    }
	                }
	            });
           
          if (table) {
            table.setData(response)
              .then(function(){
                 if(response.length === 0) {
                     alert("該時段無排班資料");
                 }
              })
              .catch(function(error){
                 console.error("Tabulator 渲染錯誤", error);
              });
          }
        },
        error: function(xhr, status, error) {
          console.error("AJAX 錯誤:", status, error);
          console.log("回應內容:", xhr.responseText);
          alert("資料載入失敗，請檢查網路或後端狀態。");
        }
      });
    } 

    
    
	
    const paramEmpId = getURLParameter("AppointEmployee");
    const paramDate = getURLParameter("AppointDate");
    const paramStartTime = getURLParameter("AppointStartTime");
    const paramEndTime = getURLParameter("AppointEndTime");
    const paramSlotId = getURLParameter("AppointSlotId"); 

    console.log("初始參數:", { paramEmpId, paramDate, paramStartTime,paramEndTime, paramSlotId });

 
    if (paramEmpId) {
        $(employeeSelect).val(paramEmpId);
        
       
        if ($(employeeSelect).val() == paramEmpId) {
            
           
            initializeTable(paramEmpId, currentStartDate);
            
            
            if (paramDate && paramStartTime) {
                
                
                let empName = $("#employeeSelect option:selected").text();
                
               
                selectedSlotData = {
                    employeeId: paramEmpId,
                    employeeName: empName,
                    slotDate: paramDate,
                    startTime: paramStartTime,
                    endTime:paramEndTime,
                    realSlotId: paramSlotId
                };
                
               
                selectedSlotInfo.textContent = empName + ", " + paramDate + " " + paramStartTime + "~" + paramEndTime;
                selectedSlotInfo.style.color = "#007bff"; 
                
                
                confirmBtn.disabled = false;
            }
        }
    } else {
        
        updateNavButtonsState();
    }

   
    $("#employeeSelect").on("change", function () {
      const selectedId = $(this).val();
      currentStartDate = getTodayDate(); 
      initializeTable(selectedId, currentStartDate);
    });
	
 	
    $("#prev-week-btn").on("click", function () {
      if (!employeeSelect.value) return; 
      
      const prevDateStr = getNewDate(-7, currentStartDate);
		
   	 
      if (prevDateStr < REAL_TODAY_STR) {
         if (currentStartDate <= REAL_TODAY_STR) return; 
         currentStartDate = REAL_TODAY_STR;
      } else {
         currentStartDate = prevDateStr;
      }
   
      initializeTable(employeeSelect.value, currentStartDate);
    });
	
  
    $("#next-week-btn").on("click", function () {
      if (!employeeSelect.value) return;
      currentStartDate = getNewDate(7, currentStartDate);
      initializeTable(employeeSelect.value, currentStartDate);
    });
	
  	
    $("#confirm-selection-btn").on("click", function () {
      if (!selectedSlotData) {
        alert("請先選擇時段");
        return;
      }
      
    
      if (window.opener && !window.opener.closed) { 
          try {
        	  
              window.opener.receiveScheduleData(
                  selectedSlotData.employeeId,
                  selectedSlotData.employeeName,
                  selectedSlotData.slotDate,
                  selectedSlotData.startTime,
                  selectedSlotData.endTime,
                  selectedSlotData.realSlotId	
              );
        	 
              window.close();
          } catch (e) {
              console.error("傳回資料失敗:", e);
              alert("無法將資料傳回主頁面，請確認頁面未關閉。");
          }
      } else {
          alert("父視窗已關閉，修改訂單失敗，請重新至預約訂單進行修改");
      }
    });

   
    updateNavButtonsState();
  });
</script>
</body>
</html>