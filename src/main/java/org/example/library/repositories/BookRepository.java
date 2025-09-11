package org.example.library.repositories;

import org.example.library.models.Book;
import org.example.library.models.BookStatus;
import org.example.library.models.Faculty;
import org.example.library.models.FacultyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Overriding findById to fetch all necessary associations
    @Override
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties", "entry", "entry.addedBy"})
    Optional<Book> findById(Long bookId);

    // --- Methods returning Page<Book> with @EntityGraph to solve N+1 problem ---

    @Override
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByTitleContaining(String title, Pageable pageable);

    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByBookAuthors_Author_LastNameContaining(String author, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.bookAuthors ba JOIN ba.author a WHERE b.title LIKE %:title% AND a.lastName LIKE %:author%")
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByTitleContainingAndBookAuthors_Author_LastNameContaining(String title, String author, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.bookAuthors ba JOIN ba.author a WHERE b.title LIKE %:query% OR a.firstName LIKE %:query% OR a.lastName LIKE %:query%")
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByTitleContainingOrBookAuthors_Author_FirstNameContainingOrBookAuthors_Author_LastNameContaining(String query, Pageable pageable);

    @Query("SELECT be.book FROM BookEntry be WHERE be.addedBy.userId = :userId")
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByAddedById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.bookAuthors ba WHERE ba.author.authorId = :authorId")
    @EntityGraph(attributePaths = {"bookAuthors", "bookAuthors.author", "faculties"})
    Page<Book> findByBookAuthors_Author_AuthorId(@Param("authorId") Long authorId, Pageable pageable);


    // --- Methods returning List<Book> with JOIN FETCH to solve N+1 problem ---

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author WHERE b.status = :status")
    List<Book> findRentedBooks(@Param("status") BookStatus status);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author JOIN b.faculties f WHERE f = :faculty")
    List<Book> findByFaculty(@Param("faculty") Faculty faculty);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author JOIN b.faculties f WHERE f.type = :facultyType")
    List<Book> findByFacultiesType(@Param("facultyType") FacultyType facultyType);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author JOIN b.faculties f WHERE f IN :faculties")
    List<Book> findByFacultiesIn(@Param("faculties") Set<Faculty> faculties);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author JOIN b.faculties f WHERE f.type = 'COMMON'")
    List<Book> findCommonBooks();
}
