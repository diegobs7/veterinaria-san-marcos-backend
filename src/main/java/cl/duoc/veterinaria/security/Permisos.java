package cl.duoc.veterinaria.security;

// Expresiones de @PreAuthorize compartidas por los controllers
public final class Permisos {

    private Permisos() {
    }

    // Solo el administrador de la VETERINARIA: el backend es compartido con Kaiju,
    // y un administrador de Kaiju no debe modificar el catálogo de la veterinaria
    public static final String ADMIN_VETERINARIA =
            "hasRole('ADMINISTRADOR') and principal.aplicacion().name() == 'VETERINARIA'";
}