# Puerto del servidor
server.port=8080

# Configuración PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/${dbName}
spring.datasource.username=${dbUser}
spring.datasource.password=${dbPassword}

# Driver PostgreSQL
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
