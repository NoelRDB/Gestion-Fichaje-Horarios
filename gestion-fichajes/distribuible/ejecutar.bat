@echo off
REM Script para ejecutar Gestion de Fichajes en Windows

echo ========================================
echo   Gestion de Fichajes
echo ========================================
echo.
echo Iniciando aplicacion...
echo.

REM Ir al directorio del script
cd /d "%~dp0"

REM Ejecutar el JAR
java -jar gestion-fichajes-0.0.1-SNAPSHOT.jar

echo.
echo ========================================
echo   Aplicacion cerrada
echo ========================================
echo.
echo Presiona cualquier tecla para cerrar...
pause >nul
