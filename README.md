# FitMatch Móvil

> Aplicación Android para gestión de rutinas de entrenamiento, seguimiento de progreso y conexión con otros usuarios fitness.

---

## Descripción

FitMatch Móvil es una aplicación Android desarrollada con Kotlin y Jetpack Compose que permite a los usuarios crear y gestionar sus rutinas de entrenamiento, registrar su progreso, explorar un catálogo de ejercicios con información detallada, y conectarse con otros usuarios mediante un sistema de amigos y compartición de rutinas por código QR.

---

## Funcionalidades principales

### Autenticación
- Registro de nuevos usuarios con validación de datos
- Inicio de sesión con nombre de usuario o correo electrónico
- Contraseñas almacenadas con hash SHA-256
- Sesión persistente durante el uso de la app

### Rutinas
- Crear, editar y eliminar rutinas personalizadas
- Agregar ejercicios a cada rutina con series, repeticiones y tiempo de descanso
- Marcar rutinas como completadas con duración real y calificación
- Compartir rutinas por código QR
- Escanear QR de amigos para importar sus rutinas

### Progreso e Historial
- Registro automático de sesiones completadas
- Gráficas de duración por sesión (últimas 7)
- Gráficas de calificación por sesión (últimas 7)
- Resumen general: total de sesiones, minutos y calificación promedio
- Historial detallado de actividades

### Ejercicios
- Catálogo organizado por grupo muscular (Pecho, Brazos, Espalda, Piernas)
- Información detallada de cada ejercicio: músculo principal y consejos de ejecución
- Agregar ejercicios del catálogo directamente a tus rutinas
- Soporte multiidioma: Español, Inglés y Francés

### Social
- Búsqueda de usuarios por nombre de usuario
- Envío y recepción de solicitudes de amistad
- Lista de amigos con opción de eliminar
- Ver perfil y rutinas públicas de amigos
- Sincronización de usuarios vía Firebase

### Perfil
- Foto de perfil personalizable (se guarda localmente de forma persistente)
- Descripción de perfil editable
- Selector de idioma integrado (Español, Inglés, Francés)
- Acceso a plan de progresión

### Plan de Progresión
- Visualización del plan de entrenamiento semanal

---

## Tecnologías utilizadas

| Tecnología | Uso |
|-----------|-----|
| **Kotlin** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa |
| **Material 3** | Sistema de diseño |
| **Room** | Base de datos local |
| **Firebase Firestore** | Base de datos en la nube |
| **Navigation Compose** | Navegación entre pantallas |
| **ViewModel + StateFlow** | Arquitectura MVVM |
| **DataStore Preferences** | Preferencias persistentes |
| **Coil** | Carga de imágenes |
| **ML Kit Barcode Scanning** | Escaneo de códigos QR |
| **ZXing** | Generación de códigos QR |
| **CameraX** | Acceso a la cámara |
| **Poppins (Google Fonts)** | Tipografía |

---

## Pantallas

| Pantalla | Descripción |
|---------|-------------|
| `PantallaLogin` | Inicio de sesión con degradado visual |
| `Pantallaregistro` | Registro de nuevos usuarios |
| `PantallaPanel` | Dashboard principal con accesos rápidos |
| `PantallaRutinas` | Lista y gestión de rutinas |
| `PantallaDetalleRutina` | Ejercicios dentro de una rutina |
| `PantallaEjercicios` | Catálogo de ejercicios por grupo muscular |
| `PantallaProgreso` | Gráficas y estadísticas de progreso |
| `PantallaHistorial` | Historial de sesiones completadas |
| `PantallaPerfil` | Perfil de usuario con foto e idioma |
| `PantallaAmigos` | Lista de amigos y solicitudes |
| `PantallaBuscarUsuarios` | Búsqueda de usuarios locales y en red |
| `PantallaPerfilAmigo` | Perfil y rutinas públicas de un amigo |
| `PantallaQRRutina` | Generar y compartir QR de rutina |
| `PantallaEscanearQR` | Escanear QR de rutina de otro usuario |
| `PantallaPlanProgresion` | Plan de entrenamiento semanal |
| `Pantallakardex` | Kardex de actividades |

---

## Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)**:

```
app/
├── data/
│   ├── local/          → Room DAOs y base de datos
│   ├── model/          → Entidades (Usuario, Rutina, Ejercicio, etc.)
│   └── repository/     → Repositorios (Room + Firebase)
├── ui/
│   ├── navigation/     → NavGraph y Screen
│   ├── screens/        → Pantallas Composable
│   └── theme/          → Colores, tipografía y tema
└── viewmodel/          → ViewModels por funcionalidad
```

---

## Instalación y configuración

### Requisitos previos
- Android Studio Hedgehog o superior
- JDK 11
- Android SDK API 24 o superior
- Cuenta de Firebase con proyecto configurado

### Pasos

1. **Clonar el repositorio**
```bash
git clone https://github.com/IsmaGitHub-13/Fit-machMovil.git
cd Fit-machMovil
git checkout develop
```

2. **Configurar Firebase**
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com)
   - Registra la app con el package name `com.fic.mobile_app_base_compose`
   - Descarga `google-services.json` y colócalo en `app/`
   - Activa **Firestore Database** en modo de prueba

3. **Abrir en Android Studio**
   - File → Open → selecciona la carpeta del proyecto
   - Espera a que Gradle sincronice

4. **Ejecutar la app**
   - Conecta un dispositivo Android (API 24+) o crea un emulador
   - Presiona **Run**

---

## Soporte de idiomas

La app soporta los siguientes idiomas, seleccionables desde el perfil del usuario:

- **Español** (por defecto)
- **English**
- **Français**

---

## Equipo de desarrollo

| Integrante | Rol |
|-----------|-----|
| **Ismael Alcantara** | Desarrollo |
| **Jesus Andrik Valenzuela Piña** | Desarrollo (UI/Firebase) |
| **Adan Alfonso Sauceda** | Desarrollo |
| **Paul Vazquez** | Desarrollo |
| **Alan Tristan Briseño** | Desarrollo |

---

## Licencia

Proyecto desarrollado con fines académicos para la materia de Computación Móvil — FIC UASIN 2025.
