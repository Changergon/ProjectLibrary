package org.example.library.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequestDTO {
    private Long id;
    private String status;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalDueDate;

    // User info
    private Long userId;
    private String username;

    // Book info
    private Long bookId;
    private String bookTitle;

    // PhysicalCopy info
    private Long copyId;
    private int rowNumber;
    private int shelfNumber;
    private int positionNumber;
}
