package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
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
    @JsonIgnore
    private Book book;

    @Column(name = "available")
    private boolean available;

    @Column(name = "row_number")
    private int rowNumber;

    @Column(name = "shelf_number")
    private int shelfNumber;

    @Column(name = "position_number")
    private int positionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhysicalCopy that = (PhysicalCopy) o;
        return copyId != null && copyId.equals(that.copyId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
