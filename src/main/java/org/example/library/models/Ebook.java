package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "ebooks")
public class Ebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ebook_id")
    private Long ebookId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore // To prevent serialization loops
    private Book book;

    @Column(name = "file_location")
    private String fileLocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ebook ebook = (Ebook) o;
        return ebookId != null && ebookId.equals(ebook.ebookId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
