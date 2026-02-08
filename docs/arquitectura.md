# 📐 Arquitectura del Proyecto - User Service

## 📋 Resumen Ejecutivo

Este proyecto implementa un microservicio REST para gestión de usuarios utilizando una **Arquitectura Hexagonal (Clean Architecture)** con Spring Boot 3.2.0 y Java 21. El proyecto incluye un pipeline completo de CI/CD con GitHub Actions y despliegue automatizado mediante GitOps con ArgoCD.

---

## 🏗️ Arquitectura de la Aplicación

### Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue los principios de la Arquitectura Hexagonal, también conocida como Clean Architecture, que separa la lógica de negocio de los detalles de implementación.

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                      │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │   Entry Points        │      │  Driven Adapters     │    │
│  │   (REST Controllers)  │      │  (External Services) │    │
│  └──────────────────────┘      └──────────────────────┘    │
│           │                              │                    │
└───────────┼──────────────────────────────┼────────────────────┘
            │                              │
            ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Application Configuration & Orchestration    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
            │                              │
            ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │   Domain Models      │      │   Use Cases          │    │
│  │   (Entities, DTOs)   │      │   (Business Logic)   │    │
│  └──────────────────────┘      └──────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Capas del Proyecto

#### 1. **Domain Layer** (`domain/`)
Contiene la lógica de negocio pura, independiente de frameworks y tecnologías.

- **`domain/model/`**: Modelos de dominio, entidades, DTOs y excepciones
  - `UserRequest`, `UserResponse`
  - `DocumentType`
  - `CustomException`, `CustomAttribute`
  - Interfaces de repositorio (`UserRepository`)

- **`domain/usecase/`**: Casos de uso que implementan la lógica de negocio
  - `UserUseCase`: Lógica para consultar usuarios

#### 2. **Application Layer** (`application/`)
Orquesta los casos de uso y configura la aplicación.

- `Application.java`: Punto de entrada principal
- `ApplicationConfig.java`: Configuración de Spring Boot
- `application.yml`: Configuración de la aplicación

#### 3. **Infrastructure Layer** (`infrastructure/`)
Implementa los adaptadores que conectan la aplicación con el mundo exterior.

- **`entry-points/`**: Puntos de entrada (REST API)
  - `UserRouter`: Configuración de rutas WebFlux
  - `UserHandler`: Manejo de peticiones HTTP
  - `GlobalExceptionHandler`: Manejo centralizado de excepciones

- **`driven-adapters/`**: Adaptadores para servicios externos
  - `UserServiceAdapter`: Implementación del repositorio de usuarios

---

## 🔄 Flujo de Datos

```
Cliente HTTP
    │
    ▼
┌─────────────────┐
│  UserRouter      │  (Entry Point - REST)
│  (WebFlux)       │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  UserHandler     │  (Application Layer)
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  UserUseCase     │  (Domain Layer - Business Logic)
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  UserRepository  │  (Domain Interface)
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│ UserServiceAdapter│ (Infrastructure - Implementation)
└─────────────────┘
```

---

## 🚀 Pipeline CI/CD

### Arquitectura del Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                    GITHUB REPOSITORY                         │
│                                                              │
│  Push to main / PR Approved                                 │
│           │                                                  │
│           ▼                                                  │
└───────────┼────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│              GITHUB ACTIONS WORKFLOW                         │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Test       │  │   Static     │  │   Dynamic    │     │
│  │   (JUnit)    │  │   Analysis   │  │   Analysis   │     │
│  │              │  │   (CodeQL)   │  │   (Trivy)    │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │              │
│         └──────────────────┼──────────────────┘              │
│                            │                                 │
│                            ▼                                 │
│                  ┌──────────────────┐                        │
│                  │  Build & Push   │                        │
│                  │  Docker Image   │                        │
│                  │  (DockerHub/    │                        │
│                  │   Quay.io)      │                        │
│                  └────────┬─────────┘                        │
│                           │                                  │
│                           ▼                                  │
│                  ┌──────────────────┐                        │
│                  │  Update GitOps  │                        │
│                  │  (values.yaml)   │                        │
│                  │  (Artifact)      │                        │
│                  └────────┬─────────┘                        │
└───────────────────────────┼──────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    MANUAL STEP                               │
│                  (Subir values.yaml)                         │
└───────────────────────────┬──────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    GITOPS REPOSITORY                         │
│                  (helm/myapp/values.yaml)                    │
│                                                              │
│  image.tag: <commit-sha>                                     │
└───────────────────────────┬──────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      ARGOCD                                  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Application: user-service                            │  │
│  │  - Monitorea cambios en Git                           │  │
│  │  - Sincroniza automáticamente                         │  │
│  │  - Despliega en Kubernetes                            │  │
│  └────────────────────────┬───────────────────────────────┘  │
└───────────────────────────┼──────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  KUBERNETES CLUSTER                          │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Deployment  │  │   Service    │  │   Ingress    │     │
│  │  (Pods)       │  │  (ClusterIP) │  │  (Optional)  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### Etapas del Pipeline

#### 1. **Test** (Pruebas Unitarias)
- Ejecuta pruebas JUnit
- Genera reporte de cobertura con JaCoCo (objetivo: ≥80%)
- Valida que todas las pruebas pasen
- Publica reportes como artifacts

#### 2. **Static Analysis** (Análisis Estático)
- Ejecuta CodeQL Analysis (automático de GitHub)
- Opcional: SonarQube (si está configurado)
- Detecta code smells y vulnerabilidades
- Publica resultados

#### 3. **Dynamic Analysis** (Análisis Dinámico)
- Construye imagen Docker
- Escanea la imagen con Trivy
- Detecta vulnerabilidades en dependencias
- Publica reportes SARIF en GitHub Security

#### 4. **Composition Analysis** (Análisis de Composición)
- Genera SBOM (Software Bill of Materials) con Syft
- Formato: CycloneDX JSON
- Analiza dependencias y licencias
- Publica SBOM como artifact

#### 5. **Build & Push** (Construcción y Publicación)
- **Solo se ejecuta en push a `main`**
- Construye imagen Docker
- Etiqueta con commit SHA (7 caracteres) y `latest`
- Publica en DockerHub o Quay.io

#### 6. **Update GitOps** (Actualización GitOps)
- **Solo se ejecuta en push a `main`**
- Actualiza `helm/myapp/values.yaml` con el nuevo tag (commit SHA)
- **Genera artifact** con el archivo actualizado (debido a restricción de push)
- Crea summary con instrucciones para subir manualmente

---

## 🔄 Flujo GitOps con ArgoCD

### Principios GitOps

1. **Declarativo**: La configuración deseada se describe en archivos YAML
2. **Versionado**: Todo está en Git
3. **Automático**: ArgoCD detecta cambios y sincroniza
4. **Observable**: Estado visible en el dashboard de ArgoCD

### Flujo de Sincronización

```
1. Pipeline actualiza values.yaml y genera artifact
   │
   ▼
2. Usuario descarga artifact y sube values.yaml a Git
   │
   ▼
3. ArgoCD detecta cambio (polling cada 3 minutos)
   │
   ▼
4. ArgoCD compara estado deseado vs. actual
   │
   ▼
5. ArgoCD aplica cambios (kubectl apply)
   │
   ▼
6. Kubernetes actualiza Deployment
   │
   ▼
7. Nuevos Pods se crean con nueva imagen
   │
   ▼
8. Health checks verifican que la app esté lista
   │
   ▼
9. Service enruta tráfico a nuevos Pods
```

### Configuración de ArgoCD

El archivo `argocd/application.yaml` define:
- **Source**: Repositorio Git y path del Helm chart
- **Destination**: Cluster y namespace de Kubernetes
- **Sync Policy**: Automático con auto-healing
- **Health Checks**: Verificación de salud de la aplicación

---

## 🐳 Containerización

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
COPY application/target/application-1.0-SNAPSHOT.jar app.jar
EXPOSE 8090
ENV PORT=8090
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
```

### Imagen Docker
- **Base**: Eclipse Temurin 21 JDK Alpine (ligera)
- **Puerto**: 8090 (configurable vía variable de entorno PORT)
- **JAR**: `application-1.0-SNAPSHOT.jar`

---

## ☸️ Helm Chart

### Estructura

```
helm/myapp/
├── Chart.yaml              # Metadata del chart
├── values.yaml             # Valores por defecto (actualizado por pipeline)
└── templates/
    ├── deployment.yaml     # Deployment de Kubernetes
    ├── service.yaml        # Service de Kubernetes
    ├── serviceaccount.yaml # Service Account
    └── _helpers.tpl        # Funciones helper
```

### Valores Configurables

- **image.repository**: Registry y nombre de la imagen
- **image.tag**: Tag de la imagen (se actualiza con commit SHA)
- **replicaCount**: Número de réplicas
- **resources**: Límites y requests de CPU/memoria
- **env**: Variables de entorno

---

## 📊 Stack Tecnológico

### Backend
- **Java**: 21 (LTS)
- **Spring Boot**: 3.2.0
- **Spring WebFlux**: Framework reactivo
- **Maven**: Gestión de dependencias
- **Lombok**: Reducción de boilerplate
- **Jackson**: Serialización JSON

### Testing
- **JUnit 5**: Framework de pruebas
- **Mockito**: Mocking
- **JaCoCo**: Cobertura de código (≥80%)

### CI/CD
- **GitHub Actions**: Automatización
- **Docker**: Containerización
- **Helm**: Gestión de Kubernetes
- **ArgoCD**: GitOps

### Infraestructura
- **Kubernetes**: Orquestación de contenedores
- **DockerHub/Quay.io**: Registry de imágenes

### Análisis y Seguridad
- **CodeQL**: Análisis estático
- **Trivy**: Análisis dinámico de vulnerabilidades
- **Syft**: Generación de SBOM

---

## 🔐 Seguridad

### Implementaciones de Seguridad

1. **Análisis Estático**: Detección temprana de vulnerabilidades (CodeQL)
2. **Análisis Dinámico**: Escaneo de imágenes Docker (Trivy)
3. **SBOM**: Inventario de dependencias (Syft)
4. **Secrets Management**: Variables de entorno en Kubernetes
5. **Health Checks**: Verificación continua de salud

---

## 📈 Escalabilidad

### Estrategias de Escalado

1. **Horizontal Pod Autoscaling (HPA)**: Escalado automático basado en CPU/memoria
2. **Replicas**: Múltiples instancias para alta disponibilidad
3. **Load Balancing**: Distribución de carga mediante Service

### Configuración de Recursos

- **Requests**: Recursos garantizados (250m CPU, 256Mi memoria)
- **Limits**: Límites máximos (500m CPU, 512Mi memoria)

---

## 🧪 Testing Strategy

### Niveles de Prueba

1. **Unit Tests**: Pruebas de casos de uso y lógica de negocio
2. **Integration Tests**: Pruebas de endpoints REST
3. **E2E Tests**: Pruebas end-to-end (opcional)

### Cobertura

- **Objetivo**: ≥80% de cobertura de código
- **Herramienta**: JaCoCo
- **Reporte**: Generado en cada ejecución del pipeline
- **Validación**: El pipeline falla si la cobertura es < 80%

---

## ⚠️ Consideraciones Especiales

### Restricción de Push Directo

Debido a políticas de la organización, no se puede hacer push directo al repositorio. El workflow está adaptado para:

1. **Generar artifact** con el `values.yaml` actualizado
2. **Mostrar instrucciones** en el summary del workflow
3. **Usuario sube manualmente** el archivo por la interfaz web de GitHub
4. **ArgoCD sincroniza** automáticamente una vez subido

### Flujo Manual de GitOps

```
Pipeline ejecuta → Genera artifact → Usuario descarga → 
Usuario sube a GitHub → ArgoCD detecta → Sincroniza
```

---

## 📝 Próximos Pasos

### Mejoras Futuras

1. **Observabilidad**:
   - Integración con Prometheus y Grafana
   - Distributed tracing con Jaeger
   - Logging centralizado con ELK Stack

2. **Seguridad**:
   - Implementar OAuth2/JWT
   - Rate limiting
   - WAF (Web Application Firewall)

3. **Performance**:
   - Caching con Redis
   - Database connection pooling
   - CDN para assets estáticos

4. **DevOps**:
   - Blue-Green deployments
   - Canary releases
   - Feature flags

---

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [GitOps Principles](https://www.gitops.tech/)
- [ArgoCD Documentation](https://argo-cd.readthedocs.io/)
- [Helm Documentation](https://helm.sh/docs/)

---

**Última actualización**: 2026-02-08  
**Versión**: 1.0-SNAPSHOT  
**Java**: 21  
**Spring Boot**: 3.2.0
