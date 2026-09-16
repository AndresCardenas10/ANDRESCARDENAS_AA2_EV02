-- ============================================================
-- SGB — Sistema Gestión Barbería
-- Datos de prueba: tabla SERVICIO
-- Evidencia GA7-220501096-AA2-EV02
-- ============================================================
-- La tabla `servicio` en sgb.sql se crea vacía (no trae datos de
-- ejemplo). El módulo de citas necesita al menos un servicio para
-- poder crear una cita de prueba, así que este script inserta unos
-- cuantos servicios típicos de una barbería.
--
-- Ejecuta este script en phpMyAdmin (pestaña SQL, con la base `sgb`
-- ya seleccionada) DESPUÉS de haber importado sgb.sql.
-- ============================================================

USE sgb;

INSERT INTO servicio (nombre, descripcion, precio, duracion_min, activo) VALUES
    ('Corte de cabello', 'Corte clásico a máquina y tijera', 20000, 30, 1),
    ('Afeitado clásico', 'Afeitado con toalla caliente y navaja', 15000, 20, 1),
    ('Corte + barba', 'Corte de cabello y arreglo de barba', 30000, 45, 1),
    ('Tinte de cabello', 'Aplicación de tinte y lavado', 40000, 60, 1);
