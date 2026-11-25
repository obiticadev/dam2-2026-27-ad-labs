#!/usr/bin/env bash
set -euo pipefail
tema="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
opcion="${1:-desde-sql}"
if (( $# )); then shift; fi
case "$opcion" in
    desde-sql) proyecto="$tema/00-examen"; clase=examen.MainDesdeSql ;;
    desde-sqlite) proyecto="$tema/00-examen"; clase=examen.MainDesdeSqlite ;;
    consultas|ejercicios|entrega) exec bash "$tema/00-examen/b_conect.sh" "$opcion" "$@" ;;
    pruebas) proyecto="$tema/00-examen"; clase=examen.herramientas.PruebasSqlite ;;
    pruebas-asistentes) proyecto="$tema/00-examen"; clase=examen.b_conect.Hotel ;;
    atomicos) proyecto="$tema/01-atomicos"; clase=repaso.Atomicos ;;
    personal) proyecto="$tema/02-simulacros/01-personal"; clase=repaso.Personal ;;
    almacen) proyecto="$tema/02-simulacros/02-almacen"; clase=repaso.Almacen ;;
    *) echo "Uso: bash ejecutar.sh {desde-sql|desde-sqlite|consultas|ejercicios|entrega|atomicos|personal|almacen|pruebas|pruebas-asistentes} [argumentos]" >&2; exit 2 ;;
esac

# El driver viaja con la carpeta: es el mismo JAR que declara el pom.
nombre=sqlite-jdbc-3.51.0.0.jar
jar="$tema/00-examen/lib/$nombre"
if [[ ! -f "$jar" ]]; then
    echo "Falta $tema/00-examen/lib/$nombre" >&2
    exit 2
fi

# Fijar la carpeta de ejecución: datos/, sql/ y la salida no dependen de dónde abrió la terminal.
cd -- "$proyecto"
salida=target
mkdir -p "$salida/clases"
fuentes=()
# Bucle en vez de mapfile: el bash 3 de macOS no lo tiene.
while IFS= read -r f; do fuentes+=("$f"); done < <(find "$tema/00-examen/src/main/java" -name '*.java')
if [[ "$clase" == repaso.* ]]; then
    fuentes+=("$proyecto/src/main/java/repaso/"*.java)
fi
if [[ "$opcion" == pruebas* ]]; then
    while IFS= read -r f; do fuentes+=("$f"); done < <(find "$tema/00-examen/src/test/java" -name '*.java')
fi
javac --release 17 -encoding UTF-8 -cp "$jar" -d "$salida/clases" "${fuentes[@]}"
classpath="$salida/clases:$jar"
case "$(uname -s)" in
    # Git Bash en Windows: java.exe espera rutas C:/... separadas por ;
    MINGW*|MSYS*|CYGWIN*) classpath="$salida/clases;$(cygpath -m "$jar")" ;;
esac
exec java --enable-native-access=ALL-UNNAMED -ea -cp "$classpath" "$clase" "$@"
