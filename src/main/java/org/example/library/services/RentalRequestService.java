package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.repositories.PhysicalCopyRepository;
import org.example.library.repositories.RentalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(RentalRequestService.class);

    private final RentalRequestRepository rentalRequestRepository;
    private final PhysicalCopyRepository physicalCopyRepository;

    @Autowired
    public RentalRequestService(RentalRequestRepository rentalRequestRepository, PhysicalCopyRepository physicalCopyRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.physicalCopyRepository = physicalCopyRepository;
    }

    @Transactional
    public RentalRequest createRentalRequest(RentalRequest request) {
        logger.info("Creating new rental request for user ID: {} and physical copy ID: {}", request.getUser().getId(), request.getPhysicalCopy().getCopyId());
        request.setStatus(RentalRequestStatus.PENDING);
        return rentalRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getPendingRentalRequests() {
        logger.info("Fetching all PENDING rental requests.");
        return rentalRequestRepository.findByStatus(RentalRequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getActiveRentalRequests() {
        logger.info("Fetching all ACTIVE rental requests.");
        return rentalRequestRepository.findByStatus(RentalRequestStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getCompletedRentalRequests() {
        logger.info("Fetching all COMPLETED rental requests.");
        return rentalRequestRepository.findByStatus(RentalRequestStatus.COMPLETED);
    }

    @Transactional
    public void approveRentalRequest(Long requestId) {
        logger.info("Approving rental request with ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot approve request that is not in PENDING state.");
        }

        PhysicalCopy copy = request.getPhysicalCopy();
        if (copy == null || !copy.isAvailable()) {
            throw new IllegalStateException("Physical copy is not available for rental.");
        }

        request.setStatus(RentalRequestStatus.ACTIVE); // Change status to ACTIVE as the book is now with the user
        request.setRentalStartDate(LocalDateTime.now());
        request.setRentalDueDate(LocalDateTime.now().plusDays(14));

        copy.setAvailable(false);
        physicalCopyRepository.save(copy);

        rentalRequestRepository.save(request);
        logger.info("Rental request ID: {} has been approved and is now ACTIVE.", requestId);
    }

    @Transactional
    public void rejectRentalRequest(Long requestId) {
        logger.info("Rejecting rental request with ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject request that is not in PENDING state.");
        }

        request.setStatus(RentalRequestStatus.REJECTED);
        rentalRequestRepository.save(request);
        logger.info("Rental request ID: {} has been REJECTED.", requestId);
    }

    @Transactional
    public void returnBook(Long requestId) {
        logger.info("Processing return for rental request ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.ACTIVE) {
            throw new IllegalStateException("Cannot return a book that is not in ACTIVE state.");
        }

        request.setStatus(RentalRequestStatus.RETURNED);

        PhysicalCopy copy = request.getPhysicalCopy();
        if (copy != null) {
            copy.setAvailable(true);
            physicalCopyRepository.save(copy);
        }

        rentalRequestRepository.save(request);
        logger.info("Book return processed for request ID: {}. Status set to RETURNED.", requestId);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getOverdueRentals() {
        logger.info("Fetching overdue rentals.");
        LocalDateTime now = LocalDateTime.now();
        return rentalRequestRepository.findByStatus(RentalRequestStatus.ACTIVE).stream()
                .filter(request -> request.getRentalDueDate() != null && request.getRentalDueDate().isBefore(now))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PhysicalCopy> getAvailableCopies() {
        logger.info("Fetching all available physical copies.");
        return physicalCopyRepository.findByAvailable(true);
    }
}
