package com.pavelmakhov.booapp.domain.service;

import com.pavelmakhov.booapp.domain.model.Provider;
import com.pavelmakhov.booapp.domain.model.TimeSlot;
import com.pavelmakhov.booapp.domain.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    private final ProviderRepository repository;

    public ProviderService(ProviderRepository repository) {
        this.repository = repository;
    }

    public List<Provider> findAll() {
        return repository.findAll();
    }

    public List<Provider> findByOccupation(String occupation) {
        return repository.findAllByOccupationIgnoreCase(occupation);
    }
}
