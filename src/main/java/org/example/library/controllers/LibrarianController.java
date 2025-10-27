package org.example.library.controllers;

import org.example.library.models.DTO.PhysicalCopyCreateDto;
import org.example.library.models.PhysicalCopy;
import org.example.library.services.PhysicalCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/librarian")
public class LibrarianController {

    @Autowired
    private PhysicalCopyService physicalCopyService;

    @PostMapping("/physical-copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<?> addPhysicalCopy(@RequestBody PhysicalCopyCreateDto dto) {
        try {
            PhysicalCopy createdCopy = physicalCopyService.addPhysicalCopy(
                    dto.getBookId(),
                    dto.getRowNumber(),
                    dto.getShelfNumber(),
                    dto.getPositionNumber()
            );
            return ResponseEntity.ok(createdCopy);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
