package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_authors")
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Override
    public String toString() {
        return "BookAuthor{" +
                "id=" + id +
                ", bookId=" + (book != null ? book.getBookId() : null) + // Указываем только идентификатор книги
                ", authorId=" + (author != null ? author.getAuthorId() : null) + // Указываем только идентификатор автора
                '}';
    }
}
