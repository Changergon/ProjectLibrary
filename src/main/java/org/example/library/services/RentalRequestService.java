package org.example.library.services;

import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.repositories.RentalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalRequestService {

    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    public RentalRequest createRentalRequest(RentalRequest request) {
        request.setStatus(RentalRequestStatus.PENDING); // Используем перечисление
        return rentalRequestRepository.save(request);
    }

    public List<RentalRequest> getRentalRequests() {
        return rentalRequestRepository.findAll();
    }

    public void approveRentalRequest(Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setStatus(RentalRequestStatus.APPROVED); // Используем перечисление
            rentalRequestRepository.save(request);
        }
    }

    public List<RentalRequest> getRentedBooks() {
        // Получаем только одобренные запросы
        return rentalRequestRepository.findByStatus(RentalRequestStatus.APPROVED); // Предполагается, что метод есть в репозитории
    }
}
