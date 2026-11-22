package cl.duoc.veterinaria.usuario;

import java.util.Set;

public enum Aplicacion {

    VETERINARIA("DUENO", Set.of("ADMINISTRADOR", "RECEPCIONISTA", "DUENO")),
    KAIJU("VENDEDOR", Set.of("ADMINISTRADOR", "VENDEDOR", "ENCARGADO_INVENTARIO"));

    private final String rolInicial;
    private final Set<String> roles;

    Aplicacion(String rolInicial, Set<String> roles) {
        this.rolInicial = rolInicial;
        this.roles = roles;
    }

    public String rolInicial() {
        return rolInicial;
    }

    public boolean permiteRol(String rol) {
        return roles.contains(rol);
    }
}
