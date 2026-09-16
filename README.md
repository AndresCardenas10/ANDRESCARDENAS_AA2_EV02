# Módulo Login / Usuarios — SGB (Sistema Gestión Barbería)
Evidencia **GA7-220501096-AA2-EV01** — Codificación de módulos del software

## Qué hace este módulo

Implementa la capa Java + JDBC del módulo de **login y gestión de usuarios**
del proyecto SGB, cumpliendo las cuatro operaciones exigidas por la guía:

| Operación   | Método (`UsuarioDAO`)          | Uso desde el menú (`App`) |
|-------------|---------------------------------|----------------------------|
| Insertar    | `insertarUsuario(Usuario)`      | Opción 1 — Registrar usuario |
| Consultar   | `consultarUsuarios()` / `consultarPorCorreo(String)` | Opción 2 — Consultar usuarios |
| Actualizar  | `actualizarUsuario(Usuario)`    | Opción 3 — Actualizar usuario |
| Eliminar    | `eliminarUsuario(int)`          | Opción 4 — Eliminar usuario |
| Login       | `autenticar(String correo, String clave)` | Opción 5 — Iniciar sesión |

## Esquema de datos usado

Este módulo está alineado con el script **`sql/sgb_estructura.sql`**, que
crea la base de datos **`sgb`** (10 tablas: ROL, USUARIO, SERVICIO,
HORARIO_BARBERO, CITA, CITA_SERVICIO, PAGO, NOTIFICACION, RESENA, PRODUCTO).

La tabla `USUARIO` real tiene estas columnas relevantes para este módulo:
`id_usuario, id_rol, nombre, apellido, correo, contrasena_hash, telefono,
token_recuperacion, expira_token, activo, creado_en`.

Las clases `Usuario.java`, `UsuarioDAO.java` y `App.java` ya están
ajustadas a este esquema (incluyen `apellido` y usan `contrasena_hash`
en vez de una columna `clave` simplificada).

## Estructura del proyecto

```
ANDRESCARDENAS_AA2_EV01/
├── pom.xml
├── README.md                          (este archivo)
├── ENLACE_REPOSITORIO.txt             (completar antes de entregar)
├── sql/
│   ├── sgb_estructura.sql             (crea la BD 'sgb' con las 10 tablas)
│   └── sgb_usuarios_prueba.sql        (inserta 6 usuarios de prueba)
└── src/main/java/com/sgb/
    ├── App.java                       (menú de consola / clase principal)
    ├── conexion/ConexionBD.java       (conexión JDBC)
    ├── modelo/Usuario.java            (entidad)
    ├── modelo/Rol.java                (entidad)
    └── dao/UsuarioDAO.java            (CRUD + login)
```

**Convenciones de codificación aplicadas** (según lo exigido por la guía):
- Clases: `PascalCase` (`Usuario`, `UsuarioDAO`, `ConexionBD`).
- Métodos y variables: `camelCase` (`insertarUsuario`, `idUsuario`).
- Paquetes: minúsculas, notación de puntos (`com.sgb.dao`).

## ⚠️ Configuración especial de XAMPP en esta máquina (puerto 3307)

Esta instalación de XAMPP **no usa el puerto por defecto de MySQL (3306)**,
sino el **3307**, con usuario `root` y contraseña vacía. Esto ya viene
ajustado en dos archivos de configuración: `my.cnf` (del propio MySQL) y
`config.inc.php` (de phpMyAdmin). Si vas a correr este proyecto en la
misma máquina donde ya se hizo este ajuste, no necesitas tocar nada más
que lo que se explica abajo. Si lo vas a correr en **otra máquina/PC**,
sigue esta sección paso a paso.

### 1. Verifica o ajusta el puerto de MySQL (`my.cnf`)

Archivo: `C:\xampp\mysql\bin\my.cnf`

Asegúrate de que en las secciones `[client]` y `[mysqld]` el puerto esté
en `3307`:

```ini
[client]
port=3307
socket="C:/xampp/mysql/mysql.sock"

[mysqld]
port=3307
socket="C:/xampp/mysql/mysql.sock"
```

> Si tu XAMPP trae el puerto en 3306 por defecto y no tienes conflictos
> con otro MySQL instalado en esa máquina, puedes dejarlo en 3306 — en
> ese caso, usa 3306 también en `ConexionBD.java` (paso 3).

### 2. Verifica o ajusta la configuración de phpMyAdmin (`config.inc.php`)

Archivo: `C:\xampp\phpMyAdmin\config.inc.php`

Debe apuntar al mismo puerto configurado en el paso 1, con `root` y sin
contraseña:

```php
$cfg['Servers'][$i]['auth_type'] = 'config';
$cfg['Servers'][$i]['user'] = 'root';
$cfg['Servers'][$i]['password'] = '';
$cfg['Servers'][$i]['AllowNoPassword'] = true;
$cfg['Servers'][$i]['host'] = '127.0.0.1';
$cfg['Servers'][$i]['port'] = '3307';
$cfg['Servers'][$i]['connect_type'] = 'tcp';
```

### 3. Ajusta `ConexionBD.java` para que use el mismo puerto

Archivo: `src/main/java/com/sgb/conexion/ConexionBD.java`

```java
private static final String URL =
        "jdbc:mysql://localhost:3307/sgb?useTimezone=true&serverTimezone=UTC";
private static final String USUARIO = "root";
private static final String CLAVE = "";
```

El puerto debe coincidir exactamente con el de `my.cnf`, y el nombre de
la base debe ser **`sgb`** (no `sgb_bd`), que es la que crea
`sgb_estructura.sql` con las 10 tablas del proyecto completo.

### 4. Si MySQL no arranca o se cae (`shutdown unexpectedly`)

Esto suele indicar una base de datos de sistema corrupta (por un apagado
incorrecto de MySQL). Si el log de error (botón **Logs** junto a MySQL en
el Panel de Control de XAMPP) menciona algo como
`Incorrect file format 'proxies_priv'` o errores similares en la carpeta
`mysql` interna:

1. Detén MySQL completamente en XAMPP (y cierra cualquier `mysqld.exe`
   colgado desde el Administrador de tareas si no responde).
2. Haz una copia de respaldo de `C:\xampp\mysql\data` completa, por
   seguridad.
3. Dentro de `C:\xampp\mysql\data`, renombra la carpeta `mysql` a
   `mysql_old` (no la borres).
4. Copia la carpeta `mysql` desde `C:\xampp\mysql\backup\` y pégala en
   `C:\xampp\mysql\data\`.
5. **No toques** la carpeta `sgb` dentro de `data` — ahí están tus
   tablas y datos reales, y no se ven afectados por este proceso.
6. Vuelve a iniciar MySQL desde XAMPP.

## Cómo ejecutarlo

### 1. Preparar la base de datos
1. Abre **XAMPP Control Panel** e inicia **Apache** y **MySQL** (revisa
   la sección anterior si MySQL no arranca o si es la primera vez que
   configuras esta máquina).
2. Entra a phpMyAdmin (`Admin` junto a MySQL) → pestaña **SQL**.
3. Pega y ejecuta el contenido de `sql/sgb_estructura.sql`.
   - Esto crea la base `sgb` y las 10 tablas del proyecto, incluyendo
     los tres roles base (Administrador, Barbero, Cliente).
4. Pega y ejecuta el contenido de `sql/sgb_usuarios_prueba.sql`.
   - Esto inserta 6 usuarios de prueba: 1 administrador, 2 barberos y
     3 clientes (ver tabla de credenciales más abajo).

### 2. Abrir el proyecto en NetBeans
1. `File > Open Project` y selecciona esta carpeta (tiene `pom.xml`,
   NetBeans la reconoce como proyecto Maven).
2. Si el conector JDBC no descarga solo, clic derecho sobre el proyecto →
   **Clean and Build** (equivalente a lo que se hizo en clase).
3. Ejecuta `App.java` con el botón ▶ (Run).

### 3. Probar el módulo
En consola aparecerá un menú (1-5 y 0 para salir). Prueba en este orden:
1. Opción 5 con `admin@demo.com` / `123456` → debe iniciar sesión.
2. Opción 1 para registrar un usuario nuevo.
3. Opción 2 para verificar que aparece en el listado.
4. Opción 3 para actualizarlo y opción 4 para eliminarlo.

### Credenciales de prueba (creadas por `sgb_usuarios_prueba.sql`)

| Correo                         | Clave  | Rol            |
|---------------------------------|--------|----------------|
| admin@demo.com                 | 123456 | Administrador  |
| carlos.barbero@demo.com        | 123456 | Barbero        |
| julian.barbero@demo.com        | 123456 | Barbero        |
| laura.cliente@demo.com         | 123456 | Cliente        |
| santiago.cliente@demo.com      | 123456 | Cliente        |
| valentina.cliente@demo.com     | 123456 | Cliente        |

## Decisiones de diseño (y por qué)

- **`PreparedStatement` en vez de `Statement` + concatenación de texto**:
  el ejemplo de clase usa `Statement` con el SQL armado como texto plano
  (`"SELECT * FROM usuario WHERE correo = '" + correo + "'"`), lo cual es
  vulnerable a inyección SQL. Aquí se usa `PreparedStatement` con `?`,
  que cumple el mismo objetivo pedagógico (conexión JDBC + CRUD) de forma
  más segura, sin salirse de lo que pide la guía.
- **Clave en texto plano**: se mantiene igual que en el ejemplo de clase
  para no salirse del alcance de esta evidencia. La guía no exige cifrado
  aquí (eso corresponde a RNF06 del proyecto, para una fase posterior);
  queda comentado en `UsuarioDAO.autenticar()` como mejora futura.
- **DAO separado del modelo y de la conexión**: separa responsabilidades
  (`ConexionBD` solo conecta, `Usuario`/`Rol` solo representan datos,
  `UsuarioDAO` solo hace CRUD), en línea con los diagramas de clases ya
  desarrollados en fases anteriores del proyecto.

## Pendiente antes de entregar

- [ ] Inicializar Git, hacer commit y push a GitHub.
- [ ] Completar `ENLACE_REPOSITORIO.txt` con la URL real.
- [ ] Comprimir toda esta carpeta como `ANDRESCARDENAS_AA2_EV01.zip`.
