package com.pet.model.order;

import java.util.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "shipments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shipmentId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	@NonNull
	private Order order;
	@NonNull
	private String shippingMethod;
	@NonNull
	private Integer shippingFee;
	@NonNull
	private String trackingNumber;
	@NonNull
	private Date shippedAt;
	@NonNull
	private Date deliveredAt;
	@NonNull
	private String status;
	@NonNull
	private String recipientName;
	@NonNull
	private String recipientPhone;
	@NonNull
	private String shippingAddress;

}
