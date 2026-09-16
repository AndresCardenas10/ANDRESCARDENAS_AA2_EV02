# Módulo Citas (Web) — SGB
Evidencia **GA7-220501096-AA2-EV02** — Módulos de software codificados y probados

Este documento describe SOLO lo que se agregó en esta evidencia sobre el
proyecto ya entregado en **GA7-220501096-AA2-EV01** (ver `README.md`
original para el módulo de login/usuarios en consola). Aquí, ese mismo
proyecto Java se convirtió en una **aplicación web** (Servlets + JSP)
según lo exige la guía de esta evidencia, tratada como un ejercicio
técnico independiente dentro de la actividad GA7-AA2, sin afectar la
decisión de stack (JavaScript/Node.js) documentada para el backend
definitivo del proyecto formativo.

## Qué hace este módulo

Un CRUD web completo de **citas**, protegido por inicio de sesión (que
reutiliza el login ya construido en la Evidencia EV01):

| Operación  | Cómo se hace |
|------------|--------------|
| Login      | `index.jsp` → POST a `LoginServlet` (`/login`), que reutiliza `UsuarioDAO.autenticar()` |
| Consultar  | `CitaServlet` (`doGet`, `/citas`) lista todas las citas con cliente, barbero y servicio |
| Insertar   | Formulario en `citas.jsp` → POST a `/citas` con `accion=crear` |
| Actualizar | Botón "Editar" en la tabla → mismo formulario → POST con `accion=actualizar` |
| Eliminar   | Botón "Eliminar" en cada fila → POST con `accion=eliminar` |
| Logout     | `LogoutServlet` (`/logout`), invalida la sesión |

## Qué se agregó (archivos nuevos)

```
src/main/webapp/
├── index.jsp                          (login, antes no existía interfaz web)
└── citas.jsp                          (listado + formulario de citas)
src/main/java/com/sgb/web/
├── LoginServlet.java                  (@WebServlet("/login"))
├── LogoutServlet.java                 (@WebServlet("/logout"))
└── CitaServlet.java                   (@WebServlet("/citas") — CRUD de citas)
sql/
├── sgb_servicios_prueba.sql           (siembra servicios de prueba; sgb.sql no traía ninguno)
└── obsoleto/sgb_login_usuarios.sql    (script viejo de EV01 que ya no se usa, se conserva como referencia)
```

`pom.xml` cambió de `packaging=jar` (aplicación de consola) a
`packaging=war` (aplicación web), agregando las dependencias de
Servlets y JSP (Jakarta EE 10 / Tomcat 10.1.x).

## Por qué se hizo así (decisiones de diseño)

- **Enfoque básico, sin `CitaDAO.java` aparte**: a diferencia de
  `UsuarioDAO` (Evidencia EV01), aquí el SQL y la lógica del CRUD están
  directamente en `CitaServlet.java`, tal como se explicó en la clase de
  apoyo. Es una decisión consciente para esta evidencia, no un olvido.
- **Sin clase `Cita.java`**: cada fila de la tabla `cita` se representa
  como un `Map<String, Object>` en vez de una clase de modelo, para no
  introducir más estructura de la que pide el enfoque básico.
- **Patrón Post/Redirect/Get** en `CitaServlet.doPost()`: después de
  crear/editar/eliminar, se redirige (no se reenvía) a `/citas`. Esto
  evita el error que salió en la clase de apoyo ("no se pudo llamar a
  sendRedirect, la respuesta ya fue comiteada").
- **`PreparedStatement` en todas las consultas**: igual que en
  `UsuarioDAO`, para no exponer el proyecto a inyección SQL.
- **`index.jsp` en vez de `index.html`**: se necesita leer el parámetro
  `?error=...` de la URL para mostrar mensajes distintos (credenciales
  incorrectas vs. sesión requerida), algo que un `.html` plano no puede
  hacer sin JavaScript adicional.

## Antes de probar: la tabla `servicio` está vacía

`sgb.sql` crea la tabla `servicio` pero no trae ningún dato. El
formulario de citas necesita al menos un servicio para funcionar, así
que **hay que ejecutar `sql/sgb_servicios_prueba.sql` después de
`sgb.sql`** (ver la guía de pruebas de la Etapa 4).

## Problema encontrado y solucionado: login fallaba con "No suitable driver found"

Durante las pruebas de la Etapa 4, el login fallaba de forma
intermitente con el error:

```
Error al autenticar usuario: No suitable driver found for
jdbc:mysql://localhost:3307/sgb?useTimezone=true&serverTimezone=UTC
```

**Causa:** no era un problema de dependencias faltantes (el `.jar` de
`mysql-connector-j` sí estaba presente en `WEB-INF/lib`, confirmado
incluso después de un `mvn clean install` exitoso). El problema real es
que `DriverManager` registra los drivers JDBC una sola vez por cada
proceso de la máquina virtual de Java (JVM). Al desplegar y volver a
desplegar la aplicación varias veces dentro de la **misma** instancia
de Tomcat (algo normal mientras se está probando desde NetBeans), el
driver quedaba "cargado" en un estado inconsistente y las conexiones
posteriores fallaban, aunque el `.jar` correcto siguiera estando en el
proyecto.

**Solución aplicada**, en `ConexionBD.obtenerConexion()`:

1. Se agregó un `Class.forName("com.mysql.cj.jdbc.Driver")` explícito
   antes de pedir la conexión, para forzar el registro del driver en
   cada llamada en vez de confiar en el registro automático:

   ```java
   public static Connection obtenerConexion() throws SQLException {
       try {
           Class.forName("com.mysql.cj.jdbc.Driver");
       } catch (ClassNotFoundException e) {
           throw new SQLException("No se encontro el driver de MySQL: " + e.getMessage());
       }
       return DriverManager.getConnection(URL, USUARIO, CLAVE);
   }
   ```

2. Se detuvo por completo el servidor Apache Tomcat (no solo un
   "redeploy" dentro de NetBeans) y se volvió a ejecutar el proyecto
   desde cero, para partir de una JVM limpia.

Con estos dos cambios (código + reinicio completo del servidor) el
login, y el CRUD de citas completo, quedaron funcionando de forma
estable.
