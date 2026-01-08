package com.pet.dao.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.LeaveRecord;



public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Integer> {

}
