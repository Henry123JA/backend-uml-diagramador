package com.example.demo.controller;

import com.example.demo.model.Accesorio;
import com.example.demo.service.AccesorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesorio")
public class AccesorioController {
    private final AccesorioService service;

    public AccesorioController(AccesorioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Accesorio> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Accesorio> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Accesorio create(@RequestBody Accesorio entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Accesorio> update(@PathVariable Long id, @RequestBody Accesorio entity) {
        return service.findById(id)
                .map(existing -> {
                    entity.setId(id);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
