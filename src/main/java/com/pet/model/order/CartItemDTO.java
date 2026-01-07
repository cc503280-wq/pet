package com.pet.model.order;

import java.io.Serializable;

public class CartItemDTO implements Serializable {
	private static final long serialVersionUID = 1L; 
	 	private Integer productId;
	    private Integer quantity;
	    private String productName;
	    private Integer price;
	    
	    public CartItemDTO(Integer productId, Integer quantity, String productName, Integer price) {
	        this.productId = productId;
	        this.quantity = quantity;
	        this.productName=productName;
	        this.price=price;
	    }

	    // Getter 與 Setter
	    public Integer getProductId() { return productId;}
	    public void setProductId(Integer productId) { this.productId = productId; }
	    public Integer getQuantity() { return quantity; }
	    public void setQuantity(Integer quantity) { this.quantity = quantity; }
	    public String getProductName() { return productName; }
	    public void setProductName(String productName) { this.productName=productName; }
	    public Integer getPrice() {return price;}
	    public void setPrice(Integer price) { this.price=price; }
}
