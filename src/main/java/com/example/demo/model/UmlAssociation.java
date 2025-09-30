package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UmlAssociation {
    // "association" | "aggregation" | "composition" | "inheritance"
    private String type;

    private String sourceClassName;
    private String targetClassName;

    private String sourceMultiplicity; // "1", "0..1", "1..*", "0..*", "*"
    private String targetMultiplicity; // idem

    private String role; // opcional, solo informativo
}
