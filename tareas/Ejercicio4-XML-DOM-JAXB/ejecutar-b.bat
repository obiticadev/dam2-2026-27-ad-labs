@echo off
setlocal
cd /d "%~dp0"

if not exist bin mkdir bin
javac --release 11 -encoding UTF-8 -cp "lib/*" -d bin src\ejercicioxml\ListadoDepartamentosEmpleados.java src\ejercicioxml\CrearDatosEmpresa.java
if errorlevel 1 exit /b 1

java -cp "bin;lib/*" ejercicioxml.CrearDatosEmpresa
