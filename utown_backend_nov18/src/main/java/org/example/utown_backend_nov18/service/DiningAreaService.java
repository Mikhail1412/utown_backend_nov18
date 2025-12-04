package org.example.utown_backend_nov18.service;

import org.example.utown_backend_nov18.model.DiningArea;
import org.example.utown_backend_nov18.repository.DiningAreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiningAreaService {

    private final DiningAreaRepository repo;

    public DiningAreaService(DiningAreaRepository repo) {
        this.repo = repo;
    }

    public List<DiningArea> findAll() {
        return repo.findAll();
    }

    public Optional<DiningArea> findById(Long id) {
        return repo.findById(id);
    }

    public DiningArea save(DiningArea area) {
        return repo.save(area);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
