package com.example.demo.service;

import com.example.demo.model.Accesorio;
import com.example.demo.repository.AccesorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccesorioService {
    private final AccesorioRepository repository;

    public AccesorioService(AccesorioRepository repository) {
        this.repository = repository;
    }

    public List<Accesorio> findAll() {
        return repository.findAll();
    }

    public Optional<Accesorio> findById(Long id) {
        return repository.findById(id);
    }

    public Accesorio save(Accesorio entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
