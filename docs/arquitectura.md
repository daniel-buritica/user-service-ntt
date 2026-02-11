# Pipeline CI/CD - Client Service

## Resumen Ejecutivo

Este proyecto implementa un **pipeline completo de CI/CD** con GitHub Actions para un microservicio Spring Boot. El pipeline incluye pruebas automatizadas, análisis de código, construcción de imágenes Docker, y despliegue automatizado mediante GitOps con ArgoCD.

**Artifact ID**: `client-service`  
**Versión**: `1.0-SNAPSHOT`  
**Grupo**: `co.com.prueba`  
**Java**: 21  
**Spring Boot**: 3.2.0

---

## Pipeline CI/CD

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
│                  │  (Commit & Push) │                        │
│                  └────────┬─────────┘                        │
└───────────────────────────┼──────────────────────────────────┘
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
│  │  Application: client-service                          │  │
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
- Ejecuta pruebas JUnit con `mvn clean test verify`
- Genera reporte de cobertura con JaCoCo (objetivo: ≥80%)
- Valida cobertura con `mvn jacoco:check` (configuración de `rules` en el plugin)
- Si `jacoco:check` falla, realiza validación manual parseando `jacoco.xml`
- El pipeline falla si la cobertura es < 80%
- Publica reportes como artifacts (`target/site/jacoco/`, `target/surefire-reports/`)

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
- Actualiza `helm/myapp/values.yaml` con el nuevo tag (commit SHA de 7 caracteres)
- Hace commit y push directo al repositorio con mensaje `[skip ci]`
- Crea summary con información del tag actualizado
- ArgoCD detecta automáticamente los cambios y sincroniza

---

## Flujo GitOps con ArgoCD

### Principios GitOps

1. **Declarativo**: La configuración deseada se describe en archivos YAML
2. **Versionado**: Todo está en Git
3. **Automático**: ArgoCD detecta cambios y sincroniza
4. **Observable**: Estado visible en el dashboard de ArgoCD

### Flujo de Sincronización

```
1. Pipeline actualiza values.yaml y hace commit/push
   │
   ▼
2. ArgoCD detecta cambio (polling cada 3 minutos)
   │
   ▼
3. ArgoCD compara estado deseado vs. actual
   │
   ▼
4. ArgoCD aplica cambios (kubectl apply)
   │
   ▼
5. Kubernetes actualiza Deployment
   │
   ▼
6. Nuevos Pods se crean con nueva imagen
   │
   ▼
7. Health checks verifican que la app esté lista
   │
   ▼
8. Service enruta tráfico a nuevos Pods
```

### Configuración de ArgoCD

El archivo `argocd/application.yaml` define:
- **Source**: Repositorio Git y path del Helm chart
- **Destination**: Cluster y namespace de Kubernetes
- **Sync Policy**: Automático con auto-healing
- **Health Checks**: Verificación de salud de la aplicación

---

## Containerización

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
COPY target/client-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8090
ENV PORT=8090
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
```

### Imagen Docker
- **Base**: Eclipse Temurin 21 JDK Alpine (ligera)
- **Puerto**: 8090 (configurable vía variable de entorno PORT)
- **JAR**: `client-service-1.0-SNAPSHOT.jar` (renombrado a `app.jar` en el contenedor)
- **Ubicación Dockerfile**: `deployment/Dockerfile`

---

## Helm Chart

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

## Stack Tecnológico CI/CD

### CI/CD
- **GitHub Actions**: Automatización del pipeline
- **Docker**: Containerización
- **Helm**: Gestión de Kubernetes
- **ArgoCD**: GitOps y despliegue continuo

### Infraestructura
- **Kubernetes**: Orquestación de contenedores
- **DockerHub/Quay.io**: Registry de imágenes

### Testing
- **JUnit 5**: Framework de pruebas
- **JaCoCo**: Cobertura de código (≥80%)

### Análisis y Seguridad
- **CodeQL**: Análisis estático de código
- **Trivy**: Análisis dinámico de vulnerabilidades en imágenes Docker
- **Syft**: Generación de SBOM (Software Bill of Materials)
- **SonarQube**: Análisis de calidad de código (opcional)

---

## Seguridad en el Pipeline

### Implementaciones de Seguridad

1. **Análisis Estático**: Detección temprana de vulnerabilidades (CodeQL)
2. **Análisis Dinámico**: Escaneo de imágenes Docker (Trivy)
3. **SBOM**: Inventario de dependencias (Syft)
4. **Secrets Management**: Variables de entorno en Kubernetes
5. **Health Checks**: Verificación continua de salud

---

## Testing Strategy

### Niveles de Prueba

1. **Unit Tests**: Pruebas de casos de uso y lógica de negocio
2. **Integration Tests**: Pruebas de endpoints REST
3. **E2E Tests**: Pruebas end-to-end (opcional)

### Cobertura

- **Objetivo**: ≥80% de cobertura de código (líneas)
- **Herramienta**: JaCoCo 0.8.11
- **Configuración**: 
  - `rules` definidas en el nivel de `<configuration>` del plugin (disponible para `mvn jacoco:check`)
  - Excluye clases generadas por Lombok (`**/*$*.class`)
  - Excluye clase principal `Application.class`
  - Validación por paquete (`PACKAGE`) con límite mínimo de 80% (`COVEREDRATIO`)
- **Validación**: 
  - Primero intenta `mvn jacoco:check` (usa configuración del plugin)
  - Si falla, parsea manualmente `jacoco.xml` para calcular porcentaje
  - El pipeline falla si la cobertura es < 80%
- **Reporte**: Generado en `target/site/jacoco/` en cada ejecución del pipeline

---



### Actualización Automática de GitOps

El workflow actualiza automáticamente el archivo `helm/myapp/values.yaml` y hace push directo al repositorio:

1. **Extrae SHA corto**: Toma los primeros 7 caracteres del commit SHA
2. **Actualiza values.yaml**: Reemplaza el tag de la imagen con el nuevo SHA
3. **Commit y push**: Hace commit con mensaje `[skip ci]` para evitar loops infinitos
4. **ArgoCD sincroniza**: ArgoCD detecta el cambio automáticamente y despliega la nueva versión

### Secrets de GitHub Actions

Configura los siguientes secrets en GitHub:

- `DOCKERHUB_USERNAME`: Usuario de DockerHub
- `DOCKERHUB_TOKEN`: Token de acceso de DockerHub
- `QUAY_USERNAME`: Usuario de Quay.io (alternativa)
- `QUAY_TOKEN`: Token de acceso de Quay.io (alternativa)
- `SONAR_TOKEN`: Token de SonarQube (opcional)
- `SONAR_HOST_URL`: URL de SonarQube (opcional)
  - `SONAR_ORGANIZATION`: Organización de SonarQube (opcional)