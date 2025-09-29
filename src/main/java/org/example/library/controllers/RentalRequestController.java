package org.example.library.controllers;

import org.example.library.models.DTO.RentalRequestDTO;
import org.example.library.models.LibraryUser;
import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.services.PhysicalCopyService;
import org.example.library.services.RentalRequestService;
import org.example.library.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rental-requests")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;
    private final PhysicalCopyService physicalCopyService;
    private final UserService userService;

    public RentalRequestController(RentalRequestService rentalRequestService, PhysicalCopyService physicalCopyService, UserService userService) {
        this.rentalRequestService = rentalRequestService;
        this.physicalCopyService = physicalCopyService;
        this.userService = userService;
    }

    @PostMapping("/copy/{copyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalRequestDTO> createRentalRequest(@PathVariable Long copyId, Principal principal) {
        try {
            LibraryUser currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            RentalRequest createdRequest = rentalRequestService.createRequest(currentUser.getUserId(), copyId);
            return ResponseEntity.ok(rentalRequestService.convertToDTO(createdRequest));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<RentalRequestDTO>> getPendingRentalRequests() {
        List<RentalRequest> requests = rentalRequestService.findAllPendingRequests();
        List<RentalRequestDTO> dtos = requests.stream().map(rentalRequestService::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<RentalRequestDTO>> getApprovedRentalRequests() {
        List<RentalRequest> requests = rentalRequestService.findAllApprovedRequests();
        List<RentalRequestDTO> dtos = requests.stream().map(rentalRequestService::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> approveRentalRequest(@PathVariable Long id) {
        rentalRequestService.approveRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> rejectRentalRequest(@PathVariable Long id) {
        rentalRequestService.rejectRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/issue/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> issueBook(@PathVariable Long id) {
        try {
            rentalRequestService.issueBook(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<RentalRequestDTO>> getActiveRentalRequests() {
        List<RentalRequest> rentedBooks = rentalRequestService.getActiveRentalRequests();
        List<RentalRequestDTO> dtos = rentedBooks.stream().map(rentalRequestService::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<RentalRequestDTO>> getCompletedRentalRequests() {
        List<RentalRequest> completedRequests = rentalRequestService.getCompletedRentalRequests();
        List<RentalRequestDTO> dtos = completedRequests.stream().map(rentalRequestService::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<RentalRequestDTO>> getOverdueRentals() {
        List<RentalRequest> overdue = rentalRequestService.getOverdueRentals();
        List<RentalRequestDTO> dtos = overdue.stream().map(rentalRequestService::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/return/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        rentalRequestService.returnBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-copies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PhysicalCopy>> getAvailableCopies() {
        List<PhysicalCopy> availableCopies = physicalCopyService.findAllAvailable();
        return ResponseEntity.ok(availableCopies);
    }
}
