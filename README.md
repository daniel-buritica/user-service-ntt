# Servicio de usuario


Este proyecto implementa un microservicio REST para consultar la información básica de un usuario.

## Características
- Desarrollado con Spring Boot y Maven para construir un microservicio web reactivo.
- Implementa un endpoint RESTful para consultar un usuario por tipo y número de documento.
- Maneja apropiadamente códigos de estado HTTP en las respuestas.
- Incluye pruebas unitarias con JUnit y Mockito.
- Usa una arquitectura limpia y modular para mantener desacoplados los componentes.
- Contiene configuración de Docker para desplegar como contenedor.

## Requerimientos
- Java 11.0.20
- Web flux
- Maven
- Docker (opcional)

## Ejecución
El proyecto se puede ejecutar de diferentes formas:

### Local
```bash
mvn spring-boot:run
```
- Se levantará en http://localhost:8090.
### Empaquetado
- Generará un archivo JAR ejecutable en target/.
```bash
mvn clean package
```


### Contenedor Docker
- Construir imagen:
```bash
docker build -t user-service .
```
- Ejecutar contenedor:
```bash
docker run -p 8090:8090 user-service
```
### Swagger
- http://localhost:8090/swagger-doc/webjars/swagger-ui/index.html


### Tecnologías

- Java 11.0.20
- Spring Boot
- Spring Webflux
- JUnit, Mockito
- Maven
- Docker


  
