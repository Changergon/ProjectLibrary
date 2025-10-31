package org.example.library.services;

import org.example.library.domain.Journal;
import org.example.library.dto.JournalRequest;
import org.example.library.dto.JournalResponse;
import org.example.library.exception.ResourceNotFoundException;
import org.example.library.repositories.JournalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JournalService {

    private final JournalRepository journalRepository;

    public JournalService(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    public Page<JournalResponse> getJournals(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Journal> journalPage;
        if (search != null && !search.isEmpty()) {
            journalPage = journalRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            journalPage = journalRepository.findAll(pageable);
        }

        return journalPage.map(this::convertToJournalResponse);
    }

    public JournalResponse getJournalById(UUID id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with id: " + id));
        return convertToJournalResponse(journal);
    }

    public JournalResponse createJournal(JournalRequest journalRequest) {
        Journal journal = new Journal();
        BeanUtils.copyProperties(journalRequest, journal);
        journal = journalRepository.save(journal);
        return convertToJournalResponse(journal);
    }

    public JournalResponse updateJournal(UUID id, JournalRequest journalRequest) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with id: " + id));
        BeanUtils.copyProperties(journalRequest, journal);
        journal = journalRepository.save(journal);
        return convertToJournalResponse(journal);
    }

    public void deleteJournal(UUID id) {
        if (!journalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Journal not found with id: " + id);
        }
        journalRepository.deleteById(id);
    }

    private JournalResponse convertToJournalResponse(Journal journal) {
        JournalResponse journalResponse = new JournalResponse();
        BeanUtils.copyProperties(journal, journalResponse);
        return journalResponse;
    }
}
