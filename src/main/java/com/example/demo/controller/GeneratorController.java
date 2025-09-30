package com.example.demo.controller;

import com.example.demo.service.CodeGenService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/api/generator")
public class GeneratorController {

    private final CodeGenService codeGenService;

    public GeneratorController(CodeGenService codeGenService) {
        this.codeGenService = codeGenService;
    }
/*
    @PostMapping("/backend")
    public ResponseEntity<Resource> generateJson(@RequestBody String payload) throws IOException {
        File zipFile = codeGenService.generateFromPayload(payload); // <-- NUEVO
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"backend.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/sql")
    public ResponseEntity<Resource> generateSQL(@RequestBody String payload) throws IOException {
        File sqlFile = codeGenService.generateSQLFromPayload(payload); // <-- NUEVO
        InputStreamResource resource = new InputStreamResource(new FileInputStream(sqlFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"schema.sql\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }*/
}
