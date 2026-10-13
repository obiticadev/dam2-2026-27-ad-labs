#!/usr/bin/env sh
set -eu

SCRIPT_DIR="$(CDPATH= cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p bin
javac --release 11 -encoding UTF-8 -cp "lib/*" -d bin \
    src/ejercicioxml/LeerDepartamentosEmpleadosDOM.java \
    src/ejercicioxml/CrearDatosEmpresaJAXB.java

case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) CLASSPATH="bin;lib/*" ;;
    *) CLASSPATH="bin:lib/*" ;;
esac

exec java -cp "$CLASSPATH" ejercicioxml.CrearDatosEmpresaJAXB
