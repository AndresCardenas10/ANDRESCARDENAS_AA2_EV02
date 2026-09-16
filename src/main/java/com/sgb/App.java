package com.sgb;

import com.sgb.dao.UsuarioDAO;
import com.sgb.modelo.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Clase principal: menú de consola para probar el módulo de login/usuarios
 * del proyecto SGB (Sistema Gestión Barbería).
 * Evidencia GA7-220501096-AA2-EV01.
 */
public class App {

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> actualizarUsuario();
                case 4 -> eliminarUsuario();
                case 5 -> iniciarSesion();
                case 0 -> System.out.println("Saliendo del módulo de login/usuarios...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n=== Módulo de Login / Usuarios — SGB ===");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Consultar usuarios");
        System.out.println("3. Actualizar usuario");
        System.out.println("4. Eliminar usuario");
        System.out.println("5. Iniciar sesión (login)");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void registrarUsuario() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Correo: ");
        String correo = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("ID de rol (1=Administrador, 2=Barbero, 3=Cliente): ");
        int idRol = leerEntero();

        Usuario nuevo = new Usuario(nombre, apellido, correo, contrasena, telefono, idRol);
        boolean exito = usuarioDAO.insertarUsuario(nuevo);
        System.out.println(exito ? "Usuario registrado correctamente." : "No se pudo registrar el usuario.");
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.consultarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private static void actualizarUsuario() {
        System.out.print("ID del usuario a actualizar: ");
        int id = leerEntero();
        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Nuevo teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("Nuevo ID de rol: ");
        int idRol = leerEntero();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setIdRol(idRol);
        usuario.setActivo(true);

        boolean exito = usuarioDAO.actualizarUsuario(usuario);
        System.out.println(exito ? "Usuario actualizado correctamente." : "No se pudo actualizar el usuario.");
    }

    private static void eliminarUsuario() {
        System.out.print("ID del usuario a eliminar: ");
        int id = leerEntero();
        boolean exito = usuarioDAO.eliminarUsuario(id);
        System.out.println(exito ? "Usuario eliminado correctamente." : "No se pudo eliminar el usuario.");
    }

    private static void iniciarSesion() {
        System.out.print("Correo: ");
        String correo = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        Usuario usuario = usuarioDAO.autenticar(correo, contrasena);
        if (usuario != null) {
            System.out.println("Inicio de sesión exitoso. Bienvenido, "
                    + usuario.getNombre() + " (" + usuario.getNombreRol() + ")");
        } else {
            System.out.println("Correo o clave incorrectos, o el usuario está inactivo.");
        }
    }

    private static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido, se usará 0.");
            return 0;
        }
    }
}
