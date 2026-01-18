package com.pet.model.appointment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class AppointmentRequest {

    @NotNull
    private Integer memberId;

    @NotNull
    private Integer petId;

    @NotNull
    private Integer groomerId;

    @NotNull
    @Future
    private LocalDate appointmentDate;

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;

    private Integer durationMinutes;

    @Positive
    private BigDecimal totalPrice;

    private String notes;

    @NotEmpty
    private List<Integer> serviceIds;
}