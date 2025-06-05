package org.example.library.repositories;

import org.example.library.models.PhysicalCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalCopyRepository extends JpaRepository<PhysicalCopy, Long> {
    // при необходимости добавьте методы
}
