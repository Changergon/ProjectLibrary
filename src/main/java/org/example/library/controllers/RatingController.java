package org.example.library.controllers;

import org.example.library.services.BookRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {



    @Autowired
    private BookRatingService bookRatingService;

    @PostMapping("/{bookId}/rate")
    public ResponseEntity<?> rateBook(
            @PathVariable Long bookId,
            @RequestParam int rating,
            Authentication authentication) {

        try {
            bookRatingService.addOrUpdateRating(bookId, rating, authentication.getName());
            // Возвращаем JSON с сообщением
            Map<String, String> response = Collections.singletonMap("message", "Оценка успешно добавлена");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = Collections.singletonMap("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }




}
