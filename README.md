# Veterinaria San Marcos · Backend

Proyecto semestral DSY1104 · Caso: Veterinaria San Marcos
Integrantes: Diego Bergeret, Andrés Marín, Thomas Araya
Frontend: https://github.com/diegobs7/veterinaria-san-marcos-react

## Decisiones técnicas
Spring Boot (Java 21) · MySQL 8.4 en Docker · Spring Security + JWT · Swagger (springdoc) · ejecución en EC2

## Estado del avance (bloque usuarios)
| Operación | Estado | Diseñado | Implementado | Probado |
|---|---|---|---|---|
| Tabla de usuarios | Pendiente | Sí | No | No |
| Inicio de sesión (JWT) | Pendiente | Sí | No | No |
| Registro | Pendiente | Sí | No | No |
| Modificar usuario | Pendiente | No | No | No |
| Listar usuarios | Pendiente | No | No | No |
| Eliminar / desactivar usuario | Pendiente | No | No | No |

## Cómo ejecutar
1. `cp .env.example .env` y completar los valores
2. `docker compose up -d`
3. `./mvnw spring-boot:run`