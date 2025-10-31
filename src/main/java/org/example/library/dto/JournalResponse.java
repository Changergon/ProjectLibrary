package org.example.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class JournalResponse extends JournalRequest {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
