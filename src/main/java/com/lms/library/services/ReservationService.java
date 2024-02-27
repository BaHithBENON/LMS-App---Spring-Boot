package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelReservation;
import com.lms.library.repositories.ReservationRepository;

@Service
public class ReservationService {
	
	@Autowired
    private ReservationRepository reservationRepository;

    public List<ModelReservation> findAll() {
        return reservationRepository.findAll();
    }

    public ModelReservation save(ModelReservation reservation) {
        return reservationRepository.save(reservation);
    }

    public ModelReservation findById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
}
