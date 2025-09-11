package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_access")
public class BookAccess {

    @EmbeddedId
    private BookAccessId id;

    @Column(name = "access")
    private boolean access; // true, если доступ разрешен, false - запрещен

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private LibraryUser libraryUser;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAccess that = (BookAccess) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
