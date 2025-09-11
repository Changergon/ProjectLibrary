package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.Author;
import org.example.library.repositories.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public Author findByFirstNameAndLastName(String firstName, String lastName) {
        logger.info("Finding author by name: {} {}", firstName, lastName);
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        logger.info("Fetching all authors.");
        // This now uses the optimized findAll() from the repository
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        logger.info("Fetching author by ID: {}", id);
        // This now uses the optimized findById() and throws an exception if not found
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));
    }

    @Transactional
    public Author saveAuthor(Author author) {
        logger.info("Saving author: {} {}", author.getFirstName(), author.getLastName());
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        logger.info("Deleting author with ID: {}", id);
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Author not found with ID: " + id);
        }
        authorRepository.deleteById(id);
    }
}
