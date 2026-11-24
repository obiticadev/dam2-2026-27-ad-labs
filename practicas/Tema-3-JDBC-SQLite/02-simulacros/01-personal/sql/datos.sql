CREATE TABLE dept (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE
);
CREATE TABLE emp (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    salario INTEGER NOT NULL CHECK (salario > 0),
    dept_id INTEGER NOT NULL REFERENCES dept(id)
);
INSERT INTO dept VALUES (10, 'Ventas'), (20, 'Soporte'), (30, 'Archivo');
INSERT INTO emp VALUES (1, 'Ana', 1000, 10), (2, 'Luis', 1300, 10), (3, 'Omar', 1500, 20);
