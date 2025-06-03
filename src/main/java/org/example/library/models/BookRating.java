package org.example.library.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "book_ratings")
public class BookRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private LibraryUser user;

    @Column(nullable = false)
    private int rating; // от 1 до 5
}