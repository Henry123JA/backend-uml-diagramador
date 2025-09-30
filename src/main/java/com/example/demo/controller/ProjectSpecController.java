package com.example.demo.controller;

import com.example.demo.model.ProjectSpec;
import com.example.demo.repository.ProjectSpecRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


        import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectSpecController {

    private final ProjectSpecRepository repository;

    public ProjectSpecController(ProjectSpecRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProjectSpec> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ProjectSpec create(@RequestBody ProjectSpec projectSpec) {
        return repository.save(projectSpec);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSpec> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectSpec> update(@PathVariable Long id, @RequestBody ProjectSpec updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setProjectName(updated.getProjectName());
                    existing.setUmlJson(updated.getUmlJson());
                    existing.setBasePackage(updated.getBasePackage());
                    existing.setCreatedBy(updated.getCreatedBy());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(project -> {
                    repository.delete(project);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
