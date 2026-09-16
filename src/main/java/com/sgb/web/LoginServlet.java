package com.sgb.web;

import com.sgb.dao.UsuarioDAO;
import com.sgb.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet de inicio de sesion del modulo web de SGB (Evidencia EV02).
 *
 * Reutiliza el UsuarioDAO ya construido en la Evidencia EV01: este
 * servlet no vuelve a escribir la logica de acceso a datos, solo la
 * conecta con el formulario HTML (index.jsp) y con la sesion HTTP.
 *
 * @WebServlet reemplaza la declaracion en web.xml: al anotar la clase
 * con la URL "/login", Tomcat sabe que las peticiones a esa ruta deben
 * ser atendidas por esta clase (esto es lo que en clase se configuraba
 * manualmente al crear un "New Servlet" en NetBeans).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Atiende el POST que envia el formulario de index.jsp. Se usa POST
     * (no GET) porque el formulario transporta una contrasena: con GET
     * quedaria visible en la URL y en el historial del navegador.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // request.getParameter(...) obtiene los valores que el usuario
        // escribio en los <input name="correo"> y <input name="contrasena">
        // del formulario.
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // Reutilizamos el metodo autenticar() de UsuarioDAO (ya probado
        // en la Evidencia EV01) en vez de repetir la consulta SQL aqui.
        Usuario usuarioAutenticado = usuarioDAO.autenticar(correo, contrasena);

        if (usuarioAutenticado != null) {
            // Credenciales correctas: creamos una sesion HTTP y guardamos
            // el usuario en ella. request.getSession() sin argumentos
            // crea la sesion si todavia no existe.
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuarioAutenticado);

            // Redirigimos (no reenviamos) al modulo de citas. Se usa
            // sendRedirect y no un forward para que el navegador haga una
            // nueva peticion GET a "/citas": asi, si el usuario recarga la
            // pagina despues, no se reenvia el formulario de login.
            response.sendRedirect("citas");
        } else {
            // Credenciales incorrectas: volvemos al login con
            // ?error=1 para que index.jsp muestre el mensaje de error.
            response.sendRedirect("index.jsp?error=1");
        }
    }

    /**
     * Si alguien intenta entrar directamente a /login por GET (por
     * ejemplo, escribiendo la URL a mano), lo mandamos de vuelta al
     * formulario en vez de mostrar un error del servidor.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}
