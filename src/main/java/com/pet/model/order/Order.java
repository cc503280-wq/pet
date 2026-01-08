package com.pet.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "total_amount_undiscount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmountUndiscount;

    @Column(name = "coupon_id")
    private Integer couponId; // 可以為 NULL

    @Column(name = "total_amount_discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmountDiscount;

    @Column(name = "use_points")
    private Integer usePoints;

    @Column(name = "total_amount_discount_points", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmountDiscountPoints;

    @Column(name = "get_points")
    private Integer getPoints;
    
    
}