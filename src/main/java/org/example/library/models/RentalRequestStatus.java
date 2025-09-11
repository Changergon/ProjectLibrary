package org.example.library.models;

public enum RentalRequestStatus {
    PENDING,         // Ожидание
    APPROVED,        // Одобрено, готово к выдаче
    REJECTED,        // Отклонено
    ACTIVE,          // Книга на руках у пользователя
    RETURNED,        // Книга возвращена
    COMPLETED        // Аренда успешно завершена
}
