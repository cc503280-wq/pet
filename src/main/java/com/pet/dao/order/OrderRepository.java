package com.pet.dao.order;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByMemberId(Integer memberId);
	// 1. 全站總銷售額 (SQL Server 語法)
    @Query(value = """
        SELECT 
            FORMAT(order_date, 'yyyy-MM') as [month], 
            SUM(total_amount_discount_points) 
        FROM orders 
        WHERE order_date >= DATEADD(MONTH, -5, GETDATE())
        GROUP BY FORMAT(order_date, 'yyyy-MM')
        ORDER BY [month] ASC
        """, nativeQuery = true)
    List<Object[]> getTotalMonthlySales();

    // 2. 指定會員銷售額 (SQL Server 語法)
    @Query(value = """
        SELECT 
            FORMAT(order_date, 'yyyy-MM') as [month], 
            SUM(total_amount_discount_points) 
        FROM orders 
        WHERE member_id = :memberId 
        AND order_date >= DATEADD(MONTH, -5, GETDATE())
        GROUP BY FORMAT(order_date, 'yyyy-MM')
        ORDER BY [month] ASC
        """, nativeQuery = true)
    List<Object[]> getMemberMonthlySales(@Param("memberId") Integer memberId);
}
