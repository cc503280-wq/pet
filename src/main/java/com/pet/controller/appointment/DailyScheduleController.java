package com.pet.controller.appointment;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pet.service.appointment.DailyScheduleService;

@RestController
@RequestMapping("/schedule")
public class DailyScheduleController {

	@Autowired
	private DailyScheduleService dailyScheduleService;

	@GetMapping("/available")
	public Object getAvailableSlots(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam int duration) {

		return dailyScheduleService.getAvailableTimeSlots(date, duration);
	}
	
	
	
}
