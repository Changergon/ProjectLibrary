package org.example.library.api;

import jakarta.validation.Valid;
import org.example.library.dto.JournalRequest;
import org.example.library.dto.JournalResponse;
import org.example.library.services.JournalService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public Page<JournalResponse> getJournals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return journalService.getJournals(page, size, search);
    }

    @GetMapping("/{id}")
    public JournalResponse getJournalById(@PathVariable UUID id) {
        return journalService.getJournalById(id);
    }

    @PostMapping
    public ResponseEntity<JournalResponse> createJournal(@Valid @RequestBody JournalRequest journalRequest) {
        JournalResponse createdJournal = journalService.createJournal(journalRequest);
        return new ResponseEntity<>(createdJournal, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public JournalResponse updateJournal(@PathVariable UUID id, @Valid @RequestBody JournalRequest journalRequest) {
        return journalService.updateJournal(id, journalRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJournal(@PathVariable UUID id) {
        journalService.deleteJournal(id);
    }
}
