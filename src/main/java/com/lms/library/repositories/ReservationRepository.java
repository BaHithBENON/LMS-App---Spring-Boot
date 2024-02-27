package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelReservation;

@Repository
public interface ReservationRepository extends JpaRepository<ModelReservation, Long> {

}
