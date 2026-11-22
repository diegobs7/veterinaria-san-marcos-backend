package cl.duoc.veterinaria.security;

import cl.duoc.veterinaria.usuario.Aplicacion;

public record UsuarioPrincipal(Long id, String email, String rol, Aplicacion aplicacion) {
}