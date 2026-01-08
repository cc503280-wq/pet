package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.DailySchedule;

public interface AppointmentDetailListRepository extends JpaRepository<AppointmentDetailList, Integer>  {
	List<AppointmentDetailList> findByAppointmentId(Integer appointmentId);
}
