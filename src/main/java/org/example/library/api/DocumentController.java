package org.example.library.api;

import org.example.library.domain.Document;
import org.example.library.services.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Document uploadDocument(@RequestParam("file") MultipartFile file) {
        return documentService.saveDocument(file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID id) {
        Path filePath = documentService.getDocumentFile(id);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            Document document = documentService.getDocument(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
    }
}
