package org.example.library.repositories;

import org.example.library.models.BookRating;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating, Long> {
    @EntityGraph(attributePaths = {"book", "user"})
    Optional<BookRating> findByBookBookIdAndUserUserId(Long bookId, Long userId);

    @EntityGraph(attributePaths = {"book", "user"})
    List<BookRating> findByBookBookId(Long bookId);

}
