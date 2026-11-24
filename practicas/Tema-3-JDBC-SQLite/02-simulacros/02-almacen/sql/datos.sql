CREATE TABLE producto (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE,
    stock INTEGER NOT NULL CHECK (stock >= 0)
);
CREATE TABLE salida (
    referencia TEXT NOT NULL PRIMARY KEY,
    producto_id INTEGER NOT NULL REFERENCES producto(id),
    unidades INTEGER NOT NULL CHECK (unidades > 0)
);
INSERT INTO producto VALUES (1, 'Teclado', 10), (2, 'Ratón', 5);
