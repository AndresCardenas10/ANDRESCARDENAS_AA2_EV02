<%@ page import="java.util.List, java.util.Map" %>
<%--
    SGB - Modulo Citas (Web)
    citas.jsp: pagina protegida (requiere sesion) que muestra el listado
    de citas y el formulario para crear/editar. CitaServlet ya valido la
    sesion antes de reenviar (forward) aqui, pero igual comprobamos de
    nuevo por seguridad: si alguien llega a esta pagina sin pasar por el
    servlet (por ejemplo, escribiendo la URL directo), lo mandamos al
    login. Esto es exactamente el mismo problema que se vio en la clase
    de apoyo con "dashboard.jsp".
--%>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("index.jsp?error=sesion");
        return;
    }

    // Recuperamos las listas que dejo CitaServlet en el request con
    // setAttribute(...). request.getAttribute(...) devuelve un Object,
    // por eso se necesita el "cast" a List<Map<String, Object>>.
    List<Map<String, Object>> citas = (List<Map<String, Object>>) request.getAttribute("citas");
    List<Map<String, Object>> clientes = (List<Map<String, Object>>) request.getAttribute("clientes");
    List<Map<String, Object>> barberos = (List<Map<String, Object>>) request.getAttribute("barberos");
    List<Map<String, Object>> servicios = (List<Map<String, Object>>) request.getAttribute("servicios");
    String mensajeError = (String) request.getAttribute("mensajeError");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>SGB - Citas</title>
    <style>
        body { font-family: Arial, sans-serif; background:#f4f4f4; margin:0; padding:24px; }
        h1 { font-size:22px; }
        .topbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; }
        .topbar a { color:#2f6f4f; text-decoration:none; font-size:14px; }
        table { width:100%; border-collapse:collapse; background:#fff; margin-bottom:28px; }
        th, td { padding:8px 10px; border-bottom:1px solid #e0e0e0; text-align:left; font-size:14px; }
        th { background:#2f6f4f; color:#fff; }
        form.tarjeta { background:#fff; padding:20px; border-radius:8px; max-width:480px; }
        label { display:block; margin-top:10px; font-size:13px; }
        input, select, textarea { width:100%; padding:6px; margin-top:3px; box-sizing:border-box; }
        button { margin-top:16px; padding:8px 16px; background:#2f6f4f; color:#fff; border:none; border-radius:4px; cursor:pointer; }
        .btn-eliminar { background:#b3261e; color:#fff; border:none; padding:4px 8px; border-radius:4px; cursor:pointer; font-size:12px; }
        .btn-editar { background:#3f7fbf; color:#fff; border:none; padding:4px 8px; border-radius:4px; cursor:pointer; font-size:12px; }
        .error { background:#fdecea; color:#b3261e; padding:8px; border-radius:4px; font-size:13px; margin-bottom:16px; }
    </style>
</head>
<body>

<div class="topbar">
    <h1>SGB &mdash; Citas</h1>
    <a href="logout">Cerrar sesi&oacute;n</a>
</div>

<% if (mensajeError != null) { %>
    <div class="error"><%= mensajeError %></div>
<% } %>

<!-- ===================== TABLA DE CITAS ===================== -->
<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Barbero</th>
            <th>Servicio</th>
            <th>Fecha y hora</th>
            <th>Estado</th>
            <th>Notas</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <% if (citas == null || citas.isEmpty()) { %>
            <tr><td colspan="8">No hay citas registradas todav&iacute;a.</td></tr>
        <% } else {
            for (Map<String, Object> cita : citas) { %>
            <tr>
                <td><%= cita.get("idCita") %></td>
                <td><%= cita.get("clienteNombre") %></td>
                <td><%= cita.get("barberoNombre") %></td>
                <td><%= cita.get("servicioNombre") %></td>
                <td><%= cita.get("fechaHora") %></td>
                <td><%= cita.get("estado") %></td>
                <td><%= cita.get("notas") == null ? "" : cita.get("notas") %></td>
                <td>
                    <!-- Boton "Editar": no llama al servidor todavia, solo
                         copia los datos de esta fila al formulario de abajo
                         usando la funcion JavaScript cargarParaEditar(). -->
                    <button type="button" class="btn-editar"
                        onclick="cargarParaEditar(
                            '<%= cita.get("idCita") %>',
                            '<%= cita.get("idCliente") %>',
                            '<%= cita.get("idBarbero") %>',
                            '<%= cita.get("idServicio") %>',
                            '<%= cita.get("estado") %>',
                            '<%= cita.get("notas") == null ? "" : cita.get("notas") %>'
                        )">Editar</button>

                    <!-- Boton "Eliminar": este si es un formulario propio
                         que hace POST directo a /citas con accion=eliminar. -->
                    <form action="citas" method="post" style="display:inline"
                          onsubmit="return confirm('¿Eliminar esta cita?');">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="idCita" value="<%= cita.get("idCita") %>">
                        <button type="submit" class="btn-eliminar">Eliminar</button>
                    </form>
                </td>
            </tr>
        <% } } %>
    </tbody>
</table>

<!-- ============== FORMULARIO DE CREAR / EDITAR CITA ============== -->
<h2 id="tituloFormulario" style="font-size:16px;">Nueva cita</h2>
<form class="tarjeta" action="citas" method="post" id="formularioCita">
    <!-- Campo oculto "accion": empieza en "crear"; el boton Editar lo
         cambia a "actualizar" con JavaScript. Este es el mismo truco que
         se explico en clase para que un solo formulario sirva para las
         dos operaciones. -->
    <input type="hidden" name="accion" id="accion" value="crear">
    <!-- Solo se envia cuando accion=actualizar; en creacion la BD lo
         asigna solo con AUTO_INCREMENT. -->
    <input type="hidden" name="idCita" id="idCita" value="">

    <label for="idCliente">Cliente</label>
    <select name="idCliente" id="idCliente" required>
        <option value="">-- Selecciona --</option>
        <% if (clientes != null) { for (Map<String, Object> c : clientes) { %>
            <option value="<%= c.get("id") %>"><%= c.get("nombreCompleto") %></option>
        <% } } %>
    </select>

    <label for="idBarbero">Barbero</label>
    <select name="idBarbero" id="idBarbero" required>
        <option value="">-- Selecciona --</option>
        <% if (barberos != null) { for (Map<String, Object> b : barberos) { %>
            <option value="<%= b.get("id") %>"><%= b.get("nombreCompleto") %></option>
        <% } } %>
    </select>

    <label for="idServicio">Servicio</label>
    <select name="idServicio" id="idServicio" required>
        <option value="">-- Selecciona --</option>
        <% if (servicios != null) { for (Map<String, Object> s : servicios) { %>
            <option value="<%= s.get("id") %>"><%= s.get("nombre") %></option>
        <% } } %>
    </select>

    <label for="fechaHora">Fecha y hora</label>
    <input type="datetime-local" name="fechaHora" id="fechaHora" required>

    <!-- El estado solo tiene sentido al editar (una cita nueva siempre
         nace en "pendiente", eso ya lo pone el servidor); por eso este
         campo se deshabilita en modo creacion. -->
    <label for="estado">Estado</label>
    <select name="estado" id="estado" disabled>
        <option value="pendiente">pendiente</option>
        <option value="confirmada">confirmada</option>
        <option value="cancelada">cancelada</option>
        <option value="completada">completada</option>
    </select>

    <label for="notas">Notas</label>
    <textarea name="notas" id="notas" rows="2"></textarea>

    <button type="submit">Guardar cita</button>
    <button type="button" onclick="limpiarFormulario()" style="background:#888;">Cancelar edici&oacute;n</button>
</form>

<script>
    // Copia los datos de la fila elegida al formulario y lo pone en
    // modo "actualizar". Esto pasa solo en el navegador (no llama al
    // servidor); la peticion real al servidor ocurre cuando el usuario
    // le da clic a "Guardar cita".
    function cargarParaEditar(idCita, idCliente, idBarbero, idServicio, estado, notas) {
        document.getElementById('accion').value = 'actualizar';
        document.getElementById('idCita').value = idCita;
        document.getElementById('idCliente').value = idCliente;
        document.getElementById('idBarbero').value = idBarbero;
        document.getElementById('idServicio').value = idServicio;
        document.getElementById('estado').value = estado;
        document.getElementById('estado').disabled = false;
        document.getElementById('notas').value = notas;
        document.getElementById('tituloFormulario').innerText = 'Editar cita #' + idCita;
    }

    function limpiarFormulario() {
        document.getElementById('formularioCita').reset();
        document.getElementById('accion').value = 'crear';
        document.getElementById('idCita').value = '';
        document.getElementById('estado').disabled = true;
        document.getElementById('tituloFormulario').innerText = 'Nueva cita';
    }
</script>

</body>
</html>
