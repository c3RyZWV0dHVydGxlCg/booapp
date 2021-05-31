package com.pavelmakhov.booapp.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"date", "timeSlot", "provider"}))
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "provider")
    private Provider provider;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "timeSlot")
    private TimeSlot timeSlot;
}
