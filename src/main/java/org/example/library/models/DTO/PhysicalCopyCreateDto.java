package org.example.library.models.DTO;

import lombok.Data;

@Data
public class PhysicalCopyCreateDto {
    private Long bookId;
    private int rowNumber;
    private int shelfNumber;
    private int positionNumber;
}
