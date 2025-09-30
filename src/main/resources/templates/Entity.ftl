<#-- Entity.ftl -->
package ${basePackage}.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "${className?lower_case}")
<#if parentClass??>
@DiscriminatorValue("${className}")
<#elseif hasChildren?? && hasChildren>
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("${className}")
</#if>
public class ${className}<#if parentClass??> extends ${parentClass}</#if> {

<#if !parentClass??>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
</#if>

    <#-- Atributos básicos -->
    <#if attributes?? && attributes?size gt 0>
    <#list attributes as attr>
    private ${(attr.type)!'String'} ${(attr.name)!'campo${attr?index}'};
    </#list>
    </#if>

    <#-- Relaciones JPA -->
    <#if relations?? && relations?size gt 0>
    <#list relations as rel>
    <#assign jpaAnnotation = (rel.jpaAnnotation)!'@ManyToOne'>
    <#assign fieldType = (rel.otherType)!'Object'>
    <#assign fieldName = (rel.fieldName)!(fieldType?lower_case)>
    <#assign isCollection = (rel.collection)?? && rel.collection == true>

    <#-- Solo cascade, sin orphanRemoval ni mappedBy complicados -->
    <#if (rel.cascade)?? && rel.cascade?length gt 0>
    ${jpaAnnotation}(cascade = ${rel.cascade})
    <#else>
    ${jpaAnnotation}
    </#if>

    <#if (rel.joinAnnotation)?? && rel.joinAnnotation?length gt 0>
    ${rel.joinAnnotation}
    </#if>

    <#if isCollection>
    private Set<${fieldType}> ${fieldName} = new HashSet<>();
    <#else>
    private ${fieldType} ${fieldName};
    </#if>
    </#list>
    </#if>

    // Constructor por defecto
    public ${className}() {}

<#if !parentClass??>
    // Getters y Setters para ID
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
</#if>

    <#-- Getters y Setters para atributos -->
    <#if attributes?? && attributes?size gt 0>
    <#list attributes as attr>
    <#assign attrType = (attr.type)!'String'>
    <#assign attrName = (attr.name)!'campo${attr?index}'>
    <#assign capitalizedName = attrName?cap_first>

    public ${attrType} get${capitalizedName}() { return ${attrName}; }
    public void set${capitalizedName}(${attrType} ${attrName}) { this.${attrName} = ${attrName}; }
    </#list>
    </#if>

    <#-- Getters y Setters para relaciones -->
    <#if relations?? && relations?size gt 0>
    <#list relations as rel>
    <#assign fieldType = (rel.otherType)!'Object'>
    <#assign fieldName = (rel.fieldName)!(fieldType?lower_case)>
    <#assign capitalizedName = fieldName?cap_first>
    <#assign isCollection = (rel.collection)?? && rel.collection == true>

    <#if isCollection>
    public Set<${fieldType}> get${capitalizedName}() { return ${fieldName}; }
    public void set${capitalizedName}(Set<${fieldType}> ${fieldName}) { this.${fieldName} = ${fieldName}; }
    <#else>
    public ${fieldType} get${capitalizedName}() { return ${fieldName}; }
    public void set${capitalizedName}(${fieldType} ${fieldName}) { this.${fieldName} = ${fieldName}; }
    </#if>
    </#list>
    </#if>
}