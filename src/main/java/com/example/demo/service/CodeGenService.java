package com.example.demo.service;

import freemarker.template.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

@Service
public class CodeGenService {

    private final Configuration cfg;

    public CodeGenService() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public File saveCodeAsZip(String entitiesCode, String basePackage) throws Exception {
        // 📂 Carpeta base generada
        Path baseDir = Paths.get("generated-backend/src/main/java/" + basePackage.replace(".", "/"));
        Path resourcesDir = Paths.get("generated-backend/src/main/resources");
        Path repoDir = baseDir.resolve("repository");
        Path serviceDir = baseDir.resolve("service");
        Path controllerDir = baseDir.resolve("controller");
        Path modelDir = baseDir.resolve("model");


        // 🔄 Borrar si ya existía
        Path genDir = Paths.get("generated-backend");
        if (Files.exists(genDir)) {
            Files.walk(genDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        Files.createDirectories(modelDir);
        Files.createDirectories(repoDir);
        Files.createDirectories(serviceDir);
        Files.createDirectories(controllerDir);
        Files.createDirectories(resourcesDir);

        // 1️⃣ Guardar Entities directamente del código de OpenAI
        saveEntities(entitiesCode, modelDir);

        // 2️⃣ Generar demás archivos desde templates .ftl
        renderTemplate("application.properties.ftl",
                Map.of("dbName", "mydb", "dbUser", "postgres", "dbPassword", "8960406"),
                resourcesDir.resolve("application.properties"));

        renderTemplate("GeneratedBackendApplication.ftl",
                Map.of("basePackage", basePackage),
                baseDir.resolve("GeneratedBackendApplication.java"));

        renderTemplate("pom.ftl",
                Map.of("basePackage", basePackage, "artifactId", "generated-backend"),
                Paths.get("generated-backend/pom.xml"));

        // 🔄 Recorrer entidades y generar repos, services, controllers
        // ⚠️ Aquí puedes parsear los nombres de las clases de "entitiesCode"
        List<String> classNames = extractClassNames(entitiesCode);

        renderTemplate("readme.md.ftl",
                Map.of("classNames", classNames),
                Paths.get("generated-backend/README.md"));

        // 🔄 Recorrer entidades y generar repos, services, controllers
        for (String className : classNames) {
            Map<String, Object> data = Map.of(
                    "basePackage", basePackage,
                    "className", className
            );

            renderTemplate("Repository.ftl", data, repoDir.resolve(className + "Repository.java"));
            renderTemplate("Service.ftl", data, serviceDir.resolve(className + "Service.java"));
            renderTemplate("Controller.ftl", data, controllerDir.resolve(className + "Controller.java"));
        }

        // 3️⃣ Empaquetar en ZIP
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

    private void saveEntities(String entitiesCode, Path modelDir) throws IOException {
        // ⚠️ Este método separa cada bloque ```java ... ``` en un archivo .java
        String[] blocks = entitiesCode.split("```java");
        for (String block : blocks) {
            if (!block.contains("class")) continue;
            String code = block.replace("```", "").trim();

            // Extraer nombre de clase
            String className = extractClassName(code);
            Files.writeString(modelDir.resolve(className + ".java"), code);
        }
    }

    private String extractClassName(String code) {
        for (String line : code.split("\n")) {
            if (line.contains("class ")) {
                return line.split("class ")[1].split(" ")[0].trim();
            }
        }
        return "UnknownEntity";
    }

    private List<String> extractClassNames(String entitiesCode) {
        List<String> names = new ArrayList<>();
        for (String block : entitiesCode.split("```java")) {
            if (block.contains("class ")) {
                names.add(extractClassName(block));
            }
        }
        return names;
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

    public File saveTextAsFile(String content, String fileName) throws Exception {
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}
