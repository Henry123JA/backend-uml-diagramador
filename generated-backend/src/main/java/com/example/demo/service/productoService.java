package com.example.demo.service;

import com.example.demo.model.producto;
import com.example.demo.repository.productoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class productoService {

    private final productoRepository repository;

    public productoService(productoRepository repository) {
        this.repository = repository;
    }

    public List<producto> findAll() {
        return repository.findAll();
    }

    public Optional<producto> findById(Long id) {
        return repository.findById(id);
    }

    public producto save(producto entity) {
        return repository.save(entity);
    }

    public producto update(Long id, producto entity) {
        entity.setId(id);
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
