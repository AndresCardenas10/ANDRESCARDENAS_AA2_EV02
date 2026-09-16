-- ============================================================
-- SGB — Sistema Gestión Barbería
-- Script de creación: módulo de LOGIN / USUARIOS
-- Evidencia GA7-220501096-AA2-EV01
-- ============================================================
-- NOTA IMPORTANTE:
-- Estas tablas (ROL y USUARIO) representan el modelo asumido para este
-- módulo, construido a partir de las historias de usuario (RF01-RF11,
-- RNF01-RNF12) y del estándar de tres roles (Dueño/Administrador,
-- Barbero, Cliente) definido en la fase de análisis del proyecto.
--
-- Si tu script DDL original (el que ya ejecutaste en MySQL Workbench
-- con las 10 tablas: ROL, USUARIO, SERVICIO, HORARIO_BARBERO, CITA,
-- CITA_SERVICIO, PAGO, NOTIFICACION, INVENTARIO, MOV_INVENTARIO) usa
-- nombres de columnas distintos a los de aquí, AJUSTA este script y
-- las clases Usuario.java / UsuarioDAO.java para que coincidan
-- exactamente con tu base de datos real. Así evitas duplicar
-- estructuras o romper la trazabilidad con tus artefactos anteriores.
-- ============================================================

CREATE DATABASE IF NOT EXISTS sgb_bd
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sgb_bd;

-- ── Tabla ROL ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rol (
    id_rol     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(30) NOT NULL UNIQUE
);

-- ── Tabla USUARIO ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    correo         VARCHAR(150) NOT NULL UNIQUE,
    clave          VARCHAR(255) NOT NULL,
    telefono       VARCHAR(20),
    id_rol         INT NOT NULL,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- ── Datos base de roles ────────────────────────────────────
INSERT INTO rol (nombre_rol) VALUES
    ('Administrador'),
    ('Barbero'),
    ('Cliente');

-- ── Usuario de prueba para validar el login ────────────────
-- correo: admin@demo.com | clave: 123456
INSERT INTO usuario (nombre, correo, clave, telefono, id_rol)
VALUES ('Administrador Demo', 'admin@demo.com', '123456', '3000000000', 1);
