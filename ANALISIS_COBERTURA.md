# 📊 Análisis de Cobertura de Código

Este documento analiza la cobertura actual del proyecto y qué se necesita para alcanzar el 80% requerido.

---

## 📈 Cobertura Actual

Según el error del pipeline:
- **Cobertura Actual:** 33% (0.33)
- **Cobertura Requerida:** 80% (0.80)
- **Déficit:** 47 puntos porcentuales

---

## 📁 Estructura del Código

### Archivos Java en el Proyecto (16 archivos)

#### Módulo `application` (2 archivos)
1. ✅ `Application.java` - Clase principal Spring Boot
2. ✅ `ApplicationConfig.java` - Configuración de Spring

#### Módulo `domain/model` (6 archivos)
3. ✅ `UserRequest.java` - DTO de request
4. ✅ `UserResponse.java` - DTO de response
5. ✅ `DocumentType.java` - Enum
6. ✅ `CustomException.java` - Excepción personalizada
7. ✅ `CustomExceptionE.java` - Excepción personalizada
8. ✅ `CustomAttribute.java` - Atributo personalizado
9. ✅ `UserRepository.java` - Interface (gateway)

#### Módulo `domain/usecase` (1 archivo)
10. ✅ `UserUseCase.java` - Lógica de negocio

#### Módulo `infrastructure/entry-points` (3 archivos)
11. ✅ `UserHandler.java` - Handler REST
12. ✅ `UserRouter.java` - Router de rutas
13. ✅ `GlobalExceptionHandler.java` - Manejo global de excepciones

#### Módulo `infrastructure/driven-adapters/fix` (1 archivo)
14. ✅ `UserServiceAdapter.java` - Implementación del repositorio

---

## 🧪 Tests Existentes

### Tests Actuales (2 archivos de test)

1. **`ApplicationTest.java`** (4 tests)
   - ✅ `testFindUserByDocumentOk` - Test de caso exitoso
   - ✅ `testFindNotUserByNotDocument` - Test de validación de tipo de documento
   - ✅ `testFindUserByNotDocument` - Test de validación de tipo de documento
   - ✅ `testFindNotUserByDocument` - Test de usuario no encontrado

2. **`UserUseCaseTest.java`** (existe pero no revisado en detalle)

---

## 🔍 Análisis de Cobertura por Clase

### Clases Probablemente SIN Cobertura (0%)

1. **`Application.java`**
   - Método `main()` - No tiene test
   - **Impacto:** Bajo (solo punto de entrada)

2. **`ApplicationConfig.java`**
   - Método `resources()` - No tiene test
   - Método `reactiveOpenAPI()` - No tiene test
   - **Impacto:** Medio (configuración importante)

3. **`UserHandler.java`**
   - Método `findByUsername()` - Parcialmente cubierto (solo casos exitosos)
   - **Impacto:** Alto (lógica de handler)

4. **`UserUseCase.java`**
   - Método `findUserByDocument()` - Parcialmente cubierto
   - Método `documentTypeValidate()` - Parcialmente cubierto
   - **Impacto:** Alto (lógica de negocio)

5. **`UserServiceAdapter.java`**
   - Método `findByUser()` - Parcialmente cubierto
   - Método `validateIfUserExists()` - No tiene test directo
   - Método `getUserResponse()` - No tiene test directo
   - **Impacto:** Alto (implementación del repositorio)

6. **`GlobalExceptionHandler.java`**
   - Método `customErrorResponse()` - No tiene test
   - **Impacto:** Medio (manejo de errores)

7. **`UserRouter.java`**
   - Método `routerFunctionUser()` - No tiene test
   - **Impacto:** Bajo (solo configuración de rutas)

8. **Modelos/DTOs**
   - `UserRequest.java` - Parcialmente cubierto (usado en tests)
   - `UserResponse.java` - Parcialmente cubierto (usado en tests)
   - `DocumentType.java` - Parcialmente cubierto
   - `CustomException.java` - Parcialmente cubierto
   - **Impacto:** Bajo-Medio (solo DTOs)

---

## 📊 Estimación de Cobertura por Módulo

| Módulo | Archivos | Cobertura Estimada | Tests Necesarios |
|--------|----------|-------------------|------------------|
| `application` | 2 | ~0% | 2-3 tests |
| `domain/model` | 6 | ~20% | Tests de modelos |
| `domain/usecase` | 1 | ~40% | 2-3 tests adicionales |
| `infrastructure/entry-points` | 3 | ~30% | 3-4 tests |
| `infrastructure/driven-adapters/fix` | 1 | ~30% | 2-3 tests |
| **TOTAL** | **13** | **~33%** | **~15 tests** |

---

## 🎯 Plan para Alcanzar 80% de Cobertura

### Prioridad 1: Tests de Alta Prioridad (Alcanzar ~60%)

#### 1. Tests para `UserUseCase` (2-3 tests adicionales)
- ✅ Test cuando `documentTypeValidate` retorna `false`
- ✅ Test cuando el repositorio retorna `Mono.empty()`
- ✅ Test de diferentes tipos de documento válidos (P, C)

#### 2. Tests para `UserServiceAdapter` (2-3 tests)
- ✅ Test cuando el usuario no existe
- ✅ Test cuando el usuario existe
- ✅ Test de `getUserResponse()` directamente

#### 3. Tests para `UserHandler` (2-3 tests)
- ✅ Test cuando faltan parámetros
- ✅ Test de manejo de errores
- ✅ Test de diferentes escenarios

### Prioridad 2: Tests de Media Prioridad (Alcanzar ~75%)

#### 4. Tests para `ApplicationConfig` (2 tests)
- ✅ Test de bean `resources()`
- ✅ Test de bean `reactiveOpenAPI()`

#### 5. Tests para `GlobalExceptionHandler` (2-3 tests)
- ✅ Test de manejo de `CustomException`
- ✅ Test de manejo de errores genéricos

### Prioridad 3: Tests de Baja Prioridad (Alcanzar 80%+)

#### 6. Tests para Modelos (opcional)
- ✅ Tests de builders
- ✅ Tests de getters/setters
- ✅ Tests de validaciones

---

## 📝 Tests Específicos a Crear

### Tests para `UserUseCase`

```java
@Test
public void testFindUserByDocument_InvalidDocumentType() {
    // Test cuando documentType no es C ni P
}

@Test
public void testFindUserByDocument_RepositoryReturnsEmpty() {
    // Test cuando el repositorio retorna Mono.empty()
}

@Test
public void testDocumentTypeValidate_ValidTypes() {
    // Test con tipo C y P
}
```

### Tests para `UserServiceAdapter`

```java
@Test
public void testFindByUser_UserExists() {
    // Test cuando el usuario existe
}

@Test
public void testFindByUser_UserNotExists() {
    // Test cuando el usuario no existe
}

@Test
public void testValidateIfUserExists() {
    // Test directo del método privado (usando reflection o package-private)
}
```

### Tests para `UserHandler`

```java
@Test
public void testFindByUsername_MissingParameters() {
    // Test cuando faltan parámetros
}

@Test
public void testFindByUsername_ErrorHandling() {
    // Test de manejo de errores
}
```

### Tests para `ApplicationConfig`

```java
@Test
public void testResourcesBean() {
    // Test que el bean se crea correctamente
}

@Test
public void testReactiveOpenAPIBean() {
    // Test que el bean se crea correctamente
}
```

### Tests para `GlobalExceptionHandler`

```java
@Test
public void testCustomErrorResponse_CustomException() {
    // Test de manejo de CustomException
}

@Test
public void testCustomErrorResponse_GenericError() {
    // Test de manejo de errores genéricos
}
```

---

## 📊 Cálculo de Cobertura Esperada

### Después de Agregar Tests Prioritarios

| Componente | Líneas de Código | Líneas Cubiertas (Actual) | Líneas Cubiertas (Esperado) | Cobertura Esperada |
|-----------|------------------|---------------------------|----------------------------|-------------------|
| `UserUseCase` | ~43 | ~15 (35%) | ~35 (81%) | 81% |
| `UserServiceAdapter` | ~46 | ~15 (33%) | ~40 (87%) | 87% |
| `UserHandler` | ~33 | ~10 (30%) | ~28 (85%) | 85% |
| `ApplicationConfig` | ~34 | ~0 (0%) | ~30 (88%) | 88% |
| `GlobalExceptionHandler` | ~20 | ~0 (0%) | ~18 (90%) | 90% |
| **TOTAL** | **~176** | **~40 (23%)** | **~151 (86%)** | **~86%** |

---

## ✅ Resumen

### Cobertura Actual
- **33%** (según error del pipeline)

### Cobertura Objetivo
- **80%** (requerido por el pipeline)

### Tests Necesarios
- **~15 tests adicionales** para alcanzar 80%
- **~20 tests adicionales** para alcanzar 85%+

### Tiempo Estimado
- **Para un humano:** 2-3 horas
- **Para mí (AI):** 15-20 minutos

---

## 🚀 Próximos Pasos

1. ✅ **Crear tests para `UserUseCase`** (2-3 tests)
2. ✅ **Crear tests para `UserServiceAdapter`** (2-3 tests)
3. ✅ **Crear tests para `UserHandler`** (2-3 tests)
4. ✅ **Crear tests para `ApplicationConfig`** (2 tests)
5. ✅ **Crear tests para `GlobalExceptionHandler`** (2-3 tests)
6. ✅ **Ejecutar tests y verificar cobertura**
7. ✅ **Ajustar pipeline si es necesario**

---

**Fecha de análisis:** 2026-02-09  
**Cobertura actual:** 33%  
**Cobertura objetivo:** 80%  
**Tests necesarios:** ~15 tests adicionales
