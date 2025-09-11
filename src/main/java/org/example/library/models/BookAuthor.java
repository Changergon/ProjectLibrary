package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
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
    @JsonBackReference // Prevents serialization loop
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAuthor that = (BookAuthor) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BookAuthor{" +
                "id=" + id +
                ", bookId=" + (book != null ? book.getBookId() : null) + // Указываем только идентификатор книги
                ", authorId=" + (author != null ? author.getAuthorId() : null) + // Указываем только идентификатор автора
                '}';
    }
}
