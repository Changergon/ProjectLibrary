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
    private String status;
    private List<String> authorNames;
    private double averageRating; // Добавлено среднее значение рейтинга
    private Integer userRating; // Рейтинг текущего пользователя (если есть)
}
