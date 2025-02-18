package org.example.library.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
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
    @MapsId("userId") // Указываем, что userId в BookAccessId соответствует userId в LibraryUser
    @JoinColumn(name = "user_id")
    private LibraryUser  libraryUser;

    @ManyToOne
    @MapsId("bookId") // Указываем, что bookId в BookAccessId соответствует bookId в Book
    @JoinColumn(name = "book_id")
    private Book book;

}
