package org.example.library.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAccessId implements Serializable {
    private Long userId;
    private Long bookId;
}
