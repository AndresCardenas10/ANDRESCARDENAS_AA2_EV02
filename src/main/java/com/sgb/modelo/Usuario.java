package com.sgb.modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla USUARIO del proyecto SGB.
 * Alineada con el esquema real (sgb_estructura.sql): id_usuario, id_rol,
 * nombre, apellido, correo, contrasena_hash, telefono, token_recuperacion,
 * expira_token, activo, creado_en.
 */
public class Usuario {

    private int idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasenaHash;
    private String telefono;
    private int idRol;
    private String nombreRol;   // solo lectura: se llena al consultar (JOIN con ROL)
    private boolean activo;
    private LocalDateTime creadoEn;

    public Usuario() {
    }

    /** Constructor para registrar un usuario nuevo (sin id ni fecha, los asigna la BD). */
    public Usuario(String nombre, String apellido, String correo, String contrasenaHash,
                   String telefono, int idRol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.idRol = idRol;
        this.activo = true;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario
                + ", nombre='" + nombre + '\''
                + ", apellido='" + apellido + '\''
                + ", correo='" + correo + '\''
                + ", telefono='" + telefono + '\''
                + ", rol='" + nombreRol + '\''
                + ", activo=" + activo
                + '}';
    }
}
