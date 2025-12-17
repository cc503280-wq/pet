package com.pet.model.appoinment;


public class PetServiceBean {
	
	private Integer serviceId;
    private String serviceName;
    private Integer price;
    private Integer durationMinutes;
    

	public PetServiceBean() {
		super();
		// TODO Auto-generated constructor stub
	}


	public PetServiceBean(Integer serviceId, String serviceName, Integer price, Integer durationMinutes) {
		super();
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.price = price;
		this.durationMinutes = durationMinutes;
	}


	public Integer getServiceId() {
		return serviceId;
	}


	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public Integer getPrice() {
		return price;
	}


	public void setPrice(Integer price) {
		this.price = price;
	}


	public Integer getDurationMinutes() {
		return durationMinutes;
	}


	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	

}
