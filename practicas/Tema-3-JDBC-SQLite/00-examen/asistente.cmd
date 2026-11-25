@echo off
setlocal
set "CARPETA=%~dp0"
set "TIPO=%~1"
if "%TIPO%"=="" set "TIPO=consultas"
if /I "%TIPO%"=="consultas" goto consultas
if /I "%TIPO%"=="ejercicios" goto ejercicios
echo Uso: asistente.cmd consultas^|ejercicios [base.sqlite] [sesion.properties]
exit /b 2

:consultas
set "CLASE=examen.AsistenteConsultas"
goto preparar

:ejercicios
set "CLASE=examen.AsistenteEjercicios"

:preparar
set "JAR=%CARPETA%lib\sqlite-jdbc-3.51.0.0.jar"
if exist "%JAR%" goto compilar
set "JAR=%CARPETA%..\..\..\entregable\practica-evaluable-ut02\lib\sqlite-jdbc-3.51.0.0.jar"
if exist "%JAR%" goto compilar
echo Falta lib\sqlite-jdbc-3.51.0.0.jar.
exit /b 2

:compilar
pushd "%CARPETA%"
if errorlevel 1 exit /b 2
if not exist target\asistentes mkdir target\asistentes
(for %%F in (src\main\java\examen\*.java) do @echo "src/main/java/examen/%%~nxF") > target\asistentes\fuentes.txt
javac --release 17 -encoding UTF-8 -cp "%JAR%" -d target\asistentes @target\asistentes\fuentes.txt
if errorlevel 1 goto fallo
if not defined ASISTENTE_ANCHO set "ASISTENTE_ANCHO=64"
java --enable-native-access=ALL-UNNAMED "-Dasistente.ancho=%ASISTENTE_ANCHO%" -cp "target\asistentes;%JAR%" %CLASE% %2 %3
set "RESULTADO=%ERRORLEVEL%"
popd
exit /b %RESULTADO%

:fallo
popd
exit /b 1
