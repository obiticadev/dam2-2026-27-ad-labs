#!/usr/bin/env bash
set -euo pipefail
tema="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
opcion="${1:-plantilla}"
if (( $# )); then shift; fi
case "$opcion" in
    plantilla) proyecto="$tema/00-plantilla"; clase=examen.Main ;;
    atomicos) proyecto="$tema/01-atomicos"; clase=repaso.Atomicos ;;
    personal) proyecto="$tema/02-simulacros/01-personal"; clase=repaso.Personal ;;
    almacen) proyecto="$tema/02-simulacros/02-almacen"; clase=repaso.Almacen ;;
    *) echo "Uso: bash ejecutar.sh {plantilla|atomicos|personal|almacen} [argumentos]" >&2; exit 2 ;;
esac

# Usar el mismo JAR del pom; admite una copia local para trabajar sin internet.
nombre=sqlite-jdbc-3.51.0.0.jar
jar="$tema/00-plantilla/lib/$nombre"
if [[ ! -f "$jar" ]]; then
    jar="$tema/../../entregable/practica-evaluable-ut02/lib/$nombre"
fi
if [[ ! -f "$jar" ]]; then
    echo "Copia $nombre en $tema/00-plantilla/lib/" >&2
    exit 2
fi

# Fijar la carpeta de ejecución: datos/, sql/ y target/ ya no dependen de dónde abrió la terminal.
cd -- "$proyecto"
mkdir -p target/clases
fuentes=("$tema/00-plantilla/src/main/java/examen/"*.java)
if [[ "$opcion" != plantilla ]]; then
    fuentes+=("$proyecto/src/main/java/repaso/"*.java)
fi
javac --release 17 -encoding UTF-8 -cp "$jar" -d target/clases "${fuentes[@]}"
exec java --enable-native-access=ALL-UNNAMED -ea -cp "target/clases:$jar" "$clase" "$@"
