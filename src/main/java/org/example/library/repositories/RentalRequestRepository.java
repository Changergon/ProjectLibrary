package org.example.library.repositories;

import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {
    List<RentalRequest> findByStatus(RentalRequestStatus status); // Метод для поиска по статусу
}
