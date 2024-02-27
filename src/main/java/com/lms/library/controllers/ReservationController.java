package com.lms.library.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelReservation;
import com.lms.library.services.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
	
	@Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<ModelReservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PostMapping
    public ModelReservation createReservation(@RequestBody ModelReservation reservation) {
        return reservationService.save(reservation);
    }

    @GetMapping("/{id}")
    public ModelReservation getReservationById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelReservation updateReservation(@PathVariable Long id, @RequestBody ModelReservation reservationDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteById(id);
    }
}
