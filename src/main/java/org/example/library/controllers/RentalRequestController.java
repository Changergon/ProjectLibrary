package org.example.library.controllers;

import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.services.RentalRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-requests")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;

    public RentalRequestController(RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    @PostMapping
    public ResponseEntity<RentalRequest> createRentalRequest(@RequestBody RentalRequest request) {
        RentalRequest createdRequest = rentalRequestService.createRentalRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    // Returns all PENDING requests by default
    @GetMapping
    public ResponseEntity<List<RentalRequest>> getPendingRentalRequests() {
        List<RentalRequest> requests = rentalRequestService.getPendingRentalRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Void> approveRentalRequest(@PathVariable Long id) {
        rentalRequestService.approveRentalRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Void> rejectRentalRequest(@PathVariable Long id) {
        rentalRequestService.rejectRentalRequest(id);
        return ResponseEntity.ok().build();
    }

    // Returns all ACTIVE rental requests (books currently rented out)
    @GetMapping("/active")
    public ResponseEntity<List<RentalRequest>> getActiveRentalRequests() {
        List<RentalRequest> rentedBooks = rentalRequestService.getActiveRentalRequests();
        return ResponseEntity.ok(rentedBooks);
    }

    // Returns all COMPLETED rental requests
    @GetMapping("/completed")
    public ResponseEntity<List<RentalRequest>> getCompletedRentalRequests() {
        List<RentalRequest> completedRequests = rentalRequestService.getCompletedRentalRequests();
        return ResponseEntity.ok(completedRequests);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalRequest>> getOverdueRentals() {
        List<RentalRequest> overdue = rentalRequestService.getOverdueRentals();
        return ResponseEntity.ok(overdue);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        rentalRequestService.returnBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-copies")
    public ResponseEntity<List<PhysicalCopy>> getAvailableCopies() {
        List<PhysicalCopy> availableCopies = rentalRequestService.getAvailableCopies();
        return ResponseEntity.ok(availableCopies);
    }
}
