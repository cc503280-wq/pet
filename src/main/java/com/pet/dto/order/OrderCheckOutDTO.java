package com.pet.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderCheckOutDTO {
	 // 對應前端: memberId
    private Integer memberId;
    
    // 對應前端: couponId (若前端傳 null，這裡也會是 null)
    private Integer couponId;
    
    private Integer couponUsersId;
    
    // 對應前端: pointsUsed
    private Integer pointsUsed;
    
    // 對應前端: receiverName
    private String receiverName;
    
    // 對應前端: receiverPhone
    private String receiverPhone;
    
    // 對應前端: receiverAddress
    private String receiverAddress;
    
    // 對應前端: shippingMethod
    private String shippingMethod;
    
    private String paymentMethod;
    
    // 對應前端: totalAmount
    // 注意：雖然前端有傳總金額，但後端通常只用來比對，實際金額應由後端重算
    private Integer totalAmount;
    
    

}
