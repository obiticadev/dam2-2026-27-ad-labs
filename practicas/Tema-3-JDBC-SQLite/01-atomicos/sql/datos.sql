-- Relación 1:N; la columna hija se valida con PRAGMA foreign_keys de Java.
CREATE TABLE dept (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE
);
CREATE TABLE emp (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    salario INTEGER NOT NULL CHECK (salario > 0),
    comision INTEGER CHECK (comision >= 0),
    dept_id INTEGER NOT NULL REFERENCES dept(id) ON DELETE RESTRICT
);
INSERT INTO dept VALUES (10, 'Ventas'), (20, 'Soporte'), (30, 'Archivo');
INSERT INTO emp VALUES
    (1, 'Ana; Ruiz', 1000, NULL, 10),
    (2, 'Luis', 1200, 0, 10),
    (3, 'Omar', 1500, 100, 20);
