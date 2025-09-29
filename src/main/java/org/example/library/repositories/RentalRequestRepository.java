package org.example.library.repositories;

import org.example.library.models.LibraryUser;
import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {
    @EntityGraph(attributePaths = {"user", "physicalCopy"})
    List<RentalRequest> findByStatus(RentalRequestStatus status);

    @Override
    @EntityGraph(attributePaths = {"user", "physicalCopy", "physicalCopy.book"})
    List<RentalRequest> findAll();

    @EntityGraph(attributePaths = {"user", "physicalCopy", "physicalCopy.book"})
    List<RentalRequest> findByUser_UserId(Long userId);

    boolean existsByPhysicalCopyAndUserAndStatusIn(PhysicalCopy physicalCopy, LibraryUser user, List<RentalRequestStatus> statuses);
}
