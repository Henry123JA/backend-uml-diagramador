package com.example.demo.service;

import com.example.demo.model.UmlClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.*;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

@Service
public class CodeGenService {

    private final Configuration cfg;

    public CodeGenService() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public File generateFromJson(String umlJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UmlClass[] classes = mapper.readValue(umlJson, UmlClass[].class);

        // Ajustar paquete según tu proyecto
        String basePackage = "com.example.demo";

        Path baseDir = Paths.get("generated-backend/src/main/java/" + basePackage.replace(".", "/"));
        Path modelDir = baseDir.resolve("model");
        Path repoDir = baseDir.resolve("repository");
        Path serviceDir = baseDir.resolve("service");
        Path controllerDir = baseDir.resolve("controller");
        Path resourcesDir = Paths.get("generated-backend/src/main/resources");

        Files.createDirectories(modelDir);
        Files.createDirectories(repoDir);
        Files.createDirectories(serviceDir);
        Files.createDirectories(controllerDir);
        Files.createDirectories(resourcesDir);

        for (UmlClass c : classes) {
            Map<String, Object> data = new HashMap<>();
            data.put("className", c.getName());
            data.put("attributes", c.getAttributes());
            data.put("basePackage", basePackage);

            // Entity
            renderTemplate("Entity.ftl", data, modelDir.resolve(c.getName() + ".java"));

            // Repository
            renderTemplate("Repository.ftl", data, repoDir.resolve(c.getName() + "Repository.java"));

            // Service
            renderTemplate("Service.ftl", data, serviceDir.resolve(c.getName() + "Service.java"));

            // Controller
            renderTemplate("Controller.ftl", data, controllerDir.resolve(c.getName() + "Controller.java"));
        }

        // application.properties (una sola vez)
        Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put("dbName", "mydb");
        dbConfig.put("dbUser", "postgres");
        dbConfig.put("dbPassword", "8960406");

        renderTemplate(
                "application.properties.ftl",
                dbConfig,
                resourcesDir.resolve("application.properties"));

        renderTemplate(
                "GeneratedBackendApplication.ftl",
                Map.of("basePackage", basePackage),
                baseDir.resolve("GeneratedBackendApplication.java")
        );
        // pom.xml
        Map<String, Object> pomData = new HashMap<>();
        pomData.put("basePackage", basePackage);
        pomData.put("artifactId", "generated-backend");
        renderTemplate("pom.ftl", pomData, Paths.get("generated-backend/pom.xml"));

        // Comprimir en ZIP
        File zipFile = new File("backend.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Files.walk(Paths.get("generated-backend"))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            zos.putNextEntry(new ZipEntry(Paths.get("generated-backend").relativize(path).toString()));
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }

        return zipFile;
    }

    private void renderTemplate(String templateName, Map<String, Object> data, Path outputPath) throws IOException {
        try (Writer out = new FileWriter(outputPath.toFile())) {
            Template template = cfg.getTemplate(templateName);
            try {
                template.process(data, out);
            } catch (TemplateException e) {
                throw new IOException("Error procesando template: " + templateName, e);
            }
        }
    }

    public File generateSQLFromJson(String umlJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UmlClass[] classes = mapper.readValue(umlJson, UmlClass[].class);

        Map<String, Object> data = new HashMap<>();
        data.put("classes", Arrays.asList(classes));

        Path outputPath = Paths.get("schema.sql");
        renderTemplate("schema.ftl", data, outputPath);

        return outputPath.toFile();
    }

}
