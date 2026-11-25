@echo off
setlocal
set "CARPETA=%~dp0"
set "TIPO=%~1"
if "%TIPO%"=="" set "TIPO=consultas"
if /I "%TIPO%"=="consultas" goto consultas
if /I "%TIPO%"=="ejercicios" goto ejercicios
if /I "%TIPO%"=="entrega" goto entrega
echo Uso: b_conect.cmd consultas^|ejercicios [base.sqlite] [sesion.properties] ^| entrega [Fichero.java ...]
exit /b 2

:consultas
set "CLASE=examen.b_conect.Bravo"
goto preparar

:ejercicios
set "CLASE=examen.b_conect.Charlie"
goto preparar

:entrega
set "CLASE=examen.b_conect.Foxtrot"

:preparar
set "JAR=%CARPETA%lib\sqlite-jdbc-3.51.0.0.jar"
if exist "%JAR%" goto compilar
echo Falta lib\sqlite-jdbc-3.51.0.0.jar.
exit /b 2

:compilar
pushd "%CARPETA%"
if errorlevel 1 exit /b 2
if not exist target\asistentes mkdir target\asistentes
(for %%F in (src\main\java\examen\herramientas\*.java) do @echo "src/main/java/examen/herramientas/%%~nxF"
 for %%F in (src\main\java\examen\b_conect\*.java) do @echo "src/main/java/examen/b_conect/%%~nxF") > target\asistentes\fuentes.txt
javac --release 17 -encoding UTF-8 -cp "%JAR%" -d target\asistentes @target\asistentes\fuentes.txt
if errorlevel 1 goto fallo
if not defined ASISTENTE_ANCHO set "ASISTENTE_ANCHO=64"
java --enable-native-access=ALL-UNNAMED "-Dasistente.ancho=%ASISTENTE_ANCHO%" -cp "target\asistentes;%JAR%" %CLASE% %2 %3 %4 %5
set "RESULTADO=%ERRORLEVEL%"
popd
exit /b %RESULTADO%

:fallo
popd
exit /b 1
