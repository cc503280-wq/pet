package com.pet.dao.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.AppointmentDetails;


public interface AppointmentDetailsRepository extends JpaRepository<AppointmentDetails, Integer> {

}
