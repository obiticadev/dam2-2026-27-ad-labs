#!/usr/bin/env python3
"""Ayudas locales de temas 3/4. Nunca sustituye los intentos del alumno.

Desde cualquiera de los dos proyectos: python3 ../herramientas/laboratorio.py ...
"""
import argparse
import os
from pathlib import Path
import shutil
import subprocess
import tempfile

RAIZ = Path(__file__).resolve().parents[1]
PROYECTOS = ("Tema-3-JDBC-SQLite", "Tema-4-Hibernate-HQL")


def ejecutar(args, cwd):
    subprocess.run([str(a) for a in args], cwd=cwd, check=True)


def proyecto_actual():
    p = Path.cwd().resolve()
    if p.name not in PROYECTOS or p.parent != RAIZ:
        raise SystemExit("Ejecuta la orden desde Tema-3-JDBC-SQLite o Tema-4-Hibernate-HQL.")
    return p


def compilar_manual(p):
    if not (p / "lib").is_dir() or not list((p / "lib").glob("*.jar")):
        raise SystemExit("Falta lib/: ejecuta primero preparar (con dependencias ya disponibles).")
    salida = p / "target/manual"
    salida.mkdir(parents=True, exist_ok=True)
    fuentes = sorted((p / "src/main/java").rglob("*.java"))
    ejecutar(["javac", "--release", "17", "-encoding", "UTF-8", "-cp", str(p / "lib/*"), "-d", salida, *fuentes], p)
    recursos = p / "src/main/resources"
    if recursos.exists():
        shutil.copytree(recursos, salida, dirs_exist_ok=True)
    return salida


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("accion", choices=["preparar", "arranque", "manual", "soluciones", "kit"])
    parser.add_argument("--online", action="store_true", help="Permitir a Maven descargar; solo preparar")
    parser.add_argument("--destino", type=Path, help="Carpeta NUEVA para kit portable")
    args = parser.parse_args()
    p = proyecto_actual()
    if args.online and args.accion != "preparar":
        parser.error("--online solo es válido con preparar")
    if args.accion == "preparar":
        offline = [] if args.online else ["-o"]
        ejecutar(["mvn", *offline, "test", "-Dgroups=b00"], p)
        ejecutar(["mvn", *offline, "org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies",
                  "-DincludeScope=runtime", "-DoutputDirectory=lib"], p)
        compilar_manual(p)
        print("Listo. Ahora prueba arranque y mvn -o clean test -Dgroups=b00.")
    elif args.accion in ("arranque", "manual"):
        salida = compilar_manual(p)
        if args.accion == "arranque":
            ejecutar(["java", "-cp", os.pathsep.join([str(salida), str(p / "lib/*")]), "lab.Arranque"], p)
    elif args.accion == "soluciones":
        # Siempre compilación limpia en copia temporal: no mezcla .class resueltos
        # con los del alumno ni modifica src/main/java.
        base = Path("/tmp/opencode")
        with tempfile.TemporaryDirectory(prefix="ad-soluciones-", dir=base if base.exists() else None) as tmp:
            copia = Path(tmp) / p.name
            shutil.copytree(p, copia, ignore=shutil.ignore_patterns("target", "lib", "datos", ".git", "kit-*"))
            for fuente in (p / "soluciones").rglob("*.java"):
                destino = copia / "src/main/java" / fuente.relative_to(p / "soluciones")
                shutil.copy2(fuente, destino)
            ejecutar(["mvn", "-o", "test"], copia)
    elif args.accion == "kit":
        if not args.destino:
            parser.error("kit requiere --destino /ruta/nueva")
        destino = args.destino.expanduser().resolve()
        if destino.exists():
            raise SystemExit("El destino ya existe; elige uno nuevo. No se sobrescribe.")
        if destino == RAIZ or RAIZ in destino.parents:
            raise SystemExit("Guarda el kit fuera de autoestudio para evitar copias recursivas.")
        if any(not list((RAIZ / nombre / "lib").glob("*.jar")) for nombre in PROYECTOS):
            raise SystemExit("Prepara primero las dependencias de AMBOS temas con preparar.")
        destino.mkdir(parents=True)
        for nombre in PROYECTOS:
            shutil.copytree(RAIZ / nombre, destino / nombre, ignore=shutil.ignore_patterns("target", "datos", ".git"))
        shutil.copytree(RAIZ / "herramientas", destino / "herramientas", ignore=shutil.ignore_patterns("__pycache__"))
        for nombre in ("RUTA_6_HORAS.md", "VERIFICACION_TEMAS_3_4.md"):
            shutil.copy2(RAIZ / nombre, destino / nombre)
        print("Kit con JAR para javac/java:", destino)
        print("Necesitas JDK 17+ y Python 3. NO incluye Maven ni su repositorio local.")


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError as error:
        raise SystemExit(error.returncode)
