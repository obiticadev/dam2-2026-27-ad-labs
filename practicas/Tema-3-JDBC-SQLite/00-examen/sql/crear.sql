-- EJEMPLO PARA MainDesdeSql.java: ejecuta esa clase sin argumentos.
-- Crea datos/examen.sqlite si todavía no existe.
--
-- CUANDO TENGAS EL SQL REAL:
-- 1. Sustituye TODO este contenido por el SQL del profesor.
-- 2. En MainDesdeSql.java cambia datos/examen.sqlite por un nombre nuevo,
--    por ejemplo datos/examen-real.sqlite, para no reutilizar la base de prueba.
-- No incluyas BEGIN, COMMIT, ROLLBACK ni comandos de consola como .read.


PRAGMA foreign_keys = ON;

CREATE TABLE material (
codigo TEXT NOT NULL PRIMARY KEY,
nombre TEXT NOT NULL,
categoria TEXT NOT NULL,
unidades_disponibles INTEGER NOT NULL
);

CREATE TABLE prestamo (
numero INT NOT NULL PRIMARY KEY,
codigo_material TEXT NOT NULL,
alumno TEXT NOT NULL,
FOREIGN KEY (codigo_material) REFERENCES material(codigo)
);

INSERT INTO material (codigo, nombre, categoria, unidades_disponibles)
VALUES ('R01', 'Cable de red', 'REDES', 3),
('R02', 'Switch de 8 puertos', 'REDES', 1),
('R03', 'Router', 'REDES', 0),
('I01', 'Proyector', 'IMAGEN', 0);

