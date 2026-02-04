package com.pet.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Table(name = "OrderItems")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productItemId;
	@ManyToOne
	@JoinColumn(name = "order_id",nullable=false)
	@NonNull
	private Order order;
	@NonNull
	private Integer productId;
	@NonNull
	private Integer quantity;
	@NonNull
	private BigDecimal unitPrice;
	@NonNull
	private BigDecimal subtotal;
	
	@Transient
	private String productName;	

}
