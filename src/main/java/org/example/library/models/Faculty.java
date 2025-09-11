package org.example.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "faculties")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faculty_id")
    private Long facultyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FacultyType type;

    @ManyToMany(mappedBy = "faculties")
    @JsonIgnore
    private Set<Book> books;

    @ManyToMany(mappedBy = "faculties")
    @JsonIgnore
    private Set<LibraryUser> users;

    @Override
    public int hashCode() {
        return facultyId != null ? facultyId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Faculty faculty = (Faculty) obj;

        return Objects.equals(facultyId, faculty.facultyId);
    }
}
