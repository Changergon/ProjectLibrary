package org.example.library.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalCopyDTO {
    // From PhysicalCopy
    private Long copyId;
    private boolean available;
    private int rowNumber;
    private int shelfNumber;
    private int positionNumber;

    // From Book
    private Long bookId;
    private String bookTitle;
    private List<String> authorNames;
}
