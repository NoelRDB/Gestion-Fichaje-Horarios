#!/bin/bash
# Script para ejecutar Gestión de Fichajes

# Obtener la ruta del directorio donde está el script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Ir al directorio del script
cd "$DIR"

echo "════════════════════════════════════════"
echo "  🏢 Gestión de Fichajes"
echo "════════════════════════════════════════"
echo ""

# Buscar Java
JAVA_CMD=""

# 1. Intentar usar java del PATH
if command -v java &> /dev/null; then
    JAVA_CMD="java"
# 2. Buscar JAVA_HOME
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
# 3. Buscar en SDKMAN
elif [ -x "$HOME/.sdkman/candidates/java/current/bin/java" ]; then
    JAVA_CMD="$HOME/.sdkman/candidates/java/current/bin/java"
# 4. Buscar en ubicaciones comunes de Linux
elif [ -x "/usr/lib/jvm/java-21-openjdk/bin/java" ]; then
    JAVA_CMD="/usr/lib/jvm/java-21-openjdk/bin/java"
elif [ -x "/usr/lib/jvm/java-21-openjdk-amd64/bin/java" ]; then
    JAVA_CMD="/usr/lib/jvm/java-21-openjdk-amd64/bin/java"
elif [ -x "/usr/bin/java" ]; then
    JAVA_CMD="/usr/bin/java"
# 5. Buscar cualquier Java disponible en /usr/lib/jvm
elif [ -d "/usr/lib/jvm" ]; then
    for jvm_dir in /usr/lib/jvm/java-*-openjdk*/bin/java; do
        if [ -x "$jvm_dir" ]; then
            JAVA_CMD="$jvm_dir"
            break
        fi
    done
fi

# Verificar si se encontró Java
if [ -z "$JAVA_CMD" ]; then
    echo "❌ ERROR: No se encontró Java"
    echo ""
    echo "Por favor, instala Java 21:"
    echo "  sudo apt install openjdk-21-jdk"
    echo ""
    echo "Presiona Enter para cerrar..."
    read
    exit 1
fi

# Verificar versión de Java
JAVA_VERSION=$($JAVA_CMD -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✓ Java encontrado: versión $JAVA_VERSION"

if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "⚠️  ADVERTENCIA: Se requiere Java 17 o superior"
    echo "   Tu versión: $JAVA_VERSION"
fi

echo ""
echo "Iniciando aplicación..."
echo ""

# Ejecutar el JAR
$JAVA_CMD -jar gestion-fichajes-0.0.1-SNAPSHOT.jar

echo ""
echo "════════════════════════════════════════"
echo "  Aplicación cerrada"
echo "════════════════════════════════════════"
echo ""
echo "Presiona Enter para cerrar esta ventana..."
read
