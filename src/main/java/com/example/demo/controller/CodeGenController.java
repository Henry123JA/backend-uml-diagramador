package com.example.demo.controller;

import com.example.demo.service.OpenAIService;
import com.example.demo.service.CodeGenService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/codegen")
public class CodeGenController {

    private final OpenAIService openAIService;
    private final CodeGenService codeGenService;

    public CodeGenController(OpenAIService openAIService, CodeGenService codeGenService) {
        this.openAIService = openAIService;
        this.codeGenService = codeGenService;
    }

    // 1️⃣ Generar backend (entidades en .zip)
    @PostMapping("/generate-backend")
    public ResponseEntity<FileSystemResource> generateBackend(@RequestBody String umlJson) throws Exception {
        String code = openAIService.generateEntitiesFromJson(umlJson, "com.example.demo.model");
        File zipFile = codeGenService.saveCodeAsZip(code, "com.example.demo");
        return buildFileResponse(zipFile, "backend.zip");
    }

    // 2️⃣ Generar script SQL (DDL)
    @PostMapping("/generate-sql")
    public ResponseEntity<FileSystemResource> generateSql(@RequestBody String umlJson) throws Exception {
        String sql = openAIService.generateSqlSchemaFromJson(umlJson);
        File sqlFile = codeGenService.saveTextAsFile(sql, "script.sql");
        return buildFileResponse(sqlFile, "script.sql");
    }

    // 3️⃣ Generar script SQL con inserts
    @PostMapping("/generate-sql-inserts")
    public ResponseEntity<FileSystemResource> generateSqlWithInserts(@RequestBody String umlJson,
                                                                     @RequestParam(defaultValue = "4") int rows) throws Exception {
        String sql = openAIService.generateSqlInsertsFromJson(umlJson, rows);
        File sqlFile = codeGenService.saveTextAsFile(sql, "script_inserts.sql");
        return buildFileResponse(sqlFile, "script_inserts.sql");
    }

    // 4️⃣ Generar XML draw.io
    @PostMapping("/generate-xml")
    public ResponseEntity<FileSystemResource> generateXml(@RequestBody String umlJson) throws Exception {
        String xml = openAIService.generateDrawioXmlFromJson(umlJson);
        File xmlFile = codeGenService.saveTextAsFile(xml, "diagram.drawio.xml");
        return buildFileResponse(xmlFile, "diagram.drawio.xml");
    }

    // 🔹 Método utilitario para devolver archivos
    private ResponseEntity<FileSystemResource> buildFileResponse(File file, String downloadName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(downloadName)
                .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(new FileSystemResource(file), headers, HttpStatus.OK);
    }
}
