package org.example.library.controllers;

import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.services.RentalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-requests")
public class RentalRequestController {

    @Autowired
    private RentalRequestService rentalRequestService;

    @PostMapping
    public ResponseEntity<RentalRequest> createRentalRequest(@RequestBody RentalRequest request) {
        RentalRequest createdRequest = rentalRequestService.createRentalRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping
    public ResponseEntity<List<RentalRequest>> getRentalRequests() {
        List<RentalRequest> requests = rentalRequestService.getRentalRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Void> approveRentalRequest(@PathVariable Long id) {
        rentalRequestService.approveRentalRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rented-books")
    public ResponseEntity<List<RentalRequest>> getRentedBooks() {
        List<RentalRequest> rentedBooks = rentalRequestService.getRentedBooks();
        return ResponseEntity.ok(rentedBooks);
    }

    // Получить все заявки с их статусами и расположением физической копии
    @GetMapping("/pending")
    public ResponseEntity<List<RentalRequest>> getPendingRequests() {
        List<RentalRequest> pendingRequests = rentalRequestService.getRentalRequests().stream()
                .filter(r -> r.getStatus() == RentalRequestStatus.PENDING)
                .toList();
        return ResponseEntity.ok(pendingRequests);
    }

    // Получить просроченные книги
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
