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

## Requisitos para compilar y ejecutar

1. **Android Studio:** una versión compatible con Android Gradle Plugin 9.4.1.
2. **JDK:** 17 o superior.
3. **Android SDK:** API 37 para compilar; Android Studio puede instalarla al sincronizar el proyecto.
4. **Dispositivo:** Android 13 (API 33) o superior, en emulador o dispositivo físico con depuración USB activa. La aplicación no se ejecuta en Android 8.

### Pasos de Instalación:

1. Clonar el repositorio
   git clone https://github.com/J4C1-G1T/AppMPAZPaniol_Benjamin_Arboleda_Jaciel_Soto.git

2. Extraer el ZIP y abrir en Android Studio la carpeta raíz que contiene `settings.gradle.kts`.
3. Esperar la sincronización de Gradle y aceptar la instalación de los componentes del SDK que solicite.
4. Seleccionar un emulador con Android 13 (API 33) o superior y ejecutar la configuración `app`.

El ZIP contiene el estado de una sola rama (no el historial ni las demás ramas). Para obtener el código más reciente, descargar el ZIP de la rama `main`; no seleccionar una rama `feature`.

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