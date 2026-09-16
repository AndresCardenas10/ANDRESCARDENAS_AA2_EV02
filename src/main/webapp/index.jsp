<%--
    SGB - Modulo Citas (Web)
    index.jsp: formulario de inicio de sesion.

    Es JSP y no HTML plano porque necesitamos leer el parametro "error"
    que llega en la URL (?error=1 o ?error=sesion) para mostrar un
    mensaje distinto segun el caso, tal como se explico en la clase de
    apoyo: "no se puede leer un parametro de la URL desde un .html, hay
    que pasarlo a .jsp".
--%>
<%
    // Leemos el parametro "error" que puede venir en la URL.
    String error = request.getParameter("error");
    String mensajeError = null;
    if ("1".equals(error)) {
        mensajeError = "Correo o contraseña incorrectos.";
    } else if ("sesion".equals(error)) {
        mensajeError = "Debes iniciar sesión primero.";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>SGB - Iniciar sesi&oacute;n</title>
    <style>
        body { font-family: Arial, sans-serif; background:#f4f4f4; display:flex; justify-content:center; align-items:center; height:100vh; margin:0; }
        .caja { background:#fff; padding:30px; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.15); width:320px; }
        h1 { font-size:20px; margin-top:0; }
        label { display:block; margin-top:12px; font-size:14px; }
        input { width:100%; padding:8px; margin-top:4px; box-sizing:border-box; }
        button { margin-top:18px; width:100%; padding:10px; background:#2f6f4f; color:#fff; border:none; border-radius:4px; cursor:pointer; }
        .error { background:#fdecea; color:#b3261e; padding:8px; border-radius:4px; font-size:13px; margin-top:12px; }
    </style>
</head>
<body>
    <div class="caja">
        <h1>SGB &mdash; M&oacute;dulo de citas</h1>

        <% if (mensajeError != null) { %>
            <div class="error"><%= mensajeError %></div>
        <% } %>

        <!-- El formulario envia los datos por POST al LoginServlet
             (mapeado en /login). Nunca se debe usar GET para enviar una
             contrase&ntilde;a porque quedaria visible en la URL. -->
        <form action="login" method="post">
            <label for="correo">Correo</label>
            <input type="email" id="correo" name="correo" required>

            <label for="contrasena">Contrase&ntilde;a</label>
            <input type="password" id="contrasena" name="contrasena" required>

            <button type="submit">Ingresar</button>
        </form>
    </div>
</body>
</html>
