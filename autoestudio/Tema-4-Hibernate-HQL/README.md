# Tema 4 · Hibernate, ORM y HQL

Proyecto independiente con Java 17, **Hibernate 5.4.33.Final**, `javax.persistence`,
H2 1.4.200 y ensayo SQLite con dialecto externo 0.1.2. Versiones del material docente,
no recomendación para producción. No mezclar con Hibernate 6/7 ni `jakarta.persistence`.

1. [B00 · Configuración rápida](00-CONFIGURACION.md).
2. [B01–B12 · Mapeo, CRUD, relaciones, estados y consultas](BLOQUES.md).
3. [Cobertura y referencia a la teoría](COBERTURA.md).
4. [Verificación real](../VERIFICACION_TEMAS_3_4.md).

**No forma parte de las seis horas del examen del tema 3**, según el alcance de
Obsidian. Está preparado para practicarlo después, sin saltarse la configuración.
Recorrido sugerido independiente: B00–B02 → B03–B05 → B06–B09 → B10–B12. No se
asigna una duración garantizada a dominar todas las relaciones y consultas.

## Cómo se practica

- `src/main/java/lab/Arranque.java`: arranque completo de referencia.
- `src/main/java/lab/Modelo.java`: modelo de apoyo ya mapeado. Para aprender las
  anotaciones, B01/B04 piden reproducir partes desde cero en un proyecto de ensayo;
  no se han dejado anotaciones rotas que impidan arrancar todos los demás bloques.
- `src/main/java/lab/Orm.java`: métodos por completar. Reciben sesión con transacción;
  no deben cerrar ni confirmar recursos que pertenecen al llamante.
- `src/test/java/lab/OrmTest.java`: comprueba en nuevas sesiones, estados y datos reales.
- `soluciones/lab/Orm.java`: referencia separada. No se compila en la ruta normal.

Desde esta carpeta:

```bash
mvn -o test -Dgroups=b00
mvn -o test -Dgroups=b06
python3 ../herramientas/laboratorio.py arranque
python3 ../herramientas/laboratorio.py soluciones
```

La última orden verifica **la referencia en una copia temporal**, no tus ejercicios.
`mvn -o test` prueba tus intentos y fallará hasta completarlos. No se han añadido
estos tests al reactor antiguo para no interferir con ficheros/XML.
