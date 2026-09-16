package com.sgb.web;

import com.sgb.conexion.ConexionBD;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet del modulo de CITAS (Evidencia GA7-220501096-AA2-EV02).
 *
 * A diferencia de UsuarioDAO (Evidencia EV01, que separa el acceso a
 * datos en una clase DAO aparte), aqui se usa el enfoque BASICO pedido
 * para esta evidencia: la conexion, las consultas SQL y la logica del
 * CRUD estan directamente en el servlet, igual que en el ejemplo hecho
 * en la clase de apoyo (formulario -> Servlet -> JDBC -> respuesta).
 *
 * Operaciones cubiertas (las 4 que exige la guia): insertar, consultar,
 * actualizar y eliminar citas.
 *
 * En vez de crear una clase Cita.java aparte (lo cual ya empezaria a
 * ser el patron DAO/MVC completo que esta evidencia no pide), cada fila
 * de la tabla CITA se representa como un Map<String, Object>: es la
 * forma mas simple de pasarle datos a citas.jsp sin declarar una clase
 * nueva.
 */
@WebServlet("/citas")
public class CitaServlet extends HttpServlet {

    /**
     * Metodo de apoyo: valida que haya una sesion activa (usuario que ya
     * inicio sesion). Se usa al principio de doGet y de doPost para que
     * nadie pueda ver o modificar citas sin haber iniciado sesion antes
     * (esto es justamente el problema que se resolvio en la clase de
     * apoyo con "request.getSession()" y el "if (session == null)").
     *
     * @return true si hay sesion valida; si no la hay, ya redirige a
     *         index.jsp y devuelve false (el que llama debe hacer
     *         "return" inmediatamente si esto da false).
     */
    private boolean requiereSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("index.jsp?error=sesion");
            return false;
        }
        return true;
    }

    /**
     * GET /citas: solo consulta y muestra. Aqui NO se modifica nada en
     * la base de datos (por eso es GET y no POST): se listan las citas
     * existentes y se cargan los combos (clientes, barberos, servicios)
     * que necesita el formulario de creacion/edicion en citas.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requiereSesion(request, response)) {
            return;
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            request.setAttribute("citas", listarCitas(con));
            request.setAttribute("clientes", listarUsuariosPorRol(con, "Cliente"));
            request.setAttribute("barberos", listarUsuariosPorRol(con, "Barbero"));
            request.setAttribute("servicios", listarServicios(con));
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Error al consultar la base de datos: " + e.getMessage());
        }

        // forward (no redirect): reenvia la misma peticion a citas.jsp
        // conservando los "request.setAttribute" de arriba, para que el
        // JSP los pueda leer con request.getAttribute(...).
        RequestDispatcher despachador = request.getRequestDispatcher("citas.jsp");
        despachador.forward(request, response);
    }

    /**
     * POST /citas: aqui SI se modifica la base de datos (crear, editar
     * o eliminar), por eso se usa POST. El formulario de citas.jsp
     * manda un campo oculto llamado "accion" para que este metodo sepa
     * cual de las tres operaciones ejecutar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requiereSesion(request, response)) {
            return;
        }

        String accion = request.getParameter("accion");

        try (Connection con = ConexionBD.obtenerConexion()) {
            switch (accion == null ? "" : accion) {
                case "crear" -> crearCita(con, request);
                case "actualizar" -> actualizarCita(con, request);
                case "eliminar" -> eliminarCita(con, request);
                default -> { /* accion desconocida: no se hace nada */ }
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Error al guardar la cita: " + e.getMessage());
        }

        // Patron Post/Redirect/Get: despues de guardar, redirigimos con
        // GET a /citas en vez de reenviar (forward) la misma respuesta.
        // Esto evita exactamente el error que salio en la clase de
        // apoyo ("no se pudo llamar a sendRedirect, la respuesta ya fue
        // comiteada"): nunca escribimos salida y redirigimos a la vez.
        response.sendRedirect("citas");
    }

    // ------------------------------------------------------------------
    // Operaciones sobre la base de datos (CRUD de la tabla CITA)
    // ------------------------------------------------------------------

    /** INSERTAR: crea una cita nueva a partir de los datos del formulario. */
    private void crearCita(Connection con, HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO cita (id_cliente, id_barbero, id_servicio, fecha_hora, estado, notas) "
                + "VALUES (?, ?, ?, ?, 'pendiente', ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(request.getParameter("idCliente")));
            ps.setInt(2, Integer.parseInt(request.getParameter("idBarbero")));
            ps.setInt(3, Integer.parseInt(request.getParameter("idServicio")));
            ps.setTimestamp(4, convertirFechaHora(request.getParameter("fechaHora")));
            ps.setString(5, request.getParameter("notas"));
            ps.executeUpdate();
        }
    }

    /** CONSULTAR: trae todas las citas con los nombres de cliente, barbero y servicio (JOIN). */
    private List<Map<String, Object>> listarCitas(Connection con) throws SQLException {
        String sql = "SELECT c.id_cita, c.fecha_hora, c.estado, c.notas, "
                + "c.id_cliente, c.id_barbero, c.id_servicio, "
                + "CONCAT(cli.nombre, ' ', cli.apellido) AS cliente_nombre, "
                + "CONCAT(bar.nombre, ' ', bar.apellido) AS barbero_nombre, "
                + "s.nombre AS servicio_nombre "
                + "FROM cita c "
                + "INNER JOIN usuario cli ON c.id_cliente = cli.id_usuario "
                + "INNER JOIN usuario bar ON c.id_barbero = bar.id_usuario "
                + "INNER JOIN servicio s ON c.id_servicio = s.id_servicio "
                + "ORDER BY c.fecha_hora DESC";

        List<Map<String, Object>> citas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // LinkedHashMap para conservar el orden de las columnas
                // al recorrerlo despues en citas.jsp.
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("idCita", rs.getInt("id_cita"));
                fila.put("fechaHora", rs.getTimestamp("fecha_hora"));
                fila.put("estado", rs.getString("estado"));
                fila.put("notas", rs.getString("notas"));
                fila.put("idCliente", rs.getInt("id_cliente"));
                fila.put("idBarbero", rs.getInt("id_barbero"));
                fila.put("idServicio", rs.getInt("id_servicio"));
                fila.put("clienteNombre", rs.getString("cliente_nombre"));
                fila.put("barberoNombre", rs.getString("barbero_nombre"));
                fila.put("servicioNombre", rs.getString("servicio_nombre"));
                citas.add(fila);
            }
        }
        return citas;
    }

    /** ACTUALIZAR: modifica una cita existente (cliente, barbero, servicio, fecha, estado y notas). */
    private void actualizarCita(Connection con, HttpServletRequest request) throws SQLException {
        String sql = "UPDATE cita SET id_cliente = ?, id_barbero = ?, id_servicio = ?, "
                + "fecha_hora = ?, estado = ?, notas = ? WHERE id_cita = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(request.getParameter("idCliente")));
            ps.setInt(2, Integer.parseInt(request.getParameter("idBarbero")));
            ps.setInt(3, Integer.parseInt(request.getParameter("idServicio")));
            ps.setTimestamp(4, convertirFechaHora(request.getParameter("fechaHora")));
            ps.setString(5, request.getParameter("estado"));
            ps.setString(6, request.getParameter("notas"));
            ps.setInt(7, Integer.parseInt(request.getParameter("idCita")));
            ps.executeUpdate();
        }
    }

    /** ELIMINAR: borra una cita por su id. */
    private void eliminarCita(Connection con, HttpServletRequest request) throws SQLException {
        String sql = "DELETE FROM cita WHERE id_cita = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(request.getParameter("idCita")));
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------------
    // Consultas auxiliares para llenar los <select> del formulario
    // ------------------------------------------------------------------

    /** Trae los usuarios activos que tengan el rol indicado ("Cliente" o "Barbero"). */
    private List<Map<String, Object>> listarUsuariosPorRol(Connection con, String nombreRol) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido "
                + "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol "
                + "WHERE r.nombre_rol = ? AND u.activo = 1 "
                + "ORDER BY u.nombre";
        List<Map<String, Object>> usuarios = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("id", rs.getInt("id_usuario"));
                    fila.put("nombreCompleto", rs.getString("nombre") + " " + rs.getString("apellido"));
                    usuarios.add(fila);
                }
            }
        }
        return usuarios;
    }

    /** Trae los servicios activos disponibles. */
    private List<Map<String, Object>> listarServicios(Connection con) throws SQLException {
        String sql = "SELECT id_servicio, nombre FROM servicio WHERE activo = 1 ORDER BY nombre";
        List<Map<String, Object>> servicios = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("id", rs.getInt("id_servicio"));
                fila.put("nombre", rs.getString("nombre"));
                servicios.add(fila);
            }
        }
        return servicios;
    }

    /**
     * Convierte el texto que manda un &lt;input type="datetime-local"&gt;
     * (formato "yyyy-MM-ddTHH:mm") a un Timestamp de SQL.
     */
    private Timestamp convertirFechaHora(String textoFechaHora) {
        LocalDateTime fechaHora = LocalDateTime.parse(textoFechaHora);
        return Timestamp.valueOf(fechaHora);
    }
}
