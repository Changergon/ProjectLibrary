package org.example.library.models.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long bookId;
    private String title;
    private String isbn;
    private int publicationYear;
    private String description;
    private String publisher;
    private String status; // Можно использовать строку, чтобы избежать проблем с сериализацией
    private List<String> authorNames; // Список имен авторов
}
