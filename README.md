# Veterinaria San Marcos · Backend

Proyecto semestral DSY1104 · Caso: Veterinaria San Marcos
Integrantes: Diego Bergeret, Andrés Marín, Thomas Araya
Frontend: https://github.com/diegobs7/veterinaria-san-marcos-react
App Android que comparte este backend: https://github.com/diegobs7/PocketTrackKaiju

## Decisiones técnicas
Spring Boot (Java 21) · MySQL 8.4 en Docker · Spring Security + JWT (HS256) · Swagger (springdoc) · ejecución en EC2

- Contraseñas guardadas con hash BCrypt, nunca en texto plano. Mínimo 8 caracteres, igual que en la página.
- El rol se asigna en el servidor al registrar (rol inicial de la aplicación); el cliente no puede elegirlo.
- Login con error genérico ("Credenciales incorrectas") para no revelar si el correo existe.
- Desactivar es un borrado lógico (`activo=false`) y se puede revertir con Reactivar; el filtro JWT consulta la base en cada solicitud, por lo que el token de un usuario desactivado deja de funcionar.
- Eliminar borra la cuenta de forma permanente. En la página solo se ofrece para cuentas ya desactivadas (dos pasos para no borrar por error).
- Un usuario solo puede ver o modificar su propia cuenta; el administrador puede gestionar todas las de su aplicación.
- CORS: solo los orígenes indicados en `CORS_ORIGENES` pueden llamar a la API desde el navegador.

## Un backend para dos aplicaciones
El mismo backend guarda los usuarios de la página de la veterinaria y de la app PocketTrack Kaiju. Cada usuario pertenece a una `aplicacion`, que se envía en el login y en el registro.

| Aplicación | Rol inicial al registrarse | Roles permitidos |
|---|---|---|
| `VETERINARIA` | `DUENO` | `ADMINISTRADOR`, `RECEPCIONISTA`, `DUENO` |
| `KAIJU` | `VENDEDOR` | `ADMINISTRADOR`, `VENDEDOR`, `ENCARGADO_INVENTARIO` |

- El correo y el RUT son únicos **por aplicación**: la misma persona puede tener una cuenta en cada app.
- Un administrador solo ve y gestiona los usuarios de su propia aplicación.
- Al arrancar se crea un administrador en cada aplicación con `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

## Estado del avance (bloque usuarios)
| Operación | Estado | Diseñado | Implementado | Probado |
|---|---|---|---|---|
| Tabla de usuarios (MySQL, hash BCrypt, persistente) | Listo | Sí | Sí | Sí |
| Inicio de sesión (JWT) | Listo | Sí | Sí | Sí |
| Registro (con RUT, teléfono y dirección) | Listo | Sí | Sí | Sí |
| Crear usuario con rol (administrador) | Listo | Sí | Sí | Sí |
| Modificar usuario | Listo | Sí | Sí | Sí |
| Listar usuarios | Listo | Sí | Sí | Sí |
| Cambiar rol | Listo | Sí | Sí | Sí |
| Desactivar usuario | Listo | Sí | Sí | Sí |
| Reactivar usuario | Listo | Sí | Sí | Sí |
| Eliminar usuario (permanente) | Listo | Sí | Sí | Sí |
| Usuarios separados por aplicación (VETERINARIA / KAIJU) | Listo | Sí | Sí | Sí |
| CORS para el frontend React | Listo | Sí | Sí | Sí |
| Pruebas unitarias automáticas | Pendiente | No | No | No |

## Rutas

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/v1/auth/register` | Público |
| POST | `/api/v1/auth/login` | Público |
| GET | `/api/v1/auth/me` | Autenticado |
| GET | `/api/v1/usuarios` | Solo ADMINISTRADOR (usuarios de su aplicación) |
| POST | `/api/v1/usuarios` | Solo ADMINISTRADOR (crea con rol) |
| GET | `/api/v1/usuarios/{id}` | Propietario o ADMINISTRADOR |
| PUT | `/api/v1/usuarios/{id}` | Propietario o ADMINISTRADOR |
| PATCH | `/api/v1/usuarios/{id}/rol` | Solo ADMINISTRADOR |
| DELETE | `/api/v1/usuarios/{id}` | Solo ADMINISTRADOR (desactiva) |
| PATCH | `/api/v1/usuarios/{id}/activar` | Solo ADMINISTRADOR (reactiva) |
| DELETE | `/api/v1/usuarios/{id}/eliminar` | Solo ADMINISTRADOR (elimina) |

Documentación interactiva (Swagger UI): `http://<IP-EC2>:8080/swagger-ui.html`. Usar **Authorize** y pegar solo el token (`accessToken` de la respuesta del login).

## Variables del `.env`
El archivo `.env` no se sube a GitHub. Se crea a partir de `.env.example`.

| Variable | Para qué sirve |
|---|---|
| `DB_HOST`, `DB_PORT` | Dónde está MySQL (por defecto `localhost` y `3306`) |
| `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_ROOT_PASSWORD` | Base de datos MySQL del contenedor `vet-mysql` |
| `JWT_SECRET` | Clave para firmar los tokens. Generarla con `openssl rand -base64 48` |
| `JWT_EXPIRATION_MINUTES` | Duración del token (por defecto 60) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Administrador que se crea al arrancar en cada aplicación |
| `CORS_ORIGENES` | Dirección de la página de React, por ejemplo `http://<IP-EC2>:5173`. Varias se separan con coma |

## Cómo ejecutar
1. `cp .env.example .env` y completar los valores
2. `docker compose up -d`
3. `./mvnw spring-boot:run` (desde la carpeta del proyecto, porque ahí se lee el `.env`)

Notas:
- `DB_PASSWORD` y `DB_ROOT_PASSWORD` solo se aplican la primera vez que se crea el contenedor. Para cambiarlas en una base de prueba: `docker compose down -v` (borra los datos).
- `ADMIN_PASSWORD` solo se usa cuando se crea el administrador; cambiarla después no modifica al administrador ya creado.
- Si la base viene de una versión anterior (correo único sin aplicación), hay que borrar una vez el índice único antiguo de `email`, porque `ddl-auto=update` no lo elimina:
  ```bash
  docker exec -it vet-mysql mysql -u root -p veterinaria -e "SHOW INDEX FROM usuarios WHERE Column_name='email';"
  docker exec -it vet-mysql mysql -u root -p veterinaria -e "ALTER TABLE usuarios DROP INDEX NOMBRE_DEL_INDICE_ANTIGUO;"
  ```
  Se conserva `uk_usuarios_email_aplicacion`.

## Pruebas realizadas (16-nov-2026)

| # | Prueba | Esperado | Obtenido |
|---|--------|----------|----------|
| 1 | Admin lista usuarios | 200 | 200 |
| 2 | Dueño lista usuarios | 403 | 403 |
| 3 | Listar sin token | 401 | 401 |
| 4 | Dueño ve su propia cuenta | 200 | 200 |
| 5 | Dueño ve la cuenta del admin | 403 | 403 |
| 6 | Dueño modifica su nombre | 200 | 200 |
| 7 | Dueño modifica a otro usuario | 403 | 403 |
| 8 | Dueño se asigna rol administrador | 403 | 403 |
| 9 | Admin cambia rol de otro usuario | 200 | 200 |
| 10 | Admin asigna rol inexistente | 400 | 400 |
| 11 | Admin se desactiva a sí mismo | 409 | 409 |
| 12 | Admin desactiva a otro usuario | 204 | 204 |
| 13 | Token de usuario desactivado | 401 | 401 |
| 14 | Login de usuario desactivado | 401 | 401 |
| 15 | Login tras reiniciar el backend (persistencia) | 200 | 200 |

## Pruebas por aplicación (22-nov-2026)
Con una base nueva, en Swagger y desde la página de React, como administrador de cada aplicación.

| # | Prueba | Esperado | Obtenido |
|---|--------|----------|----------|
| 16 | Al arrancar se crea un admin en VETERINARIA y otro en KAIJU con el mismo correo | 2 filas | 2 filas |
| 17 | Admin VETERINARIA crea un RECEPCIONISTA | 201 | 201 |
| 18 | Admin VETERINARIA crea un VENDEDOR (rol de otra app) | 400 | 400 |
| 19 | Admin VETERINARIA repite un correo | 409 | 409 |
| 20 | Admin VETERINARIA lista: solo usuarios de VETERINARIA | 200 | 200 |
| 21 | Admin KAIJU crea un VENDEDOR con el mismo correo de la prueba 17 | 201 | 201 |
| 22 | Admin KAIJU crea un DUENO (rol de otra app) | 400 | 400 |
| 23 | Admin KAIJU lista: solo usuarios de KAIJU | 200 | 200 |
| 24 | Admin KAIJU ve un usuario de VETERINARIA | 404 | 404 |
| 25 | Preflight CORS desde `http://localhost:5173` | 200 con `Access-Control-Allow-Origin` | 200 |
| 26 | Login desde la página de React | Entra al panel | Entra al panel |
| 27 | Admin se cambia su propio rol (desde la página) | 409 | 409 |
| 28 | Admin desactiva un usuario y ese usuario intenta iniciar sesión | 401 | 401 |
| 29 | Admin reactiva y luego elimina un usuario desactivado | 200 y 204 | 200 y 204 |
| 30 | Registro con un RUT ya usado en VETERINARIA | 409 "El RUT ya está registrado" | 409 |
