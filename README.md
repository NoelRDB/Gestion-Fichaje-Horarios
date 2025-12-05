# ✨ Gestión de Fichaje y Horarios

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge&logo=javafx&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Status-Active-success?style=for-the-badge" />
</p>

Este proyecto es un sistema completo de **gestión de fichajes laborales**, diseñado para registrar entradas, salidas y descansos de los trabajadores, así como monitorizar en tiempo real el estado de cada empleado y facilitar la administración de turnos.

Su objetivo es ofrecer una solución **rápida, intuitiva y eficiente**, especialmente útil en sectores como **hostelería**, comercios o cualquier entorno donde los horarios suelen ser variables.

---

## 🚀 Características principales

- 🕒 **Registro de fichajes**: entrada, salida y tiempos de descanso.
- 👥 **Gestión completa de empleados** desde el panel de administrador.
- 📊 **Dashboard en tiempo real** para ver actividad y fichajes activos.
- 📚 **Historial detallado** de jornadas, tiempos y descansos.
- 🔐 **Sistema de roles** (Administrador / Trabajador).
- 🧩 Interfaz clara, moderna y fácil de utilizar.

---

## 🎯 Objetivo del proyecto

Crear una herramienta accesible, clara y rápida que permita a cualquier empresa:

- Cumplir con la normativa de control horario
- Consultar datos en tiempo real
- Facilitar la organización interna
- Optimizar la gestión del personal

---

## 📸 Capturas de pantalla

A continuación se muestran algunas vistas principales del sistema.

### 🔐 Pantalla de Login

<p align="center">
  <img width="450" height="450" src="https://github.com/user-attachments/assets/b9529326-8b81-415e-b213-ea7cbdae9c47" alt="Pantalla de Login" />
</p>

### 🛠️ Panel de Administrador

<p align="center">
  <img width="900" src="https://github.com/user-attachments/assets/42dce6fa-4f6e-46ce-a07b-9a66bccf5250" alt="Panel administrador" />
</p>

### 👨‍🔧 Panel del Trabajador

<p align="center">
  <img width="800" src="https://github.com/user-attachments/assets/da23e6c2-bb6d-4a6d-80ad-2542de72191b" alt="Panel trabajador" />
</p>

---

## 🧩 Tecnologías utilizadas

- **Java 21** - Lenguaje principal
- **Spring Boot 4.0** - Framework enterprise
- **Spring Data JPA** - ORM y persistencia de datos
- **Spring Validation** - Validación de datos
- **JavaFX 21** - Interfaz gráfica de escritorio
- **FXML** - Lenguaje de marcado para UI
- **H2 Database** - Base de datos relacional embebida
- **Maven** - Gestor de dependencias y build
- **Arquitectura MVC** - Patrón de diseño

---

## 📂 Estructura general del proyecto

```
gestion-fichajes/
├── src/
│   ├── main/
│   │   ├── java/com/noelrdb/gestion_fichajes/
│   │   │   ├── GestionFichajesApplication.java       # Main de Spring Boot
│   │   │   ├── JavaFXApplication.java                # Main de JavaFX
│   │   │   ├── FxmlView.java                         # Enumeración de vistas FXML
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java              # Inicialización de datos
│   │   │   ├── javafx/
│   │   │   │   ├── StageManager.java                 # Gestor de ventanas
│   │   │   │   ├── controller/
│   │   │   │   │   ├── LoginController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   ├── AdminDashboardController.java
│   │   │   │   │   └── SessionManager.java
│   │   │   ├── menu/
│   │   │   │   └── controller/
│   │   │   │       └── MenuController.java
│   │   │   ├── signing/                              # Módulo de fichajes
│   │   │   │   ├── controller/
│   │   │   │   │   └── SigningController.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── Signing.java                  # Entidad JPA
│   │   │   │   ├── dto/                              # Data Transfer Objects
│   │   │   │   ├── repository/
│   │   │   │   │   └── SigningRepository.java        # JPA Repository
│   │   │   │   └── service/
│   │   │   │       └── SigningService.java           # Lógica de negocio
│   │   │   └── worker/                               # Módulo de trabajadores
│   │   │       ├── controller/
│   │   │       │   └── WorkerController.java
│   │   │       ├── entity/
│   │   │       │   └── Worker.java                   # Entidad JPA
│   │   │       ├── dto/                              # Data Transfer Objects
│   │   │       ├── repository/
│   │   │       │   └── WorkerRepository.java         # JPA Repository
│   │   │       └── service/
│   │   │           └── WorkerService.java            # Lógica de negocio
│   │   └── resources/
│   │       ├── application.properties                # Configuración Spring
│   │       ├── fxml/                                 # Vistas FXML
│   │       │   ├── login.fxml
│   │       │   ├── dashboard.fxml
│   │       │   └── admin-dashboard.fxml
│   │       └── static/                               # Recursos estáticos
│   └── test/
│       └── java/com/noelrdb/gestion_fichajes/
│           └── GestionFichajesApplicationTests.java
├── target/                                           # Compilados (ignorar)
├── distribuible/                                     # ← EJECUTABLE PARA USUARIOS
│   ├── ejecutar.sh
│   ├── ejecutar.bat
│   ├── gestion-fichajes-0.0.1-SNAPSHOT.jar
│   ├── data/
│   └── README.md
├── data/                                             # Base de datos H2
├── pom.xml                                           # Configuración Maven
├── HELP.md
└── README.md                                         # Este archivo
```

---

## ⚡ Inicio Rápido

### Para desarrolladores:

```bash
# Clonar el repositorio
git clone <tu-repositorio>
cd gestion-fichajes

# Compilar el proyecto
mvn clean package -DskipTests

# Ejecutar la aplicación
mvn javafx:run
```

### Para usuarios finales:

**Ve a la carpeta `distribuible/` y sigue las instrucciones del README**

- **Linux/Mac:** Ejecutar `./ejecutar.sh`
- **Windows:** Ejecutar `ejecutar.bat`

---

## 💾 Base de Datos

- **Motor:** H2 Database (embebida)
- **Ubicación:** `data/gestion-fichajes-db.mv.db`
- **Persistencia:** Los datos se guardan automáticamente en el archivo local

### Usuarios iniciales de prueba:

| Rol | DNI | Código |
|-----|-----|--------|
| Admin | `admin` | `0` |
| Trabajador | `12345678A` | `1234` |
| Trabajador | `87654321B` | `5678` |
| Trabajador | `11223344C` | `9999` |

---

## 📦 Distribución

Todo listo en la carpeta **`distribuible/`**:

- `ejecutar.sh` (Linux/Mac)
- `ejecutar.bat` (Windows)
- `gestion-fichajes-0.0.1-SNAPSHOT.jar`
- `README.md` (instrucciones para usuarios)
- `data/` (carpeta para la base de datos)

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura MVC** con capas de separación:

- **Controller**: Gestiona la lógica de interacción con la UI (JavaFX)
- **Service**: Contiene la lógica de negocio principal
- **Repository**: Acceso a datos mediante Spring Data JPA
- **Entity**: Modelos de datos persistentes
- **DTO**: Objetos de transferencia de datos

### Patrones utilizados:

- **MVC** - Separación de responsabilidades
- **Spring Dependency Injection** - Inyección de dependencias
- **Repository Pattern** - Abstracción de acceso a datos
- **Service Layer** - Lógica centralizada

---

## 🔧 Requisitos del sistema

- **Java 21 o superior** (incluido JRE)
- **Sistema operativo:** Windows, Linux o macOS

---

## 📝 Notas de desarrollo

- El proyecto utiliza **H2 Database** para facilitar la distribución (sin servidor externo)
- Las vistas se definen en **FXML** para mejor mantenibilidad
- Spring Boot gestiona la inyección de dependencias automáticamente
- Los datos se cargan en `DataInitializer` al inicio de la aplicación

---

**v0.0.1** | Diciembre 2025
