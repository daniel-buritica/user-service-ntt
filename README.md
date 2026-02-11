# User Service - Microservicio REST con CI/CD y GitOps

Este proyecto implementa un microservicio REST para consultar la información básica de un usuario, con un pipeline completo de CI/CD usando GitHub Actions y despliegue automatizado mediante GitOps con ArgoCD.

## Características

- **Arquitectura Hexagonal**: Implementación con Clean Architecture para mantener desacoplados los componentes
- **Spring Boot 3.2.0**: Framework reactivo con WebFlux
- **Java 21**: Última versión LTS
- **CI/CD Completo**: Pipeline automatizado con GitHub Actions
- **GitOps**: Despliegue automatizado con ArgoCD
- **Containerización**: Docker y Helm Charts para Kubernetes
- **Testing**: Pruebas unitarias con cobertura ≥80%
- **Análisis de Seguridad**: CodeQL, Trivy, SBOM

## Requisitos

- **Java 21** (LTS)
- **Maven 3.9+**
- **Docker** (para construcción de imágenes)
- **Kubernetes** con ArgoCD instalado (para despliegue)
- **Cuenta de DockerHub o Quay.io** (para registry de imágenes)

## Arquitectura

El proyecto sigue una **Arquitectura Hexagonal (Clean Architecture)** con las siguientes capas:

- **Domain Layer**: Lógica de negocio pura (modelos, casos de uso)
- **Application Layer**: Orquestación y configuración
- **Infrastructure Layer**: Adaptadores (REST, servicios externos)

Para más detalles, consulta [docs/arquitectura.md](docs/arquitectura.md)

## 🚀 Ejecución Local

### Desarrollo

```bash
# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8090`

### Empaquetado

```bash
# Generar JAR ejecutable
mvn clean package
```

El JAR se generará en: `target/client-service-1.0-SNAPSHOT.jar`

### Contenedor Docker

```bash
# Construir imagen
docker build -t user-service -f deployment/Dockerfile .

# Ejecutar contenedor
docker run -p 8090:8090 user-service
```

### Swagger/OpenAPI

Documentación de la API disponible en:
- Swagger UI: `http://localhost:8090/swagger-doc/swagger-ui.html`
- API Docs: `http://localhost:8090/swagger-doc/v3/api-docs`

## Pipeline CI/CD

El proyecto incluye un pipeline completo de CI/CD que se ejecuta automáticamente en:

- **Push a la rama `main`**
- **Aprobación de Pull Requests** hacia `main`

### Jobs del Pipeline

1. **Test**: Pruebas unitarias con validación de cobertura ≥80%
2. **Static Analysis**: Análisis estático con CodeQL (y SonarQube opcional)
3. **Dynamic Analysis**: Escaneo de vulnerabilidades con Trivy
4. **Composition Analysis**: Generación de SBOM con Syft
5. **Build & Push**: Construcción y publicación de imagen Docker
6. **Update GitOps**: Actualización automática de `values.yaml` con nuevo tag

### Configuración

Para configurar el pipeline, consulta [CONFIGURACION.md](CONFIGURACION.md)

## Despliegue con GitOps

El proyecto utiliza **GitOps** con ArgoCD para el despliegue automatizado:

1. El pipeline actualiza `helm/myapp/values.yaml` con el nuevo tag de imagen
2. ArgoCD detecta el cambio en Git
3. ArgoCD sincroniza automáticamente el despliegue en Kubernetes

### Helm Chart

El proyecto incluye un Helm Chart completo en `helm/myapp/`:

```bash
# Instalar el chart
helm install myapp ./helm/myapp

# Actualizar el chart
helm upgrade myapp ./helm/myapp
```

### ArgoCD Application

Aplicar el manifest de ArgoCD:

```bash
kubectl apply -f argocd/application.yaml
```

## Testing

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

### Cobertura Mínima

El proyecto requiere una cobertura mínima del **80%**. El pipeline fallará si no se cumple este requisito.

## 📚 Estructura del Proyecto

```
user-service-ntt-main/
├── .github/
│   ├── workflows/
│   │   └── triggerci.yml      # Pipeline CI/CD
│   └── CODEOWNERS             # Configuración de code owners
├── src/
│   ├── main/
│   │   ├── java/              # Código fuente
│   │   │   └── co/com/prueba/
│   │   │       ├── Application.java
│   │   │       ├── config/    # Configuración Spring
│   │   │       ├── model/     # Modelos y entidades
│   │   │       ├── usecase/   # Casos de uso
│   │   │       ├── entrypoints/  # REST API (handlers, routers)
│   │   │       └── adapter/   # Adaptadores externos
│   │   └── resources/         # Recursos (application.yml, etc.)
│   └── test/
│       └── java/              # Tests unitarios
├── deployment/
│   └── Dockerfile             # Dockerfile para la aplicación
├── helm/
│   └── myapp/                # Helm Chart
│       ├── Chart.yaml
│       ├── values.yaml
│       └── templates/
├── argocd/
│   └── application.yaml      # Manifest de ArgoCD
├── docs/
│   └── arquitectura.md       # Documentación de arquitectura
├── pom.xml                    # POM único (mono-módulo)
└── README.md
```

## Configuración

### Variables de Entorno

La aplicación puede configurarse mediante variables de entorno:

- `PORT`: Puerto de la aplicación (default: 8090)
- `ORIGIN`: Origen permitido para CORS (default: *)

### Secrets de GitHub Actions

Configura los siguientes secrets en GitHub:

- `DOCKERHUB_USERNAME`: Usuario de DockerHub
- `DOCKERHUB_TOKEN`: Token de acceso de DockerHub
- `QUAY_USERNAME`: Usuario de Quay.io (alternativa)
- `QUAY_TOKEN`: Token de acceso de Quay.io (alternativa)
- `SONAR_TOKEN`: Token de SonarQube (opcional)
- `SONAR_HOST_URL`: URL de SonarQube (opcional)
- `SONAR_ORGANIZATION`: Organización de SonarQube (opcional)

## Documentación

- [Guía de Configuración](CONFIGURACION.md) - Pasos detallados para configurar el proyecto
- [Arquitectura](docs/arquitectura.md) - Documentación completa de la arquitectura
- [Plan de Implementación](PLAN.md) - Plan detallado del proyecto
- [Tareas](task.md) - Requerimientos del ejercicio

## 🛠️ Tecnologías

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Framework reactivo)
- **Maven** (Gestión de dependencias)
- **Lombok** (Reducción de boilerplate)

### Testing
- **JUnit 5**
- **Mockito**
- **JaCoCo** (Cobertura de código)

### CI/CD
- **GitHub Actions**
- **Docker**
- **Helm**
- **ArgoCD**

### Análisis y Seguridad
- **CodeQL** (Análisis estático)
- **Trivy** (Análisis dinámico)
- **Syft** (SBOM)





### Code Owners

- **@daniel-buritica** - Owner principal del proyecto
