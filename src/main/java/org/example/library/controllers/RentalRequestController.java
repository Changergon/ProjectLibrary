package org.example.library.controllers;

import org.example.library.models.RentalRequest;
import org.example.library.services.RentalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
