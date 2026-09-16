-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3307
-- Tiempo de generación: 06-09-2026 a las 02:20:22
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sgb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cita`
--

CREATE TABLE `cita` (
  `id_cita` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_barbero` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `estado` enum('pendiente','confirmada','cancelada','completada') NOT NULL DEFAULT 'pendiente',
  `expira_en` datetime DEFAULT NULL COMMENT 'Límite para confirmar; NULL = no expira',
  `notas` text DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Citas programadas entre clientes y barberos';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cita_servicio`
--

CREATE TABLE `cita_servicio` (
  `id_cita` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `precio_aplicado` decimal(8,2) NOT NULL COMMENT 'Precio del servicio al momento de la cita'
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `horario_barbero`
--

CREATE TABLE `horario_barbero` (
  `id_horario` int(11) NOT NULL,
  `id_barbero` int(11) NOT NULL,
  `dia_semana` tinyint(4) NOT NULL COMMENT '1=Lun, 2=Mar, ..., 7=Dom',
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

CREATE TABLE `notificacion` (
  `id_notificacion` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `tipo` varchar(50) NOT NULL COMMENT 'cita | pago | recordatorio | sistema',
  `mensaje` text NOT NULL,
  `leida` tinyint(1) NOT NULL DEFAULT 0,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp()
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `id_pago` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `monto` decimal(8,2) NOT NULL,
  `metodo_pago` enum('efectivo','transferencia') NOT NULL,
  `url_comprobante` varchar(500) DEFAULT NULL COMMENT 'URL del comprobante; solo aplica para transferencia',
  `estado_pago` enum('pendiente','verificado','rechazado') NOT NULL DEFAULT 'pendiente',
  `registrado_en` timestamp NOT NULL DEFAULT current_timestamp()
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio` decimal(8,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `resena`
--

CREATE TABLE `resena` (
  `id_resena` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `calificacion` tinyint(4) NOT NULL COMMENT '1 a 5 estrellas',
  `comentario` text DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp()
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `id_rol` int(11) NOT NULL,
  `nombre_rol` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Roles de usuario del sistema';

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_rol`, `nombre_rol`) VALUES
(1, 'Administrador'),
(2, 'Barbero'),
(3, 'Cliente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicio`
--

CREATE TABLE `servicio` (
  `id_servicio` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio` decimal(8,2) NOT NULL,
  `duracion_min` int(11) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `contrasena_hash` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `token_recuperacion` varchar(255) DEFAULT NULL,
  `expira_token` datetime DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp()
) ;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `id_rol`, `nombre`, `apellido`, `correo`, `contrasena_hash`, `telefono`, `token_recuperacion`, `expira_token`, `activo`, `creado_en`) VALUES
(1, 1, 'Admin', 'Demo', 'admin@demo.com', '123456', '3000000000', NULL, NULL, 1, '2026-09-06 00:02:00'),
(2, 2, 'moises', 'goat', 'goatmoises@gmail.com', 'elgoat123', '0000000001', NULL, NULL, 1, '2026-09-06 00:03:40'),
(3, 1, 'Andresito', 'cardenas', 'cardenas@gmail.com', '00007', '700000000', NULL, NULL, 1, '2026-09-06 00:11:30'),
(4, 2, 'Carlos', 'Ramirez', 'carlos.barbero@demo.com', '123456', '3001111111', NULL, NULL, 1, '2026-09-06 00:16:51'),
(5, 2, 'Julian', 'Gomez', 'julian.barbero@demo.com', '123456', '3002222222', NULL, NULL, 1, '2026-09-06 00:16:51'),
(6, 3, 'Laura', 'Martinez', 'laura.cliente@demo.com', '123456', '3003333333', NULL, NULL, 1, '2026-09-06 00:16:51'),
(7, 3, 'Santiago', 'Perez', 'santiago.cliente@demo.com', '123456', '3004444444', NULL, NULL, 1, '2026-09-06 00:16:51'),
(8, 3, 'Valentina', 'Torres', 'valentina.cliente@demo.com', '123456', '3005555555', NULL, NULL, 1, '2026-09-06 00:16:51');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cita`
--
ALTER TABLE `cita`
  ADD PRIMARY KEY (`id_cita`),
  ADD KEY `fk_cita_cliente` (`id_cliente`),
  ADD KEY `fk_cita_barbero` (`id_barbero`),
  ADD KEY `fk_cita_servicio` (`id_servicio`);

--
-- Indices de la tabla `cita_servicio`
--
ALTER TABLE `cita_servicio`
  ADD PRIMARY KEY (`id_cita`,`id_servicio`),
  ADD KEY `fk_cs_servicio` (`id_servicio`);

--
-- Indices de la tabla `horario_barbero`
--
ALTER TABLE `horario_barbero`
  ADD PRIMARY KEY (`id_horario`),
  ADD KEY `fk_horario_barbero` (`id_barbero`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`id_notificacion`),
  ADD KEY `fk_notificacion_usuario` (`id_usuario`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`id_pago`),
  ADD KEY `fk_pago_cita` (`id_cita`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`);

--
-- Indices de la tabla `resena`
--
ALTER TABLE `resena`
  ADD PRIMARY KEY (`id_resena`),
  ADD UNIQUE KEY `uq_resena_cita` (`id_cita`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `uq_rol_nombre` (`nombre_rol`);

--
-- Indices de la tabla `servicio`
--
ALTER TABLE `servicio`
  ADD PRIMARY KEY (`id_servicio`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `uq_usuario_correo` (`correo`),
  ADD KEY `fk_usuario_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cita`
--
ALTER TABLE `cita`
  MODIFY `id_cita` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `horario_barbero`
--
ALTER TABLE `horario_barbero`
  MODIFY `id_horario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  MODIFY `id_notificacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id_pago` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `resena`
--
ALTER TABLE `resena`
  MODIFY `id_resena` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `servicio`
--
ALTER TABLE `servicio`
  MODIFY `id_servicio` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `cita`
--
ALTER TABLE `cita`
  ADD CONSTRAINT `fk_cita_barbero` FOREIGN KEY (`id_barbero`) REFERENCES `usuario` (`id_usuario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cita_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `usuario` (`id_usuario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cita_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `cita_servicio`
--
ALTER TABLE `cita_servicio`
  ADD CONSTRAINT `fk_cs_cita` FOREIGN KEY (`id_cita`) REFERENCES `cita` (`id_cita`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cs_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `horario_barbero`
--
ALTER TABLE `horario_barbero`
  ADD CONSTRAINT `fk_horario_barbero` FOREIGN KEY (`id_barbero`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `fk_notificacion_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `fk_pago_cita` FOREIGN KEY (`id_cita`) REFERENCES `cita` (`id_cita`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `resena`
--
ALTER TABLE `resena`
  ADD CONSTRAINT `fk_resena_cita` FOREIGN KEY (`id_cita`) REFERENCES `cita` (`id_cita`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
