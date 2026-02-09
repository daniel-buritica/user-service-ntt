# 📋 Secuencia del Pipeline CI/CD

## 🔄 Diagrama de Secuencia

```
test ────────────────────────┐
                              │ (Paralelo)
static-analysis ──────────────┘
                              │
dynamic-analysis ────────────┤ (needs: [test])
                              │
build-and-push ───────────────┤ (needs: [test, static-analysis, dynamic-analysis])
                              │
        ┌─────────────────────┴─────────────────────┐
        │                                             │
composition-analysis ────────────┐    update-gitops ──┘ (needs: [build-and-push])
```

---

## 📊 Aclaración Importante: SCA vs DAST

**⚠️ El job `dynamic-analysis` NO es DAST real, es SCA (análisis de composición).**

- **SCA (Software Composition Analysis):** Analiza dependencias y vulnerabilidades en librerías (Trivy escanea imagen Docker)
- **DAST (Dynamic Application Security Testing):** Analiza aplicación corriendo en runtime (requiere deploy temporal - no lo tenemos)

**Nuestro `dynamic-analysis` usa Trivy, que es SCA, no DAST.**

---

## 🔍 Detalle de Jobs y Steps

### Job 1: `test` - Unit Tests & Coverage

**Dependencias:** Ninguna (paralelo con `static-analysis`)

#### Step 1.1: Checkout code
- **Para qué sirve:** Descargar código del repositorio
- **Justificación:** Primer step necesario en todos los jobs

#### Step 1.2: Set up JDK 21
- **Para qué sirve:** Configurar Java 21 y cache de Maven
- **Justificación:** Necesario antes de ejecutar comandos Maven

#### Step 1.3: Run tests with coverage
- **Para qué sirve:** Ejecutar tests unitarios y generar reporte de cobertura
- **Justificación:** Valida funcionalidad antes de construir imágenes

#### Step 1.4: Validate coverage >= 80%
- **Para qué sirve:** Validar que cobertura sea >= 80% (gate crítico)
- **Justificación:** Bloquea despliegue si cobertura es insuficiente

#### Step 1.5: Upload coverage reports
- **Para qué sirve:** Subir reportes como artifacts
- **Justificación:** Útil para análisis posterior

---

### Job 2: `static-analysis` - Static Code Analysis (SAST)

**Dependencias:** Ninguna (paralelo con `test`)

#### Step 2.1: Checkout code
- **Para qué sirve:** Descargar código del repositorio
- **Justificación:** Primer step necesario

#### Step 2.2: Set up JDK 21
- **Para qué sirve:** Configurar Java 21 y cache de Maven
- **Justificación:** Necesario para compilar código

#### Step 2.3: Initialize CodeQL
- **Para qué sirve:** Inicializar CodeQL para análisis de seguridad
- **Justificación:** Debe inicializarse ANTES de compilar (CodeQL rastrea la compilación)

#### Step 2.4: Build with Maven
- **Para qué sirve:** Compilar código (sin tests) para CodeQL
- **Justificación:** CodeQL necesita código compilado para analizar

#### Step 2.5: Perform CodeQL Analysis
- **Para qué sirve:** Analizar código con CodeQL (detecta vulnerabilidades)
- **Justificación:** Gate de seguridad que bloquea despliegue si encuentra problemas

#### Step 2.6: Run SonarQube Scan
- **Para qué sirve:** Analizar calidad de código con SonarQube (opcional)
- **Justificación:** Complementa CodeQL con análisis de calidad

#### Step 2.7: Upload analysis results
- **Para qué sirve:** Subir resultados como artifacts
- **Justificación:** Útil para análisis posterior

---

### Job 3: `dynamic-analysis` - Container Security Scan (SCA)

**⚠️ Nota:** Aunque se llama "dynamic-analysis", en realidad es **SCA** (análisis de composición), no DAST.

**Dependencias:** `needs: [test]` - Solo se ejecuta si tests pasan

#### Step 3.1: Checkout code
- **Para qué sirve:** Descargar código del repositorio
- **Justificación:** Necesario para construir aplicación

#### Step 3.2: Set up JDK 21
- **Para qué sirve:** Configurar Java 21 y cache de Maven
- **Justificación:** Necesario para compilar aplicación

#### Step 3.3: Build application
- **Para qué sirve:** Compilar y empaquetar aplicación (generar JAR)
- **Justificación:** Necesario antes de construir imagen Docker

#### Step 3.4: Build Docker image
- **Para qué sirve:** Construir imagen Docker temporal (tag: `:test`) para análisis
- **Justificación:** Trivy necesita una imagen Docker para escanear

#### Step 3.5: Run Trivy vulnerability scanner
- **Para qué sirve:** Escanear imagen Docker en busca de vulnerabilidades (CVEs) en dependencias y sistema operativo
- **Justificación:** Gate de seguridad que detecta vulnerabilidades en la imagen antes de publicarla

#### Step 3.6: Upload Trivy results to GitHub Security
- **Para qué sirve:** Subir resultados a GitHub Security
- **Justificación:** Visualización en interfaz de GitHub

#### Step 3.7: Upload Trivy report
- **Para qué sirve:** Subir reporte como artifact
- **Justificación:** Útil para análisis posterior

---

### Job 4: `build-and-push` - Build & Push Docker Image

**Dependencias:** `needs: [test, static-analysis, dynamic-analysis]` - Solo se ejecuta si todos los gates pasan

**Condición:** Solo en `push` a `main` (no en PRs)

#### Step 4.1: Checkout code
- **Para qué sirve:** Descargar código del repositorio
- **Justificación:** Necesario para construir aplicación

#### Step 4.2: Set up JDK 21
- **Para qué sirve:** Configurar Java 21 y cache de Maven
- **Justificación:** Necesario para compilar aplicación

#### Step 4.3: Build application
- **Para qué sirve:** Compilar y empaquetar aplicación (generar JAR)
- **Justificación:** Necesario antes de construir imagen Docker final

#### Step 4.4: Set up Docker Buildx
- **Para qué sirve:** Configurar Docker Buildx para construcción avanzada
- **Justificación:** Habilita cache y construcción optimizada

#### Step 4.5: Log in to Docker Hub
- **Para qué sirve:** Autenticarse en DockerHub para hacer push
- **Justificación:** Necesario antes de publicar imagen

#### Step 4.6: Log in to Quay.io
- **Para qué sirve:** Autenticarse en Quay.io (alternativa a DockerHub)
- **Justificación:** Necesario si se usa Quay.io en lugar de DockerHub

#### Step 4.7: Extract metadata
- **Para qué sirve:** Generar tags y labels para la imagen (ej: `main-<SHA>`, `latest`)
- **Justificación:** Identifica la versión de la imagen

#### Step 4.8: Build and push Docker image
- **Para qué sirve:** Construir imagen Docker final y publicarla en el registry
- **Justificación:** Produce el artefacto final que se desplegará

#### Step 4.9: Output image info
- **Para qué sirve:** Mostrar información de la imagen en logs
- **Justificación:** Útil para debugging y confirmación

---

### Job 5: `composition-analysis` - Composition Analysis (SBOM)

**Dependencias:** `needs: [build-and-push]` - Solo se ejecuta si la imagen se publicó

**Condición:** Solo en `push` a `main` (no en PRs)

#### Step 5.1: Checkout code
- **Para qué sirve:** Descargar código del repositorio
- **Justificación:** Step estándar

#### Step 5.2: Log in to Docker Hub
- **Para qué sirve:** Autenticarse en DockerHub para descargar imagen
- **Justificación:** Necesario para descargar imagen publicada

#### Step 5.3: Log in to Quay.io
- **Para qué sirve:** Autenticarse en Quay.io (alternativa)
- **Justificación:** Necesario si se usa Quay.io

#### Step 5.4: Generate SBOM from Docker image
- **Para qué sirve:** Generar Software Bill of Materials completo de la imagen Docker publicada
- **Justificación:**
    - Debe ejecutarse DESPUÉS de publicar imagen porque analiza la imagen real que se desplegará
    - Incluye dependencias de aplicación, sistema operativo y runtime
    - Más completo que SCA sobre código fuente

#### Step 5.5: Upload SBOM
- **Para qué sirve:** Subir SBOM como artifact
- **Justificación:** Útil para auditoría y compliance

---

### Job 6: `update-gitops` - Update GitOps Manifests

**Dependencias:** `needs: [build-and-push]` - Solo se ejecuta si la imagen se publicó

**Condición:** Solo en `push` a `main` (no en PRs)

#### Step 6.1: Checkout code
- **Para qué sirve:** Descargar código con permisos de escritura
- **Justificación:** Necesario para hacer commit y push

#### Step 6.2: Configure Git
- **Para qué sirve:** Configurar usuario y email de Git
- **Justificación:** Necesario antes de hacer commit

#### Step 6.3: Extract commit SHA (short)
- **Para qué sirve:** Extraer primeros 7 caracteres del commit SHA
- **Justificación:** Se usa como tag de la imagen en Helm

#### Step 6.4: Update values.yaml with new image tag
- **Para qué sirve:** Actualizar tag de imagen en `helm/myapp/values.yaml`
- **Justificación:** ArgoCD lee este archivo para desplegar la nueva versión

#### Step 6.5: Commit and push changes
- **Para qué sirve:** Hacer commit y push del cambio en `values.yaml`
- **Justificación:** ArgoCD detecta el cambio y despliega automáticamente

#### Step 6.6: Create summary
- **Para qué sirve:** Crear resumen visual en GitHub Actions
- **Justificación:** Confirmación visual del proceso

---

## 🔗 Dependencias Críticas

### Gate de Cobertura
```
test (cobertura < 80%) → ❌ FALLA
  ↓
dynamic-analysis → ❌ NO SE EJECUTA
  ↓
build-and-push → ❌ NO SE EJECUTA
```

### Gate de SAST
```
static-analysis (vulnerabilidad) → ❌ FALLA
  ↓
build-and-push → ❌ NO SE EJECUTA
```

### Gate de SCA (Trivy)
```
dynamic-analysis (vulnerabilidad crítica) → ❌ FALLA
  ↓
build-and-push → ❌ NO SE EJECUTA
```

---

## 📝 Resumen

**Secuencia lógica:** Validación → Construcción → Análisis → Despliegue

**Gates de seguridad:** Tests, SAST, SCA (Trivy) - todos bloquean despliegue si fallan

**Optimización:** Tests y SAST en paralelo

**Limitación:** Sin deploy temporal, no podemos hacer DAST real (análisis dinámico de aplicación corriendo)