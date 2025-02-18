package org.example.library.models.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private List<String> roles; // Add this field to store the user's roles

    // Add other necessary fields if needed
}
