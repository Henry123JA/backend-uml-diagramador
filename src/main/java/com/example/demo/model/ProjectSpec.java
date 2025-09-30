package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;       // Hibernate 6
import org.hibernate.type.SqlTypes;


@Entity
@Data
public class ProjectSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode umlJson;

    private String basePackage;
    private String createdBy;

    // getter/setter explícito de id por compatibilidad con tus controllers
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}

