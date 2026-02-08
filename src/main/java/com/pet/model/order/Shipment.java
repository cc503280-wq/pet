package com.pet.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 收件人資訊
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;

    // 物流方式 (例如: "7-11", "全家", "宅配")
    private String shippingMethod;

    // ★ 關鍵 1：超商門市代號 (例如: 131386)
    // 如果是宅配，這裡可以是 null
    private String storeId; 

    private Integer shippingFee;

    // 物流狀態 (備貨中, 已出貨, 配送中, 已送達)
    private String status;

    // ★ 關鍵 2：寄貨編號 (CVS Payment No)
    // 這是綠界回傳給您的，您要給客人這組號碼去超商寄貨 (C2C)
    private String deliverySn; 

    // ★ 關鍵 3：綠界物流編號 (AllPayLogisticsID)
    // 這是綠界內部的唯一編號，查單用
    private String logisticsId; 

    private LocalDateTime shippedAt; // 出貨時間
    private LocalDateTime deliveredAt; // 送達時間
}