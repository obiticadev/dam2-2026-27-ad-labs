-- EJEMPLO PARA MainDesdeSql.java: ejecuta esa clase sin argumentos.
-- Crea datos/examen.sqlite si todavía no existe.
--
-- CUANDO TENGAS EL SQL REAL:
-- 1. Sustituye TODO este contenido por el SQL del profesor.
-- 2. En MainDesdeSql.java cambia datos/examen.sqlite por un nombre nuevo,
--    por ejemplo datos/examen-real.sqlite, para no reutilizar la base de prueba.
-- No incluyas BEGIN, COMMIT, ROLLBACK ni comandos de consola como .read.

CREATE TABLE departamentos (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE
);

CREATE TABLE empleados (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    salario REAL NOT NULL CHECK (salario >= 0),
    departamento_id INTEGER NOT NULL,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);

INSERT INTO departamentos (id, nombre) VALUES
    (1, 'Informática'),
    (2, 'Ventas'),
    (3, 'Administración');

INSERT INTO empleados (id, nombre, salario, departamento_id) VALUES
    (1, 'Ana', 1800.00, 1),
    (2, 'Luis', 1650.00, 1),
    (3, 'Marta', 1700.00, 2),
    (4, 'Pedro', 1550.00, 2);
