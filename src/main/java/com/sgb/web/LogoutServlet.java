package com.sgb.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet de cierre de sesion. No es un requisito textual de la guia de
 * la evidencia, pero es el complemento logico del login (tal como se
 * mostro en la clase de apoyo) y ayuda a probar que la sesion realmente
 * se esta creando y destruyendo bien.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // getSession(false): NO crea una sesion nueva si no existe una;
        // solo nos interesa invalidar la que ya estaba abierta.
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect("index.jsp");
    }
}
