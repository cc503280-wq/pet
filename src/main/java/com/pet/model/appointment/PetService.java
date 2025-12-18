package model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="service")
public class PetService {
	@Id @Column(name="service_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer serviceId;
	@Column(name="service_name")
    private String serviceName;
	@Column(name="price")
    private Integer price;
	@Column(name="duration_minutes")
    private Integer durationMinutes;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "petservice", cascade = CascadeType.ALL)
	private Set<Appointment> appointments = new HashSet<Appointment>();
    

	public PetService() {
	}


	public PetService(Integer serviceId, String serviceName, Integer price, Integer durationMinutes) {
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


	public Set<Appointment> getAppointments() {
		return appointments;
	}


	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}


	
	
}
	