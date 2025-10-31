package org.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class BookRequest {

    @NotBlank
    @Length(min = 1, max = 255)
    private String title;

    @NotBlank
    @Length(min = 1, max = 255)
    private String author;

    @NotNull
    @Pattern(regexp = "^[0-9-]{10,17}$")
    private String isbn;

    @Length(max = 2000)
    private String description;

    @Min(1450)
    @Max(2100)
    private Integer publicationYear;

    @Min(1)
    private Integer pageCount;
}
