package com.example.demo.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UmlClass {
    private String name;
    private List<UmlAttribute> attributes;

}

