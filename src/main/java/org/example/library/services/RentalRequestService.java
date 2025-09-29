package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.DTO.RentalRequestDTO;
import org.example.library.models.LibraryUser;
import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.repositories.LibraryUserRepository;
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
    private final LibraryUserRepository libraryUserRepository;

    @Autowired
    public RentalRequestService(RentalRequestRepository rentalRequestRepository, PhysicalCopyRepository physicalCopyRepository, LibraryUserRepository libraryUserRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.physicalCopyRepository = physicalCopyRepository;
        this.libraryUserRepository = libraryUserRepository;
    }

    public RentalRequestDTO convertToDTO(RentalRequest request) {
        LibraryUser user = request.getUser();
        PhysicalCopy copy = request.getPhysicalCopy();
        return new RentalRequestDTO(
                request.getId(),
                request.getStatus().name(),
                request.getRentalStartDate(),
                request.getRentalDueDate(),
                user.getUserId(),
                user.getUsername(),
                copy.getBook().getBookId(),
                copy.getBook().getTitle(),
                copy.getCopyId(),
                copy.getRowNumber(),
                copy.getShelfNumber(),
                copy.getPositionNumber()
        );
    }

    @Transactional
    public RentalRequest createRequest(Long userId, Long copyId) {
        logger.info("Attempting to create rental request for user ID: {} and copy ID: {}", userId, copyId);

        LibraryUser user = libraryUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        PhysicalCopy copy = physicalCopyRepository.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Physical copy not found with ID: " + copyId));

        if (!copy.isAvailable()) {
            throw new IllegalStateException("This book copy is currently unavailable.");
        }

        boolean hasExistingRequest = rentalRequestRepository.existsByPhysicalCopyAndUserAndStatusIn(
                copy, user, List.of(RentalRequestStatus.PENDING, RentalRequestStatus.APPROVED, RentalRequestStatus.ACTIVE)
        );

        if (hasExistingRequest) {
            throw new IllegalStateException("You already have an active or pending request for this book.");
        }

        copy.setAvailable(false);
        physicalCopyRepository.save(copy);

        RentalRequest request = new RentalRequest();
        request.setUser(user);
        request.setPhysicalCopy(copy);
        request.setStatus(RentalRequestStatus.PENDING);

        RentalRequest savedRequest = rentalRequestRepository.save(request);
        logger.info("Successfully created rental request with ID: {}", savedRequest.getId());
        return savedRequest;
    }

    @Transactional
    public void approveRequest(Long requestId) {
        logger.info("Approving rental request with ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot approve a request that is not in PENDING state.");
        }

        request.setStatus(RentalRequestStatus.APPROVED);
        rentalRequestRepository.save(request);
        logger.info("Rental request ID: {} has been APPROVED.", requestId);
    }

    @Transactional
    public void issueBook(Long requestId) {
        logger.info("Issuing book for rental request ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.APPROVED) {
            throw new IllegalStateException("Book can only be issued if the request is APPROVED.");
        }

        request.setStatus(RentalRequestStatus.ACTIVE);
        request.setRentalStartDate(LocalDateTime.now());
        request.setRentalDueDate(LocalDateTime.now().plusDays(14)); // Возвращаем стандартный срок

        rentalRequestRepository.save(request);
        logger.info("Book issued for request ID: {}. Status is now ACTIVE.", requestId);
    }

    @Transactional
    public void rejectRequest(Long requestId) {
        logger.info("Rejecting rental request with ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));

        if (request.getStatus() != RentalRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a request that is not in PENDING state.");
        }

        PhysicalCopy copy = request.getPhysicalCopy();
        if (copy != null) {
            copy.setAvailable(true);
            physicalCopyRepository.save(copy);
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
    public List<RentalRequest> findUserRequests(Long userId) {
        logger.info("Fetching all rental requests for user ID: {}", userId);
        return rentalRequestRepository.findByUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> findAllPendingRequests() {
        logger.info("Fetching all PENDING rental requests.");
        return rentalRequestRepository.findByStatus(RentalRequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> findAllApprovedRequests() {
        logger.info("Fetching all APPROVED rental requests.");
        return rentalRequestRepository.findByStatus(RentalRequestStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getActiveRentalRequests() {
        return rentalRequestRepository.findByStatus(RentalRequestStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getCompletedRentalRequests() {
        return rentalRequestRepository.findByStatus(RentalRequestStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getOverdueRentals() {
        return rentalRequestRepository.findByStatus(RentalRequestStatus.ACTIVE).stream()
                .filter(request -> request.getRentalDueDate() != null && request.getRentalDueDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}
