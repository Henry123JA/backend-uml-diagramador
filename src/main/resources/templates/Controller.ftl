package ${basePackage}.controller;

import ${basePackage}.model.${className};
import ${basePackage}.service.${className}Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${className?lower_case}s")
public class ${className}Controller {

    private final ${className}Service service;

    public ${className}Controller(${className}Service service) {
        this.service = service;
    }

    @GetMapping
    public List<${className}> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ${className} findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public ${className} save(@RequestBody ${className} entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ${className} update(@PathVariable Long id, @RequestBody ${className} entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
