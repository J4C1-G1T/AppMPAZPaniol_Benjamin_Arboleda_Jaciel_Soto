#  MPAZ Pañol - Sistema Móvil de Gestión de Préstamos



**MPAZ Pañol** es una aplicación móvil nativa desarrollada para Android orientada a optimizar el control, la solicitud y la administración de préstamos de equipos en pañoles institucionales. El sistema implementa un entorno multi-rol con persistencia de datos local y diseño declarativo moderno.

---

##  Integrantes del Proyecto

* **Jaciel Jesús Ricardo Soto Nahuelpi** (@J4C1-G1T) - Rol Solicitante, Persistencia Room e Integración.
* **Benjamín Arboleda** - Rol Administrador, Gestión de Inventario y Control de Estados.

---

##  Tecnologías y Arquitectura

La aplicación ha sido construida siguiendo los estándares de la asignatura **DSY1105 (Desarrollo de Aplicaciones Móviles)**:

* **Lenguaje:** Kotlin 2.0+ (Sintaxis moderna, Corutinas, Flow y Null Safety).
* **Interfaz de Usuario:** Jetpack Compose (Material Design 3, Layouts declarativos, Scaffold, State Management).
* **Patrón Arquitectónico:** MVVM (Model - View - ViewModel) desacoplado mediante `StateFlow` y `sealed class` para manejo de estados de UI.
* **Persistencia Local:** Room Database sobre SQLite (Entidades, DAOs, Migraciones e inyección de contexto mediante Singleton).
* **Navegación:** Jetpack Compose Navigation (`NavHost`, `NavController`) para flujos entre pantallas.
* **Control de Versiones & Metodología:** Git, GitHub (Feature Branch Workflow, Pull Requests) y Trello (Kanban Ágil).

---

##  Reglas de Negocio Implementadas

1. **Anticipación Mínima de Reserva:** Las solicitudes de préstamo de equipos deben realizarse con un mínimo estricto de **3 horas de anticipación**. Esta regla está validada tanto en la vista (`SolicitudScreen`) como en la capa lógica del ViewModel (`SolicitanteViewModel`).
2. **Control de Estados Multi-Rol:**
    * **Solicitante:** Puede navegar por el catálogo de equipos disponibles, registrar solicitudes con su motivo y consultar su historial en tiempo real.
    * **Administrador:** Puede revisar el panel de solicitudes entrantes, cambiar estados ("Pendiente", "Aprobado", "Rechazado") y gestionar el stock del inventario.

---

## ️ Estructura del Proyecto (MVVM)

```text
com.proyecto.appmpazpaniol/
├── data/               # Capa de Persistencia Room (Entities, DAOs, AppDatabase, Repository)
│   ├── AppDatabase.kt
│   ├── PaniolRepository.kt
│   ├── SolicitudDao.kt
│   └── SolicitudEntity.kt
├── model/              # Modelos de Datos y DTOs del dominio
│   ├── AdminModel.kt
│   └── Equipo.kt
├── ui/                 # Componentes Visuales con Jetpack Compose
│   ├── navigation/     # Grafo de Navegación (AppNavigation, Screen)
│   ├── screens/        # Pantallas (CatalogoScreen, SolicitudScreen, MisSolicitudesScreen, AdminScreen, etc.)
│   └── theme/          # Estilos y Paleta de Colores Material3
├── viewmodel/          # Lógica de Presentación (SolicitanteViewModel, AdminViewModel)
└── MainActivity.kt     # Punto de entrada principal de la aplicación
```

---

##  Requisitos para Compilar y Ejecutar

1. **Android Studio:** Koala / Ladybug o superior (JDK 17 o 21 configurado).
2. **SDK Mínimo:** Android 8.0 (API Level 26) o superior.
3. **Dispositivo:** Emulador AVD (ej. Pixel 7 Pro en API 34) o dispositivo físico con depuración USB activa.

### Pasos de Instalación:

1. Clonar el repositorio
   git clone https://github.com/J4C1-G1T/AppMPAZPaniol_Benjamin_Arboleda_Jaciel_Soto.git

2. Abrir el proyecto en Android Studio
   Esperar a que Gradle ejecute la sincronización inicial (Sync Now)
3. Ejecutar la aplicación
   Seleccionar el emulador en la barra superior y presionar Run 'app' (Shift + F10)

## Flujo de trabajo de ramas

`main` es la rama principal y la version que se debe abrir y ejecutar en el emulador. Todas las ramas de trabajo deben partir de `main` y volver a ella mediante un Pull Request.

Para iniciar una rama nueva:

```bash
git fetch origin
git switch main
git pull --ff-only origin main
git switch -c feature/nombre-del-cambio
```

Antes de abrir un Pull Request, integra los cambios recientes de `main` y ejecuta las verificaciones:

```bash
git fetch origin
git merge origin/main
.\gradlew.bat testDebugUnitTest assembleDebug
```

Los Pull Requests hacia `main` ejecutan estas mismas verificaciones automaticamente en GitHub Actions.