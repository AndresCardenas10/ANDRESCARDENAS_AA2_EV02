package com.sgb.dao;

import com.sgb.conexion.ConexionBD;
import com.sgb.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object del módulo de login/usuarios. Cumple las cuatro
 * operaciones exigidas por la evidencia: inserción, consulta,
 * actualización y eliminación; además de un método de autenticación
 * (login) que reutiliza la misma lógica de mapeo de resultados.
 *
 * Alineado con el esquema real de la tabla USUARIO (sgb_estructura.sql):
 * id_usuario, id_rol, nombre, apellido, correo, contrasena_hash, telefono,
 * token_recuperacion, expira_token, activo, creado_en.
 */
public class UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.nombre, u.apellido, u.correo, u.contrasena_hash, "
            + "u.telefono, u.id_rol, r.nombre_rol, u.activo, u.creado_en "
            + "FROM USUARIO u INNER JOIN ROL r ON u.id_rol = r.id_rol";

    /** INSERTAR: registra un nuevo usuario. */
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO USUARIO (nombre, apellido, correo, contrasena_hash, telefono, id_rol, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasenaHash());
            ps.setString(5, usuario.getTelefono());
            ps.setInt(6, usuario.getIdRol());
            ps.setBoolean(7, usuario.isActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /** CONSULTAR: retorna todos los usuarios registrados. */
    public List<Usuario> consultarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_BASE)) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /** CONSULTAR: busca un usuario puntual por su correo (único). */
    public Usuario consultarPorCorreo(String correo) {
        String sql = SELECT_BASE + " WHERE u.correo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuario por correo: " + e.getMessage());
        }
        return null;
    }

    /** ACTUALIZAR: modifica nombre, apellido, teléfono, rol y estado de un usuario existente. */
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE USUARIO SET nombre = ?, apellido = ?, telefono = ?, id_rol = ?, activo = ? "
                + "WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getTelefono());
            ps.setInt(4, usuario.getIdRol());
            ps.setBoolean(5, usuario.isActivo());
            ps.setInt(6, usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /** ELIMINAR: borra un usuario por su id. */
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM USUARIO WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * LOGIN: valida correo + contraseña contra la base de datos y exige que
     * el usuario esté activo. Retorna el usuario autenticado o null si las
     * credenciales no coinciden.
     *
     * Nota: la contraseña se compara en texto plano por simplicidad, tal
     * como se manejó en el ejemplo de clase (la columna se llama
     * contrasena_hash pero por ahora guarda texto plano). Para producción
     * (RNF06 del proyecto) se recomienda almacenar la contraseña con hash
     * real (p. ej. BCrypt) en vez de texto plano.
     */
    public Usuario autenticar(String correo, String contrasena) {
        String sql = SELECT_BASE + " WHERE u.correo = ? AND u.contrasena_hash = ? AND u.activo = TRUE";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    /** Convierte la fila actual de un ResultSet en un objeto Usuario. */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setContrasenaHash(rs.getString("contrasena_hash"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setNombreRol(rs.getString("nombre_rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        Timestamp marcaTiempo = rs.getTimestamp("creado_en");
        if (marcaTiempo != null) {
            usuario.setCreadoEn(marcaTiempo.toLocalDateTime());
        }
        return usuario;
    }
}
