package org.example.library.controllers;

import org.example.library.models.DTO.PhysicalCopyCreateDto;
import org.example.library.models.PhysicalCopy;
import org.example.library.services.PhysicalCopyService;
import org.example.library.services.RentalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RentBooksPageController {

    @Autowired
    private RentalRequestService rentalRequestService;

    @Autowired
    private PhysicalCopyService physicalCopyService;

    @GetMapping("/rent-books")
    public String showRentBooksPage(Model model) {
        List<PhysicalCopy> availableCopies = rentalRequestService.getAvailableCopies();
        model.addAttribute("availableCopies", availableCopies);
        return "rent-books";  // имя thymeleaf шаблона
    }

    @GetMapping("/add-physical-copy")
    public String showAddPhysicalCopyPage() {
        return "add-physical-copy";  // имя thymeleaf шаблона для добавления физической копии
    }

    @PostMapping("/api/physical-copies")
    @ResponseBody
    public ResponseEntity<PhysicalCopy> addPhysicalCopy(@RequestBody PhysicalCopyCreateDto dto) {
        PhysicalCopy createdCopy = physicalCopyService.addPhysicalCopy(
                dto.getBookId(),
                dto.getRowNumber(),
                dto.getShelfNumber(),
                dto.getPositionNumber()
        );
        return ResponseEntity.ok(createdCopy);
    }
}
