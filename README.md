# Veterinaria San Marcos · Backend

Proyecto semestral DSY1104 · Caso: Veterinaria San Marcos
Integrantes: Diego Bergeret, Andrés Marín, Thomas Araya
Frontend: https://github.com/diegobs7/veterinaria-san-marcos-react

## Decisiones técnicas
Spring Boot (Java 21) · MySQL 8.4 en Docker · Spring Security + JWT (HS256) · Swagger (springdoc) · ejecución en EC2

- Contraseñas guardadas con hash BCrypt, nunca en texto plano.
- El rol se asigna en el servidor al registrar (DUENO); el cliente no puede elegirlo.
- Login con error genérico ("Credenciales incorrectas") para no revelar si el correo existe.
- Eliminar usuario es un borrado lógico (`activo=false`); el filtro JWT consulta la base en cada solicitud, por lo que el token de un usuario desactivado deja de funcionar.
- Un usuario solo puede ver o modificar su propia cuenta; el administrador puede gestionar todas.

## Estado del avance (bloque usuarios)
| Operación | Estado | Diseñado | Implementado | Probado |
|---|---|---|---|---|
| Tabla de usuarios (MySQL, hash BCrypt, persistente) | Listo | Sí | Sí | Sí |
| Inicio de sesión (JWT) | Listo | Sí | Sí | Sí |
| Registro | Listo | Sí | Sí | Sí |
| Modificar usuario | Listo | Sí | Sí | Sí |
| Listar usuarios | Listo | Sí | Sí | Sí |
| Eliminar / desactivar usuario | Listo | Sí | Sí | Sí |
| CORS para el frontend React | Pendiente | No | No | No |
| Pruebas unitarias automáticas | Pendiente | No | No | No |

## Rutas

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/v1/auth/register` | Público |
| POST | `/api/v1/auth/login` | Público |
| GET | `/api/v1/auth/me` | Autenticado |
| GET | `/api/v1/usuarios` | Solo ADMINISTRADOR |
| GET | `/api/v1/usuarios/{id}` | Propietario o ADMINISTRADOR |
| PUT | `/api/v1/usuarios/{id}` | Propietario o ADMINISTRADOR |
| PATCH | `/api/v1/usuarios/{id}/rol` | Solo ADMINISTRADOR |
| DELETE | `/api/v1/usuarios/{id}` | Solo ADMINISTRADOR (desactiva) |

Documentación interactiva (Swagger UI): `http://<IP-EC2>:8080/swagger-ui.html`. Usar **Authorize** y pegar solo el token.

## Cómo ejecutar
1. `cp .env.example .env` y completar los valores
2. `docker compose up -d`
3. `./mvnw spring-boot:run`

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
