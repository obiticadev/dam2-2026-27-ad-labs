#!/usr/bin/env bash
set -euo pipefail
carpeta="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
tipo="${1:-consultas}"
if (( $# )); then shift; fi
case "$tipo" in
    consultas) clase=examen.conect.AsistenteConsultas ;;
    ejercicios) clase=examen.conect.AsistenteEjercicios ;;
    entrega) clase=examen.conect.RevisarEntrega ;;
    *) echo "Uso: bash asistente.sh {consultas|ejercicios} [base.sqlite] [sesion.properties] | entrega [Fichero.java ...]" >&2; exit 2 ;;
esac
nombre=sqlite-jdbc-3.51.0.0.jar
jar="$carpeta/lib/$nombre"
if [[ ! -f "$jar" ]]; then
    echo "Falta lib/$nombre (viaja con la carpeta; no lo borres)." >&2
    exit 2
fi
cd -- "$carpeta"
mkdir -p target/asistentes
javac --release 17 -encoding UTF-8 -cp "$jar" -d target/asistentes src/main/java/examen/herramientas/*.java src/main/java/examen/conect/*.java
classpath="$carpeta/target/asistentes:$jar"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*)
        classpath="$(cygpath -m "$carpeta/target/asistentes");$(cygpath -m "$jar")"
        ;;
esac
exec java --enable-native-access=ALL-UNNAMED \
    "-Dasistente.ancho=${ASISTENTE_ANCHO:-64}" -cp "$classpath" "$clase" "$@"
