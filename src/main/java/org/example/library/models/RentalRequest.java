package org.example.library.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_requests")
public class RentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private LibraryUser  user;

    @ManyToOne
    @JoinColumn(name = "physical_copy_id", nullable = false)
    private PhysicalCopy physicalCopy;

    @Column(name = "rental_start_date")
    private LocalDateTime rentalStartDate;

    @Column(name = "rental_due_date")
    private LocalDateTime rentalDueDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RentalRequestStatus status; // Изменено с String на RentalRequestStatus
}
