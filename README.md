# BioBitácora

Aplicación móvil para el registro y catalogación de biodiversidad urbana, desarrollada como proyecto académico de la materia de Cómputo Móvil.

---

## Descripción

**BioBitácora** permite a los estudiantes actuar como *científicos ciudadanos*, registrando flora y fauna local mediante:

* Geolocalización (GPS)
* Captura de imágenes
* Almacenamiento local
* Consulta de datos desde APIs externas
* Protección mediante biometría

---

## Objetivo del Proyecto

Desarrollar una aplicación móvil moderna utilizando:

* Arquitectura **MVVM**
* UI declarativa con **Jetpack Compose**
* Persistencia local y consumo de APIs
* Integración con hardware del dispositivo

---

## Objetivos Específicos

* Implementar interfaces modernas con Compose
* Manejar estado con ViewModel y StateFlow
* Persistir datos con Room y DataStore
* Consumir servicios REST (JSON)
* Integrar cámara y GPS
* Aplicar autenticación biométrica

---

## Arquitectura

El proyecto sigue el patrón:

**MVVM (Model - View - ViewModel)**

```text
UI (Compose) ↔ ViewModel ↔ Repository ↔ Data (Local/Remote)
```

---

## Estructura del Proyecto

```text
com.tuuniversidad.biobitacora/

├── ui/
│   ├── navigation/
│   ├── screens/
│   └── theme/
│
├── viewmodel/
│
├── data/            # (Se implementará en semanas posteriores)
│   ├── local/
│   ├── remote/
│   └── repository/
│
└── util/
```

> ⚠️ Nota: No todas las capas están implementadas desde el inicio. Se desarrollarán progresivamente durante el curso.

---

## Tecnologías

* Kotlin
* Jetpack Compose
* Navigation Compose
* ViewModel / StateFlow

> En semanas posteriores:

* Room (SQLite)
* Retrofit
* DataStore
* Biometric Authentication

---

## Cronograma de Desarrollo

### Semana 1: UI & Navegación

* Configuración del proyecto
* Pantallas base
* Navegación entre vistas

### Semana 2: Estado & MVVM

* ViewModels
* Manejo de estado
* Datos simulados

### Semana 3: Persistencia Local

* Base de datos con Room
* DataStore

### Semana 4: API & Conectividad

* Consumo de servicios REST
* Modo offline

### Semana 5: Sensores & Seguridad

* Cámara
* GPS
* Biometría

### Semana 6: Testing & Release

* Pruebas unitarias
* APK firmado
* Ofuscación

---

## Reglas del Proyecto

* No hardcodear textos → usar recursos (`strings.xml`)
* No mezclar lógica en Composables
* Seguir patrón MVVM
* Commits frecuentes y claros
* Código limpio y organizado

---

## Estado Actual

Proyecto base inicial
- UI básica
- Navegación
- Datos simulados

---

## Notas para el Alumno

* Este repositorio es una **base inicial**
* Cada semana deberás extender la funcionalidad
* No todas las características están implementadas aún
* Sigue las instrucciones de clase para cada entrega

---

## Funcionalidades Esperadas

* Registro de usuario
* Registro de avistamientos
* Captura de imagen
* Geolocalización
* Catálogo de especies desde API
* Acceso protegido con biometría

---

## Evaluación

El proyecto será evaluado con base en:

* Funcionalidad
* Arquitectura
* Calidad del código
* Uso correcto de tecnologías
* Cumplimiento de requisitos

---

## Licencia

Uso académico.

