package org.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class JournalRequest {

    @NotBlank
    @Length(min = 1, max = 255)
    private String title;

    @Length(max = 255)
    private String publisher;

    @Min(1450)
    @Max(2100)
    private Integer publicationYear;

    @Min(1)
    private Integer issueNumber;
}
