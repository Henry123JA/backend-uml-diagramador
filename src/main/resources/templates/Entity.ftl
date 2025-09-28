package ${basePackage}.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ${className} {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<#list attributes as attr>
    private ${attr.type} ${attr.name};
</#list>
}
