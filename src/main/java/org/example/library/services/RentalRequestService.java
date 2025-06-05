package org.example.library.services;

import org.example.library.models.PhysicalCopy;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.repositories.PhysicalCopyRepository;
import org.example.library.repositories.RentalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalRequestService {

    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    @Autowired
    private PhysicalCopyRepository physicalCopyRepository;

    public RentalRequest createRentalRequest(RentalRequest request) {
        request.setStatus(RentalRequestStatus.PENDING); // Используем перечисление
        return rentalRequestRepository.save(request);
    }

    public List<RentalRequest> getRentalRequests() {
        return rentalRequestRepository.findAll();
    }

    public void approveRentalRequest(Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId).orElse(null);
        if (request != null && request.getStatus() == RentalRequestStatus.PENDING) {
            request.setStatus(RentalRequestStatus.APPROVED);
            request.setRentalStartDate(LocalDateTime.now());
            request.setRentalDueDate(LocalDateTime.now().plusDays(14));

            // Обновляем статус физической копии
            PhysicalCopy copy = request.getPhysicalCopy();
            if (copy != null) {
                copy.setAvailable(false);
                // Сохраните копию через соответствующий репозиторий (нужно внедрить)
                physicalCopyRepository.save(copy);
            }

            rentalRequestRepository.save(request);
        }
    }

    public List<RentalRequest> getOverdueRentals() {
        LocalDateTime now = LocalDateTime.now();
        return rentalRequestRepository.findByStatus(RentalRequestStatus.APPROVED).stream()
                .filter(request -> request.getRentalDueDate() != null && request.getRentalDueDate().isBefore(now))
                .collect(Collectors.toList());
    }



    public List<RentalRequest> getRentedBooks() {
        // Получаем только одобренные запросы
        return rentalRequestRepository.findByStatus(RentalRequestStatus.APPROVED); // Предполагается, что метод есть в репозитории
    }

    public void returnBook(Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId).orElse(null);
        if (request != null && request.getStatus() == RentalRequestStatus.APPROVED) {
            // Устанавливаем статус заявки на REJECTED или можем удалить её
            request.setStatus(RentalRequestStatus.REJECTED); // Или другой статус по вашему выбору
            rentalRequestRepository.save(request);

            // Делаем физическую копию доступной
            PhysicalCopy copy = request.getPhysicalCopy();
            if (copy != null) {
                copy.setAvailable(true);
                physicalCopyRepository.save(copy);
            }
        }
    }


    public List<PhysicalCopy> getAvailableCopies() {
        return physicalCopyRepository.findAll().stream()
                .filter(PhysicalCopy::isAvailable)
                .collect(Collectors.toList());
    }


    public List<RentalRequest> getAllRequests() {
        return rentalRequestRepository.findAll();
    }
}
