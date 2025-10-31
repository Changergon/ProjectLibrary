package org.example.library.services;

import org.example.library.domain.Document;
import org.example.library.exception.ResourceNotFoundException;
import org.example.library.repositories.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    public DocumentService(DocumentRepository documentRepository, StorageService storageService) {
        this.documentRepository = documentRepository;
        this.storageService = storageService;
    }

    public Document getDocument(UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }

    public Path getDocumentFile(UUID id) {
        Document document = getDocument(id);
        return storageService.load(document.getFilePath());
    }

    public Document saveDocument(MultipartFile file) {
        String filePath = storageService.store(file);
        Document document = new Document(file.getOriginalFilename(), file.getContentType(), filePath);
        return documentRepository.save(document);
    }

    public void deleteDocument(UUID id) {
        Document document = getDocument(id);
        storageService.delete(document.getFilePath());
        documentRepository.delete(document);
    }
}
