package com.pet.controller.member;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pet.dao.member.CouponDao;
import com.pet.dao.member.MemberDao;
import com.pet.model.member.Coupon;
import com.pet.model.member.Member;


@WebServlet("/CouponServlet")
public class CouponServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CouponDao couponDao = new CouponDao();
	Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create(); //設定回傳JSON的日期格式
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		PrintWriter out = response.getWriter();
		
		try { 
			
			switch(action) {
			//查詢全部
			case "list":
			    String status = request.getParameter("status"); // 前端下拉選單傳來 all, active, disabled
			    List<Coupon> coupons;

			    // 根據 status 呼叫不同 DAO 方法
			    if (status == null || status.equals("all")) {
			        coupons = couponDao.queryAllCoupons();
			    } else if (status.equals("active")) {
			        coupons = couponDao.queryActiveCoupons();
			    } else if (status.equals("disabled")) {
			        coupons = couponDao.queryDisabledCoupons();
			    } else {
			        // 防呆：傳了未知 status
			        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			        out.print("{\"error\":\"未知的 status\"}");
			        return;
			    }
			    
			    List<Map<String, Object>> result1 = convertCouponsToJson(coupons);

			    // 最後輸出
			    out.print(gson.toJson(result1));
			    break;
			    
				//依id查詢
			case "queryById":
				int id = Integer.parseInt(request.getParameter("couponId"));
				Coupon coupon = couponDao.queryCouponById(id);
				Map<String, Object> json = new HashMap<>();
			    json.put("couponId", coupon.getCouponId());
			    json.put("code", coupon.getCode());
			    json.put("discountType", coupon.getDiscountType());
			    json.put("discountValue", coupon.getDiscountValue());
			    json.put("isLimited", coupon.getIsLimited());
			    json.put("totalAmount", coupon.getTotalAmount());
			    json.put("issuedAmount", coupon.getIssuedAmount());
			    json.put("minPurchase", coupon.getMinPurchase());
			    json.put("status", coupon.getStatus());
			    json.put("issueStartAt", simpleDateFormat.format(coupon.getIssueStartAt()));
			    json.put("issueEndAt", simpleDateFormat.format(coupon.getIssueEndAt()));
			    json.put("useStartAt", simpleDateFormat.format(coupon.getUseStartAt()));
			    json.put("useEndAt", simpleDateFormat.format(coupon.getUseEndAt()));

			    out.print(gson.toJson(json));  
			    break;
				
				// 發放期間區間查詢
            case "queryByIssueRange":
            	String startStr = request.getParameter("start");
            	String endStr = request.getParameter("end");
            	if(startStr == null || endStr == null || startStr.isEmpty() || endStr.isEmpty()){
            	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	    out.print("{\"error\":\"請提供起迄日期\"}");
            	    return;
            	}
            	Date issueStart = simpleDateFormat.parse(startStr);
            	Date issueEnd = simpleDateFormat.parse(endStr);
                List<Coupon> issueCoupons = couponDao.queryCouponsByIssueRange(issueStart, issueEnd);
                List<Map<String, Object>> result3 = convertCouponsToJson(issueCoupons);
                out.print(gson.toJson(result3));
                break;
				
             // 使用期間區間查詢
            case "queryByUseRange":
            	String startUseStr = request.getParameter("start");
            	String endUseStr = request.getParameter("end");
            	if(startUseStr == null || endUseStr == null || startUseStr.isEmpty() || endUseStr.isEmpty()){
            	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	    out.print("{\"error\":\"請提供起迄日期\"}");
            	    return;
            	}
            	Date useStart = simpleDateFormat.parse(startUseStr);
            	Date useEnd = simpleDateFormat.parse(endUseStr);
                List<Coupon> useCoupons = couponDao.queryCouponsByUseRange(useStart, useEnd);
                List<Map<String, Object>> result4 = convertCouponsToJson(useCoupons);
                out.print(gson.toJson(result4));
                break;    
                
				//停用啟用
			case "toggleStatus":
                int couponId = Integer.parseInt(request.getParameter("couponId"));
                boolean success = couponDao.toggleStatusCoupon(couponId);
                out.print(gson.toJson(success));
                break;
                
                //新增
			case "create": 
				String code = request.getParameter("code");
				String discountType = request.getParameter("discountType");
			    Double discountValue = Double.parseDouble(request.getParameter("discountValue"));
			    System.out.println(request.getParameter("discountValue"));
			    System.out.println(discountValue);
			    int isLimited = Integer.parseInt(request.getParameter("isLimited"));
			    //如為不限量，數量可以為空值
			    Integer totalAmount = null;
			    String totalAmountStr = request.getParameter("totalAmount");
			    if (totalAmountStr != null && !totalAmountStr.isEmpty()) {
			        totalAmount = Integer.parseInt(totalAmountStr);
			    }
			    Integer issuedAmount = null;
			    String issuedAmountStr = request.getParameter("issuedAmount");
			    if (isLimited == 1) { // 有限量
			        if (issuedAmountStr != null && !issuedAmountStr.isEmpty()) {
			            issuedAmount = Integer.parseInt(issuedAmountStr);
			        } else {
			            issuedAmount = 0; // 有限量但前端沒傳，補0
			        }
			    } else { // 不限量
			        issuedAmount = null; // 保持 null
			    }
			    int minPurchase = Integer.parseInt(request.getParameter("minPurchase"));
			    Date issueStartAt = simpleDateFormat.parse(request.getParameter("issueStartAt"));
		        Date issueEndAt = simpleDateFormat.parse(request.getParameter("issueEndAt"));
		        Date useStartAt = simpleDateFormat.parse(request.getParameter("useStartAt"));
		        Date useEndAt = simpleDateFormat.parse(request.getParameter("useEndAt"));
		        
		        String errorMsg = validateCoupon(discountType,discountValue, isLimited, totalAmount, issuedAmount, minPurchase,
                        issueStartAt, issueEndAt, useStartAt, useEndAt);
				if (errorMsg != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					out.print("{\"error\":\"" + errorMsg + "\"}");
					return;
				}
			    Coupon newCoupon = new Coupon(code, discountType, discountValue, isLimited, totalAmount, issuedAmount, issueStartAt, issueEndAt, useStartAt, useEndAt, minPurchase);
			    Coupon create = couponDao.createCoupon(newCoupon);
			    out.print(gson.toJson(create));

			    break;
			    
			    //修改
			case "update":
			    try {
			    	int updateCouponId = Integer.parseInt(request.getParameter("couponId"));
			    	System.out.println(request.getParameter("couponId"));
			    	String updateCode = request.getParameter("code");
					String updateDiscountType = request.getParameter("discountType");
				    Double updateDiscountValue = Double.parseDouble(request.getParameter("discountValue"));
				    
				    int updateIsLimited = Integer.parseInt(request.getParameter("isLimited"));
				    Integer updateTotalAmount = null;
				    String updateTotalAmountStr = request.getParameter("totalAmount");
				    if (updateTotalAmountStr != null && !updateTotalAmountStr.isEmpty()) {
				    	updateTotalAmount = Integer.parseInt(updateTotalAmountStr);
				    }
				    Integer updateIssuedAmount = null;
				    String updateissuedAmountStr = request.getParameter("issuedAmount");
				    if (updateIsLimited == 1) { // 有限量
				        if (updateissuedAmountStr != null && !updateissuedAmountStr.isEmpty()) {
				        	updateIssuedAmount = Integer.parseInt(updateissuedAmountStr);
				        } else {
				        	updateIssuedAmount = 0; // 有限量但前端沒傳，補0
				        }
				    } else { // 不限量
				    	updateIssuedAmount = null; // 保持 null
				    }
				    int updateMinPurchase = Integer.parseInt(request.getParameter("minPurchase"));
				    Date updateIssueStartAt = simpleDateFormat.parse(request.getParameter("issueStartAt"));
			        Date updateIssueEndAt = simpleDateFormat.parse(request.getParameter("issueEndAt"));
			        Date updateUseStartAt = simpleDateFormat.parse(request.getParameter("useStartAt"));
			        Date updateUseEndAt = simpleDateFormat.parse(request.getParameter("useEndAt"));
			        
			        String updateErrorMsg = validateCoupon(updateDiscountType,updateDiscountValue, updateIsLimited, updateTotalAmount, updateIssuedAmount, updateMinPurchase,
			        		updateIssueStartAt, updateIssueEndAt, updateUseStartAt, updateUseEndAt);
					if (updateErrorMsg != null) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						out.print("{\"error\":\"" + updateErrorMsg + "\"}");
						return;
					}
				    Coupon updateCoupon = new Coupon(updateCouponId, updateCode, updateDiscountType, updateDiscountValue, updateIsLimited, updateTotalAmount, updateIssuedAmount, updateIssueStartAt, updateIssueEndAt, updateUseStartAt, updateUseEndAt, updateMinPurchase);
				    Coupon updated = couponDao.updateCoupon(updateCoupon);
				    out.print(gson.toJson(updated));
			        System.out.println("回傳: " + gson.toJson(updated));
			    } catch(Exception e){
			        e.printStackTrace();
			        response.getWriter().println("伺服器錯誤！");
			    }
			    break;
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\":\"未知的 action\"}");
			}
			
		} catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"伺服器錯誤\"}");
        }
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String validateCoupon(String discountType, Double discountValue, Integer isLimited, Integer totalAmount, Integer issuedAmount, 
            Integer minPurchase, Date issueStartAt, Date issueEndAt, Date useStartAt, Date useEndAt) {
		
		if ("percent".equals(discountType)) {
		    if (discountValue == null || discountValue < 0.0 || discountValue > 1.0) {
		        return "百分比折扣必須介於 0.0 ~ 1.0（例如 0.5 = 五折）";
		    }
		}
		
		if ("amount".equals(discountType)) {
		    if (discountValue == null || discountValue < 0) {
		        return "金額折扣不得小於 0";
		    }
		    if (minPurchase != null && discountValue > minPurchase) {
	            return "折抵金額不可超過最低消費金額";
	        }
		}
		
		if (isLimited < 0 || minPurchase < 0 ||
		(totalAmount != null && totalAmount < 0) ||
		(issuedAmount != null && issuedAmount < 0)) {
		return "數值欄位不能小於0";
		}
		
		if (issueStartAt.after(issueEndAt)) {
		return "發放開始日期不得晚於發放結束日期";
		}
		
		if (useStartAt.after(useEndAt)) {
		return "使用開始日期不得晚於使用結束日期";
		}
		
		if (useStartAt.before(issueStartAt)) {
		return "使用開始日期不得早於發放開始日期";
		}
		
		return null; // 驗證通過
	}
	//把資料統一格式後放入json
	private List<Map<String, Object>> convertCouponsToJson(List<Coupon> coupons) {
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (Coupon c : coupons) {
	        Map<String, Object> json = new HashMap<>();
	        json.put("couponId", c.getCouponId());
	        json.put("code", c.getCode());
	        json.put("discountType", c.getDiscountType());
	        json.put("discountValue", c.getDiscountValue());
	        json.put("isLimited", c.getIsLimited());
	        json.put("totalAmount", c.getTotalAmount());
	        json.put("issuedAmount", c.getIssuedAmount());
	        json.put("minPurchase", c.getMinPurchase());
	        json.put("status", c.getStatus());
	        json.put("issueStartAt", simpleDateFormat.format(c.getIssueStartAt()));
	        json.put("issueEndAt", simpleDateFormat.format(c.getIssueEndAt()));
	        json.put("useStartAt", simpleDateFormat.format(c.getUseStartAt()));
	        json.put("useEndAt", simpleDateFormat.format(c.getUseEndAt()));
	        result.add(json);
	    }
	    return result;
	}

}
