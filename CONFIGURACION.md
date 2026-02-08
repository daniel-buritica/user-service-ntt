# 🔧 Guía de Configuración - User Service CI/CD

Esta guía te ayudará a configurar el proyecto completo paso a paso.

---

## 📋 Prerequisitos

- Cuenta de GitHub
- Cuenta de DockerHub o Quay.io
- Acceso a un cluster de Kubernetes con ArgoCD instalado
- Git instalado en tu PC
- Java 21 instalado (para desarrollo local)

---

## 🚀 Paso 1: Configuración de GitHub Secrets

### 1.1 Acceder a Secrets

1. Ve a tu repositorio en GitHub
2. Click en **Settings** → **Secrets and variables** → **Actions**
3. Click en **New repository secret**

### 1.2 Configurar Secrets de DockerHub

Si usas **DockerHub**:

```
Nombre: DOCKERHUB_USERNAME
Valor: tu_usuario_dockerhub

Nombre: DOCKERHUB_TOKEN
Valor: tu_token_dockerhub
```

**Cómo obtener el token de DockerHub:**
1. Ve a https://hub.docker.com/settings/security
2. Click en **New Access Token**
3. Dale un nombre (ej: "github-actions")
4. **IMPORTANTE**: Selecciona el permiso **Read & Write** (no solo Read)
5. Click en **Generate**
6. **IMPORTANTE**: Copia el token inmediatamente, ya que solo se muestra una vez
7. Pega el token en el secret `DOCKERHUB_TOKEN` de GitHub

**⚠️ Nota**: Si el token no tiene permisos de escritura, verás el error:
```
401 Unauthorized: access token has insufficient scopes
```
En ese caso, elimina el token anterior y crea uno nuevo con permisos **Read & Write**.

### 1.3 Configurar Secrets de Quay.io (Alternativa)

Si prefieres usar **Quay.io**:

```
Nombre: QUAY_USERNAME
Valor: tu_usuario_quay

Nombre: QUAY_TOKEN
Valor: tu_token_quay
```

**Cómo obtener el token de Quay.io:**
1. Ve a https://quay.io/user/YOUR_USERNAME?tab=settings
2. Click en **Generate Encrypted Password**
3. Copia el token generado

### 1.4 Configurar SonarQube (Opcional)

Si quieres usar SonarQube:

```
Nombre: SONAR_TOKEN
Valor: tu_token_sonarqube

Nombre: SONAR_HOST_URL
Valor: https://sonarcloud.io (o tu instancia)

Nombre: SONAR_ORGANIZATION
Valor: tu_organizacion_sonar
```

---

## 🔧 Paso 2: Actualizar Archivos de Configuración

### 2.1 Actualizar `helm/myapp/values.yaml`

Edita el archivo `helm/myapp/values.yaml` y cambia:

```yaml
image:
  repository: docker.io/USERNAME/user-service  # Cambiar USERNAME
```

Por tu usuario real:

```yaml
image:
  repository: docker.io/tu_usuario/user-service
```

### 2.2 Actualizar `argocd/application.yaml`

Edita el archivo `argocd/application.yaml` y cambia:

```yaml
source:
  repoURL: https://github.com/USERNAME/REPO_NAME.git  # Cambiar ambos
```

Por tu repositorio real:

```yaml
source:
  repoURL: https://github.com/tu_usuario/tu_repositorio.git
```

### 2.3 Actualizar `.github/CODEOWNERS`

Edita el archivo `.github/CODEOWNERS` y cambia:

```
* @YOUR_GITHUB_USERNAME
```

Por tu usuario de GitHub:

```
* @tu_usuario_github
```

### 2.4 Actualizar Workflow (si usas Quay.io)

Si prefieres usar Quay.io en lugar de DockerHub, edita `.github/workflows/triggerci.yml`:

```yaml
env:
  REGISTRY: quay.io  # Cambiar de docker.io a quay.io
```

---

## 📤 Paso 3: Subir Archivos a GitHub

### 3.1 Desde otro PC (con permisos de push)

```bash
# Clonar el repositorio (si aún no lo tienes)
git clone https://github.com/tu_usuario/tu_repositorio.git
cd tu_repositorio

# Agregar todos los archivos
git add .

# Hacer commit
git commit -m "feat: implementar pipeline CI/CD completo con GitOps"

# Push a main
git push origin main
```

### 3.2 Verificar que los archivos estén en GitHub

Asegúrate de que estos archivos estén presentes:

- ✅ `.github/workflows/triggerci.yml`
- ✅ `.github/CODEOWNERS`
- ✅ `helm/myapp/Chart.yaml`
- ✅ `helm/myapp/values.yaml`
- ✅ `helm/myapp/templates/*.yaml`
- ✅ `argocd/application.yaml`
- ✅ `docs/arquitectura.md`
- ✅ `pom.xml` (con JaCoCo)
- ✅ `deployment/Dockerfile`

---

## ✅ Paso 4: Verificar el Pipeline

### 4.1 Primera Ejecución

1. Ve a la pestaña **Actions** en GitHub
2. Deberías ver el workflow ejecutándose
3. Revisa cada job:
   - ✅ `test` - Debe pasar con cobertura ≥80%
   - ✅ `static-analysis` - Debe completarse
   - ✅ `dynamic-analysis` - Debe completarse
   - ✅ `composition-analysis` - Debe generar SBOM
   - ✅ `build-and-push` - Debe construir y subir imagen
   - ✅ `update-gitops` - Debe actualizar values.yaml

### 4.2 Verificar Imagen Docker

1. Ve a DockerHub/Quay.io
2. Deberías ver la imagen `user-service` con tags:
   - `main-<commit-sha>` (ej: `main-a1b2c3d`)
   - `latest` (solo en main)

### 4.3 Verificar Actualización de values.yaml

1. Ve al archivo `helm/myapp/values.yaml` en GitHub
2. Debería tener el tag actualizado:
   ```yaml
   image:
     tag: "a1b2c3d"  # Commit SHA corto
   ```

---

## 🎯 Paso 5: Configurar ArgoCD

### 5.1 Aplicar el Manifest de ArgoCD

En tu cluster de Kubernetes, aplica el manifest:

```bash
kubectl apply -f argocd/application.yaml
```

O si ArgoCD está en otro namespace:

```bash
kubectl apply -f argocd/application.yaml -n argocd
```

### 5.2 Verificar en ArgoCD UI

1. Accede al dashboard de ArgoCD
2. Deberías ver la aplicación `user-service`
3. Verifica que:
   - ✅ Estado: `Synced` y `Healthy`
   - ✅ Source: Apunta a tu repositorio
   - ✅ Destination: Apunta a tu cluster/namespace

### 5.3 Verificar Despliegue en Kubernetes

```bash
# Verificar deployment
kubectl get deployment -n default

# Verificar pods
kubectl get pods -n default

# Verificar service
kubectl get service -n default
```

---

## 🧪 Paso 6: Probar el Flujo Completo

### 6.1 Hacer un Cambio

1. Haz un cambio pequeño en el código (ej: un comentario)
2. Haz commit y push:
   ```bash
   git add .
   git commit -m "test: probar pipeline"
   git push origin main
   ```

### 6.2 Observar el Pipeline

1. Ve a **Actions** en GitHub
2. Observa cómo se ejecuta el pipeline
3. Verifica que:
   - ✅ Todos los jobs pasen
   - ✅ La imagen se construya con nuevo tag
   - ✅ `values.yaml` se actualice automáticamente

### 6.3 Verificar Sincronización de ArgoCD

1. Ve al dashboard de ArgoCD
2. Deberías ver que detecta el cambio
3. ArgoCD sincronizará automáticamente
4. Los nuevos pods se crearán con la nueva imagen

---

## 🔍 Troubleshooting

### Problema: Pipeline falla en "Docker login" o "access token has insufficient scopes"

**Solución:**
- Verifica que los secrets `DOCKERHUB_USERNAME` y `DOCKERHUB_TOKEN` estén configurados
- **IMPORTANTE**: El token debe tener permisos de **Read & Write**, no solo Read
- Si ves el error `401 Unauthorized: access token has insufficient scopes`:
   1. Ve a https://hub.docker.com/settings/security
   2. Elimina el token anterior (si existe)
   3. Crea un nuevo token con permisos **Read & Write**
   4. Actualiza el secret `DOCKERHUB_TOKEN` en GitHub con el nuevo token
   5. Verifica que el `DOCKERHUB_USERNAME` sea correcto (debe ser tu usuario de DockerHub, no tu email)

### Problema: Coverage < 80%

**Solución:**
- Aumenta la cobertura de pruebas
- Revisa el reporte de JaCoCo en los artifacts del workflow

### Problema: "Git push failed" en update-gitops

**Solución:**
- Verifica que el workflow tenga permisos `contents: write`
- Verifica que el `GITHUB_TOKEN` tenga permisos suficientes

### Problema: ArgoCD no sincroniza

**Solución:**
- Verifica que el manifest `application.yaml` esté aplicado
- Verifica que la URL del repositorio sea correcta
- Verifica que ArgoCD tenga acceso al repositorio

### Problema: Imagen no se encuentra

**Solución:**
- Verifica que la imagen se haya subido correctamente al registry
- Verifica que el `image.repository` en `values.yaml` sea correcto
- Verifica que el tag coincida con el commit SHA

---

## 📚 Recursos Adicionales

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [ArgoCD Documentation](https://argo-cd.readthedocs.io/)
- [Helm Documentation](https://helm.sh/docs/)
- [DockerHub Documentation](https://docs.docker.com/docker-hub/)

---

## ✅ Checklist Final

Antes de considerar el proyecto completo, verifica:

- [ ] Secrets configurados en GitHub
- [ ] Archivos actualizados con tus valores (usuario, repo, etc.)
- [ ] Pipeline ejecuta correctamente
- [ ] Imagen Docker se construye y sube
- [ ] `values.yaml` se actualiza automáticamente
- [ ] ArgoCD Application creada y sincronizada
- [ ] Aplicación desplegada en Kubernetes
- [ ] Documentación completa

---

**¡Listo!** Tu pipeline CI/CD con GitOps está configurado y funcionando. 🎉