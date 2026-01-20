package com.pet.model.appointment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Entity
@Table(name="service")
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="service_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer serviceId;

	@Column(name="service_name")
    private String serviceName; 

	@Column(name="target_pet_type")
    private String targetPetType; 

	@Column(name="target_pet_size")
    private String targetPetSize; 

	@Column(name = "description")
	private String description; 

	@Column(name="price")
    private BigDecimal price;

	@Column(name="duration_minutes")
    private Integer durationMinutes; 

	@Column(name="picture")
	private String picture; 

	@Column(name="is_addon")
	private Boolean isAddon; 

	@Column(name="is_active")
	private Boolean isActive=true; 

	@Column(name="created_at", insertable = false, updatable = false)
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime createdAt;

	@Column(name="updated_at", insertable = false, updatable = false)
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime updatedAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceItem", cascade = CascadeType.ALL)
	@JsonIgnore
	private transient Set<AppointmentDetails> appointmentDetails = new HashSet<AppointmentDetails>();

}