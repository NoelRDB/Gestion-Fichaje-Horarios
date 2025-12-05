# 🏢 Sistema de Gestión de Fichajes

Sistema de gestión de fichajes de trabajadores con interfaz gráfica JavaFX.

## 🚀 Inicio Rápido

### Para desarrolladores:
```bash
mvn clean package -DskipTests
mvn javafx:run
```

### Para usuarios finales:
**Ve a la carpeta `distribuible/` y sigue las instrucciones del README**

## 📦 Distribución

Todo listo en la carpeta **`distribuible/`**:
- `ejecutar.sh` (Linux/Mac)
- `ejecutar.bat` (Windows)
- `gestion-fichajes-0.0.1-SNAPSHOT.jar`
- `README.md` (instrucciones para usuarios)

Comparte el archivo: **`GestionFichajes-v0.0.1.zip`**

## 💾 Base de Datos

Los datos se guardan en `data/gestion-fichajes-db.mv.db` (persistente)

## 👤 Usuarios Iniciales

- **Admin:** DNI `admin` | Código `0`
- **Juan García:** DNI `12345678A` | Código `1234`
- **María López:** DNI `87654321B` | Código `5678`
- **Pedro Martínez:** DNI `11223344C` | Código `9999`

## 🛠️ Tecnologías

- Java 21
- JavaFX 21
- Spring Boot 4.0
- H2 Database
- Maven

## 📁 Estructura

```
gestion-fichajes/
├── src/                    # Código fuente
├── target/                 # Compilados
├── distribuible/           # ← EJECUTABLE PARA USUARIOS
│   ├── ejecutar.sh
│   ├── ejecutar.bat
│   ├── *.jar
│   └── README.md
├── data/                   # Base de datos
├── pom.xml
└── README.md               # Este archivo
```

---

**v0.0.1** | Diciembre 2025
