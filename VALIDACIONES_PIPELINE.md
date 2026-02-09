# 📋 Guía de Validaciones del Pipeline CI/CD

Este documento explica **qué valida cada paso** del pipeline y **cómo ver los resultados** de cada validación.

---

## 🎯 Resumen Ejecutivo

El pipeline ejecuta **6 jobs principales** que validan diferentes aspectos de tu código:

1. ✅ **Unit Tests & Coverage** - Pruebas unitarias y cobertura de código
2. ✅ **Static Code Analysis** - Análisis estático de código (CodeQL + SonarQube)
3. ✅ **Dynamic Security Analysis** - Análisis de seguridad en la imagen Docker
4. ✅ **Composition Analysis (SBOM)** - Inventario de dependencias
5. ✅ **Build & Push Docker Image** - Construcción y publicación de imagen
6. ✅ **Update GitOps Manifests** - Actualización automática de Helm charts

---

## 📍 Dónde Ver los Resultados

### En GitHub Actions

1. **Ve a tu repositorio en GitHub**
2. Click en la pestaña **Actions**
3. Selecciona el workflow run que quieres revisar
4. Click en el job específico para ver los detalles

### URLs Directas

- **Pipeline completo**: `https://github.com/[TU_USUARIO]/[TU_REPO]/actions`
- **Último run**: `https://github.com/[TU_USUARIO]/[TU_REPO]/actions/runs/[RUN_ID]`

---

## 🔍 Job 1: Unit Tests & Coverage

### ¿Qué valida?

Este job valida que:
- ✅ Todos los tests unitarios pasen correctamente
- ✅ La cobertura de código sea **≥ 80%**
- ✅ No haya errores de compilación

### Pasos del Job

#### 1. **Checkout code**
- **Qué hace**: Descarga el código del repositorio
- **Qué valida**: Que el código esté disponible
- **Cómo ver**: Logs del step en GitHub Actions

#### 2. **Set up JDK 21**
- **Qué hace**: Configura Java 21 (Temurin)
- **Qué valida**: Que Java esté instalado correctamente
- **Cómo ver**: Logs del step

#### 3. **Run tests with coverage** ⭐
- **Qué hace**: 
  - Ejecuta todos los tests unitarios: `mvn clean test`
  - Genera reportes de cobertura con JaCoCo: `jacoco:report`
- **Qué valida**:
  - Que todos los tests pasen (sin errores ni fallos)
  - Que el código compile correctamente
- **Cómo ver los resultados**:
  1. **En los logs del step**: Verás el resultado de cada test
  2. **En los artifacts**: Descarga el artifact `coverage-report`
     - Ve a: Actions → Tu run → Artifacts
     - Descarga `coverage-report`
     - Abre `target/site/jacoco/index.html` en tu navegador
  3. **Localmente** (después de ejecutar `mvn test jacoco:report`):
     ```bash
     open target/site/jacoco/index.html
     ```

#### 4. **Validate coverage >= 80%** ⭐
- **Qué hace**: 
  - Ejecuta `mvn jacoco:check`
  - Valida que la cobertura sea ≥ 80%
  - Si falla, calcula la cobertura manualmente desde `jacoco.xml`
- **Qué valida**:
  - **Cobertura de líneas de código ≥ 80%**
  - Si la cobertura es menor, el pipeline **falla**
- **Cómo ver los resultados**:
  1. **En los logs del step**: Verás mensajes como:
     ```
     ✅ Coverage validation passed (>= 80%)
     ```
     o
     ```
     Coverage: 85.23% (Covered: 1234, Missed: 215, Total: 1449)
     ✅ Coverage 85.23% meets 80% threshold
     ```
  2. **Si falla**, verás:
     ```
     ❌ Coverage 75.50% is below 80% threshold
     ```
  3. **Para ver detalles**:
     - Descarga el artifact `coverage-report`
     - Abre el HTML report en `target/site/jacoco/index.html`
     - Verás qué líneas están cubiertas (verde) y cuáles no (rojo)

#### 5. **Upload coverage reports**
- **Qué hace**: Sube los reportes como artifacts para descarga
- **Qué valida**: Nada, solo almacena los reportes
- **Cómo ver**: Descarga el artifact `coverage-report` desde Actions

---

## 🔍 Job 2: Static Code Analysis

### ¿Qué valida?

Este job valida:
- ✅ **Vulnerabilidades de seguridad** en el código
- ✅ **Bugs y code smells** (malas prácticas)
- ✅ **Calidad del código** (complejidad, duplicación, etc.)
- ✅ **Cumplimiento de estándares** de codificación

### Pasos del Job

#### 1. **Checkout code**
- Descarga el código

#### 2. **Set up JDK 21**
- Configura Java

#### 3. **Initialize CodeQL** ⭐
- **Qué hace**: Inicializa CodeQL (herramienta de análisis de código de GitHub)
- **Qué valida**: Nada aún, solo prepara el análisis
- **Cómo ver**: Logs del step

#### 4. **Build with Maven**
- **Qué hace**: Compila el código (sin tests)
- **Qué valida**: Que el código compile
- **Cómo ver**: Logs del step

#### 5. **Perform CodeQL Analysis** ⭐
- **Qué hace**: 
  - Analiza el código con CodeQL
  - Busca vulnerabilidades, bugs y problemas de seguridad
- **Qué valida**:
  - **Vulnerabilidades de seguridad** (SQL injection, XSS, etc.)
  - **Bugs** (null pointer exceptions, memory leaks, etc.)
  - **Problemas de calidad** (código muerto, variables no usadas, etc.)
- **Cómo ver los resultados**:
  1. **En GitHub Security**:
     - Ve a: Tu repositorio → **Security** → **Code scanning alerts**
     - URL: `https://github.com/[TU_USUARIO]/[TU_REPO]/security/code-scanning`
     - Verás todas las alertas encontradas por CodeQL
  2. **En los logs del step**: Verás un resumen del análisis
  3. **En Pull Requests**: CodeQL añade comentarios automáticos con los problemas encontrados

#### 6. **Run SonarQube Scan (if configured)** ⭐
- **Qué hace**: 
  - Analiza el código con SonarQube/SonarCloud
  - Genera métricas de calidad de código
- **Qué valida**:
  - **Code smells** (malas prácticas)
  - **Bugs** (errores potenciales)
  - **Vulnerabilidades de seguridad**
  - **Cobertura de código** (integra con JaCoCo)
  - **Duplicación de código**
  - **Complejidad ciclomática**
- **Cómo ver los resultados**:
  1. **En SonarCloud** (si está configurado):
     - Ve a: https://sonarcloud.io/projects
     - Busca tu proyecto
     - URL: `https://sonarcloud.io/project/overview?id=[TU_PROJECT_KEY]`
     - Verás:
       - Dashboard con métricas de calidad
       - Lista de issues (bugs, vulnerabilidades, code smells)
       - Cobertura de código
       - Duplicación
       - Complejidad
  2. **En los logs del step**: Verás el resultado del análisis
  3. **Si no está configurado**: Verás el mensaje:
     ```
     ⚠️ SonarQube not configured. Skipping scan.
     ```

#### 7. **Upload analysis results**
- **Qué hace**: Sube los resultados como artifacts
- **Cómo ver**: Descarga el artifact `static-analysis-results`

---

## 🔍 Job 3: Dynamic Security Analysis

### ¿Qué valida?

Este job valida:
- ✅ **Vulnerabilidades en la imagen Docker**
- ✅ **Dependencias vulnerables** en el contenedor
- ✅ **Problemas de seguridad** en las capas de la imagen

### Pasos del Job

#### 1. **Checkout code**
- Descarga el código

#### 2. **Set up JDK 21**
- Configura Java

#### 3. **Build application**
- **Qué hace**: Compila y empaqueta la aplicación
- **Qué valida**: Que compile correctamente
- **Cómo ver**: Logs del step

#### 4. **Build Docker image**
- **Qué hace**: Construye la imagen Docker
- **Qué valida**: Que el Dockerfile sea válido
- **Cómo ver**: Logs del step

#### 5. **Run Trivy vulnerability scanner** ⭐
- **Qué hace**: 
  - Escanea la imagen Docker con Trivy
  - Busca vulnerabilidades conocidas (CVE)
  - Analiza dependencias y paquetes instalados
- **Qué valida**:
  - **Vulnerabilidades críticas** en la imagen base
  - **Dependencias vulnerables** (librerías con CVEs)
  - **Problemas de seguridad** en paquetes del sistema
- **Cómo ver los resultados**:
  1. **En GitHub Security**:
     - Ve a: Tu repositorio → **Security** → **Dependabot alerts**
     - URL: `https://github.com/[TU_USUARIO]/[TU_REPO]/security/dependabot`
     - Verás las vulnerabilidades encontradas
  2. **En los logs del step**: Verás un resumen del escaneo
  3. **En los artifacts**: Descarga `trivy-report` (formato SARIF)
  4. **En Pull Requests**: Trivy añade comentarios con vulnerabilidades encontradas

#### 6. **Upload Trivy results to GitHub Security**
- **Qué hace**: Sube los resultados a GitHub Security
- **Cómo ver**: Ve a Security → Dependabot alerts

#### 7. **Upload Trivy report**
- **Qué hace**: Sube el reporte como artifact
- **Cómo ver**: Descarga el artifact `trivy-report`

---

## 🔍 Job 4: Composition Analysis (SBOM)

### ¿Qué valida?

Este job valida:
- ✅ **Inventario completo de dependencias** (SBOM)
- ✅ **Licencias de las dependencias**
- ✅ **Versiones de todas las librerías**

### Pasos del Job

#### 1. **Checkout code**
- Descarga el código

#### 2. **Set up JDK 21**
- Configura Java

#### 3. **Generate SBOM with Syft** ⭐
- **Qué hace**: 
  - Genera un Software Bill of Materials (SBOM)
  - Lista todas las dependencias del proyecto
  - Incluye versiones y licencias
- **Qué valida**:
  - **Inventario de dependencias** (todas las librerías usadas)
  - **Licencias** de cada dependencia
  - **Versiones** de cada componente
- **Cómo ver los resultados**:
  1. **En los artifacts**: Descarga el artifact `sbom`
  2. **Abre el archivo**: `sbom.cyclonedx.json`
  3. **Formato**: JSON con formato CycloneDX
  4. **Contenido**: Lista completa de dependencias con:
     - Nombre del paquete
     - Versión
     - Licencia
     - Tipo (Maven, NPM, etc.)

#### 4. **Upload SBOM**
- **Qué hace**: Sube el SBOM como artifact
- **Cómo ver**: Descarga el artifact `sbom`

---

## 🔍 Job 5: Build & Push Docker Image

### ¿Qué valida?

Este job valida:
- ✅ Que la aplicación se compile correctamente
- ✅ Que la imagen Docker se construya sin errores
- ✅ Que la imagen se suba correctamente a DockerHub

### Pasos del Job

#### 1. **Checkout code**
- Descarga el código

#### 2. **Set up JDK 21**
- Configura Java

#### 3. **Build application**
- **Qué hace**: Compila y empaqueta la aplicación
- **Qué valida**: Que compile correctamente
- **Cómo ver**: Logs del step

#### 4. **Set up Docker Buildx**
- **Qué hace**: Configura Docker Buildx para builds avanzados
- **Qué valida**: Nada, solo configuración
- **Cómo ver**: Logs del step

#### 5. **Log in to Docker Hub**
- **Qué hace**: Autentica con DockerHub
- **Qué valida**: Que las credenciales sean válidas
- **Cómo ver**: Logs del step (si falla, verás error de autenticación)

#### 6. **Extract metadata**
- **Qué hace**: Genera los tags para la imagen
- **Qué valida**: Nada
- **Cómo ver**: Logs del step

#### 7. **Build and push Docker image** ⭐
- **Qué hace**: 
  - Construye la imagen Docker
  - La sube a DockerHub con los tags generados
- **Qué valida**:
  - Que el Dockerfile sea válido
  - Que la imagen se construya correctamente
  - Que se suba a DockerHub sin errores
- **Cómo ver los resultados**:
  1. **En DockerHub**:
     - Ve a: https://hub.docker.com/r/[TU_USUARIO]/user-service/tags
     - Verás las imágenes con tags:
       - `latest` (solo en main)
       - `main-[commit-sha]` (ej: `main-0596907`)
  2. **En los logs del step**: Verás el progreso del build y push
  3. **En el step "Output image info"**: Verás información de la imagen creada

#### 8. **Output image info**
- **Qué hace**: Muestra información de la imagen creada
- **Qué valida**: Nada, solo informa
- **Cómo ver**: Logs del step

---

## 🔍 Job 6: Update GitOps Manifests

### ¿Qué valida?

Este job valida:
- ✅ Que el archivo `values.yaml` se actualice correctamente
- ✅ Que el commit se haga correctamente
- ✅ Que el push a Git funcione

### Pasos del Job

#### 1. **Checkout code**
- Descarga el código con permisos de escritura

#### 2. **Configure Git**
- **Qué hace**: Configura Git con el usuario de GitHub Actions
- **Qué valida**: Nada
- **Cómo ver**: Logs del step

#### 3. **Extract commit SHA (short)**
- **Qué hace**: Extrae los primeros 7 caracteres del commit SHA
- **Qué valida**: Nada
- **Cómo ver**: Logs del step

#### 4. **Update values.yaml with new image tag** ⭐
- **Qué hace**: 
  - Actualiza `helm/myapp/values.yaml` con el nuevo tag de imagen
  - Cambia `tag: ""` por `tag: "[commit-sha]"`
- **Qué valida**:
  - Que el archivo se actualice correctamente
  - Que el formato sea válido
- **Cómo ver los resultados**:
  1. **En el repositorio**:
     - Ve a: `helm/myapp/values.yaml`
     - Verás el tag actualizado: `tag: "0596907"`
  2. **En los logs del step**:
     ```
     Updated values.yaml:
     tag: "0596907"
     ```
  3. **En el commit**: Verás un commit automático:
     ```
     chore: update image tag to 0596907 [skip ci]
     ```

#### 5. **Commit and push changes**
- **Qué hace**: 
  - Hace commit del cambio
  - Hace push a la rama main
- **Qué valida**:
  - Que el commit se haga correctamente
  - Que el push funcione
- **Cómo ver los resultados**:
  1. **En el repositorio**:
     - Ve a: Commits
     - Verás el commit automático
  2. **En los logs del step**: Verás el resultado del push

#### 6. **Create summary**
- **Qué hace**: Crea un resumen del job
- **Qué valida**: Nada
- **Cómo ver**: 
  - En la pestaña "Summary" del job en GitHub Actions
  - Verás un resumen con:
    - Image Tag
    - Commit SHA
    - Archivo actualizado

---

## 📊 Resumen: Dónde Ver Cada Validación

| Validación | Dónde Ver |
|------------|-----------|
| **Tests unitarios** | Logs del step "Run tests with coverage" |
| **Cobertura de código** | Artifact `coverage-report` → `target/site/jacoco/index.html` |
| **CodeQL (vulnerabilidades)** | Repositorio → Security → Code scanning alerts |
| **SonarQube (calidad)** | https://sonarcloud.io/projects |
| **Trivy (vulnerabilidades Docker)** | Repositorio → Security → Dependabot alerts |
| **SBOM (dependencias)** | Artifact `sbom` → `sbom.cyclonedx.json` |
| **Imagen Docker** | https://hub.docker.com/r/[USUARIO]/user-service/tags |
| **GitOps update** | Repositorio → `helm/myapp/values.yaml` |

---

## 🚨 Qué Hacer Si Algo Falla

### Si fallan los tests:
1. Ve a los logs del step "Run tests with coverage"
2. Identifica qué test falló
3. Revisa el código del test y la clase probada
4. Corrige el problema y haz commit

### Si la cobertura es < 80%:
1. Descarga el artifact `coverage-report`
2. Abre `target/site/jacoco/index.html`
3. Identifica las clases con baja cobertura (marcadas en rojo)
4. Escribe más tests para esas clases
5. Haz commit y push

### Si CodeQL encuentra vulnerabilidades:
1. Ve a: Security → Code scanning alerts
2. Revisa cada alerta
3. Lee las recomendaciones de CodeQL
4. Corrige el código según las recomendaciones
5. Haz commit y push

### Si SonarQube encuentra problemas:
1. Ve a: https://sonarcloud.io/projects
2. Selecciona tu proyecto
3. Revisa la pestaña "Issues"
4. Corrige los problemas según las recomendaciones
5. Haz commit y push

### Si Trivy encuentra vulnerabilidades:
1. Ve a: Security → Dependabot alerts
2. Revisa las vulnerabilidades
3. Actualiza las dependencias vulnerables
4. Haz commit y push

---

## ✅ Checklist de Validaciones

Después de cada push, verifica:

- [ ] ✅ Todos los tests pasan
- [ ] ✅ Cobertura ≥ 80%
- [ ] ✅ No hay vulnerabilidades críticas en CodeQL
- [ ] ✅ No hay problemas críticos en SonarQube
- [ ] ✅ No hay vulnerabilidades críticas en Trivy
- [ ] ✅ La imagen Docker se construyó correctamente
- [ ] ✅ La imagen se subió a DockerHub
- [ ] ✅ `values.yaml` se actualizó con el nuevo tag

---

## 📚 Recursos Adicionales

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

**Última actualización**: 2026-02-08
