package com.example.demo.controller;

import com.example.demo.model.producto;
import com.example.demo.service.productoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class productoController {

    private final productoService service;

    public productoController(productoService service) {
        this.service = service;
    }

    @GetMapping
    public List<producto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public producto findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public producto save(@RequestBody producto entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public producto update(@PathVariable Long id, @RequestBody producto entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
