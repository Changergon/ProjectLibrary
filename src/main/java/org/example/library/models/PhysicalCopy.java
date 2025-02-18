package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "physical_copies")
public class PhysicalCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "copy_id")
    private Long copyId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "available")
    private boolean available;

    @Column(name = "row_number")
    private int rowNumber;

    @Column(name = "shelf_number")
    private int shelfNumber;

    @Column(name = "position_number")
    private int positionNumber;
}
